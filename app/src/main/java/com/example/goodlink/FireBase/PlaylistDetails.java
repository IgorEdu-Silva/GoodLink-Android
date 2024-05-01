package com.example.goodlink.FireBase;

public class PlaylistDetails {
    private String titulo;
    private String nomeCriador;

    public PlaylistDetails() {}

    public PlaylistDetails(String titulo, String nomeCriador) {
        this.titulo = titulo;
        this.nomeCriador = nomeCriador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNomeCriador() {
        return nomeCriador;
    }

    public void setNomeCriador(String nomeCriador) {
        this.nomeCriador = nomeCriador;
    }
}
