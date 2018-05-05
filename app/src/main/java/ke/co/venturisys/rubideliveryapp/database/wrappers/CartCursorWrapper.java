package ke.co.venturisys.rubideliveryapp.database.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;

import ke.co.venturisys.rubideliveryapp.database.schemas.CartDbSchema.CartTable;
import ke.co.venturisys.rubideliveryapp.others.Meal;

public class CartCursorWrapper extends CursorWrapper {

    public CartCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Meal getMeal() {
        String icon = getString(getColumnIndex(CartTable.Cols.ICON));
        String title = getString(getColumnIndex(CartTable.Cols.TITLE));
        String details = getString(getColumnIndex(CartTable.Cols.DETAILS));
        String amount = getString(getColumnIndex(CartTable.Cols.AMOUNT));
        String price = getString(getColumnIndex(CartTable.Cols.PRICE));
        String category = getString(getColumnIndex(CartTable.Cols.CATEGORY));

        return new Meal(icon, title, details, amount, price, category, "");
    }

    public String getTitle() {
        return getString(getColumnIndex(CartTable.Cols.TITLE));
    }
}
