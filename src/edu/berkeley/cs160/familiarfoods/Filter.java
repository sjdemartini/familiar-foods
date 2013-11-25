package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Filter extends Activity {
	FamiliarFoodsDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter_foods);

		// Get the database:
		db = ((FamiliarFoodsDatabase) getApplication());

		// Populate ListView
		final ListView listview = (ListView) findViewById(R.id.filterList);
		List<String> cuisines = db.getAllCuisines();

		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < cuisines.size(); ++i) {
			list.add(cuisines.get(i));
		}

		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_multiple_choice, list);

		listview.setAdapter(adapter);
		listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
//				view.animate().setDuration(2000).alpha(0)
//						.withEndAction(new Runnable() {
//							@Override
//							public void run() {
//								list.remove(item);
//								adapter.notifyDataSetChanged();
//								view.setAlpha(1);
//							}
//						});
				Toast.makeText(getApplicationContext(), "Click" + position, Toast.LENGTH_LONG)
					.show();
			}

		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ListView lv = (ListView) findViewById(R.id.filterList);
		Toast.makeText(this, String.valueOf(lv.getCheckedItemCount()),
				Toast.LENGTH_LONG).show();
		return true;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	

	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}
}
