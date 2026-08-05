# Plano de Implementação - Sistema de Áudio (BGM e SFX)

Este plano descreve a integração de música de fundo e efeitos sonoros nativos para melhorar a imersão e o feedback tátil do jogo.

## User Review Required

> [!IMPORTANT]
> **Integração com Pause**: A música (BGM) será pausada automaticamente ao abrir o menu de pause ou minimizar o app, e retomada apenas se o jogo estiver em estado "Ativo".
> **Sons Nativos**: Utilizaremos a `ToneGenerator` API para efeitos sonoros, garantindo latência zero e economia de recursos sem precisar de múltiplos arquivos de áudio.

## Proposed Changes

### 1. Música de Fundo (GameActivity)

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java)
- **Campos**: Adicionar `private MediaPlayer mediaPlayer`.
- **`onCreate`**: Inicializar o player com `R.raw.bmg_ducks`, definir `setLooping(true)` e `setVolume(0.5f, 0.5f)`.
- **Ciclo de Vida**:
    - **`onResume`**: Chamar `mediaPlayer.start()` apenas se `!isPaused`.
    - **`onPause`**: Chamar `mediaPlayer.pause()`.
    - **`onDestroy`**: Chamar `mediaPlayer.release()` e limpar referência.
- **Menu de Pause**:
    - **`showPauseMenu`**: Pausar a música ao abrir o diálogo.
    - **"Continuar"**: Retomar a música ao voltar pro jogo.
    - **"Sair/Reiniciar"**: Parar/Resetar conforme a transição.

---

### 2. Efeitos Sonoros (ToneGenerator)

#### [MODIFY] [GameActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/GameActivity.java) e [CartaActivity.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/CartaActivity.java)
- **Campos**: Adicionar `private ToneGenerator toneGenerator`.
- **`onCreate`**: Inicializar com `AudioManager.STREAM_MUSIC` e volume 50.
- **Mapeamento de Sons**:
    - **Hit**: `ToneGenerator.TONE_PROP_BEEP` (100ms).
    - **Shotgun**: `ToneGenerator.TONE_PROP_ACK` (150ms).
    - **Wave Success**: `ToneGenerator.TONE_PROP_PROMPT` (200ms).
    - **Game Over**: `ToneGenerator.TONE_PROP_NACK` (300ms).
    - **Confirmação Carta**: `ToneGenerator.TONE_PROP_BEEP2` (100ms).
- **Helper**: Criar método `playSfx(int toneType, int duration)` para facilitar disparos.

---

### 3. Organização de Arquivos

#### [NEW] [res/raw/](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/res/raw/)
- Garantir a existência do diretório para o arquivo `bmg_ducks.mp3`.

---

## Verification Plan

### Manual Verification (Dispositivo Físico)
1. **BGM Contínuo**: Iniciar o jogo e confirmar que a música toca em loop e o volume não abafa o som do sistema.
2. **Sincronia de Pause**:
    - Pausar o jogo manualmente: a música deve parar imediatamente.
    - Minimizar o app (Home): a música deve parar.
    - Voltar ao app: se estava pausado, a música **não** deve tocar até você clicar em "Continuar".
3. **SFX de Feedback**:
    - Clicar num pato: ouvir o "beep" agudo.
    - Usar Shotgun: ouvir o som mais grave/distinto da explosão.
    - Ganhar a onda: ouvir o som de sucesso antes da tela de cartas.
4. **Cleanup**: Confirmar via Logcat que não há erros de "MediaPlayer finalized without being released" ao sair e entrar no jogo várias vezes.
