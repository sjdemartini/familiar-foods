package edu.berkeley.cs160.familiarfoods;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A class used to implement the XML representing a found link
 * between two foods in the database when search is run.
 */
public class FamiliarFoodListRowView extends RelativeLayout {

	private RelativeLayout thumbnail;
	private LinearLayout thumbVertical;
	private TextView votes;
	private ImageView upButton;
	private ImageView downButton;
	private ImageView picture;
	private TextView foodName;
	private TextView cuisine;
	private TextView foodDescription;
	private ImageView nextButton;
	FamiliarFoodsDatabase db;

	public FamiliarFoodListRowView(Context context) {
	    super(context);

	    LayoutInflater inflater = (LayoutInflater) context
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        inflater.inflate(R.layout.view_familiar_food_list_row, this, true);

	    foodName = (TextView) findViewById(R.id.name);
	    cuisine = (TextView) findViewById(R.id.similar_food_cuisine);
	    picture = (ImageView) findViewById(R.id.list_image);
	    votes = (TextView) findViewById(R.id.votes);
	    upButton = (ImageView) findViewById(R.id.vote_up);
	    downButton = (ImageView) findViewById(R.id.vote_down);

	    foodDescription = (TextView) findViewById(R.id.description);
	    db = new FamiliarFoodsDatabase();
	    startListeners();
	}

	private void startListeners() {
		upButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				String foodName1 = ((TextView) arg0.findViewById(R.id.similarFoodSearch)).getText().toString();
//				String foodName2 = foodName.getText().toString();
//				db.upVoteLink(foodName1, foodName2);
				updateVote(1);
			}
		});
		downButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				String foodName1 = ((TextView) arg0.findViewById(R.id.similarFoodSearch)).getText().toString();
//				String foodName2 = foodName.getText().toString();
//				db.downVoteLink(foodName1, foodName2);
				updateVote(-1);
			}
		});

	}

	private void updateVote(int delta) {
		int num = Integer.parseInt(votes.getText().toString());
		votes.setText(Integer.toString(num + delta));
	}

	public void setText(String s) {
		foodName.setText(s);
	}

	public void setCuisine(String s) {
	    cuisine.setText(s);
	}
	public void setDescription(List<String> s) {
		String desc = "";
		for(int i=0; i<s.size(); i++) {
			if (desc.equals("")) {
				desc = s.get(i);
				continue;
			}
			desc = desc + ", " + s.get(i);
		}
		foodDescription.setText(desc);
	}

	public void setPicture(Bitmap b) {
		picture.setImageBitmap(b);
	}

	public void setVotes(String s) {
	    votes.setText(s);
	}

}