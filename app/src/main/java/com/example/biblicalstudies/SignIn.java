package com.example.biblicalstudies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;

public class SignIn extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button close;
    private Button signIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        email = findViewById(R.id.sign_in_email);
        password = findViewById(R.id.sign_in_password);
        close = findViewById(R.id.sign_in_close);
        signIn = findViewById(R.id.sign_in_btn);
        mAuth = FirebaseAuth.getInstance();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate(view);
            }
        });

    }

    private void authenticate(final View view) {
        mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(view.getContext(), "Sign In Successful!",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(view.getContext(), HomeDefault.class);
                        startActivity(i);
                        finishAffinity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(view.getContext(), "Unsuccessful!",Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class Auth implements Serializable {
        FirebaseAuth mAuth;
        public Auth(FirebaseAuth mAuth){
            this.mAuth = mAuth;
        }
    }
}
