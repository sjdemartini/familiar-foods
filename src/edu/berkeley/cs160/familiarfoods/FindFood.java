package edu.berkeley.cs160.familiarfoods;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

public class FindFood extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.SearchBar);

	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      findFamiliarFood(query);
	    }
	}

	private void findFamiliarFood(String query) {
		// TODO searching goes here.
		
	}
}
