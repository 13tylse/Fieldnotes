package com.example.fieldnotes.java;

/*
DEVELOPER NOTES:

The container class for a Picture belonging to a Stop.
Most of the attributes are fairly straightforward: there's a unixTime that's used for
a database primary key, a parentUnixTime of the parent Stop for the foreign key,
and a caption for the picture.

Note that this class does not contain any actual Pictures or Bitmaps. It contains a filepath that
is parsed by the StopActivity when ready to display to the user. When the user takes a photo,
the database stores the ABSOLUTE file path of the picture. This is detailed more in the
CaptionActivity class, but the picture will be in the default photos directory in a
subdirectory titled "Fieldnotes."
 */

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.fieldnotes.database.FieldNotesViewModel;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Container class for a photo belonging to a <code>Stop</code>. Contains attributes for
 * its <code>unixTime</code>, which is used as the primary key in the pictures table of the
 * database. It also contains not a picture, but the filepath to a picture saved by the phone
 * (since databases can't save pictures.)
 *
 * @author Steven Hricenak (2019) Stephen Faett (2019)
 */
@Entity(tableName = "pictures_table")
public class Picture {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "picture_id")
    private long unixTime;

    @ForeignKey(entity = Stop.class,
            parentColumns = "stop_id",
            childColumns = "stop_id",
            onDelete = CASCADE)
    @ColumnInfo(name = "parent_stop_id")
    private long parentUnixTime; //foreign key for parent Stop

    @ColumnInfo(name = "file_path")
    private String filePath;    //absolute file path

    @ColumnInfo(name = "caption")
    private String caption;

    /**
     * Queries into the pictures table of the database for an entry with
     * the passed <code>unixTime</code> to get the file path for the image.
     *
     * @param unixTime The primary key of the <code>Picture</code> in the pictures table.
     */
    public Picture(long unixTime, FieldNotesViewModel viewModel) {
        this.unixTime = unixTime;

        //synchronously query database
        Picture databasePicture = viewModel.getPictureByUnixTime(unixTime);
        this.parentUnixTime = databasePicture.parentUnixTime;
        this.filePath = databasePicture.filePath;
        this.caption = databasePicture.caption;
    }

    /**
     * An initialization of a <code>Picture</code> that does not require queries to the database.
     *
     * @param unixTime The <code>unixTime</code> associated with the <code>Picture</code>,
     *                 which also serves as its primary key.
     * @param filePath The file path on the device for the associated image.
     */
    public Picture(long unixTime, String filePath) {
        this.unixTime = unixTime;
        this.filePath = filePath;
        this.caption = "";
    }

    /**
     * An initialization of a <code>Picture</code> that does not require queries to the database.
     *
     * @param unixTime The <code>unixTime</code> associated with the <code>Picture</code>,
     *                 which also serves as its primary key.
     * @param path     The absolute file path on the device for the associated image
     * @param caption  The text caption associated with the image.
     */
    public Picture(long unixTime, String path, String caption) {
        this.unixTime = unixTime;
        this.filePath = path;
        this.caption = caption;

    }

    /**
     * Returns the <code>unixTime</code> associated with the image,
     * which serves as its primary key in the database.
     *
     * @return the <code>unixTime</code> associated with the image.
     */
    public long getUnixTime() {
        return unixTime;
    }

    /**
     * Returns the <code>unixTime</code> of the <code>Stop</code> this <code>Picture</code>
     * belongs to. This is for easy access for returning to the previous <code>Stop</code>.
     *
     * @return The <code>unixTime</code> of the parent <code>Stop</code>.
     */
    public long getParentUnixTime() {
        return parentUnixTime;
    }

    /**
     * Sets the <code>unixTime</code> of the <code>Stop</code> this <code>Picture</code>
     * belongs to.
     * <p>
     * This seems to be needed for the SQLite database, but SHOULD NOT be called from
     * developer written code. We should look into possibilities for making this function
     * accessible only to the database, or see if we can make it non-modifiable.
     *
     * @param parentUnixTime The <code>unixTime</code> that the parent <code>Stop</code> will
     *                       be updated to.
     */
    public void setParentUnixTime(long parentUnixTime) {
        this.parentUnixTime = parentUnixTime;
    }

    /**
     * Returns the file path of the saved image on the device.
     *
     * @return The file path to the image.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the file path for the image.
     *
     * @param path The file path supplied for the image.
     */
    public void setFilePath(String path) {
        this.filePath = path;
    }

    /**
     * Returns the caption associated with the image.
     *
     * @return The text caption associated with the image.
     */
    public String caption() {
        return caption;
    }

    /**
     * Sets the caption for the image.
     *
     * @param caption The desired caption to pair to the image.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

}
