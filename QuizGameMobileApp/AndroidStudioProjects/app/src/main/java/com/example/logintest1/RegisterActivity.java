package com.example.logintest1;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    //Auth Import
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    Button btnRegister;
    TextView btnReturnLogin, warning;
    EditText regFirstname, regLastname, regEmail, regPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //XML Imports
        btnRegister = findViewById(R.id.btnRegister);
        btnReturnLogin = findViewById(R.id.btnLogin);
        regFirstname = findViewById(R.id.etFirstName);
        regLastname = findViewById(R.id.etLastname);
        regEmail = findViewById(R.id.etEmail);
        regPassword = findViewById(R.id.etPassword);
        warning = findViewById(R.id.passowordwarn);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Register User
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        //Return to login
        btnReturnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void registerUser(){
        String rgFirstname = regFirstname.getText().toString();
        String rgLastname = regLastname.getText().toString();
        String rgEmail = regEmail.getText().toString().trim();
        String rgPassword = regPassword.getText().toString();


        if(rgFirstname.isEmpty() || rgLastname.isEmpty() || rgEmail.isEmpty() || rgPassword.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Please Fill up the form first before register", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rgPassword.length() <= 6){
            warning.setVisibility(View.VISIBLE);
        }
        auth.createUserWithEmailAndPassword(rgEmail, rgPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Setting data to firestore database
                            Map<String, Object> user = new HashMap<>();
                            user.put("LearnersFirstname", rgFirstname);
                            user.put("LearnersLastname", rgLastname);
                            user.put("LearnersEmail", rgEmail);
                            //user.put("LearnersPassword", rgPassword);
                            user.put("hasPlayed", false);
                            //user.put("LearnersScore", 0);
                            user.put("LearnersRank", 0);
                            String UID = auth.getCurrentUser().getUid();
                            db.collection("Learners").document(UID)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            DocumentReference documentReference = db.document("Learners/"+UID);

                                            documentReference.get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    if (documentSnapshot.exists()) {
                                                                        String email = documentSnapshot.get("LearnersEmail").toString();

                                                                        Map<String, Object> hist = new HashMap<>();
                                                                        hist.put("none", "none");

                                                                        documentReference.collection(email + " history").document("1")
                                                                                .set(hist)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                                        startActivity(intent);
                                                                                        finish();
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    });
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Register Error! Please Try Again", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }
}