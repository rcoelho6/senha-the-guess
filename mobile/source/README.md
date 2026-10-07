# Senha — app Android nativo

Projeto Android local em **Kotlin + Jetpack Compose + Material 3**. O fluxo está baseado na proposta em [`../proposta-app.html`](../proposta-app.html).

## Abrir e executar

1. Instale uma versão atual do Android Studio e o Android SDK Platform 37.
2. Abra a pasta `mobile/source` no Android Studio e aguarde a sincronização do Gradle.
3. Inicie um emulador Android (API 26 ou superior) ou conecte um aparelho com depuração USB.
4. Execute a configuração `app`.

Pelo terminal PowerShell, depois de instalar o SDK e aceitar as licenças:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

O APK de depuração será gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Funcionalidades da demonstração

- Boas-vindas, cadastro local demonstrativo e acesso por ID.
- Lobby, criação/entrada em partida, código de convite com copiar/compartilhar Android.
- Definição da senha com teclado numérico e validação de quatro algarismos distintos.
- Palpites próprios e simulados do oponente, com contagem de corretos/parciais.
- Tela de regras, resultado e simulação de timeout.
- ViewModel com `StateFlow`, navegação AndroidX e regras puras cobertas por testes unitários.

## Limites

O aplicativo **não chama o backend** nesta etapa. Perfil, partida e palpites são locais e em memória; a entrada e as ações do oponente são simuladas. O cadastro não cria uma conta real. Antes da integração, será necessário decidir identidade/login, turnos, polling do estado, heartbeat e reconexão, conforme descrito em [`../README.md`](../README.md).

## Toolchain

Gradle Wrapper 9.6.0, Android Gradle Plugin 9.4.0, Kotlin 2.4.20, Compose BOM 2026.09.00 e Navigation Compose 2.10.2. A máquina usada para gerar o projeto tem JDK 21, mas não tinha Android SDK instalado; a compilação precisa do SDK Platform 37 e Build Tools configurados.
