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
Camada 100% Kotlin puro (sem dependências de Room, Retrofit, Moshi ou Android), concentrando modelos de negócio, contratos de repositório e casos de uso:
- **Contratos (`domain/repository/`):**
  - `DragonBallRepository.kt`: Interface pura que abstrai as operações remotas e locais para os casos de uso.
- **Modelos de Domínio (`domain/model/`):**
  - `Character.kt`: `Character`, `CharacterDetail`, `Transformation`, `OriginPlanet` e `Planet`.
  - `ActionFigure.kt`: `ActionFigure` e enum `FigureStatus` (`EXPOSED`, `BOXED`, `WISHLIST`).
  - `WorkoutLog.kt`: `WorkoutLog` e `SaiyanRank`.
  - `BattleResult.kt`: Resultado estruturado da simulação de combate no Scouter.
- **Casos de Uso (`domain/usecase/`):**
  - `GetCharactersUseCase.kt` / `GetCharacterDetailUseCase.kt` / `GetPlanetsUseCase.kt`: Orquestração reativa e consulta de dados do repositório.
  - `FilterCharactersUseCase.kt`: Mapeamento e filtragem por nome, raça e afiliação.
  - `CalculateSaiyanRankUseCase.kt`: Cálculo de Ki ganho por treino (`calculateWorkoutKi`), Ki total (`calculateTotalKi`) e patente Saiyajin (`getSaiyanRank`).
  - `ManageWorkoutsUseCase.kt`: Validação de séries/reps/carga e registro/exclusão de treinos.
  - `ManageFiguresUseCase.kt`: Regras de coleção, toggle, edição de status/notas e seed inicial.
  - `SimulateBattleUseCase.kt`: Heurística de poder (`estimateKiScore`) e veredito do duelo estilo *Sparking! ZERO*.

### 2.3. Camada de Dados (`Data`) — Estratégia Offline-First
Responsável pela comunicação externa, conversão DTO/Entity e persistência local:
- **Remoto (`data/api/` e `data/model/`):**
  - `DragonBallApiService.kt`: Interface Retrofit para os endpoints da Dragon Ball API.
  - `DragonBallModels.kt`: DTOs anotados com `@JsonClass(generateAdapter = true)` para geração em tempo de compilação via KSP.
- **Mappers (`data/mapper/`):**
  - `DragonBallMapper.kt`: Converte DTOs da API em entidades do Room (`CharacterEntity`, `PlanetEntity`) e reconstrói detalhes completos (`originPlanet`, `transformations`) a partir do cache JSON local.
- **Local (`data/local/`):**
  - `Entities.kt`: Tabelas `characters` (`CharacterEntity`), `planets` (`PlanetEntity`), `figures` (`FigureEntity`) e `workout_logs` (`WorkoutLogEntity`).
  - `Daos.kt`: `CharacterDao`, `PlanetDao`, `FigureDao` e `WorkoutDao` retornando `Flow<List<...>>` e consultas pontuais para fallback offline.
  - `AppDatabase.kt`: Instância singleton do Room (`z_scouter_database`, versão 2).
- **Repositório (`data/repository/ZScouterRepository.kt`):**
  - Implementa `DragonBallRepository` com estratégia **Offline-First**: sincroniza respostas da API para o Room, expõe `Flow`s locais como fonte reativa de dados e realiza fallback automático para o cache local em falhas de rede (`isOfflineMode`).

### 2.4. Injeção de Dependências (`DI` com Koin)
Módulos globais configurados em `di/` e inicializados em `ZScouterApplication`:
- `NetworkModule.kt`: Provê `Moshi`, `HttpLoggingInterceptor`, `OkHttpClient`, `Retrofit` e `DragonBallApiService`.
- `DatabaseModule.kt`: Provê `AppDatabase`, `CharacterDao`, `PlanetDao`, `FigureDao` e `WorkoutDao`.
- `RepositoryModule.kt`: Provê `DragonBallMapper` e vincula `DragonBallRepository` à implementação `ZScouterRepository`.
- `DomainModule.kt`: Provê os UseCases (`FilterCharactersUseCase`, `CalculateSaiyanRankUseCase`, `SimulateBattleUseCase`).
- `ViewModelModule.kt`: Provê `ZScouterViewModel` e consolida `appModules`.

---

## 3. Fluxo de Dados Unidirecional (UDF)

1. **Ação do Usuário:** Toque em um filtro, registro de treino ou botão *"Analisar com Scouter"*.
2. **Processamento:** O `ZScouterViewModel` (injetado via `koinViewModel()`) dispara uma coroutine em `viewModelScope`, aplica os UseCases de domínio ou aciona o `DragonBallRepository`.
3. **Atualização de Estado:** O repositório retorna `Result<T>` (API) ou emite novos valores no `Flow` (Room), atualizando os `StateFlow`s do ViewModel.
4. **Recomposição:** Os Composables coletam o estado via `collectAsState()` e atualizam apenas os nós visuais afetados.

---

## 4. Estratégia de Organização das Pastas e Pacotes

A estrutura segue agrupamento por **camada da Clean Architecture (`data`, `domain`, `ui`, `di`)**, mantendo arquivos enxutos e altamente coesos:

```text
app/src/main/
├── AndroidManifest.xml                   # Permissões (INTERNET, ACCESS_NETWORK_STATE) e ZScouterApplication
├── java/com/example/
│   ├── MainActivity.kt                   # Entry point, Edge-to-Edge, Scaffold, BackHandler e koinViewModel()
│   ├── ZScouterApplication.kt            # Inicialização global do Koin (startKoin)
│   ├── di/
│   │   ├── DatabaseModule.kt             # Módulo Room (AppDatabase + DAOs)
│   │   ├── DomainModule.kt               # Módulo dos UseCases
│   │   ├── NetworkModule.kt              # Módulo Retrofit, OkHttp e Moshi
│   │   ├── RepositoryModule.kt           # Bind DragonBallRepository -> ZScouterRepository
│   │   └── ViewModelModule.kt            # Módulo ViewModel + lista appModules
│   ├── domain/
│   │   ├── model/
│   │   │   ├── ActionFigure.kt           # ActionFigure e FigureStatus
│   │   │   ├── BattleResult.kt           # Resultado do simulador de duelo
│   │   │   ├── Character.kt              # Character, CharacterDetail, Planet, Transformation
│   │   │   └── WorkoutLog.kt             # WorkoutLog e SaiyanRank
│   │   ├── repository/
│   │   │   └── DragonBallRepository.kt   # Contrato puro da camada de dados
│   │   └── usecase/
│   │       ├── CalculateSaiyanRankUseCase.kt
│   │       ├── FilterCharactersUseCase.kt
│   │       ├── GetCharacterDetailUseCase.kt
│   │       ├── GetCharactersUseCase.kt
│   │       ├── GetPlanetsUseCase.kt
│   │       ├── ManageFiguresUseCase.kt
│   │       ├── ManageWorkoutsUseCase.kt
│   │       └── SimulateBattleUseCase.kt
│   ├── data/
│   │   ├── api/
│   │   │   └── DragonBallApiService.kt   # Interface Retrofit
│   │   ├── local/
│   │   │   ├── AppDatabase.kt            # Configuração RoomDatabase (v2)
│   │   │   ├── Daos.kt                   # CharacterDao, PlanetDao, FigureDao, WorkoutDao
│   │   │   └── Entities.kt               # CharacterEntity, PlanetEntity, FigureEntity, WorkoutLogEntity
│   │   ├── mapper/
│   │   │   └── DragonBallMapper.kt       # Conversores DTO <-> Entity
│   │   ├── model/
│   │   │   └── DragonBallModels.kt       # DTOs da API (Characters, Transformations, Planets)
│   │   └── repository/
│   │       └── ZScouterRepository.kt     # Repositório Offline-First (Room + Retrofit)
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
│           └── ZScouterViewModel.kt      # Estado reativo consumindo UseCases e Repository
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
