package ke.co.venturisys.rubideliveryapp.others;

import android.graphics.Color;

/**
 * Created by victor on 3/17/18.
 * Class for constants
 */

public class Constants {

    public static final String TAG = "TAG";
    public static final String ERROR = "ERROR";
    public static final String TAG_HOME = "home";
    public static final String TAG_PROFILE = "profile";
    public static final String TAG_NOTIFICATIONS = "notifications";
    public static final String TAG_ORDER_HISTORY = "order_history";
    public static final String TAG_CART = "cart";
    public static final String TAG_REGISTRATION = "registration";
    public static final String TAG_LOGIN = "login";
    public static final String TAG_EDIT_PROFILE = "EDIT PROFILE";
    public static final String TAG_RESET_PASSWORD = "RESET PASSWORD";
    public static final String URL = "URL";
    public static final String RES_ID = "RESOURCE ID";
    // extra used to pass data to activities
    public static final String EXTRA_PROFILE_IMAGE_URL = "ke.co.venturisys.rubideliveryapp.profile_image";
    public static final String EXTRA_USER_MEALS = "ke.co.venturisys.rubideliveryapp.meals";
    public static final String EXTRA_USER_BACKDROP_TITLE = "ke.co.venturisys.rubideliveryapp.category_title";
    public static final String EXTRA_USER_BACKDROP_ICON = "ke.co.venturisys.rubideliveryapp.category_icon";
    public static final String EXTRA_POST_URL = "ke.co.venturisys.rubideliveryapp.post_url";
    public static final String EXTRA_SEARCH_QUERY = "ke.co.venturisys.rubideliveryapp.search_query";
    public static final String LIST_STATE_KEY = "LIST_STATE_KEY";
    // time for splash screen to last in milliseconds
    public static final long SPLASH_TIME = 1200;
    public static final String VERIFY_SMS = "VERIFY_SMS";
    public static final String SELECT_PICTURE = "SELECT_PICTURE";
    // arg used to pass data to fragments
    public static final String ARG_TITLE = "title";
    public static final String ARG_ICON = "icon";
    public static final String ARG_SHOW_FIELDS = "show_fields";
    public static final String ARG_NAME = "name";
    public static final String ARG_DETAILS = "details";
    public static final String ARG_PHONE_NUMBER = "phone number";
    public static final String ARG_LOCATION = "location";
    public static final String ARG_EMAIL = "email";
    public static final String ARG_SEARCH_QUERY = "search_query";
    // intent request codes
    public static final int REQUEST_PHOTO = 1;
    public static final int REQUEST_GALLERY = 293;
    public static final int REQUEST_SPEECH = 10;
    // permission tags
    public static final String PERMISSION_STORAGE = "external_storage";
    public static final String PERMISSION_CAMERA = "camera";
    public static final String URI = "URI";
    static final String INTERNET_PICKER = "INTERNET_PICKER";
    static final String ARG_PATH = "path";
    static final String FILE = "FILE";
    static final int BORDER_COLOR = Color.WHITE;
    static final int BORDER_RADIUS = 15;
    static final int REQUEST_MOBILE_NETWORK = 18395;
    static final int BUFFER = 1024;
    // Shared preferences file name
    static final String PREF_WELCOME_NAME = "rubi-delivery-welcome";
    static final String PREF_BOOKMARK_NAME = "rubi-delivery-bookmark";
    static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    static final String CAMERA_FACING = "android.intent.extras.CAMERA_FACING";
    // permission request codes
    static final int REQUEST_CAMERA = 2782;
    static final int REQUEST_EXTERNAL_STORAGE = 4;

}
