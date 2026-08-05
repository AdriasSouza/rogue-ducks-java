# Plano de Implementação - Densidade de Patos e Refinamento de Mecânicas

Este plano detalha o aumento da densidade de patos simultâneos (buff indireto da Shotgun), o escalonamento de vida dos patos resistentes e o refinamento da habilidade de Tempo Suspenso.

## User Review Required

> [!IMPORTANT]
> **Densidade Dinâmica**: O limite de patos simultâneos em tela aumentará conforme as ondas, permitindo combos de Shotgun mais frequentes. Implementaremos um limite explícito de concorrência no spawn.
> **Tempo Suspenso**: A habilidade deixará de ser global para ser local (afeta apenas patos presentes), garantindo que o spawn de novos patos não seja prejudicado.

## Proposed Changes

### 1. Refinamento de Spawn e Densidade (Itens 2 e 4)

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- **`spawnNextDuck`**:
    - Adicionar cálculo de `maxSimultaneos` (Ondas 1-2: 2, 3-5: 4, 6+: 6).
    - Verificar `patosAtivos.size() < maxSimultaneos` antes de criar um novo pato.
    - Se o limite for atingido, reagendar a tentativa de spawn para 400ms depois.
- **`acionarPausa` (Tempo Suspenso)**:
    - Remover a flag global `isTimeFrozen`.
    - Capturar os patos ativos no momento do clique em uma lista local.
    - Pausar apenas esses patos e agendar o `resume` apenas para eles.
- **`timerRunnable`**:
    - Remover dependência de `isTimeFrozen` (o timer da onda agora corre sempre, exceto no pause global).

---

### 2. Escalonamento de Dificuldade (Item 5)

#### [MODIFY] [Pato.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/Pato.java)
- Adicionar métodos `setVidaAtual(int hp)` e `setVidaMaxima(int hp)`.

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- **`spawnNextDuck`**:
    - Se o tipo for `RESISTENTE`, calcular HP bônus: `vida = Math.min(6, 3 + (wave / 4))`.

---

### 3. Correções de HUD (Item 1)

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- Validar IDs de overlay e TextView de cooldown.
- Garantir que o `cooldownHandler` atualize os textos numéricos corretamente.

---

## Verification Plan

### Manual Verification (Dispositivo Físico)
1. **Densidade**: Na Onda 6, confirmar visualmente se o jogo mantém até 6 patos na tela sem engasgos.
2. **Tempo Suspenso**: Ativar e confirmar que novos patos nascem e se movem enquanto os antigos estão congelados.
3. **Resistência**: Validar na Onda 5 que patos resistentes agora precisam de 4 cliques.
4. **Shotgun**: Confirmar que o dano em área está mais frequente devido à maior densidade.
