package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;
import java.util.HashMap;
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

	static final String KEY_SONG = "song"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_VOTES = "votes";
    static final String KEY_THUMB_URL = "thumb_url";
    
    /** The database for this app. */
    FamiliarFoodsDatabase db;

    Spinner linkFoodSpinner1;
    ListView foodList;
    LazyAdapter adapter;
    
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
		ArrayList<HashMap<String, Object>> foodsList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < foods.size() && i < votes.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			
			String food = foods.get(i);
			//String description = StringUtils.join(db.getFoodDescription(food), ',');
			map.put(KEY_NAME, food);
			map.put(KEY_VOTES, votes.get(i).toString());
			map.put(KEY_DESCRIPTION, db.getFoodDescription(food));
			map.put(KEY_THUMB_URL, db.getFoodPhoto(food));
			
			//TODO
			String cuisine = db.getCuisineForFood(food);
			
			foodsList.add(map);
		}
		
		// Set up adapter
		//List results = []; //some list of all the info
//		final StableArrayAdapter adapter = new StableArrayAdapter(this,
//				android.R.layout.simple_list_item_1, foods);
		adapter = new LazyAdapter(this, foodsList);
		foodList.setAdapter(adapter);      
 
        // Click event for single list row
        foodList.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView parent, View view,
                    int position, long id) {
 
            }
        });
	}
	
//	private class StableArrayAdapter extends ArrayAdapter<String> {
//
//        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
//
//        public StableArrayAdapter(Context context, int textViewResourceId,
//                List<String> objects) {
//            super(context, textViewResourceId, objects);
//            for (int i = 0; i < objects.size(); ++i) {
//                mIdMap.put(objects.get(i), i);
//            }
//        }
//
//        @Override
//        public long getItemId(int position) {
//            String item = getItem(position);
//            return mIdMap.get(item);
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return true;
//        }
//
//    }
}