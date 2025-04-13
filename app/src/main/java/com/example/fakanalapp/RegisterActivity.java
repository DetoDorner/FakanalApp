package com.example.fakanalapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase inicializálás
        mAuth = FirebaseAuth.getInstance();

        // Nézetek összekötése
        emailInput = findViewById(R.id.register_emailInput);
        passwordInput = findViewById(R.id.register_passwordInput);
        registerButton = findViewById(R.id.register_button);

        // Gomb esemény
        registerButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Töltsd ki az emailt és a jelszót!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "A jelszónak legalább 6 karakterből kell állnia!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();

                            // Átirányítás vissza a bejelentkezésre
                            Intent intent = new Intent(RegisterActivity.this, EmailLoginActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(this, "Hiba: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
