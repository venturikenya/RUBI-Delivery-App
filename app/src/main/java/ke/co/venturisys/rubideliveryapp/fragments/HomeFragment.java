package ke.co.venturisys.rubideliveryapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.OrderPagerActivity;
import ke.co.venturisys.rubideliveryapp.activities.SearchActivity;
import ke.co.venturisys.rubideliveryapp.others.ImagesViewPagerAdapter;
import ke.co.venturisys.rubideliveryapp.others.LandingPageFood;
import ke.co.venturisys.rubideliveryapp.others.LandingPageGridAdapter;
import ke.co.venturisys.rubideliveryapp.others.OrderLab;
import me.relex.circleindicator.CircleIndicator;

import static android.app.Activity.RESULT_OK;
import static ke.co.venturisys.rubideliveryapp.others.Constants.LIST_STATE_KEY;
import static ke.co.venturisys.rubideliveryapp.others.Constants.REQUEST_SPEECH;
import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Extras.backgroundOnClick;
import static ke.co.venturisys.rubideliveryapp.others.Extras.getSpeechInput;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.rubideliveryapp.others.Extras.requestInternetAccess;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setImageViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.Extras.validateSearch;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;

public class HomeFragment extends GeneralFragment {

    private static int currentPage = 0; // detects page in image slide carousel
    GridLayoutManager layoutManager; // lays out children in a grid format
    RecyclerView recyclerView;
    LandingPageGridAdapter adapter; // adapter to communicate with recycler view
    ImageView landingBg, searchBtn, speechBtn;
    ImageButton overflowBtn;
    Button offerBtn;
    TextView recyclerTitle;
    TextInputLayout inputLayoutSearch;
    EditText inputSearch;
    List<LandingPageFood> foods;
    CoordinatorLayout mainContent;
    ProgressBar progressBar; // shown while user info is being loaded
    Parcelable mListState; // used to save state of recycler view across rotation

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // inflate layout
        view = inflater.inflate(R.layout.fragment_home, container, false);
        assert getActivity() != null;

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
        speechBtn = view.findViewById(R.id.microphone_image_view);
        offerBtn = view.findViewById(R.id.offerBtn);
        recyclerTitle = view.findViewById(R.id.landing_card_recycler_title);
        overflowBtn = view.findViewById(R.id.landing_page_overflow_button);
        inputLayoutSearch = view.findViewById(R.id.input_layout_search);
        inputSearch = view.findViewById(R.id.input_search);
        mainContent = view.findViewById(R.id.main_content);
        progressBar = view.findViewById(R.id.progressBar);
        setImageViewDrawableColor(progressBar.getIndeterminateDrawable(), getResources()
                .getColor(R.color.colorProgressBar));

        // set colors to overflow, microphone and search buttons
        if (getActivity() != null) {
            setImageViewDrawableColor(overflowBtn.getDrawable(), ContextCompat.getColor(getActivity(), R.color.colorApp));
            setImageViewDrawableColor(searchBtn.getDrawable(), ContextCompat.getColor(getActivity(), R.color.colorApp));
            setImageViewDrawableColor(speechBtn.getDrawable(), ContextCompat.getColor(getActivity(), R.color.colorApp));
        }

        // when user clicks on mic
        speechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(getActivity()))
                    getSpeechInput(getActivity(), HomeFragment.this);
                else requestInternetAccess(mainContent, getActivity());
            }
        });

        // show today's offers
        offerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundOnClick(getActivity(), offerBtn);
            }
        });

        // set up image carousel for day's offers using viewpager
        final ViewPager mViewPager = view.findViewById(R.id.offer_view_pager);
        final ImagesViewPagerAdapter adapter = new ImagesViewPagerAdapter(getActivity(), offerBtn);
        mViewPager.setAdapter(adapter);
        // indicator shows current page visible
        CircleIndicator indicator = view.findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            public void run() {
                if (currentPage == adapter.getCount()) currentPage = 0;
                mViewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 2500, 5000);

        requestInternetAccess(mainContent, getActivity());

        // set image(s) of today's offers to background
        HashMap<String, Object> src = new HashMap<>();
        src.put(RES_ID, R.drawable.ruby_small);
        loadPictureToImageView(src, R.drawable.bg_circle, landingBg, false, false,
                false, false);

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

        // on clicking overflow(3-dots) button
        overflowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        prepareFoodItems();

        return view;
    }

    private void prepareFoodItems() {
        OrderLab orderLab = OrderLab.get(getActivity());
        foods = orderLab.getFoods();

        adapter = new LandingPageGridAdapter(getActivity(), foods);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (foods.size() > 0) progressBar.setVisibility(View.GONE);

        // redirect to order page
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = foods.get(position).getId();
                Intent intent = OrderPagerActivity.newIntent(getActivity(), pos);
                startActivity(intent);
            }
        });
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

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        assert getContext() != null;
        PopupMenu popup = new PopupMenu(getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.landing_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(getContext()));
        popup.show();
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

        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        Context mContext;

        MyMenuItemClickListener(@NonNull Context context) {
            this.mContext = context;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_select_favourites:
                    recyclerTitle.setText(getString(R.string.action_select_favourites));
                    return true;
                case R.id.action_select_categories:
                    recyclerTitle.setText(getString(R.string.action_select_categories));
                    return true;
                default:
            }
            return false;
        }
    }
}
