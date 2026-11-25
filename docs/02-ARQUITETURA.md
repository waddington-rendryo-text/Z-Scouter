# 02 — Arquitetura e Planejamento (SDD)

> **Commit de referência:** `docs: documenta arquitetura e planejamento`  
> **Estilo Arquitetural:** Clean Architecture adaptada para Android + MVVM (Model-View-ViewModel) com Unidirectional Data Flow (UDF).

---

## 1. Visão Geral da Arquitetura

O **Z-Scouter** foi estruturado para separar responsabilidades entre **apresentação visual**, **regras de negócio** e **fontes de dados (remota e local)**, facilitando manutenção, evolução incremental e testes unitários na JVM.

```text
+-------------------------------------------------------------------+
|                        PRESENTATION LAYER                         |
|  Jetpack Compose (Screens, Components, Theme) + ZScouterViewModel |
|  • StateFlow<UiState<T>>  • Eventos de UI  • Navigation / Dialogs |
+---------------------------------+---------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                           DOMAIN LAYER                            |
|  Regras de Negócio, Modelos de Domínio & Casos de Uso             |
|  • Cálculo de Ki & Patentes Saiyajin                              |
|  • Motor de Simulação Sparking (BattleResult & Heurística de Ki)  |
|  • Regras de Filtragem (Raça, Afiliação, Busca)                   |
+---------------------------------+---------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                            DATA LAYER                             |
|                ZScouterRepository (Single Source)                 |
|     +---------------------------+---------------------------+     |
|     |   Remote Data Source      |    Local Data Source      |     |
|     |   Retrofit + Moshi        |    Room Database + DAOs   |     |
|     |   DragonBallApiService    |    FigureDao / WorkoutDao |     |
|     +---------------------------+---------------------------+     |
+-------------------------------------------------------------------+
```

---

## 2. Detalhamento das Camadas (Clean Architecture)

### 2.1. Camada de Apresentação (`Presentation`)
Responsável exclusivamente por renderizar o estado da interface, capturar interações do usuário e observar fluxos reativos com lifecycle awareness.
- **Telas (`ui/screens/`):**
  - `RadarScreen.kt`: Galeria de personagens, banner herói do Vegeta, barra de busca e chips de filtro por raça.
  - `SaiyanModeScreen.kt`: HUD Scouter, citações motivacionais, formulário de registro de treinos e histórico.
  - `SparkingBattleScreen.kt`: Arena VS, seletor de combatentes e exibição animada do veredito do Scouter.
  - `FiguresShelfScreen.kt`: Abas para estante de *Action Figures* (CRUD + filtros de status) e catálogo de *Planetas Lore*.
- **Componentes Reutilizáveis (`ui/components/`):**
  - `ScouterHudCard.kt`, `CharacterCard.kt`, `CharacterDetailDialog.kt`, `ZBottomNavigation.kt`.
- **ViewModel (`ui/viewmodel/ZScouterViewModel.kt`):**
  - Gerencia estados tipados via `sealed interface UiState<out T>` (`Loading`, `Success`, `Error`).
  - Combina fluxos com `combine` (`filteredCharacters`) e expõe `StateFlow` para a UI.
- **Tema (`ui/theme/`):**
  - Centraliza paleta (`Color.kt`), tipografia com fonte local `Orbitron` (`Type.kt`) e esquema Material 3 (`Theme.kt`).

### 2.2. Camada de Domínio (`Domain`)
Concentra a lógica pura da aplicação, independente de frameworks de UI ou detalhes de persistência/rede:
- **Regras de Progressão Saiyajin:** Cálculo de Ki ganho por volume de treino (`calculateTotalKi`) e classificação em patentes (`getSaiyanRankTitle`).
- **Motor de Simulação de Combate:** `BattleResult` e `estimateKiScore`, responsáveis por interpretar strings heterogêneas de Ki da API (ex.: `"60.000.000"`, `"19.84 Septillion"`, `"Billion"`, `"Trillion"`), aplicar pesos de raça/personagem e gerar análise tática.
- **Regras de Filtragem:** Mapeamento entre filtros em português na UI (ex.: `"Saiyajin"`, `"Namekuseijin"`) e valores retornados em inglês pela API (`"Saiyan"`, `"Namekian"`).

> **Plano de Evolução Arquitetural:** À medida que novas regras forem adicionadas (como sincronização em nuvem ou geração de treinos personalizados), os casos de uso hoje encapsulados no `ZScouterViewModel` podem ser extraídos para classes dedicadas no pacote `domain/usecase/` (ex.: `CalculateWorkoutKiUseCase`, `SimulateBattleUseCase`, `FilterCharactersUseCase`) com interfaces de repositório em `domain/repository/`.

### 2.3. Camada de Dados (`Data`)
Responsável pela comunicação externa, serialização JSON e persistência local:
- **Remoto (`data/api/` e `data/model/`):**
  - `DragonBallApiService.kt` + `ApiClient`: Cliente Retrofit configurado com `OkHttpClient`, `HttpLoggingInterceptor` e `MoshiConverterFactory`.
  - `DragonBallModels.kt`: DTOs anotados com `@JsonClass(generateAdapter = true)` para geração em tempo de compilação via KSP.
- **Local (`data/local/`):**
  - `Entities.kt`: Tabelas `figures` (`FigureEntity`) e `workout_logs` (`WorkoutLogEntity`).
  - `Daos.kt`: `FigureDao` e `WorkoutDao` retornando `Flow<List<...>>` e `Flow<Long?>` para atualização automática da UI.
  - `AppDatabase.kt`: Instância singleton do Room (`z_scouter_database`).
- **Repositório (`data/repository/ZScouterRepository.kt`):**
  - Abstrai chamadas de rede e operações no Room usando `withContext(Dispatchers.IO)` e encapsulamento seguro com `Result<T>`.

---

## 3. Fluxo de Dados Unidirecional (UDF)

1. **Ação do Usuário:** Toque em um filtro, registro de treino ou botão *"Analisar com Scouter"*.
2. **Processamento:** O `ZScouterViewModel` dispara uma coroutine em `viewModelScope`, aplica regras de domínio ou aciona o `ZScouterRepository`.
3. **Atualização de Estado:** O repositório retorna `Result<T>` (API) ou emite novos valores no `Flow` (Room), atualizando os `StateFlow`s do ViewModel.
4. **Recomposição:** Os Composables coletam o estado via `collectAsState()` e atualizam apenas os nós visuais afetados.

---

## 4. Estratégia de Organização das Pastas e Pacotes

A estrutura segue agrupamento por **camada técnica + tipo de componente**, mantendo arquivos enxutos e altamente coesos:

```text
app/src/main/
├── AndroidManifest.xml                   # Permissões (INTERNET, ACCESS_NETWORK_STATE) e Activity principal
├── java/com/example/
│   ├── MainActivity.kt                   # Entry point, Edge-to-Edge, Scaffold, BackHandler e navegação por abas
│   ├── data/
│   │   ├── api/
│   │   │   └── DragonBallApiService.kt   # Interface Retrofit e singleton ApiClient
│   │   ├── local/
│   │   │   ├── AppDatabase.kt            # Configuração RoomDatabase
│   │   │   ├── Daos.kt                   # Consultas reativas (FigureDao, WorkoutDao)
│   │   │   └── Entities.kt               # Modelos relacionais (FigureEntity, WorkoutLogEntity)
│   │   ├── model/
│   │   │   └── DragonBallModels.kt       # DTOs da API (Characters, Transformations, Planets)
│   │   └── repository/
│   │       └── ZScouterRepository.kt     # Facade unificada entre API remota e Room local
│   └── ui/
│       ├── components/                   # Componentes visuais reutilizáveis e desacoplados
│       │   ├── CharacterCard.kt
│       │   ├── CharacterDetailDialog.kt
│       │   ├── ScouterHudCard.kt
│       │   └── ZBottomNavigation.kt
│       ├── screens/                      # Telas principais correspondentes às 4 abas
│       │   ├── FiguresShelfScreen.kt
│       │   ├── RadarScreen.kt
│       │   ├── SaiyanModeScreen.kt
│       │   └── SparkingBattleScreen.kt
│       ├── theme/                        # Design System Material 3
│       │   ├── Color.kt
│       │   ├── Theme.kt
│       │   └── Type.kt
│       └── viewmodel/
│           └── ZScouterViewModel.kt      # Estado reativo, filtros, treino e simulador
└── res/
    ├── drawable/                         # Banner do Vegeta, assets do ícone e layer-list
    ├── font/
    │   └── orbitron.ttf                  # Fonte futurista empacotada localmente
    ├── mipmap-*/                         # Ícones adaptativos e fallbacks PNG/round
    └── values/strings.xml                # Nome do app ("Z-Scouter") e recursos textuais
```

---

## 5. Decisões Técnicas e Trade-offs

- **Catálogo de Versões Centralizado (`gradle/libs.versions.toml`):** Mantém compatibilidade garantida entre AGP, Kotlin, Compose BOM, Room e KSP.
- **Room vs. Armazenamento em Memória:** Escolhido o Room para que treinos e coleção de miniaturas do usuário permaneçam salvos entre sessões mesmo sem conexão à internet.
- **Moshi + KSP em vez de Gson:** Maior segurança com nullability do Kotlin e menor overhead de reflexão.
- **Navegação por Estado + `BackHandler`:** Para 4 abas fixas de nível superior com troca instantânea (`Crossfade`) e diálogo modal de detalhes, reduz o boilerplate mantendo comportamento nativo do botão Voltar.
