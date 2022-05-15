package edu.hanu.mycart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import edu.hanu.mycart.adapter.ProductAdapter;
import edu.hanu.mycart.models.Cart;
import edu.hanu.mycart.models.Product;
import edu.hanu.mycart.models.ProductDetail;

//Lưu cart items trong database
public class MainActivity extends AppCompatActivity {
    List<Product> products;
    RecyclerView rcv;
    androidx.appcompat.widget.SearchView searchView;
    Cart cart;


    public class ProductDownloader extends AsyncTask<String, Void, List<Product>> {

        @Override
        protected List<Product> doInBackground(String... strings) {
            List<Product> products = new ArrayList<>();
            try {
                URL url = new URL(strings[0]);
                String productsJson = getURLContent(url);
                JSONArray jsonArray = new JSONArray(productsJson);
                int numberOfPros = jsonArray.length();
                for (int i = 0; i < numberOfPros; i++) {
                    JSONObject proJson = jsonArray.getJSONObject(i);
                    int id = proJson.getInt("id");
                    String name = proJson.getString("name");
                    int price = proJson.getInt("unitPrice");

                    URL imageURL = new URL(proJson.getString("thumbnail"));
                    Bitmap bitmap = getURLImage(imageURL);
                    Product product = new Product(id, bitmap, name, price);

                    products.add(product);
                }
            } catch (Exception e) {
                //Log.i("JSON", "EROOR");
                e.printStackTrace();
            }


            return products;
        }

        private String getURLContent(URL url) {
            StringBuilder result = new StringBuilder();
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                int data;
                while ((data = isr.read()) != -1) {
                    result.append((char) data);
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private Bitmap getURLImage(URL url) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream isr = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(isr);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //CartActivity.cart.setProductDetailList(CartActivity.productDetailList);
        //tạo cart
        cart = Cart.getInstance();
        //data trong cart sẽ có ngay từ all trong database
        //tiến hành chạy đồng thời list trong cart với trong database
        all();
        searchView = findViewById(R.id.searchView);

        ProductDownloader productDownloader = new ProductDownloader();
        try {
            products = productDownloader.execute("https://mpr-cart-api.herokuapp.com/products").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        rcv = findViewById(R.id.main_rcv);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        ProductAdapter productAdapter = new ProductAdapter(this);
        rcv.setLayoutManager(gridLayoutManager);
        rcv.setAdapter(productAdapter);
        productAdapter.setData(products);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cart_nav, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_viewcart:
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
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

            cart.getProductDetailList().add(productDetail);

        }
        c.close();
        db.close();
    }
}
