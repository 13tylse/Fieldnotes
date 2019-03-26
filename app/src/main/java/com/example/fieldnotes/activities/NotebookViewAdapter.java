package com.example.fieldnotes.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fieldnotes.R;
import com.example.fieldnotes.java.Notebook;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for Notebook RecyclerView in <code>MainActivity</code>. This class will notify any
 * attached RecyclerView of data updates to cascade changes in the database.
 * @author Tyler Seidel
 */
public class NotebookViewAdapter extends RecyclerView.Adapter<NotebookViewAdapter.FieldNotesViewHolder> {

    private final LayoutInflater inflater;
    private List<Notebook> notebooks = Collections.emptyList();

    NotebookViewAdapter(Context context) {
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
    public NotebookViewAdapter.FieldNotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = inflater.inflate(R.layout.list_item, parent, false);
        return new FieldNotesViewHolder(itemView);
    }

    /**
     * Populates data in each view at position i
     *
     * @param notesViewHolder View Holder that contains relevant views to populate
     * @param i               spot in Recycler View
     */
    @Override
    public void onBindViewHolder(@NonNull final NotebookViewAdapter.FieldNotesViewHolder notesViewHolder, int i) {
        Notebook notebook = notebooks.get(i);
        notesViewHolder.notebookTitle.setText(notebook.getNotebookName());
        notesViewHolder.notebookDate.setText(notebook.getDate().toString());
        notesViewHolder.notebookUnix.setText(String.format(Locale.ENGLISH, "%d", notebook.getUnixTime()));
    }

    /**
     * Returns count of notebooks
     *
     * @return int of counted notebooks
     */
    @Override
    public int getItemCount() {
        return notebooks.size();
    }

    /**
     * sets notebook to new list and informs adapter of changes
     *
     * @param notebooks new notebooks list to set
     */
    public void setNotebooks(List<Notebook> notebooks) {
        this.notebooks = notebooks;
        //notifies RecyclerView to repopulate list with changed data
        notifyDataSetChanged();
    }

    //nested class for storing views
    class FieldNotesViewHolder extends RecyclerView.ViewHolder {
        private final TextView notebookTitle;
        private final TextView notebookDate;
        private final TextView notebookUnix;

        private FieldNotesViewHolder(View itemView) {
            super(itemView);
            notebookTitle = itemView.findViewById(R.id.list_item_title);
            notebookDate = itemView.findViewById(R.id.list_item_date);
            notebookUnix = itemView.findViewById(R.id.list_item_unix);
        }
    }
}