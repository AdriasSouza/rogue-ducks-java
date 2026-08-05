# Plano de Implementação - Carta Ricochete (V3 Final)

Este plano detalha a implementação da 6ª carta, **Ricochete**, com a condição de disparo exata e estruturalmente segura para evitar cascatas.

## User Review Required

> [!IMPORTANT]
> **Condição Rígida de Disparo**: O Ricochete disparará apenas se:
> `pato.getVidaAtual() <= 0 && nivelRicochete > 0 && isDiretoDoClique == true && isRicochetHit == false`.
> Isso garante que o efeito ocorra apenas na morte pelo clique original e nunca em reações secundárias.

## Proposed Changes

### 1. Documentação de Design

#### [MODIFY] [GAME_DESIGN.md](file:///home/iartes/AndroidStudioProjects/RogueDucks/docs/GAME_DESIGN.md)
- Adicionar **Ricochete (Passiva)**:
    - **Gatilho**: Morte de um pato via clique direto.
    - **Busca**: Localiza o pato **mais próximo** (nearest) dentro de um raio de 100dp.
    - **Dano**:
        - Nível 1: 50% do dano original (mín 1).
        - Nível 2: 75% do dano original.
    - **Limite**: Máximo Nível 2. Estruturalmente impossível de encadear (No Chaining).

---

### 2. Modelos e Estado

#### [MODIFY] [EstadoJogador.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/EstadoJogador.java)
- Adicionar `int nivelRicochete = 0`.
- Método `getFatorRicochete()`: Retorna 0.5f (Nível 1) ou 0.75f (Nível 2).
- Atualizar `ativarUpgrade(id)` para suportar o stacking de "ricochete" até o limite de 2.

#### [MODIFY] [CartaActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/CartaActivity.java)
- Adicionar a carta Ricochete ao pool inicial.
- **Lógica de Pool Dinâmico**: Antes de sortear, verificar o `nivelRicochete` no `EstadoJogador` enviado via Intent. Se for >= 2, remover a carta do pool temporário de sorteio.

---

### 3. Lógica de Jogo (GameActivity)

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- **Refatorar `aplicarDanoAoPato`**:
    - Nova assinatura: `aplicarDanoAoPato(Pato pato, int dano, boolean isDiretoDoClique, boolean isRicochetHit)`.
- **Implementar `buscarPatoMaisProximo(Pato origem, float raioPx)`**:
    - Busca linear em `patosAtivos` calculando distâncias e retornando o menor valor dentro do raio.
- **Implementar `dispararRicochete(Pato origem, int danoOriginal)`**:
    - Encapsula a lógica de busca e aplicação do dano secundário (com `isRicochetHit = true`).
- **Atualizar todos os Call Sites**:
    - Clique direto: `(..., true, false)`.
    - Shotgun: `(..., false, false)`.
    - Ricochete: `(..., false, true)`.
    - Bomba: `(..., false, false)`.

---

## Verification Plan

### Manual Verification (Dispositivo Físico)
1. **Pato Único**: Confirmar flash normal.
2. **Nearest Target**: Validar que o ricochete busca o vizinho mais próximo, não um aleatório.
3. **No Chain**: Matar um pato com o Ricochete e confirmar que ele não gera um terceiro disparo.
4. **Shotgun Interaction**: Validar que o Ricochete ocorre apenas uma vez por clique, mesmo que o Shotgun mate 3 patos.
5. **Stacking**: Validar que no Nível 2 o dano causado no alvo secundário é visivelmente maior.
6. **Pool Removal**: Confirmar que a carta desaparece após a 2ª escolha.
