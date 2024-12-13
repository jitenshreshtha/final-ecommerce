package com.example.final_project;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Product_details extends AppCompatActivity {
    private ImageView imageView;
    private TextView titleTextView, descriptionTextView, priceTextView, quantityTextView, decreaseQuantity, increaseQuantity;
    private int quantity = 1;
    private Button addToCart, goToCart;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        // initialising views
        titleTextView = findViewById(R.id.details_title);
        descriptionTextView = findViewById(R.id.details_description);
        priceTextView = findViewById(R.id.details_price);
        imageView = findViewById(R.id.details_image);
        quantityTextView = findViewById(R.id.details_quantity);
        decreaseQuantity = findViewById(R.id.decrease_quantity);
        increaseQuantity = findViewById(R.id.increase_quantity);
        addToCart = findViewById(R.id.add_to_cart_button);
        goToCart = findViewById(R.id.go_to_cart_button);

        //getting gameId from intent
        int gameId = getIntent().getIntExtra("gameId", -1);

        // Setting up click listeners for icons
        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView cartIcon = findViewById(R.id.cart_icon);
        ImageView userIcon = findViewById(R.id.user_icon);

        // Navigate to HomeActivity when Home Icon is clicked
        homeIcon.setOnClickListener(v -> {
            Intent intent1 = new Intent(Product_details.this, Product_List.class);
            startActivity(intent1);
        });

        // Navigate to CartActivity when Cart Icon is clicked
        cartIcon.setOnClickListener(v -> {
            Intent intent2 = new Intent(Product_details.this, Cart.class);
            startActivity(intent2);
        });

        // Navigate to UserProfileActivity when User Icon is clicked
        userIcon.setOnClickListener(v -> {
            Intent intent3 = new Intent(Product_details.this, Login.class);
            startActivity(intent3);
        });

        if (gameId >= 0) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cards").child(String.valueOf(gameId));
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //fetching data
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String description = dataSnapshot.child("longDescription").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    Long price = dataSnapshot.child("price").getValue(Long.class);


                    //displaying product details
                    titleTextView.setText(title);
                    descriptionTextView.setText(description);
                    priceTextView.setText(String.valueOf(price));
                    quantityTextView.setText(String.valueOf(quantity));

                    // displaying image with glide
                    Glide.with(Product_details.this)
                            .load(imageUrl)
                            .into(imageView);

                    // Decrease quantity
                    decreaseQuantity.setOnClickListener(v -> {
                        if (quantity > 1) {
                            quantity--;
                            quantityTextView.setText(String.valueOf(quantity));
                        }
                    });

                    // Increase quantity
                    increaseQuantity.setOnClickListener(v -> {
                        quantity++;
                        quantityTextView.setText(String.valueOf(quantity));
                    });

                    addToCart.setOnClickListener(v -> {
                        addToCartMethod(title,imageUrl,price,quantity);
                    });
                    goToCart.setOnClickListener(v->{
                       Intent gotoCartIntent =  new Intent(Product_details.this, Cart.class);
                       startActivity(gotoCartIntent);
                    });

                }

                private void addToCartMethod(String title, String imageUrl, Long price, int quantity){
                    CartManager.addToCart(Product_details.this, title,imageUrl,price,quantity);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Product_details.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

}