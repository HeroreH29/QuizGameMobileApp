package com.example.logintest1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakeAQuiz extends AppCompatActivity {

    RadioGroup quizCategory;
    RadioButton chosenCategory;
    Button startQuizBtn;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_aquiz);

        quizCategory = findViewById(R.id.quizCategory);
        startQuizBtn = findViewById(R.id.startQuizBtn);

        // OnClickListener for Start Quiz Button
        startQuizBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                startQuiz();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startQuiz() {
        int categoryId = quizCategory.getCheckedRadioButtonId();

        chosenCategory = findViewById(categoryId);

        String category = chosenCategory.getText().toString();

        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String documentID = user.getUid();


        DocumentReference learner = db.document("Learners/" + documentID);

        learner.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String email = documentSnapshot.get("LearnersEmail").toString();
                            Boolean check = documentSnapshot.getBoolean("hasPlayed");
                            int points = documentSnapshot.getLong("LearnersScore").intValue();
                            int quizScore = 5000;

                            if (check == false) {
                                DocumentReference playerHistory = db.document("Learners/" + documentID + "/" + email + " history/" + "1");

                                Map<String, Object> history = new HashMap<>();
                                history.put("category", category);
                                history.put("dateTime", date);
                                history.put("totalPoints", quizScore);
                                history.put("totalScore", 50);

                                playerHistory.set(history)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                int totalPoints = points + quizScore;

                                                learner.update("hasPlayed", true);
                                                learner.update("LearnersScore", totalPoints);

                                                Toast.makeText(TakeAQuiz.this, "Done\n" + category + "\n" +
                                                        "\n" + date + "\n5000\n50", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), Homepage.class));
                                                finish();
                                            }
                                        });
                            }
                            else{
                                CollectionReference playerHistory = db.collection("Learners/"+documentID+"/"+email + " history");

                                Map<String, Object> history = new HashMap<>();
                                history.put("category", category);
                                history.put("dateTime", date);
                                history.put("totalPoints", quizScore);
                                history.put("totalScore", 50);

                                playerHistory.get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                int check = queryDocumentSnapshots.size();
                                                int count = check+1;

                                                int totalPoints = points + quizScore;

                                                learner.update("LearnersScore", totalPoints);

                                                playerHistory.document(String.valueOf(count)).set(history)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(TakeAQuiz.this, "Done\n" + category + "\n" +
                                                                        "\n" + date + "\n5000\n50", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(getApplicationContext(), Homepage.class));
                                                                finish();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error", "onFailure: " + e.toString());
                        return;
                    }
                });
    }
}