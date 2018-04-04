package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.MainActivity;
import ke.co.venturisys.rubideliveryapp.activities.OrderActivity;
import ke.co.venturisys.rubideliveryapp.activities.OrderPagerActivity;
import ke.co.venturisys.rubideliveryapp.activities.ProfileImageActivity;
import ke.co.venturisys.rubideliveryapp.fragments.EditProfileFragment;
import ke.co.venturisys.rubideliveryapp.fragments.HomeFragment;

import static ke.co.venturisys.rubideliveryapp.activities.MainActivity.setCurrentTag;
import static ke.co.venturisys.rubideliveryapp.activities.MainActivity.setNavItemIndex;
import static ke.co.venturisys.rubideliveryapp.others.Constants.FILE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.REQUEST_GALLERY;
import static ke.co.venturisys.rubideliveryapp.others.Constants.REQUEST_SPEECH;
import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URI;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URL;
import static ke.co.venturisys.rubideliveryapp.others.PictureUtilities.galleryAddPic;
import static ke.co.venturisys.rubideliveryapp.others.PictureUtilities.takePicture;

/**
 * Created by victor on 3/17/18.
 * This class contains methods that will be used throughout project
 */

public class Extras {

    /*
     * Method used to appear filter to drawables of text view
     */
    public static void setTextViewDrawableColor(TextView textView, int color,
                                                Activity activity) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(
                        ContextCompat.getColor(activity, color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    /*
     * Method used to set filter to drawables of image view
     */
    public static void setImageViewDrawableColor(Drawable drawable, int color) {
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
    }

    /*
     * Method used to set up action bar for activities
     */
    public static void setUpActionBar(String title, AppCompatActivity activity) {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(
                    Html.fromHtml("<font color='#1976D2'>" + title
                            + "</font>"));
            final Drawable upArrow = activity.getResources().getDrawable(R.drawable.ic_chevron_left_small);
            upArrow.setColorFilter(activity.getResources().getColor(R.color.colorApp), PorterDuff.Mode.SRC_IN);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

    /*
     * Method to successfully load image to given image view with right scale
     */
    public static void loadPictureToImageView(@NonNull HashMap<String, Object> source,
                                              int placeholder,
                                              @NonNull ImageView imageView,
                                              boolean toTransform,
                                              boolean fit,
                                              boolean centerInside,
                                              boolean border) {

        Picasso picasso;
        RequestCreator creator = null;

        try {

            picasso = Picasso.get();

            // initialise creator from various sources
            if (source.containsKey(URL)) creator = picasso.load((String) source.get(URL));
            if (source.containsKey(RES_ID)) creator = picasso.load((int) source.get(RES_ID));
            if (source.containsKey(FILE)) creator = picasso.load((File) source.get(FILE));
            if (source.containsKey(URI)) creator = picasso.load((Uri) source.get(URI));

            if (creator != null) {
                // set up creator with various scenarios
                creator = creator.placeholder(placeholder)
                        .error(android.R.drawable.ic_delete)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE);
                if (toTransform) creator = creator.transform(new CircleTransform(border));
                if (fit) creator = creator.fit();
                if (centerInside) creator = creator.centerInside();

                // load into image view considering the possible results of loading
                creator.into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Picasso Success", "Successful image loading");
                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("Picasso Error", "Unsuccessful image loading");
                        exception.printStackTrace();
                    }
                });
            }

        } catch (Exception ex) {
            Log.e("PICTURES ERROR", "Something went wrong");
            ex.printStackTrace();
        }
    }

    /*
     * Method redirects to target activity while popping every other activity off stack
     */
    public static void exitToTargetActivity(AppCompatActivity activity, Class class_) {
        Intent intent = new Intent(activity, class_);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish(); // close this activity
    }

    /*
     * Set badge on the menu item from our Activity.
     */
    public static void setBadgeCount(Context context, LayerDrawable icon, String count, int res) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(res);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(res, badge);
    }

    /*
     * This method provides for changing of fragments based on choice selected
     * either on navigation menu or within the fragments
     */
    public static void changeFragment(final Fragment homeFragment, Handler mHandler,
                                      final String CURRENT_TAG, final AppCompatActivity activity) {
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                // result in 'shaking' appearance when the fragments transition
//                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
//                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, homeFragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        mHandler.post(mPendingRunnable);
    }

    /*
     * Method redirects to home fragment on back button pressed
     */
    public static boolean goToTargetFragment(AppCompatActivity activity, int keyCode,
                                             KeyEvent event, String TAG_SRC, String TAG_TARGET) {
        // detect if back button is pressed
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

            // redirect to landing page
            if (activity instanceof MainActivity) {
                setNavItemIndex(0);
                setCurrentTag(TAG_TARGET);
                Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(TAG_SRC);
                if (fragment != null)
                    activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();

                changeFragment(HomeFragment.newInstance(), new Handler(), TAG_SRC, activity);
            } else if (activity instanceof OrderActivity || activity instanceof OrderPagerActivity) {
                exitToTargetActivity(activity, MainActivity.class);
            }

            return true;

        }

        return false;
    }

    public static View inflateCartMenu(MenuInflater inflater, Menu menu, Activity activity) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
        View view = menu.findItem(R.id.action_proceed_order).getActionView();
        // obtain drawable of shopping bag to place badge on top of it
        TextView textView = view.findViewById(R.id.editOrderTextView);
        // set shopping bag icon
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_bag, 0, 0, 0);
        // add badge showing no. of orders
        LayerDrawable icon = (LayerDrawable) textView.getCompoundDrawables()[0];
        setBadgeCount(activity, icon, String.valueOf(2), R.id.ic_shopping_badge);
        // set colour of shopping bag
        setTextViewDrawableColor(textView, R.color.colorApp, activity);
        return view;
    }

    /*
     * Method redirects to order page when you click on offer button or background image view
     */
    public static void backgroundOnClick(Context activity, Button offerBtn) {
        Intent intent = OrderActivity.newIntent(activity,
                R.drawable.ruby_small, offerBtn.getText().toString());
        activity.startActivity(intent);
    }

    /*
     * Check if length of edit text page is equal to 0, return true if it is
     */
    public static boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    /**
     * Method is used for checking valid email id format.
     *
     * @param email Address being entered
     * @return boolean true for valid, false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /*
     * Call speech input app implicitly so as to allow user to search meals using speech
     * Please, test using real android device, not emulator
     * Will require intent connectivity, so check if internet is available before running
     */
    public static void getSpeechInput(@NonNull Activity activity, @NonNull Fragment fragment) {
        // use implicit intent to get input of user in form of speech/voice
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // pass extra values along with intent
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        // make sure that we have activity present in phone that supports this implicit intent
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            fragment.startActivityForResult(intent, REQUEST_SPEECH);
        } else {
            Toast.makeText(activity, "Your device does not support speech input",
                    Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Selects image from gallery app(s)
     */
    static void galleryIntent(Activity activity, String TAG) {
        Fragment fragment = ((AppCompatActivity) activity).getSupportFragmentManager().findFragmentByTag(TAG);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // make sure that we have activity present in phone that supports this implicit intent
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // Always show the chooser (if there are multiple options available)
            /*
             * Benefits of this include:
             * Even if the user has previously selected a default action for this intent, the chooser will still be displayed.
             * If no applications match, Android displays a system message.
             * You can specify a title for the chooser dialog.
             */
            if (fragment == null)
                activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.activity_title_image_chooser)), REQUEST_GALLERY);
            else
                fragment.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.activity_title_image_chooser)), REQUEST_GALLERY);
        } else {
            Toast.makeText(activity, "Your device does not support image selection",
                    Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Call camera to take picture
     */
    static void cameraIntent(Activity activity, String TAG, @NonNull File directory) {
        Fragment fragment = ((AppCompatActivity) activity).getSupportFragmentManager().findFragmentByTag(TAG);
        Toast.makeText(activity, "Smile For The Camera!", Toast.LENGTH_SHORT).show();
        if (fragment == null)
            setImageForUpload(takePicture(activity, TAG, directory.getAbsolutePath()), false);
        else setImageForUpload(takePicture(fragment, TAG, directory.getAbsolutePath()), true);
        galleryAddPic(activity);
    }

    private static void setImageForUpload(Uri imageForUpload, boolean fragment) {
        if (fragment) EditProfileFragment.imageForUpload = imageForUpload;
        else ProfileImageActivity.imageForUpload = imageForUpload;
    }
}
