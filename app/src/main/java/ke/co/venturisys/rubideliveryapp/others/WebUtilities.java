package ke.co.venturisys.rubideliveryapp.others;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import static android.content.Context.MODE_PRIVATE;
import static ke.co.venturisys.rubideliveryapp.others.Constants.PREF_BOOKMARK_NAME;

public class WebUtilities {
    /*
     * checks whether an url is from same domain or not.
     * Will be useful to launch the browser activity in case of external url
     */
    public static boolean isSameDomain(String url, String url1) {
        return getRootDomainUrl(url.toLowerCase()).equals(getRootDomainUrl(url1.toLowerCase()));
    }

    private static String getRootDomainUrl(String url) {
        String[] domainKeys = url.split("/")[2].split("\\.");
        int length = domainKeys.length;
        int dummy = domainKeys[0].equals("www") ? 1 : 0;
        if (length - dummy == 2)
            return domainKeys[length - 2] + "." + domainKeys[length - 1];
        else {
            if (domainKeys[length - 1].length() == 2) {
                return domainKeys[length - 3] + "." + domainKeys[length - 2] + "." + domainKeys[length - 1];
            } else {
                return domainKeys[length - 2] + "." + domainKeys[length - 1];
            }
        }
    }

    /*
     *  This method is to change the bookmark icon color when an url is bookmarked.
     */
    public static void tintMenuIcon(Context context, ImageView item, int color) {
        Drawable drawable = item.getDrawable();
        if (drawable != null) {
            // If we don't mutate the drawable, then all drawable's with this id will have a color
            // filter applied to it.
            drawable.mutate();
            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN));
        }
    }

    /*
     * Adds or removes an url from bookmarks list using SharedPreferences
     */
    public static boolean bookmarkUrl(Context context, String url) {
        boolean bookmarked;
        // mode_private means the file can only be accessed using calling application
        SharedPreferences pref = context.getSharedPreferences(PREF_BOOKMARK_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        // if url is already bookmarked, un bookmark it
        if (isBookmarked(context, url)) {
            editor.putBoolean(url, false);
            bookmarked = false;
        } else {
            editor.putBoolean(url, true);
            bookmarked = true;
        }

        editor.apply();
        return bookmarked;
    }

    /*
     * Checks whether url has already been bookmarked
     */
    private static boolean isBookmarked(Context context, String url) {
        SharedPreferences pref = context.getSharedPreferences(PREF_BOOKMARK_NAME, 0);
        return pref.getBoolean(url, false);
    }

}
