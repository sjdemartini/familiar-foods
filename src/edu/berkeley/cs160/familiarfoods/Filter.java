package edu.berkeley.cs160.familiarfoods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
	List<String> cuisines;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter_foods);
		
		selectAll = (Button) findViewById(R.id.AllButton);
		selectNone = (Button) findViewById(R.id.NoneButton);

		db = ((FamiliarFoodsDatabase) getApplication());

		cuisines = db.getAllCuisines();

		startListeners();
		
		setUpAdapter();
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
		getMenuInflater().inflate(R.menu.done, menu);	
		return true;
	}
	
	public List<String> getSelectedCuisines() {
		ListView lv = (ListView) findViewById(R.id.filterList);
		return (List<String>) lv.getCheckedItemPositions();
	}

	protected void startListeners() {
		selectAll.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            //boolean isChecked = selectAll.isChecked();
	        	ListView l1 = getListView();
                int size = l1.getCount();
                for (int i = 0; i < size; i++)
                    l1.setItemChecked(i, true);
	        }
	    });
		
		selectNone.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            //boolean isChecked = selectAll.isChecked();
	        	ListView l1 = getListView();
                int size = l1.getCount();
                for (int i = 0; i < size; i++)
                    l1.setItemChecked(i, false);
	        }
	    });
//		ListView lv = getListView();
//		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, final View view,
//					int position, long id) {
//				final String item = (String) parent.getItemAtPosition(position);
//				view.animate().setDuration(2000).alpha(0)
//						.withEndAction(new Runnable() {
//							@Override
//							public void run() {
//								list.remove(item);
//								adapter.notifyDataSetChanged();
//								view.setAlpha(1);
//							}
//						});
//				Toast.makeText(getApplicationContext(), "Click" + position, Toast.LENGTH_LONG)
//					.show();
//			}
//
//		});
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
