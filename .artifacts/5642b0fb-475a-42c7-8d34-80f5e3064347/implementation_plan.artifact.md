# Plano de Correção e Refinamento - Debug Playtest

Este plano foca na resolução de crashes críticos (NPE e ConcurrentModification), correção da meta de onda e implementação do sistema de Pause global.

## User Review Required

> [!IMPORTANT]
> **Shotgun NPE**: O crash ocorria porque a `View` do pato era removida (e setada como null) ao morrer pelo clique direto *antes* do cálculo da explosão.
> **Vento Contrário**: Iterar sobre a lista original enquanto remove/cancela animators causava instabilidade.
> **Pause Global**: Implementaremos uma interrupção total que afeta Timers, Spawns, Animators e Handlers.

## Proposed Changes

### 1. Correções de Estabilidade (Crashes)

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- **`handleDuckClick`**: Salvar as coordenadas X/Y do clique *antes* de aplicar dano ao pato principal para evitar NPE caso ele morra e sua View seja limpa.
- **`acionarVento`**: Iterar sobre `new ArrayList<>(patosAtivos)` para evitar `ConcurrentModificationException`.
- **`acionarBomba`**: Garantir iteração segura sobre cópia da lista.
- **`removeDuck`**: Adicionar verificações de nulidade extras para segurança.

### 2. Correção de Regressão (Meta/Balanceamento)

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- Restaurar valores de `meta` e `timeLeft` no `setupWave` seguindo a tabela original do `GAME_DESIGN.md`.

### 3. Melhoria de Feedback (Cooldowns)

#### [MODIFY] [activity_game.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/layout/activity_game.xml)
- Adicionar um `TextView` centralizado em cada slot de habilidade ativa para exibir o tempo restante em segundos.

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- Atualizar o texto numérico no `startCooldownUIUpdate`.

### 4. Sistema de Pause Global

#### [MODIFY] [activity_game.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/layout/activity_game.xml)
- Adicionar um botão de Pause (ícone simples) no canto superior.

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- **Mecânica de Pause**:
    - Flag `isGamePaused`.
    - Pausar todos os Animators.
    - Pausar `blinkHandlers` dos Fantasmas.
    - Impedir novos spawns e progressão do timer da onda.
- **Menu de Pause**: `AlertDialog` com:
    - `Continuar`: Retoma tudo.
    - `Reiniciar Run`: Reseta `EstadoJogador`, pontos e volta para Onda 1.
    - `Sair`: Volta para a `MainActivity`.
- **Botão Voltar**: Sobrescrever `onBackPressed` para abrir o menu de pause.

---

## Verification Plan

### Automated Tests
- Build do projeto para validar novas referências de ID e métodos.

### Manual Verification (Dispositivo Físico)
1. **Shotgun**: Atirar em patos resistentes e dourados repetidamente para garantir que a morte do alvo não crasha a explosão.
2. **Vento**: Ativar com muitos patos em tela para validar a iteração segura.
3. **Meta**: Confirmar na Onda 1 se a meta é 7 e o tempo é 30s.
4. **Pause**:
    - Pausar o jogo, esperar 5 segundos, despausar e confirmar que o tempo da onda não passou.
    - Reiniciar run e confirmar que as cartas/pontos zeram.
5. **Back Button**: Pressionar o botão de voltar do sistema e confirmar abertura do menu de pause.
