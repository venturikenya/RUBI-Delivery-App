package ke.co.venturisys.rubideliveryapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ke.co.venturisys.rubideliveryapp.R;

import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToMainActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setUpActionBar;

public class TermsConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        setUpActionBar(getString(R.string.activity_title_terms_conditions), this);
    }

    @Override
    public void onBackPressed() {
        exitToMainActivity(this, MainActivity.class);
        super.onBackPressed();
    }
}
