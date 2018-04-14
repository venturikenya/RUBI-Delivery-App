package ke.co.venturisys.rubideliveryapp.fragments;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.MainActivity;
import ke.co.venturisys.rubideliveryapp.activities.OrderPagerActivity;
import ke.co.venturisys.rubideliveryapp.database.helpers.CartBaseHelper;
import ke.co.venturisys.rubideliveryapp.database.helpers.OrderBaseHelper;
import ke.co.venturisys.rubideliveryapp.database.schemas.CartDbSchema;
import ke.co.venturisys.rubideliveryapp.database.schemas.OrderDbSchema;
import ke.co.venturisys.rubideliveryapp.database.wrappers.CartCursorWrapper;
import ke.co.venturisys.rubideliveryapp.others.CartLinearAdapter;
import ke.co.venturisys.rubideliveryapp.others.Meal;

import static ke.co.venturisys.rubideliveryapp.others.Constants.LIST_STATE_KEY;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_CART;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_HOME;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.getCartCount;
import static ke.co.venturisys.rubideliveryapp.others.Extras.getContentValues;
import static ke.co.venturisys.rubideliveryapp.others.Extras.goToTargetFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.inflateCartMenu;
import static ke.co.venturisys.rubideliveryapp.others.PictureUtilities.getFileName;

public class CartFragment extends GeneralFragment {

    LinearLayoutManager layoutManager; // lays out children in a linear format
    RecyclerView recyclerView;
    CartLinearAdapter adapter; // adapter to communicate with recycler view

    List<Meal> meals;
    Handler mHandler;
    TextView cartAmount;
    TextView cartPrice;
    Button btnProceedCheckout;
    Timer timer;
    Parcelable mListState; // used to save state of recycler view across rotation
    SQLiteDatabase mDatabase;

    public CartFragment() {
    }

    public static CartFragment newInstance() {

        Bundle args = new Bundle();

        CartFragment fragment = new CartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        meals = new ArrayList<>();
        mHandler = new Handler();

        assert getActivity() != null;

        // set up recycler view
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new CartLinearAdapter(getActivity(), meals);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // initialise widgets
        cartAmount = view.findViewById(R.id.cart_page_number_tv);
        cartPrice = view.findViewById(R.id.cart_page_price_tv);
        btnProceedCheckout = view.findViewById(R.id.btnProceedToCheckout);
        // get writable database as we want to write data
        mDatabase = new CartBaseHelper(getActivity()).getWritableDatabase();
        final SQLiteDatabase db = new OrderBaseHelper(getActivity()).getWritableDatabase();

        btnProceedCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewCart();
                for (Meal meal : meals) {
                    ContentValues values = getContentValues(meal, getFileName("yyyy-MM-dd hh:mm:ss.SSSSS"));
                    db.insert(OrderDbSchema.OrderTable.NAME, null, values);
                }
                if (DatabaseUtils.queryNumEntries(db, OrderDbSchema.OrderTable.NAME) == getCartCount(getActivity())) {
                    boolean checkedOut = getActivity().deleteDatabase(new OrderBaseHelper(getActivity()).getDatabaseName());
                    if (checkedOut) {
                        Toast.makeText(getActivity(), "Cart cleared", Toast.LENGTH_SHORT).show();
                        exitToTargetActivity((AppCompatActivity) getActivity(), MainActivity.class);
                    }
                }
            }
        });

        reviewCart();

        updateTextViews(meals.size());
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded()) {
                            reviewCart();
                            updateTextViews(adapter.getMeals());
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 500);

        return view;
    }

    private void updateTextViews(int number) {
        // set no of meals ordered to text view
        cartAmount.setText("".concat(number + " " + getString(R.string.item)));
        if (number > 1) cartAmount.setText(cartAmount.getText().toString().concat("s"));

        Double total = 0.00;
        try {
            for (Meal meal : meals) {
                total += Double.parseDouble(meal.getPrice());
            }
        } catch (Exception ex) {
            Log.e("ERROR", "An error occurred");
            ex.printStackTrace();
            total = 0.00;
        }

        // set total price of order to text view
        String price_prefix = getString(R.string.total_price).split(" ")[0]; // get currency of amount
        cartPrice.setText(price_prefix.concat(" ".concat("" + String.format(Locale.US, "%1$.2f", total))));

        // what if there are no orders
        if (number <= 0) {
            cartAmount.setText(getString(R.string.nothing_placeholder));
            cartPrice.setText(price_prefix.concat(" ".concat("0.00")));
            btnProceedCheckout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            btnProceedCheckout.setClickable(false);
        }
    }

    /*
     * This method queries all the meals stored in the GraphQL server using Apollo Client
     * and passes it into the list that will be passed to recycler view
     */
    private void reviewCart() {
        meals = getTitles();

        adapter.notifyDataSetChanged();
    }

    /**
     * Gets list of meal titles for auto completion from database
     *
     * @return list of titles
     */
    private ArrayList<Meal> getTitles() {
        // create array list to hold saved registration numbers
        ArrayList<Meal> meals = new ArrayList<>();
        // create wrapper for cursor
        CartCursorWrapper cursor = queryMeal();

        // get received emails and add to array list
        //noinspection TryFinallyCanBeTryWithResources
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                meals.add(cursor.getMeal());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return meals;
    }

    /**
     * Creates the query for retrieving meals
     *
     * @return instance of cursor wrapper
     */
    private CartCursorWrapper queryMeal() {
        @SuppressLint("Recycle") Cursor cursor = mDatabase.query(
                CartDbSchema.CartTable.NAME, // Table
                null, // Columns - null selects all columns
                null, // WHERE
                null, // Conditions to be met
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CartCursorWrapper(cursor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
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
        assert getActivity() != null;

        // retrieve contents on screen before rotation
        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return goToTargetFragment((AppCompatActivity) getActivity(), keyCode, event,
                        TAG_CART, TAG_HOME);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!(getActivity() instanceof OrderPagerActivity)) {
            View view = inflateCartMenu(inflater, menu, getActivity());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Nothing more here", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
