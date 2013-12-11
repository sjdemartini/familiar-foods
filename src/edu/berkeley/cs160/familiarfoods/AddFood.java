package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddFood extends Activity {
    /** The database for this app. */
    FamiliarFoodsDatabase db;

    Button doneButton;
    EditText foodNameEditText;
    Spinner cuisineSpinner;
    ImageButton cameraButton;
    ImageView foodPhotoView;
    Bitmap foodPhoto;

    ScrollView addFoodScrollView;
    LinearLayout descriptorLinearLayout;

    /** Code used to indicate that an image capture. */
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_food);

        // Show the Up button in the action bar.
        setupActionBar();

        // Prevent from the keyboard from coming up:
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get the database:
        db = ((FamiliarFoodsDatabase) getApplication());

        // Get the descriptors and add them as choices
        List<String> descriptors = db.getDescriptorChoices();
        addFoodScrollView = (ScrollView) findViewById(R.id.addFoodScrollView);
        insertDescriptorInScrollView(descriptors);

        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        foodPhotoView = (ImageView) findViewById(R.id.foodPhotoImage);
        foodNameEditText = (EditText) findViewById(R.id.foodNameEditText);
        cuisineSpinner = (Spinner) findViewById(R.id.cuisineSpinner);
        descriptorLinearLayout = (LinearLayout) findViewById(R.id.descriptorll);
        doneButton = (Button) findViewById(R.id.doneButton);

        ArrayList<String> cuisines = (ArrayList<String>) db.getAllCuisines();
        ArrayAdapter adapter = new ArrayAdapter(
                this, android.R.layout.simple_spinner_item, cuisines);
        cuisineSpinner.setAdapter(adapter);

        startListeners();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void startListeners() {
    	doneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addFood();
            }
        });
    	cameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDescriptorInScrollView(List<String> descriptors) {
    	LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toggleView = inflator.inflate(R.layout.add_food, null);

    	setContentView(toggleView);

    	LinearLayout descriptorLL = (LinearLayout) toggleView.findViewById(R.id.descriptorll);
        int numDescriptors = descriptors.size();
    	for(int i=0; i<numDescriptors; i++) {
        	CheckBox newCheck = new CheckBox(this);
        	newCheck.setText(descriptors.get(i));

        	descriptorLL.setBaselineAligned(false);
        	descriptorLL.addView(newCheck, i);
        }
    }

    /**
     * Show the activity for taking pictures.
     */
    public void takePicture() {
        // create Intent to take a picture and return control to the calling application
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // start the image capture Intent
        startActivityForResult(takePicIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                foodPhoto = (Bitmap) extras.get("data");
                foodPhotoView.setImageBitmap(foodPhoto);
            } else if (resultCode != RESULT_CANCELED) {
                // Image capture failed, so advise user of this
                Toast.makeText(
                        this,
                        "Picture taking failed. Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addFood() {
    	// Check whether the food name is valid:
        String foodName = foodNameEditText.getText().toString().trim();
    	if (foodName.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please enter the food name.",
                    Toast.LENGTH_SHORT).show();
            return;
    	}
    	if (db.doesFoodExist(foodName)) {
            Toast.makeText(
                    this,
                    "That food already exists!",
                    Toast.LENGTH_SHORT).show();
            return;
    	}

    	// Check whether the cuisine is valid:
    	String cuisine = cuisineSpinner.getSelectedItem().toString().trim();
    	if (cuisine.isEmpty()) {
            // Don't submit unless cuisine chosen
            Toast.makeText(
                    this,
                    "Please choose a cuisine for this food.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (db.doesFoodExist(foodName)) {
            // Don't allow an existing food to be added
            Toast.makeText(
                    this,
                    "That food already exists!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Check whether there was a photo taken:
        if (foodPhoto == null) {
            Toast.makeText(
                    this,
                    "Please add a photo of this food.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the food descriptors
    	ArrayList<String> desc = new ArrayList<String>();
    	for(int i=0; i<descriptorLinearLayout.getChildCount(); i++) {
    	    View v = descriptorLinearLayout.getChildAt(i);
    	    if (v instanceof CheckBox) {
    	        if (((CheckBox) v).isChecked()) {
    	            desc.add(((CheckBox)v).getText().toString());
    	        }
    	    }
    	}
    	if (desc.size() == 0) {
    	    // Don't submit unless descriptors chosen
    	    Toast.makeText(
                    this,
                    "Please select at least one food descriptor.",
                    Toast.LENGTH_SHORT).show();
    	    return;
    	}

    	Object[] descriptionArray = desc.toArray();
    	String[] descriptionStringArray = new String[descriptionArray.length];
    	for(int i=0; i<descriptionArray.length; i++) {
    		descriptionStringArray[i] = descriptionArray[i].toString();
    	}

		db.addFoodToDatabase(
		        foodName, cuisineSpinner.getSelectedItem().toString(),
		        descriptionStringArray, foodPhoto);

    	NavUtils.navigateUpFromSameTask(this);
    	Toast.makeText(
                this,
                String.format("You've successfully added %s.", foodName),
                Toast.LENGTH_SHORT).show();
    }
}