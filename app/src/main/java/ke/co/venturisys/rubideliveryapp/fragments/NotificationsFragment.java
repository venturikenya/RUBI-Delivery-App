package ke.co.venturisys.rubideliveryapp.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.others.Notification;
import ke.co.venturisys.rubideliveryapp.others.NotificationsLinearAdapter;

import static ke.co.venturisys.rubideliveryapp.others.Constants.LIST_STATE_KEY;

public class NotificationsFragment extends GeneralFragment {

    LinearLayoutManager layoutManager; // lays out children in a linear format
    RecyclerView recyclerView;
    NotificationsLinearAdapter adapter; // adapter to communicate with recycler view

    TextView notificationsTitle;
    List<Notification> notifications;
    Parcelable mListState; // used to save state of recycler view across rotation

    public NotificationsFragment() {
    }

    public static NotificationsFragment newInstance() {

        Bundle args = new Bundle();

        NotificationsFragment fragment = new NotificationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // inflate layout
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        notifications = new ArrayList<>();

        // set up recycler view
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new NotificationsLinearAdapter(getActivity(), notifications);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // initialise widgets
        notificationsTitle = view.findViewById(R.id.notifications_page_title);

        receiveNotifications();

        return view;
    }

    private void receiveNotifications() {
        Notification notification = new Notification("15% off day", "10:34 AM", "Get great discounts today on your favourite dishes");
        notifications.add(notification);

        notification = new Notification("Valentine's Special", "2 weeks ago", "Buy one of our main course meals and get a free drink");
        notifications.add(notification);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // retrieve contents on screen before rotation
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // save recycler view state
        mListState = layoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, mListState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }
}
