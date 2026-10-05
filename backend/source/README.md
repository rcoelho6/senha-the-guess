# Backend — Jogo de senha

Projeto Maven multi-módulo baseado na arquitetura MVP. Usa Java 21 e Spring Boot 4.1.1, versão compatível com Java 21 conforme a documentação oficial.

## Módulos

- **`gameplay`**: API REST, lógica/domínio da partida, Redis como estado corrente e cliente HTTP para integração com Persistência. Dependências planejadas: Spring MVC, RestClient, validação e Spring Data Redis (Lettuce).
- **`persistence`**: API interna, validação de IDs e gravação histórica append-only. Dependências planejadas: Spring MVC, validação, Spring Data JPA/Hibernate e driver JDBC do PostgreSQL.
- Os dois módulos usam `spring-boot-starter-test` para a futura suíte de testes. As versões das dependências são gerenciadas pelo parent/BOM Spring Boot no `pom.xml` raiz.

## Build

Na raiz deste diretório, o reactor Maven reconhece os módulos com `mvn test` ou `mvn package` (requer Maven 3.6.3+ e JDK 21). As classes de aplicação ainda são placeholders vazios; por isso, embora a estrutura e dependências Maven estejam definidas, os serviços ainda não iniciam como aplicações Spring Boot. O reempacotamento executável do Spring Boot está desativado pela propriedade `spring-boot.repackage.skip` e deve ser habilitado quando as classes de inicialização forem implementadas.

## Estrutura

```text
source/
├── pom.xml
├── gameplay/
│   ├── pom.xml
│   └── src/{main,test}/...
└── persistence/
    ├── pom.xml
    └── src/{main,test}/...
```

Não há lógica, campos, métodos ou anotações nas classes nesta etapa.
