package com.example.final_project;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CartManager {
    public static void addToCart(Context context, String title, String imageUrl, Long price, int quantity) {
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get the database reference for the user's products
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("carts").child(userUID).child(title);


        // Check if the product already exists in the cart
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Product exists, update the quantity
                    CartItem existingItem = dataSnapshot.getValue(CartItem.class);
                    if (existingItem != null) {
                        int updatedQuantity = existingItem.getQuantity() + quantity;

                        if (updatedQuantity > 10) {
                            Toast.makeText(context, "Cart limit reached", Toast.LENGTH_SHORT).show();
                            updatedQuantity = 10;
                        } else if (updatedQuantity < 1) {
                            updatedQuantity = 1;
                        }

                        // Update the quantity in the database
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("quantity", updatedQuantity);

                        databaseReference.updateChildren(updates)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(context, "Cart updated!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Failed to update cart", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // Product does not exist, create a new entry
                    Map<String, Object> productDetails = new HashMap<>();
                    productDetails.put("title", title);
                    productDetails.put("imageUrl", imageUrl);
                    productDetails.put("price", price);
                    productDetails.put("quantity", quantity);

                    // Add the new product to the database
                    databaseReference.setValue(productDetails)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(context, "Failed to check cart", Toast.LENGTH_SHORT).show();
            }
        });
}

    // Cart item model
    public static class CartItem {
        private String title;
        private String imageUrl;
        private Long price;
        private int quantity;

        // Empty constructor for Firebase
        public CartItem() {
        }

        public CartItem(String title, String imageUrl, Long price, int quantity) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.price = price;
            this.quantity = quantity;
        }

        // Getters and setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

}
