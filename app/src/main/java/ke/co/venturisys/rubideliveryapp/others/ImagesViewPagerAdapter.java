package ke.co.venturisys.rubideliveryapp.others;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Extras.backgroundOnClick;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;

/**
 * Created by victor on 3/27/18.
 * This class creates an Adapter to load the images into ViewPager
 * for the images of day's offers in landing page
 */

public class ImagesViewPagerAdapter extends PagerAdapter {
    private Context context;
    private Button offerBtn;
    private int[] images = new int[]
            {R.drawable.ruby_small, R.drawable.ic_pastries, R.drawable.ic_beef_specials};

    public ImagesViewPagerAdapter(@NonNull Context context, @NonNull Button button) {
        this.context = context;
        this.offerBtn = button;
    }

    /*
     * Returns the number of available views in the ViewPager
     */
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /*
     *  This method should create the page for the position passed to it as an argument
     *  Inflate custom image slider layout to create the android images slider
     *  and set the image resource for the ImageView in it using Picasso image loading library
     *  The inflated view is added to the ViewPager using addView() and returned
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.custom_image_slider, container, false);
        ImageView imageView = view.findViewById(R.id.backdrop);
        // set image(s) of today's offers to background
        HashMap<String, Object> src = new HashMap<>();
        src.put(RES_ID, images[position]);
        loadPictureToImageView(src, R.drawable.bg_circle, imageView, false, false,
                false, false);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundOnClick(context, offerBtn);
            }
        });

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    /*
     *  Removes the page for the given position from the container
     *  Simply remove object using removeView()
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
