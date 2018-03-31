package ke.co.venturisys.rubideliveryapp.others;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;

/**
 * Created by victor on 3/30/18.
 * This class creates an Adapter to load the images into ViewPager
 * for the images of the intro slider for first launch of app
 */

public class IntroViewPagerAdapter extends PagerAdapter {

    private Context context;
    // foreground circular images
    private int[] circles = new int[]{
            R.drawable.ruby_done_logo,
            R.drawable.intro_screen_02_circle,
            R.drawable.intro_screen_03_circle,
            R.drawable.intro_screen_04_circle
    };

    public IntroViewPagerAdapter(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return circles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.welcome_slide, container, false);
        ImageView circularImage = view.findViewById(R.id.welcome_circular_image);
        // set image(s) to intro slider's circular foreground
        HashMap<String, Object> foreground = new HashMap<>();
        foreground.put(RES_ID, circles[position]);
        loadPictureToImageView(foreground, R.drawable.bg_circle, circularImage, false, false,
                false, false);
        // set title(s) & subtitle(s) to resp text views
        TextView tvTitle = view.findViewById(R.id.welcome_title),
                tvSubtitle = view.findViewById(R.id.welcome_subtitle);
        String[] titles = context.getResources().getStringArray(R.array.intro_slider_titles),
                subtitles = context.getResources().getStringArray(R.array.intro_slider_subtitles);
        tvTitle.setText(titles[position]);
        String subtitle = subtitles[position];
        if (subtitle.equals("None")) subtitle = "";
        tvSubtitle.setText(subtitle);
        if (position > 0) {
            tvSubtitle.setTextColor(Color.WHITE);
            tvTitle.setTextColor(Color.WHITE);
        }

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}
