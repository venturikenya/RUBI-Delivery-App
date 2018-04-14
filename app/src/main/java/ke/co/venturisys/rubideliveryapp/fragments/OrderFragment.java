package ke.co.venturisys.rubideliveryapp.fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import ke.co.venturisys.rubideliveryapp.AllMealsQuery;
import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.SearchActivity;
import ke.co.venturisys.rubideliveryapp.others.Meal;
import ke.co.venturisys.rubideliveryapp.others.MyApolloClient;
import ke.co.venturisys.rubideliveryapp.others.OrderLinearAdapter;

import static android.app.Activity.RESULT_OK;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_ICON;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_TITLE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ERROR;
import static ke.co.venturisys.rubideliveryapp.others.Constants.LIST_STATE_KEY;
import static ke.co.venturisys.rubideliveryapp.others.Constants.REQUEST_SPEECH;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_CART;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URL;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.getSpeechInput;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.rubideliveryapp.others.Extras.requestInternetAccess;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setImageViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.Extras.validateSearch;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

/**
 * Transition: https://medium.com/@andkulikov/animate-all-the-things-transitions-in-android-914af5477d50
 */
public class OrderFragment extends GeneralFragment {

    LinearLayoutManager layoutManager; // lays out children in a linear format
    RecyclerView recyclerView;
    OrderLinearAdapter adapter; // adapter to communicate with recycler view

    CoordinatorLayout mainContent;
    ImageView backdrop, searchBtn, speechBtn;
    TextView backdropTitle, tvFabCart;
    FloatingActionButton fabCartButton;
    ViewGroup transitionsContainer;
    TextInputLayout inputLayoutSearch;
    EditText inputSearch;
    ProgressBar progressBar; // shown while meals are being loaded
    List<Meal> meals;

    String backdrop_title;
    String backdrop_icon;
    Parcelable mListState; // used to save state of recycler view across rotation
    boolean visible = true; // determine visibility of fab text view
    int rotationAngle;

    public OrderFragment() {
    }

    public static OrderFragment newInstance(String icon, String title) {

        // receive arguments and attach them to fragment on creation for future use
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_ICON, icon);

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
            backdrop_icon = getArguments().getString(ARG_ICON);
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
        mainContent = view.findViewById(R.id.order_coordinator_layout);
        backdrop = view.findViewById(R.id.order_backdrop);
        backdropTitle = view.findViewById(R.id.backdrop_name_title_view);
        transitionsContainer = view.findViewById(R.id.transitions_container);
        transitionsContainer.setClipChildren(false);
        transitionsContainer.setClipToPadding(false);
        fabCartButton = view.findViewById(R.id.fabCartBtn);
        tvFabCart = view.findViewById(R.id.fabCartTextView);
        tvFabCart.setVisibility(View.INVISIBLE);
        inputLayoutSearch = view.findViewById(R.id.input_layout_search);
        inputLayoutSearch.setHintAnimationEnabled(false);
        inputSearch = view.findViewById(R.id.input_search);
        searchBtn = view.findViewById(R.id.search_image_view);
        speechBtn = view.findViewById(R.id.microphone_image_view);
        progressBar = view.findViewById(R.id.progressBar);
        setImageViewDrawableColor(progressBar.getIndeterminateDrawable(), getResources()
                .getColor(R.color.colorProgressBar));

        // let floating fab be visible from Android Marsh mellow
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
            transitionsContainer.setVisibility(View.GONE);

        // set color to search and microphone button
        if (getActivity() != null) {
            setImageViewDrawableColor(searchBtn.getDrawable(), ContextCompat.getColor(getActivity(), R.color.colorApp));
            setImageViewDrawableColor(speechBtn.getDrawable(), ContextCompat.getColor(getActivity(), R.color.colorApp));
        }

        // when user clicks on mic
        speechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(getActivity()))
                    getSpeechInput(getActivity(), OrderFragment.this);
                else requestInternetAccess(mainContent, getActivity());
            }
        });

        // on clicking search button
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateSearch(inputSearch, inputLayoutSearch, getActivity())) {
                    Toast.makeText(getActivity(), "Searching", Toast.LENGTH_SHORT).show();
                    Intent intent = SearchActivity.newIntent(getActivity(), inputSearch.getText().toString().trim());
                    startActivity(intent);
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
        src.put(URL, backdrop_icon);
        loadPictureToImageView(src, R.drawable.bg_circle, backdrop, false, false,
                false, false);

        getMeals();

        return view;
    }

    /*
     * This method queries all the meals stored in the GraphQL server using Apollo Client
     * and passes it into the list that will be passed to recycler view
     */
    private void getMeals() {
        assert getActivity() != null;
        MyApolloClient.getMyApolloClient().query(
                AllMealsQuery.builder().build()
        ).enqueue(new ApolloCall.Callback<AllMealsQuery.Data>() {
            @Override
            // successful query
            public void onResponse(@Nonnull Response<AllMealsQuery.Data> response) {

                // should log the first meal's title
                Log.d(TAG, "onResponse: " + Objects.requireNonNull(response.data()).allMeals().get(0).name());
                final List<AllMealsQuery.AllMeal> allMeals = Objects.requireNonNull(response.data()).allMeals();

                // run changes on UI thread to show changes
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            for (AllMealsQuery.AllMeal allMeal : allMeals) {
                                if (allMeal.category().equals(backdrop_title)) {
                                    meals.add(new Meal(allMeal.icon(),
                                            allMeal.name(),
                                            allMeal.description(),
                                            "" + allMeal.amount(),
                                            allMeal.price(),
                                            allMeal.category()));
                                }
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                // Log error so as to fix it
                Log.e(ERROR, "onFailure: Something went wrong. " + e.getMessage());
            }
        });

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // receive data from speech input app
            case REQUEST_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    // extract data returned from speech input app
                    // and assign to array list
                    ArrayList<String> result = data.getStringArrayListExtra
                            (RecognizerIntent.EXTRA_RESULTS);
                    // set text received to search edit text field
                    inputSearch.setText(result.get(0));
                }

                break;
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
