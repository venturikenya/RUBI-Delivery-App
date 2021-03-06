package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import ke.co.venturisys.rubideliveryapp.database.helpers.CartBaseHelper;
import ke.co.venturisys.rubideliveryapp.database.schemas.CartDbSchema;

import static ke.co.venturisys.rubideliveryapp.others.Extras.updateMeal;

/**
 * Created by victor on 3/22/18.
 * This class handles arrangement of views in a linear layout for cart page
 */

public class CartLinearAdapter extends RecyclerViewAdapter {

    private Activity activity;
    private List<Meal> meals;

    public CartLinearAdapter(Activity activity, List<Meal> meals) {
        this.activity = activity;
        this.meals = meals;
    }

    public int getMeals() {
        return meals.size();
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
                .concat(" ".concat("" + String.format(Locale.US, "%1$.2f",
                        Double.parseDouble(meal.getPrice())))));
        myHolder.mealDetails.setText(meal.getDetails());
        myHolder.mealNumber.setText(meal.getAmount());
        myHolder.mealTitle.setText(meal.getTitle());

        // detect change in number of meals typed and show change in price accordingly
        TextWatcher orderWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int amount = Integer.parseInt(myHolder.mealNumber.getText().toString());
                    if (amount > 0) { // ensure meal is greater than 0 before adjusting amount and price
                        myHolder.mealPrice.setText(activity.getString(R.string.kenyan_currency)
                                .concat(" ".concat("" + String.format(Locale.US, "%1$.2f",
                                        Double.parseDouble(meal.getPrice()) * amount))));
                    }
                } catch (Exception ex) {
                    Log.e("ERROR", "An error occurred");
                    ex.printStackTrace();
                    myHolder.mealPrice.setText(activity.getString(R.string.kenyan_currency)
                            .concat(" ".concat("" + String.format(Locale.US, "%1$.2f",
                                    0.00))));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        myHolder.mealNumber.addTextChangedListener(orderWatcher);

        // set listeners for buttons
        myHolder.mealAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // increment meal amount and price
                    int amount = Integer.parseInt(myHolder.mealNumber.getText().toString());
                    myHolder.mealNumber.setText(String.valueOf(
                            amount + 1));
                    myHolder.mealPrice.setText(activity.getString(R.string.kenyan_currency)
                            .concat(" ".concat("" + String.format(Locale.US, "%1$.2f",
                                    Double.parseDouble(meal.getPrice()) * (amount + 1)))));
                } catch (Exception ex) {
                    Log.e("ERROR", "An error occurred");
                    ex.printStackTrace();
                    Toast.makeText(activity, "Please enter a number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myHolder.mealMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // decrement meal amount and price
                    int amount = Integer.parseInt(myHolder.mealNumber.getText().toString());
                    if (amount > 0) { // ensure meal is greater than 0 before adjusting amount and price
                        myHolder.mealNumber.setText(String.valueOf(
                                amount - 1));
                        myHolder.mealPrice.setText(activity.getString(R.string.kenyan_currency)
                                .concat(" ".concat("" + String.format(Locale.US, "%1$.2f",
                                        Double.parseDouble(meal.getPrice()) * (amount - 1)))));
                    }
                } catch (Exception ex) {
                    Log.e("ERROR", "An error occurred");
                    ex.printStackTrace();
                    Toast.makeText(activity, "Please enter a number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myHolder.mealDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove meal at given position
                deleteMeal(meal, activity);
                removeAt(myHolder.getAdapterPosition());
            }
        });

        myHolder.mealEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save changes to amount of meal
                String amount = myHolder.mealNumber.getText().toString().trim(),
                        price = myHolder.mealPrice.getText().toString().trim();
                SQLiteDatabase mDatabase = new CartBaseHelper(activity).getWritableDatabase();
                updateMeal(meal, amount, price, mDatabase);
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

    private void deleteMeal(Meal meal, Context context) {
        SQLiteDatabase db = new CartBaseHelper(context).getWritableDatabase();
        db.delete(CartDbSchema.CartTable.NAME, CartDbSchema.CartTable.Cols.TITLE + " = ?",
                new String[]{String.valueOf(meal.getTitle())});
        db.close();
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
