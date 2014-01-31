package com.virtualmanila.guardianportallister.sgmy;

import java.util.ArrayList;
import java.util.List;

import com.virtualmanila.guardianportallister.sgmy.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class GPListAdapter extends ArrayAdapter<GuardianPortal> implements
		Filterable {

	LayoutInflater inflater;
	Context context;
	List<GuardianPortal> values;
	List<GuardianPortal> origValues;

	public GPListAdapter(Context context, List<GuardianPortal> objects) {
		super(context, R.layout.list_item_guardian_portal, objects);
		this.context = context;
		this.values = objects;
		this.origValues = objects;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.list_item_guardian_portal,
					null, false);
			Holder holder = new Holder();
			holder.name = (TextView) rowView.findViewById(R.id.name);
			holder.coords = (TextView) rowView.findViewById(R.id.coords);
			holder.owner = (TextView) rowView.findViewById(R.id.owner);
			holder.age = (TextView) rowView.findViewById(R.id.age);
			holder.color = rowView.findViewById(R.id.color);
			rowView.setTag(holder);
		}
		Holder holder = (Holder) rowView.getTag();
		GuardianPortal gp = getItem(position);
		holder.name.setText(gp.getName());
		holder.coords.setText(gp.getLattitude() + "," + gp.getLongitude());
		holder.owner.setText(gp.getOwner());
		int age = gp.getAge();
		holder.age.setText(context.getString(R.string.label_days, age));
		holder.color.setBackgroundColor(getBadgeColor(age, gp.isLive()));
		return rowView;
	}

	@Override
	public int getCount() {
		return values.size();
	}

	@Override
	public GuardianPortal getItem(int position) {
		return values.get(position);
	}

	public List<GuardianPortal> getAll() {
		return values;
	}

	@Override
	public Filter getFilter() {

		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				values = (List<GuardianPortal>) results.values;
				notifyDataSetChanged();
				android.util.Log.d("ALAIN", "values = " + values.size());
				android.util.Log
						.d("ALAIN", "origValues = " + origValues.size());
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				FilterResults results = new FilterResults();
				ArrayList<GuardianPortal> filteredArrayNames = new ArrayList<GuardianPortal>();

				// perform your search here using the searchConstraint String.
				boolean filter = "true".equalsIgnoreCase(constraint.toString());
				android.util.Log.d("ALAIN", "constraint = " + constraint);
				android.util.Log.d("ALAIN", "filter = " + filter);
				for (int i = 0; i < origValues.size(); i++) {
					GuardianPortal gp = origValues.get(i);
					if (!filter || (filter && gp.isLive())) {
						filteredArrayNames.add(gp);
					}
				}

				results.count = filteredArrayNames.size();
				results.values = filteredArrayNames;
				android.util.Log.e("VALUES", results.values.toString());

				return results;
			}
		};

		return filter;
	}

	private class Holder {
		TextView name;
		TextView coords;
		TextView owner;
		TextView age;
		View color;
	}

	private int getBadgeColor(int age, boolean live) {
		if (live) {
			if (age >= 150)
				return Color.BLACK;
			else if (age >= 89)
				return Color.RED;
			else if (age >= 19)
				return Color.YELLOW;
			else
				return Color.TRANSPARENT;
		} else {
			return Color.GREEN;
		}
	}
}
