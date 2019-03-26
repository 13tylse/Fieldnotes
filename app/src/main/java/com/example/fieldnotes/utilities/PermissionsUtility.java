package com.example.fieldnotes.utilities;

/*
DEVELOPER NOTES:

This utility class is used for asking permissions. If the user has not granted the permissions,
it makes a call to Android to ask for permissions. We call it right when the app starts and
ask for all permissions right up front, since they're needed for the bulk of the app's
functionality.

When calling the primary method, getPermissions, you pass the activity you're in (usually "this",)
and the permissions you want, separated by single pipe. For example, if you need permissions for
camera and location, you'd call

getPermissions(this, CAMERA | LOCATION);

If you need to add a permission:
1.) Add a new constant with an appropriate name. It MUST be assigned to an integer of a power of 2.
    We use bitwise operations, so it MUST be a power of 2.
2.) There's a chain of ifs in getPermissions with the following form:

    if ((permissionRequest & CONST_NAME) == CONST_NAME){
            permissionsList.add(Manifest.permission.NEEDED_PERMISSION);
    }

    Add another if statement of this form with your new constant, with all the permission names
    you need.

3.) Don't forget to add the permissions you want to AndroidManifest.xml.

 */

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

/**
 * A utility class for checking the Android permissions.
 */
public class PermissionsUtility {
    //ALL should contain all 1's, where the number of 1's is the number of possible
    public static final int ALL = 111;

    //finals should be powers of 2
    //1, 2, 4, 8, 16, 32, etc.
    public static final int CAMERA = 1;
    public static final int EXTERNAL_STORAGE = 2;
    public static final int LOCATION = 4;

    private static Activity activity;

    /**
     * When passes a constant from this class, it will call the corresponding helper functions
     * to ask the user for the appropriate permissions.
     * <p>
     * Example: to get permissions for both the camera nd external storage, the appropriate
     * call would be:
     * <code>PermissionsUtility.getPermissions(this, CAMERA | EXTERNAL_STORAGE);</code>
     *
     * @param act               The <code>Activity</code> calling this method.
     * @param permissionRequest One, or a combination of using binary or, the constants of this class.
     * @return True, if all the permissions requested are granted, and false otherwise.
     */
    public static boolean getPermissions(Activity act, int permissionRequest) {

        activity = act;

        ArrayList<String> permissionsList = new ArrayList<>();
        if ((permissionRequest & CAMERA) == CAMERA)
            permissionsList.add(Manifest.permission.CAMERA);
        if ((permissionRequest & EXTERNAL_STORAGE) == EXTERNAL_STORAGE) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if ((permissionRequest & LOCATION) == LOCATION) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        String[] permissions = new String[permissionsList.size()];

        for (int i = 0; i < permissions.length; i++)
            permissions[i] = permissionsList.get(i);

        if (checkPermissions(permissions))
            return true;

        ActivityCompat.requestPermissions(activity, permissions, ALL);

        return false;
    }

    /**
     * Takes the list of permissions passed to it and checks if the app has them. Asks the user
     * for each permission that they have not already granted.
     *
     * @param permissions The permissions needed.
     * @return True if all permissions are granted; false otherwise.
     */
    private static boolean checkPermissions(String[] permissions) {
        for (String perm : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
