package ke.co.venturisys.rubideliveryapp.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.others.IntroViewPagerAdapter;
import ke.co.venturisys.rubideliveryapp.others.PreferencesManager;

import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;

public class WelcomeActivity extends AppCompatActivity {

    PreferencesManager preferenceManager;
    ViewPager viewPager;
    Button btnNext;
    TextView btnSkip;
    ImageView backgroundIv;
    ImageView[] dots;
    RelativeLayout dotsLayout;
    IntroViewPagerAdapter adapter;
    String[] titles;
    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // intro slider background images
            int[] bg = new int[]{
                    R.drawable.intro_screen_01_background,
                    R.drawable.intro_screen_02_background,
                    R.drawable.intro_screen_03_background,
                    R.drawable.intro_screen_04_background
            };
            // set image(s) to intro slider's background
            backgroundIv.setImageDrawable(
                    ContextCompat.getDrawable(WelcomeActivity.this, bg[position]));

            // set background of next btn
            if (position == 0) {
                btnNext.setBackground(ContextCompat.getDrawable(
                        WelcomeActivity.this, R.drawable.rounded_rectangle_edges_grey));
            } else {
                btnNext.setBackground(ContextCompat.getDrawable(
                        WelcomeActivity.this, R.drawable.rounded_rectangle_edges_grey2));
            }

            // handles visibility and content of skip & next btns resp
            if (position == 0 || position == (titles.length - 1)) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.INVISIBLE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.continue_));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /*
     * Method sets the color of the dots depending on screen the intro slider is currently on
     */
    private void addBottomDots(int currentPage) {
        dotsLayout.removeAllViews();
        for (ImageView dot : dots) {
            dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.holo_circle));
            dotsLayout.addView(dot);
        }
        dots[currentPage].setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.fill_circle));
    }

    /*
     * Detect when any of the dots is clicked on & redirect to appropriate page
     */
    private void dotsOnClick() {
        dots[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        dots[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        dots[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });
        dots[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(3);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        preferenceManager = new PreferencesManager(this);
        if (!preferenceManager.isFirstTimeLaunch()) {
            launchHomeScreen();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        // initialise widgets
        viewPager = findViewById(R.id.welcome_view_pager);
        btnNext = findViewById(R.id.welcome_next_btn);
        btnSkip = findViewById(R.id.welcome_skip_btn);
        backgroundIv = findViewById(R.id.welcome_backdrop);
        dotsLayout = findViewById(R.id.dotsLayout);
        titles = getResources().getStringArray(R.array.intro_slider_titles);
        dots = new ImageView[]{
                findViewById(R.id.dot1),
                findViewById(R.id.dot2),
                findViewById(R.id.dot3),
                findViewById(R.id.dot4)
        };

        // making notification bar transparent
        changeStatusBarColor();

        // set listeners for skipping and moving to next slide
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < titles.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

        adapter = new IntroViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        dotsOnClick();
    }

    private int getItem(int i) {
        return (viewPager.getCurrentItem() + i);
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void launchHomeScreen() {
        preferenceManager.setFirstTimeLaunch(false);
        exitToTargetActivity(this, LoginActivity.class);
    }
}
