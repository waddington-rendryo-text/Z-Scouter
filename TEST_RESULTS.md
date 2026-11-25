# Relatório de Testes — Z-Scouter

> **Comando executado:** `gradle :app:testDebugUnitTest`  
> **Data da execução:** 2026-11-25  
> **Ambiente:** JVM local + Robolectric `4.16.1` (`@Config(sdk = [36])`) + Room In-Memory + `kotlinx-coroutines-test` `1.10.2`  
> **Resultado Geral:** ✅ **20 testes executados | 20 aprovados | 0 falhas | 0 ignorados | 0 erros**

---

## 1. Resumo por Suíte de Testes

| Suíte (`app/src/test/java/com/example/`) | Tipo | Testes | Aprovados | Falhas | Tempo |
| :--- | :--- | :---: | :---: | :---: | :---: |
| `UseCasesUnitTest` | Unitário (JVM / Fake Repo) | 6 | 6 | 0 | 0.149s |
| `ZScouterLogicTest` | Unitário (JVM / Domínio) | 5 | 5 | 0 | 0.002s |
| `ZScouterViewModelTest` | Unitário (JVM / Coroutines) | 3 | 3 | 0 | 0.064s |
| `OfflineFirstRepositoryTest` | Integração Local (Robolectric + Room) | 3 | 3 | 0 | 7.798s |
| `ExampleRobolectricTest` | Integração Android / DI (Robolectric + Koin) | 2 | 2 | 0 | 0.330s |
| `ExampleUnitTest` | Unitário Base (JVM) | 1 | 1 | 0 | 0.001s |
| **Total** | | **20** | **20** | **0** | **8.344s** |

---

## 2. Detalhamento dos Casos de Teste

### 2.1. `UseCasesUnitTest` (Casos de Uso & Regras de Negócio)
Valida todos os 8 Use Cases da camada `domain/` utilizando o dublê em memória `FakeDragonBallRepository`:

1. `calculateSaiyanRank covers all rank tiers and exercise bonuses` — Verifica Ki inicial (`9.001`), acúmulo com `null`/positivo, bônus de `+2.500` para exercícios *"Gravidade"*/*Sayajin* vs `+1.000` padrão e todas as 9 faixas de patente Saiyajin.
2. `filterCharacters filters by query description race and affiliation` — Valida filtragem combinada por busca textual (nome e descrição), mapeamento de raças PT-BR para os valores da API (`Androide`, `Deuses / Anjos`, `Frieza Race`) e afiliações (`Exército de Freeza`, `Red Ribbon`, `Outros`).
3. `getCharacters getCharacterDetail and getPlanets delegate to repository and observe flows` — Verifica delegação dos casos de uso de consulta (`Result.success` / `Result.failure`) e emissão reativa dos `Flow`s.
4. `manageFigures seeds once toggles adds updates and deletes figures` — Garante que o seed inicial (*Vegeta* e *Goku*) ocorra apenas com estante vazia, além de testar toggle, edição de status/nota com `trim()` e remoção.
5. `manageWorkouts clamps bounds calculates ki trims text and deletes logs` — Valida limites (`coerceIn` para séries, repetições e carga), cálculo de Ki do treino, sanitização de texto e atualização reativa de `totalKiGained` após exclusão.
6. `simulateBattle applies ki heuristics and prince vegeta verdicts` — Testa a heurística de Ki (sufixos *Septillion*, bônus divino/Saiyajin), porcentagem no intervalo `2..98` e vereditos do Príncipe Vegeta.

### 2.2. `ZScouterLogicTest` (Domínio Base)
1. `testBaseKi_startsOver8000` — Confirma Ki inicial `9.001` (`> 8000`).
2. `testSaiyanRankProgression` — Confirma progressão entre patentes.
3. `testWorkoutKiCalculation` — Valida fórmula de ganho de Ki em treino com bônus de gravidade.
4. `testFilterCharactersByRaceAndQuery` — Confirma busca combinada com filtro de raça e afiliação.
5. `testSimulateBattleBetweenSaiyans` — Confirma vitória consistente e margem percentual válida.

### 2.3. `ZScouterViewModelTest` (Gerenciamento de Estado & `StateFlow`)
1. `initial load populates characters planets default fighters and seed figures` — Garante carregamento inicial de personagens, planetas, lutadores padrão (*Vegeta* vs *Goku*) e seed da coleção.
2. `loadCharacterDetail handles success fallback and error states` — Testa transição de `characterDetailState` para `UiState.Success` (API), fallback automático a partir da lista em memória e `UiState.Error` quando inexistente.
3. `logWorkout updates totalKi and runBattleSimulation produces result` — Verifica registro de treino refletindo no Ki acumulado e execução completa da simulação de batalha.

### 2.4. `OfflineFirstRepositoryTest` (Estratégia Offline-First com Room In-Memory)
1. `offline first caches api responses in room and serves cache when offline` — Valida sincronização API → Room (personagens, detalhes com planeta/transformações e planetas) e retorno transparente do cache local quando a rede falha (`isOfflineMode = true`).
2. `offline request with empty cache returns failure without offline cache flag` — Garante propagação de `Result.failure` quando a rede falha e o cache local está vazio.
3. `local figures and workouts crud update reactive flows in room` — Testa inserção, consulta, exclusão e emissão reativa via `figuresFlow`, `workoutsFlow` e `totalKiGainedFlow` diretamente no Room.

### 2.5. `ExampleRobolectricTest` & `ExampleUnitTest`
1. `koin di graph resolves core dependencies and all domain use cases` — Resolve todo o grafo de injeção de dependências do Koin (`DragonBallApiService`, `DragonBallRepository` e os 8 Use Cases).
2. `read string from context` — Valida o recurso `R.string.app_name` (`"Z-Scouter"`).
3. `addition_isCorrect` — Teste unitário base de sanidade da JVM.

---

## 3. Verificação de Compilação

- **Build Debug APK (`compile_applet`):** ✅ `Build succeeded - the applet is compiled`
- **Suíte Debug Unit/Robolectric (`gradle :app:testDebugUnitTest`):** ✅ `BUILD SUCCESSFUL` (20/20 aprovados)
