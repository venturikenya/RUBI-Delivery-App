package ke.co.venturisys.rubideliveryapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.fragments.OrderFragment;
import ke.co.venturisys.rubideliveryapp.others.LandingPageFood;
import ke.co.venturisys.rubideliveryapp.others.OrderLab;

import static ke.co.venturisys.rubideliveryapp.others.Constants.EXTRA_USER_MEALS;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setUpActionBar;

/*
 * This activity enables user to view the different categories
 * by swiping screen left or right
 */
public class OrderPagerActivity extends AppCompatActivity {

    ViewPager mViewPager;
    private List<LandingPageFood> meals;

    public static Intent newIntent(Context packageContext, int position) {
        Intent intent = new Intent(packageContext, OrderPagerActivity.class);
        intent.putExtra(EXTRA_USER_MEALS, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pager);
        // set up toolbar
        setUpActionBar(getString(R.string.activity_title_order), this);
        meals = OrderLab.get(this).getFoods();
        int mealId = getIntent().getIntExtra(EXTRA_USER_MEALS, 0);

        mViewPager = findViewById(R.id.activity_order_pager_view_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            // prepares adapter for all items in list
            @Override
            public int getCount() {
                return meals.size();
            }

            // return specific item in list and set to fragment
            @Override
            public Fragment getItem(int position) {
                LandingPageFood food = meals.get(position);
                return OrderFragment.newInstance(food.getIcon(), food.getName());
            }
        });

        for (int i = 0; i < meals.size(); i++) {
            if (meals.get(i).getId() == mealId) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
