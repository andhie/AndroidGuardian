package com.sentulasia.enl;

import com.sentulasia.enl.model.GuardianPortal;
import com.sentulasia.enl.widget.PortalCard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;

import java.util.List;

public class GPListAdapter extends ArrayAdapter<GuardianPortal> implements Filterable {

    List<GuardianPortal> values;

    List<GuardianPortal> origValues;

    public GPListAdapter(Context context, List<GuardianPortal> objects) {
        super(context, 0, objects);
        this.values = objects;
        this.origValues = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PortalCard card;
        if (convertView == null) {
            card = new PortalCard(getContext());
        } else {
            card = (PortalCard) convertView;
        }

        GuardianPortal portal = getItem(position);
        card.setData(portal);

        return card;
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

//    @Override
//    public Filter getFilter() {
//
//        Filter filter = new Filter() {
//
//            @SuppressWarnings("unchecked")
//            @Override
//            protected void publishResults(CharSequence constraint,
//                    FilterResults results) {
//
//                values = (List<GuardianPortal>) results.values;
//                notifyDataSetChanged();
//                android.util.Log.d("ALAIN", "values = " + values.size());
//                android.util.Log
//                        .d("ALAIN", "origValues = " + origValues.size());
//            }
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//
//                FilterResults results = new FilterResults();
//                ArrayList<GuardianPortal> filteredArrayNames = new ArrayList<GuardianPortal>();
//
//                // perform your search here using the searchConstraint String.
//                boolean filter = "true".equalsIgnoreCase(constraint.toString());
//                android.util.Log.d("ALAIN", "constraint = " + constraint);
//                android.util.Log.d("ALAIN", "filter = " + filter);
//                for (int i = 0; i < origValues.size(); i++) {
//                    GuardianPortal gp = origValues.get(i);
//                    if (!filter || (filter && gp.isLive())) {
//                        filteredArrayNames.add(gp);
//                    }
//                }
//
//                results.count = filteredArrayNames.size();
//                results.values = filteredArrayNames;
//                android.util.Log.e("VALUES", results.values.toString());
//
//                return results;
//            }
//        };
//
//        return filter;
//    }

}
