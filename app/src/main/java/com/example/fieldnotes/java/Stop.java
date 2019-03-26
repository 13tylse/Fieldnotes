package com.example.fieldnotes.java;

/*
DEVELOPER NOTES:

This is another pretty standard container class.

It's the job of this class to store all the information the user enters on StopActivity.
That is, the time, the latitude and longitude, any notes taken, and a collection of Pictures.

All of these real world values exist as attributes with all appropriate setters and getter.

Like all of our database tables, the unixTime is the primary key. Each Stop is linked to its parent
Notebook where the foreign key is the unixTime of the notebook, called parentUnixTime here.

In the Notebook class we document a "deep" constructor. A similar constructor is here.
When you pass both the unixTime and the ViewModel of the database, the database is queried for the
Stop's information, and then populates the parentUnixTime ArrayList with all the pictures found
in the database belonging to this Stop. These queries are SYNCHRONOUS. See our manifest's
documentation for more details on our synch/asynch issues.
 */

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.fieldnotes.database.FieldNotesViewModel;

import java.util.ArrayList;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Represents the information of a specific location made by the user.
 * Contains <code>longitude</code>, <code>latitude</code>, <code>notes</code>,
 * and <code>time</code>.
 * <br/>
 * This class makes use of the Android Room Database Architecture Library. Rather than have
 * a <code>SQLiteOpenHelper</code> class, annotations within the data class will be used to
 * create the database as follows:
 * <br/>
 * <code>@Entity(table_name)</code> annotates the class as a table within the DB.
 * <br/>
 * <code>@PrimaryKey</code> and <code>@NonNull</code> are required when a class variable
 * is to represent the primary key within a table.
 * <br/>
 * Finally, the <code>@ColumnInfo(col_name)</code> tag is used to specify information regarding
 * columns, including column names and data types.
 *
 * @author Steven Hricenak (2019), Tyler Seidel (2019)
 */
@Entity(tableName = "stops_table")
public class Stop {

    @Ignore
    ArrayList<Picture> pictureList;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "stop_id")
    private long unixTime;

    @ColumnInfo(name = "stop_name")
    @NonNull
    private String stopName;
    @ForeignKey(entity = Notebook.class,
            parentColumns = "notebook_id",
            childColumns = "parent_notebook_id",
            onDelete = CASCADE)
    @ColumnInfo(name = "parent_notebook_id")
    @NonNull
    private long parentUnixTime;


    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;
    @ColumnInfo(name = "notes")
    private String notes;
    @ColumnInfo(name = "stop_time")
    private long time; //could be updated, so could be different from unixTime

    /**
     * Creation method for a <code>Stop</code> that does not require querying the database.
     *
     * @param unixTime       The time of creation of the <code>Stop</code>,
     *                       which also serves as its primary key in the database.
     * @param stopName       The name of the <code>Stop</code>.
     * @param parentUnixTime The <code>unixTime</code> of the <code>Notebook</code>
     *                       the <code>Stop</code> is created for.
     * @param notes          The text inputted by the user for notes.
     * @param latitude       The <code>Latitude</code> coordinate of the stop.
     * @param longitude      The <code>Longitude</code> coordinate of the stop.
     */
    @Ignore
    public Stop(long unixTime, String stopName, long parentUnixTime, String notes, double latitude, double longitude) {
        this.unixTime = unixTime;
        this.stopName = stopName;
        this.parentUnixTime = parentUnixTime;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = unixTime;
    }

    /**
     * Queries the stops table in the database with the passed in <code>unixTime</code> for the
     * entry with the passed <code>unixTime</code>, then populates the attributes with the
     * information from the query. It then queries the pictures table for all entries with foreign
     * key equal to the passed <code>parentUnixTime</code> and fills <code>pictureList</code> with
     * the <code>Picture</code> objects from the query.
     *
     * @param unixTime The primary key of the <code>Stop</code> in the stops table.
     */
    public Stop(long unixTime, FieldNotesViewModel viewModel) {
        this.unixTime = unixTime;

        //synchronously query database
        Stop databaseStop = viewModel.getStopByUnixTime(unixTime);
        this.stopName = databaseStop.stopName;
        this.parentUnixTime = databaseStop.parentUnixTime;
        this.notes = databaseStop.notes;
        this.longitude = databaseStop.longitude;
        this.latitude = databaseStop.latitude;
        this.time = databaseStop.time;

        pictureList = new ArrayList<>();
        pictureList.addAll(viewModel.getPicturesByParentUnixTimeSynchronously(databaseStop.unixTime));
    }

    /**
     * Queries the stops table in the database for the entry with the passed <code>unixTime</code>,
     * then populates the attributes with the information from the query.
     *
     * @param unixTime The primary key of the <code>Stop</code> in the stops table.
     */
    public Stop(long unixTime) {
        this.unixTime = unixTime;
    }

    /**
     * Returns the <code>unixTime</code> of this <code>Stop</code>.
     *
     * @return The <code>unixTime</code> of this <code>Stop</code>.
     */
    public long getUnixTime() {
        return unixTime;
    }

    /**
     * Returns the name of this <code>Stop</code>.
     *
     * @return The name of this <code>Stop</code>.
     */
    public String getStopName() {
        return stopName;
    }

    /**
     * Sets the name of this <code>Stop</code>.
     *
     * @param name The name of this <code>Stop</code>.
     */
    public void setStopName(String name) {
        this.stopName = name;
    }

    /**
     * Sets the <code>Notes</code> associated with this stop.
     *
     * @param notes The text logs entered by the user.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Sets the coordinates associated with the <code>Stop</code>.
     *
     * @param latitude  The <code>latitude</code> coordinate of the <code>Stop</code>.
     * @param longitude The <code>longitude</code> coordinate of the <code>Stop</code>.
     */
    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the <code>unixTime</code> of the <code>Notebook</code>this <code>Stop</code>
     * belongs to.
     * That is, when you reach this <code>Stop</code> from its parent <code>Notebook</code>,
     * the <code>unixTime</code> of that <code>Notebook</code> for easy access if the user
     * needs to go up a directory.
     *
     * @return The <code>unixTime</code> of the parent <code>Notebook</code>.
     */
    public long getParentUnixTime() {
        return parentUnixTime;
    }

    /**
     * Sets the <code>unixTime</code> of the <code>Notebook</code> this <code>Stop</code>
     * belongs to.
     * <br/>
     * This seems to be needed for the SQLite database, but SHOULD NOT be called from
     * developer written code. We should look into possibilities for making this function
     * accessible only to the database, or see if we can make it non-modifiable.
     *
     * @param parentUnixTime The <code>unixTime</code> that the parent <code>Notebook</code> will
     *                       be updated to.
     */
    public void setParentUnixTime(long parentUnixTime) {
        this.parentUnixTime = parentUnixTime;
    }

    /**
     * Returns the <code>longitude</code> of this <code>Stop</code>.
     *
     * @return The <code>longitude</code> of this <code>Stop</code>.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the <code>longitude</code> coordinate of the <code>Stop</code>.
     * Needed for the SQLite database.
     *
     * @param longitude The <code>longitude</code> coordinate of the <code>Stop</code>.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the <code>latitude</code> of this <code>Stop</code>.
     *
     * @return The <code>latitude</code> of this <code>Stop</code>.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the <code>latitude</code> coordinate of the <code>Stop</code>.
     * Needed for the SQLite database.
     *
     * @param latitude The <code>latitude</code> coordinate of the <code>Stop</code>.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the <code>notes</code> belonging to this <code>Stop</code>.
     *
     * @return The <code>notes</code> belonging to this <code>Stop</code>.
     */
    public String notes() {
        return notes;
    }

    /**
     * Returns the <code>time</code> corresponding to this <code>Stop</code>.
     * This is different from the <code>unixTime</code>, since the user has the option
     * to update the time of a <code>Stop</code>.
     * The <code>unixTime</code> is used for database operations, while <code>time</code>,
     * which may be equal to <code>unixTime</code> but doesn't have to, is the time
     * displayed/controlled to the user.
     *
     * @return The actual <code>time</code> associated with this <code>Stop</code>.
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the actual <code>Time</code> for this stop. It can differ from the <code>UnixTime</code>
     * provided upon creation, but is originally the same.
     *
     * @param time The actual <code>Time</code> defined by the user.
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Returns the <code>Picture</code> object at the specified <code>index</code> in the
     * <code>ArrayList</code>.
     *
     * @param index The index of the <code>Picture</code> in the <code>ArrayList</code>.
     * @return The <code>Picture</code> object requested.
     */
    public Picture getPictureAtIndex(int index) {
        if (pictureList != null && index < pictureList.size()) {
            return pictureList.get(index);
        } else {
            return null;
        }
    }

    /**
     * Returns the <code>ArrayList</code> of <code>Pictures</code>.
     *
     * @return The <code>ArrayList</code> of <code>Pictures</code>.
     */
    public ArrayList<Picture> getPictureArrayList() {
        return pictureList;
    }
}