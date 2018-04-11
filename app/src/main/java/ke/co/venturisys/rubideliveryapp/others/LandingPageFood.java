package ke.co.venturisys.rubideliveryapp.others;

/**
 * Created by victor on 3/19/18.
 * This class models the landing page recycler view
 */

public class LandingPageFood {

    private String name;
    private String icon;
    private int id;

    LandingPageFood(String name, String icon, int id) {
        this.name = name;
        this.icon = icon;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public int getId() {
        return id;
    }
}
