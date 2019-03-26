package com.example.fieldnotes.java;

/*
DEVELOPER NOTES:

This is a container class representing a Notebook. It consists of mostly setters and getters.
Contains a name, a unix time, which serves as the primary key of its database table, and an
ArrayList of Stops.

The only thing that needs some deeper understanding is the different types of constructors.
There are two that are intuitive: one takes the unix time, the other takes unix time and name.

The third constructor is a little more involved, and is required by the RTF generation.
The unix time is passed and the ViewModel of the activity passing it. Then it queries
the database SYNCHRONOUSLY (see out manifest for synch/asynch issues we faced,) for all its
Stops. Once the Stops are all added to stopList, we call reInitStops. Previously, the Stops
contain  all their data besides their pictures. The reInitStops method loops through the
Stops and re-initializes the Stops using a similar "deep" constructor.

All of this is so the RTF generator can be passed just a single Notebook object.
 */

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.fieldnotes.database.FieldNotesViewModel;

import java.util.ArrayList;
import java.util.Date;

/**
 * Represents the information belonging to a Notebook. Contains a set of
 * <code>Stops</code> that belong to the <code>Notebook</code>. This is the class used
 * by the notebook activity to display the available stops to the user.
 * <br/>
 * This class makes use of the Android Room Database Architecture Library. Rather than have
 * a <code>SQLiteOpenHelper</code> class, annotations within the data class will be used to
 * create the database as follows:
 *
 * <code>@Entity(table_name)</code> annotates the class as a table within the DB.
 *
 * <code>@PrimaryKey</code> and <code>@NonNull</code> are required when a class variable
 * is to represent the primary key within a table.
 * <br/>
 * Finally, the <code>@ColumnInfo(col_name)</code> tag is used to specify information regarding
 * columns, including column names and data types.
 *
 * @author Steven Hricenak (2019), Stephen Faett (2019), Tyler Seidel (2019)
 */
@Entity(tableName = "notebooks_table")
public class Notebook {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "notebook_id")
    private long unixTime;

    @ColumnInfo(name = "notebook_name")
    @NonNull
    private String notebookName;

    @Ignore
    private ArrayList<Stop> stopList;

    @Ignore
    private Date notebookDate;

    public Notebook(long unixTime) {
        this.unixTime = unixTime;
        this.notebookDate = new Date(unixTime);
    }

    /**
     * An initialization of a <code>Notebook</code> that does not require queries to the database.
     *
     * @param unixTime The <code>Unix Time</code> associated with the notebook, which also serves as its primary key.
     * @param name     The name associated with the notebook.
     */
    public Notebook(long unixTime, String name) {
        this.unixTime = unixTime;
        this.notebookName = name;
    }

    /**
     * Queries into the notebook table of the database for an entry with
     * the passed <code>unixTime</code> to get the name of the <code>Notebook</code>.
     * It then queries the stops table for all entries with foreign key equal to the
     * passed <code>unixTime</code> and fills <code>stopList</code> with
     * the <code>Stop</code> objects from the query.
     *
     * @param unixTime The primary key of the <code>Notebook</code> in the notebooks table.
     */
    public Notebook(long unixTime, final FieldNotesViewModel viewModel){
        this.unixTime = unixTime;
        this.notebookDate = new Date(unixTime);

        Notebook databaseNotebook = viewModel.getNotebookByUnixTime(unixTime);
        this.notebookName = databaseNotebook.notebookName;

        stopList = new ArrayList<>();
        stopList.addAll(viewModel.getStopsByNotebookSynchronously(this));
        reInitStops(viewModel);
    }

    /**
     * Called when the deep constructor is finished populating the <code>stopList</code>. Goes
     * through and calls the deep constructor for each <code>Stop</code>. This is not
     * nested in the deep constructor because nested observations of asynchronous calls could
     * cause problems.
     *
     * @param viewModel The <code>ViewModel</code> for the SQLite database.
     */
    private void reInitStops(FieldNotesViewModel viewModel) {
        for(int i = 0; i < stopList.size(); i++){
            Stop tempStop = new Stop(stopList.get(i).getUnixTime(), viewModel);
            stopList.set(i, tempStop);
        }
    }

    /**
     * Gets the <code>unixTime</code> of this <code>Notebook</code>.
     *
     * @return The <code>unixTime</code> of this <code>Notebook</code>.
     */
    public long getUnixTime() {
        return unixTime;
    }

    /**
     * Gets the name of this <code>Notebook</code>.
     *
     * @return The name of this <code>Notebook</code>.
     */
    public String getNotebookName() {
        return notebookName;
    }

    /**
     * Sets the <code>name</code> of the <code>notebook</code>.
     *
     * @param notebookName The <code>name</code> of the <code>notebook</code>.
     */
    public void setNotebookName(String notebookName) {
        this.notebookName = notebookName;
    }

    /**
     * Returns the names of the <code>Stop</code> objects in <code>stopList</code>
     * in an <code>ArrayList</code>.
     *
     * @return The names of the <code>Stop</code> objects in <code>stopList</code>.
     */
    public ArrayList<String> getStopNames() {
        if (stopList != null) {
            ArrayList<String> stopNames = new ArrayList<>();
            for (int i = 0; i < stopList.size(); i++) {
                stopNames.add(stopList.get(i).getStopName());
            }
            return stopNames;
        }
        return null;
    }

    /**
     * Returns the <code>unixTime</code> from the <code>Stop</code> at index
     * <code>stopIndex</code> in <code>stopList</code>.
     *
     * @param stopIndex The index of the <code>Stop</code> in <code>stopList</code>.
     * @return The <code>unixTime</code> of the <code>Stop</code> indexed to.
     */
    public long getUnixTimeOfStop(int stopIndex) {
        if (stopList != null) {
            Stop stop = stopList.get(stopIndex);
            return stop.getUnixTime();
        }
        return 0;
    }

    /**
     * Gets the list of <code>Stops</code> belonging to this <code>Notebook</code>.
     *
     * @return The list of <code>stops</code>.
     */
    public ArrayList<Stop> getStopList() {
        return stopList;
    }


    /**
     * Gets a <code>Stop</code> at a specific index.
     *
     * @param index the index of the <code>stop</code> to find.
     * @return The <code>stop</code> at the index.
     */
    public Stop getStop(int index) {
        if (stopList != null) {
            return stopList.get(index);
        }
        return null;
    }

    /**
     * Gets the first <code>Stop</code> with the passed name.
     *
     * @param name The name of the <code>stop</code> to find.
     * @return The <code>stop</code>.
     */
    public Stop getStop(String name) {
        if (stopList != null) {
            for (Stop stop : stopList) {
                if (stop.getStopName() == name) {
                    return stop;
                }
            }
        }
        return null;
    }

    /**
     * Gets the <code>date</code> associated with this <code>Notebook</code>.
     *
     * @return The <code>date</code> of this <code>Notebook</code>.
     */
    public Date getDate() {
        return notebookDate;
    }
}
