package ke.co.venturisys.rubideliveryapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.others.Permissions.InternetConnectionDialogFragment;

import static ke.co.venturisys.rubideliveryapp.others.Constants.INTERNET_PICKER;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_CART;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.inflateCartMenu;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneralFragment extends Fragment {

    // variable to inflate respective layouts
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /*
     * This method creates a snack bar in fragments that require internet
     * so that providing an interface for the user to enable internet connection
     */
    void requestInternetAccess(CoordinatorLayout coordinatorLayout) {
        assert getContext() != null;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final View view = inflateCartMenu(inflater, menu, getActivity());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                changeFragment(CartFragment.newInstance(), new Handler(), TAG_CART,
                        (AppCompatActivity) getActivity());
            }
        });
    }
}
