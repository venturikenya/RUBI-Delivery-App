package ke.co.venturisys.rubideliveryapp.fragments;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.others.LandingPageFood;
import ke.co.venturisys.rubideliveryapp.others.LandingPageGridAdapter;

import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setTextViewDrawableColor;

public class HomeFragment extends GeneralFragment {

    GridLayoutManager layoutManager; // lays out children in a grid format
    RecyclerView recyclerView;
    LandingPageGridAdapter adapter; // adapter to communicate with recycler view
    ImageView landingBg, searchBtn;
    Button offerBtn;
    TextView recyclerTitle;
    TextInputLayout inputLayoutSearch;
    EditText inputSearch;
    List<LandingPageFood> foods;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // inflate layout
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initCollapsingToolbar();
        foods = new ArrayList<>();

        // set up recycler view
        recyclerView = view.findViewById(R.id.landing_card_recycler_view);
        adapter = new LandingPageGridAdapter(getActivity(), foods);
        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // initialise widgets
        landingBg = view.findViewById(R.id.backdrop);
        searchBtn = view.findViewById(R.id.search_image_view);
        offerBtn = view.findViewById(R.id.offerBtn);
        recyclerTitle = view.findViewById(R.id.landing_card_recycler_title);
        // set drawable to text view
        //recyclerTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_overflow_dots, 0);
        inputLayoutSearch = view.findViewById(R.id.input_layout_search);
        inputSearch = view.findViewById(R.id.input_search);

        // set colors of drawables of text view and edit text
        setTextViewDrawableColor(recyclerTitle, R.color.colorApp, getActivity());
        if (getActivity() != null) {
            searchBtn.getDrawable().setColorFilter(
                    new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.colorApp),
                            PorterDuff.Mode.SRC_IN));
        }

        // set image(s) of today's offers to background
        HashMap<String, Object> src = new HashMap<>();
        src.put(RES_ID, R.drawable.ic_beef_specials);
        loadPictureToImageView(src, R.drawable.bg_circle, landingBg, false, false, false);

        prepareFoodItems();

        return view;
    }

    private void prepareFoodItems() {
        int[] icons = new int[]{
                R.drawable.ic_pastries,
        };

        LandingPageFood food = new LandingPageFood("Pastries", icons[0]);
        foods.add(food);

        food = new LandingPageFood("Pastries", icons[0]);
        foods.add(food);

        food = new LandingPageFood("Pastries", icons[0]);
        foods.add(food);

        food = new LandingPageFood("Pastries", icons[0]);
        foods.add(food);

        food = new LandingPageFood("Pastries", icons[0]);
        foods.add(food);

        food = new LandingPageFood("Pastries", icons[0]);
        foods.add(food);

        food = new LandingPageFood("Pastries", icons[0]);
        foods.add(food);

        food = new LandingPageFood("Pastries", icons[0]);
        foods.add(food);

        food = new LandingPageFood("Pastries", icons[0]);
        foods.add(food);

        food = new LandingPageFood("Pastries", icons[0]);
        foods.add(food);

        adapter.notifyDataSetChanged();
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.app_name));
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
}
