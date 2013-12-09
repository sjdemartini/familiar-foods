package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class LinkFood extends Activity {

    /** The database for this app. */
    FamiliarFoodsDatabase db;
    
    Spinner linkFoodSpinner1;
    Spinner linkFoodSpinner2;
    Spinner linkCuisineSpinner1;
    Spinner linkCuisineSpinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_food);

        // Show the Up button in the action bar.
        setupActionBar();
        
        // Get the database:
        db = ((FamiliarFoodsDatabase) getApplication());

        ArrayList<String> cuisines = (ArrayList<String>) db.getAllCuisines();
        ArrayAdapter adapter = new ArrayAdapter(this,
        android.R.layout.simple_spinner_item, cuisines);
        
        linkCuisineSpinner1 = (Spinner) findViewById(R.id.linkSimilarCuisineSpinner1);
        linkCuisineSpinner2 = (Spinner) findViewById(R.id.linkSimilarCuisineSpinner2);
        
        linkCuisineSpinner1.setAdapter(adapter);
        linkCuisineSpinner2.setAdapter(adapter);
        
        ArrayList<String> foods = (ArrayList<String>) db.getAllFoods();
        ArrayAdapter foodAdapter = new ArrayAdapter(this,
        android.R.layout.simple_spinner_item, foods);
        
        linkFoodSpinner1 = (Spinner) findViewById(R.id.linkSimilarFoodSpinner1);
        linkFoodSpinner2 = (Spinner) findViewById(R.id.linkSimilarFoodSpinner2);
        
        linkFoodSpinner1.setAdapter(foodAdapter);
        linkFoodSpinner2.setAdapter(foodAdapter);
        
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
    	ImageButton submitButton = (ImageButton) findViewById(R.id.submitButton);
    	submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addLink();
            }
        });
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
    
    public void addLink() {
    	String foodName1 = linkFoodSpinner1.getSelectedItem().toString();
    	String foodName2 = linkFoodSpinner2.getSelectedItem().toString();
    	db.linkFoods(foodName1, foodName2);
    }
}