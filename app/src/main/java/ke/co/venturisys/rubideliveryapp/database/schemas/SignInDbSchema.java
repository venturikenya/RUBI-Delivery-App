package ke.co.venturisys.rubideliveryapp.database.schemas;

/**
 * Created by victor on 12/04/2018.
 * Created to define table and columns in signing in db
 */

public class SignInDbSchema {
    /**
     * Describes the table
     */
    public static final class SignInTable {
        // Name of table in database
        public static final String NAME = "signing_in";

        /**
         * Describes the columns
         */
        public static final class Cols {
            public static final String EMAIL = "email";
            public static final String NAME = "name";
        }
    }
}
