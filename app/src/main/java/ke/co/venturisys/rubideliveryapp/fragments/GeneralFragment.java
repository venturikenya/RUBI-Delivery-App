package ke.co.venturisys.rubideliveryapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_CART;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.inflateCartMenu;

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
