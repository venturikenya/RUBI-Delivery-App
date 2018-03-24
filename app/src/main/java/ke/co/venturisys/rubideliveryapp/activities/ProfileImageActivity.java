package ke.co.venturisys.rubideliveryapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.EXTRA_PROFILE_IMAGE_URL;
import static ke.co.venturisys.rubideliveryapp.others.Constants.URL;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;

public class ProfileImageActivity extends AppCompatActivity {

    ImageView profileImageView;

    /*
     * Creates intent configured with extra to receive user's profile image
     */
    public static Intent newIntent(Context packageContext, String imgProfile) {
        Intent intent = new Intent(packageContext, ProfileImageActivity.class);
        intent.putExtra(EXTRA_PROFILE_IMAGE_URL, imgProfile);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        // receive profile image url from extra
        String imgProfile = getIntent().getStringExtra(EXTRA_PROFILE_IMAGE_URL);

        assert getSupportActionBar() != null;
        // set up button on toolbar
        final Drawable upArrow = getResources().getDrawable(R.drawable.left_arrow);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        // initialise widgets
        profileImageView = findViewById(R.id.activity_profile_image_view);
        // set profile image to image view
        HashMap<String, Object> src = new HashMap<>();
        src.put(URL, imgProfile);
        loadPictureToImageView(src, R.mipmap.ic_box, profileImageView, true, false,
                false, false);
    }

    @Override
    public void onBackPressed() {
        exitToTargetActivity(this, MainActivity.class);
        super.onBackPressed();
    }
}
