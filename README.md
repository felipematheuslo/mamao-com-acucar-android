# 🥭 Mamão com Açúcar - Android Nativo

> Aplicativo colaborativo para mapeamento e monitoramento de árvores frutíferas urbanas.

---

## 📌 Sobre o Projeto

O **Mamão com Açúcar** é uma plataforma comunitária que permite aos cidadãos mapear, encontrar e atualizar o estado de árvores frutíferas espalhadas pelas cidades. Com ele, você pode identificar onde há frutas maduras na sua região, registrar novas fruteiras no mapa e acompanhar as fases de floração e frutificação de cada árvore.

Originalmente construído como uma aplicação Web (React/Vite), este repositório contém a versão **Android Nativa** desenvolvida do zero em **Kotlin** com **Jetpack Compose**.

---

## ✨ Funcionalidades Principais

- 🗺️ **Mapeamento Interativo**: Visualização de mapas de alta performance com OpenStreetMap (OSMDroid) e estilos personalizados (*Voyager*, *Dark*, *Positron*, *Mapnik*).
- 📍 **Cadastro de Fruteiras (Mapear)**: Pinagem e posicionamento direto no mapa para cadastrar novas árvores com coordenadas precisas, espécie e estado inicial.
- 🌸 **Status das Fruteiras**: Acompanhamento do ciclo de vida da fruteira com emojis indicativos:
  - 🌸 **Em Flor**
  - 🍏 **Verde / Crescendo**
  - 🍎 **Pronto para Colheita**
  - 🍂 **Seco / Sem Frutos**
- 🔍 **Busca & Filtros**: Lista interativa de árvores com cálculo de distância em relação à localização atual do usuário e filtros por status.
- 💬 **Atualizações Colaborativas**: Histórico de status e comentários reportados pela comunidade para cada árvore.
- 🏆 **Gamificação & Conquistas**: Sistema de badges (*Sementinha*, *Mestre Frutífero*, etc.) atribuídos com base na contribuição do usuário no mapeamento urbano.
- ⚙️ **Configurações & Estilo**: Alteração em tempo real do estilo do mapa, gerenciamento da conta e tema visual refinado.

---

## 🛠️ Stack Tecnológico

| Camada | Tecnologia |
| :--- | :--- |
| **Linguagem** | Kotlin 2.0+ |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Arquitetura** | MVVM (Model-View-ViewModel) + StateFlow |
| **Navegação** | Navigation Compose + Edge-to-Edge (`enableEdgeToEdge`) |
| **Mapas** | OSMDroid (`org.osmdroid:osmdroid-android`) + TileCache otimizado |
| **Backend & Banco de Dados** | Firebase Auth & Cloud Firestore |
| **Gerenciador de Dependências** | Gradle (Kotlin DSL - `build.gradle.kts`) |
| **Mínimo SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 36 |

---

## 📂 Estrutura do Projeto

```text
app/src/main/java/com/felipelaurindo/mamaocomacucar/
├── data/
│   ├── model/           # Data classes (LoggedUser, Tree, TreeUpdate, TreeStatus, UserBadge)
│   └── repository/      # Camada de repositório Firebase / Firestore
├── ui/
│   ├── auth/            # Telas de Login (LoginScreen.kt) e Registro (RegisterScreen.kt)
│   ├── map/             # Tela do Mapa (MapScreen.kt) e ViewModel (MapViewModel.kt)
│   │   └── components/  # Componentes (AddTreeDialog, TreeDetailSheet, TreeListSheet, ToastOverlay)
│   ├── settings/        # BottomSheets de Ajustes (AccountSettingsSheet, AppSettingsSheet)
│   └── theme/           # Cores (Color.kt), Tipografia (Type.kt) e Tema Compose (Theme.kt)
└── util/                # Utilitários (Formatters, Geolocalização, etc.)
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
1. **Android Studio** (versão Jellyfish / Koala ou superior recomendada).
2. **JDK 11** ou superior configurado no ambiente.
3. Dispositivo físico com Android 7.0+ ou Emulador configurado com Google Play Services.

### Passo a Passo

1. **Clonar o Repositório**:
   ```bash
   git clone https://github.com/felipematheuslo/mamao-com-acucar-android.git
   cd mamao-com-acucar-android
   ```

2. **Configurar o Firebase**:
   - Certifique-se de que o arquivo `google-services.json` está presente na pasta `app/`.

3. **Compilar e Executar**:
   - Abra o projeto no Android Studio.
   - Aguarde o término da sincronização do Gradle (*Gradle Sync*).
   - Clique em **Run** (`Shift + F10`) com o emulador ou dispositivo conectado.

---

## 🎨 Guia de Design (UI/UX)

- **Cores da Marca**:
  - `MamaoOrange` (`#F97316`) — Cor primária de ação e destaque.
  - `MamaoGreen` (`#16A34A`) — Cor de suporte para elementos da natureza.
- **Paleta Neutra**: Paleta `Stone` (Stone50 a Stone900) para fundos, cartões suspensos (*Floating Cards*) e tipografia.
- **Experiência Edge-to-Edge**: Barras do sistema transparentes com ícones adaptativos `SystemBarStyle.light`.

---

## 📄 Licença

Este projeto é desenvolvido para fins comunitários e de código aberto. Sinta-se à vontade para contribuir!
