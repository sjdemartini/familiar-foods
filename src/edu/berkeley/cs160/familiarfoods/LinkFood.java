package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

public class LinkFood extends Activity {

    /** The database for this app. */
    FamiliarFoodsDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_food);

        // Show the Up button in the action bar.
        setupActionBar();

        // Get the database:
        db = ((FamiliarFoodsDatabase) getApplication());

        // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView textView1 = (AutoCompleteTextView) findViewById(R.id.autoCompleteFood1);
        AutoCompleteTextView textView2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteFood2);
        // Get the string array
        ArrayList<String> foods = (ArrayList<String>) db.getAllFoods();
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foods);
        textView1.setAdapter(adapter);
        textView2.setAdapter(adapter);

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
    	AutoCompleteTextView textView1 = (AutoCompleteTextView) findViewById(R.id.autoCompleteFood1);
        AutoCompleteTextView textView2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteFood2);

    	String foodName1 = textView1.getText().toString();
    	String foodName2 = textView2.getText().toString();
    	if (foodName1.equals(foodName2)) {
			// Don't allow a food to be linked to itself
            Toast.makeText(
                    this,
                    "Can't link a food with itself!",
                    Toast.LENGTH_SHORT).show();
            return;
    	}
    	if (! db.doesFoodExist(foodName1)) {
    	    Toast.makeText(
                    this,
                    "The food you entered for \"Food 1\" does not exist in " +
                    "our app.\n" +
                    "Please ensure that you enter an existing food.",
                    Toast.LENGTH_LONG).show();
            return;
    	}
    	if (! db.doesFoodExist(foodName2)) {
    	    Toast.makeText(
    	            this,
    	            "The food you entered for \"Food 2\" does not exist in " +
    	                    "our app.\n" +
    	                    "Please ensure that you enter an existing food.",
    	                    Toast.LENGTH_LONG).show();
    	    return;
    	}
    	db.linkFoods(foodName1, foodName2);

    	NavUtils.navigateUpFromSameTask(this);
        Toast.makeText(
                this,
                String.format(
                        "You've successfully confirmed a link between %s and %s.",
                        foodName1, foodName2),
                Toast.LENGTH_LONG).show();
    }
}