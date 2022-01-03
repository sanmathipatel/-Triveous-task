package com.sanmathi.cryptos.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanmathi.cryptos.Models.FavoriteCoins;
import com.sanmathi.cryptos.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder>{

    private Context context;
    private List<FavoriteCoins> mCoins;
    private List<FavoriteCoins> coinsList;

    public FavoritesAdapter(Context context, List<FavoriteCoins> mCoins) {
        this.context = context;
        this.mCoins = mCoins;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.coins_item , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Favorites").child(firebaseUser.getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    coinsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FavoriteCoins coins = snapshot.getValue(FavoriteCoins.class);
                        coinsList.add(coins);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final FavoriteCoins coins = mCoins.get(position);

            coinsList = new ArrayList<>();

            holder.coinName.setText(coins.getCoinName());
            if (!coins.isFavorite()) {
                holder.favorite.setImageResource(R.drawable.favorite_outine);
            } else {
                holder.favorite.setImageResource(R.drawable.favoritye_filled);
            }

            holder.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int found =0;

                    for (int i =0; i< coinsList.size();i++){
                        if(coins.getCoinName().matches(coinsList.get(i).getCoinName())){

                            try {
                                reference.child(coinsList.get(i).getId()).removeValue();

                                holder.favorite.setImageResource(R.drawable.favorite_outine);
                                Toast.makeText(context, coins.getCoinName() +" removed from Favorites",Toast.LENGTH_SHORT).show();
                                found = 1;
                                break;
                            }catch(Exception e){
                            Toast.makeText(context,e.toString(), Toast.LENGTH_LONG).show();                            }
                        }
                    }

                    if(found == 0){

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("coinName", coins.getCoinName());
                        hashMap.put("favorite",true);
                        String key = reference.push().getKey();
                        hashMap.put("id",key);
                        reference.child(key).setValue(hashMap);
                        holder.favorite.setImageResource(R.drawable.favorite_outine);

                        Toast.makeText(context, coins.getCoinName() +" added to Favorites",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return mCoins.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView coinName;
        public ImageView favorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            coinName = itemView.findViewById(R.id.coin_name);
            favorite = itemView.findViewById(R.id.favorite);
        }
    }

}


