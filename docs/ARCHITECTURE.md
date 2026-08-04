# Arquitetura Técnica

## Componentes Android
- **MainActivity**: Menu inicial (Botões: Iniciar, Ranking, Sair).
- **GameActivity**: Loop principal do jogo, gerenciamento de View e Handlers.
- **CartaActivity**: Seleção de upgrade entre ondas via `ActivityResultLauncher`.
- **RankingActivity**: Listagem de scores usando `ListView` e SQLite.

## Classes de Domínio
- **Pato**: Objeto que contém tipo (Enum), HP, velocidade e referência à `ImageView` em tela.
- **EstadoJogador**: Objeto que armazena os modificadores da run atual (ex: `danoBase`, `temShotgun`, `raioShotgunDp`).
- **GameManager**: Lógica de controle de ondas e timers integrada na `GameActivity`.

## Decisões Técnicas
- **Movimentação**: Utilizado `ObjectAnimator` com `LinearInterpolator` para trajetórias lineares estáveis.
- **Upgrades**: Sistema de **Stacking**. Escolher o mesmo upgrade aumenta o nível do efeito (Dano +1 ou Raio +30dp) com tetos definidos.
- **Orientação**: Travada em `landscape` para todas as Activities para garantir consistência visual e de layout.
- **Persistência**: SQLite para Ranking local.

## Banco de Dados (SQLite)
- **Tabela `Ranking`**: 
    - `id` (INTEGER PK AUTOINCREMENT)
    - `nome` (TEXT)
    - `pontuacao` (INTEGER)
    - `onda` (INTEGER)
    - `data` (DATETIME DEFAULT CURRENT_TIMESTAMP)

## Assets
- Layouts XML tradicionais.
- Sprites de pato via Drawables/Colors.
