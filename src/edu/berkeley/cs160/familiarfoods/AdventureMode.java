package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AdventureMode extends Activity {

    /** The database for this app. */
    FamiliarFoodsDatabase db;

    /**
     * A list of the foods that can be shown for adventure mode based on any
     * filters on cuisine. The foods list is in randomized order, as that is
     * critical to Adventure Mode showing a "random" result.
     */
    List<String> foods;
    private int currFoodIndex = 0;
    private int numFoods;
    String displayedFood;

    /** TextView for displaying the current food's name. */
    TextView foodName;
    /** TextView for displaying the cuisine of the current food. */
    TextView cuisineName;
    /** ImageView for displaying a picture of the current food. */
    ImageView foodImage;

    /** Button to go to the next food. */
    Button nextButton;
    /** Button to go to the previous food. */
    Button prevButton;
    /** Button to go to the filter foods. */
    Button filterButton;

    private static final byte NEXT = 1;
    private static final byte PREV = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventure_mode);

        // Show the Up button in the action bar.
        setupActionBar();

        // Get the database:
        db = ((FamiliarFoodsDatabase) getApplication());

        // Get views on the screen:
        foodName = (TextView) findViewById(R.id.currentFood);
        cuisineName = (TextView) findViewById(R.id.currentCuisine);
        foodImage = (ImageView) findViewById(R.id.currentDisplayedFood);
        nextButton = (Button) findViewById(R.id.nextFoodButton);
        prevButton = (Button) findViewById(R.id.previousFoodButton);
        filterButton = (Button) findViewById(R.id.filterResultsButton);

        // Hack to ensure that db initialization is complete
        while (!db.isInitializingFinished()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Filter by foods
        Intent i = getIntent();
        List<String> cuisines;
        if (i.getExtras() != null) {
        	cuisines = i.getStringArrayListExtra("cuisines");
        } else {
        	cuisines = db.getAllCuisines();
        }
        setFoods(cuisines);

        // Start the button click listeners
        startListeners();

        displayFood();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.adventure_mode, menu);
        return true;
    }

    protected void startListeners() {
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                rotateThroughFoods(NEXT);
            }
        });
        prevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                rotateThroughFoods(PREV);
            }
        });
        filterButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		startFilterActivity();
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

    /**
     * Set the foods to be shown based on a list of cuisines.
     *
     * This method is useful when filtering by cuisine type. Results shown will
     * be based on the foods field, which is a randomized ordering of foods from
     * the cuisines provided here.
     *
     * @param cuisines The list of cuisines to be used for filtering.
     */
    protected void setFoods(List<String> cuisines) {
        foods = db.getFoodsForCuisines(cuisines);
        numFoods = foods.size();

        // Randomize the order of the foods shown:
        Collections.shuffle(foods);
    }

    protected void rotateThroughFoods(byte direction) {
        if (direction == NEXT) {
            currFoodIndex++;
        } else {
            currFoodIndex--;
        }
        nextButton.setEnabled(currFoodIndex < (numFoods - 1));
        prevButton.setEnabled(currFoodIndex > 0);
        displayFood();
    }

    protected void displayFood() {
        displayedFood = foods.get(currFoodIndex);
        foodName.setText(displayedFood);
        cuisineName.setText(db.getCuisineForFood(displayedFood));
        foodImage.setImageBitmap(db.getFoodPhoto(displayedFood));
    }
    
    protected void startFilterActivity() {
		Intent openFilterIntent = new Intent(this, Filter.class);
        startActivity(openFilterIntent);
    }
}
