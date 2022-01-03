package com.sanmathi.cryptos.Models;

public class FavoriteCoins {
    private String id;
    private String coinName;
    private boolean favorite;

    public FavoriteCoins(){

    }

    public FavoriteCoins(String id, String coinName, boolean favorite){
        this.id = id;
        this.coinName = coinName;
        this.favorite = favorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }
}
