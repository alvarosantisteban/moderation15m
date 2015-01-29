package com.alvarosantisteban.moderacion15m.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alvarosantisteban.moderacion15m.R;

import java.util.List;

/**
 *
 * Adapter for the list with the resulting statistics from the debate.
 *
 * @author Alvaro Santisteban Dieguez 28/01/15 - alvarosantisteban@gmail.com
 */
public class ResultsListAdapter extends ArrayAdapter<Participant> {

    private static final String TAG = "ResultsListAdapter";

    Context context;

    private List<Participant> mItems;
    protected LayoutInflater mLayoutInflater;
    protected int mListItemLayoutResourceId;

    public ResultsListAdapter(final Context context,
                           int listItemLayoutResourceId,
                           final List<Participant> items) {
        super(context, listItemLayoutResourceId, items);
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListItemLayoutResourceId = listItemLayoutResourceId;
        mItems = items;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        Object viewHolder;

        if (view == null) {
            view = mLayoutInflater.inflate(mListItemLayoutResourceId, null);
            if (view != null) {
                viewHolder = getViewHolder(view);
                view.setTag(viewHolder);
            } else {
                throw new IllegalStateException("Layout could not be inflated.");
            }
        } else {
            viewHolder = view.getTag();
        }
        setViewContent(viewHolder, position);
        return view;
    }

    protected Object getViewHolder(final View view) {
        return new ViewHolder(view);
    }

    protected void setViewContent(final Object abstractViewHolder, int position) {
        ViewHolder viewHolder = (ViewHolder) abstractViewHolder;
        Participant participant = mItems.get(position);

        viewHolder.name.setText(participant.getName());
        viewHolder.numInterventions.setText(String.valueOf(participant.getNumInterventions()));
        viewHolder.timeInterventions.setText(participant.getInterventionsTime().toString());
    }

    protected void setResultsList(List<Participant> participants) {
        mItems = participants;
        notifyDataSetChanged();
    }

    public String toString(long item){
         return String.valueOf(item);
    }

    static class ViewHolder {

        TextView name;
        TextView numInterventions;
        TextView timeInterventions;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.list_item_results_name);
            numInterventions = (TextView) view.findViewById(R.id.list_item_results_num_interventions);
            timeInterventions = (TextView) view.findViewById(R.id.list_item_results_time_interventions);
        }
    }
}