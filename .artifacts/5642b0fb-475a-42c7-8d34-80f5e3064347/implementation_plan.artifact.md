# Plano de Implementação - Polimento Visual e UX

Este plano foca na padronização estética do projeto e na melhoria da experiência do usuário (UX) através de estilos consistentes e feedbacks visuais de interação.

## User Review Required

> [!NOTE]
> Utilizaremos uma paleta de cores inspirada em temas "Dark/Retro" para combinar com a temática Roguelike.
> As animações de feedback de acerto serão leves para não comprometer a performance em dispositivos reais.

## Proposed Changes

### 1. Identidade Visual e Estilos

#### [MODIFY] [colors.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/values/colors.xml)
- Definir paleta global:
    - `bg_dark`: #1A1A1D (Fundo principal)
    - `primary_gold`: #F1C40F (Destaque e Pato Dourado)
    - `secondary_red`: #E74C3C (Ações críticas/Perigo)
    - `pato_normal`: #4CAF50
    - pato_fast`: #F44336
    - `pato_resistant`: #2196F3
    - `card_offensive`: #3D0B0B
    - `card_utility`: #0B243D

#### [NEW] [styles.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/values/styles.xml)
- Criar estilos reutilizáveis:
    - `Style.RogueDucks.Button`: Botões com cantos arredondados e ripple.
    - `Style.RogueDucks.Text.Title`: Texto grande e negrito para títulos.
    - `Style.RogueDucks.Text.HUD`: Texto otimizado para leitura rápida no jogo.

---

### 2. Feedback de Interação

#### [NEW] [Shape Drawables](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/drawable/)
- `bg_button.xml`: Selector com estados `pressed` e `normal` (com ripple no v21+).
- `bg_card_round.xml`: Shape com bordas arredondadas (12dp) e stroke suave.

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- **Feedback de Acerto**: No `aplicarDanoAoPato`, adicionar uma animação de "flash" (mudar alpha/escala rapidamente) usando `ObjectAnimator`.
- **Feedback de Morte**: Animação de `scaleX/Y` para 0 antes de remover a View.

---

### 3. Refinamento de Telas (Layouts)

#### [MODIFY] [activity_main.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/layout/activity_main.xml)
- Aplicar o tema dark e os estilos de botão centralizados.

#### [MODIFY] [item_carta.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/layout/item_carta.xml)
- Aplicar `bg_card_round.xml` e ajustar margens/padding para um visual de "card" real.

#### [MODIFY] [CartaActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/CartaActivity.java)
- Lógica para mudar a cor de fundo do card conforme o tipo de upgrade (Ofensivo vs Utilitário).

---

## Verification Plan

### Manual Verification (Dispositivo Físico)
1. **Consistência**: Verificar se o fundo de todas as telas é o mesmo `bg_dark`.
2. **Botões**: Confirmar o efeito de ripple ou mudança de cor ao tocar.
3. **Cards**: Validar se os cards de "Shotgun" têm fundo diferente de "Tempo Suspenso".
4. **Acerto**: Confirmar se o pato "pisca" ou diminui levemente ao ser atingido.
5. **Transições**: Abrir e fechar o Ranking para checar a fluidez visual.
