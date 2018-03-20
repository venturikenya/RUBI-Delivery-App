package ke.co.venturisys.rubideliveryapp.others;

/**
 * Created by victor on 3/20/18.
 * Provides a model for notifications recycler view
 */

public class Notification {

    private String title;
    private String time;
    private String details;

    public Notification(String title, String time, String details) {
        this.title = title;
        this.time = time;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    String getDetails() {
        return details;
    }
}
