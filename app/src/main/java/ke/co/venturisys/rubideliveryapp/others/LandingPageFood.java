package ke.co.venturisys.rubideliveryapp.others;

/**
 * Created by victor on 3/19/18.
 * This class models the landing page recycler view
 */

public class LandingPageFood {

    private String name;
    private int icon;

    public LandingPageFood(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }
}
