package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import ke.co.venturisys.rubideliveryapp.R;

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
        if (textView != null && activity != null) {
            for (Drawable drawable : textView.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(
                            activity.getResources().getColor(color), PorterDuff.Mode.SRC_IN));
                }
            }
        }
    }

    /*
     * Method used to set up action bar for activities
     */
    public static void setUpActionBar(String title, AppCompatActivity activity) {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(
                    Html.fromHtml("<font color='#1976D2'>"+ title
                            + "</font>"));
            final Drawable upArrow = activity.getResources().getDrawable(R.drawable.ic_chevron_left_small);
            upArrow.setColorFilter(activity.getResources().getColor(R.color.colorApp), PorterDuff.Mode.SRC_IN);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }
}
