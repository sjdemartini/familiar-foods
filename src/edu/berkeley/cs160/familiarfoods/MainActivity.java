package edu.berkeley.cs160.familiarfoods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    public static final String TAG = "FF_Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        beginListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void beginListeners() {
    	Button adventureModeButton = (Button) findViewById(R.id.button1);
		
		adventureModeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAdventureModeActivity(arg0);
			}
    	});
    }
    
    public void startAdventureModeActivity(View v){
        Intent openAdventureIntent = new Intent(this, AdventureMode.class);
        startActivity(openAdventureIntent);
    }

}
