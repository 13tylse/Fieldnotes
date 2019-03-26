package com.example.fieldnotes.utilities;

/*
DEVELOPER NOTES:

There's not too much heavy stuff here. It's verbose to force the keyboard to display/hide, so
instead of calling those few lines each time in our code, we thought it'd be cleaner to
have the methods here.

Both require an Activity, which is almost always "this." To hide the keyboard, you need to provide
the View in which the keyboard is displayed. We do this be saving the current view to a local
variable when we call forceShowKeyboard, then pass that local variable back to
forceHideKeyboard when we want to hide it.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * A utility for handling functionality with the Android keyboard. Currently has two functions:
 * one for forcing the Android keyboard to display, and one forcing it to close.
 */
public class KeyboardUtility {

    /**
     * Forces the <code>Activity</code> passed to display the Android keyboard.
     *
     * @param activity The <code>Activity</code> to display tha Android keyboard.
     */
    public static void forceShowKeyboard(Activity activity) {
        if (activity == null)
            return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Forces the <code>Activity</code> to remove the keyboard from the <code>View</code>.
     *
     * @param activity The <code>Activity</code> to display tha Android keyboard.
     * @param view     The <code>View</code> that the keyboard is to be hidden from.
     */
    public static void forceHideKeyboard(Activity activity, View view) {
        if (view == null || activity == null)
            return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
