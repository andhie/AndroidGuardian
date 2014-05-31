package com.sentulasia.enl;

import com.sentulasia.enl.model.GuardianPortal;
import com.sentulasia.enl.widget.PortalCard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class GPListAdapter extends ArrayAdapter<GuardianPortal> implements Filterable {

    private List<GuardianPortal> origValues;

    private PortalFilter mFilter;

    public GPListAdapter(Context context, List<GuardianPortal> objects) {
        super(context, 0, objects);
        this.origValues = new ArrayList<GuardianPortal>(objects);
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
    public void sort(Comparator<? super GuardianPortal> comparator) {
        super.sort(comparator);
        Collections.sort(origValues, comparator);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new PortalFilter();
        }

        return mFilter;
    }

    private class PortalFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            List<GuardianPortal> filteredList = new ArrayList<GuardianPortal>(origValues.size());

            if (constraint.length() > 0) {
                // perform your search here using the searchConstraint String.
                String query = constraint.toString().toUpperCase(Locale.US);
                int size = origValues.size();
                for (int i = 0; i < size; i++) {
                    GuardianPortal portal = origValues.get(i);

                    boolean name = portal.getPortal_name().toUpperCase(Locale.US).contains(query);
                    boolean location = portal.getLocation().toUpperCase(Locale.US).contains(query);
                    boolean agent = portal.getAgent_name().toUpperCase(Locale.US).contains(query);

                    if (name || location || agent) {
                        filteredList.add(portal);
                    }

                }
            } else {
                filteredList.addAll(origValues);
            }

            results.count = filteredList.size();
            results.values = filteredList;

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<GuardianPortal> filteredList = (List<GuardianPortal>) results.values;
            clear();
            addAll(filteredList);
        }
    }

}
