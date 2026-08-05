package com.example.rogueducks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private RelativeLayout gameContainer;
    private TextView txtScore, txtWave, txtTime, txtGoal;
    private FrameLayout btnBomba, btnPausa, btnVento;
    private View overlayBomba, overlayPausa, overlayVento;
    private TextView txtCdBomba, txtCdPausa, txtCdVento;
    private ImageButton btnPause;

    private int score = 0;
    private int wave = 1;
    private int ducksSpawned = 0;
    private int ducksKilled = 0;
    private int totalDucksInWave = 10;
    private int meta = 7;
    private int timeLeft = 30;

    private final Random random = new Random();
    private final Handler gameHandler = new Handler(Looper.getMainLooper());
    private final Handler cooldownHandler = new Handler(Looper.getMainLooper());
    
    private int screenWidth, screenHeight;
    private boolean isWaveActive = false;
    private boolean isWaveEnded = false;
    private boolean isPaused = false;

    private EstadoJogador estadoJogador = new EstadoJogador();
    private final List<Pato> patosAtivos = new ArrayList<>();
    private DatabaseHelper dbHelper;

    // Cooldown states (ms)
    private long lastBombaTime = 0, lastPausaTime = 0, lastVentoTime = 0;
    private boolean isHeadwindActive = false;

    private final ActivityResultLauncher<Intent> cartaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Carta escolhida = (Carta) result.getData().getSerializableExtra("CARTA_ESCOLHIDA");
                    if (escolhida != null) {
                        estadoJogador.ativarUpgrade(escolhida.getId());
                        wave++;
                        setupWave(wave);
                        startWave();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dbHelper = new DatabaseHelper(this);
        gameContainer = findViewById(R.id.gameContainer);
        txtScore = findViewById(R.id.txtScore);
        txtWave = findViewById(R.id.txtWave);
        txtTime = findViewById(R.id.txtTime);
        txtGoal = findViewById(R.id.txtGoal);
        
        btnBomba = findViewById(R.id.btnBomba);
        btnPausa = findViewById(R.id.btnPausa);
        btnVento = findViewById(R.id.btnVento);
        overlayBomba = findViewById(R.id.overlayBomba);
        overlayPausa = findViewById(R.id.overlayPausa);
        overlayVento = findViewById(R.id.overlayVento);
        txtCdBomba = findViewById(R.id.txtCdBomba);
        txtCdPausa = findViewById(R.id.txtCdPausa);
        txtCdVento = findViewById(R.id.txtCdVento);
        
        btnPause = findViewById(R.id.btnPause);

        btnBomba.setOnClickListener(v -> acionarBomba());
        btnPausa.setOnClickListener(v -> acionarPausa());
        btnVento.setOnClickListener(v -> acionarVento());
        btnPause.setOnClickListener(v -> showPauseMenu());

        // Garantir nitidez dos ícones de habilidade
        aplicarNitidezHabilidade(btnBomba);
        aplicarNitidezHabilidade(btnPausa);
        aplicarNitidezHabilidade(btnVento);

        getScreenDimensions();
        setupWave(wave);
        startWave();
        startCooldownUIUpdate();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isWaveActive && !isWaveEnded) {
                    showPauseMenu();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }

    private void setupWave(int currentWave) {
        isWaveEnded = false;
        switch (currentWave) {
            case 1: totalDucksInWave = 10; timeLeft = 30; meta = 7; break;
            case 2: totalDucksInWave = 12; timeLeft = 30; meta = 9; break;
            case 3: totalDucksInWave = 14; timeLeft = 35; meta = 11; break;
            case 4: totalDucksInWave = 16; timeLeft = 35; meta = 12; break;
            case 5: totalDucksInWave = 18; timeLeft = 40; meta = 14; break;
            default:
                totalDucksInWave = 10 + (currentWave * 2);
                timeLeft = Math.min(60, 30 + (currentWave - 5) * 2);
                meta = (int) Math.ceil(totalDucksInWave * 0.75);
                break;
        }
        ducksSpawned = 0;
        ducksKilled = 0;
        
        btnBomba.setVisibility(estadoJogador.isBombaDesbloqueada() ? View.VISIBLE : View.GONE);
        btnPausa.setVisibility(estadoJogador.isPausaDesbloqueada() ? View.VISIBLE : View.GONE);
        btnVento.setVisibility(estadoJogador.isVentoDesbloqueado() ? View.VISIBLE : View.GONE);
        
        updateHUD();
    }

    private void updateHUD() {
        txtScore.setText("Pontos: " + score);
        txtWave.setText("Onda: " + wave);
        txtTime.setText("Tempo: " + timeLeft + "s");
        txtGoal.setText("Meta: " + ducksKilled + "/" + meta);
    }

    private void startWave() {
        isWaveActive = true;
        isPaused = false;
        spawnNextDuck();
        startTimer();
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isWaveActive || isPaused) return;

            if (timeLeft > 0) {
                timeLeft--;
                updateHUD();
                gameHandler.postDelayed(this, 1000);
            } else if (!isWaveEnded) {
                endWave();
            }
        }
    };

    private void startTimer() {
        gameHandler.removeCallbacks(timerRunnable);
        gameHandler.postDelayed(timerRunnable, 1000);
    }

    private void spawnNextDuck() {
        gameHandler.removeCallbacks(this::spawnNextDuck);

        if (!isWaveActive || isPaused || ducksSpawned >= totalDucksInWave) return;

        // Limite de Concorrência por Onda
        int maxSimultaneos;
        if (wave <= 2) maxSimultaneos = 2;
        else if (wave <= 5) maxSimultaneos = 4;
        else maxSimultaneos = 6;

        if (patosAtivos.size() >= maxSimultaneos) {
            // Teto atingido, tenta de novo em breve
            gameHandler.postDelayed(this::spawnNextDuck, 400);
            return;
        }

        Pato.Tipo tipo = sortearTipoPato(wave);
        float multVel = Math.min(2.0f, 1.0f + (Math.max(0, wave - 1) * 0.05f));
        if (isHeadwindActive) multVel *= (1.0f - estadoJogador.getPercentualVentoBase() / 100f);
        
        Pato pato = new Pato("duck_" + System.currentTimeMillis() + "_" + ducksSpawned, tipo, multVel);
        
        // Escalonamento de HP para Resistentes
        if (tipo == Pato.Tipo.RESISTENTE) {
            int hpEscalonado = Math.min(6, 3 + (wave / 4));
            pato.setVidaMaxima(hpEscalonado);
            pato.setVidaAtual(hpEscalonado);
        }

        createDuckView(pato);
        animateDuck(pato);
        
        if (isPaused) {
            if (pato.getAnimator() != null) pato.getAnimator().pause();
            pato.setBlinkPaused(true);
        }

        ducksSpawned++;
        long baseDelay = (timeLeft * 1000L) / (totalDucksInWave - ducksSpawned + 1);
        long randomDelay = Math.max(500, baseDelay / 2 + random.nextInt((int) Math.max(1, baseDelay)));
        gameHandler.postDelayed(this::spawnNextDuck, randomDelay);
    }

    private Pato.Tipo sortearTipoPato(int currentWave) {
        int r = random.nextInt(100);
        if (currentWave == 1) return Pato.Tipo.NORMAL;
        if (currentWave == 2) return r < 20 ? Pato.Tipo.RAPIDO : Pato.Tipo.NORMAL;
        if (currentWave == 3) {
            if (r < 20) return Pato.Tipo.RAPIDO;
            if (r < 40) return Pato.Tipo.RESISTENTE;
            return Pato.Tipo.NORMAL;
        }
        if (currentWave == 4) {
            if (r < 5) return Pato.Tipo.DOURADO;
            if (r < 25) return Pato.Tipo.RAPIDO;
            if (r < 45) return Pato.Tipo.RESISTENTE;
            return Pato.Tipo.NORMAL;
        }
        if (currentWave == 5) {
            if (r < 5) return Pato.Tipo.DOURADO;
            if (r < 15) return Pato.Tipo.FANTASMA;
            if (r < 35) return Pato.Tipo.RAPIDO;
            if (r < 55) return Pato.Tipo.RESISTENTE;
            return Pato.Tipo.NORMAL;
        }
        int pDourado = 5 + estadoJogador.getBonusChanceDourado();
        int pFantasma = Math.min(25, 10 + (currentWave - 5) * 3);
        int pResistente = Math.min(30, 20 + (currentWave - 5) * 2);
        int pRapido = Math.min(30, 20 + (currentWave - 5) * 2);
        if (r < pDourado) return Pato.Tipo.DOURADO;
        if (r < pDourado + pFantasma) return Pato.Tipo.FANTASMA;
        if (r < pDourado + pFantasma + pRapido) return Pato.Tipo.RAPIDO;
        if (r < pDourado + pFantasma + pRapido + pResistente) return Pato.Tipo.RESISTENTE;
        return Pato.Tipo.NORMAL;
    }

    private void aplicarNitidezHabilidade(View v) {
        if (v.getBackground() instanceof BitmapDrawable) {
            ((BitmapDrawable) v.getBackground()).setFilterBitmap(false);
        }
    }

    private void createDuckView(Pato pato) {
        ImageView duckImg = new ImageView(this);
        int resId;
        switch (pato.getTipo()) {
            case RAPIDO: resId = R.drawable.duck_fast; break;
            case RESISTENTE: resId = R.drawable.duck_resistant; break;
            case FANTASMA: resId = R.drawable.duck_ghost; break;
            case DOURADO: resId = R.drawable.duck_golden; break;
            default: resId = R.drawable.duck_normal; break;
        }
        duckImg.setImageResource(resId);
        
        // Garantir nitidez dos pixels
        if (duckImg.getDrawable() instanceof BitmapDrawable) {
            ((BitmapDrawable) duckImg.getDrawable()).setFilterBitmap(false);
        }
        
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        int startY = random.nextInt(Math.max(1, screenHeight - size - 300)) + 150;
        
        duckImg.setTranslationX(-size - 100);
        duckImg.setTranslationY(startY);
        duckImg.setLayoutParams(params);
        duckImg.setOnClickListener(v -> handleDuckClick(pato));
        
        gameContainer.addView(duckImg);
        pato.setView(duckImg);
        patosAtivos.add(pato);
    }

    private void animateDuck(Pato pato) {
        ImageView view = pato.getView();
        if (view == null) return;
        float endX = screenWidth + 200;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX(), endX);
        long duration = (long) (4000 / pato.getVelocidade());
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (pato.isAtivo()) removeDuck(pato);
            }
        });
        pato.setAnimator(animator);
        animator.start();
    }

    private void handleDuckClick(Pato pato) {
        if (!pato.isAtivo() || isWaveEnded || isPaused) return;
        
        float clickX = 0, clickY = 0;
        if (pato.getView() != null) {
            clickX = pato.getView().getTranslationX() + pato.getView().getWidth() / 2f;
            clickY = pato.getView().getTranslationY() + pato.getView().getHeight() / 2f;
        }

        int dano = estadoJogador.getDanoBase();
        aplicarDanoAoPato(pato, dano);

        if (estadoJogador.isTemShotgun()) {
            float raioPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                    estadoJogador.getRaioShotgunDp(), getResources().getDisplayMetrics());
            
            // Efeito visual da explosão
            mostrarEfeitoShotgun(clickX, clickY, raioPx);

            List<Pato> copiaPatos = new ArrayList<>(patosAtivos);
            for (Pato p : copiaPatos) {
                if (p == pato || !p.isAtivo() || p.getView() == null) continue;
                
                float pX = p.getView().getTranslationX() + p.getView().getWidth() / 2f;
                float pY = p.getView().getTranslationY() + p.getView().getHeight() / 2f;
                
                double dist = Math.sqrt(Math.pow(clickX - pX, 2) + Math.pow(clickY - pY, 2));
                if (dist <= raioPx) {
                    aplicarDanoAoPato(p, Math.max(1, dano / 2));
                }
            }
        }
    }

    private void mostrarEfeitoShotgun(float x, float y, float raioPx) {
        View effect = new View(this);
        effect.setBackgroundResource(R.drawable.effect_shotgun);
        
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        effect.setLayoutParams(params);
        
        effect.setTranslationX(x - size / 2f);
        effect.setTranslationY(y - size / 2f);
        
        gameContainer.addView(effect);

        float finalScale = (raioPx * 2) / size;

        ObjectAnimator sX = ObjectAnimator.ofFloat(effect, "scaleX", 0f, finalScale);
        ObjectAnimator sY = ObjectAnimator.ofFloat(effect, "scaleY", 0f, finalScale);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(effect, "alpha", 0.6f, 0f);

        sX.setDuration(250);
        sY.setDuration(250);
        alpha.setDuration(250);

        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (effect.getParent() != null) {
                    ((RelativeLayout) effect.getParent()).removeView(effect);
                }
            }
        });

        sX.start();
        sY.start();
        alpha.start();
    }

    private void aplicarDanoAoPato(Pato pato, int dano) {
        if (!pato.isAtivo()) return;
        
        pato.sofrerDano(dano);

        // Feedback Visual de Hit (Não-bloqueante)
        ImageView view = pato.getView();
        if (view != null) {
            ObjectAnimator pulseX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.2f, 1.0f);
            ObjectAnimator pulseY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.2f, 1.0f);
            pulseX.setDuration(100);
            pulseY.setDuration(100);
            pulseX.start();
            pulseY.start();
        }

        if (pato.getVidaAtual() <= 0) {
            // Lógica de jogo imediata (Pontuação e Meta)
            score += pato.getTipo().pontos;
            ducksKilled++;
            updateHUD();
            
            // Animação de Morte (Paralela à remoção)
            executarAnimacaoMorte(pato);
        } else {
            float alpha = (float) pato.getVidaAtual() / pato.getVidaMaxima();
            if (view != null) view.setAlpha(Math.max(0.2f, alpha));
        }
    }

    private void executarAnimacaoMorte(Pato pato) {
        pato.setAtivo(false); // Impede novos cliques
        ImageView view = pato.getView();
        if (view == null) {
            removeDuck(pato); // Segurança
            return;
        }

        // Remover do controle de jogo imediatamente
        if (pato.getAnimator() != null) {
            pato.getAnimator().removeAllListeners();
            pato.getAnimator().cancel();
        }
        patosAtivos.remove(pato);

        // Animação visual de saída "shrink and fade"
        ObjectAnimator sX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 0f);
        ObjectAnimator sY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), 0f);
        
        sX.setDuration(200);
        sY.setDuration(200);
        alpha.setDuration(200);

        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Remoção física da View apenas após o efeito visual
                if (view.getParent() != null) {
                    ((RelativeLayout) view.getParent()).removeView(view);
                }
                pato.setView(null);
            }
        });

        sX.start();
        sY.start();
        alpha.start();
    }

    private void removeDuck(Pato pato) {
        pato.setAtivo(false);
        if (pato.getAnimator() != null) {
            pato.getAnimator().removeAllListeners();
            pato.getAnimator().cancel();
        }
        patosAtivos.remove(pato);
        if (pato.getView() != null) {
            gameContainer.removeView(pato.getView());
            pato.setView(null);
        }
    }

    private void acionarBomba() {
        if (isPaused) return;
        long now = System.currentTimeMillis();
        long cd = estadoJogador.getCooldownBombaBase() * 1000L;
        if (now - lastBombaTime < cd) return;
        lastBombaTime = now;
        
        for (Pato p : new ArrayList<>(patosAtivos)) aplicarDanoAoPato(p, 999);
    }

    private void acionarPausa() {
        if (isPaused) return;
        long now = System.currentTimeMillis();
        long cd = 25 * 1000L;
        if (now - lastPausaTime < cd) return;
        lastPausaTime = now;
        
        // Pausa local: apenas patos JÁ na tela
        List<Pato> patosParaPausar = new ArrayList<>(patosAtivos);
        for (Pato p : patosParaPausar) {
            if (p.getAnimator() != null) p.getAnimator().pause();
            p.setBlinkPaused(true);
        }

        gameHandler.postDelayed(() -> {
            for (Pato p : patosParaPausar) {
                // Só retoma se o pato ainda estiver ativo e o jogo não estiver pausado globalmente
                if (p.isAtivo() && !isPaused) {
                    if (p.getAnimator() != null) p.getAnimator().resume();
                    p.setBlinkPaused(false);
                }
            }
        }, estadoJogador.getDuracaoPausaBase() * 1000L);
    }

    private void acionarVento() {
        if (isPaused) return;
        long now = System.currentTimeMillis();
        long cd = 15 * 1000L;
        if (now - lastVentoTime < cd) return;
        lastVentoTime = now;
        
        isHeadwindActive = true;
        float red = 1.0f - estadoJogador.getPercentualVentoBase() / 100f;
        for (Pato p : new ArrayList<>(patosAtivos)) {
            p.setVelocidade(p.getVelocidade() * red);
            if (p.getAnimator() != null && p.getView() != null) {
                float curX = p.getView().getTranslationX();
                p.getAnimator().removeAllListeners(); // Fix: Stop animator from removing duck on cancel
                p.getAnimator().cancel();
                animateDuckFrom(p, curX);
            }
        }
        gameHandler.postDelayed(() -> isHeadwindActive = false, 5000);
    }

    private void animateDuckFrom(Pato pato, float startX) {
        ImageView view = pato.getView();
        if (view == null) return;
        float endX = screenWidth + 200;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
        float remainingDist = endX - startX;
        float totalDist = screenWidth + 400f;
        long duration = (long) (remainingDist / (totalDist / (4000f / pato.getVelocidade())));
        animator.setDuration(Math.max(100, duration));
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (pato.isAtivo()) removeDuck(pato);
            }
        });
        pato.setAnimator(animator);
        animator.start();
        if (isPaused) animator.pause();
    }

    private void startCooldownUIUpdate() {
        cooldownHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateCooldownOverlay(overlayBomba, txtCdBomba, lastBombaTime, estadoJogador.getCooldownBombaBase() * 1000L);
                updateCooldownOverlay(overlayPausa, txtCdPausa, lastPausaTime, 25 * 1000L);
                updateCooldownOverlay(overlayVento, txtCdVento, lastVentoTime, 15 * 1000L);
                cooldownHandler.postDelayed(this, 200);
            }
        }, 200);
    }

    private void updateCooldownOverlay(View overlay, TextView txt, long lastTime, long cd) {
        long elapsed = System.currentTimeMillis() - lastTime;
        View parent = (View) overlay.getParent();
        
        if (elapsed >= cd) {
            overlay.getLayoutParams().height = 0;
            txt.setText("");
            if (parent != null) parent.setClickable(true);
        } else {
            float perc = 1.0f - (float) elapsed / cd;
            overlay.getLayoutParams().height = (int) (perc * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()));
            int remainingSec = (int) Math.ceil((cd - elapsed) / 1000f);
            txt.setText(remainingSec + "s");
            if (parent != null) parent.setClickable(false);
        }
        overlay.requestLayout();
    }

    private void showPauseMenu() {
        if (isPaused || isWaveEnded) return;
        isPaused = true;
        
        for (Pato p : patosAtivos) {
            if (p.getAnimator() != null) p.getAnimator().pause();
            p.setBlinkPaused(true);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Jogo Pausado");
        builder.setItems(new CharSequence[]{"Continuar", "Reiniciar Run", "Voltar ao Menu"}, (dialog, which) -> {
            switch (which) {
                case 0: // Continuar
                    isPaused = false;
                    for (Pato p : patosAtivos) {
                        if (p.getAnimator() != null) p.getAnimator().resume();
                        p.setBlinkPaused(false);
                    }
                    startTimer(); // Fix: Restart timer tick
                    spawnNextDuck(); // Fix: Restart spawn loop
                    break;
                case 1: // Reiniciar
                    restartRun();
                    break;
                case 2: // Sair
                    sairJogo();
                    break;
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void restartRun() {
        isPaused = false;
        isWaveActive = false;
        gameHandler.removeCallbacksAndMessages(null);
        
        // Limpeza total
        for (Pato p : new ArrayList<>(patosAtivos)) {
            removeDuck(p);
        }
        gameContainer.removeAllViews();
        
        estadoJogador = new EstadoJogador();
        score = 0;
        wave = 1;
        setupWave(wave);
        startWave();
    }

    private void sairJogo() {
        isPaused = false;
        isWaveActive = false;
        gameHandler.removeCallbacksAndMessages(null);
        cooldownHandler.removeCallbacksAndMessages(null);
        for (Pato p : new ArrayList<>(patosAtivos)) {
            removeDuck(p);
        }
        finish();
    }

    private void endWave() {
        isWaveEnded = true;
        isWaveActive = false;
        gameHandler.removeCallbacksAndMessages(null);

        for (Pato p : new ArrayList<>(patosAtivos)) {
            removeDuck(p);
        }
        
        if (ducksKilled >= meta) {
            Intent intent = new Intent(this, CartaActivity.class);
            cartaLauncher.launch(intent);
        } else {
            showGameOverDialog();
        }
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fim de Jogo!");
        builder.setMessage("Você chegou até a onda " + wave + " com " + score + " pontos.\nDigite seu nome:");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String nome = input.getText().toString().trim();
            dbHelper.inserirPontuacao(nome, score, wave);
            startActivity(new Intent(this, RankingActivity.class));
            finish();
        });
        builder.setNegativeButton("Menu", (dialog, which) -> sairJogo());
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isWaveActive = false;
        gameHandler.removeCallbacksAndMessages(null);
        cooldownHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameHandler.removeCallbacksAndMessages(null);
        cooldownHandler.removeCallbacksAndMessages(null);
    }
}