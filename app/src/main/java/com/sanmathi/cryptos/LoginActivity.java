package com.sanmathi.cryptos;

//Importing packages
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

//Main class
public class LoginActivity extends AppCompatActivity {

    //View variables
    private AutoCompleteTextView email;
    private AutoCompleteTextView password;
    private TextView error;
    private Button loginButton;
    private ProgressBar loadingVideo;
    private Button registerButton;
    private TextView notYetUser;

    //Firebase Authentication variable
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);

            //Referencing view variable
            registerButton = findViewById(R.id.registerButton);
            email = findViewById(R.id.email);
            password = findViewById(R.id.password);
            error = findViewById(R.id.error);
            loginButton = findViewById(R.id.loginButton);
            loadingVideo = findViewById(R.id.loadingVideo);
            notYetUser = findViewById(R.id.notYetUser);

            mAuth = FirebaseAuth.getInstance();

            //Hiding action bar
            Objects.requireNonNull(getSupportActionBar()).hide();

            //Setting click listeners for the buttons
            registerButton.setOnClickListener(v -> goRegister());
            loginButton.setOnClickListener(v -> Login());
        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    //Logging in the user with firebase
    @SuppressLint("SetTextI18n")
    private void Login(){
        //Checking whether email or password fields are empty
        setEnable(false);
        if(email.getText().toString().length()<1||password.getText().toString().length()<1) {
            if (email.getText().toString().length() < 1) {
                email.setError("Please enter your Email ID");
                setEnable(true);
            }if (password.getText().toString().length() < 1) {
                password.setError("Please enter your password");
                setEnable(true);
            }
            Toast.makeText(getApplicationContext(), "Please enter for required fields", Toast.LENGTH_LONG).show();
        }
        //Checking whether the user entered the valid email id
        else{
            //Email and password strings
            String emailString = email.getText().toString();
            String passwordString = password.getText().toString();
            //Email pattern
            String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if(!emailString.matches(EMAIL_PATTERN)){
                email.setError("Please enter valid Email ID");
                setEnable(true);
                Toast.makeText(getApplicationContext(), "Please enter valid Email ID", Toast.LENGTH_SHORT).show();
            } else {
                loginButton.setVisibility(View.GONE);
                loadingVideo.setVisibility(View.VISIBLE);
                setEnable(false);
                mAuth.signInWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra("Ex","Login");
                                        startActivity(intent);
                                        finish();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                        setEnable(true);
                                    }
                                });
                            }else {

                                setEnable(true);

                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    loginButton.setVisibility(View.VISIBLE);
                                    loadingVideo.setVisibility(View.GONE);
                                    error.setVisibility(View.VISIBLE);
                                } catch(Exception e) {
                                    error.setText("An unknown error occured");
                                    loginButton.setVisibility(View.VISIBLE);
                                    loadingVideo.setVisibility(View.GONE);
                                    error.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        }
    }

    //Function to jump for RegisterActivity
    private void goRegister(){
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    //Enabling and disabling views
    private void setEnable(boolean bool){
        email.setEnabled(bool);
        password.setEnabled(bool);
        registerButton.setEnabled(bool);

        if(bool){
            registerButton.setVisibility(View.VISIBLE);
            notYetUser.setVisibility(View.VISIBLE);
        }else{
            registerButton.setVisibility(View.GONE);
            notYetUser.setVisibility(View.GONE);
        }
    }
}

//Pending work: Nil
//STATUS: Completed