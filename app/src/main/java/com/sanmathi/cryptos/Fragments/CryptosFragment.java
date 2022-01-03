package com.sanmathi.cryptos.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanmathi.cryptos.Adapters.CoinsAdapter;
import com.sanmathi.cryptos.Models.FavoriteCoins;
import com.sanmathi.cryptos.R;

import java.util.ArrayList;
import java.util.List;

public class CryptosFragment extends Fragment {

    private RecyclerView recyclerView;
    private CoinsAdapter coinsAdapter;
    private List<FavoriteCoins> coinsList;
    private List<FavoriteCoins> coinsList1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cryptos, container, false);
        try {

            recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            coinsList = new ArrayList<>();
            coinsList1 = new ArrayList<>();
            coinsAdapter = new CoinsAdapter(getContext(), coinsList);
            recyclerView.setAdapter(coinsAdapter);
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Favorites").child(firebaseUser.getUid());

            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    coinsList1.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FavoriteCoins coins = snapshot.getValue(FavoriteCoins.class);
                        coinsList1.add(coins);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            readCoins();

        }catch (Exception e){
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private void readCoins () {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Public").child("COINS");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coinsList.clear();
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FavoriteCoins coins = snapshot.getValue(FavoriteCoins.class);

                        for(int i = 0; i< coinsList1.size();i++){
                            if(coins.getCoinName().equals(coinsList1.get(i).getCoinName())){
                                coins.setFavorite(true);
                            }
                        }

                        coinsList.add(coins);
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
                try {
                    coinsAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
