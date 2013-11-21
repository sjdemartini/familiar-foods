package edu.berkeley.cs160.familiarfoods;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class AdventureMode extends Activity {

    /** The database for this app. */
    FamiliarFoodsDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventure_mode);
        // Show the Up button in the action bar.
        setupActionBar();
        db = ((FamiliarFoodsDatabase) getApplication());
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
     * Return a random food from any cuisine type.
     * @return a random food food of any cuisine type.
     */
    public String getRandomFood() {
        return "";
    }

    /**
     * Return a random food from a given cuisine type.
     * @param cuisine The type of cuisine from which a random food is wanted.
     * @return a random food from the given cuisine.
     */
    public String getRandomFood(String cuisine) {
        return "";
    }
}
