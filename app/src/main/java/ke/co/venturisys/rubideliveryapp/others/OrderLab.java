package ke.co.venturisys.rubideliveryapp.others;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ke.co.venturisys.rubideliveryapp.R;

/**
 * Created by victor on 3/26/18.
 * Is a singleton, a class that allows only one instance of itself to be created.
 * Used to return a list of categories of meal for user to order from
 */

public class OrderLab {

    private static OrderLab sOrderLab;
    private List<LandingPageFood> foods;

    private OrderLab(Context context) {
        foods = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int[] icons = new int[]{
                    R.drawable.ic_pastries,
            };

            LandingPageFood food = new LandingPageFood("Pastries", icons[0], (i + 1));
            foods.add(food);
        }
    }

    public static OrderLab get(Context context) {
        if (sOrderLab == null) sOrderLab = new OrderLab(context);
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
