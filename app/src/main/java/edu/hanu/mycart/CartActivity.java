package edu.hanu.mycart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.hanu.mycart.adapter.CartAdapter;
import edu.hanu.mycart.models.Cart;
import edu.hanu.mycart.models.Product;
import edu.hanu.mycart.models.ProductDetail;
import edu.hanu.mycart.repository.CartRepository;

public class CartActivity extends AppCompatActivity {

    Cart cart;
    public static CartAdapter cartAdapter = new CartAdapter();
    RecyclerView cartRcv;
    TextView totalAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cart = Cart.getInstance();
        totalAmount = findViewById(R.id.total_amount);
        FormatHelper formatHelper = FormatHelper.getFormatHelper();

        //clear data trong cart list
        cart.getProductDetailList().clear();
        cartAdapter.setContext(this);
        cartRcv = findViewById(R.id.rcv_cart);
        //set ngay cart cho on create đầu tiên
        cartRcv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        //list của cart query tất cả trong database mỗi khi start a new activity
        all();
        totalAmount.setText(formatHelper.format(cart.TinhTien()));


        cartRcv.setAdapter(cartAdapter);
        cartAdapter.setData(cart, totalAmount);


    }

    private void all() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cart", null);
        int idIndex = c.getColumnIndex("id");
        int titleIndex = c.getColumnIndex("title");
        int productIdIndex = c.getColumnIndex("productId");
        int imgIndex = c.getColumnIndex("image");
        int priceIndex = c.getColumnIndex("price");
        int quantityIndex = c.getColumnIndex("quantity");
        while (c.moveToNext()) {
            int id = c.getInt(idIndex);
            String title = c.getString(titleIndex);
            int productId = c.getInt(productIdIndex);

            //convert byte array to bitmap
            byte[] image = c.getBlob(imgIndex);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

            int price = c.getInt(priceIndex);
            int quantity = c.getInt(quantityIndex);

            Product product = new Product(productId, bitmap, title, price);
            ProductDetail productDetail = new ProductDetail(id, product, quantity, price * quantity);
            Log.i("detail", productDetail.toString());

            cart.getProductDetailList().add(productDetail);

        }
        c.close();
        db.close();
    }
}