package ke.co.venturisys.rubideliveryapp.database.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ke.co.venturisys.rubideliveryapp.database.schemas.OrderDbSchema.OrderTable;

public class OrderBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "orderBase.db";

    public OrderBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + OrderTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                OrderTable.Cols.ORDER_ID + ", " +
                OrderTable.Cols.NAME + ", " +
                OrderTable.Cols.AMOUNT + ", " +
                OrderTable.Cols.PRICE + ", " +
                OrderTable.Cols.TIMESTAMP +
                ")");
    }

    // Upgrading databases
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + OrderTable.NAME);

        // Create tables again
        onCreate(db);
    }
}
