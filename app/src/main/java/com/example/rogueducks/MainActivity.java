package com.example.rogueducks;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnRanking = findViewById(R.id.btnRanking);
        Button btnExit = findViewById(R.id.btnExit);
        Button btnGaleria = findViewById(R.id.btnGaleria);
        Button btnLore = findViewById(R.id.btnLore);
        Button btnAbout = findViewById(R.id.btnAbout);

        btnPlay.setOnClickListener(v -> startActivity(new Intent(this, GameActivity.class)));
        btnRanking.setOnClickListener(v -> startActivity(new Intent(this, RankingActivity.class)));
        btnExit.setOnClickListener(v -> finish());

        btnLore.setOnClickListener(v -> showLoreDialog());
        btnAbout.setOnClickListener(v -> showAboutDialog());
        btnGaleria.setOnClickListener(v -> showGaleriaDialog());
    }

    private void showLoreDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_info, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_RogueDucks).setView(view).create();

        ((TextView) view.findViewById(R.id.txtDialogTitle)).setText("COMO JOGAR");
        ((TextView) view.findViewById(R.id.txtDialogContent)).setText(
                "O FIM DOS DIAS DO PATO\n\n" +
                "Ninguém sabe ao certo o que causou a Rebelião. Um dia os patos eram só aves de lagoa; no outro, eram uma força implacável que varreu cidades inteiras.\n\n" +
                "Você é um dos últimos sobreviventes. Sem exército, sem resgate, só você e o que conseguir encontrar pra se defender. A cada onda que resiste, aprende algo novo sobre como sobreviver mais um pouco - habilidades, armas improvisadas, truques que fazem a diferença entre mais uma noite viva ou ser só mais um nome esquecido.\n\n" +
                "Não existe vitória final. Existe só o quanto você aguenta.\n\n" +
                "Sobreviva o máximo que conseguir."
        );

        view.findViewById(R.id.btnDialogClose).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showAboutDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_info, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_RogueDucks).setView(view).create();

        ((TextView) view.findViewById(R.id.txtDialogTitle)).setText("SOBRE");
        ((TextView) view.findViewById(R.id.txtDialogContent)).setText(
                "Duck Roguelike\n\n" +
                "Autor: Adrias Soares de Souza\n" +
                "Cargo: Desenvolvedor Full-stack\n" +
                "Criado em: 31/07/2026"
        );

        view.findViewById(R.id.btnDialogClose).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showGaleriaDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_galeria, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_RogueDucks).setView(view).create();
        LinearLayout container = view.findViewById(R.id.containerGaleria);

        addCardToGaleria(container, "Shotgun", "Acertos causam dano em área.", R.drawable.icon_shotgun);
        addCardToGaleria(container, "Bala Dupla", "Cada clique conta como 2 tiros.", R.drawable.icon_double_bullet);
        addCardToGaleria(container, "Bomba de Tela", "Limpa todos os patos ativos.", R.drawable.icon_bomb);
        addCardToGaleria(container, "Tempo Suspenso", "Pausa o movimento por alguns segundos.", R.drawable.icon_time_freeze);
        addCardToGaleria(container, "Vento Contrário", "Reduz a velocidade dos patos em 25%.", R.drawable.icon_headwind);
        addCardToGaleria(container, "Ricochete", "Bala atinge o pato mais próximo ao morrer.", R.drawable.icon_ricochete);

        view.findViewById(R.id.btnGaleriaClose).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addCardToGaleria(LinearLayout container, String name, String desc, int iconRes) {
        View item = LayoutInflater.from(this).inflate(R.layout.item_galeria, null);
        ((TextView) item.findViewById(R.id.txtGaleriaName)).setText(name);
        ((TextView) item.findViewById(R.id.txtGaleriaDesc)).setText(desc);
        ImageView img = item.findViewById(R.id.imgGaleriaIcon);
        img.setImageResource(iconRes);
        if (img.getDrawable() instanceof BitmapDrawable) {
            ((BitmapDrawable) img.getDrawable()).setFilterBitmap(false);
        }
        container.addView(item);
    }
}