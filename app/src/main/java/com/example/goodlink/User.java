package com.example.goodlink;

public class User {
    private String nome;
    private String email;

    public User() {
        // Construtor vazio necessário para o Firebase
    }

    public User(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    // Métodos getters e setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
