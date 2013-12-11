package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class HomeActivity extends Activity implements OnItemClickListener {

    public static final String TAG = "FF_Home";
    ImageButton adventureModeButton;
    ImageButton addFoodButton;
    ImageButton linkFoodButton;
    Spinner findFoodSpinner;

    FamiliarFoodsDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prevent the action bar from showing on the Home screen
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_home);

        adventureModeButton = (ImageButton) findViewById(R.id.AdventureModeButton);
        addFoodButton = (ImageButton) findViewById(R.id.AddNewFoodButton);
        linkFoodButton = (ImageButton) findViewById(R.id.LinkFoodsButton);


        db = ((FamiliarFoodsDatabase) getApplication());

        AutoCompleteTextView searchBar = (AutoCompleteTextView) findViewById(R.id.SearchBar);
        ArrayList<String> foods = (ArrayList<String>) db.getAllFoods();
        searchBar.setOnItemClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foods);
        searchBar.setAdapter(adapter);

        startListeners();
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		String query = ((TextView) view).getText().toString();

		Intent openFindFoodIntent = new Intent(this, FindFood.class);
		openFindFoodIntent.putExtra("query", query);
		//Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
		startActivity(openFindFoodIntent);
	}

}
