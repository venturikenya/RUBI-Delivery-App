package ke.co.venturisys.rubideliveryapp.others;

import android.app.Activity;
import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import ke.co.venturisys.rubideliveryapp.AllCategoriesQuery;

import static ke.co.venturisys.rubideliveryapp.others.Constants.ERROR;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG;

/**
 * Created by victor on 3/26/18.
 * Is a singleton, a class that allows only one instance of itself to be created.
 * Used to return a list of categories of meal for user to order from
 */

public class OrderLab {

    private static OrderLab sOrderLab;
    private List<LandingPageFood> foods;

    private OrderLab(final Activity activity) {
        foods = new ArrayList<>();

        /*
         * Here we are going to query all the category of meals stored in the GraphQL server
         */
        MyApolloClient.getMyApolloClient().query(
                AllCategoriesQuery.builder().build()
        ).enqueue(new ApolloCall.Callback<AllCategoriesQuery.Data>() {
            @Override
            // successful query
            public void onResponse(@Nonnull Response<AllCategoriesQuery.Data> response) {

                // should log the first category's title
                Log.d(TAG, "onResponse: " + Objects.requireNonNull(response.data()).allCategories().get(0).name());
                final List<AllCategoriesQuery.AllCategory> allCategories = Objects.requireNonNull(response.data()).allCategories();

                // run changes on UI thread to show changes
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < allCategories.size(); i++) {
                            AllCategoriesQuery.AllCategory allCategory = allCategories.get(i);
                            foods.add(new LandingPageFood(allCategory.name(), allCategory.icon(), i));
                        }
                    }
                });

            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {

                // Log error so as to fix it
                Log.e(ERROR, "onFailure: Something went wrong. " + e.getMessage());

            }
        });
    }

    public static OrderLab get(Activity activity) {
        if (sOrderLab == null) sOrderLab = new OrderLab(activity);
        return sOrderLab;
    }

    public List<LandingPageFood> getFoods() {
        return foods;
    }

    public LandingPageFood getFood(String title) {
        for (LandingPageFood food : foods) {
            if (food.getName().equals(title)) return food;
        }
        return null;
    }
}
