package ke.co.venturisys.rubideliveryapp.others;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.database.helpers.CartBaseHelper;
import ke.co.venturisys.rubideliveryapp.database.schemas.CartDbSchema;
import ke.co.venturisys.rubideliveryapp.database.wrappers.CartCursorWrapper;

import static ke.co.venturisys.rubideliveryapp.others.Extras.addMeal;
import static ke.co.venturisys.rubideliveryapp.others.Extras.updateMeal;

/**
 * Created by victor on 3/25/18.
 * This class handles arrangement of views in a linear layout for order page
 */

public class OrderLinearAdapter extends RecyclerViewAdapter {

    private Activity activity;
    private List<Meal> meals;
    private SQLiteDatabase mDatabase;

    public OrderLinearAdapter(Activity activity, List<Meal> meals) {
        this.activity = activity;
        this.meals = meals;
        // this serves to save meal into and retrieve from SQLite db
        this.mDatabase = new CartBaseHelper(activity).getWritableDatabase();
    }

    /*
     * Create a holder with inflated layout as view
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.order_page_card, parent, false);

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

        myHolder.mealAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = myHolder.mealNumber.getText().toString().trim(),
                        price = myHolder.mealPrice.getText().toString().trim(),
                        title = myHolder.mealTitle.getText().toString().trim();
                List<String> titles = getTitles();

                // add meal if meal not added to cart, else update
                if (titles.contains(title)) addMeal(meal, amount, price, mDatabase);
                else updateMeal(meal, amount, price, mDatabase);
                Toast.makeText(activity, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Gets list of meal titles for auto completion from database
     *
     * @return list of titles
     */
    private ArrayList<String> getTitles() {
        // create array list to hold saved registration numbers
        ArrayList<String> titles = new ArrayList<>();
        // create wrapper for cursor
        CartCursorWrapper cursor = queryTitle();

        // get received emails and add to array list
        //noinspection TryFinallyCanBeTryWithResources
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                titles.add(cursor.getTitle());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return titles;
    }

    /**
     * Creates the query for retrieving registration numbers
     *
     * @return instance of cursor wrapper
     */
    private CartCursorWrapper queryTitle() {
        @SuppressLint("Recycle") Cursor cursor = mDatabase.query(
                CartDbSchema.CartTable.NAME, // Table
                new String[]{CartDbSchema.CartTable.Cols.TITLE}, // Columns - null selects all columns
                null, // WHERE
                null, // Conditions to be met
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CartCursorWrapper(cursor);
    }

    /*
     * Return number of items in list so as to prepare recycler view creation
     */
    @Override
    public int getItemCount() {
        return meals.size();
    }

    /*
     * Inflate widgets in layout passed
     */
    public class Holder extends RecycleViewHolder {

        TextView mealTitle, mealDetails, mealPrice;
        ImageButton mealAddBtn, mealMinusBtn;
        EditText mealNumber;
        Button mealAddCart;

        Holder(View itemView) {
            super(itemView);
            mealTitle = itemView.findViewById(R.id.food_title);
            mealPrice = itemView.findViewById(R.id.food_price);
            mealDetails = itemView.findViewById(R.id.food_details);
            mealAddBtn = itemView.findViewById(R.id.meal_add_btn);
            mealMinusBtn = itemView.findViewById(R.id.meal_minus_btn);
            mealNumber = itemView.findViewById(R.id.meal_edit_amount);
            mealAddCart = itemView.findViewById(R.id.add_to_cart_btn);
        }
    }
}
