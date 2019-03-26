package com.example.fieldnotes.database;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

/**
 * Factory that creates <code>ViewModel</code> objects using a parameter (in the case of this app,
 * a unix time with which we fetch <code>Stop</code> objects.
 *
 * @author Tyler Seidel (2019)
 */
public class FieldNotesViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private long unixTime;

    public FieldNotesViewModelFactory(Application application, long param) {
        mApplication = application;
        unixTime = param;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new FieldNotesViewModel(mApplication, unixTime);
    }
}
