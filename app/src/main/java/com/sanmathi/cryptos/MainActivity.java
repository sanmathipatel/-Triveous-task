package com.sanmathi.cryptos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanmathi.cryptos.Fragments.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sanmathi.cryptos.Models.User;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private Button logout;
    private ImageView search;
    private TextView title;
    boolean visible;
    BottomNavigationView bottomNavigationView;
    Fragment selecterFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            try {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            } catch (Exception e) {

            }

            //redirect if user is not null
            if (firebaseUser == null) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            } else {

                search = findViewById(R.id.search);
                visible = false;
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        title.setText("Hi, " + user.getFullname());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                bottomNavigationView = findViewById(R.id.bottom_navigation);
                logout = findViewById(R.id.logout);
                title = findViewById(R.id.title);

                bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

                getSupportActionBar().hide();

                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!visible){
                            logout.setVisibility(View.VISIBLE);
                            visible = true;
                        }else{
                            visible = false;
                            logout.setVisibility(View.GONE);
                        }
                    }
                });

                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        signOut();
                    }
                });

                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selecterFragment = new CryptosFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();

                    }
                });
            }
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }

        if(selecterFragment==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CryptosFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.currecy:

                            selecterFragment = new CryptosFragment();
                            break;

                        case R.id.favorite:
                            selecterFragment = new FavoriteFragment();
                            break;
                    }

                    if (selecterFragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , selecterFragment).commit();
                    }

                    return true;
                }
            };

    public void signOut(){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
    }
}