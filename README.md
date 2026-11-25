# Z-Scouter — Central Dragon Ball Z & Super

Aplicativo Android nativo em **Kotlin + Jetpack Compose** que transforma o universo de **Dragon Ball** em uma central interativa para fãs da franquia: enciclopédia de guerreiros e planetas via API REST, rastreador de treinos na academia com progressão de Ki ("Modo Saiyajin"), simulador de batalhas estilo *Sparking! ZERO* e estante pessoal de colecionáveis (*S.H. Figuarts*).

---

## 🎯 Persona Alvo

Desenvolvido com foco no perfil de **Lucas "Luke" Silva** (28 anos, Designer Gráfico/Ilustrador em São Paulo):
- **Nostalgia & Atualidade:** Cresceu assistindo Dragon Ball Z na TV aberta nos anos 2000 e acompanha o mangá e filmes atuais (*Dragon Ball Super: Broly*).
- **Inspiração no Príncipe Vegeta:** Valoriza disciplina, superação pessoal e frases clássicas da dublagem brasileira.
- **Estilo de Vida "Kame":** Treinos de musculação inspirados em câmaras de gravidade e trilhas sonoras épicas.
- **Colecionador & Gamer:** Organização de Action Figures (*S.H. Figuarts*) e debates de nível de poder para jogos como *Dragon Ball: Sparking! ZERO* e *FighterZ*.

---

## ✨ Funcionalidades Principais

1. **Radar Z (Enciclopédia de Guerreiros):**
   - Listagem em grade dos personagens da Dragon Ball API com filtro por raça (*Saiyajin, Humano, Namekuseijin, Androide, Majin, Deuses/Anjos*) e busca em tempo real.
   - Ficha Scouter detalhada com Ki base, Max Ki, biografia, planeta de origem e carrossel de transformações.
2. **Modo Saiyajin (Treino & Evolução de Ki):**
   - HUD Scouter animado iniciando acima de `9.001` ("Mais de 8000!").
   - Registro de treinos (exercício, séries, reps, carga em kg e observações) com conversão automática em pontos de Ki.
   - Progressão de patente Saiyajin (*Novato → Elite → Oozaru → Super Sayajin → SSJ Deus → Blue → Instinto/Ego Superior*) e frases motivacionais aleatórias.
3. **Simulador Sparking (Duelos & Debates):**
   - Seleção de dois lutadores lado a lado para análise comparativa de Ki, transformações e vantagem percentual.
   - Veredito tático e argumento de mesa de bar gerado a partir dos atributos dos combatentes.
4. **Prateleira de Figures & Planetas Lore:**
   - Inventário local persistente (Room) para gerenciar miniaturas (*Exposto no Estúdio*, *Na Caixa Original*, *Wishlist*), notas de colecionador e avaliação.
   - Aba dedicada à exploração dos planetas do multiverso de Dragon Ball.

---

## 🛠️ Stack Tecnológica

- **Linguagem:** Kotlin
- **UI:** Jetpack Compose + Material Design 3 + Edge-to-Edge (`WindowInsets`)
- **Tipografia & Visual:** Fonte local `Orbitron`, paleta inspirada em *Capsule Corp.* e *Super Saiyajin*, ícone adaptativo personalizado
- **Arquitetura & DI:** Clean Architecture (`data`, `domain`, `ui`, `di`) + MVVM com **Koin** (`koin-android`, `koin-androidx-compose`)
- **Rede & Serialização:** Retrofit 2 + OkHttp + Moshi (`KotlinJsonAdapterFactory` / KSP)
- **Persistência Local:** Room Database (`FigureEntity`, `WorkoutLogEntity`) com Kotlin Coroutines & `Flow`
- **Imagens:** Coil Compose (`SubcomposeAsyncImage`)
- **Testes:** JUnit 4 + Robolectric

---

## 📚 Documentação SDD (Fase 1)

Toda a documentação de planejamento e engenharia orientada a especificações (SDD) está organizada na pasta [`docs/`](./docs):

| Documento | Descrição | Commit de Referência |
| :--- | :--- | :--- |
| [01 - Especificação Inicial](./docs/01-ESPECIFICACAO.md) | Problema resolvido, definição da Dragon Ball REST API, requisitos funcionais/não funcionais e regras de negócio. | `docs: adiciona especificação inicial do projeto` |
| [02 - Arquitetura e Planejamento](./docs/02-ARQUITETURA.md) | Clean Architecture (Presentation, Domain, Data), fluxo UDF e organização de pastas/pacotes. | `docs: documenta arquitetura e planejamento` |
| [03 - Decomposição de Tarefas](./docs/03-DECOMPOSICAO-TAREFAS.md) | Divisão lógica das tarefas em etapas de execução, critérios de aceitação e checklist. | `docs: adiciona decomposição de tarefas` |

---

## 🚀 Como Executar

1. Abra o projeto no **Android Studio** (SDK 36 / minSdk 24 / Java 11).
2. Sincronize as dependências Gradle (`gradle/libs.versions.toml`).
3. Execute o app no emulador/dispositivo ou rode os testes unitários locais:
   ```bash
   gradle :app:testDebugUnitTest
   ```
