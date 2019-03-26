package com.example.fieldnotes.utilities;

/*
DEVELOPER NOTES:

This class only has one public method: create RTF. This method will create an RTF
document with the Notebook's name passed to it as the title, so NotebookName.rtf.
This file is saved in the default Downloads directory.
This method must be called in its own dedicated thread. If you attempt to call it on the
main UI thread, the app WILL crash.

Rtf generation uses a Buffered Writer to append RTF script to an open file. This writer is passed
to all helper functions, which subsequently declare "throws IOException" that is handled in the
main createRTF method.

The writer starts with the title of the notebook, which is centered in the document and has
a little larger text. Then getStopsText is called to loop through all the Stops and output
that information to the document. This helper method will also call getPicturesString,
which encodes all the pictures belonging to the current Stop. There's a few more helper
functions that format the date/time and geolocation, but these are fairly straightforward.

The trickiest bit of code here is encoding pictures to hexadecimal. This is how rtf
encode/decodes its pictures (so we don't have file dependencies when sharing the rtf file.)
The InputStream class has a method called "read" that returns the next byte of information, in
the form of an integer. The Integer class has a "toHex" function, but it is important to note that
this function does not pad zeroes. That is, if the byte can be expressed in one hex digit, it is.
This is a problem, since rtf needs all its hexadecimal byes to be exactly two digits. To
prevent issues, we simply check if the length of the hex is 1, and if it is, manually
concatenate a 0 to the beginning.

The resource used for learning the RTF used here can be found at
https://www.oreilly.com/library/view/rtf-pocket-guide/9781449302047/ch01.html
(If the link is broken, try Google-ing "RTF Pocket Guide.")

Here are a few quick tips for RTF:

The header of the RTF file is contained in the constant RTF_HEADER. The only thing that
should really be changed/added here is the font. Currently, it's Times New Roman, but fonts can
be added like this:

{\rtf1\ansi\deff0 {\fonttbl {\f0 Times New Roman;} {\f1 Arial;}}
Then use the \f0 tag for Times and the \f1 tag for Arial.

The entire document is surrounded in {}, the first { being in the header constant, the last }
being added right before closing the writer.

Paragraph text is similar:
{\pard\qc\f0\fs24 Your text here. \par}
Where the \pard tag starts the paragraph, \qc centers the text (omit for left-justified,)
the \f0 is the font assigned to f0 (Times New Roman,) and \fs24 is the font size.
RTF works in half steps, so 24 is actually 12 point font, and 25 would be 12.5 point font.

This is also an issue with pictures. The height and width of the pictures are multiplied by
16 in this case. We experimented with this constant a little bit, and 16 seems to be the
best multiplier.

Pictures have this syntax:
{\pict\jpegblip\picw yourPictureWidth \pich yourPictureHeight \picwgoal yourPictureWidth \pichgoal yourPictureHeight \hex yourEncodedHexCode}

This can be surrounded by paragraphs if you want to center them.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.fieldnotes.java.Notebook;
import com.example.fieldnotes.java.Picture;
import com.example.fieldnotes.java.Stop;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RtfUtility {

    private static final String TAG = "RTF Utility";

    private static final String RTF_HEADER = "{\\rtf1\\ansi\\deff0 {\\fonttbl {\\f0 Times New Roman;}}";
    private static final String RTF_NEWLINE = System.getProperty("line.separator");

    private static final int SCALE_FACTOR = 4;

    /**
     * Creates .rtf file and writes to it using helper methods. Should not be run in the main
     * thread. Needs to be run in its own thread or it will crash the app.
     *
     * @param notebook <code>Notebook</code> that will have contents put into file.
     * @return The file generated.
     */
    public static File createRTF(Notebook notebook) {
        File file = null;

        try {
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), notebook.getNotebookName() + ".rtf");
            int i = 1;
            while (file.exists()) {
                file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), notebook.getNotebookName() + "(" + i + ").rtf");
                i++;
            }

            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            generateRichText(notebook, writer);
            writer.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return file;
    }

    /**
     * Private method that generates a new RTF file given a notebook.
     *
     * @param notebook <code>Notebook</code> that will have contents put into file.
     * @param writer Output stream for writing notebooks contents into.
     * @throws IOException Handled in <code>createRTF</code>.
     */
    private static void generateRichText(Notebook notebook, BufferedWriter writer) throws IOException {
        writer.append(RTF_HEADER);
        writer.append(RTF_NEWLINE);

        String name = notebook.getNotebookName();
        writer.append("{\\pard\\qc\\f0\\fs48 " + name + "\\par}");
        writer.append(RTF_NEWLINE);

        ArrayList<Stop> stops = notebook.getStopList();
        getStopsText(stops, writer);

        writer.append("}");
    }

    /**
     * Gets data from every <code>Stop</code> and converts each into string for storing in a file.
     *
     * @param stops List of <code>Stops</code> to convert into .rtf readable format.
     * @param writer Output stream to write string outputs into.
     * @throws IOException Handled in <code>createRTF</code>.
     */
    private static void getStopsText(ArrayList<Stop> stops, BufferedWriter writer) throws IOException {
        boolean first = true;
        for (Stop stop : stops) {
            //If it's the first Stop do nothing; otherwise print a new page.
            if(first)
                first = false;
            else
                writer.append("{\\page}");

            String name = stop.getStopName();
            writer.append("{\\pard\\f0\\fs32 " + name + "\\par}");
            writer.append(RTF_NEWLINE);

            String timeString = getTimeString(stop.getTime());
            writer.append("{\\pard\\f0\\fs24 Time: " + timeString + "\\par}");

            String coordString = getCoordinatesString(stop.getLongitude(), stop.getLatitude());
            if (coordString != null)
                writer.append("{\\pard\\f0\\fs24 Coordinates: " + coordString + "\\par}");

            writer.append(RTF_NEWLINE);

            String notes = stop.notes();
            if(notes != null)
                writer.append("{\\pard\\f0\\fs24 " + notes + "\\par}");

            writer.append(RTF_NEWLINE);
            ArrayList<Picture> pics = stop.getPictureArrayList();
            if (pics != null && pics.size() > 0) {
                writer.append("{\\pard \\par}");
                getPicturesString(pics, writer);
            }
        }
    }

    /**
     * Converts milliseconds into standard time format with AM and PM.
     *
     * @param time Time in milliseconds since Epoch.
     * @return String of converted time into standard format (HH:MM)
     */
    private static String getTimeString(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; //January is 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR);
        if (hour == 0)   //calendar represents noon and midnight with 0.
            hour = 12;
        int ampm = calendar.get(Calendar.AM_PM);
        String ampmText = (ampm == Calendar.AM ? "am" : "pm");
        int minute = calendar.get(Calendar.MINUTE);

        DecimalFormat df = new DecimalFormat("00");
        String dateText = "" + df.format(month) + "/" + df.format(day) + "/" + year;
        String timeText = "" + hour + ":" + df.format(minute) + ampmText;
        String dateTime = timeText + " " + dateText;

        return dateTime;
    }

    /**
     * Gets the corresponding letter for the longitude and latitude and converts them into strings.
     *
     * @param longitude Value to convert.
     * @param latitude Value to convert.
     * @return The readable representation of the coordinates.
     */
    private static String getCoordinatesString(double longitude, double latitude) {
        if (longitude == 0 && latitude == 0)
            return null;

        String longDir = (longitude > 0 ? "E" : "W");
        String latDir = (latitude > 0 ? "N" : "S");

        longitude = Math.abs(longitude);
        latitude = Math.abs(latitude);

        String fullCoordinates = "" + latitude + latDir + ", " + longitude + longDir;
        return fullCoordinates;
    }

    /**
     * Gets all <code>Picture</code> objects in list and converts them to bitmaps to compress and
     * store them in a file. Compresses pictures into JPEG format at 50% quality.
     *
     * @param pictures List of pictures to convert and store in output stream.
     * @param writer Output stream that will have pictures stored in it.
     * @throws IOException Handled in <code>createRTF</code>.
     */
    private static void getPicturesString(ArrayList<Picture> pictures, BufferedWriter writer) throws IOException {
        if (pictures.size() == 0)
            return;
        InputStream imageIP;
        Bitmap bitmap;
        for (Picture picture : pictures) {
            String filepath = picture.getFilePath();

            File testFile = new File(filepath);
            if(!testFile.exists())
                continue;

            imageIP = new FileInputStream(filepath);
            int orientation = PictureGetter.getOrientation(picture.getFilePath());
            bitmap = BitmapFactory.decodeFile(picture.getFilePath());
            bitmap = PictureGetter.fixOrientation(bitmap, orientation);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] imageInBytes = stream.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(imageInBytes);


            if (imageIP == null || bitmap == null)
                continue;

            int width = bitmap.getWidth() * SCALE_FACTOR;
            int height = bitmap.getHeight() * SCALE_FACTOR;
            writer.append("{\\pard\\qc ");
            writer.append("{\\pict\\jpegblip\\picw" + width + "\\pich" + height
                    + "\\picwgoal" + width + "\\pichgoal" + height + "\\hex ");
            getHex(bis, writer);
            writer.append("} \\par}");
            writer.append(RTF_NEWLINE);
            String caption = picture.caption();
            writer.append("{\\pard \\par}");
            writer.append("{\\pard\\qc\\f0\\fs20 " + caption + "\\par}");
            writer.append(RTF_NEWLINE);
        }
    }

    /**
     * Gets the input stream for the image and converts each line to hex values
     * for storing in a file. Writes hex value to output stream.
     *
     * @param imageIP The input stream of the image.
     * @param writer The output stream.
     * @throws IOException Handled in <code>createRTF</code>.
     */
    private static void getHex(InputStream imageIP, BufferedWriter writer) throws IOException {
        int temp;
        while ((temp = imageIP.read()) != -1) { //gives -1 when stream reaches EOF
            String hexString = Integer.toHexString(temp);
            if (hexString.length() == 1)
                hexString = "0" + hexString;
            writer.append(hexString);
        }
    }
}