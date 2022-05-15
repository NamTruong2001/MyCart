package edu.hanu.mycart.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import edu.hanu.mycart.DBHelper;
import edu.hanu.mycart.models.ProductDetail;

public class CartRepository {
    private static CartRepository instance;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private CartRepository(Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public static CartRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CartRepository(context);
        }
        return instance;
    }
    public boolean insert(ProductDetail productDetail) {
        ContentValues cv = new ContentValues();
        cv.put("productId", productDetail.getProduct().getId());
        cv.put("title", productDetail.getProduct().getName());
        cv.put("image", getBitmapAsByteArray(productDetail.getProduct().getThumbnail()));
        cv.put("price", productDetail.getProduct().getUnitPrice());
        cv.put("quantity", productDetail.getQuantity());
        long items = db.insert("cart", null, cv);
        if (items > 0) {
            productDetail.setId((int) items);
            return true;
        } else {
            return false;
        }

    }

    public boolean update(ProductDetail productDetail) {
        ContentValues cv = new ContentValues();
        //cv.put("price", productDetail.getProduct().getUnitPrice());
        cv.put("quantity", productDetail.getQuantity());
        //cv.put("total", productDetail.getProduct().getUnitPrice() * productDetail.getQuantity());
        int items = db.update("cart", cv, "id = ?", new String[]{productDetail.getId() + ""});
        if (items > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean delete(int id) {
        String query = "DELETE FROM cart WHERE id = " + id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }

    }


    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

}
