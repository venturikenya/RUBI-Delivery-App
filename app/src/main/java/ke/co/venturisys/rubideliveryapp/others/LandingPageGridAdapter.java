package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;

/**
 * Created by victor on 3/19/18.
 * Handles the arrangement of views in a grid layout format for landing page
 * Courtesy of the http://www.theappguruz.com/blog/learn-recyclerview-with-an-example-in-android
 */

public class LandingPageGridAdapter extends RecyclerViewAdapter {

    private Activity activity;
    private List<LandingPageFood> foods;

    public LandingPageGridAdapter(Activity activity, List<LandingPageFood> foods) {
        this.activity = activity;
        this.foods = foods;
    }

    /*
         * Used to create new ViewHolder, along with its returned View to display.
         * Called until a sufficient number of ViewHolders have been created,
         * after which the old ViewHolders are recycled, saving space and time
         */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.landing_page_card, parent, false);
        return new Holder(view);
    }

    /*
     * Used to display the data at the specified position.
     * It updates the content of the itemView to reflect the item at the given position.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Holder myHolder = (Holder) holder;
        final LandingPageFood food = foods.get(position);

        myHolder.tvLandingPage.setText(food.getName());
        HashMap<String, Object> src = new HashMap<>();
        src.put(RES_ID, food.getIcon());
        loadPictureToImageView(src, R.mipmap.ic_box, myHolder.imLandingPage, true, true,
                true, false);
    }

    /*
     * Returns the number of objects in list for preparation of space for recycler view
     */
    @Override
    public int getItemCount() {
        return foods.size();
    }

    /*
     * Serves purpose of inflating widgets in passed view for binding
     */
    public class Holder extends RecycleViewHolder {

        ImageView imLandingPage;
        TextView tvLandingPage;

        Holder(View itemView) {
            super(itemView);
            imLandingPage = itemView.findViewById(R.id.landing_page_card_view_image);
            tvLandingPage = itemView.findViewById(R.id.landing_page_card_view_text);
        }

        public TextView getTvLandingPage() {
            return tvLandingPage;
        }
    }
}
