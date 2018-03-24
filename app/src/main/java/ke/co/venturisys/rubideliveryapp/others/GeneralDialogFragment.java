package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by victor on 3/24/18.
 * This abstract class implements a general dialog fragment
 * and other dialog fragments will inherit from it
 */

public abstract class GeneralDialogFragment extends DialogFragment {

    public Activity activity;
    public int alertDialogLayout;
    ViewGroup viewGroup;
    AlertDialog alertDialog;
    int alertDialogTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewGroup = container;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = getActivity();
        View view = LayoutInflater.from(activity)
                .inflate(alertDialogLayout, viewGroup);

        initializeWidgets(view);

        alertDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(alertDialogTitle)
                .create();

        return alertDialog;
    }

    // exit current fragment and return to previous one
    public void exitFragment() {
        alertDialog.dismiss();
    }

    protected abstract void initializeWidgets(View view);

    public void setAlertDialogTitle(int alertDialogTitle) {
        this.alertDialogTitle = alertDialogTitle;
    }
}
