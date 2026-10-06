# Proposta do App Android — Senha

Abra [o protótipo navegável](proposta-app.html) no navegador. É um arquivo independente (HTML/CSS/JS, sem dependências externas) com moldura de Android, navegação entre telas e simulações locais dos principais toques. Não chama o backend.

## Telas incluídas

1. **Boas-vindas** — explica o jogo e oferece cadastro ou acesso.
2. **Cadastro** — nome, e-mail e senha; conceito visual sujeito a um serviço de contas.
3. **Acesso com ID** — alternativa compatível com jogadores já existentes no backend.
4. **Início / lobby** — atalhos para criar ou entrar em partida e resumo do perfil.
5. **Criar partida** — informar o ID do oponente.
6. **Convite / aguardando** — compartilhar/copiar `gameplayId`, definir a própria senha e aguardar entrada.
7. **Entrar em partida** — colar/digitar um `gameplayId` existente.
8. **Definir senha** — quatro algarismos distintos, mantidos ocultos na interface.
9. **Partida ativa** — enviar palpites e acompanhar palpites/resultados do adversário no histórico.
10. **Resultado** — vitória, derrota ou timeout, com retorno ao lobby.
11. **Fluxo completo** — resumo visual da jornada.

O diagrama-fonte é [fluxo-telas.mmd](fluxo-telas.mmd), e sua renderização está em [fluxo-telas.png](fluxo-telas.png).

## Jornada principal

`Boas-vindas → Cadastro/Acesso → Início → Criar ou Entrar → Lobby/Senha → Partida ativa ⇄ palpites enviados/recebidos → Resultado → Início`.

No fluxo de criação, o primeiro jogador compartilha o `gameplayId`; o oponente entra com esse ID. A partida só começa quando o oponente entrou e os dois definiram suas senhas. A tela de jogo mantém um histórico de palpites de ambos e apresenta o retorno de corretos/parciais.

## Contrato com o backend atual

- Criar partida: `POST /games` com `playerId` e `opponentPlayerId`.
- Entrada/recusa: `PATCH /games/{gameplayId}/players/{playerId}/join` ou `/decline`.
- Definir senha: `PUT /games/{gameplayId}/players/{playerId}/secret`.
- Presença: `PATCH /games/{gameplayId}/players/{playerId}/heartbeat` (heartbeat a cada segundo enquanto ativa).
- Enviar palpite: `PUT /games/{gameplayId}/players/{playerId}/guess`.
- Atualizar a tela/receber palpites: `GET /games/{gameplayId}/state` — o contrato atual não oferece push/WebSocket.

Os botões do protótipo simulam esses passos localmente. Antes da integração, será necessário configurar URL/base de ambiente, estados de carregamento, falhas de rede, retries apropriados e tratamento de sessão.

## Decisões provisórias e lacunas

- **Cadastro:** o backend atual só valida `playerId` já cadastrado; não possui `POST /players`, login, senha de conta ou sessão. O formulário de cadastro é uma proposta visual e exige definir/implementar serviço de identidade. Até lá, o fluxo funcional para integração é “Acessar com ID existente”.
- **Convite:** o backend fornece `gameplayId`; link/compartilhamento externo é responsabilidade do App.
- **Receber palpites:** a API atual exige consultar o estado (polling); não há WebSocket/push. O intervalo de consulta será definido na integração, separado do heartbeat de 1 segundo.
- **Turnos:** o backend não faz cumprir alternância. O protótipo assume provisoriamente que o criador começa e que o App alterna após cada palpite; essa regra precisa ser confirmada e compartilhada entre aparelhos.
- **Resultado:** em vitória por palpite correto, o último palpite indica o vencedor. Em timeout, o contrato atual não identifica qual jogador parou de responder, então a tela deve mostrar motivo genérico.
- **Segurança:** a versão atual do backend não tem autenticação forte; não tratar `playerId` como identidade confiável em produção.

## Direção visual

Proposta Android em tema claro, inspiração Material 3, fundo azul-noite no entorno do aparelho, cards claros, acento verde-menta e dígitos em tiles. A navegação lateral do protótipo é apenas para revisão; no app, a navegação principal será feita pelos CTAs e pelo lobby.



[Proposta APP Android](https://htmlpreview.github.io/?https://github.com/rcoelho6/senha-the-guess/blob/feature/first-mvp/mobile/proposta-app.html)


