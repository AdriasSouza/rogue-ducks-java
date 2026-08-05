# Plano de Implementação - Integração de Sprites Pixel Art

Este plano detalha a substituição dos recursos visuais vetoriais pelos novos sprites em pixel art (PNG), garantindo nitidez e consistência estética.

## User Review Required

> [!IMPORTANT]
> Para manter a nitidez dos pixels ao ampliar as imagens, utilizaremos `setFilterBitmap(false)`.
> Esta mudança é puramente estética e não altera as regras de colisão, vida ou velocidade.

## Proposed Changes

### 1. Atualização dos Patos

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- No método `createDuckView(Pato pato)`:
    - Mapear os tipos para os novos PNGs:
        - `NORMAL` -> `duck_normal.png`
        - `RAPIDO` -> `duck_fast.png`
        - `RESISTENTE` -> `duck_resistant.png`
        - `FANTASMA` -> `duck_ghost.png`
        - `DOURADO` -> `duck_golden.png`
    - Após `duckImg.setImageResource(resId)`, aplicar:
        ```java
        if (duckImg.getDrawable() instanceof BitmapDrawable) {
            ((BitmapDrawable) duckImg.getDrawable()).setFilterBitmap(false);
        }
        ```

### 2. Atualização das Habilidades (HUD)

#### [MODIFY] [activity_game.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/layout/activity_game.xml)
- Substituir as referências de `android:background` nos botões de habilidade:
    - `btnBomba` -> `@drawable/icon_bomb`
    - `btnPausa` -> `@drawable/icon_time_freeze`
    - `btnVento` -> `@drawable/icon_headwind`

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- No `onCreate`, após capturar as referências dos botões de habilidade, garantir nitidez nos ícones (se forem Bitmaps).

### 3. Atualização das Cartas de Escolha

#### [MODIFY] [CartaActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/CartaActivity.java)
- No método `configurarSlot`, adicionar um ícone ao layout do card (requer ajuste no `item_carta.xml`).
- Mapear IDs de carta para ícones:
    - `shotgun` -> `icon_shotgun.png`
    - `double_bullet` -> `icon_double_bullet.png`
    - `screen_bomb` -> `icon_bomb.png`
    - `time_freeze` -> `icon_time_freeze.png`
    - `headwind` -> `icon_headwind.png`

#### [MODIFY] [item_carta.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/layout/item_carta.xml)
- Adicionar um `ImageView` acima do `txtCardName` para exibir o ícone da habilidade.

### 4. Background do Jogo

#### [MODIFY] [activity_game.xml](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/layout/activity_game.xml)
- Definir `android:background="@drawable/bg_sky"` no `gameContainer`.
- Como o `RelativeLayout` não possui `scaleType`, se houver distorção excessiva, utilizaremos um `ImageView` como primeira camada do layout com `android:scaleType="centerCrop"` para garantir o preenchimento total da tela em 16:9 ou similar.

### Manual Verification (Dispositivo Físico)
1. **Ducks**: Confirmar que os patos agora são sprites pixelados coloridos em vez de formas geométricas.
2. **HUD**: Confirmar que os ícones das habilidades ativas mudaram para os novos PNGs.
3. **Cartas**: Abrir a tela de upgrades e validar se cada card exibe o ícone correspondente ao efeito.
4. **Nitidez**: Observar de perto se os pixels estão "quadrados" (nítidos) ou se há borrão (blur).
