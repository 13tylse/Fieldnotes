package com.example.fieldnotes.activities;

/*
DEVELOPER NOTES:

The way this Activity works is that the first time the user reaches it, the displayed image will
be null. This null object results in the camera activity being called. Once the user takes
a photo, the user returns here, with the ImageView now populated.

The openCameraActivity method needs some digestion. The first two lines of code initialize
"safe mode," which is necessary for the app to save image files properly. (Safe mode is
also needed for sharing RTFs.) The next few lines are pretty standard: we create a dedicated
subdirectory named "Fieldnotes" inside the default Android picture directory, and create a
file where the image will be saved. (Note: this is like "touching" a file in a Linux
environment: we create the file, but nothing is there yet.) We set up an intent for the
Camera Activity, and we start an activity FOR RESULT, since we need to display the new
picture when the user returns. Then there's a large chunk of code creating meta data for the
picture using a ContentResolver and the MediaStore class. Actually putting the meta data there
isn't too bad if you have an example, since the MediaStore class has constants for every field.
At the end of the method, we create the a new picture object with the current unixTime and
the ABSOLUTE filepath of the file we just created. We do not commit this object to the database yet,
but save it for when we return from the camera intent. (The object is preserved across the
camera activity; that is, all the data here will remain when the camera activity is finished.)

The camera activity allows users to either accept pictures, retake them, or cancel.
Accept and cancel are handled in the onActivityResult method. If the user cancels out of the
camera activity, this entire activity finishes, and for this reason: when the user is in the
Stop Activity and clicks the camera button, they are actually redirected to this activity,
but the camera is activated immediately, so it doesn't seem like they went to an intermediate.
Cancelling out of the camera activity and being directed to a blank Caption Activity would make
no sense, so they are returned to the Stop Activity.
Handling a RESULT_OK is much more intuitive. We get the actual image from the filepath we saved
earlier (the camera intent does not return an image,) we use our PictureUtility to ensure the
orientation is correct, and the populate then ImageView with a Bitmap generated from the filepath.

The commits to the database don't happen until the user hits one of the two save buttons.
The pictures are discarded if the user hits "back."
 */

import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.fieldnotes.R;
import com.example.fieldnotes.database.FieldNotesViewModel;
import com.example.fieldnotes.java.Picture;
import com.example.fieldnotes.utilities.PictureGetter;

import java.io.File;
import java.util.Locale;

/**
 * An <code>Activity</code> that handles reviewing the picture the user just took. Upon creation
 * it calls a function that checks user permissions and then sends the user to the camera
 * activity, where the user takes a picture that is saved to a dedicated subdirectory in the
 * default pictures path of the phone. Upon returning from the camera activity, the
 * <code>ImageView</code> is populated with the picture just taken and the user can add
 * a caption. The user then can take another photo, or click the back button to return to
 * the parent <code>Stop</code> activity.
 * <p>
 * Functionality is not yet completed for saving the pictures to the database.
 *
 * @author Steven Hricenak (2019)
 */
public class CaptionActivity extends AppCompatActivity {

    //constants
    private static final String PARENT_UNIX = "Parent Unix Time";
    private static final String TAG = "CaptionActivity";
    private static final int TAKE_PICTURE_REQUEST_CODE = 1;

    Picture picture;
    Bitmap bitmap;
    ImageView imageView;
    long parentUnixtime;
    private int width;
    private int height;
    private FieldNotesViewModel viewModel;

    /**
     * Executes all code from the parent function, and additionally initializes the
     * <code>ImageView</code>, and calls the Android camera.
     *
     * @param savedInstanceState The data stored by the app when it is minimized, brought out
     *                           of focus, etc. so the app can restore its data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewModel = ViewModelProviders.of(this).get(FieldNotesViewModel.class);

        //gets data from passed Intent
        parentUnixtime = getIntent().getLongExtra(PARENT_UNIX, 0);

        //gets height and width for picture
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        imageView = findViewById(R.id.picture);

        if(imageView.getDrawable() == null)
            openCameraActivity();
    }

    /**
     * Executes when the app returns from the camera intent. First checks if the
     * <code>requestCode</code> aligns the the camera request
     * (<code>TAKE_PICTURE_REQUEST_CODE</code>,) then displays the new picture in the
     * <code>imageView</code>.
     * <p>
     * According to library standards, the <code>resultCode</code> will be -1 if it executed
     * correctly, and will be 0 if an error occurred or the user cancelled out of the
     * camera activity. This seems counter intuitive but
     *
     * @param requestCode The code that the initial <code>Intent</code> was sent with.
     * @param resultCode  The code returned from the activity.
     * @param intent      The intent sent from the activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == TAKE_PICTURE_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_CANCELED: //cancel button is hit
                    finish();
                    break;
                case RESULT_OK: //okay button is hit
                    picture.setParentUnixTime(parentUnixtime);
                    int orientation = PictureGetter.getOrientation(picture.getFilePath());
                    bitmap = BitmapFactory.decodeFile(picture.getFilePath());
                    bitmap = PictureGetter.fixOrientation(bitmap, orientation);
                    imageView.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    /**
     * Opens the Android camera activity. Saves the camera taken to a "Fieldnotes"
     * subdirectory. (It will also create the subdirectory if it does not exist.)
     * Also creates a <code>Picture</code> object with the current time
     * and the filepath the image was saved to.
     */
    private void openCameraActivity() {
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            long unixTime = System.currentTimeMillis();
            String pictureName = Long.toString(unixTime) + ".jpg";
            String appDirectory = "Fieldnotes";
            File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), appDirectory);
            imageRoot.mkdir();
            File img = new File(imageRoot, pictureName);
            Uri outputFileUri = Uri.fromFile(img);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);

            //Adding meta data for the picture
            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.TITLE, pictureName);
            values.put(MediaStore.Images.Media.DESCRIPTION, "");
            values.put(MediaStore.Images.Media.DATE_TAKEN, unixTime);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.ORIENTATION, 0);
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, img.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, img.getName().toLowerCase(Locale.US).hashCode());
            values.put("_data", img.getAbsolutePath());

            ContentResolver contentResolver = getContentResolver();
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            //if picture was taken correctly, we create a picture object to use
            String filePath = img.getAbsolutePath();
            picture = new Picture(unixTime, filePath);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Method that corresponds to save button in activity, saves picture and caption in database.
     * Updates database synchronously.
     */
    private void save() {
        EditText captionField = (EditText) findViewById(R.id.caption);
        picture.setCaption(captionField.getText().toString());
        viewModel.insert(picture);
        captionField.setText("");
    }

    /**
     * Saves picture and returns user to previous activity, StopActivity.
     *
     * @param view The view that calls this method.
     */
    public void saveAndReturn(View view) {
        save();
        onBackPressed();
    }

    /**
     * Saves picture and reopens CaptionActivity to take another photo.
     *
     * @param view The view that calls this method.
     */
    public void saveAndRetake(View view) {
        save();
        openCameraActivity();
    }
}