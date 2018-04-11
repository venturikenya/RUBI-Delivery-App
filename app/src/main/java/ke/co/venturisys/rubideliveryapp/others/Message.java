package ke.co.venturisys.rubideliveryapp.others;

import java.util.HashMap;

import static ke.co.venturisys.rubideliveryapp.others.Constants.NAME;
import static ke.co.venturisys.rubideliveryapp.others.Constants.PIC;

/**
 * Created by victor on 4/10/2018
 * This class holds the package info eg image file to be sent to server as message
 */
public class Message {

    private String pic;
    private String name;

    public Message(HashMap message) {
        this.name = (String) message.get(NAME);
        this.pic = (String) message.get(PIC);
    }

    public String getName() {
        return name;
    }

    public String getPic() {
        return pic;
    }
}
