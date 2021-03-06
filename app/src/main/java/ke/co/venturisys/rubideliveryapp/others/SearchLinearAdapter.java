package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.URL;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;

public class SearchLinearAdapter extends RecyclerViewAdapter {

    private Activity activity;
    private List<Meal> meals;

    public SearchLinearAdapter(Activity activity, List<Meal> meals) {
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
                .inflate(R.layout.search_page_card, parent, false);

        return new Holder(view);
    }

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
        myHolder.mealQuantity.setText(activity.getString(R.string.amount_left).concat(" " + meal.getQuantity()));
        HashMap<String, Object> src = new HashMap<>();
        src.put(URL, meal.getIcon());
        loadPictureToImageView(src, R.drawable.ruby_small, myHolder.mealIcon,
                false, true, false, false);

        // detect change in number of meals typed and show change in price accordingly
        TextWatcher orderWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int amount = Integer.parseInt(myHolder.mealNumber.getText().toString()),
                            quantity = Integer.parseInt(meal.getQuantity());
                    if (amount > 0) { // ensure meal is greater than 0 before adjusting amount and price
                        myHolder.mealPrice.setText(activity.getString(R.string.kenyan_currency)
                                .concat(" ".concat("" + String.format(Locale.US, "%1$.2f",
                                        Double.parseDouble(meal.getPrice()) * amount))));
                    } else if (Integer.parseInt(s.toString()) > quantity) { // ensure meal is less than quantity available
                        // before adjusting amount and price
                        myHolder.mealNumber.setText(quantity);
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
                    int amount = Integer.parseInt(myHolder.mealNumber.getText().toString()),
                            quantity = Integer.parseInt(meal.getQuantity());
                    if (amount < quantity) { // ensure meal is less than quantity available before adjusting amount and price
                        myHolder.mealNumber.setText(String.valueOf(
                                amount + 1));
                        myHolder.mealPrice.setText(activity.getString(R.string.kenyan_currency)
                                .concat(" ".concat("" + String.format(Locale.US, "%1$.2f",
                                        Double.parseDouble(meal.getPrice()) * (amount + 1)))));
                    }
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

        myHolder.mealAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add meal to cart
                Toast.makeText(activity, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public class Holder extends RecycleViewHolder {

        ImageView mealIcon;
        TextView mealTitle, mealDetails, mealPrice, mealQuantity;
        ImageButton mealAddBtn, mealMinusBtn;
        EditText mealNumber;
        Button mealAddCart;

        Holder(View itemView) {
            super(itemView);
            mealIcon = itemView.findViewById(R.id.food_icon);
            mealTitle = itemView.findViewById(R.id.food_title);
            mealPrice = itemView.findViewById(R.id.food_price);
            mealDetails = itemView.findViewById(R.id.food_details);
            mealQuantity = itemView.findViewById(R.id.food_quantity);
            mealAddBtn = itemView.findViewById(R.id.meal_add_btn);
            mealMinusBtn = itemView.findViewById(R.id.meal_minus_btn);
            mealNumber = itemView.findViewById(R.id.meal_edit_amount);
            mealAddCart = itemView.findViewById(R.id.add_to_cart_btn);
        }
    }
}
