package edu.hanu.mycart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.hanu.mycart.CartActivity;
import edu.hanu.mycart.FormatHelper;
import edu.hanu.mycart.MainActivity;
import edu.hanu.mycart.R;
import edu.hanu.mycart.models.Cart;
import edu.hanu.mycart.models.Product;
import edu.hanu.mycart.models.ProductDetail;
import edu.hanu.mycart.repository.CartRepository;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {
    private List<Product> products;
    private List<Product> productsTemp;
    private Context mContext;

    public ProductAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Product> products) {
        this.products = products;
        this.productsTemp = this.products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Cart cart = Cart.getInstance();

        CartRepository cartRepository = CartRepository.getInstance(mContext);
        Product product = products.get(holder.getAdapterPosition());
        holder.title.setText(product.getName());
        holder.img.setImageBitmap(product.getThumbnail());
        holder.price.setText(FormatHelper.getFormatHelper().format(product.getUnitPrice()));
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //nếu cart chưa có product này
                if (cart.hasProduct(product) == null) {
                    ProductDetail productDetail = new ProductDetail();
                    productDetail.setProduct(product);
                    productDetail.setQuantity(1);
                    productDetail.setTotal(product.getUnitPrice());
                    cart.getProductDetailList().add(productDetail);


                    cartRepository.insert(productDetail);

                    Toast.makeText(mContext, "Add to cart successfully", Toast.LENGTH_SHORT).show();
                } else {
                    //nếu cart đã có product này
                    ProductDetail thisPro = cart.hasProduct(product);
                    thisPro.setQuantity(thisPro.getQuantity() + 1);

                    cartRepository.update(thisPro);
                    Toast.makeText(mContext, "Add to an already exist item in the cart", Toast.LENGTH_SHORT).show();
                }
                CartActivity.cartAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                //nếu thanh search empty -> trả về list product ban đầu
                if (strSearch.isEmpty()) {
                    products = productsTemp;
                } else {
                    //nếu ko -> cho tất cả product có chứa kí tự vào trong 1 list mới
                    List<Product> list = new ArrayList<>();
                    for (Product product : productsTemp) {
                        if (product.getName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(product);
                        }
                    }
                    //thay đổi tham chiếu của product
                    products = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = products;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
               products = (List<Product>) results.values;
               notifyDataSetChanged();
            }
        };
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView title;
        private TextView price;
        private ImageButton addToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.product_img);
            title = itemView.findViewById(R.id.product_title);
            price = itemView.findViewById(R.id.product_price);
            addToCart = itemView.findViewById(R.id.product_add);
        }
    }
}
