# Atalaia — Guia inicial de arquitetura e próximos passos

> **Objetivo deste documento:** alinhar a organização inicial do projeto entre os integrantes da equipe. Esta é uma proposta para começar de forma organizada, não uma lista de funcionalidades que precisam ser implementadas todas de uma vez.

## 1. Visão geral do Atalaia

O **Atalaia** é um aplicativo comunitário voltado à segurança e à mobilidade de pedestres. A proposta é permitir que pessoas registrem situações perigosas em vias públicas — por exemplo, árvores caídas, alagamentos, roubos ou relatos de troca de tiros — para que outros usuários possam ser alertados quando estiverem próximos da região afetada.

A ideia combina, de maneira mais enxuta, elementos de aplicativos de mapas e de plataformas de relatos comunitários. Entre as funcionalidades imaginadas estão:

- visualizar ocorrências em um mapa ou em uma lista;
- registrar uma ocorrência com categoria, descrição, fotografia e localização;
- consultar os detalhes e o horário de um relato;
- receber avisos quando houver uma ocorrência próxima, com vibração do dispositivo quando permitido pelo usuário e pelo sistema;
- futuramente, permitir que os relatos sejam atualizados, confirmados ou marcados como resolvidos/expirados.

**Atenção:** relatos enviados pelos usuários podem estar incompletos, desatualizados ou incorretos. A interface deverá indicar claramente que são relatos comunitários, mostrar quando foram publicados e evitar apresentar uma informação não verificada como certeza. O app não deve incentivar ninguém a se aproximar de um perigo para tirar fotos ou confirmar um relato.

## 2. O que é MVVM?

MVVM significa **Model–View–ViewModel**. É um padrão de arquitetura que separa responsabilidades para evitar que toda a aplicação fique concentrada nas telas ou na `MainActivity`.

### Model

Representa os dados e conceitos importantes do aplicativo. No Atalaia, exemplos possíveis são uma ocorrência de perigo, sua categoria, seu estado e, caso o aplicativo tenha contas, o usuário. O acesso a uma API ou banco de dados costuma ser organizado em repositórios.

### View

É a interface apresentada ao usuário. Se o projeto estiver usando **Jetpack Compose**, as telas serão normalmente funções `@Composable`, como `HomeScreen` ou `CreateReportScreen`. A View apresenta dados, recebe interações e encaminha ações ao ViewModel. Ela não deve conter toda a lógica de negócio nem acessar diretamente o banco de dados.

### ViewModel

Coordena a lógica de apresentação de uma tela: recebe ações da View, solicita operações aos repositórios e disponibiliza o estado que a interface precisa exibir. Por exemplo, um `HomeViewModel` pode solicitar a lista de ocorrências e expor os estados de carregamento, sucesso ou erro.

### Fluxo simplificado

```text
Usuário interage com a tela
          ↓
View (Screen / Composable)
          ↓ ação
ViewModel
          ↓ solicita dados/operação
Repository
          ↓
API, banco de dados ou dados de teste
          ↑ resultado
ViewModel atualiza o estado da tela
          ↓
View redesenha a interface com o novo estado
```

**MVVM não significa apenas criar três pastas chamadas Model, View e ViewModel.** O importante é cada parte ter uma responsabilidade clara. Por exemplo, a tela não deve realizar sozinha uma chamada HTTP, e o ViewModel não deve conter código visual de botões ou mapas.

## 3. Activity, telas e Models: o que criar?

### Activity

A `MainActivity.kt` normalmente é o ponto de entrada do aplicativo Android. Se o projeto usa Jetpack Compose, ela pode hospedar a interface inicial usando `setContent { ... }`.

**Não é necessário criar uma Activity para cada tela.** Para este projeto, a recomendação inicial é manter uma `MainActivity` e usar telas Compose com navegação entre elas. Criar várias Activities só deve ocorrer se houver uma razão concreta para isso.

Se o projeto estiver usando layouts XML em vez de Compose, a organização será um pouco diferente (por exemplo, Activities/Fragments e arquivos XML). Antes de mudar a estrutura, confira como a `MainActivity.kt` atual foi implementada.

### Models

Alguns modelos candidatos para discutir com a equipe:

- **`DangerReport`**: representa um relato de perigo.
- **`ReportCategory`**: enumeração das categorias de ocorrência, como alagamento, árvore caída, roubo, troca de tiros e outros.
- **`ReportStatus`**: situação do relato, por exemplo ativo, resolvido ou expirado — os valores definitivos ainda precisam ser decididos.
- **`AppUser`**: dados básicos do usuário, apenas se houver necessidade de contas/perfis nesta versão.

O modelo `DangerReport` poderá precisar de campos como identificador, categoria, descrição, referência da foto, latitude/longitude, data e hora de publicação e estado do relato. Os nomes e tipos finais devem ser decididos conforme a solução de armazenamento adotada.

Não é necessário criar agora várias versões da mesma classe. Um `DangerReportDto` separado, por exemplo, só será útil se o formato usado pela API for diferente do modelo utilizado pela aplicação.

## 4. Estrutura de pastas sugerida

Partindo do pacote atual `com.example.atalaia` e **presumindo que o projeto usa Jetpack Compose**, uma organização possível é:

```text
com.example.atalaia/
├── MainActivity.kt
├── data/
│   ├── model/                 # Modelos específicos da API/banco, se necessários
│   ├── remote/                # Comunicação com uma API, se houver
│   ├── local/                 # Banco local, se houver
│   └── repository/            # Implementações dos repositórios
├── domain/
│   ├── model/                 # Conceitos centrais, como DangerReport
│   ├── repository/            # Contratos dos repositórios
│   └── usecase/               # Casos de uso, se a complexidade justificar
└── ui/
    ├── components/            # Componentes visuais reutilizáveis
    ├── navigation/            # Navegação entre telas
    ├── theme/                 # Tema gerado pelo Android Studio; manter
    └── screens/
        ├── home/
        │   ├── HomeScreen.kt
        │   ├── HomeViewModel.kt
        │   └── HomeUiState.kt
        ├── create_report/
        │   ├── CreateReportScreen.kt
        │   ├── CreateReportViewModel.kt
        │   └── CreateReportUiState.kt
        └── report_detail/
            ├── ReportDetailScreen.kt
            ├── ReportDetailViewModel.kt
            └── ReportDetailUiState.kt
```

Esta estrutura é uma **referência**, não uma exigência de criar todas as pastas vazias agora. `remote/`, `local/`, `usecase/` e até uma divisão separada entre `data/model` e `domain/model` podem ser adicionadas quando forem necessárias. Não use uma camada só para duplicar classes sem uma finalidade clara.

### Responsabilidade das pastas

| Pasta | Para que serve |
|---|---|
| `data/` | Implementações de acesso aos dados, como API e armazenamento local. |
| `data/repository/` | Implementa operações usadas pelo restante do app. |
| `domain/model/` | Representa conceitos centrais do problema, como uma ocorrência. |
| `domain/repository/` | Define os contratos das operações de dados quando essa separação for útil. |
| `domain/usecase/` | Reúne operações de negócio específicas, se necessário. |
| `ui/screens/` | Contém as telas e seus ViewModels/estados, organizados por funcionalidade. |
| `ui/components/` | Guarda componentes de interface reutilizáveis. |
| `ui/navigation/` | Centraliza rotas e navegação. |
| `ui/theme/` | Mantém cores, tipografia e tema visual do app. |

## 5. Primeiras telas e classes a considerar

A equipe deve confirmar o escopo inicial antes de implementar tudo. Como ponto de partida, estas são as principais áreas possíveis:

| Funcionalidade | Responsabilidade inicial |
|---|---|
| **Home / Mapa** | Exibir ocorrências disponíveis, inicialmente com dados fictícios, em lista e/ou mapa. |
| **Criar relato** | Escolher categoria, adicionar descrição e foto e obter a localização com permissão do usuário. |
| **Detalhes do relato** | Mostrar informações de uma ocorrência selecionada, inclusive data/hora e estado. |
| **Alertas próximos** | Avaliar como identificar ocorrências próximas e avisar o usuário. Pode ficar para uma etapa posterior. |
| **Perfil/autenticação** | Só implementar nesta fase se contas e identificação dos autores estiverem no escopo aprovado. |

Classes/tipos que podem ser úteis, conforme as decisões do projeto:

- `DangerReport`, `ReportCategory` e `ReportStatus` para os dados do relato;
- `HomeViewModel` e `HomeUiState` para o estado da tela principal;
- `CreateReportViewModel` e `CreateReportUiState` para o formulário de relato;
- `DangerReportRepository` para definir como os relatos são consultados e cadastrados;
- `AppNavigation` para a navegação, caso seja usado Jetpack Compose Navigation.

Evitem criar classes apenas porque aparecem nesta lista. Primeiro, confirmem a necessidade e a responsabilidade de cada uma.

## 6. Ordem de trabalho recomendada

1. **Confirmar como o projeto atual funciona.** Abrir `MainActivity.kt`, identificar se usa Compose ou XML e executar a aplicação sem alterações.
2. **Alinhar as decisões básicas.** Definir quais telas entram na primeira versão, se haverá conta de usuário e qual será a fonte dos dados (API, banco local ou protótipo com dados fictícios).
3. **Definir os modelos mínimos.** Começar pelo conceito de ocorrência e pelas categorias realmente necessárias.
4. **Criar a navegação e a tela principal.** No início, é aceitável usar dados fictícios para desenvolver e testar a interface antes de integrar um serviço real.
5. **Implementar o cadastro de relato.** Trabalhar com categoria, descrição, foto e localização, solicitando as permissões necessárias e explicando ao usuário por que são usadas.
6. **Conectar os dados.** Implementar o repositório e definir a integração com API/banco somente após a equipe escolher a solução.
7. **Planejar alertas de proximidade.** Definir o raio de alerta, atualização dos relatos, permissões de localização/notificação e comportamento em segundo plano antes de implementar notificações e vibração.
8. **Testar e revisar.** Testar telas, estados de carregamento/erro, permissões negadas e dados inválidos; validar em dispositivos reais quando possível.

## 7. Cuidados importantes para um app de segurança comunitária

- Solicitar permissões de câmera, localização, notificações e vibração somente quando forem necessárias, explicando seu propósito.
- Não presumir que o usuário autorizou acesso contínuo à localização. Começar pelo mínimo necessário e seguir as restrições da versão Android utilizada.
- Evitar expor publicamente a localização do usuário que publicou o relato ou dados pessoais desnecessários.
- Mostrar data e hora das ocorrências e planejar como marcar relatos antigos como expirados ou resolvidos.
- Diferenciar relatos comunitários de informações verificadas e prever mecanismos para corrigir ou denunciar informações inadequadas.
- Não prometer alertas em tempo real antes de definir e testar a estratégia técnica. Alertas em segundo plano dependem de permissões, limitações do Android e, possivelmente, de um serviço de backend.
- Nunca armazenar senhas, chaves privadas ou tokens diretamente no código versionado.

## 8. Trabalho em equipe e GitHub

- Manter o código e os arquivos necessários ao Gradle versionados; não enviar `local.properties`, caches ou diretórios de compilação (`build/`).
- Cada integrante deve criar uma branch para sua tarefa, seguindo o fluxo de branches combinado pela equipe.
- Antes de começar, atualizar a branch a partir da branch compartilhada e verificar `git status`.
- Fazer commits pequenos e descritivos, por exemplo: `feat: criar tela inicial` ou `feat: definir modelo de ocorrência`.
- Combinar previamente mudanças em arquivos compartilhados, principalmente `MainActivity.kt`, navegação e arquivos Gradle.
- Abrir o projeto após o pull e verificar a sincronização do Gradle antes de concluir que há um erro no código.

## 9. Decisões ainda pendentes

Antes de avançar para funcionalidades que dependam de infraestrutura, a equipe precisa decidir:

- O app terá login/contas na primeira versão?
- Será usado um backend/API? Qual será a fonte oficial dos relatos?
- O mapa será essencial no primeiro protótipo ou uma lista de ocorrências será suficiente para começar?
- Como os relatos serão confirmados, atualizados, resolvidos ou expirados?
- Qual será o critério para avisar alguém que está próximo de uma ocorrência?
- Quais permissões e informações de localização são realmente necessárias?

**Princípio geral:** começar com uma versão pequena que abra, navegue e apresente dados de teste; depois adicionar cadastro, persistência, mapa e alertas por etapas. A arquitetura deve ajudar o projeto a evoluir, não dificultar a primeira entrega.
