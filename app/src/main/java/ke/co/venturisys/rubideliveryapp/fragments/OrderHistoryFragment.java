package ke.co.venturisys.rubideliveryapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ke.co.venturisys.rubideliveryapp.R;

public class OrderHistoryFragment extends GeneralFragment {

    public OrderHistoryFragment() {}

    public static OrderHistoryFragment newInstance() {

        Bundle args = new Bundle();

        OrderHistoryFragment fragment = new OrderHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_history, container, false);

        return view;
    }
}
