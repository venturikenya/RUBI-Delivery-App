package ke.co.venturisys.rubideliveryapp.fragments;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.others.Meal;
import ke.co.venturisys.rubideliveryapp.others.OrderLinearAdapter;

import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_ICON;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_TITLE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.LIST_STATE_KEY;
import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_CART;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;

/**
 * Transition: https://medium.com/@andkulikov/animate-all-the-things-transitions-in-android-914af5477d50
 */
public class OrderFragment extends GeneralFragment {

    LinearLayoutManager layoutManager; // lays out children in a linear format
    RecyclerView recyclerView;
    OrderLinearAdapter adapter; // adapter to communicate with recycler view

    ImageView backdrop, searchBtn;
    TextView backdropTitle, tvFabCart;
    FloatingActionButton fabCartButton;
    ViewGroup transitionsContainer;
    TextInputLayout inputLayoutSearch;
    EditText inputSearch;
    List<Meal> meals;

    String backdrop_title;
    int backdrop_icon;
    Parcelable mListState; // used to save state of recycler view across rotation
    boolean visible = true; // determine visibility of fab text view
    int rotationAngle;

    public OrderFragment() {
    }

    public static OrderFragment newInstance(int icon, String title) {

        // receive arguments and attach them to fragment on creation for future use
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_ICON, icon);

        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // retrieve fragment arguments sent from home page via order activity
        if (getArguments() != null) {
            backdrop_title = getArguments().getString(ARG_TITLE);
            backdrop_icon = getArguments().getInt(ARG_ICON);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // inflate layout
        view = inflater.inflate(R.layout.fragment_order, container, false);
        meals = new ArrayList<>();
        assert getActivity() != null;

        // set up recycler view
        recyclerView = view.findViewById(R.id.order_card_recycler_view);
        adapter = new OrderLinearAdapter(getActivity(), meals);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // initialise widgets
        backdrop = view.findViewById(R.id.order_backdrop);
        backdropTitle = view.findViewById(R.id.backdrop_name_title_view);
        transitionsContainer = view.findViewById(R.id.transitions_container);
        transitionsContainer.setClipChildren(false);
        transitionsContainer.setClipToPadding(false);
        fabCartButton = view.findViewById(R.id.fabCartBtn);
        tvFabCart = view.findViewById(R.id.fabCartTextView);
        tvFabCart.setVisibility(View.INVISIBLE);
        inputLayoutSearch = view.findViewById(R.id.order_input_layout_search);
        inputLayoutSearch.setHintAnimationEnabled(false);
        inputSearch = view.findViewById(R.id.order_input_search);
        searchBtn = view.findViewById(R.id.order_search_image_view);

        // let floating fab be visible from Android Marsh mellow
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
            transitionsContainer.setVisibility(View.GONE);

        // set color to search button
        if (getActivity() != null) {
            searchBtn.getDrawable().setColorFilter(
                    new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.colorApp),
                            PorterDuff.Mode.SRC_IN));
        }

        // on clicking search button
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateSearch()) {
                    Toast.makeText(getActivity(), "Searching", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* To set custom size arrow to fab */
        // get the drawable
        Drawable fabSrc = getResources().getDrawable(R.drawable.ic_chevron_left);
        // copy it in a new one
        assert fabSrc.getConstantState() != null;
        Drawable willBeWhite = fabSrc.getConstantState().newDrawable();
        // set the color filter
        willBeWhite.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        // set it to your fab button initialized before
        fabCartButton.setImageDrawable(willBeWhite);

        // carry out some animations
        fabCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                // rotate arrow icon by 180 degrees
                ObjectAnimator anim = ObjectAnimator.ofFloat(v, "rotation",
                        rotationAngle, rotationAngle + 180);
                anim.setDuration(500);
                anim.start();
                rotationAngle += 180;
                rotationAngle = rotationAngle % 360;
                // set visibility of fab text view
                visible = !visible;
                tvFabCart.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        // go to cart page
        tvFabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(CartFragment.newInstance(),
                        new Handler(), TAG_CART, (AppCompatActivity) getActivity());
            }
        });

        // load backdrop details
        backdropTitle.setText(backdrop_title);
        HashMap<String, Object> src = new HashMap<>();
        src.put(RES_ID, backdrop_icon);
        loadPictureToImageView(src, R.drawable.bg_circle, backdrop, false, false,
                false, false);

        getMeals();

        return view;
    }

    // checks if user has entered search query when (s)he has pressed search button
    // and raises error if not
    private boolean validateSearch() {

        if (inputSearch.getText().toString().trim().isEmpty()) {
            inputLayoutSearch.setError(getString(R.string.err_msg_search));
            return false;
        } else {
            inputLayoutSearch.setErrorEnabled(false);
        }

        return true;

    }

    private void getMeals() {
        Meal meal = new Meal("Chicken Tikka Masala", "Served with homemade naan bread",
                "1", "450");
        meals.add(meal);

        meal = new Meal("Mango juice", "Freshly blended and served cold",
                "1", "600");
        meals.add(meal);

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
        assert getActivity() != null;

        // retrieve contents on screen before rotation
        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }

    }
}
