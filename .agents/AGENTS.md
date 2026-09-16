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
- **Mapas**: OSMDroid (`org.osmdroid:osmdroid-android:6.1.20`) com suporte a mapa Clássico (*OpenStreetMap* padrão Leaflet) e imagens de Satélite (*Esri World Imagery*), ambos livres e sem marcas d'água.
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
│   ├── FruitCatalogData.kt        # Catálogo botânico estático (68 espécies, nomes científicos, biomas e descrições)
│   ├── FruitsData.kt              # Lista de espécies de frutas permitidas e sugestões rápidas (ALLOWED_FRUITS)
│   ├── model/
│   │   ├── CommentUpdate.kt       # Modelo de atualização de status e comentários reportados
│   │   ├── TreeItem.kt            # Modelo da árvore (TreeItem) e enum de status biológico (TreeStatus)
│   │   └── UserProfile.kt         # Modelos de perfil (UserProfile) e usuário da sessão (LoggedUser)
│   └── repository/
│       └── FirestoreRepository.kt # Operações com Firestore (usuários, árvores e subcoleções de updates)
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
│   │   ├── TreeDetailSheet.kt     # BottomSheet com detalhes da árvore e formulário de nova atualização
│   │   ├── TreeListSheet.kt       # BottomSheet de busca, filtragem por status e ordenação por distância
│   │   └── components/
│   │       ├── AddTreeDialog.kt       # Diálogo para cadastrar nova fruteira nas coordenadas selecionadas
│   │       ├── AdMobBanner.kt         # Container e carregamento do banner AdMob
│   │       ├── FloatingSearchBar.kt   # Barra flutuante de busca rápida e atalho ao perfil do usuário
│   │       ├── FruitCatalogSheet.kt   # BottomSheet do Guia Botânico (chips de biomas e busca por espécie)
│   │       ├── MapBottomBar.kt        # Barra inferior unificada com AdMob ancorado e 3 destinos (Explorar, Mapear, Catálogo)
│   │       ├── ToastOverlay.kt        # Notificações estilo Toast animadas nativas
│   │       ├── TreeClusterOverlay.kt  # Algoritmo e renderização de clusters de marcadores
│   │       ├── TreeMarkerUtils.kt     # Criação de drawables e ícones de marcadores customizados
│   │       └── TreeQuickPreviewCard.kt # Card compacto prévia ao tocar em marcador do mapa
│   ├── settings/
│   │   ├── AccountSettingsSheet.kt # BottomSheet de perfil e estatísticas do usuário
│   │   └── AppSettingsSheet.kt    # BottomSheet de troca de estilo do mapa (Relevo, Satélite)
│   └── theme/
│       ├── Color.kt               # Cores da marca (MamaoOrange, MamaoGreen) e paleta Stone (Stone50..Stone950)
│       ├── Theme.kt               # Tema principal `MamaoComAcucarTheme`
│       └── Type.kt                # Tipografia Material 3 personalizada
└── util/
    ├── FruitIconUtils.kt          # Mapeamento de espécies de frutas para drawables vetoriais
    ├── LocationUtils.kt           # Utilitários de geolocalização e cálculo de distância (Haversine/Formatters)
    └── StringUtils.kt             # Utilitários de normalização de strings e usernames
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

### Biomas e Categorias do Guia Botânico (`FruitCatalogData`)
- `Populares`: Frutas amplamente consumidas e cultivadas em quintais e praças.
- `Cerrado`: Espécies nativas do bioma Cerrado (ex: Pequi, Araticum, Buriti, Cagaita, Gabiroba, Jatobá, Macaúba, Mangaba, Murici).
- `Caatinga`: Espécies do semiárido e Caatinga (ex: Umbu, Juá, Carnaúba).
- `Amazônia`: Espécies florestais amazônicas (ex: Açaí, Cupuaçu, Bacuri, Guaraná, Tucumã).
- `Mata Atlântica`: Frutas da faixa atlântica (ex: Jabuticaba, Pitanga, Grumixama, Uvaia).
- `Nativas Raras`: Espécies menos conhecidas e silvestres (ex: Maba, Jandiroba, Cherimoia).

### Gamificação (`UserBadge` em `MapViewModel.kt`)
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

1. **Z-Ordering no Compose Box do MapScreen**:
   - `MapBottomBar` deve ser declarado **após** os Bottom Sheets (`TreeListSheet`, `FruitCatalogSheet`, `TreeDetailSheet`) na árvore Compose para garantir que a barra inferior permaneça com o z-index superior, visível e clicável.
   - Os Bottom Sheets devem aplicar `.padding(bottom = 64.dp).navigationBarsPadding()` na sua `Surface` para que seu conteúdo interno e botões de ação nunca fiquem escondidos sob a barra inferior.

2. **Estado de Expansão de Bottom Sheets**:
   - Sheets informativos (`TreeListSheet`, `FruitCatalogSheet`) devem inicializar com `isExpanded = true` (`0.85f` e `0.88f` de altura da tela). Isso garante abertura total no primeiro clique, com ícone de seta apontando para baixo `⌄` para permitir que o usuário comprima para `0.52f` se desejar espiar o mapa.

3. **Performance de Animação e Curvas**:
   - Para animações de subida e descida de cards e transição de altura, use `tween(200..220, easing = FastOutSlowInEasing)` em vez de springs padrão com baixa rigidez, garantindo resposta tátil imediata (60fps) essencial em uso outdoor.

4. **Inicialização do OSMDroid**:
   - Sempre executar `Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))` no `onCreate` da `MainActivity`. Sem essa linha, o cache local em disco fica inativo e o mapa apresentará lentidão no carregamento de tiles.
   - Defina o `userAgentValue` com o nome do pacote do app para evitar bloqueio por parte das APIs de tiles.

5. **Propriedades da Interface OSMDroid**:
   - A propriedade do nome do TileSource é um método no OSMDroid: use `.name()` e **não** `.name`.

6. **Animações no Mapa**:
   - Para mover a câmera com zoom de forma suave e fluida no botão de GPS ou seleção de item, utilize a sobrecarga `animateTo(GeoPoint(lat, lng), zoomLevel, durationMillis)` do `MapController`. Evite chamar `setZoom()` seguido de `animateTo()`.

7. **Componentes e Previews no Compose**:
   - Evite passar dependências diretas de ViewModels ou instâncias do Firebase nas assinaturas de subcomponentes Compose. Prefira receber dados primitivos/lambdas para garantir que os `@Preview` do Android Studio continuem compilando e funcionando.

8. **Compatibilidade de Dependências**:
   - Não atualize a versão do `firebase-bom` ou dependências do `AndroidX` de maneira arbitrária sem checar a compatibilidade com o Kotlin 2.0+ e o `compileSdk 36`.

9. **Comandos de Build & Verificação no Terminal (Gradle Wrapper)**:
   - Para verificar compilação e tipagem Kotlin rapidamente: `.\gradlew compileDebugSources`
   - Para gerar o APK de Debug completo: `.\gradlew assembleDebug`

10. **Ergonomia e Políticas de AdMob**:
    - O banner AdMob ancorado na base (`MapBottomBar`) deve manter separação física segura dos controles interativos e botões de ação (como o FAB de GPS) para prevenir cliques acidentais e manter conformidade estrita com as políticas do Google Play. Nunca sobreponha o banner com elementos sem opção clara de dismiss.

11. **Tratamento de Teclado e Insets (`imePadding`)**:
    - Em telas, diálogos e sheets contendo campos de texto (`TextField` / `OutlinedTextField`), como em `AddTreeDialog`, `LoginScreen` e `RegisterScreen`, utilize sempre `Modifier.imePadding()` em conjunto com scroll vertical (`Modifier.verticalScroll()`) para que o teclado virtual do Android não cubra os campos de digitação nem os botões de ação.


