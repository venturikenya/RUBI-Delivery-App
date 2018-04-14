package ke.co.venturisys.rubideliveryapp.database.schemas;

public class OrderDbSchema {

    /**
     * Describes the table
     */
    public static final class OrderTable {
        // Name of table in database
        public static final String NAME = "ordered";

        /**
         * Describes the columns
         */
        public static final class Cols {
            public static final String ORDER_ID = "order_id";
            public static final String NAME = "name";
            public static final String AMOUNT = "amount";
            public static final String PRICE = "price";
            public static final String TIMESTAMP = "timestamp";
        }
    }

}
