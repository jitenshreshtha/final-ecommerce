package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Checkout extends AppCompatActivity {
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);


        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get the database reference for the user's products
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("carts").child(userUID);

        //initialising awesome validation with basic style
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);



        // Setting validation rules for all fields
        awesomeValidation.addValidation(this, R.id.first_name, "^(?!\\s*$)[A-Za-z]+([ '-][A-Za-z]+)*$", R.string.error_first_name_invalid);
        awesomeValidation.addValidation(this, R.id.last_name, "^(?!\\s*$)[A-Za-z]+([ '-][A-Za-z]+)*$", R.string.error_last_name_invalid);
        awesomeValidation.addValidation(this, R.id.mailing_address, "^[a-zA-Z0-9\\s,.'-]{3,}$", R.string.error_mailing_address_invalid);
        awesomeValidation.addValidation(this, R.id.email_address, android.util.Patterns.EMAIL_ADDRESS, R.string.error_invalid_email);
        awesomeValidation.addValidation(this, R.id.phone_number, "^\\d{10}$", R.string.error_phone);


        RadioGroup paymentOptions = findViewById(R.id.payment_options);
        // Submit button click listener
        Button submitOrder = findViewById(R.id.submit_order);
        submitOrder.setOnClickListener(view -> {
            // First, validate all form fields
            boolean isFormValid = awesomeValidation.validate();
            // Check if any payment option is selected
            int selectedPaymentOption = paymentOptions.getCheckedRadioButtonId();
            if (selectedPaymentOption == -1) {
                // No payment option is selected
                Toast.makeText(this, "Please select a payment option.", Toast.LENGTH_SHORT).show();
            }

            // Combine both validations: form fields and payment selection
            if (isFormValid && selectedPaymentOption != -1) {
                Toast.makeText(this, "Validation Successful!", Toast.LENGTH_SHORT).show();

                // Clear the cart from the database
                databaseReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Cart cleared successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to clear cart: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                // Navigate to the Thank You page
                Intent newIntent = new Intent(Checkout.this, Thankyou.class);
                startActivity(newIntent);
            } else {
                // If form is invalid or payment option not selected, show general error
                if (!isFormValid) {
                    Toast.makeText(this, "Validation Failed. Please check your inputs.", Toast.LENGTH_SHORT).show();
                }
                if (selectedPaymentOption == -1) {
                    Toast.makeText(this, "Please select a payment option.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //getting the references to the RadioGroup and related layouts
        LinearLayout creditCardFields = findViewById(R.id.credit_card_fields);
        LinearLayout debitCardFields = findViewById(R.id.debit_card_fields);
        LinearLayout paymentPortalFields = findViewById(R.id.payment_portal_fields);

        paymentOptions.setOnCheckedChangeListener((group, checkedId) -> {
            awesomeValidation.clear();
            if (checkedId == R.id.credit_card_option) {
                creditCardFields.setVisibility(View.VISIBLE);
                debitCardFields.setVisibility(View.GONE);
                paymentPortalFields.setVisibility(View.GONE);

                // Add validation rules for credit card fields
                awesomeValidation.addValidation(this, R.id.credit_card_fullname, "^(?!\\s*$)[A-Za-z]+([ '-][A-Za-z]+)*$", R.string.error_credit_card_fullname);
                awesomeValidation.addValidation(this, R.id.credit_card_number, "^[0-9]{16}$", R.string.error_credit_card_number);
                awesomeValidation.addValidation(this, R.id.credit_card_expiry, "^(0[1-9]|1[0-2])\\/([0-9]{2})$", R.string.error_credit_card_expiry);
                awesomeValidation.addValidation(this, R.id.credit_card_cvv, "^[0-9]{3,4}$", R.string.error_credit_card_cvv);
            } else if (checkedId == R.id.debit_card_option){
                debitCardFields.setVisibility(View.VISIBLE);
                creditCardFields.setVisibility(View.GONE);
                paymentPortalFields.setVisibility(View.GONE);

                // Add validation rules for debit card fields
                awesomeValidation.addValidation(this, R.id.debit_card_fullname, "^(?!\\s*$)[A-Za-z]+([ '-][A-Za-z]+)*$", R.string.error_debit_card_fullname);
                awesomeValidation.addValidation(this, R.id.debit_card_number, "^[0-9]{16}$", R.string.error_debit_card_number);
                awesomeValidation.addValidation(this, R.id.debit_card_expiry, "^(0[1-9]|1[0-2])\\/([0-9]{2})$", R.string.error_debit_card_expiry);
                awesomeValidation.addValidation(this, R.id.debit_card_cvv, "^[0-9]{3,4}$", R.string.error_debit_card_cvv);
            }
            else if (checkedId == R.id.payment_portal_option){
                paymentPortalFields.setVisibility(View.VISIBLE);
                creditCardFields.setVisibility(View.GONE);
                debitCardFields.setVisibility(View.GONE);

                // Payment portal options validation (if applicable)
                awesomeValidation.addValidation(this, R.id.payment_portal_options, RegexTemplate.NOT_EMPTY, R.string.error_payment_portal);
            }
            else {
                creditCardFields.setVisibility(View.GONE);
                debitCardFields.setVisibility(View.GONE);
                paymentPortalFields.setVisibility(View.GONE);
            }
        });
    }
}