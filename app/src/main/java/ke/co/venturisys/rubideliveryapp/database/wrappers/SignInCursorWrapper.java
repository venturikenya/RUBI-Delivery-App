package ke.co.venturisys.rubideliveryapp.database.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;

import ke.co.venturisys.rubideliveryapp.database.schemas.SignInDbSchema.SignInTable;

/**
 * Created by victor on 12/04/2018.
 * Class that wraps a cursor
 */

public class SignInCursorWrapper extends CursorWrapper {

    public SignInCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getEmail() {
        return getString(getColumnIndex(SignInTable.Cols.EMAIL));
    }
}
