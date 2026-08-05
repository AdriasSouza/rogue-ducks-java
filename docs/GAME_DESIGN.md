# Game Design - Duck Roguelike

## Objetivo
Sobreviver ao maior número de ondas possível, abatendo patos para atingir metas e evoluindo com cartas de upgrade.

## Loop Principal
1. **Onda de Patos**: Patos aparecem na tela. O jogador deve clicar neles dentro de um tempo limite.
2. **Progressão**: Cada onda aumenta a dificuldade (mais patos/mais resistência).
3. **Escolha de Carta**: Se a meta da onda for atingida, o jogador escolhe 1 de 3 cartas de upgrade.
4. **Game Over**: Se a meta não for atingida, a pontuação final é salva no ranking e o jogo termina.

## Tipos de Pato
- **Normal**: Linha reta, 1 clique.
- **Rápido**: Veloz, 1 clique.
- **Resistente**: 2-3 cliques, muda de cor ao ser atingido.
- **Fantasma**: Pisca (invisível/visível), clicável apenas quando visível.
- **Dourado**: Bônus raro de pontos ou moedas.

## Cartas de Efeito (Upgrades)
- **Ofensivas**:
    - *Shotgun*: Acerta patos em um raio de explosão. (Stack: +30dp de raio, máx 250dp).
    - *Bala Dupla*: Cada clique conta como 2 tiros. (Stack: +1 de dano, máx 5).
    - *Tiro Perfurante*: Atravessa patos na mesma trajetória.
- **Utilitárias (Habilidades Ativas)**:
    - *Bomba de Tela*: Limpa todos os patos da tela. (Cooldown Base: 20s. Stack: -3s de cooldown por nível. Piso Mínimo: 8s).
    - *Tempo Suspenso*: Pausa o movimento dos patos por X segundos. (Duração Base: 3s. Cooldown: 25s fixo. Stack: +1s de duração por nível. Teto Máximo: 6s).
    - *Vento Contrário*: Reduz a velocidade de todos os patos ativos. (Redução Base: 25% por 5s. Cooldown: 15s fixo. Stack: +10% de redução por nível. Teto Máximo: 55%).
    - *Ímã*: Patos lentos perto do toque.
- **Passivas**:
    - *Mais Tempo*: Aumenta duração da onda.
    - *Visão de Águia*: Fantasmas visíveis por mais tempo.
    - *Sorte do Caçador*: Aumenta chance de pato dourado.

## Balanceamento (Ponto de Partida)

### Valores por Tipo de Pato
| Tipo | Pontos | Velocidade Base | Resistência (Cliques) |
| :--- | :--- | :--- | :--- |
| Normal | 10 | 1.0x | 1 |
| Rápido | 20 | 1.8x | 1 |
| Resistente | 30 | 0.8x | 2-3 |
| Fantasma | 50 | 1.2x | 1 |
| Dourado | 100 | 2.5x | 1 |

### Progressão de Ondas
- **Onda 1**: 10 patos (100% Normal), 30s, Meta: 7 abatidos.
- **Onda 2**: 12 patos (80% Normal, 20% Rápido), 30s, Meta: 9 abatidos.
- **Onda 3**: 14 patos (60% Normal, 20% Rápido, 20% Resistente), 35s, Meta: 11 abatidos.
- **Onda 4**: 16 patos (55% Normal, 20% Rápido, 20% Resistente, 5% Dourado), 35s, Meta: 12 abatidos.
- **Onda 5**: 18 patos (45% Normal, 20% Rápido, 20% Resistente, 10% Fantasma, 5% Dourado), 40s, Meta: 14 abatidos.

### Progressão Infinita (Ondas 6+)
- **Quantidade de Patos**: $10 + (Onda \times 2)$.
- **Tempo de Onda**: Base 30s + 2s a cada onda (Teto: 60s).
- **Meta de Abate**: Fixo em 75% da quantidade total da onda (arredondado para cima).
- **Chances de Tipos (Limites Máximos)**:
    - Rápido: Cresce até 30%.
    - Resistente: Cresce até 30%.
    - Fantasma: Cresce até 25% (Início na onda 5).
    - Dourado: Fixo em 5% a 10% conforme upgrades.
- **Velocidade**: Aumento de 5% por onda até o teto de 2.0x da velocidade base original.

### Fórmulas Gerais
- **Meta**: 75% do total (priorizar valores da tabela para ondas 1-5).
- **Velocidade Máxima**: 2.0x.

### Balanceamento de Densidade e HP
- **Patos Simultâneos (Limite de Concorrência)**:
    - Ondas 1-2: Máximo de 2 patos simultâneos.
    - Ondas 3-5: Máximo de 4 patos simultâneos.
    - Ondas 6+: Máximo de 6 patos simultâneos.
- **Escalonamento de HP (Pato Resistente)**:
    - Vida Base: 3 HP.
    - Incremento: +1 HP a cada 4 ondas (Ex: Onda 5 = 4 HP, Onda 9 = 5 HP).
    - Teto Máximo: 6 HP.
