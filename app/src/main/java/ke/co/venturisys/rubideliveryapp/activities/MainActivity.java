package ke.co.venturisys.rubideliveryapp.activities;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.fragments.CartFragment;
import ke.co.venturisys.rubideliveryapp.fragments.HomeFragment;
import ke.co.venturisys.rubideliveryapp.fragments.NotificationsFragment;
import ke.co.venturisys.rubideliveryapp.fragments.OrderHistoryFragment;
import ke.co.venturisys.rubideliveryapp.fragments.ProfileFragment;

import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_HOME;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_NOTIFICATIONS;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_ORDER_HISTORY;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_PROFILE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URL;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setBadgeCount;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setTextViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;
import static ke.co.venturisys.rubideliveryapp.others.URLs.urlProfileImg;

/**
 * Sliding menu: https://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/
 * Hamburger icon: http://codetheory.in/android-navigation-drawer/
 */

public class MainActivity extends AppCompatActivity {

    // index to identify current nav menu item
    public static int navItemIndex = 0;
    // tag for fragment to be shown
    public static String CURRENT_TAG = TAG_HOME;
    // toolbar titles respected to selected nav menu item
    String[] activityTitles;
    // flag to load home fragment when user presses back key
    boolean shouldLoadHomeFragOnBackPress = true;
    Handler mHandler;

    // widgets
    LinearLayout headerLayout;
    TextView tvMenuLocation, tvNameLocation;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    View navHeader;
    ImageView imgProfile, imgCloseMenu;
    FloatingActionButton fab;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Fragment fragment;

    public static void setNavItemIndex(int navItemIndex) {
        MainActivity.navItemIndex = navItemIndex;
    }

    public static void setCurrentTag(String currentTag) {
        CURRENT_TAG = currentTag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mHandler = new Handler();

        // initialise widgets
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        fab = findViewById(R.id.fab);
        // navigation view header
        navHeader = navigationView.getHeaderView(0);
        tvMenuLocation = navHeader.findViewById(R.id.locationTextView);
        tvMenuLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });
        // set drawable to text view
        tvMenuLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_pointer, 0, 0, 0);
        tvNameLocation = navHeader.findViewById(R.id.nameTextView);
        tvNameLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });
        headerLayout = navHeader.findViewById(R.id.navHeaderLayout);
        imgProfile = navHeader.findViewById(R.id.img_profile);
        imgCloseMenu = navHeader.findViewById(R.id.img_close_menu);

        imgCloseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawers();
            }
        });

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        // set color of location text view drawable in navigation menu
        setTextViewDrawableColor(tvMenuLocation, R.color.colorApp, this);

        fragment = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (fragment instanceof CartFragment) {
            fab.hide();
        }

        // wait for connection to be confirmed then show fab (maybe) if successful
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    // check if connected!
                    while (!isNetworkAvailable(MainActivity.this)) {
                        //Wait to connect
                        fab.hide();
                    }

                    toggleFab();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    /*
     * Serves to redirect user to profile page when user select the navigation header
     */
    private void goToProfile() {
        setNavItemIndex(1);
        setCurrentTag(TAG_PROFILE);
        changeFragment(ProfileFragment.newInstance(), mHandler, TAG_PROFILE, this);
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers();
        fab.hide();
    }

    /*
     *  Loads the fragment returned from getHomeFragment() function into FrameLayout.
     *  It also takes care of other things like changing the toolbar title,
     *  hiding / showing fab and
     *  invalidating the options menu so that new menu can be loaded for different fragment.
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawerLayout.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        changeFragment(getHomeFragment(), mHandler, CURRENT_TAG, this);

        // show or hide the fab button
        toggleFab();

        // Closing drawer on item click
        drawerLayout.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    /*
     * Returns the appropriate Fragment depending on the nav menu item user selected
     */
    private Fragment getHomeFragment() {

        switch (navItemIndex) {
            case 0:
                // home
                return HomeFragment.newInstance();
            case 1:
                // profile
                return ProfileFragment.newInstance();
            case 2:
                // notifications
                return NotificationsFragment.newInstance();
            case 3:
                // order history
                return OrderHistoryFragment.newInstance();
            default:
                return HomeFragment.newInstance();
        }

    }

    /*
     * Loads the navigation drawer header information
     * like profile image, name, location and notifications action dot
     */
    private void loadNavHeader() {
        tvNameLocation.setText(getString(R.string.name_placeholder));
        tvMenuLocation.setText(getString(R.string.location_placeholder));

        // load profile image
        HashMap<String, Object> src = new HashMap<>();
        src.put(URL, urlProfileImg);
        loadPictureToImageView(src, R.mipmap.ic_box, imgProfile, true, false,
                false, false);

        // if nav header is selected
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });


        // if user clicks on profile image, blow it up to full scale
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ProfileImageActivity.newIntent(MainActivity.this, urlProfileImg);
                startActivity(intent);
            }
        });

        // set count of notifications on bell
        int notifications = 1;
        // LayerDrawable is a Drawable that manages an array of other Drawables
        LayerDrawable icon = (LayerDrawable) navigationView.getMenu().
                findItem(R.id.nav_notifications).getIcon();
        setBadgeCount(this, icon, String.valueOf(notifications), R.id.ic_notifications_badge);

        // showing dot next to notifications label if notifications present
        //noinspection ConstantConditions
        if (notifications > 0) navigationView.getMenu().getItem(2).setActionView(R.layout.menu_dot);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    /*
     * Initializes the Navigation Drawer by creating necessary click listeners
     * and other functions.
     */
    private void setUpNavigationView() {


        //Setting Navigation View Item Selected Listener
        // to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                // Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    // Replace the current tag value with selected fragment tag value
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_profile:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PROFILE;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_order_history:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_ORDER_HISTORY;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_terms_conditions:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, TermsConditionsActivity.class));
                        drawerLayout.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                // Checking if the item is in checked state or not and invert the state
                // if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open
                super.onDrawerOpened(drawerView);
            }
        };

        // Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // calling sync state is necessary for hamburger icon to show up
        actionBarDrawerToggle.syncState();
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0 && isNetworkAvailable(this)) fab.show();
        else fab.hide();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu(s)

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 2 && (fragment == null || !(fragment.isVisible()))) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(this, "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(this, "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
