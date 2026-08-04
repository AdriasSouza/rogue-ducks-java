package com.example.rogueducks;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartaActivity extends AppCompatActivity {

    private final List<Carta> poolDeCartas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carta);

        inicializarPool();
        
        List<Carta> selecionadas = sortearCartasDistintas(3);
        
        configurarSlot(findViewById(R.id.card1), selecionadas.size() > 0 ? selecionadas.get(0) : null);
        configurarSlot(findViewById(R.id.card2), selecionadas.size() > 1 ? selecionadas.get(1) : null);
        configurarSlot(findViewById(R.id.card3), selecionadas.size() > 2 ? selecionadas.get(2) : null);
    }

    private void inicializarPool() {
        poolDeCartas.add(new Carta("shotgun", "Shotgun", "Acertos causam dano em área.", true));
        poolDeCartas.add(new Carta("double_bullet", "Bala Dupla", "Cada clique conta como 2 tiros.", true));
        poolDeCartas.add(new Carta("screen_bomb", "Bomba de Tela", "Limpa todos os patos (Ativa).", true));
        poolDeCartas.add(new Carta("time_freeze", "Tempo Suspenso", "Pausa o movimento dos patos (Ativa).", true));
        poolDeCartas.add(new Carta("headwind", "Vento Contrário", "Reduz a velocidade dos patos (Ativa).", true));
        
        // Cartas não implementadas
        poolDeCartas.add(new Carta("eagle_eye", "Visão de Águia", "Fantasmas duram mais (TODO).", false));
    }

    private List<Carta> sortearCartasDistintas(int quantidade) {
        List<Carta> implementadas = new ArrayList<>();
        for (Carta c : poolDeCartas) {
            if (c.isImplementada()) implementadas.add(c);
        }

        Collections.shuffle(implementadas);
        
        List<Carta> resultado = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            if (i < implementadas.size()) {
                resultado.add(implementadas.get(i));
            } else {
                resultado.add(new Carta("empty", "Esgotado", "Sem mais upgrades.", false));
            }
        }
        return resultado;
    }

    private void configurarSlot(View view, Carta carta) {
        if (carta == null || carta.getId().equals("empty")) {
            view.setVisibility(View.GONE);
            return;
        }

        view.setVisibility(View.VISIBLE);
        TextView txtName = view.findViewById(R.id.txtCardName);
        TextView txtDesc = view.findViewById(R.id.txtCardDesc);

        txtName.setText(carta.getNome());
        txtDesc.setText(carta.getDescricao());

        // Visual por categoria
        int colorId;
        if (carta.getId().equals("shotgun") || carta.getId().equals("double_bullet")) {
            colorId = R.color.card_offensive;
        } else {
            colorId = R.color.card_utility;
        }
        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, colorId)));

        view.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("CARTA_ESCOLHIDA", carta);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}