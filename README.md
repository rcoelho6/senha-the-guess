# Jogo de senha — MVP

Documentação de arquitetura para um MVP de jogo de senha entre dois jogadores. Cada jogador define uma senha de quatro algarismos distintos; os jogadores alternam palpites e recebem a quantidade de algarismos corretos e parciais.

> **Estado do repositório:** atualmente contém documentação e proposta de arquitetura; o código da aplicação ainda não foi criado.

## Arquitetura proposta

- **Gameplay:** API Java 21 / Spring Boot 3, responsável pelo ciclo da partida, convites, senhas, palpites, presença e estado operacional.
- **Persistência:** API responsável pelo cadastro de jogadores e pelo histórico imutável de eventos das partidas.
- **Estado ativo e outbox:** Amazon MemoryDB compatível com Redis OSS, com atualizações atômicas por partida e relay assíncrono.
- **Histórico:** Amazon RDS for PostgreSQL Multi-AZ, com deduplicação por `eventId` e ordenação por sequência da partida.
- **Identidade e infraestrutura:** Amazon Cognito, ALB, ECS/Fargate, Secrets Manager/KMS e CloudWatch.
- Os módulos podem ser implantados juntos ou como serviços separados; a comunicação entre eles permanece REST.

## Documentação

- [Resumo final do MVP](backend/mvp/arquitetura-mvp-final.md) — regras, fluxo da partida e visão geral.
- [Especificação técnica](backend/mvp/tech-docs/arquitetura-tecnica-mvp.md) — arquitetura, endpoints REST, payloads de entrada/saída, armazenamento e recuperação.
- [Problemas possíveis e estratégias de mitigação](backend/mvp/tech-docs/riscos-e-mitigacoes-mvp.md) — riscos técnicos, impactos, sinais e ações de mitigação.
- [Proposta inicial de arquitetura](backend/proposta%20inicial/arquitetura-backend-senha.md) — primeira proposta conceitual.

## Contratos REST

A especificação detalhada está na documentação técnica. Em resumo, as decisões sem payload usam `PATCH`, como aceitar/recusar convite e atualizar presença; operações com dados de entrada usam `PUT`, como definir senha e enviar palpite. A criação de recursos/eventos usa `POST`, e as consultas usam `GET`.

Todas as rotas e estruturas de entrada/saída, autenticação, respostas de erro e limites de visibilidade estão descritos na especificação técnica.

## Ponto de atenção conhecido

A regra de timeout do heartbeat ainda precisa de confirmação: conforme registrada, pode suspender uma partida online após poucas chamadas. Esse comportamento está destacado na documentação técnica e na análise de riscos e deve ser resolvido antes da implementação.

## Estrutura atual

```text
.
├── .gitignore
├── README.md
└── backend/
    ├── mvp/
    │   ├── arquitetura-backend-v1.md
    │   ├── arquitetura-backend-v2-mvp.md
    │   ├── arquitetura-backend-v3-mvp.md
    │   ├── arquitetura-mvp-final.md
    │   └── tech-docs/
    │       ├── arquitetura-tecnica-mvp.md
    │       └── riscos-e-mitigacoes-mvp.md
    └── proposta inicial/
        └── arquitetura-backend-senha.md
```

## Execução

Ainda não há código, configuração Gradle ou infraestrutura implantável neste repositório. Os documentos descrevem uma proposta técnica; instruções de build, testes e execução serão adicionadas quando a implementação for iniciada.
