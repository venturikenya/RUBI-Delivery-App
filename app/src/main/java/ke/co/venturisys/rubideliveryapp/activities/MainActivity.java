package ke.co.venturisys.rubideliveryapp.activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import ke.co.venturisys.rubideliveryapp.FindCustomerShortenedQuery;
import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.database.helpers.SignBaseHelper;
import ke.co.venturisys.rubideliveryapp.database.schemas.SignInDbSchema.SignInTable;
import ke.co.venturisys.rubideliveryapp.database.wrappers.SignInCursorWrapper;
import ke.co.venturisys.rubideliveryapp.fragments.CartFragment;
import ke.co.venturisys.rubideliveryapp.fragments.ChangePasswordFragment;
import ke.co.venturisys.rubideliveryapp.fragments.HomeFragment;
import ke.co.venturisys.rubideliveryapp.fragments.NotificationsFragment;
import ke.co.venturisys.rubideliveryapp.fragments.OrderHistoryFragment;
import ke.co.venturisys.rubideliveryapp.fragments.ProfileFragment;
import ke.co.venturisys.rubideliveryapp.others.MyApolloClient;

import static ke.co.venturisys.rubideliveryapp.others.Constants.ERROR;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_CHANGE_PASSWORD;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_HOME;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_NOTIFICATIONS;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_ORDER_HISTORY;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG_PROFILE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URL;
import static ke.co.venturisys.rubideliveryapp.others.Extras.changeFragment;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.getContentValues;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;
import static ke.co.venturisys.rubideliveryapp.others.Extras.requestInternetAccess;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setBadgeCount;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setTextViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.NetworkingClass.isNetworkAvailable;
import static ke.co.venturisys.rubideliveryapp.others.URLs.urlAboutUs;
import static ke.co.venturisys.rubideliveryapp.others.URLs.urlTermsConditions;
import static ke.co.venturisys.rubideliveryapp.others.URLs.urlVenturi;

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
    // firebase set up
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    // get current user
    FirebaseUser user;

    // widgets
    CoordinatorLayout mainContent;
    LinearLayout headerLayout;
    TextView tvMenuLocation, tvNameLocation;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    View navHeader;
    ImageView imgProfile, imgCloseMenu;
    FloatingActionButton fab;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Fragment fragment;
    String photo_url;
    SQLiteDatabase mDatabase; // used to save user's credentials

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

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    exitToTargetActivity(MainActivity.this, LoginActivity.class);
                }
            }
        };

        mHandler = new Handler();

        // initialise widgets
        mainContent = findViewById(R.id.parent_layout);
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
        mDatabase = new SignBaseHelper(this).getWritableDatabase();

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
     *  It also takes care of other things like changing the toolbar activity_title,
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
        final String email = Objects.requireNonNull(user.getEmail());

        // carry out a GraphQL query to get the user's name, location and profile image
        // please ensure that the user is signed in first and there is internet connection
        if (user != null) {
            if (isNetworkAvailable(this)) {
                MyApolloClient.getMyApolloClient().query(
                        FindCustomerShortenedQuery.builder().email(email).build()
                ).enqueue(new ApolloCall.Callback<FindCustomerShortenedQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull final Response<FindCustomerShortenedQuery.Data> response) {
                        // update UI on the activity's UI thread to reflect query
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String name = Objects.requireNonNull(Objects.requireNonNull
                                        (response.data()).Customer()).name();
                                // update text views
                                tvNameLocation.setText(name);
                                tvMenuLocation.setText(Objects.requireNonNull(Objects.requireNonNull
                                        (response.data()).Customer()).location());

                                // save user's details to SQLite database if not previously saved
                                List<String> emails = getEmails();
                                if (!emails.contains(email)) {
                                    ContentValues values = getContentValues(email, name);
                                    mDatabase.insert(SignInTable.NAME, null, values);
                                    Log.e(TAG, values.toString());
                                }

                                // load profile image
                                photo_url = Objects.requireNonNull(Objects.requireNonNull(response.data()).Customer()).image();
                                HashMap<String, Object> src = new HashMap<>();
                                src.put(URL, photo_url);
                                loadPictureToImageView(src, R.mipmap.ic_box, imgProfile, true, false,
                                        false, false);
                            }
                        });
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        // Log error so as to fix it
                        Log.e(ERROR, "onFailure: Something went wrong. " + e.getMessage());
                    }
                });
            } else requestInternetAccess(mainContent, this);
        }

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
                String email = user.getEmail(); // necessary for updating profile image
                Intent intent = ProfileImageActivity.newIntent(MainActivity.this, photo_url, email);
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

    /**
     * Gets list of emails for auto completion from database
     *
     * @return list of email
     */
    private ArrayList<String> getEmails() {
        // create array list to hold saved emails
        ArrayList<String> emails = new ArrayList<>();
        // create wrapper for cursor
        SignInCursorWrapper cursor = queryEmail();

        // get received emails and add to array list
        //noinspection TryFinallyCanBeTryWithResources
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                emails.add(cursor.getEmail());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return emails;
    }

    /**
     * Creates the query for retrieving registration numbers
     *
     * @return instance of cursor wrapper
     */
    private SignInCursorWrapper queryEmail() {
        @SuppressLint("Recycle") Cursor cursor = mDatabase.query(
                SignInTable.NAME, // Table
                new String[]{SignInTable.Cols.EMAIL}, // Columns - null selects all columns
                null, // WHERE
                null, // Conditions to be met
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new SignInCursorWrapper(cursor);
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
                        startActivity(BrowserActivity.newIntent(MainActivity.this, urlAboutUs));
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_terms_conditions:
                        // launch new intent instead of loading fragment
                        startActivity(BrowserActivity.newIntent(MainActivity.this,
                                urlTermsConditions));
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_creator_link:
                        // redirect to venturi's website
                        startActivity(BrowserActivity.newIntent(MainActivity.this, urlVenturi));
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_change_password:
                        changeFragment(new ChangePasswordFragment(), new Handler(), TAG_CHANGE_PASSWORD,
                                MainActivity.this);
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_delete_account:
                        deleteAccount();
                        return true;
                    case R.id.nav_logout:
                        signOut();
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

    private void deleteAccount() {
        // confirm if user wants to delete account
        Snackbar snackbar = Snackbar
                .make(mainContent, "Are you sure you to delete your account", Snackbar.LENGTH_LONG)
                .setAction("I AM SURE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // carry out deletion process
                        if (user != null) {
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this,
                                                        "Good bye, we'll miss you", Toast.LENGTH_SHORT).show();
                                                exitToTargetActivity(MainActivity.this, LoginActivity.class);
                                            } else {
                                                Toast.makeText(MainActivity.this,
                                                        "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                                Log.e(ERROR, task.getResult().toString());
                                            }
                                        }
                                    });
                        }
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(getResources().getColor(R.color.colorSnackbarActionText));

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
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

    //sign out method
    public void signOut() {
        Toast.makeText(this, "Come back soon!", Toast.LENGTH_SHORT).show();
        auth.signOut();
        exitToTargetActivity(this, LoginActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
