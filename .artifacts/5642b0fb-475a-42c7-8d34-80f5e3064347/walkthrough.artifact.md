# Walkthrough - Etapa 5: Persistência, SQLite e Ranking

Nesta etapa, implementamos o sistema de persistência local para salvar recordes, refinamos o sistema de upgrades com stacking e padronizamos a experiência do usuário.

## Alterações Realizadas

### Persistência e Ranking
- **DatabaseHelper**: Implementado o gerenciamento do banco SQLite seguindo o padrão acadêmico, com métodos para inserção e busca dos Top 10 recordes.
- **Fluxo de Game Over**: Ao perder, o jogador agora visualiza um `AlertDialog` para inserir seu nome. Se vazio, o nome padrão é "Jogador".
- **RankingActivity**: Implementada usando `ListView` e `ArrayAdapter` (conforme sugerido), listando Nome, Pontos e Onda Alcançada.

### Refinamentos de Gameplay
- **Stacking de Upgrades**:
    - **Bala Dupla**: Agora aumenta o dano base em +1 a cada escolha (Até o teto de 5).
    - **Shotgun**: Agora aumenta o raio da explosão em +30dp a cada escolha (Até o teto de 250dp).
- **UI de Cartas**: A `CartaActivity` agora esconde slots vazios (`View.GONE`), eliminando placeholders de "Esgotado".
- **Orientação Fixa**: Todas as telas do app foram travadas em `landscape` para garantir que o layout não quebre durante a partida.

### Correções Técnicas
- **Dano Colateral**: Confirmado que patos eliminados pela explosão da Shotgun passam pelo método `aplicarDanoAoPato`, incrementando pontuação e meta da onda corretamente.
- **Gestão de Recursos**: Fechamento de cursores e conexões SQLite em blocos `finally` para evitar vazamentos.

## Verificação Manual

1. **Orientação**: Confirmado. `MainActivity`, `GameActivity`, `CartaActivity` e `RankingActivity` iniciam e permanecem em landscape.
2. **Stacking**: Confirmado. Escolher "Bala Dupla" múltiplas vezes aumenta o dano, permitindo matar patos resistentes com menos cliques (ou instantaneamente).
3. **Persistência**: Confirmado. Após o Game Over, inserir o nome e salvar direciona o usuário para o Ranking, onde o score aparece ordenado.

---

## Trechos de Código Principais

### [DatabaseHelper.java](file:///home/iartes/AndroidStudioProjects/RogueDucks/app/src/main/java/com/example/rogueducks/DatabaseHelper.java)
```java
public void inserirPontuacao(String nome, int pontuacao, int onda) {
    SQLiteDatabase db = this.getWritableDatabase();
    try {
        ContentValues values = new ContentValues();
        values.put(COL_NOME, (nome == null || nome.trim().isEmpty()) ? "Jogador" : nome);
        values.put(COL_PONTUACAO, pontuacao);
        values.put(COL_ONDA, onda);
        db.insert(TABLE_RANKING, null, values);
    } finally {
        db.close();
    }
}
```

### Lógica de Salvamento (GameActivity)
```java
private void showGameOverDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Fim de Jogo!");
    builder.setMessage("Você chegou até a onda " + wave + " com " + score + " pontos.\nDigite seu nome:");

    final EditText input = new EditText(this);
    builder.setView(input);

    builder.setPositiveButton("Salvar", (dialog, which) -> {
        String nome = input.getText().toString().trim();
        dbHelper.inserirPontuacao(nome, score, wave);

        startActivity(new Intent(this, RankingActivity.class));
        finish();
    });
    // ...
}
```

### Schema do Banco (Documentado em ARCHITECTURE.md)
| Coluna | Tipo |
| :--- | :--- |
| id | INTEGER PK AUTOINCREMENT |
| nome | TEXT |
| pontuacao | INTEGER |
| onda | INTEGER |
| data | DATETIME (Default Now) |
