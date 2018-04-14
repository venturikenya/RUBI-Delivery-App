package ke.co.venturisys.rubideliveryapp.database.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ke.co.venturisys.rubideliveryapp.database.schemas.SignInDbSchema.SignInTable;

/**
 * Created by victor on 12/04/2018.
 * 1. Checks whether database exists.
 * 2. Create it and put initial data if it doesn't
 * 3. Open and check version of SignInDbSchema if exists
 * 4. Upgrade to new version if old
 */

public class SignBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "signInDb.db";

    public SignBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    // Creating tables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + SignInTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                SignInTable.Cols.EMAIL + " unique, " +
                SignInTable.Cols.NAME +
                ")"
        );
    }

    // Upgrading databases
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + SignInTable.NAME);

        // Create tables again
        onCreate(db);
    }
}
