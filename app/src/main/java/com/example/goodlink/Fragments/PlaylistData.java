package com.example.goodlink.Fragments;

public class PlaylistData {
    private String titulo;
    private String descricao;
    private String nomeCanal;
    private String iframe;
    private String urlCanal;
    private String categoria;
    private String nomeUsuario;
    private String dataPub;
    private String avaliacao;
    private String criadorId;

    public PlaylistData() {

    }

    public PlaylistData(String titulo, String descricao, String nomeCanal, String iframe, String urlCanal, String categoria, String nomeUsuario, String dataPub) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.nomeCanal = nomeCanal;
        this.iframe = iframe;
        this.urlCanal = urlCanal;
        this.categoria = categoria;
        this.nomeUsuario = nomeUsuario;
        this.dataPub = dataPub;
    }

    public PlaylistData(String titulo, String descricao, String nomeCanal, String iframe, String urlCanal, String categoria, String nomeUsuario, String dataPub, String avaliacao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.nomeCanal = nomeCanal;
        this.iframe = iframe;
        this.urlCanal = urlCanal;
        this.categoria = categoria;
        this.nomeUsuario = nomeUsuario;
        this.dataPub = dataPub;
        this.avaliacao = avaliacao;
    }

    public String getCriadorId() {
        return criadorId;
    }

    public void setCriadorId(String criadorId) {
        this.criadorId = criadorId;
    }

    public String getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomeCanal() {
        return nomeCanal;
    }

    public void setNomeCanal(String nomeCanal) {
        this.nomeCanal = nomeCanal;
    }

    public String getIframe() {
        return iframe;
    }

    public void setIframe(String iframe) {
        this.iframe = iframe;
    }

    public String getUrlCanal() {
        return urlCanal;
    }

    public void setUrlCanal(String urlCanal) {
        this.urlCanal = urlCanal;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getDataPub() {
        return dataPub;
    }

    public void setDataPub(String dataPub) {
        this.dataPub = dataPub;
    }
}
