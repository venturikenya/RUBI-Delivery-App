package ke.co.venturisys.rubideliveryapp.others;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

/*
 * RecyclerView is a container for displaying large data sets
 * that can be scrolled very efficiently by maintaining a limited number of views.
 * Is a more advanced and flexible version of ListView.
 * It supports horizontal and vertical scrolling,
 * three types of lists using RecyclerView.LayoutManager
 * 1. StaggeredGridLayoutManager
 * 2. GridLayoutManager
 * 3. LinearLayoutManager
 * and customized divider (spacing) between two elements using RecyclerView.ItemDecoration class.
 */

/**
 * Created by victor mungai on 10/11/17.
 * Inherits from RecyclerView Adapter
 * It is responsible for creating the necessary ViewHolder
 * and binding ViewHolder to data from the model layer
 * It also implements item click or item long click event
 * Courtesy of the http://www.theappguruz.com/blog/learn-recyclerview-with-an-example-in-android
 */

public abstract class RecyclerViewAdapter extends RecyclerView.Adapter {

    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;

    // implement item click listener
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(RecycleViewHolder itemHolder) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    // implement item long click listener
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    private void onItemHolderLongClick(RecycleViewHolder itemHolder) {
        if (onItemLongClickListener != null) {
            onItemLongClickListener.onItemLongClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    /**
     * Holder class to wire up the views in specified layout files
     * Its responsibility is to hold on to a given View
     */
    class RecycleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        RecycleViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemHolderClick(this);
        }

        @Override
        public boolean onLongClick(View v) {
            onItemHolderLongClick(this);
            return true;
        }

    }
}
