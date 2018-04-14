package ke.co.venturisys.rubideliveryapp.database.schemas;

public class CartDbSchema {

    /*
     * This table describes the cart table
     */
    public static final class CartTable {
        public static final String NAME = "cart";

        /*
         * This table describes the table's columns
         */
        public static final class Cols {
            public static final String ICON = "icon";
            public static final String TITLE = "title";
            public static final String DETAILS = "details";
            public static final String PRICE = "price";
            public static final String AMOUNT = "amount";
            public static final String CATEGORY = "category";
            public static final String ORDER_NUMBER = "order_number";
            public static final String TIMESTAMP = "timestamp";
        }
    }

}
