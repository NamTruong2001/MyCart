package edu.hanu.mycart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import edu.hanu.mycart.FormatHelper;
import edu.hanu.mycart.MainActivity;
import edu.hanu.mycart.R;
import edu.hanu.mycart.models.Cart;
import edu.hanu.mycart.models.Product;
import edu.hanu.mycart.models.ProductDetail;
import edu.hanu.mycart.repository.CartRepository;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    private Cart cart;
    private TextView tv;
    private Context mContext;

    public CartAdapter() {}

    public void setContext(Context context) {
        this.mContext = context;
    }
    public void setData(Cart cart, TextView tv) {
        this.cart = cart;
        this.tv = tv;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_detail, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        CartRepository cartRepository = CartRepository.getInstance(mContext);
        ProductDetail productDetail = cart.getProductDetailList().get(holder.getAdapterPosition());

        String sum = FormatHelper.getFormatHelper().format(productDetail.getProduct().getUnitPrice() * productDetail.getQuantity());
        holder.title.setText(productDetail.getProduct().getName());
        holder.img.setImageBitmap(productDetail.getProduct().getThumbnail());
        holder.price.setText(FormatHelper.getFormatHelper().format(productDetail.getProduct().getUnitPrice()));
        holder.quantity.setText(productDetail.getQuantity() + "");
        holder.sum.setText(sum);

        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productDetail.setQuantity(productDetail.getQuantity() + 1);
                tv.setText(cart.TinhTien() + "");
                cartRepository.update(productDetail);
                holder.quantity.setText(productDetail.getQuantity() +"");
                holder.sum.setText(FormatHelper.getFormatHelper().format(productDetail.getProduct().getUnitPrice() * productDetail.getQuantity()));
                tv.setText(FormatHelper.getFormatHelper().format(cart.TinhTien()));
                notifyDataSetChanged();
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = productDetail.getQuantity() - 1;
                if (quantity == 0) {
                    cart.getProductDetailList().remove(holder.getAdapterPosition());
                    cartRepository.delete(productDetail.getId());
                } else {
                    productDetail.setQuantity(quantity);
                    cartRepository.update(productDetail);

                    holder.quantity.setText(productDetail.getQuantity() + "");
                    String sum = productDetail.getProduct().getUnitPrice() * productDetail.getQuantity() + "";
                    holder.sum.setText(sum);
                }
                tv.setText(FormatHelper.getFormatHelper().format(cart.TinhTien()));
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return cart.getProductDetailList().size();
    }


    public class CartHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView img;
        private TextView quantity;
        private TextView price;
        private ImageButton increase;
        private ImageButton decrease;
        private TextView sum;

        public CartHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.product_detail_title);
            img = itemView.findViewById(R.id.product_detail_img);
            quantity = itemView.findViewById(R.id.product_detail_quantity);
            price = itemView.findViewById(R.id.product_detail_price);
            increase = itemView.findViewById(R.id.product_detail_increase);
            decrease = itemView.findViewById(R.id.product_detail_decrease);
            sum = itemView.findViewById(R.id.product_detail_sum);
        }
    }
}
