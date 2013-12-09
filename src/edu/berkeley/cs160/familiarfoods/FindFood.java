package edu.berkeley.cs160.familiarfoods;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

public class FindFood extends ListActivity {
	
	SearchView searchView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.find_similar_food);

	    searchView = (SearchView) findViewById(R.id.similarFoodSearchView);
	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      findFamiliarFood(query);
	      searchView.setQuery(query, false);
	    }
	}

	private void findFamiliarFood(String query) {
		// TODO searching goes here.
		
	}
	
	
}
