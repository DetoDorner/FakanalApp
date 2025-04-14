package com.example.fakanalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class EmailLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        // Firebase auth inicializálása
        mAuth = FirebaseAuth.getInstance();

        TextView title = findViewById(R.id.emailLogin_Title);
        Animation fancyAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_rotate_in);
        title.startAnimation(fancyAnim);

        // Nézetek összekötése (frissített ID-k)
        emailInput = findViewById(R.id.emailLogin_emailInput);
        passwordInput = findViewById(R.id.emailLogin_passwordInput);
        loginButton = findViewById(R.id.emailLogin_loginButton);

        // Gomb eseménykezelő
        loginButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Töltsd ki az emailt és jelszót!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();

                            // Navigálás a főoldalra
                            Intent intent = new Intent(EmailLoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(this, "Sikertelen bejelentkezés: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
