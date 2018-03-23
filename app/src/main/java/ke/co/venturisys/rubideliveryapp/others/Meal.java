package ke.co.venturisys.rubideliveryapp.others;

/**
 * Created by victor on 3/22/18.
 * Provides model for cart recycler view
 */

public class Meal {

    private String title;
    private String details;
    private String amount;
    private String price;

    public Meal(String title, String details, String amount, String price) {
        this.title = title;
        this.details = details;
        this.amount = amount;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    String getDetails() {
        return details;
    }

    String getAmount() {
        return amount;
    }

    String getPrice() {
        return price;
    }
}
