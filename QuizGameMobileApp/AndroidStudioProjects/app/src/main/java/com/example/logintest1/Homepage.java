package com.example.logintest1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView pnName, pnRank, pnPoints, profFirstname, profLastname, profEmail;
    ImageButton btnRanking, btnHistory;
    Button takeAQuizBtn;
    DrawerLayout drawer;
    NavigationView navView;
    androidx.appcompat.widget.Toolbar toolbar;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        pnName = findViewById(R.id.userName);
        pnPoints = findViewById(R.id.userPoints);
        pnRank = findViewById(R.id.userRank);
        btnRanking = findViewById(R.id.rankingBtn);
        btnHistory = findViewById(R.id.historyBtn);
        takeAQuizBtn = findViewById(R.id.button);



        /*For drawer layout*/
        drawer = findViewById(R.id.home_drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);
        navView.setNavigationItemSelectedListener(this);
        View header = navView.getHeaderView(0);
        profFirstname = header.findViewById(R.id.profileFirstname);
        profLastname = header.findViewById(R.id.profileLastname);
        profEmail = header.findViewById(R.id.profileEmail);

        navView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String documentID = user.getUid();

                db.collection("Learners").document(documentID)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    boolean check = documentSnapshot.getBoolean("hasPlayed");

                                    if (check) {
                                        Intent intent = new Intent(Homepage.this, RankingActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(Homepage.this, "Please take a quiz first", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });

        // OnClickListener for History Button
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // OnClickListener for Take A Quiz Button
        takeAQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TakeAQuiz.class));
            }
        });

        //Getting the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String documentID = user.getUid();

        DocumentReference userDocu = db.collection("Learners").document(documentID);

        userDocu.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    String useName = task.getResult().getString("LearnersLastname");
                    String useFirstName = task.getResult().getString("LearnersFirstname");
                    String useEmail = task.getResult().getString("LearnersEmail");
                    Long usePoints = task.getResult().getLong("LearnersScore");
                    Long useRank = task.getResult().getLong("LearnersRank");

                    String username = "Welcome, " + useName;
                    String userLastname = useName + ", ";
                    String userPoints = usePoints + " pts";
                    String userRank = "#" + useRank;

                    pnName.setText(username);
                    pnPoints.setText(userPoints);
                    pnRank.setText(userRank);
                    profLastname.setText(userLastname);
                    profEmail.setText(useEmail);
                    profFirstname.setText(useFirstName);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_cash_out:
                Intent intent = new Intent(Homepage.this, TransactActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(Homepage.this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
        }
        return true;
    }
}