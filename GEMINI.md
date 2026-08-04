# Duck Roguelike - Memória Permanente

## Visão Geral
Projeto Android nativo em Java desenvolvido para a avaliação acadêmica N2-2. O jogo é um "Duck Hunt" com elementos de roguelike (escolha de cartas de upgrade entre ondas).

## Convenções de Código
- Linguagem: Java
- Padrão de Nomenclatura: CamelCase para classes, lowerCamelCase para métodos e variáveis.
- Layouts: XML tradicional.
- Persistência: SQLite para ranking local.

## Estado Atual (2026-08-03)
- **PROJETO CONCLUÍDO (Versão 1.0)**.
- Todas as 6 etapas de desenvolvimento finalizadas e validadas.
- Testado em dispositivo físico com sucesso (Estabilidade, Ciclo de Run, Persistência).

## Funcionalidades Implementadas
1. **Loop Roguelike**: Progressão infinita de ondas com escolha de upgrades entre elas.
2. **Sistema de Cartas (5 Tipos)**: Shotgun, Bala Dupla, Bomba, Congelar Tempo e Vento Contrário.
3. **Mecânica de Stacking**: Efeitos das cartas acumulam poder com limites técnicos definidos.
4. **Habilidades Ativas**: HUD com 3 botões dinâmicos e controle de cooldown visual.
5. **Tipos de Pato**: Normal, Rápido, Resistente (HP múltiplo), Fantasma (Blink) e Dourado (Bônus).
6. **Ranking Local**: Persistência via SQLite salvando os 10 melhores scores.

## Pendências (Futuro/Opcional)
- Adição de efeitos sonoros.
- Implementação de novas cartas passivas (ex: Visão de Águia).
- Sprites de imagem externos (atualmente usa Vector Drawables).

---
*Nota: Documentação encerrada para esta fase do projeto.*
