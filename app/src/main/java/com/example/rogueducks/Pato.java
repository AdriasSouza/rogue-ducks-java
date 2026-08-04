package com.example.rogueducks;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

public class Pato {
    public enum Tipo {
        NORMAL(1, 1.0f, 10),
        RAPIDO(1, 1.8f, 20),
        RESISTENTE(3, 0.8f, 30),
        FANTASMA(1, 1.2f, 50),
        DOURADO(1, 2.5f, 100);

        final int vidaBase;
        final float velocidadeBase;
        final int pontos;

        Tipo(int vidaBase, float velocidadeBase, int pontos) {
            this.vidaBase = vidaBase;
            this.velocidadeBase = velocidadeBase;
            this.pontos = pontos;
        }
    }

    private final String id;
    private final Tipo tipo;
    private final int vidaMaxima;
    private int vidaAtual;
    private float velocidade;
    private ImageView view;
    private boolean isAtivo;
    
    private ObjectAnimator animator;
    private final Handler blinkHandler = new Handler(Looper.getMainLooper());
    private boolean isBlinkPaused = false;
    private boolean isVisibleBlink = true;

    public Pato(String id, Tipo tipo, float multiplicadorVelocidade) {
        this.id = id;
        this.tipo = tipo;
        this.vidaMaxima = tipo.vidaBase;
        this.vidaAtual = tipo.vidaBase;
        this.velocidade = tipo.velocidadeBase * multiplicadorVelocidade;
        this.isAtivo = true;

        if (tipo == Tipo.FANTASMA) {
            startBlink();
        }
    }

    private void startBlink() {
        blinkHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAtivo) return;
                if (!isBlinkPaused) {
                    isVisibleBlink = !isVisibleBlink;
                    if (view != null) {
                        view.setVisibility(isVisibleBlink ? View.VISIBLE : View.INVISIBLE);
                        view.setClickable(isVisibleBlink);
                    }
                }
                blinkHandler.postDelayed(this, isVisibleBlink ? 1000 : 500);
            }
        }, 1000);
    }

    public void setBlinkPaused(boolean paused) {
        this.isBlinkPaused = paused;
    }

    public String getId() { return id; }
    public Tipo getTipo() { return tipo; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getVidaAtual() { return vidaAtual; }
    public void sofrerDano(int dano) { this.vidaAtual -= dano; }
    public float getVelocidade() { return velocidade; }
    public void setVelocidade(float velocidade) { this.velocidade = velocidade; }
    public ImageView getView() { return view; }
    public void setView(ImageView view) { this.view = view; }
    public boolean isAtivo() { return isAtivo; }
    public void setAtivo(boolean ativo) { 
        isAtivo = ativo; 
        if (!ativo) blinkHandler.removeCallbacksAndMessages(null);
    }

    public ObjectAnimator getAnimator() { return animator; }
    public void setAnimator(ObjectAnimator animator) { this.animator = animator; }
}