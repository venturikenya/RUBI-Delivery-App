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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.activities.MainActivity;
import ke.co.venturisys.rubideliveryapp.activities.OrderActivity;
import ke.co.venturisys.rubideliveryapp.fragments.HomeFragment;

import static ke.co.venturisys.rubideliveryapp.activities.MainActivity.setCurrentTag;
import static ke.co.venturisys.rubideliveryapp.activities.MainActivity.setNavItemIndex;
import static ke.co.venturisys.rubideliveryapp.others.Constants.FILE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URI;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URL;

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
            } else if (activity instanceof OrderActivity) {
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
}
