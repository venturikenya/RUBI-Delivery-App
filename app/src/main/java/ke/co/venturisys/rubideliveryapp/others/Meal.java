package ke.co.venturisys.rubideliveryapp.others;

/**
 * Created by victor on 3/22/18.
 * Provides model for cart recycler view
 */

public class Meal {

    private String icon;
    private String title;
    private String details;
    private String amount;
    private String price;
    private String category;
    private String quantity;

    public Meal(String icon, String title, String details, String amount, String price,
                String category, String quantity) {
        this.icon = icon;
        this.title = title;
        this.details = details;
        this.amount = amount;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
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

    public String getPrice() {
        return price;
    }

    public String getIcon() {
        return icon;
    }

    public String getCategory() {
        return category;
    }

    public String getQuantity() {
        return quantity;
    }
}
