package com.example.final_project;

import android.content.Context;
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
import com.example.final_project.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.BreakIterator;
import java.util.List;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.CartViewHolder> {

    private List<Product> cartItems;
    private Context context;

    // Constructor
    public cartAdapter(List<Product> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartItems.get(position);

        holder.titleTextView.setText(product.getTitle());
        holder.priceTextView.setText(String.valueOf(product.getPrice()));
        holder.quantityTextView.setText(String.valueOf(product.getQuantity()));

        //calculating and displaying the total for the product
        double total = product.getPrice() * product.getQuantity();
        holder.totalTextView.setText(String.format("$%.2f",total));

        // Use Glide to load the image
        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.imageView);

        // Set click listener for remove button
        holder.removeButton.setOnClickListener(v -> {
            // Remove item from Firebase database
            removeItemFromDatabase(product);

            // Remove item from the list and notify adapter
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }


    private void removeItemFromDatabase(Product product){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userCartReference = FirebaseDatabase.getInstance().getReference("carts").child(userId);
        // Assuming the title is unique; otherwise, use a unique key
        userCartReference.orderByChild("title").equalTo(product.getTitle())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public static class CartViewHolder extends RecyclerView.ViewHolder {
       TextView titleTextView, priceTextView, quantityTextView,totalTextView;
       ImageView imageView;
       Button removeButton;


        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.product_title);
            imageView = itemView.findViewById(R.id.product_image);
            priceTextView = itemView.findViewById(R.id.product_price);
            quantityTextView = itemView.findViewById(R.id.product_quantity);
            totalTextView = itemView.findViewById(R.id.product_total);
            removeButton = itemView.findViewById(R.id.remove_button);

        }
    }
}
