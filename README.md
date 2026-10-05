# Jogo de senha — MVP

Documentação de arquitetura para um MVP de jogo de senha entre dois jogadores. Cada pessoa usa seu ID de usuário já cadastrado; o oponente entra com seu ID e o `gameplayId` compartilhado pelo criador da partida. Os jogadores definem senhas de quatro algarismos distintos, alternam palpites pela interface e recebem contagens de corretos e parciais.

> **Estado do repositório:** contém documentação e proposta de arquitetura; o código da aplicação ainda não foi criado.

## Arquitetura resumida

- **Gameplay:** controla partida, senhas, palpites, resultados e presença.
- **Redis:** fonte da verdade durante a partida ativa; mantém duas chaves por partida: uma para presença/timeout e outra para o estado acumulativo da gameplay.
- **Persistência:** PostgreSQL append-only recebe atualizações de forma assíncrona e tem consistência eventual. Não é consultado para responder à gameplay ativa.
- **Heartbeat:** uma chamada por segundo por jogador; se qualquer jogador ficar mais de cinco segundos sem heartbeat, a partida termina por timeout.
- **Identidade:** na MVP não há login, sessão ou token; os IDs são digitados e validados no início/entrada. Isso é uma limitação de segurança explicitamente aceita.
- A emissão à Persistência não deve bloquear a thread de gameplay, mas tamanho de fila, polling, retry, replay e garantia ponta a ponta não são gerenciados na MVP.

## Documentação

- [Resumo final do MVP](backend/mvp/arquitetura-mvp-final.md) — regras, fluxo, decisões e limites.
- [Especificação técnica](backend/mvp/tech-docs/arquitetura-tecnica-mvp.md) — arquitetura, contratos HTTP, payloads e modelos de dados.
- [Problemas possíveis e débitos técnicos](backend/mvp/tech-docs/riscos-e-mitigacoes-mvp.md) — matriz de problemas, status na MVP, motivos e caminhos de evolução.
- [Proposta inicial de arquitetura](backend/proposta%20inicial/arquitetura-backend-senha.md) — documento conceitual inicial; pode refletir premissas anteriores à definição atual da MVP.

## Escopo conhecido

A MVP aceita perda de dados se Redis falhar, não implementa replay, não garante idempotência ponta a ponta e deixa a alternância dos turnos sob controle do frontend. A análise completa e os motivos desses débitos estão na [documentação de riscos](backend/mvp/tech-docs/riscos-e-mitigacoes-mvp.md).

## Estrutura

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
