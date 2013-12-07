package edu.berkeley.cs160.familiarfoods;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class FamiliarFoodsDatabase extends Application {

    public static final String TAG = "FF_Database";

    /**
     * The file name for storing a mapping from food name to cuisine type.
     */
    public static final String FOOD_TO_CUISINE_FILE = "foodToCuisine";

    /**
     * The file name for storing a mapping from food name to a description list.
     */
    public static final String FOOD_TO_DESCRIPTION_FILE = "foodToDesc";

    /**
     * The file name for storing a mapping from food name to a file path for
     * a photo of that food.
     */
    public static final String FOOD_TO_PHOTO_FILE = "foodToPhoto";

    /**
     * The file name for storing a mapping from food name to a file path for
     * a photo of that food.
     */
    public static final String FOOD_TO_LINKS_FILE = "foodToLinks";

    /**
     * The file name for storing a mapping from cuisine type to a list of foods
     * in that cuisine.
     */
    public static final String CUISINE_TO_FOOD_FILE = "cuisineToFood";

    /** Local copy of food to cuisine hashmap. */
    protected HashMap<String, String> foodToCuisine;

    /** Local copy of food to description hashmap. */
    protected HashMap<String, List<String>> foodToDesc;

    /** Local copy of food to photo hashmap. */
    protected HashMap<String, String> foodToPhoto;

    /** Local copy of food to FoodLinks hashmap. */
    protected HashMap<String, TreeSet<FoodLink>> foodToLinks;

    /** Local copy of cuisine to food hashmap. */
    protected HashMap<String, List<String>> cuisineToFood;

    /** Dictates whether initial data should be loaded. */
    protected boolean shouldLoadData = true;

    /** Used to keep track of whether database initialization is complete. */
    protected boolean isInitializingFinished = false;

    /**
     * All cuisines, stored in sorted order (for convenience when filtering).
     */
    protected final String[] cuisines = {
            "American",
            "Chinese",
            "French",
            "Ethiopian",
            "Indian",
            "Italian",
            "Japanese",
            "Korean",
            "Malaysian",
            "Mediterranean",
            "Mexican",
            "Spanish",
            "Thai",
            "Vietnamese"
    };

    /**
     * Maintain a set of all the food links for this app.
     *
     * Useful for checking whether a given foodlink already exists.
     */
    protected Set<FoodLink> allFoodLinks;

    /**
     * A class used to represent a link between two foods in the database.
     * The foods are stored in alphabetical order for food1 and food2.
     */
    private class FoodLink implements Comparable<FoodLink> {
        String food1;
        String food2;
        /** Number of net upvotes/downvotes for this food link. */
        int numVotes = 0;

        public FoodLink(String food1, String food2) {
            // Store the foods in alphabetical order
            int compare = food1.compareToIgnoreCase(food2);
            if (compare == 0) {
                throw new IllegalArgumentException(
                        "Can't link the same food to itself.");
            } else if (compare < 0) {
                this.food1 = food1;
                this.food2 = food2;
            } else {
                this.food2 = food1;
                this.food1 = food2;
            }
            numVotes = 1;
        }

        /**
         * Up-vote this food link.
         * @return the total number of votes after the up-voting.
         */
        public int upVote() {
            numVotes++;
            return getNumVotes();
        }

        /**
         * Down-vote this food link.
         * @return the total number of votes after the down-voting.
         */
        public int downVote() {
            numVotes--;
            return getNumVotes();
        }

        public int getNumVotes() {
            return numVotes;
        }

        /**
         * Given a food name for one of the foods in this link, return the
         * other food that is part of the link.
         *
         * @param foodName
         * @return
         */
        public String getOtherFood(String foodName) {
            if (food1.equals(foodName)) {
                return food2;
            } else if (food2.equals(foodName)) {
                return food1;
            }
            throw new IllegalArgumentException(
                    "This food is not part of this link.");
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((food1 == null) ? 0 : food1.hashCode());
            result = prime * result + ((food2 == null) ? 0 : food2.hashCode());
            return result;
        }

        /*
         * Two FoodLinks are "equals" if the respective food1 and food2 Strings
         * are equal.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FoodLink other = (FoodLink) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (food1 == null) {
                if (other.food1 != null)
                    return false;
            } else if (!food1.equals(other.food1))
                return false;
            if (food2 == null) {
                if (other.food2 != null)
                    return false;
            } else if (!food2.equals(other.food2))
                return false;
            return true;
        }

        private FamiliarFoodsDatabase getOuterType() {
            return FamiliarFoodsDatabase.this;
        }

        @Override
        public int compareTo(FoodLink another) {
            // Have the comparison flipped so that highest votes come first
            // in ordering
            return Integer.valueOf(another.getNumVotes()).compareTo(
                    Integer.valueOf(getNumVotes()));
        }
    }

    @Override
    public void onCreate() {
        setupDatabaseFiles();
        if (shouldLoadData) {
            initializeDatabase();
            shouldLoadData = false;
        }
    }

    @SuppressWarnings("unchecked")
    protected void setupDatabaseFiles() {
        foodToCuisine = (HashMap<String, String>) loadObjectFromFile(
                FOOD_TO_CUISINE_FILE);
        foodToDesc = (HashMap<String, List<String>>) loadObjectFromFile(
                FOOD_TO_DESCRIPTION_FILE);
        foodToPhoto = (HashMap<String, String>) loadObjectFromFile(
                FOOD_TO_PHOTO_FILE);;
        cuisineToFood = (HashMap<String, List<String>>) loadObjectFromFile(
                CUISINE_TO_FOOD_FILE);

        // If any value was not found from the file (in which case the file
        // did not yet exist, initialize the fields to be empty and write them
        // to the files:
        if (foodToCuisine == null) {
            foodToCuisine = new HashMap<String, String>();
            saveFoodToCuisine();
        }
        if (foodToDesc == null) {
            foodToDesc = new HashMap<String, List<String>>();
            saveFoodToDesc();
        }
        if (foodToPhoto == null) {
            foodToPhoto = new HashMap<String, String>();
            saveFoodToPhoto();
        }
        if (foodToLinks == null) {
            foodToLinks = new HashMap<String, TreeSet<FoodLink>>();
            saveFoodToLinks();
        }
        if (cuisineToFood == null) {
            cuisineToFood = new HashMap<String, List<String>>();
            for (String cuisine : cuisines) {
                // Initialize the list of foods for each cuisine
                cuisineToFood.put(cuisine, new ArrayList<String>());
            }
            saveCusineToFood();
        }
    }

    /**
     * Initialize the database from a text file of data with food details.
     */
    protected void initializeDatabase() {
        // Create an AsyncTask to initialize the database, since this will
        // require a large amount of time (including fetching images from the
        // web)
        new DatabaseInitializer().execute();
    }

    /**
     * Save all the local data to file.
     */
    protected void saveAllData() {
        saveFoodToCuisine();
        saveFoodToDesc();
        saveFoodToPhoto();
        saveFoodToLinks();
        saveCusineToFood();
    }

    /**
     * Save to file the foodToCuisine map.
     *
     * This ensures the data persists between sessions.
     */
    protected void saveFoodToCuisine() {
        saveObjectToFile(foodToCuisine, FOOD_TO_CUISINE_FILE);
    }

    /**
     * Save to file the foodToDesc map.
     *
     * This ensures the data persists between sessions.
     */
    protected void saveFoodToDesc() {
        saveObjectToFile(foodToDesc, FOOD_TO_DESCRIPTION_FILE);
    }

    /**
     * Save to file the foodToPhoto map.
     *
     * This ensures the data persists between sessions.
     */
    protected void saveFoodToPhoto() {
        saveObjectToFile(foodToPhoto, FOOD_TO_PHOTO_FILE);
    }

    /**
     * Save to file the foodToLinks map.
     *
     * This ensures the data persists between sessions.
     */
    protected void saveFoodToLinks() {
        saveObjectToFile(foodToLinks, FOOD_TO_LINKS_FILE);
    }

    /**
     * Save to file the cuisineToFood map.
     *
     * This ensures the data persists between sessions.
     */
    protected void saveCusineToFood() {
        saveObjectToFile(cuisineToFood, CUISINE_TO_FOOD_FILE);
    }

    /**
     * Save an object to the internal storage.
     *
     * @param obj The object to save.
     * @param filename The filename for saving the object.
     */
    protected void saveObjectToFile(Object obj, String filename) {
        // Open a file output stream for this file
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(filename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            // File should be created if it does not exist, so this only occurs
            // if there is an error
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // Write our object to the file
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Load an object from a file from internal storage.
     *
     * @param filename The filename from which the object is loaded.
     */
    protected Object loadObjectFromFile(String filename) {
        Object obj = null;
        FileInputStream fis = null;
        try {
            fis = openFileInput(filename);
        } catch (FileNotFoundException e) {
            // If the file does not exist yet, return the obj as null, since an
            // object cannot be loaded
            return obj;
        }

        // Read our hash maps from the corresponding files
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(fis);
            obj = ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * Boolean to check whether the database has been completely initialized.
     * @return True if the database has finished being initialized.
     */
    public boolean isInitializingFinished() {
        return isInitializingFinished;
    }

    /**
     * Return a list of all foods (in no particular order) used in this app.
     * Useful for autcomplete on food name.
     *
     * @return a list of all foods in the app.
     */
    public List<String> getAllFoods() {
        return new ArrayList<String>(foodToCuisine.keySet());
    }

    /**
     * Return a list of all cuisines (in alphabetical order) used in this app.
     * Useful for creating a list of cuisines to filter by (in search and in
     * Adventure Mode).
     *
     * @return a sorted list of all cuisines.
     */
    public List<String> getAllCuisines() {
        return new ArrayList<String>(Arrays.asList(cuisines));
    }

    /**
     * Return a cuisine type for a given food.
     *
     * @param foodName the food for which a cuisine type is being looked up.
     * @return a cuisine type for the given food, or null if food not found.
     */
    public String getCuisineForFood(String foodName) {
        return foodToCuisine.get(foodName);
    }

    /**
     * Get all the foods in the database for a given cuisine.
     *
     * @param cuisine The cuisine from which foods are being fetched.
     * @return a list of all food names belonging to the given cuisine.
     */
    public List<String> getFoodsForCuisine(String cuisine) {
         List<String> foods = cuisineToFood.get(cuisine);
         if (foods == null) {
             // If the cuisine was not found, create an empty list
             foods = new ArrayList<String>();
         }
        return foods;
    }

    /**
     * Get all the foods in the database for the given cuisines.
     *
     * @param cuisines The cuisines from which foods are being fetched.
     * @return a list of all food names belonging to the given cuisines.
     */
    public List<String> getFoodsForCuisines(List<String> cuisines) {
        List<String> foods = new ArrayList<String>();
        if (cuisines == null) {
            return foods;
        }
        for (String cuisine : cuisines) {
            foods.addAll(getFoodsForCuisine(cuisine));
        }
        return foods;
    }

    /**
     * Return a list of descriptors for a given food.
     *
     * @param foodName the food for which a description is being looked up.
     * @return a list of descriptors for the food, or null if the food isn't
     *  found.
     */
    public List<String> getFoodDescription(String foodName) {
        return foodToDesc.get(foodName);
    }

    /**
     * Return a Bitmap image of a given food.
     *
     * This method is useful if an image of a food is needed to be set for an
     * ImageView.
     *
     * @param foodName The food for which an image is being looked up.
     * @return A Bitmap image of the food, or null if the food isn't found.
     */
    public Bitmap getFoodPhoto(String foodName) {
        Bitmap photo = null;
        String filename = foodToPhoto.get(foodName);
        if (filename == null) {
            return photo;
        }
        FileInputStream fis;
        try {
            fis = openFileInput(filename);
            photo = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return photo;
    }

    /**
     * Get a list of the foods linked to this food, in order from most votes
     * to least.
     *
     * This method should be used in conjunction with getLinkVotes, which
     * returns a list in the same order, where the net votes correspond to the
     * food linked.
     *
     * @param foodName
     * @return list of foods in the same order as the net votes for
     *  getLinkVotes (most votes to least).
     */
    public List<String> getLinkedFoods(String foodName) {
        List<String> foods = new ArrayList<String>();
        TreeSet<FoodLink> links = foodToLinks.get(foodName);
        for (FoodLink link : links) {
            foods.add(link.getOtherFood(foodName));
        }
        return foods;
    }

    /**
     * Get a list of the net votes for foods linked to this food, from most
     * votes to least.
     *
     * This method should be used in conjunction with getLinkedFoods, which
     * returns a list of foods.
     *
     * @param foodName
     * @return list of net votes for this food in the same order as the
     *  foods for getLinkedFoods.
     */
    public List<Integer> getLinkVotes(String foodName) {
        List<Integer> votes = new ArrayList<Integer>();
        TreeSet<FoodLink> links = foodToLinks.get(foodName);
        for (FoodLink link : links) {
            votes.add(link.getNumVotes());
        }
        return votes;
    }

    /**
     * Add a food with its parameters to the database.
     *
     * @param foodName The name of the food to add.
     * @param cuisine The name of the cuisine type.
     * @param description The descriptors for this food.
     * @param photoFile The filename for the photo.
     *
     */
    public void addFoodToDatabase(String foodName, String cuisine,
            String[] descriptors, String photoFile) {
        foodName = foodName.trim();
        cuisine = cuisine.trim();
        for (int i=0; i<descriptors.length; i++) {
            descriptors[i] = descriptors[i].trim().toLowerCase(Locale.US);
        }
        if (foodToCuisine.containsKey(foodName)) {
            throw new IllegalArgumentException(
                    String.format(
                            "The food %s is already in the database.", foodName));
        }
        if (! cuisineToFood.containsKey(cuisine)) {
            throw new IllegalArgumentException(
                    "The " + cuisine + " cuisine does not exist.");
        }
        foodToCuisine.put(foodName, cuisine);
        foodToDesc.put(foodName, Arrays.asList(descriptors));
        foodToPhoto.put(foodName, photoFile);
        cuisineToFood.get(cuisine).add(foodName);
        saveAllData();
        System.out.println("Added " + foodName);
    }

    /**
     * Store in the database a link between two foods.
     *
     * @param foodName1
     * @param foodName2
     */
    public void linkFoods(String foodName1, String foodName2) {
        FoodLink link = new FoodLink(foodName1, foodName2);
        if (allFoodLinks.contains(link)) {
            // If a link between these foods already exists, find it and
            // up-vote the link:
            TreeSet<FoodLink> links = foodToLinks.get(foodName1);
            for (FoodLink foodLink : links) {
                if (foodLink.equals(link)) {
                    foodLink.upVote();
                    return;
                }
            }
            return;
        }
        addLinkForFood(foodName1, link);
        addLinkForFood(foodName2, link);
        allFoodLinks.add(link);
    }

    /**
     * Ensure that the foodToLinks hashmap is updated, so that the list mapped
     * to from the given food name adds the given link.
     *
     * @param foodName
     * @param link
     */
    private void addLinkForFood(String foodName, FoodLink link) {
        TreeSet<FoodLink> list;
        if (foodToLinks.containsKey(foodName)) {
            list = foodToLinks.get(foodName);
        } else {
            list = new TreeSet<FoodLink>();
        }
        list.add(link);
    }

    /**
     * Up-vote a link between two foods.
     */
    public void upVoteLink(String foodName1, String foodName2) {
        FoodLink link = new FoodLink(foodName1, foodName2);
        if (! allFoodLinks.contains(link)) {
            // Can't up-vote a link that does not exist
            return;
        }

        // If a link between these foods exists, find it and upvote it
        TreeSet<FoodLink> links = foodToLinks.get(foodName1);
        for (FoodLink foodLink : links) {
            if (foodLink.equals(link)) {
                foodLink.upVote();
                return;
            }
        }
    }

    /**
     * Up-vote a link between two foods.
     */
    public void downVoteLink(String foodName1, String foodName2) {
        FoodLink link = new FoodLink(foodName1, foodName2);
        if (! allFoodLinks.contains(link)) {
            // Can't down-vote a link that does not exist
            return;
        }

        // If a link between these foods exists, find it and down-vote it
        TreeSet<FoodLink> links = foodToLinks.get(foodName1);
        for (FoodLink foodLink : links) {
            if (foodLink.equals(link)) {
                foodLink.downVote();
                return;
            }
        }
    }



    /**
     * An AsyncTask that loads initial data into the database.
     */
    private class DatabaseInitializer extends AsyncTask<Void, Void, Void> {
        public static final String TAG = "FF_InitializeDB";

        /**
         * The filename for the food data.
         *
         * The file is organized with tab-separated columns:
         * food name, cuisine, description words, photo link, similar foods
         */
        String DATA_FILENAME = "foods.tsv";

        protected Void doInBackground(Void... params) {
            loadDataFromFile();
            isInitializingFinished = true;
            return null;
        }

        /**
         * Load data from the DATA_FILENAME file.
         */
        protected void loadDataFromFile() {
            BufferedReader br = null;
            String line, foodName, cuisine, photoUrl;
            String[] descriptors;
            String separator = "\\t";
            try {
                br = new BufferedReader(
                        new InputStreamReader(getAssets().open(DATA_FILENAME)));
                while ((line = br.readLine()) != null) {
                    String[] foodDetails = line.split(separator);
                    foodName = foodDetails[0].trim();
                    if (foodToCuisine.containsKey(foodName)) {
                        // This food is already in the db
                        continue;
                    }
                    cuisine = foodDetails[1].trim();
                    // Parse the descriptors from their comma-separated list:
                    descriptors = foodDetails[2].split(",");
                    photoUrl = foodDetails[3].trim();
                    Bitmap photo = getPhotoFromUrl(photoUrl);
                    String photoFilename = foodName + ".png";
                    savePhotoToFile(photo, photoFilename);
                    addFoodToDatabase(
                            foodName, cuisine, descriptors, photoFilename);
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            System.out.println("All foods loaded.");
        }

        /**
         * Get a Bitmap for a photo fetched from the web.
         * @param photoUrl The URL of the photo to be fetched.
         * @return Bitmap of the photo.
         */
        protected Bitmap getPhotoFromUrl(String photoUrl) {
            URL url = null;
            try {
                url = new URL(photoUrl);
            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // Make a new web request for the specific photo we want to display:
            Bitmap bitmap = null;
            try {
                InputStream inputStream = url.openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return bitmap;
        }

        /**
         * Save the Bitmap image to internal storage with the given filename.
         * @param filename Filename for photo.
         * @param photo Bitmap image to save.
         */
        protected void savePhotoToFile(Bitmap photo, String filename) {
            FileOutputStream fos;
            try {
                fos = openFileOutput(filename, Context.MODE_PRIVATE);
                photo.compress(CompressFormat.PNG, 50, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }
}
