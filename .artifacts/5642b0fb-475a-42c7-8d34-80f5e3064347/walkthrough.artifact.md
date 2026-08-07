# Walkthrough - Sistema de Áudio (BGM e SFX)

Nesta etapa, adicionamos uma trilha sonora imersiva e efeitos sonoros táteis para melhorar a experiência de jogo, integrando-os ao sistema de pause e ao ciclo de vida das Activities.

## Alterações Realizadas

### 1. Música de Fundo (BGM)
- **MediaPlayer**: Implementamos a trilha sonora `bmg_ducks.mp3` em loop na `GameActivity`.
- **Sincronia de Pause**: A música é pausada automaticamente ao abrir o menu de pause, ao minimizar o app ou ao receber uma chamada.
- **Retomada Inteligente**: Em `onResume`, a música só volta a tocar se o jogador não tiver pausado o jogo manualmente antes de sair da tela.
- **Gestão de Energia**: O player é liberado (`release()`) em `onDestroy` para economizar bateria e memória.

### 2. Efeitos Sonoros (SFX)
- **ToneGenerator**: Utilizamos a API nativa para gerar tons de baixa latência, evitando o custo de carregar arquivos de áudio extras.
- **Lógica de Hit Único**: Implementamos a distinção solicitada. O som de "beep" de acerto dispara apenas para o alvo principal do clique. Danos colaterais (Shotgun/Bomba) não geram beeps individuais, evitando poluição sonora.
- **Eventos Sonoros**:
    - **Hit**: Tom agudo curto.
    - **Shotgun/Bomba**: Tom mais grave e robusto de explosão.
    - **Sucesso na Onda**: Tom de confirmação positiva.
    - **Game Over**: Tom grave de falha.
    - **Escolha de Carta**: Tom de seleção na `CartaActivity`.

---

## Revisão Técnica

### Integração com Pause (GameActivity)
```java
private void showPauseMenu() {
    // ...
    if (mediaPlayer != null) mediaPlayer.pause();
    // ...
}

// No botão Continuar:
case 0:
    isPaused = false;
    if (mediaPlayer != null) mediaPlayer.start();
    // ...
```

### Feedback Sonoro Seletivo
```java
private void aplicarDanoAoPato(Pato pato, int dano, boolean isDiretoDoClique, boolean isRicochetHit) {
    // ...
    // Toca som apenas no clique direto, não no dano em área
    if (isDiretoDoClique && toneGenerator != null) {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
    }
    // ...
}
```

---
> [!NOTE]
> O volume do sistema de áudio foi configurado em 50% por padrão para garantir que os efeitos sonoros sejam audíveis sem sobrepor a trilha sonora.
