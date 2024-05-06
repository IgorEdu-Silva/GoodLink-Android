package com.example.goodlink.FireBase;

public class RatingManager {
    private String rating;
    private String avaliador;
    private String avaliado;

    public RatingManager(String rating, String avaliador, String avaliado) {
        this.rating = rating;
        this.avaliador = avaliador;
        this.avaliado = avaliado;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAvaliador() {
        return avaliador;
    }

    public void setAvaliador(String avaliador) {
        this.avaliador = avaliador;
    }

    public String getAvaliado() {
        return avaliado;
    }

    public void setAvaliado(String avaliado) {
        this.avaliado = avaliado;
    }
}
