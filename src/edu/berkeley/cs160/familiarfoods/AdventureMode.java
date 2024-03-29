package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AdventureMode extends Activity {

    /** The database for this app. */
    FamiliarFoodsDatabase db;

    /** A list of the cuisines for which foods are currently shown. */
    List<String> cuisines;
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
    ImageButton nextButton;
    /** Button to go to the previous food. */
    ImageButton prevButton;
    /** Button to go to the filter foods. */
    Button filterButton;
    /** Filter help dialog */
    AlertDialog.Builder alt_bld;

    private static final byte NEXT = 1;
    private static final byte PREV = -1;

    /** Code used to indicate the request for cuisine filter selection. */
    private static final int CUISINE_FILTER_REQUEST_CODE = 100;

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
        nextButton = (ImageButton) findViewById(R.id.nextFoodButton);
        prevButton = (ImageButton) findViewById(R.id.previousFoodButton);
        filterButton = (Button) findViewById(R.id.filterResultsButton);

        // Hack to ensure that db initialization is complete
        while (!db.isInitializingFinished()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Filter by cuisines
        Intent i = getIntent();
        if (i.getExtras() != null) {
            cuisines = i.getStringArrayListExtra("cuisines");
        } else {
            cuisines = db.getAllCuisines();
        }
        setFoods(cuisines);

        alt_bld = new AlertDialog.Builder(this);

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
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.help_button:
                // This is the id for the help dialog about choosing cuisines
                alt_bld.setMessage(
                        R.string.help_cuisine_filter_dialog)
                        .setPositiveButton("Got it!",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }
                                });

                alt_bld.show();
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
     * @param cuisines
     *            The list of cuisines to be used for filtering.
     */
    protected void setFoods(List<String> cuisines) {
        foods = db.getFoodsForCuisines(cuisines);
        numFoods = foods.size();
        // Reset the food index to 0, since we have new foods to display
        currFoodIndex = 0;

        // Randomize the order of the foods shown:
        Collections.shuffle(foods);
    }

    /**
     * Go to the next or previous food, depending on the given direction.
     *
     * The function silently ignores the command if there are no more foods in
     * the given direction.
     *
     * @param direction
     *            one of NEXT or PREV direction constants.
     */
    protected void rotateThroughFoods(byte direction) {
        if (direction == NEXT) {
            if (currFoodIndex >= (numFoods - 1)) {
                // Ignore the command if there are no more "next" foods
                return;
            }
            currFoodIndex++;
        } else {
            if (currFoodIndex < 1) {
                // Ignore the command if there are no more "previous" foods
                return;
            }
            currFoodIndex--;
        }
        displayFood();
    }

    protected void displayFood() {
        if (foods.size() > 0) {
            displayedFood = foods.get(currFoodIndex);
            foodName.setText(displayedFood);
            cuisineName.setText(db.getCuisineForFood(displayedFood));
            foodImage.setImageBitmap(db.getFoodPhoto(displayedFood));

            boolean enableNext = currFoodIndex < (numFoods - 1);
            nextButton.setEnabled(enableNext);
            nextButton.setBackgroundResource(enableNext
                    ? R.drawable.next_button : R.drawable.blank_button);

            boolean enablePrev = currFoodIndex > 0;
            prevButton.setEnabled(enablePrev);
            prevButton.setBackgroundResource(enablePrev
                    ? R.drawable.previous_button : R.drawable.blank_button);
        } else {
            Toast.makeText(
                    this,
                    "There are no foods available for the cuisines you " +
                    "selected. Please choose a new set of cuisines.",
                    Toast.LENGTH_LONG).show();
            startFilterActivity();
        }
    }

    protected void startFilterActivity() {
        Intent openFilterIntent = new Intent(this, Filter.class);
        openFilterIntent.putStringArrayListExtra("cuisines",
                (ArrayList<String>) cuisines);
        startActivityForResult(openFilterIntent, CUISINE_FILTER_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (requestCode == CUISINE_FILTER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (i.getExtras() != null) {
                    cuisines = i.getStringArrayListExtra("cuisines");
                } else {
                    cuisines = db.getAllCuisines();
                }
                setFoods(cuisines);
                displayFood();
            }
        }
    }
}
