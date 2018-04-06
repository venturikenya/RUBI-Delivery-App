package ke.co.venturisys.rubideliveryapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.SearchActivity;
import ke.co.venturisys.rubideliveryapp.others.Meal;
import ke.co.venturisys.rubideliveryapp.others.SearchLinearAdapter;

import static android.app.Activity.RESULT_OK;
import static ke.co.venturisys.rubideliveryapp.others.Constants.ARG_SEARCH_QUERY;
import static ke.co.venturisys.rubideliveryapp.others.Constants.LIST_STATE_KEY;
import static ke.co.venturisys.rubideliveryapp.others.Constants.REQUEST_SPEECH;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_CART;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.getSpeechInput;
import static ke.co.venturisys.rubideliveryapp.others.Extras.requestInternetAccess;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setImageViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.Extras.validateSearch;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    LinearLayoutManager layoutManager; // lays out children in a linear format
    RecyclerView recyclerView;
    SearchLinearAdapter adapter; // adapter to communicate with recycler view

    View view;
    CoordinatorLayout mainContent;
    TextInputLayout inputLayoutSearch;
    EditText inputSearch;
    ImageView searchBtn, speechBtn;
    TextView tvFabCart;
    List<Meal> meals;
    Parcelable mListState; // used to save state of recycler view across rotation

    public SearchFragment() {
    }

    public static SearchFragment newInstance(String query) {

        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_QUERY, query);

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout here
        view = inflater.inflate(R.layout.fragment_search, container, false);
        meals = new ArrayList<>();
        assert getActivity() != null;

        // set up recycler view
        recyclerView = view.findViewById(R.id.search_card_recycler_view);
        adapter = new SearchLinearAdapter(getActivity(), meals);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // initialise widgets
        mainContent = view.findViewById(R.id.search_parent_layout);
        inputLayoutSearch = view.findViewById(R.id.input_layout_search);
        inputLayoutSearch.setHintAnimationEnabled(false);
        inputSearch = view.findViewById(R.id.input_search);
        searchBtn = view.findViewById(R.id.search_image_view);
        speechBtn = view.findViewById(R.id.microphone_image_view);
        tvFabCart = view.findViewById(R.id.fabCartTextView);

        if (getArguments() != null) inputSearch.setText(getArguments().getString(ARG_SEARCH_QUERY));

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
                    getSpeechInput(getActivity(), SearchFragment.this);
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
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
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

        getMeals();

        return view;
    }

    private void getMeals() {
        Meal meal = new Meal(R.drawable.ic_pastries, "Chicken Tikka Masala", "Served with homemade naan bread",
                "1", "450");
        meals.add(meal);

        meal = new Meal(R.drawable.ic_pastries, "Mango juice", "Freshly blended and served cold",
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
