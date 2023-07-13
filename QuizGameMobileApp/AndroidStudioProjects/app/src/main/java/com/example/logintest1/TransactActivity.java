package com.example.logintest1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class TransactActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    NavigationView navView;
    androidx.appcompat.widget.Toolbar toolbar;
    TextView profFirstname, profLastname, profEmail,transactPts;
    Button btnRequestTransact;
    String useEmail, useLName, useFName, usePassword;
    Long usePoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transact);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        drawer = findViewById(R.id.home_drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.nav_toolbar);
        btnRequestTransact = findViewById(R.id.requestCash);
        setSupportActionBar(toolbar);
        navView.setNavigationItemSelectedListener(this);
        View header = navView.getHeaderView(0);
        profFirstname =  header.findViewById(R.id.profileFirstname);
        profLastname = header.findViewById(R.id.profileLastname);
        profEmail = header.findViewById(R.id.profileEmail);
        transactPts = findViewById(R.id.transactPoints);

        navView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String documentID = user.getUid();

        DocumentReference userDocu = db.collection("Learners").document(documentID);

        userDocu.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    useLName = task.getResult().getString("LearnersLastname");
                    useFName = task.getResult().getString("LearnersFirstname");
                    useEmail = task.getResult().getString("LearnersEmail");
                    usePassword = task.getResult().getString("LearnersPassword");
                    usePoints = task.getResult().getLong("LearnersScore");

                    String userPoints = usePoints.toString();

                    String userLastname = useLName + ", ";
                    transactPts.setText(userPoints);
                    profLastname.setText(userLastname);
                    profEmail.setText(useEmail);
                    profFirstname.setText(useFName);
                }
            }
        });
        btnRequestTransact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userEmail = useEmail;
                final String userPass = usePassword;
                final String adminEmail = "quizlearn42gmail.com";
                String umPoints = usePoints.toString();
                String messageRequest = "From: " + userEmail + "\n\n" +
                                        "Learner " + useLName + ", " + useFName + " wants to request a transaction exchange\n"+
                                        "Please provide and send a clear transaction to Learner " + useLName + "\n\n" +
                                        "Credentials: \n\n" +
                                        "Email: " + userEmail + "\n" +
                                        "Name: " + useLName + ", " + useFName + "\n" +
                                        "Points: " + umPoints;
                Properties prop = new Properties();
                prop.put("mail.smtp.auth", "true");
                prop.put("mail.smtp.starttls.enable", "true");
                prop.put("mail.smtp.host", "smtp.gmail.com");
                prop.put("mail.smtp.port", "587");

                Session session = Session.getInstance(prop,
                        new javax.mail.Authenticator(){
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(userEmail, userPass);
                            }
                        });
                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(userEmail));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(adminEmail));
                    message.setSubject("Transaction Request");
                    message.setText(messageRequest);
                    Transport.send(message);
                    Toast.makeText(TransactActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }catch (MessagingException e){
                    throw new RuntimeException();
                }
            }
        });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.nav_home:
                Intent intent = new Intent(TransactActivity.this, Homepage.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_cash_out:
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(TransactActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
        }
        return true;
    }
}