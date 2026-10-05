# Backend — Jogo de senha

Backend multi-módulo do MVP, em Java 21 e Spring Boot 4.1.1.

## Módulos

- **`gameplay`** (porta `8080`): API REST do jogo, regras de senha/palpite, timeout e estado ativo no Redis. Atualizações de histórico são enviadas em background para Persistência.
- **`persistence`** (porta `8081`): API interna para validar jogadores existentes e aceitar gravações históricas append-only no PostgreSQL.
- As dependências são gerenciadas pelo parent Spring Boot do POM raiz. Os dois serviços incluem starters de teste.

## Requisitos e execução local

Requer JDK 21, Maven 3.6.3+ e Docker Desktop iniciado. A partir desta pasta (`backend/source`):

```powershell
docker compose up -d
mvn test
```

Em dois terminais, também nesta pasta:

```powershell
mvn -pl persistence spring-boot:run
```

```powershell
mvn -pl gameplay spring-boot:run
```

O Compose inicia Redis em `localhost:6379` e PostgreSQL em `localhost:5432`. O banco local de desenvolvimento é `senha_game` (usuário `senha_game`, senha `senha_game_dev`). Os IDs fictícios `player-123`, `player-456` e `player-789` são inseridos automaticamente apenas na primeira inicialização do volume do banco. Para reinicializar esse seed: `docker compose down -v` (isso apaga os dados locais do PostgreSQL).

## API Gameplay

- `POST /games` — cria partida com `playerId` e `opponentPlayerId`.
- `PATCH /games/{gameplayId}/players/{playerId}/join` e `/decline` — entrada/recusa do oponente.
- `PUT /games/{gameplayId}/players/{playerId}/secret` — define a senha (`{"digits":"0482"}`).
- `PATCH /games/{gameplayId}/players/{playerId}/heartbeat` — presença; mais de cinco segundos encerra por timeout.
- `PUT /games/{gameplayId}/players/{playerId}/guess` — envia palpite (`{"digits":"1234"}`).
- `GET /games/{gameplayId}/state` — estado público da partida, sem revelar senhas.

## API interna de Persistência

- `GET /internal/v1/players/{playerId}` — valida existência do jogador.
- `POST /internal/v1/game-records` — aceita snapshot para processamento assíncrono (`202 Accepted`). A resposta não confirma commit durável.

## Limites do MVP

Não há autenticação/autorização forte; os `playerId` são declarações do cliente. Turnos são responsabilidade do frontend. Redis é autoritativo durante a partida; a escrita no PostgreSQL é eventual, sem outbox, fila durável ou retry garantido. As senhas não são incluídas nos snapshots históricos nem retornadas pela API, mas ficam em texto claro no Redis; não exponha esta versão a dados sensíveis ou tráfego público sem adicionar controles de segurança.

As duas chaves Redis por partida são atualizadas em transações otimistas `WATCH/MULTI/EXEC`, com hash-tag comum para compatibilidade com Redis Cluster. O histórico armazena JSON serializado como texto PostgreSQL, append-only, ordenável pelo timestamp emitido pelo Gameplay; timestamp não garante ordenação total entre instâncias.
