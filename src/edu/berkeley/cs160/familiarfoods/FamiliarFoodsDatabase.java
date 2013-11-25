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
     * Return a list of all cuisines (in alphabetical order) used in this app.
     * Useful for creating a list of cuisines to filter by (in search and in
     * Adventure Mode).
     *
     * @return a sorted list of all cuisines.
     */
    public List<String> getAllCuisines() {
        return Arrays.asList(cuisines);
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
