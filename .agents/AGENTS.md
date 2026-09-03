# 🥭 Mamão com Açúcar - Guia do Agente de IA (AGENTS.md)

Este arquivo serve como contexto base, diretrizes de arquitetura, regras de negócio e boas práticas para Agentes de IA que trabalham neste repositório.

---

## 1. Visão Geral do Projeto
- **Nome**: Mamão com Açúcar
- **Descrição**: Aplicativo colaborativo de mapeamento urbano e monitoramento do ciclo de frutos em árvores públicas.
- **Plataforma**: Android Nativo (migrado da versão Web React/Vite, mantendo paridade de recursos e identidade visual).
- **Repositório**: `felipematheuslo/mamao-com-acucar-android`

---

## 2. Stack Tecnológico e Versões
- **Linguagem**: Kotlin (2.0+)
- **UI Framework**: Jetpack Compose com Material Design 3 (Material 3)
- **Navegação & Edge-to-Edge**: `androidx.navigation:navigation-compose:2.9.0` e `ComponentActivity.enableEdgeToEdge()`
- **Mapas**: OSMDroid (`org.osmdroid:osmdroid-android:6.1.20`) com suporte a Relevo (*Esri World Topo Map*) e imagens de Satélite (*Esri World Imagery*), ambos livres e sem marcas d'água.
- **Backend & Persistence**: Firebase Auth (Autenticação) e Cloud Firestore (Banco de Dados NoSQL em tempo real).
- **Arquitetura**: MVVM (Model-View-ViewModel) com `StateFlow`, `collectAsState()` e `ViewModel`.
- **Target / Compile SDK**: API 36 (Min SDK 24).

---

## 3. Estrutura Detalhada de Arquivos

```text
app/src/main/java/com/felipelaurindo/mamaocomacucar/
├── MainActivity.kt                # Activity principal, configura Edge-to-Edge e inicializa OSMDroid Configuration
├── MamaoApp.kt                    # NavHost principal e controle do estado de autenticação (LoggedUser)
├── data/
│   └── model/
│       ├── LoggedUser.kt          # Modelo de usuário autenticado (uid, email, displayName, username, etc.)
│       ├── Tree.kt                # Modelo de fruteira (id, name, species, lat, lng, currentStatus, etc.)
│       ├── TreeStatus.kt          # Enum de status (FLOR, VERDE, PRONTO, SECO) e meta-informações (emoji, cor)
│       ├── TreeUpdate.kt          # Modelo de atualização de status/comentário de uma árvore
│       └── UserBadge.kt           # Lógica de gamificação (nível/selo do usuário por contagem de árvores)
├── ui/
│   ├── auth/
│   │   ├── AuthViewModel.kt       # ViewModel de autenticação (Email, Google Sign-In, recuperação de senha)
│   │   ├── LoginScreen.kt         # Tela de autenticação/login
│   │   ├── RegisterScreen.kt      # Tela de registro de novo usuário
│   │   └── components/
│   │       ├── ForgotPasswordDialog.kt # Diálogo para envio de e-mail de redefinição de senha
│   │       └── GoogleSignInButton.kt   # Botão de login com Google via Credential Manager
│   ├── map/
│   │   ├── MapScreen.kt           # Tela principal do mapa, com OSMDroid, FAB de GPS e overlays
│   │   ├── MapViewModel.kt        # ViewModel gerenciador de árvores, localização e estados da UI
│   │   └── components/
│   │       ├── AddTreeDialog.kt   # Diálogo para cadastrar nova fruteira nas coordenadas selecionadas
│   │       ├── TreeDetailSheet.kt # BottomSheet com detalhes da árvore e formulário de nova atualização
│   │       ├── TreeListSheet.kt   # BottomSheet de busca, filtragem e ordenação por distância
│   │       └── ToastOverlay.kt    # Notificações estilo Toast animadas nativas
│   ├── settings/
│   │   ├── AccountSettingsSheet.kt # BottomSheet de perfil e estatísticas do usuário
│   │   └── AppSettingsSheet.kt    # BottomSheet de troca de estilo do mapa (Relevo, Satélite)
│   └── theme/
│       ├── Color.kt               # Cores da marca (MamaoOrange, MamaoGreen) e paleta Stone (Stone50..Stone950)
│       ├── Theme.kt               # Tema principal `MamaoComAcucarTheme`
│       └── Type.kt                # Tipografia Material 3 personalizada
└── util/
    └── LocationUtils.kt           # Utilitários de geolocalização e cálculo de distância (Haversine/Formatters)
```

---

## 4. Regras de Design e UI (Jetpack Compose)

### Cores e Paleta
- **Cores Principais**:
  - `MamaoOrange` (`#F97316`) e `MamaoOrangeLight` (`#FFF7ED`)
  - `MamaoGreen` (`#16A34A`) e `MamaoGreenLight` (`#F0FDF4`)
- **Paleta Neutra (Mandatória)**: Utilize **sempre** as constantes de cor da família `Stone` (`Stone50` a `Stone950`) definidas em `Color.kt`. **Nunca** utilize `Color.Black`, `Color.Gray` ou cores puras/genéricas diretamente.

### Componentes e Layouts
- **Floating Cards**: Use `Surface` com `RoundedCornerShape` (12.dp a 20.dp), bordas sutis `Stone200` e elevação (`shadowElevation = 4.dp` a `12.dp`).
- **Prevenção de Quebras de Layout**: Ao empilhar elementos em `Row` com pesos (`weight(1f)`), sempre inclua `maxLines = 1` e `overflow = TextOverflow.Ellipsis` para campos de texto dinâmicos (como nomes de usuários ou espécies de árvores).
- **Edge-to-Edge**: O app roda de ponta a ponta. Utilize os modifiers `.statusBarsPadding()` e `.navigationBarsPadding()` adequadamente em componentes suspensos (Header, FABs, NavBars).

---

## 5. Regras de Negócio e Dados

### Status da Árvore (`TreeStatus`)
- `FLOR` 🌸: Em floração.
- `VERDE` 🍏: Fruto verde / em crescimento.
- `PRONTO` 🍎: Fruto maduro / pronto para colheita.
- `SECO` 🍂: Árvore sem frutos ou fora de época.

### Gamificação (`UserBadge`)
O progresso do usuário é determinado pela contagem de árvores cadastradas no Firestore:
- `0` árvores: **SEMENTINHA 🌱**
- `1+` árvores: **BROTINHO 🌿**
- `5+` árvores: **CULTIVADOR 🪴**
- `10+` árvores: **PROTETOR DA FLORESTA 🌳**
- `25+` árvores: **GUARDIÃO DAS FRUTAS 🍊**
- `50+` árvores: **MESTRE FRUTÍFERO 🍒**

### Firestore Schemas
- **`users/{uid}`**: Perfil do usuário (displayName, username, email, treeCount).
- **`trees/{treeId}`**: Cadastro da fruteira (`name`, `species`, `latitude`, `longitude`, `currentStatus`, `createdByUid`, `createdAt`).
- **`trees/{treeId}/updates/{updateId}`**: Histórico de alterações de status e comentários reportados.

---

## 6. Boas Práticas e Gotchas Técnicos (Crucial para IAs)

1. **Inicialização do OSMDroid**:
   - Sempre executar `Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))` no `onCreate` da `MainActivity`. Sem essa linha, o cache local em disco fica inativo e o mapa apresentará lentidão no carregamento de tiles.
   - Defina o `userAgentValue` com o nome do pacote do app para evitar bloqueio por parte das APIs de tiles.

2. **Propriedades da Interface OSMDroid**:
   - A propriedade do nome do TileSource é um método no OSMDroid: use `.name()` e **não** `.name`.

3. **Animações no Mapa**:
   - Para mover a câmera com zoom de forma suave e fluida no botão de GPS ou seleção de item, utilize a sobrecarga `animateTo(GeoPoint(lat, lng), zoomLevel, durationMillis)` do `MapController`. Evite chamar `setZoom()` seguido de `animateTo()`.

4. **Componentes e Previews no Compose**:
   - Evite passar dependências diretas de ViewModels ou instâncias do Firebase nas assinaturas de subcomponentes Compose. Prefira receber dados primitivos/lambdas para garantir que os `@Preview` do Android Studio continuem compilando e funcionando.

5. **Compatibilidade de Dependências**:
   - Não atualize a versão do `firebase-bom` ou dependências do `AndroidX` de maneira arbitrária sem checar a compatibilidade com o Kotlin 2.0+ e o `compileSdk 36`.

