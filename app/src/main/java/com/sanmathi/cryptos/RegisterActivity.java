package com.sanmathi.cryptos;

//Importing packages
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

//Main class
public class RegisterActivity extends AppCompatActivity {
    //View variables
    private AutoCompleteTextView name;
    private AutoCompleteTextView email;
    private AutoCompleteTextView password;
    private AutoCompleteTextView confirmPassword;
    private Button loginButton;
    private TextView alreadyRegistered;

    private FirebaseAuth mAuth;

    private int usernameFlag;


    private Button registerButton;
    private ProgressBar loadingVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Referencing view variable
        loginButton = findViewById(R.id.loginButton);
        alreadyRegistered = findViewById(R.id.alreadyRegistered);

        registerButton = findViewById(R.id.registerButton);
        loadingVideo = findViewById(R.id.loadingVideo);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        usernameFlag = 0;

        mAuth = FirebaseAuth.getInstance();

        //Hiding action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Adding click listeners to buttons
        loginButton.setOnClickListener(v -> goLogin());
        registerButton.setOnClickListener(v -> Register());
    }

    //Function to jump for LoginActivity
    private void goLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //Register user
    private void Register() {
        //Checking whether required fields are entered or not

        setEnable(false);

        registerButton.setVisibility(View.GONE);
        loadingVideo.setVisibility(View.VISIBLE);

        if (name.getText().toString().length() < 1
                || email.getText().toString().length() < 1
                || password.getText().toString().length() < 1
                || confirmPassword.getText().toString().length() < 1) {
            if (name.getText().toString().length() < 1) {
                name.setError("Please enter your name");
                name.requestFocus();
                loadingVideo.setVisibility(View.GONE);
                registerButton.setVisibility(View.VISIBLE);
                setEnable(true);
            }
            if (email.getText().toString().length() < 1) {
                email.setError("Please enter your Email ID");
                email.requestFocus();
                loadingVideo.setVisibility(View.GONE);
                registerButton.setVisibility(View.VISIBLE);
                setEnable(true);
            }
            if (password.getText().toString().length() < 1) {
                password.setError("Please enter password");
                password.requestFocus();
                loadingVideo.setVisibility(View.GONE);
                registerButton.setVisibility(View.VISIBLE);
                setEnable(true);
            }
            if (confirmPassword.getText().toString().length() < 1) {
                confirmPassword.setError("Please enter password in Confirm password field");
                confirmPassword.requestFocus();
                loadingVideo.setVisibility(View.GONE);
                registerButton.setVisibility(View.VISIBLE);
                setEnable(true);
            }
            Toast.makeText(getApplicationContext(), "Please enter for required fields", Toast.LENGTH_LONG).show();
        }
        //Checking whether email and password matches the pattern
        else {
            String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
            //Email and password pattern
            String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if (!email.getText().toString().matches(EMAIL_PATTERN)) {
                email.setError("Please enter valid Email ID");
                email.requestFocus();
                loadingVideo.setVisibility(View.GONE);
                registerButton.setVisibility(View.VISIBLE);
                setEnable(true);
                Toast.makeText(getApplicationContext(), "Please enter valid Email ID", Toast.LENGTH_SHORT).show();
            } else if (!password.getText().toString().matches(PASSWORD_PATTERN)) {
                loadingVideo.setVisibility(View.GONE);
                registerButton.setVisibility(View.VISIBLE);
                setEnable(true);
                password.setError("Password must contain 8 characters including a lowercase and uppercase letter, a digit and a special character without spaces");
                password.requestFocus();
                Toast.makeText(getApplicationContext(), "Password must contain 8 characters including a lowercase and uppercase letter, a digit and a special character without spaces", Toast.LENGTH_SHORT).show();
            } else if (!(password.getText().toString().equals(confirmPassword.getText().toString()))) {
                confirmPassword.setError("Passwords must match");
                confirmPassword.requestFocus();
                loadingVideo.setVisibility(View.GONE);
                registerButton.setVisibility(View.VISIBLE);
                setEnable(true);
                Toast.makeText(getApplicationContext(), "Passwords must match", Toast.LENGTH_SHORT).show();
            } else {
                //View variable strings
                String nameString = name.getText().toString();
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();

                setEnable(false);
                register(nameString, emailString, passwordString);

            }
        }
    }

    //Register user to firebase
    private void register(final String fullname , String emailString , String passwordString) {
        if (usernameFlag == 0) {
            mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(RegisterActivity.this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    assert firebaseUser != null;
                    String userid = firebaseUser.getUid();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("fullname", fullname);
                    hashMap.put("bio", "");
                    hashMap.put("imageurl", "default");

                    reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                            reference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    setEnable(true);
                                }
                            });
                        }
                    });

                } else {
                    loadingVideo.setVisibility(View.GONE);
                    registerButton.setVisibility(View.VISIBLE);
                    setEnable(true);

                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch(FirebaseAuthUserCollisionException e) {
                        email.setError("Email is already registered!");
                        email.requestFocus();
                    } catch(Exception e) {
                        email.setError(e.toString());
                        email.requestFocus();
                    }
                    Toast.makeText(RegisterActivity.this, "You can't register with this email and password!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            loadingVideo.setVisibility(View.GONE);
            registerButton.setVisibility(View.VISIBLE);
            setEnable(true);
            Toast.makeText(this, "Please change the username!", Toast.LENGTH_SHORT).show();
        }
    }


    //To enable and disable views
    private void setEnable(boolean bool) {
        name.setEnabled(bool);
        email.setEnabled(bool);
        password.setEnabled(bool);
        confirmPassword.setEnabled(bool);
        loginButton.setEnabled(bool);

        if(bool){
            loginButton.setVisibility(View.VISIBLE);
            alreadyRegistered.setVisibility(View.VISIBLE);
        }else{
            loginButton.setVisibility(View.GONE);
            alreadyRegistered.setVisibility(View.GONE);
        }
    }
}

//Pending work : Nil
//STATUS: Finished
