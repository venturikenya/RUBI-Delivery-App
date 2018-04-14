package ke.co.venturisys.rubideliveryapp.database.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ke.co.venturisys.rubideliveryapp.database.schemas.CartDbSchema.CartTable;

/*
 * Check to see if the database already exists.
 * If it does not, create it and create the tables and initial data it needs.
 * If it does, open it up and see what version of your CrimeDbSchema it has.
 * If it is an old version, run code to upgrade it to a newer version.
 */

public class CartBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "cartBase.db";

    public CartBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    // Create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CartTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CartTable.Cols.ICON + ", " +
                CartTable.Cols.TITLE + " unique, " +
                CartTable.Cols.DETAILS + ", " +
                CartTable.Cols.PRICE + ", " +
                CartTable.Cols.AMOUNT + ", " +
                CartTable.Cols.CATEGORY + ", " +
                CartTable.Cols.TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                CartTable.Cols.ORDER_NUMBER +
                ")");
    }

    // Upgrading databases
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + CartTable.NAME);

        // Create tables again
        onCreate(db);
    }
}
