package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class HomeActivity extends Activity {

    public static final String TAG = "FF_Home";
    ImageButton adventureModeButton;
    ImageButton addFoodButton;
    ImageButton linkFoodButton;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
     // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.SearchBar);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        
        adventureModeButton = (ImageButton) findViewById(R.id.AdventureModeButton);
        addFoodButton = (ImageButton) findViewById(R.id.AddNewFoodButton);
        linkFoodButton = (ImageButton) findViewById(R.id.LinkFoodsButton);

        searchView = (SearchView) findViewById(R.id.SearchBar);
        
        startListeners();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void startListeners() {
    	adventureModeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startAdventureModeActivity(arg0);
            }
        });
    	addFoodButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startAddFoodActivity(arg0);
            }
        });
    	linkFoodButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startLinkFoodActivity(arg0);
            }
        });
    	searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something when user his enter on keyboard
            	return startFindFoodActivity(query);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Do something while user is entering text
                return false;
            }
        });

    }

    private boolean startFindFoodActivity(String query) {
    	Intent openFindFood = new Intent(this, FindFood.class);
    	openFindFood.putExtra("query", query);
    	startActivity(openFindFood);
    	return false;
    }
    
    public void startAdventureModeActivity(View v){
        Intent openAdventureIntent = new Intent(this, AdventureMode.class);
        startActivity(openAdventureIntent);
    }

    public void startAddFoodActivity(View v){
        Intent openAdventureIntent = new Intent(this, AddFood.class);
        startActivity(openAdventureIntent);
    }
    
    public void startLinkFoodActivity(View v){
        Intent openAdventureIntent = new Intent(this, LinkFood.class);
        startActivity(openAdventureIntent);
    }

}
