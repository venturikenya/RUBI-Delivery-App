package ke.co.venturisys.rubideliveryapp.activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.HashMap;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Constants.RES_ID;
import static ke.co.venturisys.rubideliveryapp.others.Constants.SPLASH_TIME;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.loadPictureToImageView;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView imageView;
    // handle delayed execution
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // load logo
        imageView = findViewById(R.id.splash_screen_image_view);
        HashMap<String, Object> src = new HashMap<>();
        src.put(RES_ID, R.drawable.ruby);
        loadPictureToImageView(src, R.drawable.ruby,
                imageView, false, false, false, false);

        mHandler.postDelayed(new Runnable() {

            /*
             * Showing splash screen with given run time.
             */
            @Override
            public void run() {
                // Method will run once splash time is over
                // Start login activity
                exitToTargetActivity(SplashScreenActivity.this, MainActivity.class);
            }
        }, SPLASH_TIME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Remove all the callbacks otherwise navigation will execute
        // even after activity is killed or closed.
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
    }
}
