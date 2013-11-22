package edu.berkeley.cs160.familiarfoods;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Context;
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
    protected HashMap<String, ArrayList<String>> foodToDesc;

    /** Local copy of food to photo hashmap. */
    protected HashMap<String, String> foodToPhoto;

    /** Local copy of cuisine to food hashmap. */
    protected HashMap<String, ArrayList<String>> cuisineToFood;

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
            "Mediterranean",
            "Mexican",
            "Thai",
            "Vietnamese"
    };

    @Override
    public void onCreate() {
        setupDatabaseFiles();
    }

    @SuppressWarnings("unchecked")
    protected void setupDatabaseFiles() {
        foodToCuisine = (HashMap<String, String>) loadObjectFromFile(
                FOOD_TO_CUISINE_FILE);
        foodToDesc = (HashMap<String, ArrayList<String>>) loadObjectFromFile(
                FOOD_TO_DESCRIPTION_FILE);
        foodToPhoto = (HashMap<String, String>) loadObjectFromFile(
                FOOD_TO_PHOTO_FILE);;
        cuisineToFood = (HashMap<String, ArrayList<String>>) loadObjectFromFile(
                CUISINE_TO_FOOD_FILE);

        // If any value was not found from the file (in which case the file
        // did not yet exist, initialize the fields to be empty and write them
        // to the files:
        if (foodToCuisine == null) {
            foodToCuisine = new HashMap<String, String>();
            saveFoodToCuisine();
        }
        if (foodToDesc == null) {
            foodToDesc = new HashMap<String, ArrayList<String>>();
            saveFoodToDesc();
        }
        if (foodToPhoto == null) {
            foodToPhoto = new HashMap<String, String>();
            saveFoodToPhoto();
        }
        if (cuisineToFood == null) {
            cuisineToFood = new HashMap<String, ArrayList<String>>();
            for (String cuisine : cuisines) {
                // Initialize the list of foods for each cuisine
                cuisineToFood.put(cuisine, new ArrayList<String>());
            }
            saveCusineToFood();
        }
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
            fos = openFileOutput(FOOD_TO_CUISINE_FILE, Context.MODE_PRIVATE);
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
     * Return a list of all cuisines (in alphabetical order) used in this app.
     * Useful for creating a list of cuisines to filter by (in search and in
     * Adventure Mode).
     * @return a sorted list of all cuisines.
     */
    public List<String> getAllCuisines() {
        return Arrays.asList(cuisines);
    }

    /**
     * Return a cuisine type for a given food.
     * @param foodName the food for which a cuisine type is being looked up.
     * @return a cuisine type for the given food, or null if food not found.
     */
    public String getCuisineForFood(String foodName) {
        return foodToCuisine.get(foodName);
    }

    /**
     * Get all the foods in the database for a given cuisine.
     * @param cuisine The cuisine from which foods are being searched.
     * @return a list of all food names belonging to the given cuisine, or
     *  null if the cuisine can't be found.
     */
    public List<String> getFoodsForCuisine(String cuisine) {
        return cuisineToFood.get(cuisine);
    }

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
     * @param foodName the food for which a description is being looked up.
     * @return a list of descriptors for the food.
     */
    public ArrayList<String> getFoodDescription(String foodName) {
        return foodToDesc.get(foodName);
    }

    /**
     * Add a food with its parameters to the database.
     * @param foodName The name of the food to add.
     * @param cuisine The name of the cuisine type.
     * @param description The descriptors for this food.
     * @param photoFile The filename for the photo.
     * @return whether the addition to the database was successful.
     */
    public boolean addFoodToDatabase(String foodName, String cuisine,
            ArrayList<String> description, String photoFile) {
        if (foodToCuisine.containsKey(foodName)
                || !cuisineToFood.containsKey(cuisine)) {
            // If the food is already in the database or the cuisine does not
            // exist, don't try to add the food
            return false;
        }
        foodToCuisine.put(foodName, cuisine);
        foodToDesc.put(foodName, description);
        foodToPhoto.put(foodName, photoFile);
        ArrayList<String> foodsForCuisine = cuisineToFood.get(cuisine);
        foodsForCuisine.add(foodName);
        saveAllData();
        return true;
    }
}
