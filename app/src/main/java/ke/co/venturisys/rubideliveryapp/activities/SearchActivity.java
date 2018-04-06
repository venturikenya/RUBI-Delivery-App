package ke.co.venturisys.rubideliveryapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.fragments.SearchFragment;

import static ke.co.venturisys.rubideliveryapp.others.Constants.EXTRA_SEARCH_QUERY;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;

public class SearchActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context packageContext, String query) {
        Intent intent = new Intent(packageContext, SearchActivity.class);
        intent.putExtra(EXTRA_SEARCH_QUERY, query);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        setTitle(getString(R.string.activity_title_search));
        return SearchFragment.newInstance(getIntent().getStringExtra(EXTRA_SEARCH_QUERY));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitToTargetActivity(this, MainActivity.class);
    }
}
