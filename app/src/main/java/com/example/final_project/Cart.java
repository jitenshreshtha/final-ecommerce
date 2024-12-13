package com.example.final_project;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private List<Product> cartItemList;
    private cartAdapter cartAdapter;
    private DatabaseReference userCartReference;
    private TextView total_price, grandtotal_price;
    private Button checkout_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // getting the current user's id
        String userId = mAuth.getCurrentUser().getUid();

        // setting recyclerview
        recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        cartItemList = new ArrayList<>();
        cartAdapter = new cartAdapter(cartItemList, this);
        recyclerView.setAdapter(cartAdapter);


        //referencing to the user's cart in the firebase db
        userCartReference = FirebaseDatabase.getInstance().getReference("carts").child(userId);

        //initialising views for totals
        total_price = findViewById(R.id.total_price);
        grandtotal_price = findViewById(R.id.grandtotal_price);

        checkout_button = findViewById(R.id.checkout_button);
        checkout_button.setOnClickListener(v->{
            Intent newIntent = new Intent(Cart.this, Checkout.class);
            startActivity(newIntent);
        });

        //fetch cart items from the database
        fetchCartItems();

        // Setting up click listeners for icons
        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView cartIcon = findViewById(R.id.cart_icon);
        ImageView userIcon = findViewById(R.id.user_icon);

        // Navigate to HomeActivity when Home Icon is clicked
        homeIcon.setOnClickListener(v -> {
            Intent intent1 = new Intent(Cart.this, Product_List.class);
            startActivity(intent1);
        });

        // Navigate to CartActivity when Cart Icon is clicked
        cartIcon.setOnClickListener(v -> {
            Intent intent2 = new Intent(Cart.this, Cart.class);
            startActivity(intent2);
        });

        // Navigate to UserProfileActivity when User Icon is clicked
        userIcon.setOnClickListener(v -> {
            Intent intent3 = new Intent(Cart.this, Login.class);
            startActivity(intent3);
        });

    }

    private void fetchCartItems() {
        userCartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartItemList.clear();
                double totalPrice = 0.0;

                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String imageUrl = productSnapshot.child("imageUrl").getValue(String.class);
                    String title = productSnapshot.child("title").getValue(String.class);
                    Long quantity = productSnapshot.child("quantity").getValue(Long.class);
                    Double price = productSnapshot.child("price").getValue(Double.class);

                    if (imageUrl != null && title != null ) {
                        cartItemList.add(new Product(title, imageUrl, price, quantity));
                        totalPrice+= price*quantity;
                    }
                }
                cartAdapter.notifyDataSetChanged();
                //updating totals
                updateTotals(totalPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Cart.this, "failed to load cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotals(double totalPrice){
        double tax = totalPrice *0.13;
        double grandTotal = totalPrice + tax;

        // Format the values to two decimal places
        String formattedTotalPrice = String.format("%.2f", totalPrice);
        String formattedGrandTotal = String.format("%.2f", grandTotal);

        // Set the formatted values to the TextViews
        total_price.setText(formattedTotalPrice);
        grandtotal_price.setText(formattedGrandTotal);
    }


}