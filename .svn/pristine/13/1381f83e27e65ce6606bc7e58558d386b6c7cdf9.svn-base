package com.example.fieldnotes.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fieldnotes.R;
import com.example.fieldnotes.java.Stop;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for Stop RecyclerView in <code>MainActivity</code>. This class will notify any
 * attached RecyclerView of data updates to cascade changes in the database.
 */
public class StopViewAdapter extends RecyclerView.Adapter<StopViewAdapter.FieldNotesViewHolder> {

    private final LayoutInflater inflater;
    private List<Stop> stops = Collections.emptyList();
    StopViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    /**
     * Creates new view with custom ViewHolder class.
     *
     * @param parent parent view of adapter
     * @return ViewHolder of view with populated view fields
     */
    @NonNull
    @Override
    public StopViewAdapter.FieldNotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = inflater.inflate(R.layout.list_item, parent, false);
        return new StopViewAdapter.FieldNotesViewHolder(itemView);
    }

    /**
     * Populates data in each view at position i
     *
     * @param stopViewHolder View Holder that contains relevant views to populate
     * @param i               spot in Recycler View
     */
    @Override
    public void onBindViewHolder(@NonNull final StopViewAdapter.FieldNotesViewHolder stopViewHolder, int i) {
        Stop stop = stops.get(i);
        stopViewHolder.stopTitle.setText(stop.getStopName());
        Date date = new Date(stop.getUnixTime());
        stopViewHolder.stopDate.setText(date.toString());
        stopViewHolder.stopUnix.setText(String.format(Locale.ENGLISH, "%d", stop.getUnixTime()));
    }

    /**
     * Returns count of <code>Stop</code>
     *
     * @return int of counted stops
     */
    @Override
    public int getItemCount() {
        return stops.size();
    }

    /**
     * sets <code>Stop</code> to new list and informs adapter of changes
     *
     * @param stops new stops list to set
     */
    public void setStops(List<Stop> stops) {
        this.stops = stops;
        //notifys RecyclerView to repopulate list with changed data
        notifyDataSetChanged();
    }

    //nested class for storing views
    class FieldNotesViewHolder extends RecyclerView.ViewHolder {
        private final TextView stopTitle;
        private final TextView stopDate;
        private final TextView stopUnix;

        private FieldNotesViewHolder(View itemView) {
            super(itemView);
            stopTitle = itemView.findViewById(R.id.list_item_title);
            stopDate = itemView.findViewById(R.id.list_item_date);
            stopUnix = itemView.findViewById(R.id.list_item_unix);
        }
    }
}