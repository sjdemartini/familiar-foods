package edu.berkeley.cs160.familiarfoods;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
    HashMap<String, String> hardHash;
    int foodIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventure_mode);
        // Show the Up button in the action bar.
        setupActionBar();
        startListeners();

        // Get the database:
        //db = ((FamiliarFoodsDatabase) getApplication());

        //List<String> cuisines = db.getAllCuisines();

        // TODO: Add filtering of cuisines (via user-chosen filter), instead of
        // always using all of them:
        List<String> cuisines = new LinkedList<String>();
        setFoods(cuisines);
        
        displayFoodSuggestion();
    }
    
    protected void startListeners() {
    	Button nextButton = (Button) findViewById(R.id.nextFoodButton);
		
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				foodIndex+=1;
				displayFoodSuggestion();
			}
    	});
		
		Button prevButton = (Button) findViewById(R.id.previousFoodButton);
		
		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				foodIndex-=1;
				displayFoodSuggestion();
			}
    	});
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
    	//real implementation
    	
    	/* foods = db.getFoodsForCuisines(cuisines);

        // Randomize the order of the foods shown:
        Collections.shuffle(foods); **/
    	

        //Temporary hard-coding for the project
        List<String> hardCodedFood = new LinkedList<String>();
        hardCodedFood.add("Green curry (eggplant)");
        hardCodedFood.add("Chicken Vindaloo");
        hardCodedFood.add("Enchiladas");
        foods = hardCodedFood;
        
        hardHash = new HashMap<String, String>();
    	hardHash.put("Green curry (eggplant)", "Thai");
    	hardHash.put("Chicken Vindaloo", "Indian");
    	hardHash.put("Enchiladas", "Mexican");
    }
    
    protected void displayFoodSuggestion() {
    	if (foodIndex < 0) {
			foodIndex = foods.size()-1;
		}
    	if (foodIndex >= foods.size()) {
    		foodIndex = 0;
    	}
    	String displayedFood = ((LinkedList<String>) foods).get(foodIndex);
    	TextView foodName = (TextView)findViewById(R.id.currentFood);
    	foodName.setText(displayedFood);
    	TextView cuisineName = (TextView)findViewById(R.id.currentCuisine);
    	cuisineName.setText(hardHash.get(displayedFood));
    }
}
