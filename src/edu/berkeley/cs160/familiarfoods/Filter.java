package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Filter extends Activity {
	FamiliarFoodsDatabase db;
	Button selectAll, selectNone;
	ListView filterList;
	List<String> cuisines;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter_foods);


		filterList = (ListView) findViewById(R.id.filterList);
		selectAll = (Button) findViewById(R.id.AllButton);
		selectNone = (Button) findViewById(R.id.NoneButton);

		db = ((FamiliarFoodsDatabase) getApplication());

		cuisines = db.getAllCuisines();

		List<String> currSelectedCuisines;

		// Get the current set of checked cuisines from Adventure Mode:
		Intent i = getIntent();
		currSelectedCuisines = i.getStringArrayListExtra("cuisines");

		startListeners();
		setUpAdapter();
		setSelectedCuisines(currSelectedCuisines);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		Toast.makeText(this, String.valueOf(lv.getCheckedItemCount()),
//				Toast.LENGTH_LONG).show();
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
    		case R.id.done:
    			// Go back to Adventure mode
                List<String> my_sel_items = new ArrayList<String>();
                SparseBooleanArray a = filterList.getCheckedItemPositions();

                for(int i = 0; i < a.size() ; i++) {
                    if (a.valueAt(i)) {
                    	int idx = a.keyAt(i);
                        my_sel_items.add((String) filterList.getAdapter().getItem(idx));
                    }
                }
                Intent returnToAdventureIntent = new Intent(this, AdventureMode.class);
                returnToAdventureIntent.putStringArrayListExtra("cuisines", (ArrayList<String>) my_sel_items);
                startActivity(returnToAdventureIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.done, menu);
		return true;
	}

	@SuppressWarnings("unchecked")
    public List<String> getSelectedCuisines() {
		return (List<String>) filterList.getCheckedItemPositions();
	}

    protected void setSelectedCuisines(List<String> selectedCuisines) {
        // Create a Set of the selected cuisines for constant-time lookup
        Set<String> selectedCuisinesSet = new HashSet<String>(selectedCuisines);
        int numCuisines = cuisines.size();
        for (int i = 0; i < numCuisines; i++) {
            String currCuisine = cuisines.get(i);
            if (selectedCuisinesSet.contains(currCuisine)) {
                filterList.setItemChecked(i, true);
            }
        }
    }

	protected void startListeners() {
		selectAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// boolean isChecked = selectAll.isChecked();
				ListView l1 = getListView();
				int size = l1.getCount();
				for (int i = 0; i < size; i++)
					l1.setItemChecked(i, true);
			}
		});

		selectNone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// boolean isChecked = selectAll.isChecked();
				ListView l1 = getListView();
				int size = l1.getCount();
				for (int i = 0; i < size; i++)
					l1.setItemChecked(i, false);
			}
		});
		// ListView lv = getListView();
		// lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, final View view,
		// int position, long id) {
		// final String item = (String) parent.getItemAtPosition(position);
		// view.animate().setDuration(2000).alpha(0)
		// .withEndAction(new Runnable() {
		// @Override
		// public void run() {
		// list.remove(item);
		// adapter.notifyDataSetChanged();
		// view.setAlpha(1);
		// }
		// });
		// Toast.makeText(getApplicationContext(), "Click" + position,
		// Toast.LENGTH_LONG)
		// .show();
		// }
		//
		// });
	}

	protected void setUpAdapter() {
		ListView lv = getListView();
		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < cuisines.size(); ++i) {
			list.add(cuisines.get(i));
		}

		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_multiple_choice, list);

		lv.setAdapter(adapter);
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}

	protected ListView getListView() {
		return (ListView) findViewById(R.id.filterList);
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
