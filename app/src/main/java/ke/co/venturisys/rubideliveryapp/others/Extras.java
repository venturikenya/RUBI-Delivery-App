package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;

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

    /*
     * Method to successfully load image to given image view with right scale
     */
    public static void loadPictureToImageView(@NonNull HashMap<String, Object> source,
                                int placeholder,
                                @NonNull ImageView imageView,
                                boolean toTransform,
                                boolean fit,
                                boolean centerInside) {

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
                        .error(android.R.drawable.ic_delete);
                if (toTransform) creator = creator.transform(new CircleTransform());
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
     * Method redirects to main activity while popping every other activity off stack
     */
    public static void exitToMainActivity(AppCompatActivity activity, Class class_) {
        Intent intent = new Intent(activity, class_);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }
}
