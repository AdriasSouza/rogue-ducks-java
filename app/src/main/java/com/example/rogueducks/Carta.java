package com.example.rogueducks;

import java.io.Serializable;

public class Carta implements Serializable {
    private final String id;
    private final String nome;
    private final String descricao;
    private final boolean implementada;

    public Carta(String id, String nome, String descricao, boolean implementada) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.implementada = implementada;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public boolean isImplementada() { return implementada; }
}