package edu.berkeley.cs160.familiarfoods;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
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
    HashMap<String, String> foodToCuisine;

    /** Local copy of food to description hashmap. */
    HashMap<String, ArrayList<String>> foodToDesc;

    /** Local copy of food to photo hashmap. */
    HashMap<String, String> foodToPhoto;

    /** Local copy of cuisine to food hashmap. */
    HashMap<String, ArrayList<String>> cuisineToFood;

    @Override
    public void onCreate() {
        setupDatabaseFiles();
    }

    @SuppressWarnings("unchecked")
    protected void setupDatabaseFiles() {
     // Open a file input stream for our file
        FileInputStream fisFoodToCuisine = null;
        FileInputStream fisFoodToDesc = null;
        FileInputStream fisFoodToPhoto = null;
        FileInputStream fisCuisineToFood = null;
        try {
            fisFoodToCuisine = openFileInput(FOOD_TO_CUISINE_FILE);
            fisFoodToDesc = openFileInput(FOOD_TO_DESCRIPTION_FILE);
            fisFoodToPhoto = openFileInput(FOOD_TO_PHOTO_FILE);;
            fisCuisineToFood = openFileInput(CUISINE_TO_FOOD_FILE);
        } catch (FileNotFoundException e) {
            // If the files don't exist yet, initialize the hash maps to be
            // empty, and then write them to the files.
            foodToCuisine = new HashMap<String, String>();
            foodToDesc = new HashMap<String, ArrayList<String>>();
            foodToPhoto = new HashMap<String, String>();
            cuisineToFood = new HashMap<String, ArrayList<String>>();
            saveFoodToCuisine();
            saveFoodToDesc();
            saveFoodToPhoto();
            saveCusineToFood();
            return;
        }

        // Read our hash maps from the corresponding files
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(fisFoodToCuisine);
            foodToCuisine = (HashMap<String, String>) ois.readObject();
            ois.close();
            ois = new ObjectInputStream(fisFoodToDesc);
            foodToDesc = (HashMap<String, ArrayList<String>>) ois.readObject();
            ois.close();
            ois = new ObjectInputStream(fisFoodToPhoto);
            foodToPhoto = (HashMap<String, String>) ois.readObject();
            ois.close();
            ois = new ObjectInputStream(fisCuisineToFood);
            cuisineToFood = (HashMap<String, ArrayList<String>>) ois.readObject();
            ois.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        try {
            fisFoodToCuisine.close();
            fisFoodToDesc.close();
            fisFoodToPhoto.close();
            fisCuisineToFood.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Save to file the foodToCuisine map.
     *
     * This ensures the data persists between sessions.
     */
    protected void saveFoodToCuisine() {

    }

    /**
     * Save to file the foodToDesc map.
     *
     * This ensures the data persists between sessions.
     */
    protected void saveFoodToDesc() {

    }

    /**
     * Save to file the foodToPhoto map.
     *
     * This ensures the data persists between sessions.
     */
    protected void saveFoodToPhoto() {

    }

    /**
     * Save to file the cuisineToFood map.
     *
     * This ensures the data persists between sessions.
     */
    protected void saveCusineToFood() {

    }

    /**
     * Return a cuisine type for a given food.
     * @param foodName the food for which a cuisine type is being looked up.
     * @return a cuisine type for the given food, or null if food not found.
     */
    public String getFoodCuisine(String foodName) {
        return foodToCuisine.get(foodName);
    }

    /**
     * Return a list of descriptors for a given food.
     * @param foodName the food for which a description is being looked up.
     * @return a list of descriptors for the food.
     */
    public ArrayList<String> getFoodDescription(String foodName) {
        return foodToDesc.get(foodName);
    }
}
