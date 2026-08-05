package com.example.rogueducks;

import java.io.Serializable;

public class EstadoJogador implements Serializable {
    // Ofensivas
    private int danoBase = 1;
    private boolean temShotgun = false;
    private float raioShotgunDp = 100f;
    private int nivelRicochete = 0; // Nível 0 a 2

    // Ativas - Desbloqueio
    private boolean bombaDesbloqueada = false;
    private boolean pausaDesbloqueada = false;
    private boolean ventoDesbloqueado = false;

    // Ativas - Atributos que escalam
    private int cooldownBombaBase = 20; // segundos
    private int duracaoPausaBase = 3;   // segundos
    private int percentualVentoBase = 25; // %

    public int getDanoBase() { return danoBase; }
    public boolean isTemShotgun() { return temShotgun; }
    public float getRaioShotgunDp() { return raioShotgunDp; }
    public int getNivelRicochete() { return nivelRicochete; }

    public float getFatorRicochete() {
        if (nivelRicochete == 1) return 0.5f;
        if (nivelRicochete == 2) return 0.75f;
        return 0f;
    }

    public boolean isBombaDesbloqueada() { return bombaDesbloqueada; }
    public boolean isPausaDesbloqueada() { return pausaDesbloqueada; }
    public boolean isVentoDesbloqueado() { return ventoDesbloqueado; }

    public int getCooldownBombaBase() { return cooldownBombaBase; }
    public int getDuracaoPausaBase() { return duracaoPausaBase; }
    public int getPercentualVentoBase() { return percentualVentoBase; }

    public void ativarUpgrade(String id) {
        switch (id) {
            case "double_bullet":
                if (danoBase < 5) danoBase++;
                break;
            case "shotgun":
                if (!temShotgun) temShotgun = true;
                else if (raioShotgunDp < 250f) raioShotgunDp += 30f;
                break;
            case "screen_bomb":
                if (!bombaDesbloqueada) bombaDesbloqueada = true;
                else if (cooldownBombaBase > 8) cooldownBombaBase -= 3;
                break;
            case "time_freeze":
                if (!pausaDesbloqueada) pausaDesbloqueada = true;
                else if (duracaoPausaBase < 6) duracaoPausaBase += 1;
                break;
            case "headwind":
                if (!ventoDesbloqueado) ventoDesbloqueado = true;
                else if (percentualVentoBase < 55) percentualVentoBase += 10;
                break;
            case "ricochet":
                if (nivelRicochete < 2) nivelRicochete++;
                break;
        }
    }

    // Passivas (para expansão futura)
    private boolean temTiroPerfurante = false;
    private int bonusChanceDourado = 0;

    public boolean isTemTiroPerfurante() { return temTiroPerfurante; }
    public void setTemTiroPerfurante(boolean temTiroPerfurante) { this.temTiroPerfurante = temTiroPerfurante; }
    public int getBonusChanceDourado() { return bonusChanceDourado; }
    public void setBonusChanceDourado(int bonusChanceDourado) { this.bonusChanceDourado = bonusChanceDourado; }

    // Métodos antigos mantidos para compatibilidade durante refatoração se necessário
    public void incrementarDano() { ativarUpgrade("double_bullet"); }
    public void ativarShotgun() { ativarUpgrade("shotgun"); }
}