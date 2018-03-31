package ke.co.venturisys.rubideliveryapp.others;

import android.graphics.Color;

import ke.co.venturisys.rubideliveryapp.R;

/**
 * Created by victor on 3/17/18.
 * Class for constants
 */

public class Constants {

    public static final String TAG_HOME = "home";
    public static final String TAG_PROFILE = "profile";
    public static final String TAG_NOTIFICATIONS = "notifications";
    public static final String TAG_ORDER_HISTORY = "order_history";
    public static final String TAG_CART = "cart";
    public static final String TAG_REGISTRATION = "registration";
    public static final String URL = "URL";
    public static final String RES_ID = "RESOURCE ID";
    // extra used to pass data to activities
    public static final String EXTRA_PROFILE_IMAGE_URL = "ke.co.venturisys.rubideliveryapp.profile_image";
    public static final String LIST_STATE_KEY = "LIST_STATE_KEY";
    // time for splash screen to last in milliseconds
    public static final long SPLASH_TIME = 1200;
    public static final String INTERNET_PICKER = "INTERNET_PICKER";
    // arg used to pass data to fragments
    public static final String ARG_TITLE = "title";
    public static final String ARG_ICON = "icon";
    public static final String EXTRA_USER_MEALS = "ke.co.venturisys.rubideliveryapp.meals";
    public static final String EXTRA_USER_BACKDROP_TITLE = "ke.co.venturisys.rubideliveryapp.category_title";
    public static final String EXTRA_USER_BACKDROP_ICON = "ke.co.venturisys.rubideliveryapp.category_icon";
    public static final String EXTRA_POST_URL = "ke.co.ke.venturisys.rubideliveryapp.post_url";
    static final String FILE = "FILE";
    static final String URI = "URI";
    static final int BORDER_COLOR = Color.WHITE;
    static final int BORDER_RADIUS = 15;
    static final int REQUEST_MOBILE_NETWORK = 18395;
    // Shared preferences file name
    static final String PREF_NAME = "rubi-delivery-welcome";
    static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    // intro slider background images
    public static int[] bg = new int[]{
            R.drawable.intro_screen_01_background,
            R.drawable.intro_screen_02_background,
            R.drawable.intro_screen_03_background,
            R.drawable.intro_screen_04_background
    };

}
