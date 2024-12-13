package com.example.final_project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.text.BreakIterator;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.titleTextView.setText(product.getTitle());
        holder.descriptionTextView.setText(product.getDescription());
        holder.priceView.setText(String.valueOf(product.getPrice()));

        //using glide to load image from db
        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.imageView);

        //set click listener
        holder.itemView.setOnClickListener(view ->{
            Intent intent = new Intent(context,Product_details.class);
            intent.putExtra("gameId", position+1);
            context.startActivity(intent);
        });

        // Add to cart button functionality
        holder.addToCartButton.setOnClickListener(view -> {
            holder.addToCartMethod(
                    product.getTitle(),
                    product.getImageUrl(),
                    product.getPrice(),
                    1
            );
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView, descriptionTextView, priceView;
        ImageView imageView;
        Button addToCartButton;

        public ProductViewHolder(View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.card_title);
            descriptionTextView = itemView.findViewById(R.id.card_description);
            imageView = itemView.findViewById(R.id.card_image);
            priceView = itemView.findViewById(R.id.card_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
        private void addToCartMethod(String title, String imageUrl, Long price, int quantity) {
            CartManager.addToCart(itemView.getContext(), title, imageUrl, price, quantity);
            Toast.makeText(itemView.getContext(), title + " added to cart", Toast.LENGTH_SHORT).show();
        }
    }

}
