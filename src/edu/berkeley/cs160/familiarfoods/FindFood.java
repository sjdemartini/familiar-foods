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
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FindFood extends Activity implements OnItemClickListener {

    /** The database for this app. */
    FamiliarFoodsDatabase db;

    Spinner linkFoodSpinner1;
    ListView foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_similar_food);

        // Show the Up button in the action bar.
        setupActionBar();

        db = ((FamiliarFoodsDatabase) getApplication());

        foodList = (ListView) findViewById(R.id.familiarFoodsList);
        
        AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.similarFoodSearch);
        ArrayList<String> foods = (ArrayList<String>) db.getAllFoods();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foods);
        search.setAdapter(adapter);
        search.setOnItemClickListener(this);

        Intent main_activity = getIntent();
        String query = main_activity.getExtras().get("query").toString();
        
        if (!query.equals("")) {
        	search.setHint(query);
        	searchForFamiliarFoods(query);
        }
        
        // Hide soft keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(
        	      Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
        
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		String query = ((TextView) view).getText().toString();
		
		searchForFamiliarFoods(query);
	}

	private void searchForFamiliarFoods(String query) {
		List<String> foods = db.getLinkedFoods(query);
		List<Integer> votes = db.getLinkVotes(query);
		
		for (int i = 0; i < foods.size() && i < votes.size(); i++) {
			String food = foods.get(i);
			int vote = votes.get(i);
			String cuisine = db.getCuisineForFood(food);
			List<String> description = db.getFoodDescription(food);
			Bitmap photo = db.getFoodPhoto(food);
			
		}
	}
}