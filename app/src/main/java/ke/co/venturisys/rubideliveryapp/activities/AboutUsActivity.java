package ke.co.venturisys.rubideliveryapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Extras.setUpActionBar;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        setUpActionBar(getString(R.string.activity_title_about_us), this);
    }
}
