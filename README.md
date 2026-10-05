# Jogo de senha — MVP

Backend do MVP de jogo de senha entre dois jogadores. Os jogadores usam IDs já cadastrados; o oponente entra com seu ID e o `gameplayId` compartilhado pelo criador. Cada jogador define uma senha de quatro algarismos distintos e recebe contagens de corretos e parciais nos palpites.

> **Estado do repositório:** a implementação inicial dos serviços Gameplay e Persistência está em `backend/source`; limites e débitos de segurança/resiliência descritos abaixo continuam valendo.

## Arquitetura resumida

- **Gameplay:** API REST para criação/entrada/recusa de partidas, senhas, palpites, resultados e heartbeat.
- **Redis:** fonte da verdade durante a partida; duas chaves por gameplay, estado e presença, atualizadas em transações otimistas.
- **Persistência:** valida IDs já cadastrados e recebe snapshots assíncronos append-only no PostgreSQL. Não responde estado de partida ativa.
- **Heartbeat:** clientes enviam uma chamada por segundo; ausência por mais de cinco segundos encerra por timeout.
- **Identidade:** sem login/sessão/token; IDs são fornecidos pelo cliente. É uma limitação conhecida do MVP.
- **Histórico:** envio assíncrono sem fila durável, retry ou garantia de commit antes do `202 Accepted`.

## Código e execução

O código-fonte, POMs dos módulos, configuração local e instruções estão em [backend/source](backend/source/README.md). O projeto usa Java 21, Spring Boot 4.1.1 e módulos Maven independentes `gameplay` e `persistence`. Docker Compose prepara Redis e PostgreSQL; IDs de demonstração são inseridos no banco local.

## Documentação

- [Instruções para build e execução](backend/source/README.md)
- [Resumo final do MVP](backend/mvp/arquitetura-mvp-final.md)
- [Especificação técnica](backend/mvp/tech-docs/arquitetura-tecnica-mvp.md)
- [Problemas possíveis e débitos técnicos](backend/mvp/tech-docs/riscos-e-mitigacoes-mvp.md)
- [Proposta inicial de arquitetura](backend/proposta%20inicial/arquitetura-backend-senha.md) — documento conceitual anterior às decisões finais do MVP.

## Limites conhecidos

Não há autenticação nem autorização forte; IDs digitados podem ser falsificados. Turnos e concorrência de palpites são responsabilidade do frontend. Redis é autoritativo durante a partida, e sua falha pode causar perda do estado. PostgreSQL é histórico eventualmente consistente; envio assíncrono não tem retry/outbox nem idempotência ponta a ponta. Esta implementação não deve ser exposta a tráfego público ou dados sensíveis sem controles adicionais.