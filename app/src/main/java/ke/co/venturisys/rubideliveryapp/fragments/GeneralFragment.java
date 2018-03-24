package ke.co.venturisys.rubideliveryapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.others.OnFragmentInteractionListener;
import ke.co.venturisys.rubideliveryapp.others.Permissions.InternetConnectionDialogFragment;

import static ke.co.venturisys.rubideliveryapp.others.Constants.INTERNET_PICKER;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class GeneralFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // variable to inflate respective layouts
    View view;
    OnFragmentInteractionListener mListener;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
     * This method creates a snack bar in fragments that require internet
     * so that providing an interface for the user to enable internet connection
     */
    void requestInternetAccess(CoordinatorLayout coordinatorLayout){
        if (!isNetworkAvailable(getContext())) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, getString(R.string.internet_access_request), Snackbar.LENGTH_LONG)
                    .setAction("ENABLE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentManager fm = getFragmentManager();
                            InternetConnectionDialogFragment dialog = new InternetConnectionDialogFragment();
                            assert fm != null;
                            dialog.show(fm, INTERNET_PICKER);
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(getResources().getColor(R.color.colorSnackbarActionText));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
