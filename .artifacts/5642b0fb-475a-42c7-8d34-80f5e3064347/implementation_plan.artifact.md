# Plano de Polimento Final e Conteúdo Informativo

Este plano detalha a adição de telas informativas (Lore, Galeria, Créditos), backgrounds aleatórios e um novo feedback visual de impacto de tiro, fechando o ciclo de polimento estético do projeto.

## User Review Required

> [!IMPORTANT]
> Utilizaremos **AlertDialogs customizados** para as telas de "Como Jogar", "Galeria" e "Sobre", mantendo a agilidade de desenvolvimento e a consistência visual dark/gold sem a necessidade de novas Activities.
> O efeito de acerto será uma pequena explosão avermelhada de ~150ms, visualmente distinta do círculo dourado da Shotgun.

## Proposed Changes

### 1. Telas Informativas (Dialogs Customizados)

#### [NEW] [dialog_info.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/layout/dialog_info.xml)
- Layout genérico reutilizável para Lore e Sobre, com `TextView` para título e conteúdo formatado.

#### [NEW] [dialog_galeria.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/layout/dialog_galeria.xml)
- Layout com `ScrollView` e `LinearLayout` vertical para listar as 6 cartas:
    - Cada item terá: `ImageView` (Ícone), `TextView` (Nome da Carta) e `TextView` (Descrição detalhada).

#### [MODIFY] [activity_main.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/layout/activity_main.xml)
- Reorganizar botões para incluir: **Jogar**, **Galeria**, **Como Jogar**, **Sobre**, **Ranking**, **Sair**.
- Aplicar o estilo `RogueDucks.Button` em todos.

#### [MODIFY] [MainActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/MainActivity.java)
- Implementar listeners para os novos botões chamando métodos que inflam e exibem os `AlertDialogs` customizados.

---

### 2. Feedback Visual e Ambientação

#### [NEW] [effect_hit.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/drawable/effect_hit.xml)
- Criar um drawable vetorial simples ou `shape` estrela/estilhaço na cor laranja/vermelho.

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- **Background Aleatório**: No `onCreate`, realizar sorteio entre `city_landscape`, `dawn_landscape`, `desert_landscape` e `bg_sky`.
- **Efeito de Impacto**: Substituir o pulso de escala por `mostrarEfeitoImpacto(x, y)`:
    - View pequena (35dp), cor avermelhada, animação de escala rápida (0.2 -> 1.0) e fade out em 150ms.

---

### 3. Assets de Ricochete

#### [MODIFY] [CartaActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/CartaActivity.java)
- Atualizar o mapeamento de ícones para usar o novo `icon_ricochete.png` na tela de escolha.

---

## Verification Plan

### Manual Verification (Dispositivo Físico)
1. **Menu**: Confirmar se todos os 6 botões aparecem corretamente e se os Dialogs abrem com o tema dark.
2. **Lore**: Validar se o texto "O FIM DOS DIAS DO PATO" está legível e bem formatado.
3. **Galeria**: Confirmar se as 6 cartas aparecem com seus ícones e descrições corretas.
4. **Acerto**: Atirar em um pato (sem shotgun) e confirmar se o efeito é um "estilhaço vermelho" pequeno, diferente do círculo dourado.
5. **Background**: Reiniciar o jogo 4-5 vezes para confirmar se o cenário muda aleatoriamente entre as opções disponíveis.
