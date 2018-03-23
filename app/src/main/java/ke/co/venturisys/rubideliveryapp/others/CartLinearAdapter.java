package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import ke.co.venturisys.rubideliveryapp.R;

/**
 * Created by victor on 3/22/18.
 * This class handles arrangement of views in a linear layout for cart page
 */

public class CartLinearAdapter extends RecyclerViewAdapter {

    private Activity activity;
    private List<Meal> meals;
    public boolean deleted = false;

    public CartLinearAdapter(Activity activity, List<Meal> meals) {
        this.activity = activity;
        this.meals = meals;
    }

    /*
     * Create a holder with inflated layout as view
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.cart_page_card, parent, false);

        return new Holder(view);
    }

    /*
     * Bind holder to recycler view and pass data to the widgets inflated in holder
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Holder myHolder = (Holder) holder;
        final Meal meal = meals.get(position);

        // set values of widgets
        myHolder.mealPrice.setText(activity.getString(R.string.kenyan_currency)
                .concat(" ".concat(""+String.format(Locale.US, "%1$.2f",
                        Double.parseDouble(meal.getPrice())))));
        myHolder.mealDetails.setText(meal.getDetails());
        myHolder.mealNumber.setText(meal.getAmount());
        myHolder.mealTitle.setText(meal.getTitle());

        // set listeners for buttons
        myHolder.mealAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // increment meal amount and price
                int amount = Integer.parseInt(myHolder.mealNumber.getText().toString());
                myHolder.mealNumber.setText(String.valueOf(
                        amount + 1));
                myHolder.mealPrice.setText(activity.getString(R.string.kenyan_currency)
                        .concat(" ".concat(""+String.format(Locale.US, "%1$.2f",
                                Double.parseDouble(meal.getPrice()) * (amount + 1)))));
            }
        });

        myHolder.mealMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // decrement meal amount and price
                int amount = Integer.parseInt(myHolder.mealNumber.getText().toString());
                myHolder.mealNumber.setText(String.valueOf(
                        amount - 1));
                myHolder.mealPrice.setText(activity.getString(R.string.kenyan_currency)
                        .concat(" ".concat(""+String.format(Locale.US, "%1$.2f",
                                Double.parseDouble(meal.getPrice()) * (amount - 1)))));
            }
        });

        myHolder.mealDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove meal at given position
                removeAt(myHolder.getAdapterPosition());
                deleted = true;
            }
        });

        myHolder.mealEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save changes to amount of meal
                Toast.makeText(activity, "Changes saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * Return number of items in list so as to prepare recycler view creation
     */
    @Override
    public int getItemCount() {
        return meals.size();
    }

    private void removeAt(int position) {
        meals.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, meals.size());
        notifyDataSetChanged();
    }

    /*
     * Inflate widgets in layout passed
     */
    public class Holder extends RecycleViewHolder {

        LinearLayout linearLayoutOrderAmount;
        TextView mealTitle, mealDetails, mealPrice;
        EditText mealNumber;
        Button mealEditBtn, mealDeleteBtn;
        ImageButton mealAddBtn, mealMinusBtn;

        Holder(View itemView) {
            super(itemView);
            linearLayoutOrderAmount = itemView.findViewById(R.id.order_meal_amount_linear_layout);
            mealTitle = itemView.findViewById(R.id.order_meal_name);
            mealDetails = itemView.findViewById(R.id.order_meal_details);
            mealNumber = itemView.findViewById(R.id.order_meal_edit_amount);
            mealPrice = itemView.findViewById(R.id.order_meal_price);
            mealEditBtn = itemView.findViewById(R.id.order_meal_edit_btn);
            mealDeleteBtn = itemView.findViewById(R.id.order_meal_delete_btn);
            mealAddBtn = itemView.findViewById(R.id.order_meal_add_btn);
            mealMinusBtn = itemView.findViewById(R.id.order_meal_minus_btn);
        }
    }
}
