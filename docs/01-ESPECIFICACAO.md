# 01 — Especificação Inicial do Projeto (SDD)

> **Commit de referência:** `docs: adiciona especificação inicial do projeto`  
> **Projeto:** Z-Scouter (Android / Kotlin + Jetpack Compose)  
> **Público-alvo / Persona:** Lucas "Luke" Silva (28 anos, Designer Gráfico/Ilustrador, fã de Dragon Ball Z/Super, praticante de musculação e colecionador de *S.H. Figuarts*).

---

## 1. Problema que o Aplicativo Resolve

Fãs adultos da franquia *Dragon Ball* — especialmente entusiastas do universo visual do anime, praticantes de treino físico e colecionadores de *action figures* — costumam consumir informações fragmentadas em wikis cheias de anúncios, planilhas genéricas de academia sem apelo motivacional e listas improvisadas para controlar suas coleções de miniaturas.

O **Z-Scouter** unifica esses três pilares em uma única experiência mobile com identidade visual inspirada nos dispositivos da *Capsule Corp.* e nos clássicos *Scouters*:
1. **Consulta rápida e visual** de personagens, transformações e planetas diretamente de uma API REST estruturada.
2. **Gamificação da rotina de treinos** convertendo volume de treino em pontos de **Ki** e evolução de patentes Saiyajin.
3. **Gestão pessoal de coleção (*S.H. Figuarts*)** integrada à enciclopédia de personagens, além de um **simulador de duelos** para debates entre amigos.

---

## 2. API REST Escolhida

O aplicativo consome a **Dragon Ball API** pública e gratuita:

- **Base URL:** `https://dragonball-api.com/api/`
- **Documentação oficial:** `https://web.dragonball-api.com/documentation`
- **Autenticação:** Não requer chave de API ou OAuth (API aberta).
- **Formato de resposta:** JSON (`application/json`).

### 2.1. Endpoints Utilizados

| Método | Endpoint | Parâmetros | Finalidade no App |
| :--- | :--- | :--- | :--- |
| `GET` | `/characters` | `?limit=100` | Carrega a lista completa de guerreiros para o **Radar Z** e para o seletor de lutadores do **Simulador Sparking**. |
| `GET` | `/characters/{id}` | `id` (Int) | Obtém a ficha completa (`CharacterDetailDto`), incluindo **planeta de origem** (`originPlanet`) e array de **transformações** (`transformations`). |
| `GET` | `/planets` | `?limit=50` | Carrega os planetas do multiverso de Dragon Ball para a aba **Planetas Lore**. |

### 2.2. Estrutura dos Dados (DTOs Principais)

- **`CharacterDto`**: `id`, `name`, `ki`, `maxKi`, `race`, `gender`, `description`, `image`, `affiliation`.
- **`CharacterDetailDto`**: estende os atributos de personagem com `originPlanet: OriginPlanetDto?` e `transformations: List<TransformationDto>?`.
- **`TransformationDto`**: `id`, `name`, `image`, `ki`.
- **`PlanetDto`**: `id`, `name`, `isDestroyed`, `description`, `image`.

### 2.3. Tratamento de Falhas e Resiliência

- Timeouts configurados em `15s` no `OkHttpClient`.
- Exibição de estados claros de **Loading**, **Success** e **Error** (com botão de reconexão no Radar Z).
- Fallback na abertura de detalhes: caso `/characters/{id}` falhe temporariamente, os dados já carregados em `CharacterDto` são exibidos sem travar a experiência.

---

## 3. Requisitos Funcionais (RF)

| Código | Requisito | Descrição |
| :--- | :--- | :--- |
| **RF01** | **Exploração e Filtro de Guerreiros (Radar Z)** | Listar personagens em grade de 2 colunas com imagem em alta resolução, badge de raça e Ki base. Permitir busca textual por nome/descrição e filtro por chips de raça (*Todos, Saiyajin, Humano, Namekuseijin, Androide, Majin, Deuses / Anjos*). |
| **RF02** | **Ficha Detalhada do Guerreiro (Scouter)** | Ao selecionar um personagem, exibir modal com arte ampliada, Ki base, Max Ki, afiliação, planeta natal (com status destruído/intacto), biografia e carrossel horizontal de transformações. |
| **RF03** | **Rastreador de Treino & Ki (Modo Saiyajin)** | Exibir HUD Scouter com Ki total acumulado (iniciando em `9.001`), badge *"MAIS DE 8000!"*, barra de progresso e patente Saiyajin atual. Permitir registrar exercícios (nome predefinido, séries, reps, carga em kg e observações), calcular Ki ganho e excluir registros do histórico. |
| **RF04** | **Motivação Temática** | Exibir frases motivacionais inspiradas na dublagem brasileira clássica do Vegeta e permitir alternância manual ou automática a cada treino registrado. |
| **RF05** | **Simulador de Batalha Sparking** | Permitir escolher dois combatentes (*Lutador 1* vs *Lutador 2*), executar análise animada pelo Scouter e exibir o vencedor provável, diferença percentual estimada, veredito tático e frase de debate. |
| **RF06** | **Prateleira de Action Figures & Planetas** | Permitir adicionar/remover personagens na coleção local com um toque, filtrar por status (*Exposto no Estúdio, Na Caixa Original, Wishlist (Desejo)*), editar status e notas do colecionador, e alternar para a visualização de planetas da lore. |

---

## 4. Requisitos Não Funcionais (RNF)

| Código | Requisito | Critério Técnico |
| :--- | :--- | :--- |
| **RNF01** | **Persistência Local Offline** | Treinos (`WorkoutLogEntity`) e coleção de miniaturas (`FigureEntity`) devem ser persistidos localmente via **Room Database** com atualização reativa via `Flow`. |
| **RNF02** | **Interface Moderna & Acessível** | Seguir **Material Design 3**, tema escuro de alto contraste (*DarkSpaceBg*, *SaiyanGold*, *CapsuleCyan*), suporte a **Edge-to-Edge** (`WindowInsets.safeDrawing` / `navigationBars`), `contentDescription` em imagens/ícones e alvos de toque mínimos adequados. |
| **RNF03** | **Desempenho e Reatividade** | Operações de rede e banco de dados executadas fora da Main Thread (`Dispatchers.IO` + Coroutines), com carregamento assíncrono e cache de imagens via **Coil**. |
| **RNF04** | **Identidade Tipográfica** | Fonte display **Orbitron** empacotada localmente em `res/font/orbitron.ttf`, garantindo funcionamento offline sem dependência de Downloadable Fonts. |
| **RNF05** | **Testabilidade** | Cobertura de lógica de domínio/regras de cálculo com **JUnit 4** e validação de recursos Android via **Robolectric** (`testTag` nos principais componentes interativos). |

---

## 5. Regras de Negócio Principais

- **Ki Inicial do Usuário:** Todo usuário inicia com `9.001` de Ki base (`calculateTotalKi()`), garantindo a referência clássica logo no primeiro uso.
- **Cálculo de Ki por Treino:** `kiGained = (sets * reps * max(weightKg, 1.0) * 12).toLong() + bonus`, sendo o bônus de `+2.500` para exercícios com temática *"Sayajin"* ou *"Gravidade"* e `+1.000` para os demais.
- **Patentes Saiyajin:** Evoluem em 9 faixas progressivas, de *Guerreiro Sayajin Novato* (`< 15.000`) até *Instinto / Ego Superior* (`>= 2.500.000`).
- **Coleção Pré-populada (First-Run Seed):** Caso a estante esteja vazia na inicialização, os bonecos **Vegeta** e **Goku** (*S.H. Figuarts*) são inseridos como itens iniciais de referência.
