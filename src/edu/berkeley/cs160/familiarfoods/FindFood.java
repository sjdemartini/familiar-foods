package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class FindFood extends Activity implements OnItemClickListener {

	static final String KEY_SONG = "song"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_VOTES = "votes";
    static final String KEY_THUMB_URL = "thumb_url";
    static final String KEY_CUISINE = "cuisine";

    /** The database for this app. */
    FamiliarFoodsDatabase db;

    List<String> foods;
    List<String> cuisines;
    List<HashMap<String, Object>> currentUnfilteredResults = new ArrayList<HashMap<String, Object>>();

    Spinner linkFoodSpinner1;
    Button filterButton;
    AlertDialog.Builder alt_bld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_similar_food);

        // Show the Up button in the action bar.
        setupActionBar();

        // Prevent from the keyboard from coming up:
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        db = ((FamiliarFoodsDatabase) getApplication());
        filterButton = (Button) findViewById(R.id.filterButtonSimilarFood);

		alt_bld = new AlertDialog.Builder(this);

        AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.similarFoodSearch);
        ArrayList<String> foods = (ArrayList<String>) db.getAllFoods();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foods);
        search.setAdapter(adapter);
        search.setOnItemClickListener(this);

        Intent main_activity = getIntent();
        String query = main_activity.getExtras().get("query").toString();

        if (!query.equals("")) {
        	search.setText(query, TextView.BufferType.EDITABLE);
        }

        setCuisines(main_activity);
        searchForFamiliarFoods();

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

    protected void startListeners() {
    	filterButton.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View arg0) {
    			startFilterActivity();
    		}
    	});
    }

    /**
     * Open Filter Activity with the current set of cuisines selected
     */
    protected void startFilterActivity() {
		Intent openFilterIntent = new Intent(this, Filter.class);
		openFilterIntent.putStringArrayListExtra(
		        "cuisines", (ArrayList<String>) cuisines);
        startActivityForResult(openFilterIntent, 1);
    }

    /**
     * Sets class variale cuisines to be the updated list
     * @param i
     */
    protected void setCuisines(Intent i) {
        // Filter by cuisines
        if (i.hasExtra("cuisines")) {
            cuisines = i.getStringArrayListExtra("cuisines");
        } else {
        	cuisines = db.getAllCuisines();
        }
    }

    /**
     * Receive new set of cuisines from the Filter activity
     */
    protected void onActivityResult(int requestcode, int resultcode, Intent i) {
    	if (requestcode == 1) {
    		if (resultcode == RESULT_OK) {
    			setCuisines(i);
    			updateSearchResults();
    		}
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_help, menu);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		searchForFamiliarFoods();
	}

	/**
	 * Populates listview with the search results.
	 *
	 * @param query The string with the food name that is being searched
	 */
	private void searchForFamiliarFoods() {
        String query = ((TextView) findViewById(R.id.similarFoodSearch)).getText().toString();

		List<String> foods = db.getLinkedFoods(query);
		List<Integer> votes = db.getLinkVotes(query);
		currentUnfilteredResults.clear();
		for (int i = 0; i < foods.size() && i < votes.size(); i++) {
			String food = foods.get(i);

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(KEY_NAME, food);
			map.put(KEY_VOTES, votes.get(i).toString());
			map.put(KEY_DESCRIPTION, db.getFoodDescription(food));
			map.put(KEY_THUMB_URL, db.getFoodPhoto(food));
			map.put(KEY_CUISINE, db.getCuisineForFood(food));

			currentUnfilteredResults.add(map);
		}
		updateSearchResults();
	}

    private void insertFoodLinkInScrollView(List<HashMap<String, Object>> foods) {
    	LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toggleView = inflator.inflate(R.layout.find_similar_food, null);

    	setContentView(toggleView);

    	LinearLayout descriptorLL = (LinearLayout) toggleView.findViewById(R.id.searchll);
        for(int i=0; i<foods.size();i++) {
        	FamiliarFoodListRowView connection = new FamiliarFoodListRowView(this);
        	connection.setText((String) foods.get(i).get(KEY_NAME));
        	connection.setVotes((String) foods.get(i).get(KEY_VOTES));
        	connection.setDescription((List<String>) foods.get(i).get(KEY_DESCRIPTION));
        	connection.setPicture((Bitmap) foods.get(i).get(KEY_THUMB_URL));

        	descriptorLL.setBaselineAligned(false);
        	descriptorLL.addView(connection, i);
        }
    }

	private void updateSearchResults() {
		ArrayList<HashMap<String, Object>> filteredResults = new ArrayList<HashMap<String, Object>>();
		for (HashMap<String, Object> foodMap : currentUnfilteredResults) {
			if (cuisines.contains(foodMap.get(KEY_CUISINE))) {
				filteredResults.add(foodMap);
			}
		}
		insertFoodLinkInScrollView(currentUnfilteredResults);
	}

}