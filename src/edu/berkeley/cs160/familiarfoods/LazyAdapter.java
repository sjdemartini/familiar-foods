package edu.berkeley.cs160.familiarfoods;

/**
 * Credit: http://www.androidhive.info/2012/02/android-custom-listview-with-image-and-text/
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class LazyAdapter extends BaseAdapter {
 
    private Activity activity;
    private ArrayList<HashMap<String, Object>> data;
    private static LayoutInflater inflater=null;
 
    public LazyAdapter(Activity a, ArrayList<HashMap<String, Object>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.familiar_food_list_row, null);
 
        TextView name = (TextView)vi.findViewById(R.id.name); // name
        TextView description = (TextView)vi.findViewById(R.id.description); // description
        TextView duration = (TextView)vi.findViewById(R.id.votes); // votes
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
 
        HashMap<String, Object> food = new HashMap<String, Object>();
        food = data.get(position);
 
        // Format the description list
        List<String> descriptionList = (List<String>) food.get(FindFood.KEY_DESCRIPTION);
        StringBuilder desc = new StringBuilder();
        for (int i = 0; i < descriptionList.size()-1; i++) {   
        	description.append(descriptionList.get(i));
        	description.append(", ");
        }
        if (descriptionList.size() > 0) {
        	description.append(descriptionList.get(descriptionList.size()-1));
        }
        
        // Setting all values in listview
        name.setText((String) food.get(FindFood.KEY_NAME));
        description.setText(desc);
        duration.setText((String) food.get(FindFood.KEY_VOTES));
        thumb_image.setImageBitmap((Bitmap) food.get(FindFood.KEY_THUMB_URL));
        return vi;
    }
}

