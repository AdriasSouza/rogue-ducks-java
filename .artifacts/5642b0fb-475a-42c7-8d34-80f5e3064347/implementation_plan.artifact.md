# Plano de Implementação - Feedback Visual Avançado (Shotgun e Cooldowns)

Este plano descreve a adição de efeitos visuais para a explosão da Shotgun e o refinamento do indicador visual de cooldown para as habilidades ativas.

## User Review Required

> [!NOTE]
> O efeito da **Shotgun** será uma View circular dinâmica que expande e desaparece.
> O indicador de **Cooldown** utilizará a técnica de máscara vertical (ajuste de altura de overlay) já presente, mas com polimento na lógica de interatividade.

## Proposed Changes

### 1. Efeito Visual da Shotgun

#### [NEW] [effect_shotgun.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/drawable/effect_shotgun.xml)
- Criar um `shape` circular (oval) com cor `@color/primary_gold` e alpha inicial alto (ex: 0.4).

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- Criar método `mostrarEfeitoShotgun(float x, float y, float raioPx)`:
    - Instanciar uma `View` dinamicamente.
    - Definir tamanho fixo (ex: 1dp x 1dp) e centralizar no ponto (x, y).
    - Usar `ObjectAnimator` para:
        - `scaleX` e `scaleY` de 0 até `(raioPx * 2)`.
        - `alpha` de 1.0f para 0f.
    - Duração: 250ms.
    - Remover do `gameContainer` no `onAnimationEnd`.
- Chamar este método no `handleDuckClick` quando o upgrade estiver ativo.

---

### 2. Refinamento de Cooldown Visual

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- **`updateCooldownOverlay`**:
    - Garantir que o overlay cubra o ícone de baixo para cima (ou cima para baixo) proporcionalmente.
    - **Interatividade**: Desabilitar o clique no botão (`setClickable(false)`) enquanto o cooldown estiver ativo e reabilitar ao terminar. Isso evita que o efeito de ripple do Material dispare visualmente quando a habilidade não pode ser usada.
- **`startCooldownUIUpdate`**: Sincronizar perfeitamente com os novos campos de `EstadoJogador`.

---

## Verification Plan

### Manual Verification (Dispositivo Físico)
1. **Shotgun**: Atirar em qualquer lugar (com o upgrade ativo) e confirmar se um anel/círculo dourado expande rapidamente a partir do clique.
2. **Cooldown**: Ativar a Bomba e observar se:
    - O overlay preto semitransparente desce suavemente.
    - O texto "20s", "19s"... atualiza corretamente.
    - O botão não reage a toques (sem ripple) até o tempo zerar.
3. **Estresse**: Usar Shotgun em grupos de patos para garantir que múltiplas explosões visuais simultâneas não causem jank ou bugs de sobreposição.
