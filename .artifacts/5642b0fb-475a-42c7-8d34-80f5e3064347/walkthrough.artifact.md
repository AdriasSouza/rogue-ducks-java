# Walkthrough - Balanceamento e Refinamento de Gameplay

Nesta etapa, focamos em melhorar o ritmo do jogo, tornar os upgrades mais estratégicos e corrigir problemas de feedback visual no HUD.

## Alterações Realizadas

### 1. Sistema de Spawn e Densidade
- **Limite de Concorrência**: O jogo agora permite mais patos simultâneos conforme a onda avança:
    - Ondas 1-2: Máximo 2.
    - Ondas 3-5: Máximo 4.
    - Ondas 6+: Máximo 6.
- **Lógica de Retentativa**: Se o limite for atingido, o sistema aguarda 400ms antes de tentar spawnar o próximo pato, garantindo um fluxo denso mas estável.
- **Buff Indireto da Shotgun**: Com mais patos em voo, a probabilidade de acertos múltiplos com a Shotgun aumentou significativamente.

### 2. Escalonamento de Dificuldade
- **Tanques Progressivos**: Patos **Resistentes** agora ganham +1 HP a cada 4 ondas (Teto de 6 HP).
- **Consistência Visual**: Tanto a `vidaAtual` quanto a `vidaMaxima` são atualizadas no spawn, garantindo que o feedback de alpha proporcional funcione corretamente em patos com HP extra.

### 3. Refinamento de Habilidades
- **Tempo Suspenso Local**: A habilidade agora pausa apenas os patos presentes na tela no momento da ativação. Novos patos continuam surgindo e se movendo, evitando que o tempo da onda acabe "vazio".
- **Timer da Onda**: O timer agora corre de forma independente, sendo interrompido apenas pelo Pause Global.

### 4. Correções de HUD e UX
- **Restauração de Cooldown**: Validamos e corrigimos os IDs das Views de cooldown. O overlay e o texto numérico ("Xs") estão visíveis novamente sobre os botões.
- **Interatividade**: Os botões de habilidade ficam explicitamente não-clicáveis durante o cooldown, prevenindo disparos acidentais.

---

## Revisão de Código Solicitada

### [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)

#### Spawn com Limite e HP Escalonado
```java
private void spawnNextDuck() {
    // ...
    int maxSimultaneos = (wave <= 2) ? 2 : (wave <= 5) ? 4 : 6;

    if (patosAtivos.size() >= maxSimultaneos) {
        gameHandler.postDelayed(this::spawnNextDuck, 400); // Tenta de novo em breve
        return;
    }
    // ...
    if (tipo == Pato.Tipo.RESISTENTE) {
        int hpEscalonado = Math.min(6, 3 + (wave / 4));
        pato.setVidaMaxima(hpEscalonado); // Garante alpha proporcional correto
        pato.setVidaAtual(hpEscalonado);
    }
    // ...
}
```

#### Tempo Suspenso Local
```java
private void acionarPausa() {
    // ...
    List<Pato> patosParaPausar = new ArrayList<>(patosAtivos); // Captura instantâneo
    for (Pato p : patosParaPausar) {
        if (p.getAnimator() != null) p.getAnimator().pause();
        p.setBlinkPaused(true);
    }

    gameHandler.postDelayed(() -> {
        for (Pato p : patosParaPausar) {
            if (p.isAtivo() && !isPaused) { // Só retoma se o jogo não estiver no Pause Global
                if (p.getAnimator() != null) p.getAnimator().resume();
                p.setBlinkPaused(false);
            }
        }
    }, estadoJogador.getDuracaoPausaBase() * 1000L);
}
```

---
> [!IMPORTANT]
> O escalonamento de HP exige que o jogador busque upgrades de **Bala Dupla** para manter a eficiência em ondas avançadas.
