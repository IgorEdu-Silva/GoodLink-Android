CREATE DATABASE GoodLink;

USE GoodLink;

CREATE TABLE Usuarios (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    NomeUsuario VARCHAR(255) NOT NULL,
    Email VARCHAR(255) NOT NULL UNIQUE,
    Senha VARCHAR(255) NOT NULL,
    DataRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    TokenRedefinicaoSenha VARCHAR(255)
);

CREATE TABLE Playlists (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    TituloPlaylist VARCHAR(255) NOT NULL,
    DescricaoPlaylist TEXT,
    URLVideoYouTube VARCHAR(255) NOT NULL,
    Categoria VARCHAR(255) NOT NULL,
    IDUsuarioCriador INT,
    FOREIGN KEY (IDUsuarioCriador) REFERENCES Usuarios(ID)
);

CREATE TABLE Avaliacoes (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    IDVideo INT,
    IDUsuarioAvaliador INT,
    Classificacao INT,
    Comentario TEXT,
    DataAvaliacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (IDVideo) REFERENCES Playlists(ID),
    FOREIGN KEY (IDUsuarioAvaliador) REFERENCES Usuarios(ID)
);

CREATE TABLE Catalogos (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    NomeCatalogo VARCHAR(255) NOT NULL
);

CREATE TABLE Playlists_Catalogos (
    IDPlaylist INT,
    IDCatalogo INT,
    PRIMARY KEY (IDPlaylist, IDCatalogo),
    FOREIGN KEY (IDPlaylist) REFERENCES Playlists(ID),
    FOREIGN KEY (IDCatalogo) REFERENCES Catalogos(ID)
);