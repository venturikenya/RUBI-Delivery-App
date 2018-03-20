package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ke.co.venturisys.rubideliveryapp.R;

/**
 * Created by victor on 3/20/18.
 * This class handles arrangement of views in a linear layout for notifications page
 */

public class NotificationsLinearAdapter extends RecyclerViewAdapter {

    private Activity activity;
    private List<Notification> notifications;

    public NotificationsLinearAdapter(Activity activity, List<Notification> notifications) {
        this.activity = activity;
        this.notifications = notifications;
    }

    /*
     * Create a holder with inflated layout as view
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.notifications_page_card, parent, false);

        return new Holder(view);
    }

    /*
     * Bind holder to recycler view and pass data to the widgets inflated in holder
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Holder myHolder = (Holder) holder;
        final Notification notification = notifications.get(position);

        myHolder.notificationTitle.setText(notification.getTitle());
        myHolder.notificationTime.setText(notification.getTime());
        myHolder.notificationDetails.setText(notification.getDetails());
    }

    /*
     * Return number of items in list so as to prepare recycler view creation
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /*
     * Inflate widgets in layout passed
     */
    public class Holder extends RecycleViewHolder {

        TextView notificationTitle, notificationTime, notificationDetails;

        Holder(View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notification_card_title);
            notificationTime = itemView.findViewById(R.id.notification_card_time);
            notificationDetails = itemView.findViewById(R.id.notification_card_details);
        }
    }
}
