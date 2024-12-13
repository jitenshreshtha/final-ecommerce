package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Product_List extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("cards");

        FirebaseApp.initializeApp(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Fetching data from database
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Product> productList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                }

                // Setting the adapter
                ProductAdapter adapter = new ProductAdapter( productList,Product_List.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Product_List.this, "Error loading data from database", Toast.LENGTH_SHORT).show();
            }
        });

        // Setting up click listeners for icons
        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView cartIcon = findViewById(R.id.cart_icon);
        ImageView userIcon = findViewById(R.id.user_icon);

        // Navigate to HomeActivity when Home Icon is clicked
        homeIcon.setOnClickListener(v -> {
            Intent intent1 = new Intent(Product_List.this, Product_List.class);
            startActivity(intent1);
        });

        // Navigate to CartActivity when Cart Icon is clicked
        cartIcon.setOnClickListener(v -> {
            Intent intent2 = new Intent(Product_List.this, Cart.class);
            startActivity(intent2);
        });

        // Navigate to UserProfileActivity when User Icon is clicked
        userIcon.setOnClickListener(v -> {
            Intent intent3 = new Intent(Product_List.this, Login.class);
            startActivity(intent3);
        });
    }
}