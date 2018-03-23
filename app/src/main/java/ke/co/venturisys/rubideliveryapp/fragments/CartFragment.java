package ke.co.venturisys.rubideliveryapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.MainActivity;
import ke.co.venturisys.rubideliveryapp.others.CartLinearAdapter;
import ke.co.venturisys.rubideliveryapp.others.Meal;

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

    public CartFragment() {
    }

    public static CartFragment newInstance() {

        Bundle args = new Bundle();

        CartFragment fragment = new CartFragment();
        fragment.setArguments(args);
        return fragment;
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

        reviewCart();

        updateTextViews(meals.size());
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateTextViews(adapter.getMeals());
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

        // set total price of order to text view
        String price_prefix = getString(R.string.total_price).split(" ")[0]; // get currency of amount
        cartPrice.setText(price_prefix.concat(" ".concat("" + String.format(Locale.US, "%1$.2f", 1050.00))));

        // what if there are no orders
        if (number <= 0) {
            cartAmount.setText(getString(R.string.nothing_placeholder));
            cartPrice.setText(price_prefix.concat(" ".concat("0.00")));
            btnProceedCheckout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            btnProceedCheckout.setClickable(false);
        }
    }

    private void reviewCart() {
        Meal meal = new Meal("Chicken Tikka Masala", "Served with homemade naan bread",
                "1", "450");
        meals.add(meal);

        meal = new Meal("Mango juice", "Freshly blended and served cold",
                "3", "600");
        meals.add(meal);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }

    @Override
    public void onResume() {

        super.onResume();

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                // detect if back button is pressed
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    // redirect to landing page
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    assert getActivity() != null;
                    getActivity().finish();

                    return true;

                }

                return false;
            }
        });
    }
}
