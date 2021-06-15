package com.balsa.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText editTextUserName, editTextEmail, editTextPassword;
    private Button btnRegister;
    private FirebaseAuth auth;
    private DatabaseReference myRef;

    @Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //init views
        initViews();

        //init auth and reference
        auth = FirebaseAuth.getInstance();

        //setting on click listeners for btnRegister
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUserName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                if (username.equals("") || email.equals("") || password.equals("")) {
                    Toast.makeText(RegisterActivity.this, "You need to fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(username, email, password);
                }
            }
        });

    }


    private void registerUser(final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                //checking if task is completed
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Task successful");
                    FirebaseUser user = auth.getCurrentUser();
                    //getting uid from our new user
                    String userId = user.getUid();

                    myRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    //creating hashmap in order to add to reference value
                    HashMap<String, String> registeredData = new HashMap<>();
                    registeredData.put("id", userId);
                    registeredData.put("username", username);
                    registeredData.put("imageUrl", "default");
                    registeredData.put("status", "offline");

                    myRef.setValue(registeredData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.d(TAG, "onComplete: " + task.getException().getMessage());
                                Toast.makeText(RegisterActivity.this, "Invalid registration", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to register user!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUserName = findViewById(R.id.editTextUserName);
        btnRegister = findViewById(R.id.btnRegister);
    }
}