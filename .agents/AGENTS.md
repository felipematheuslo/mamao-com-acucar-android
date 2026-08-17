# Mamão com Açúcar - Android Native

Este arquivo serve como contexto base e guia de regras para Agentes de IA trabalhando neste projeto.

## 1. Visão Geral do Projeto
- **Nome**: Mamão com Açúcar
- **Descrição**: Um aplicativo colaborativo de mapeamento de árvores frutíferas urbanas.
- **Plataforma**: Android Nativo (Migrado de uma versão Web React/Vite).

## 2. Stack Tecnológico
- **Linguagem**: Kotlin (versão 2.0+)
- **Interface**: Jetpack Compose (Material 3)
- **Mapa**: OSMDroid (OpenStreetMap) usando a classe `XYTileSource` para mapas CartoDB (Dark, Positron, Voyager).
- **Backend/Autenticação**: Firebase Auth e Firestore.
- **Arquitetura**: MVVM (Model-View-ViewModel) utilizando `StateFlow` e `ViewModel`.

## 3. Estrutura de Diretórios
- `app/src/main/java/com/felipelaurindo/mamaocomacucar/`
  - `data/model/`: Classes de dados (User, Tree, etc.)
  - `ui/auth/`: Telas de Login e Registro.
  - `ui/map/`: Tela principal do mapa (`MapScreen.kt`) e componentes do OSMDroid.
  - `ui/map/components/`: Subcomponentes visuais e formulários (ex: `AddTreeDialog.kt`, `TreeListSheet.kt`).
  - `ui/settings/`: BottomSheets de configurações (`AccountSettingsSheet.kt`, `AppSettingsSheet.kt`).
  - `ui/theme/`: Definições de Cores, Tipografia e Temas Compose.
  - `util/`: Funções utilitárias (ex: formatação de distância).

## 4. Regras de Design e UI (Jetpack Compose)
- **Cores Principais**: `MamaoOrange` e `MamaoGreen` (definidas em `Color.kt`).
- **Tema**: Sempre utilize a paleta `Stone` (Stone50 a Stone900) para textos, bordas e fundos secundários. Evite cores genéricas como `Color.Black` ou `Color.Gray`.
- **Navegação (Edge-to-Edge)**: O app roda de ponta a ponta na tela. As barras de sistema (Status/Navigation) possuem ícones escuros (`SystemBarStyle.light` configurado no `MainActivity`) e fundo transparente.
- **Componentes**: 
  - Utilize `Surface` com `RoundedCornerShape` (12.dp a 20.dp) e `shadowElevation` (4.dp a 8.dp) para "Floating Cards" no mapa.
  - Se for empilhar textos/ícones, cuidado com o uso da `Row` e expansões laterais (`weight(1f)`); utilize `maxLines = 1` e `TextOverflow.Ellipsis` quando o texto for dinâmico para evitar layouts quebrados.

## 5. Regras de Negócio e Dados
- **Árvores (Trees)**: Possuem `TreeStatus` (FLOR = 🌸, VERDE = 🍏, PRONTO = 🍎, SECO = 🍂).
- **Gamificação (Badges)**: O `UserBadge` do Firestore muda com base na quantidade de árvores cadastradas (ex: 0 = "SEMENTINHA 🌱", 100+ = "MESTRE FRUTÍFERO 🍒").
- **FireStore**: Documentos de árvore guardam latitude/longitude que devem ser lidos para criar `GeoPoint` e marcadores (`Marker`) no mapa.

## 6. Boas Práticas e Gotchas (Armadilhas Comuns)
- **Gradle & Kotlin Versions**: Nunca atualize arbitrariamente o `firebase-bom` ou dependências do `AndroidX` sem garantir que são compatíveis com a versão do Kotlin do projeto e o `compileSdk 36`.
- **OSMDroid**: 
  - Sempre inicializar a configuração com `Configuration.getInstance().load(context, preferences)` no `MainActivity` para ativar o cache local de tiles e garantir fluidez no mapa.
  - A interface `ITileSource` tem a propriedade de nome mapeada como `.name()`, e **não** `.name`.
  - Atualizar marcadores na thread correta e sempre chamar `mapView.invalidate()` após modificações dinâmicas.
- **Previews**: Se fizer componentes visuais, evite dependências pesadas do Firebase nas assinaturas (passe lambdas e primitivas) para não quebrar `@Preview` no Compose.
