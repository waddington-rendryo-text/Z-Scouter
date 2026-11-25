# 03 — Decomposição de Tarefas (SDD)

> **Commit de referência:** `docs: adiciona decomposição de tarefas`  
> **Objetivo:** Dividir o desenvolvimento do **Z-Scouter** em etapas lógicas, sequenciais e verificáveis, facilitando o acompanhamento de progresso e a organização de commits no GitHub.

---

## Visão Geral das Etapas de Execução

1. **Etapa 1 — Documentação e Planejamento (SDD)**
2. **Etapa 2 — Setup do Projeto, Identidade Visual e Assets**
3. **Etapa 3 — Camada de Dados (API REST + Persistência Local Room)**
4. **Etapa 4 — Camada de Domínio/ViewModel e Navegação Base**
5. **Etapa 5 — Implementação das Telas e Componentes de UI**
6. **Etapa 6 — Testes, Validação de Build e Polimento Final**

---

## Etapa 1: Documentação e Planejamento (SDD)

- [x] **T1.1 — Especificação Inicial (`docs/01-ESPECIFICACAO.md`)**
  - Definir persona (*Lucas "Luke" Silva*), problema resolvido, endpoints da *Dragon Ball API*, requisitos funcionais (RF01–RF06), não funcionais (RNF01–RNF05) e regras de negócio.
- [x] **T1.2 — Arquitetura e Planejamento (`docs/02-ARQUITETURA.md`)**
  - Documentar separação em camadas (*Presentation, Domain, Data*), fluxo UDF, organização de pastas e decisões técnicas.
- [x] **T1.3 — Decomposição de Tarefas & README (`docs/03-DECOMPOSICAO-TAREFAS.md`, `README.md`)**
  - Quebrar entregas em tarefas atômicas com critérios de aceite e links de navegação para o repositório GitHub.

---

## Etapa 2: Setup do Projeto, Identidade Visual e Assets

- [x] **T2.1 — Configuração Gradle e Permissões**
  - Atualizar `applicationId` único (`com.aistudio.zscouter.kxylrq`) em `app/build.gradle.kts`.
  - Habilitar `coil-compose` e confirmar dependências de Retrofit, Moshi, Room e KSP.
  - Declarar `INTERNET` e `ACCESS_NETWORK_STATE` em `AndroidManifest.xml`.
  - Atualizar nome do app para `Z-Scouter` em `strings.xml`, `settings.gradle.kts` e `metadata.json`.
- [x] **T2.2 — Assets Visuais e Ícone Adaptativo**
  - Gerar ícone personalizado do Scouter com Esfera de 4 Estrelas (`ic_launcher_foreground.xml`, `ic_launcher_background.xml` em `#0D1117` e mipmaps PNG/round).
  - Gerar banner estilizado do Vegeta (`vegeta_banner.jpg`) para o cabeçalho do Radar Z.
- [x] **T2.3 — Design System (Tema e Tipografia)**
  - Instalar fonte local `orbitron.ttf` em `res/font/`.
  - Configurar `Color.kt`, `Type.kt` e `Theme.kt` com estética *Capsule Corp.* / *Super Saiyajin*.

---

## Etapa 3: Camada de Dados (Remota e Local)

- [x] **T3.1 — Modelos e Cliente da Dragon Ball API**
  - Criar DTOs (`CharacterDto`, `CharacterDetailDto`, `TransformationDto`, `OriginPlanetDto`, `PlanetDto`) em `DragonBallModels.kt`.
  - Implementar `DragonBallApiService` e `ApiClient` com Retrofit, Moshi e timeouts de rede.
- [x] **T3.2 — Banco de Dados Local (Room)**
  - Modelar `FigureEntity` e `WorkoutLogEntity` em `Entities.kt`.
  - Implementar `FigureDao` e `WorkoutDao` com consultas reativas `Flow` em `Daos.kt`.
  - Configurar singleton `AppDatabase`.
- [x] **T3.3 — Repositório Unificado**
  - Criar `ZScouterRepository` orquestrando chamadas remotas (`Result<T>`) e persistência local em `Dispatchers.IO`.

---

## Etapa 4: ViewModel, Regras de Negócio e Navegação

- [x] **T4.1 — Gerenciamento de Estado e Lógica (`ZScouterViewModel`)**
  - Implementar carregamento inicial de personagens e planetas com `UiState`.
  - Implementar busca textual combinada com filtros de raça/afiliação (`combine`).
  - Implementar lógica de cálculo de Ki, evolução de patente Saiyajin e frases motivacionais.
  - Implementar motor do simulador Sparking (`runBattleSimulation`, `estimateKiScore`).
  - Implementar seed inicial de action figures (*Vegeta* e *Goku*) quando a coleção estiver vazia.
- [x] **T4.2 — Shell Principal e Navegação (`MainActivity.kt` + `ZScouterRoutes.kt` + `ZBottomNavigation.kt`)**
  - Configurar `enableEdgeToEdge()`, `WindowInsets.safeDrawing` e `NavHost` com rotas `@Serializable`.
  - Integrar barra de navegação inferior com 4 abas (*Radar Z, Modo Sayajin, Batalha, Prateleira*) e rota dedicada de detalhes (`CharacterDetailRoute`).

---

## Etapa 5: Implementação das Telas e Componentes de UI

- [x] **T5.1 — Radar Z e Detalhes (`RadarScreen.kt` + `CharacterCard.kt` + `CharacterDetailScreen.kt` + `DetailComponents.kt`)**
  - Grade responsiva de personagens, estados de Loading/Error/Empty, busca, filtros e tela completa de detalhes com transformações.
- [x] **T5.2 — Modo Saiyajin (`SaiyanModeScreen.kt` + `ScouterHudCard.kt`)**
  - HUD animado com contador de Ki, formulário de registro de treinos com dropdown de exercícios e histórico com exclusão.
- [x] **T5.3 — Simulador Sparking (`SparkingBattleScreen.kt`)**
  - Slots interativos de Lutador 1 vs Lutador 2, seletor horizontal de personagens, cálculo animado e card de veredito.
- [x] **T5.4 — Prateleira & Planetas (`FiguresShelfScreen.kt`)**
  - Resumo numérico da coleção, filtro por status, diálogo de edição de notas/status e aba secundária para exploração de planetas.

---

## Etapa 6: Qualidade, Testes e Documentação Final

- [x] **T6.1 — Testes Unitários de Casos de Uso, ViewModel e Domínio**
  - `ZScouterLogicTest.kt`: Validação de Ki base (`> 8000`), patentes Saiyajin, cálculo por treino, filtros e simulador.
  - `UseCasesUnitTest.kt`: Cobertura completa dos 8 casos de uso (`GetCharacters`, `GetCharacterDetail`, `GetPlanets`, `FilterCharacters`, `CalculateSaiyanRank`, `ManageFigures`, `ManageWorkouts`, `SimulateBattle`) com `FakeDragonBallRepository`.
  - `ZScouterViewModelTest.kt`: Validação de `StateFlow`, estados `Loading/Success/Error`, fallback de detalhes e simulação de batalha.
- [x] **T6.2 — Testes de Repositório Offline-First & Robolectric**
  - `OfflineFirstRepositoryTest.kt`: Sincronização API -> Room, fallback offline em memória, tratamento de erro com cache vazio e CRUD local reativo (`ActionFigure` e `WorkoutLog`).
  - `ExampleRobolectricTest.kt`: Validação de recursos (`R.string.app_name` = `"Z-Scouter"`) e resolução completa do grafo Koin.
- [x] **T6.3 — Documentação Final e Relatório de Testes**
  - `README.md` completo com persona, API REST, arquitetura, stack e instruções de execução.
  - `TEST_RESULTS.md` com o relatório consolidado da execução dos testes (`gradle :app:testDebugUnitTest`).

---

## 📌 Referência de Commits (Conventional Commits)

```bash
# Fase 1: Documentação e Planejamento (SDD)
git commit -m "docs: adiciona especificação inicial do projeto"
git commit -m "docs: documenta arquitetura e planejamento"
git commit -m "docs: adiciona decomposição de tarefas"

# Fase 6: Qualidade, Testes e Documentação Final
git commit -m "test: adiciona testes unitários dos casos de uso"
git commit -m "docs: atualiza README.md e adiciona relatório de testes"
```
