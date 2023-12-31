#### 22/09/2023

Curso de Android: acessando uma API Web

```
java -jar server.jar

lsof -i:8080
Kill -9
```

@01-Entendendo o problema da persistência interna

@@01
Introdução

Olá, eu sou Alex Felipe, instrutor da Alura, e apresentarei a vocês o curso de Persistência Web em Android. Nosso objetivo será resolver um problema relatado por um cliente. Ele possui um aplicativo de estoque de produtos, com CRUD, envolvendo listagem, inserção, remoção, edição, comportamentos bem comuns. No entanto, em algumas situações as informações cadastradas são perdidas, e não há como recuperá-las.
Entenderemos que estes problemas ocorrem quando fazemos apenas a persistência interna, ou seja, quando trabalhamos com o Room, ou com banco de dados interno. Nestes casos corremos riscos, e é justamente isso que começaremos a resolver em nosso projeto. Temos produtos como computador, mesa, teclado, mouse e afins.

Se limparmos os dados contidos em nosso aplicativo ou o desinstalarmos, as informações armazenadas nele serão realmente perdidas. Como é que a persistência web dará conta disso? Isso será resolvido estabelecendo-se uma comunicação com uma API Web, cujo funcionamento veremos durante o curso.

Percebam que, se limparmos os dados do aplicativo em modo avião, perdemos tudo o que estava armazenado, mas com o wifi ativado, podemos fechar e reabrir o aplicativo, e as informações são recuperadas por conta da comunicação com a API Web. Aprenderemos a fazer esta integração, isto é, trabalhar com a persistência interna integrando-a à API Web.

Faremos isto justamente para evitar a insatisfação do nosso cliente. Em se tratando da parte mais técnica, do que acabaremos vendo e implementando, estão as requisições web via HTTP. Conheceremos algumas bibliotecas, veremos peculiaridades existentes durante este tipo de implementação, uma vez que já aprendemos a usar AsyncTask para realizar operações paralelas que tendem a demorar.

Da mesma forma, trabalharemos de forma a aprender a solucionar problemas com requisições web. Teremos muitos códigos complexos, este será um curso mais avançado, que exige mais pré-requisitos. Conto com a sua presença, espero que esteja animado para o curso, e vamos começar!

@@02
Orientações iniciais e organização do curso

Pré-requisitos
Neste curso, esperamos que você já tenha todo o conhecimento apresentado nos cursos dos seguintes tópicos:

Requisições HTTP;
Fundamentos de Android;
Persistência de dados com Room;
RecyclerView;
ConstraintLayout.
Não necessariamente você precisa conhecer todos os detalhes dos cursos para prosseguir. Mas se preferir fazer o curso sem todos os pré-requisitos, provavelmente você terá dificuldade para compreender algumas abordagens.

Sendo assim, o recomendado é concluir os cursos anteriores para melhorar a compreensão.

Como o curso está organizado
Cada aula é composta de vídeos e exercícios, sendo que cada vídeo vai ter pelo menos um exercício.

Esse é um curso classificado como avançado, isso significa que esperamos uma maior maturidade dos alunos e alunas. Alguns exercícios serão bem resumidos, contendo apenas informações necessárias que não foram apresentadas no vídeo.

Configurações para diferentes sistemas, links e outros detalhes não apresentados em vídeo, serão compartilhados nos exercícios.
Sendo assim, a maioria dos exercícios que costumam solicitar a mesma execução feita em vídeo vai conter apenas essa orientação resumida.

É importante ressaltar que qualquer dúvida você pode entrar em contato com a gente por meio do fórum :)

https://cursos.alura.com.br/course/http-fundamentos

https://cursos.alura.com.br/course/android-refinando-o-projeto

https://cursos.alura.com.br/course/android-room-operacoes-assincronas

https://cursos.alura.com.br/course/recyclerview-listeners-animacoes

https://cursos.alura.com.br/course/layout-android-2

@@03
Introdução ao projeto

Como primeiro passo, entenderemos a implementação do projeto, que desconhecemos até então. Como já foi comentado, trata-se de um CRUD de produtos, um aplicativo capaz de listar produtos, fazer inserções, remoções e edições. Vamos começar analisando o código de ListaProdutosActivity.java, a partir de uma simulação do que está acontecendo conforme o que está sendo exibido na tela.
Na tela com o texto "Lista de produtos", basicamente carregamos uma lista vazia, feita a partir do RecyclerView, um componente com comportamento similar à nossa ListView. Se acessarmos o método configuraListaProdutos(), percebemos que utilizamos o RecyclerView, configuramos seu Adapter, que é específico, diferente do AdapterView, e fazemos todas as configurações de lista a partir de ListaProdutosAdapter().

Caso você nunca tenha visto esta API, a recomendação é de que se faça o curso da Alura; durante este curso, o conhecimento de implementação do RecyclerView não é necessário, pois abstraí o máximo possível para que pudéssemos utilizá-lo como se fosse um Adapter do nosso ListView. Sendo assim, teremos apenas alguns métodos públicos, e não precisaremos nos preocupar muito com a implementação.

Na tela do aplicativo, existe um Floating Action Button que, ao ser clicado, em vez de abrir uma nova Activity, ele abre um dialog um pouco diferente do que já vimos, com Views, como é o caso de EditTexts, de componentes do Material Design. Neste caso em específico, são componentes chamados de TextInputLayout.

Durante cursos anteriores, iniciais principalmente, não vemos muito sobre esse assunto, existe um curso sobre validações de formulário aqui na plataforma em que acabamos vendo tudo isso um pouco mais. Não é o objetivo entender sobre estas peculiaridades, apenas escolhi estes componentes para um melhor aspecto visual.

Neste caso estamos usando um dialog personalizado e, se repararmos em ListaProdutosActivity.java, o Floating Action Button configuraFabSalvaProduto() faz setOnClickListener(), e chama abreFormularioSalvaProduto(). Mas quando fazemos isso, estamos basicamente criando o dialog SalvaProdutoDialog(), e se usarmos o atalho "Ctrl + B", ele faz a extensão de FormularioProdutoDialog enviando TITULO e TITULO_BOTAO_POSITIVO.

Porém, toda a implementação genérica o bastante para a sua reutilização no comportamento de edição se encontra em FormularioProdutoDialog. E para podermos usá-lo, precisamos de contexto, os dois títulos, e o listener, que já vai para o construtor do nosso FormularioProdutoDialog, seria então a representação abstrata e genérica para todo tipo de formulário que formos utilizar para lidar com dialogs.

Se acessarmos sua implementação, teremos todas as suas configurações e complexidades; o conhecimento sobre esta implementação não é obrigatório, caso haja curiosidade e quiser editar um pouco o código, fique à vontade. O foco, no entanto, é enviar o listener para indicar que um determinado produto foi finalizado, salvo ou editado.

E a configuração da parte visual é feita em mostra(), método que exibe o dialog, em que criamos uma View com viewCriada a partir de um XML de layout que criamos, formulario_produto, por exemplo. Usando o "Ctrl + B" nele, teremos toda a implementação feita para o formulário, com TextInputLayout, e tudo o mais.

Quando finalizamos a operação, usamos o listener para enviar o dado para quem chamou o dialog, no caso, quandoConfirmado(), em que mandamos produto. Isso fica acessível para quem chama os dialogs, então quando chamamos a instância do SalvaProdutoDialog() usamos uma expressão lambda, this::salva para a implementação do Listener de confirmação, que é justamente aquele que serve para todos os formulários a serem abertos via dialog, utilizando o formulário genérico que vimos.

Isso quer dizer que se tentamos editar um produto, reutilizamos o código de formulário. A diferença é que, se acessamos abreFormularioEditaProduto(), usamos EditaProdutoDialog(), que altera o título do dialog e do botão. Ou seja, FormularioProdutoDialog contém toda a lógica para a distinção do que será apresentado durante a edição ou inserção de um produto.

Ao acessarmos tentaPreencherFormulario(), é verificado se um produto existe no momento de instância do nosso formulário; caso exista, os campos serão preenchidos, e é exibido o ID. Vejam que é realmente uma implementação genérica a ser compartilhada entre os dois formulários. Assim, apesar de EditaProdutoDialog.java e SalvaProdutoDialog.java estarem bem simples, toda a complexidade se encontra em FormularioProdutoDialog.java.

Em abreFormularioEditaProduto() chamamos o método edita(), e no comportamento de salvar, salva, que são operações feitas em bancos de dados de forma assíncrona, assim como vimos no segundo curso de Room. Isso é necessário para não travar a Thread principal, nem a tela do usuário.

Em salva() criamos uma solução na qual utilizamos uma Async Task genérica, nomeada de BaseAsyncTask, cuja implementação usa Generics do Java. Desta forma não precisamos criar várias Async Tasks para cada operação; podemos reutilizar uma única delas incluindo o comportamento que queremos que seja executado no doInBackground() de executaListener, e depois que tudo é finalizado, no onPostExecute(), a partir de FinalizadaListener.

Assim, em ListaProdutosActivity.java, temos BaseAsyncTask e, na primeira expressão lambda indicamos que salvaremos um produto ao qual teremos acesso a partir de seu ID, que buscamos e retornamos, e que vai à outra expressão lambda, referente a FinalizadaListener, que por sua vez recebe o tipo genérico gerado.

Então, toda vez que colocarmos um comportamento em executaListener, é necessário devolver um valor com base no Generics atribuído, neste caso isto acontece com o produto, que queremos que esteja acessível após ser salvo. Depois disso, o incluímos no adapter, e toda a configuração que já vimos no Adapter View. Por fim, ele é executado, com execute().

No caso da edição do Adapter, em edita(), temos comportamento similar — a única diferença é que colocamos mais informações, como é o caso de posicao. Além de devolvermos um produto, então, que seja acessível em FinalizadaListener, também enviamos posicao. Toda a edição feita no banco de dados ocorre em background, numa Thread separada. O que é feito na Thread de UI é incluído em outro Listener.

O BaseAsyncTask é utilizado também para remoção e listagem, sendo este o mais simples de todos, porque não envolve tantas operações, uma vez que utilizamos method reference, a chamada mais enxuta em expressões lambdas.

Estes são os pontos principais, mas repito que se quiserem explorar o projeto, fiquem à vontade, todos os pacotes estão acessíveis, todo o código de implementação, como dito anteriormente, os conhecimentos em relação ao Converter são necessários, portanto é recomendado que se faça o segundo curso de Room.

Caso surjam dúvidas, entre em contato conosco via fórum.

https://cursos.alura.com.br/forum/curso-android-api-web/todos/

@@04
Preparando o ambiente - Projeto inicial

Faça o download do projeto Estoque, realize o procedimento de import.
Lembre-se de renomear o diretório extraído para Estoque.
Após finalizar a configuração do Gradle com sucesso, execute-o e veja se apresenta a seguinte tela inicial. Se apresentar o resultado esperado, siga para a.

Atualização do projeto para a versão do Android Studio 4.1
As bibliotecas do projeto foram atualizadas e testadas com o Android Studio 4.1 e foram utilizadas as versões mais recentes no momento da atualização (Fevereiro de 2021). Sendo assim, recomendamos que você verifique as atividades de cada vídeo para utilizar as bibliotecas com versões mais recentes que foram testadas para esse curso.

Caso você tenha interesse em verificar as modificações do projeto, você pode conferir este commit do projeto inicial ou verifique o commit do projeto finalizado com todas as bibliotecas que serão utilizadas no decorrer do curso.

https://github.com/alura-cursos/android-persistencia-web/archive/projeto-inicial.zip

@@05
Dúvidas sobre o projeto

Foram apresentadas as principais features e implementações do projeto.
Caso você tenha dúvida de algum detalhe que foi ou não apresentado, fique à vontade para testar o projeto ou perguntar no fórum.

@@06
Entendendo a persistência externa

Agora que vimos como nosso projeto funciona, precisamos entender o motivo da perda de dados. O primeiro destaque a que atentaremos é que a solução que estamos utilizando para a persistência de dados é o Room, cuja técnica é interna.
Persistência de dados externa no Android
Dentre os problemas comuns na persistência interna, os quais acabam comprometendo os nossos dados, estão:

limpeza de dados;
desinstalação do app;
perda do celular.
Como podemos evitar esses problemas?
Precisamos de uma solução que implique em uma flexibilidade capaz de, mesmo quando houver as ações acima, acessarmos os dados. Para isso, consideramos a persistência web, o mais famoso tipo de persistência externa. Se você já ouviu falar sobre soluções em cloud, a computação em nuvem, que lidam com servidores, esta é uma abordagem a ser considerada para evitar a perda de dados.

Assim, manteríamos todos os dados salvos internamente, portanto não necessariamente precisamos descartar a solução aplicada anteriormente, inclusive, isso é muito comum. Porém, em vez de salvarmos apenas internamente, estabeleceríamos uma comunicação externa com uma API, servidor, ou web service.

Ao enviarmos estes dados, fazemos uma espécie de backup para estes serviços online, os quais acabam se responsabilizando em mantê-los disponíveis para os nossos aplicativos. A ideia, então, é proteger os dados, garantindo sua integridade caso algo aconteça com eles localmente.

Vantagens da persistência web
protege os dados dos problemas de persistência interna;
disponibiliza dos dados para mais de um dispositivo;
permite o acesso em qualquer lugar que tenha acesso à internet.
Desvantagens da persistência web
precisa de internet para que funcione;
aumenta a complexidade do projeto;
exige sincronismo com os dados internos.
A seguir veremos como utilizar esta API externa, e fazer a sua integração com o nosso aplicativo.

@@07
Sobre o uso de API web

Além do armazenamento interno, temos a alternativa do armazenamento externo para Apps Android. Em quais situações faz sentido considerarmos essa abordagem?

Para disponibilizar os dados para mais de um dispositivo.
 
Isso mesmo! Com essa abordagem somos capazes de consumir os dados do usuário desde que tenha acesso a API.
Alternativa correta
Para não precisar da persistência interna.
 
Alternativa correta
Para evitar a perda de dados do usuário.
 
Exatamente! Ao manter os dados fora do dispositivo, mesmo o usuário formatando o celular é possível recuperá-los.
Alternativa correta
Para disponibilizar os dados com maior performance.

@@08
Inicializando a API web

Como vimos, precisamos de uma API web para evitar a perda de dados do nosso usuário quando eles são mantidos no aplicativo apenas internamente. Nos deparamos com uma questão: qual será o serviço web que utilizaremos para manter esses dados?
Se considerarmos as opções mais famosas no mercado, temos a AWS, Heroku, DigitalOcean, OpenShift, Azure, dentre várias outras possibilidades.

No momento de escolha, devemos levar em consideração que são serviços pagos, com opções de planos gratuitos com limitações. Outros fatores decisivos: o acesso total à internet é essencial e, uma vez feita a integração com alguma destas opções, é provável que ela passe por atualizações e futuramente deixe de funcionar como esperado, ou visto neste curso.

Para evitar toda esta complexidade, especialmente no nosso caso, em que estamos aprendendo e o foco não é mexer tanto nesta parte de configuração ou infraestrutura, fizemos a implementação de um servidor que será disponibilizado aqui, e você irá baixar e rodá-lo como se fosse um aplicativo do computador.

Para utilizar o servidor, precisaremos apenas executá-lo; porém, fica a recomendação de que a execução seja feita via prompt de comando no terminal, pois se clicarmos duas vezes em server.jar a execução será em background, e se por algum motivo quisermos fechá-lo, será necessário abrir o gerenciador de tarefas, pois não teremos uma interface gráfica indicando que isto está sendo feito.

O comando deve ser feito no local onde está salvo este arquivo server.jar, e será simplesmente java -jar server.jar. Assim, o servidor será executado, e é exibido um log de inicialização, ao qual precisamos ficar atentos caso surja algum problema. A partir deste log ficamos sabendo que trata-se de um aplicativo desenvolvido com o framework Spring.

Ao final da execução, para verificarmos se tudo ocorreu como esperado, checamos as duas últimas mensagens do log — neste caso, lê-se que é um servidor de aplicação Tomcat na porta 8080, e que foi iniciado ("Started EstoqueApiApplicationKt"). E para nos certificarmos de que é isso mesmo, acessaremos localhost:8080 no navegador.

Durante a execução, caso haja algum cache, podemos finalizar a API e reexecutá-la depois.
Teremos uma página de erro 404 porque ainda não fizemos nenhum tipo de implementação, mas se digitarmos localhost:8080/produto, que é um dos endereços mapeados, teremos uma interface diferente, uma lista vazia, pois estamos utilizando uma arquitetura em REST (um serviço tal qual uma web service) que pode ser consumida por outros aplicativos, o CRUD de um produto, por exemplo.

Aqui, acessamos todos os produtos da API, e a seguir veremos as suas possibilidades, mas por ora ficaremos com estes detalhes referentes à inicialização da mesma. Outro ponto é que todos os comportamentos feitos serão apontados no log, ou seja, eles estarão mapeados.

Um detalhe que pode acontecer e ser muito comum é um serviço nosso estar usando a porta 8080, que no geral é bastante utilizada em testes. Neste caso, durante a execução, podemos digitar java -jar -Dserver.port=8081 server.jar no terminal para indicar a porta do servidor.

A recomendação para este tipo de execução é que ela seja feita quando não tivermos o servidor rodando uma única vez, porque é utilizado um banco de dados interno, e quando executamos o servidor, foi criado o diretório "database", utilizado via solução HSQLDB, para que não seja necessário instalar um banco de dados no seu computador.

Entretanto, se criarmos duas instâncias deste servidor, ele não funcionará, porque ele acabará bloqueando a execução do banco de dados para uma das instâncias. Isso é muito importante de ser notado, então, só devemos fazer esta execução secundária tendo finalizado a instância executada uma vez anteriormente.

Podemos testar isso acessando localhost:8080/produto no navegador. Teremos um erro, portanto rodaremos o servidor novamente, desta vez usando a porta 8081. Percebam que mesmo alterando a porta, os dados serão os mesmos, eles são migrados.

https://aws.amazon.com/pt/

https://www.heroku.com/

https://www.digitalocean.com/

https://www.openshift.com/

https://azure.microsoft.com/

@@09
Baixando e rodando a API

Faça o download da API. Faça a extração e execute o arquivo server.jar com o comando java -jar server.jar.
Além do arquivo server.jar, o download acompanha o README.md que apresenta mais detalhes da API.
A execução deve apresentar os mesmos resultados vistos em vídeo. Caso apresentar um resultado diferente, entre em contato com a gente.

https://github.com/alura-cursos/android-persistencia-web-api/archive/api-executavel.zip

@@10
Testando os comportamentos da API

Feita a inicialização da API web, veremos os comportamentos que ela disponibiliza, os quais serão integrados. Basicamente temos um CRUD na API, então conseguiremos fazer listagens, inserções, remoções e alterações dos produtos. Utilizaremos um aplicativo muito famoso para fazer requisições HTTP, o Postman. Ele é muito simples de usar, intuitivo, não requer conhecimentos prévios ou instruções de execução.
Fique à vontade para utilizar outra opção, como o cURL, muito comum em sistemas Linux.
Na interface principal do Postman, há um campo para preenchimento de uma URL, logo abaixo de Untitled Request. Assim como fizemos na barra de endereços do navegador, inicialmente digitaremos localhost:8080, para testarmos. Ao clicarmos em Send, fazemos uma requisição GET, e será exibida uma mensagem indicando que não foi possível obter uma resposta. Isto é, não temos um servidor na porta 8080.

Mas se tentarmos localhost:8081, temos uma resposta do servidor, de maneira amigável, considerando informações interessantes como Status, tempo de resposta, tamanho da requisição, Headers e outras, que podem ser exploradas durante este teste inicial, antes de toda a integração com o aplicativo.

Quando colocamos localhost:8081/produto em uma requisição GET, temos a lista dos nossos produtos, no caso, [], sendo exibida na aba Body. Na interface, as abas relacionadas ao nosso envio aparecem em cima, e à resposta, embaixo. No geral, ficaremos mais atentos às abas de respostas, enquanto faremos modificações conforme as requisições na parte superior, mas no caso do GET não é necessário nenhum tipo de modificação.

Já aprendemos a listar, como fazemos para inserir?

Podemos fazer uma cópia da requisição de localhost:8081/produto, clicando nela na aba de histórico (History) localizada na parte lateral esquerda do programa. Isso criará uma nova aba na parte central da interface, e é ela que modificaremos. Para a inserção, mudaremos o verbo de GET para POST, manteremos o endereço, e precisamos enviar um objeto que represente um produto.

Clicaremos em Body, selecionaremos raw para enviarmos um JSON. Também alteraremos Text por JSON (application/json), e isso fará com que os Headers sejam modificados para que Content-Type seja incluso e o servidor o aceite para esta requisição. Em seguida incluímos um JSON conforme o modelo do nosso produto, digitando:

{
    "nome" : "Bola de futebol",
    "preco" : "99.99",
    "quantidade" : "12"
}COPIAR CÓDIGO
Colocamos o nome do atributo via string, usamos dois pontos e incluímos seu valor, e fazemos isso mais duas vezes. Assim, criamos um objeto possível de ser enviado ao servidor. Clicaremos no botão Send. Como resposta, é demonstrado que o produto foi criado com "id" 1. Se voltarmos à aba com a requisição GET e clicarmos em Send novamente, o produto é listado.

Vamos continuar acrescentando outro produto modificando o POST, trocando "Bola de futebol" por "Bola de futsal", "99.99" por "199.99", e "12" por "17". Desta vez, na resposta é indicado que o "id" é 2. Se clicarmos em Send na aba de GET, teremos os dois produtos.

Como dito anteriormente, é possível fazer CRUD, então conseguimos fazer edições e remoções. Vamos copiar a requisição POST por conta do modelo do Body, que é justamente o que queremos modificar. Para fazê-lo, precisamos que o produto já exista, e isso é garantido enviando outro argumento na URL, localhost:8081/produto/1, sendo 1 o ID do primeiro produto.

Na aba Body, deixaremos "Bola de basquete" no lugar de "Bola de futsal", substituiremos "199.99" por "129.99", e "17" por "9". Faremos a requisição de GET novamente e, na última requisição POST, modificaremos o verbo para PUT, e clicaremos em Send. Agora sim, o produto de ID 1 foi atualizado, e teremos uma resposta indicando isso. Se retornarmos ao GET e clicarmos em Send, teremos esta mesma informação.

Para fazermos uma remoção, precisamos fazer algo similar ao que foi feito em PUT, no entanto, não é preciso enviar nenhum tipo de corpo (Body), ele pode estar vazio, O importante é que o ID exista para que seja de fato removido, caso contrário nada acontecerá. Na cópia do PUT, então, manteremos o Body vazio e alteraremos o verbo para DELETE.

Na resposta, o Status exibe 200 OK, e esta é uma informação relevante, de que trataremos mais adiante. Estes são basicamente os comportamentos da API, e agora, para demonstrar que tudo que é modificado em uma porta também constará em outra, já que o banco de dados é o mesmo, derrubaremos o servidor no terminal, o inicializaremos na porta 8080, com java -jar server.jar.

Feita a inicialização, na barra de endereços de GET testaremos localhost:8081/produto, de que obteremos o erro esperado, e então localhost:8080/produto. O retorno exibe as informações que tínhamos anteriormente, como gostaríamos. Assim, todos os comportamentos podem ser feitos nesta porta sem nenhum problema.

Para exemplificar, faremos a inserção de uma chuteira de futsal com preço 149.99 e quantidade 21. É desta forma que testamos nosso aplicativo, nossa API.

Lembrando que o ID nunca é repetido, dado que se trabalha com incrementos.

@@11
Testando possibilidades da API

Teste as possibilidades da API, para isso utilize um client de requisição HTTP que preferir. Em vídeo foi utilizado o Postman.
Você pode conferir o arquivo README.md para mais detalhes dos possíveis end points.

https://www.getpostman.com/

@@12
O que aprendemos?

Nesta aula, aprendemos:
Os motivos da perda de dados em Apps Android;
Vantagens e desvantagens da persistência web;
O que é uma API web e como é possível testá-la.

@02-Configurando as requisições HTTP

@@01
Configuração inicial do Retrofit

Temos uma API web disponível, então começaremos a integrá-la com nosso aplicativo. Dado que usamos o Android Studio e o Java, uma opção nativa que temos é utilizar a classe HttpURLConnection, a partir da qual faremos toda a configuração necessária para a realização de requisições HTTP com o mundo externo.
Esta abordagem, porém, é muito similar ao que vimos no SQLite em baixo nível: ela exige um grande número de configurações, e consequentemente o risco de cometermos erros é maior. Por ser em baixo nível, a própria comunidade do Android disponibiliza outras alternativas, inclusive mais comuns.

Usar o HttpURLConnection, portanto, serve para entendermos como se dá o funcionamento em baixo nível, ou para de repente darmos suporte a um aplicativo que acabou sendo legado, ou ainda, se quisermos fazer uma configuração de muito baixo nível, que APIs com alto nível não disponibilizam ou dão conta.

Como alternativas, existem bibliotecas muito famosas, como é o caso do Retrofit, e do Volley. O intuito delas é facilitar o acesso HTTP via requisições.

Durante este curso, optaremos pela biblioteca mais utilizada, que é o Retrofit, apesar do Volley ser do próprio Google, e sua manutenção ser feita pela equipe do Android. Além disso, por ser uma biblioteca externa, precisamos adicioná-la como sendo uma dependência do projeto.

Existe um script para isso na própria página com a sua documentação, implementation 'com.squareup.retrofit2:retrofit:2.5.0', do Gradle. Copiaremos o script, acessaremos build.gradle com "Ctrl + Shift + N", e no final da parte de dependencies, colaremos a instrução. Feito isso, clicaremos em Sync Now para sincronizar o projeto.

Vamos fazer a configuração inicial do Retrofit; segundo a documentação, é necessária primeiramente uma instância do Retrofit, com Builder(), como vimos em outras abordagens, e a configuração de um Service. Ele será a instância principal para toda a configuração de qualquer requisição HTTP que fizermos.

O Service será a entidade que manterá as possíveis requisições, e é desta forma que identificamos os dois principais componentes do Retrofit. Precisaremos centralizar a sua instância, assim como fazemos ao utilizarmos o "database", em que temos a classe EstoqueDatabase com a configuração geral da instância.

Criaremos um pacote chamado "retrofit", e dentro dele criaremos a classe única EstoqueRetrofit, que centralizará todas as configurações, e onde colaremos a implementação do Retrofit diretamente no construtor, que inseriremos com "Alt + Insert" e selecionando Constructor.

E então precisamos acrescentar o que chamamos de Base URL, ou URL base. Todas as vezes em que acessamos nossa API, até mesmo naquele teste que fizemos com o Postman, notem que utilizamos localhost:8080/. Assim, independentemente da requisição, seja para buscar, inserir, alterar ou remover produtos, esta URL acaba sendo mantida, e portanto é a URL base na nossa API.

package br.com.alura.estoque.retrofit;

import retrofit2.Retrofit;

public class EstoqueRetrofit {

    public EstoqueRetrofit() {
        new Retrofit.Builder()
            .baseUrl("http://localhost:8080/")
    }
}COPIAR CÓDIGO
É importante manter a barra final na URL para disponibilizarmos a configuração de outras entradas, também conhecidas por endpoints, como é o caso de /produto.
No entanto, ao mantermos esta URL base, tentamos acessar nosso próprio computador nesta porta, e no Android não trabalhamos exatamente com o computador em que estamos desenvolvendo. O nosso aplicativo é executado em outro computador, que no caso é um dispositivo móvel, um celular.

Ao testarmos o acesso pelo navegador do celular, o servidor não será encontrado, porque o localhost é o celular em si, e precisamos acessar diretamente pelo IP. Ou seja, em primeiro lugar, é necessário identificarmos o IP do nosso computador. Dependendo do computador e do sistema operacional em uso, pode ser que exista um comando para isso.

No Windows, usamos o comando ipconfig no terminal. No Linux e no MAC usamos ifconfig, e este comando listará todos os adaptadores de rede disponíveis no computador, identificaremos aquele que está em uso, neste caso um Ethernet via rede cabeada, copiaremos o endereço IPv4, a partir do qual o acesso ao servidor ficará disponível, e ajustaremos a baseUrl.

Neste caso, o servidor estará em 192.168.20.249:8080/produto independentemente do dispositivo em uso. Este é um detalhe que deve ser feito no momento de configuração da URL base. E se você for acessar via celular físico, com cabo USB, os dois computadores, aquele em que você estiver desenvolvendo, e o celular, precisam estar na mesma rede.

Em seguida, basta adicionarmos um build() que devolverá a instância do Retrofit.

public EstoqueRetrofit() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://192.168.20.249:8080/")
        .build();
}COPIAR CÓDIGO
A seguir começaremos a fazer a configuração e execução do nosso Service, responsável em determinar a requisição a ser feita.

@@02
Adicionando o Retrofit ao projeto

Adicione o Retrofit ao projeto colocando a seguinte dependência:
implementation 'com.squareup.retrofit2:retrofit:2.9.0'COPIAR CÓDIGO
Sincronize o projeto. Se tudo estive correto, implemente a classe para gerar a instância do Retrofit com base na URL base.

Você pode obter a URL base via prompt de comando do Windows usando o ipconfig ou terminal no Linux ou Mac via ifconfig. Lembre-se de pegar o ipv4 do adaptador de rede que o computador está conectado e o dispositivo Android também.

Ao pegar o endereço faça o teste substituindo o localhost pelo IP, confira se aparece o resultado esperado pela API. Também faça o teste no dispositivo Android.

O código de implementação pode ser conferido a partir deste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/f8198adb78536eb7678ec3a1401b727539305986

@@03
Configurando o Service

Implementada a instância do Retrofit, começaremos a configurar o Service que, por ser uma interface, precisa ser criada para que possamos implementá-la. No diretório "retrofit" de nosso projeto, criaremos um pacotes para Services, pois eles são destinados a um recurso da nossa API.
Então, dentro de "retrofit", criaremos o diretório "service", e nele criaremos ProdutoService, em que colaremos o código referente à definição das requisições por meio de assinaturas, da documentação do Retrofit. A assinatura obrigatoriamente devolverá uma Call, que é a entidade que representará nossa requisição e permitirá que ela seja executada.

Importaremos a Call de Retrofit, e toda vez que a devolvemos, é obrigatório indicar que tipo de retorno é esperado. Para isso, segundo a documentação, enviamos via Generics, no caso, uma lista de repositórios. No nosso caso, esperamos uma lista de produtos, e além disso, incluiremos uma anotação para indicar o método HTTP a ser executado para esta requisição em específico, e o endpoint.

import java.util.List;

import br.com.alura.estoque.model.Produto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ProdutoService {

    @GET("produto")
    Call<List<Produto>> buscaTodos();
}COPIAR CÓDIGO
Assim, a URL base que configuramos será acrescida da barra final (/) e de "produto", como definimos acima.

E para utilizarmos este serviço, a partir da instância de retrofit precisamos criar outra, referente à interface. Para isso acrescentaremos mais uma linha em EstoqueRetrofit(), usando create() e passando a referência do Service. Então, é necessário devolvermos como se fosse um atributo, a partir do qual teremos um Getter. Usaremos .field para que seja criado o atributo.

public EstoqueRetrofit() {
    Retrofit retrofit = new Retrofit,Builder()
        .baseUrl("http://192.168.20.249:8080/")
        .build();
    produtoService = retrofit.create(ProdutoService.class);
}

public ProdutoService getProdutoService() {
    return produtoService;
}COPIAR CÓDIGO
Deste modo, temos acesso ao nosso Service de qualquer lugar do aplicativo. Passaremos ao teste em que buscaremos por todos os produtos da nossa lista. Pelo nosso aplicativo, sabemos que fazemos esta busca internamente na lista de produto, então podemos acessar ListaProdutosActivity.java e, em buscaProdutos() adicionaremos o comportamento para que isso seja feito na API.

Comentaremos todo o código que faz a busca internamente, pois primeiro testaremos na API web e, caso tudo funcione bem, o próximo passo será a integração com a busca interna. A implementação será feita com a instância de EstoqueRetrofit(), como havíamos comentado, pegamos o getProdutoService() e o service, a partir do qual buscaremos a Call com buscaTodos().

Para uma Call, existem duas alternativas — a execução síncrona e a assíncrona. Teremos os mesmos problemas vistos no curso de Room, que quando executávamos de maneira assíncrona na main thread, a tela poderia travar, resultando na interferência do próprio Room, a não ser que incluíssemos uma permissão.

Neste caso, já que faremos uma requisição web, o próprio sistema operacional Android não permite este tipo de execução síncrona. Ou seja, faremos a execução síncrona também em uma Async Task. Veremos a abordagem assíncrona via Call em outro momento.

Precisaremos de dois Listeners, um para ser executado em background, cujo retorno devolveremos para que seja acessível para o outro. Este, por sua vez, fará uma execução deste retorno na UI thread. Faremos a execução síncrona via Call a partir do método execute(), que não pode ser usado de forma assíncrona, ou teremos uma exceção.

O programa acusa um erro de compilação pois exige que se trate a Exception lançada, então usaremos um bloco Try/Catch. Cada vez que o execute() for rodado, teremos o retorno de uma entidade denominada response, isto é, a resposta que teremos quando fizermos a requisição, como quando estávamos testando no Postman e tínhamos um retorno.

O response mantém um Generics na Call, pois ele irá compor todo o conteúdo da resposta, seja Body, Header, entre outros. No caso, acessaremos o corpo, que contém nossos produtos, a serem retornados pelo Listener. Dessa forma, eles são enviados ao onPostExecute(), o qual faz o envio para o próximo Listener que estará sendo atualizado na UI thread.

Estamos usando um Try/Catch, e pode ser que este retorno não chegue, por alguma falha de comunicação ou exceção. Para estes casos, é necessário declarar outro retorno, nulo. Feito isso, implementaremos o outro Listener com resultado. Tendo por padrão que os produtos novos serão existirão, no caso de haver uma referência nula a recomendação é que sempre verifiquemos isso, evitando um NullPointerException e notificando o usuário.

Por fim, basta executarmos a Async Task, responsável por buscar os produtos da API, enviar o retorno ao Listener, que atualizará o Adapter, e notificar caso haja algum problema.

private void buscaProdutos() {
    ProdutoService service = new EstoqueRetrofit().getProdutoService();
    Call<List<Produto>> call = service.buscaTodos();

    new BaseAsyncTask<>(() -> {
        try {
            Response<List<Produto>> resposta = call.execute();
            List<Produto> produtosNovos = resposta.body();
            return produtosNovos;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }, produtosNovos -> {
        if(produtosNovos != null) {
            adapter.atualiza(produtosNovos);
        } else {
            Toast.makeText(context: this,
                        text: "Não foi possível buscar os produtos da API",
                        Toast.LENGTH_SHORT).show();
        }
    }).execute();

//    new BaseAsyncTask<>(dao::buscaTodos,
//        resultado -> adapter.atualiza(resultado))
//        .execute();
}COPIAR CÓDIGO
Vamos executar o nosso aplicativo, levando em consideração que uma implementação deste tipo pode acabar resultando em várias falhas. Por isso, fica a sugestão de manter o Logcat aberto, configurado para "Error". Pode ser que demore um pouco para o programa ser rodado, pois fizemos as primeiras configurações.

Teremos um erro, e o Logcat indica que houve uma tentativa falha de fazer uma conversão para o tipo que queríamos. Isso porque quando utilizamos o Retrofit apenas com a configuração inicial de EstoqueRetrofit(), é feita uma tentativa de conversão para o seu tipo genérico, que aparece no Logcat como ResponseBody.

Com isso, não temos capacidade de configurar a conversão para tipos específicos, como é o caso de uma lista de produtos, ou de entidades que temos. Ou seja, precisaremos acrescentar um conversor, e a seguir veremos os detalhes de como fazê-lo.

@@04
Configurando o Service e AsyncTask

Crie o Service para Produto e implemente a requisição para buscar todos os produtos da API.
Disponibilize uma instância do Service e crie uma AsyncTask para executar a requisição. Utilize a BaseAsyncTask para auxiliá-lo.

Nesta atividade a execução apresenta um problema que será resolvido a seguir.

Para conferir o código de implementação acesse o seguinte commit.

https://github.com/alura-cursos/android-persistencia-web/commit/659ae49b8792874ea2f675a71b90a2f38ac85e09

@@05
Buscando os produtos da API

Consultando a documentação do Retrofit, teremos um tópico chamado "Retrofit Configuration" à direita, que explica sobre a configuração para a conversão da resposta para um objeto desejado. É citado o processo de desserialização, que é quando recebemos uma resposta via HTTP e convertemos para um objeto, o padrão é realmente devolver para um ResponseBody.
No entanto, é indicado que existem conversores capazes de fazer a devolução para objetos esperados, e é disso que precisamos. São listados conversores que usam bibliotecas que fazem desserialização e serialização de JSON para objetos, como é o caso de Gson, Jackson, Moshi e outros.

Além disso, podemos fazer nosso próprio conversor, e as orientações se encontram na documentação. Neste caso, dado que o objetivo é fazer uma configuração mais simples possível, utilizaremos o Gson. Copiaremos a dependência com.squareup.retrofit2:converter-gson, e a colaremos em nosso build.gradle, usando a mesma versão do Retrofit:

implementation 'com.squareup.retrofit2:converter-gson:2.5.0'COPIAR CÓDIGO
Será feito o download da biblioteca, e dependendo pode ser que isso demore, basta aguardar e sincronizar com o projeto, e assim que a sincronização for feita, podemos adicioná-lo à configuração. Em EstoqueRetrofit.java, então, durante o processo de build(), adicionaremos um Converter Factory.

public EstoqueRetrofit() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://192.168.20.249:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    produtoService = retrofit.create(ProdutoService.class);
}COPIAR CÓDIGO
Vamos limpar o Logcat, deixar a aba aberta e abrir o emulador, pois é muito comum que ocorram erros a que não estamos atentos durante a configuração. O aplicativo é rodado e é exibida a mensagem de erro que definimos anteriormente. Em Logcat, trocaremos "Error" por "Info", e apesar de nenhum erro ser mostrado, checaremos as informações para tentarmos entender o que houve.

Quando fizemos a requisição, ocorreu um problema que foi apresentado durante a versão do Android 9, envolvendo justamente as requisições HTTP. A partir desta versão, em específico da API 28, começamos a ter algumas mudanças de melhorias para o usuário final, pois quando fazemos uma requisição HTTP, que é uma requisição de um protocolo que trafega textos puros.

Ou seja, sempre que fazemos uma requisição, estamos buscando dados como se fossem textos puros pela rede, e o problema disso é que, se de repente estivermos em uma rede que é interceptada por algum software malicioso que verifica as informações trafegadas, o software acabará capturando um dado sensível, como uma senha, número de cartão de crédito, e assim por diante.

Pensando em evitar este tipo de problema, a própria equipe do Android, a partir da versão 9, da API 28, decidiu não permitir que o padrão seja feito em requisições HTTP, tanto que o Logcat indica que a exceção UnknownServiceException diz que "CLEARTEXT communication", que seria a comunicação de texto limpo envolvendo o HTTP para o endereço que configuramos (192.168.20.249) não é permitido via as regras de política de segurança de rede.

Assim, temos duas alternativas: idealmente, poderíamos utilizar um https em vez de uma requisição HTTP "pura". Porém, dado que nossa API não foi configurada para um HTTP, ou se estivermos trabalhando com uma API de teste sem certificação para HTTPS, podemos permitir uma requisição deste tipo.

Não é recomendado fazê-lo justamente pelo risco que corremos pelo usuário, mas podemos testar esta abordagem. Para isso, acessamos AndroidManifest.xml, e em nosso application utilizamos um atributo chamado usesCleartextTraffic para indicar que queremos que isso seja permitido, usando o valor true, android:usesCleartextTraffic="true".

Ao fazermos isso, o Android Studio inclusive nos orienta a buscar outras opções, informando também que ele passou a ser usado a partir da API 23. Vamos então executar e verificar o que acontece. É exibido um SocketException no Logcat, para indicar que não temos acesso para este tipo de ação.

Isso acontece pois, dependendo da API utilizada no Android, são necessárias permissões de sistemas, como é o caso do acesso à rede, de mapas (GPS), câmeras, entre outros. Para que esta execução de rede seja permitida, precisamos copiar um script de <uses-permission> na documentação do Android, que solicitará a permissão ao usuário.

Colaremos o script no manifest de AndroidManifest.xml, antes (fora) de application:

<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />COPIAR CÓDIGO
Neste caso, então, queremos uma permissão para acesso à internet, ou rede, e uma para o estado dela. Feito isso, reexecutaremos o emulador, e desta vez os nossos produtos são carregados com sucesso. Lidamos com toda a complexidade da parte de configurações, cuidados e outros detalhes importantes neste tipo de implementação.

Ainda há o que ser melhorado, claro, como é o caso de passarmos à integração da chamada interna e verificarmos estratégias possíveis para manter um fluxo que faça sentido. Continuaremos a seguir!

@@06
Adicionando converter e permitindo requisição HTTP

Adicione o converter para que a requisição converta o ResponseBody para List<Produto>. Para isso adicione um dos conversores do Retrofit ao projeto.
Gson: com.squareup.retrofit2:converter-gson
Jackson: com.squareup.retrofit2:converter-jackson
Moshi: com.squareup.retrofit2:converter-moshi
Protobuf: com.squareup.retrofit2:converter-protobuf
Wire: com.squareup.retrofit2:converter-wire
Simple XML: com.squareup.retrofit2:converter-simplexml
Scalars (primitives, boxed, and String): com.squareup.retrofit2:converter-scalars
No curso foi utilizado o converter do Gson, a versão é a mesma do Retrofit (2.9.0). Fique à vontade para usar o converter de sua preferência, porém, alguns deles podem exigir configurações extras.
Adicione o converter no momento de build do Retrofit. Em seguida, adicione as permissões de rede do Android e permita a execução de requisições http por meio do atributo usesClearTextTraffic na application via AndroidManifest.xml.

Teste o App e veja se os produtos contidos na API são apresentados.

Os produtos na API devem ser apresentados. O código para esta atividade pode ser conferido a partir deste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/3b63d9ce2a72e0e6fb411869a670389af9d40f30

@@07
Para saber mais - Sobre alternativas para permitir requisições HTTP

Como mencionado em vídeo, permitir a requisição HTTP não é uma boa prática, pois vai contra a política de segurança exigida a partir da versão 9 do Android.
A abordagem recomendada é por meio da configuração de segurança de rede. Para isso, você precisa criar um arquivo XML de configuração dentro do diretório res/xml com o nome network_security_config, então indicar na application via AndroidManifest.xml:

<?xml version="1.0" encoding="utf-8"?>
<manifest ... >
    <application android:networkSecurityConfig="@xml/network_security_config">
      <!-- restante do configuração -->
    </application>
</manifest>COPIAR CÓDIGO
Dentro dele é possível definir, por exemplo, domínios onde você permite o acesso via HTTP, como é o caso do IP do seu computador. Para mais detalhes confira a documentação.

https://developer.android.com/training/articles/security-config

@@08
Verificando possíveis erros

Antes de seguirmos com a implementação de nosso app, destacaremos um detalhe muito importante na integração do aplicativo com uma API web: até aqui, graças aos feedbacks precisos, viemos tratando de alguns problemas que foram surgindo, como quando não tínhamos permissão de acesso à internet, ou tentamos fazer uma requisição HTTP e fomos notificados de que no Android 9 não temos esta possibilidade, devido às regras de política de segurança.
Durante uma integração que faça o consumo de uma API web, será comum termos erros não tão facilmente identificáveis. Para exemplificar, faremos uma alteração aparentemente banal — acessaremos ProdutoService e, em vez de usarmos o endereço URL base com /produto, deixamos produtos.

Limparemos o Logcat e executaremos o emulador, que exibe aquela mensagem "Não é possível buscar os produtos da API", que nós colocamos em nossa Activity. Ao consultarmos o Logcat temos um erro que não nos diz muito do que houve, ou seja, não temos um feedback ideal.

Para conseguirmos facilitar esta investigação, existem duas alternativas bem interessantes: no Android Studio, além da execução comum, Run, existe outra, denominada Profile 'app', que monitora tudo que acontece em nosso aplicativo, em específico o nosso dispositivo.

Vamos testar executando o aplicativo novamente, desta vez em Profile 'app'. Será aberta uma aba com um perfil criado automaticamente (a chamada sessão), que indica o monitoramento de tudo que acontece em nosso aplicativo. Assim, conseguimos pausar a execução para analisar melhor determinada situação em relação a processamento, memória, rede, energia.

Ao clicarmos em rede (Network), por exemplo, temos que houve um pico que representa o que foi enviado e recebido, e conseguimos selecionar trechos se clicarmos e arrastarmos com o mouse. E então, se clicarmos na barrinha abaixo de "Timeline", verificamos a requisição realizada em detalhes.

O Retrofit é uma camada acima de okhttp, que é o User-Agent exibido na aba Request, que realmente faz a execução, a requisição web.
Como alternativa que não nos faz tão dependentes, existe uma biblioteca bastante utilizada em verificações para cada chamada que fazemos com o Retrofit. O Profile 'app' acaba pegando todas as chamadas de todas as bibliotecas, então se de repente estivermos usando Retrofit junto a uma biblioteca externa que não tem a ver com a comunicação que fazemos com a API direta, isso será exibido na timeline e pode não ser tão interessante.

Esta biblioteca é denominada Logging interceptor, cuja proposta é interceptar todas as requisições feitas com o Retrofit com maior precisão, em específico o próprio OkHttp, agente principal responsável pelas requisições. Sendo assim, ele conseguirá fazer o log de tudo que estiver acontecendo.

Para adicioná-lo, basta utilizar a dependência implementation 'com.squareup.okhttp3:logging-interceptor:(insert latest version)' via Gradle. Então a copiaremos e colaremos em nosso build.gradle. Antes disso, pausaremos a execução e excluiremos a sessão, para deixar a aba limpa quando formos executar outra. Caso você queira manter por motivos de histórico, fique à vontade.

Não podemos nos esquecer de incluir a versão mais recente na dependência, e para isso acessaremos repositórios que distribuem o Logging interceptor, como o MVN Repository e o JCenter. Ambos disponibilizam, como última versão, a 3.13.1, portanto será esta que utilizaremos: implementation 'com.squareup.okhttp3:logging-interceptor:3.13.1'.

Em seguida, faremos a sincronização, que envolve o download e acesso a esta lib, e sua configuração. Iremos inserir a configuração que vimos no site do OkHttp no momento em que criamos o Retrofit em EstoqueRetrofit(). Teremos que importá-lo com "Alt + Enter", e a única alteração que faremos é trocar o Level.BASIC por Level.BODY, que é o maior nível.

Percebam que criamos o client do OkHttpClient, o qual precisaremos adicionar e atrelar ao Retrofit, pois ele criará um client por padrão, e precisaremos adicionar um com as modificações que queremos, neste caso, o Logging interceptor. Então, no processo de build incluiremos client(), que assim ficará acessível para ser adicionado.

public EstoqueRetrofit() {
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(Level.BODY);
    OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

    Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.20.249:8080/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
    produtoService = retrofit.create(ProdutoService.class);
}COPIAR CÓDIGO
Vamos limpar o Logcat e executar o aplicativo no emulador com "Shift + F10" para verificarmos o que temos como feedback agora que temos um interceptador. Como resultado, obtivemos a mensagem de erro que incluímos na Activity. No Logcat, teremos maiores informações, agora mudando para o modo verboso, ou "Verbose", que é um modo debug.

Teremos que foi feito um GET para um endereço específico, que foi finalizado, a partir do qual obtivemos um 404. É mostrado tudo o que aconteceu, incluindo o que foi transferido, o resultado de Body, entre outros detalhes. Isso acaba sendo bastante similar ao que acontece no modo Profile, com o log sempre disponível para nós.

Com isso, temos flexibilidade e facilidade de entender o que acontece a cada requisição, e isso será muito útil quando não dominamos a API, ou quando fazemos uma configuração extra e acabamos cometendo erros de digitação, por exemplo. Corrigiremos produto de ProdutoService.java, e reexecutaremos a aplicação para confirmarmos que tudo foi feito corretamente.

Nos próximos passos acabaremos fazendo mais integrações, se de repente tivermos algum problema, conseguimos identificá-lo rapidamente.

@@09
Adicionando o logging interceptor

Faça com que os detalhes requisições feitas pelo Retrofit sejam apresentadas no logcat. Para isso adicione o logging interceptor:
implementation 'com.squareup.okhttp3:logging-interceptor:4.5.0'COPIAR CÓDIGO
Então configure-o para apresentar informações de nível BODY (mais detalhadas) e atribua o client do OkHttp atrelado ao logging interceptor ao processo de build do Retrofit.

Teste o App e veja se aparece as informações das requisições.

Em todas requisições devem aparecer no logcat informações como: url, status code, conteúdo do body, entre outras informações. Isso cada vez que uma requisição via Retrofit é executada.
O código desta atividade pode ser conferido a partir deste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/53ac4c8b5803331348b90165d873dcac9167a2fc

@@10
O que aprendemos?

Nesta aula, aprendemos a:
Adicionar e configurar o Retrofit;
Implementar e executar requisições HTTP;
Verificar informações trafegadas nas requisições HTTP.

#### 25/09/2023

@03-Integrando comportamento de busca

@@01
Salvando produtos da API internamente

Conseguimos carregar os produtos da nossa API web, porém, ao considerarmos esta abordagem, temos que nos atentar a alguns detalhes comuns no dia a dia. Sabemos que, ao executarmos nosso aplicativo, uma Activity acaba sendo destruída facilmente em algumas situações, como no caso de rotação de tela.
Portanto, precisamos testar tais comportamentos, entretanto, geralmente conseguimos fazê-lo sem nenhum problema por estarmos em um "cenário perfeito", com garantia de conexão, sendo que pode ser que isso não aconteça. No universo mobile, de Android, sabemos que internet é um grande desafio por sua instabilidade, e precisamos saber quando isto acontece e como isto pode ser abordado ao usuário.

Então, se por algum motivo perdermos a conexão à internet no momento em que temos os produtos, o que será que acontece caso a Activity seja destruída? Vamos testar no emulador com modo de avião ativado, rotacionando a tela e verificando o que acontece. É exibida a mensagem "Não foi possível buscar os produtos da API", e não temos mais os produtos que foram carregados pelo menos uma vez para o nosso usuário.

Cada vez mais percebemos que é necessária uma integração com o nosso banco de dados interno, pois é ele que manterá estes dados, uma vez que não temos uma comunicação externa. Durante a implementação, em que fazemos a execução e devolvemos os produtos novos, podemos fazer um processo antes da atualização do Adapter para que eles sejam salvos internamente. Poderemos ter um dao em ListaProdutosActivity.java chamando o método salva(), enviando produtosNovos, nossa lista de produtos recebidos.

new BaseAsyncTask<>(() -> {
    try {
        Response<List<Produto>> resposta = call.execute();
        List<Produto> produtosNovos = resposta.body();
        dao.salva(produtosNovos);
        return produtosNovos;
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}, produtosNovos -> {
// código omitido
}COPIAR CÓDIGO
Não temos um método que recebe uma lista, por isso podemos criá-lo com "Alt + Enter", selecionando "Create method 'salva' in 'ProdutoDAO'". Sabemos que o Room dá suporte a @Insert, portanto o incluiremos. Em ProdutoDAO.java, então, teremos:

@Insert
void salva(List<Produto> produtos);COPIAR CÓDIGO
Com isso salvamos internamente, e então em vez de devolvermos produtosNovos, o faremos com os produtos do nosso banco de dados:

new BaseAsyncTask<>(() -> {
    try {
        Response<List<Produto>> resposta = call.execute();
        List<Produto> produtosNovos = resposta.body();
        dao.salva(produtosNovos);
        return dao.buscaTodos();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}, produtosNovos -> {
// código omitido
}COPIAR CÓDIGO
Vamos testar no emulador apenas com esta modificação?

Nenhuma informação é carregada, apenas a mesma mensagem de erro que tivemos anteriormente, justamente por não termos acesso à internet. Isso será comum, pois o primeiro acesso é necessário para que pelo menos os produtos sejam carregados. Vamos, então, habilitar a internet, e rotacionar a tela do emulador.

A listagem de produtos é exibida como gostaríamos, mas quando rotacionamos a tela novamente, o aplicativo deixa de funcionar. Consultaremos o Logcat e entenderemos que houve um problema no momento da Async Task. E no banco de dados tivemos um problema de Constraint, que no caso se refere ao ID.

Isso porque estamos tentando salvar novamente um produto que já existe, e isso não é possível. No caso, temos que atualizá-los, ou seja, em vez de simplesmente salvarmos, poderíamos verificar se ele já existe e, sendo este o caso, alteramos, caso contrário, aí sim salvamos.

Para isto usaremos o Room e a técnica para quando houver conflito, a REPLACE, que fará a substituição de acordo com o ID do produto:

@Insert(onConflict = OnConflictStrategy.REPLACE)
void salva(List<Produto> produtos);COPIAR CÓDIGO
Isto evitará que usemos ifs; feito isso, reexecutaremos a aplicação. Testaremos os modos de paisagem e retrato no emulador, mas quando reativamos o modo avião, os produtos deixam de ser carregados. Por mais que a busca esteja sendo feita internamente, ela está sendo feita somente quando a requisição não lança nenhuma exceção.

Portanto, em vez de devolver nulo, que seria a alternativa quando a tentativa de requisição falha, devolveríamos o dao.buscaTodos(). Além disso, deste modo não será mais necessário retornar a busca dentro do try, e passamos a ter apenas um retorno, diretamente do banco de dados.

new BaseAsyncTask<>(() -> {
    try {
        Response<List<Produto>> resposta = call.execute();
        List<Produto> produtosNovos = resposta.body();
        dao.salva(produtosNovos);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return dao.buscaTodos();
}, produtosNovos -> {
// código omitido
}COPIAR CÓDIGO
Feita esta alteração, vamos executar a aplicação novamente. Desta vez, tudo funciona, tanto no modo avião quanto com conexão à internet. A experiência do usuário se mantém intacta, e os produtos são carregados independemente da situação da internet, como esperado.

Então, toda vez que fizermos implementações que envolvam comunicações externas, é importante nos certificarmos de que a experiência do usuário funciona conforme desejado. Esta não é necessariamente uma regra, e em comportamentos como salvar, alterar e remover produtos, talvez faça sentido usarmos outras estratégias, como permitirmos que o produto seja salvo somente online, ou algo assim.

@@02
Salvando produtos internamente

Caso você precise do projeto com todas as alterações realizadas na aula passada, você pode baixá-lo neste link.
Modifique o código que busca todos os produtos para que salve os produtos internamente e depois busque todos novamente para que sejam atualizados no adapter.

Para evitar o problema de conflito de ids, adicione a estratégia de substituição ao inserir os produtos.

Faça o teste do App e veja se são apresentados os produtos contidos na API e no banco de dados internamente.

https://github.com/alura-cursos/android-persistencia-web/archive/aula-2.zip

O App deve apresentar as informações da API e banco interno como esperado. O código desta atividade pode ser conferido a partir deste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/f61f589abba6b26e9e4c9b55836cb0659b7b507b

@@03
Sincronizando busca de produtos

Resolvemos a situação em que não estávamos carregando os produtos quando destruíamos a Activity e deixávamos de ter conexão a internet. Porém, pode acontecer de a nossa requisição web com a API demorar um pouco, e não testamos este comportamento; precisamos verificar a experiência do usuário neste tipo de contexto, que pode ser bem comum.
Para isso, acrescentaremos um Thread.sleep() no try de ListaProdutosActivity.java, e depois incluiremos outro catch.

new BaseAsyncTask<>(() -> {
    try {
        Thread.sleep(millis: 3000);
        Response<List<Produto>> resposta = call.execute();
        List<Produto> produtosNovos = resposta.body();
        dao.salva(produtosNovos);
        return produtosNovos;
    } catch (IOException e) {
        e.printStackTrace();
    } catch (InterruptedException e) {
            e.printStackTrace();
    }
    return null;
}, produtosNovos -> {
// código omitido
}COPIAR CÓDIGO
Testando nosso aplicativo, a lista de produtos é carregada após 3 segundos, depois de exibir uma tela em branco, então, por mais que estejamos fazendo a requisição, ainda estamos suscetíveis a esta demora, comprometendo a experiência do usuário. Uma abordagem inicial que podemos tentar é executar a Async Task que tínhamos anteriormente, que carregava todos os produtos de imediato, internamente.

Assim, quando buscaProdutos() for chamado, será feita uma Async Task para uma busca externa, seguida por uma interna e uma notificação, e em paralelo será executada uma Async Task que fará uma busca interna, fazendo a atualização do que demorar para ser carregado.

Inclusive, para verificarmos se tudo funciona da maneira esperada, podemos remover alguns dos produtos internamente. Executaremos a aplicação mais uma vez, e internamente teremos apenas um produto, enquanto externamente, todos. O que esperamos é que se carregue primeiro o único produto, e depois os demais.

O programa quebrou! Precisamos entender o motivo disso a partir da exceção que aparece no Logcat. Aparentemente, houve certa inconsistência, uma View inválida a partir do View holder, uma posição indesejada. Dado que fazemos a notificação atualiza(resultado) em nosso Adapter, existe uma situação na qual buscamos de maneira "simultânea", e a notificação é realizada sendo que o tamanho da lista é diferente.

A notificação que temos vai de 0 (zero) até o tamanho da lista atual, então pode ser que os tamanhos interno e externo sejam distintos. Sendo assim, precisamos tomar alguns cuidados, como evitar a abordagem de dizer o tamanho, já que temos como fazer uma atualização rápida ou simultaneamente.

Além disso, podemos indicar que ao acessar atualiza(), toda a notificação de dataset seja limpada, e antes disso, incluir notifyItemRangeRemoved() em ListaProdutosAdapter.java:

public void atualiza(List<Produto> produtos) {
    notifyItemRangeRemoved(positionStart: 0, this.produtos.size());
    this.produtos.clear();
    this.produtos.addAll(produtos);
    this.notifyItemRangeInserted(positionStart: 0, this.produtos.size());
}COPIAR CÓDIGO
Então, quando trabalhamos com dois procedimentos que são executados "simultaneamente" corremos este risco. Executaremos a aplicação via emulador novamente; a tela em branco é exibida, depois ocorrem dois loads estranhos. Inicialmente, removeremos os dois produtos novamente, e faremos a execução mais uma vez.

Desta vez, os três produtos são carregados, e depois isto se repete. Por mais que tenhamos as Async Tasks, elas não estão executando em paralelo. Isso quer dizer que, quando executamos várias Async Tasks, elas formam uma fila de execução em uma Thread separada, em background, e cada uma delas entrará em uma fila.

Isto é, a segunda Async Task só será executada após a finalização da primeira. Assim, a UI Thread chama uma Async Task 1, e também uma Async Task 2. Ambas entrarão em uma Thread em segundo plano (Default background thread), e a primeira é executada. Enquanto isto, a segunda ficará aguardando, e só depois que a primeira é finalizada, e notifica isto, aí sim a segunda Thread entra em execução, e assim sucessivamente.

Assim, da maneira como estamos fazendo, se uma Async Task demorar, as outras serão comprometidas, e é assim que elas funcionam por padrão. Portanto, vemos que deste modo não conseguimos resolver nosso problema. Queremos que esta execução, que pode demorar, realmente saia desta fila, e não prenda as demais Async Tasks, como é o caso da busca interna.

Para isso, em vez de chamarmos execute(), que é quem faz o envio para a Thread em background, chamaremos executeOnExecutor(), com que precisaremos enviar mais um argumento indicando o Executor. Trata-se de uma implementação que será basicamente uma nova Thread.

Usaremos uma implementação própria do Async Task, uma constante denominada THREAD_POOL_EXECUTOR, ou seja, uma execução fora do padrão, e que não entrará na fila:

new BaseAsyncTask<>(() -> {
    try {
        Thread.sleep(millis: 3000);
        Response<List<Produto>> resposta = call.execute();
        List<Produto> produtosNovos = resposta.body();
        dao.salva(produtosNovos);
        return produtosNovos;
    } catch (IOException e) {
        e.printStackTrace();
    } catch (InterruptedException e) {
            e.printStackTrace();
    }
    return null;
}, produtosNovos -> {
    if(produtosNovos != null) {
        adapter.atualiza(produtosNovos);
    } else {
        Toast.makeText(context: this,
                    text: "Não foi possível buscar os produtos da API",
                    Toast.LENGTH_SHORT).show();
    }
}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);COPIAR CÓDIGO
Vamos executar o emulador e ver o que acontece?

De imediato, perceberemos que os produtos são carregados, e após 3 segundos, eles virão com os produtos da API. Podemos remover os produtos internamente, e verificar o resultado. Ao executarmos mais uma vez, apenas o produto interno é carregado, e após 3 segundos, os demais surgem na listagem.

Porém, precisamos ficar atentos pois isto pode ocorrer do lado interno, pois podemos ter apenas um produto internamente e, no caso, conseguiremos fazer o load deles. Para isso, utilizaremos uma expressão lambda:

new BaseAsyncTask<>(() -> {
    List<Produto> produtosInterno = dao.buscaTodos();
    try {
        Thread.sleep(millis: 3000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return produtosInterno;
},COPIAR CÓDIGO
Entretanto, a notificação ainda será um tanto demorada. Vamos simular uma situação na qual a busca interna demorará um pouco mais, e não a externa. Inclusive diminuiremos o tempo de Thread.sleep() do try de BaseAsyncTask anterior para 1 segundo. Já sabemos que internamente há apenas um produto, e externamente, três.

Executando a aplicação, teremos uma lista vazia, seguida de uma lista com três produtos, e depois um. Tivemos um comportamento totalmente inesperado, estamos sendo comprometidos pelo tempo de execução da nossa Async Task. Quando temos procedimentos que serão executados em paralelo e exigirão algum tipo de regra de sincronismo, precisaremos ordenar quando cada um deles será executado, chamar o que precisa ser chamado, e assim por diante.

Neste caso em específico, podemos pensar que a busca interna precisa ser feita de qualquer forma, e ela tende a demorar menos. Sendo finalizada, ela deverá chamar a busca externa. Assim, removeremos o bloco Try/Catch com produtosInterno, e após a atualização do nosso Adapter, faremos a Async Task responsável pela busca externa. E então moveremos todo o código referente à atualização externa de modo a deixar o código da seguinte forma:

new BaseAsyncTask<>(dao::buscaTodos,
    resultado -> {
        adapter.atualiza(resultado);
        new BaseAsyncTask<>(() -> {
            try {
                    Thread.sleep(millis: 3000);
                    Response<List<Produto>> resposta = call.execute();
                    List<Produto> produtosNovos = resposta.body();
                    dao.salva(produtosNovos);
                    return produtosNovos;
            } catch (IOException e) {
                    e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
    }, produtosNovos -> {
        if(produtosNovos != null) {
            adapter.atualiza(produtosNovos);
        } else {
            Toast.makeText(context: this,
                        text: "Não foi possível buscar os produtos da API",
                        Toast.LENGTH_SHORT).show();
        }
    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        })
    .execute();COPIAR CÓDIGO
Feito isso, executaremos o aplicativo e verificaremos o que acontece. Para testarmos aquele mesmo comportamento com que lidamos recentemente, removeremos os dois últimos produtos e executaremos mais uma vez. Agora, sim, temos a listagem de um produto, e após 3 segundos, são carregados externamente os outros dois.

Aproveitaremos para testar no modo avião também, e os produtos estão sendo mantidos conforme gostaríamos. Então, após o carregamento interno dos produtos, é feita a busca externa, fazemos a sincronização necessária, e tudo o mais, para que se mantenham os produtos novos.

@@04
Sobre a execução de AsyncTask

Ao implementar o código que integra o App com a API para buscar produtos, vimos que existem peculiaridades na execução de AsyncTasks simultaneamente. Marque as alternativas corretas em relação aos detalhes de AsyncTask:

AsyncTask opera de maneira síncrona ou assíncrona.
 
Exato! Cada vez que usamos AsyncTasks precisamos pensar no que será executado, o quanto costuma demorar, e se é desejada a sincronia entre outras AsyncTasks.
Alternativa correta
Executar duas ou mais AsyncTask podem travar a UI.
 
Alternativa correta
AsyncTask cria uma thread nova cada vez que é executada.
 
Alternativa correta
AsyncTask executadas simultaneamente são executadas na mesma Thread.
 
Isso mesmo! Por padrão as AsyncTasks são executadas como uma fila de execução: enquanto a primeira não for finalizada, as demais não são executadas.

@@05
Aplicando estratégia de busca de produtos

Modifique o código para que primeiro busque os produtos internos e depois faça a busca na API web.
Para esta atividade considere os seguintes pontos:

Delay na busca na API para verificar se os produtos internos são carregados logo de cara;
Execução da busca na API em uma thread separada das AsyncTasks;
Corrigir o problema no adapter do RecyclerView por notificar atualização mais de uma vez rapidamente.
Teste o App e veja se apresenta os comportamentos esperados. Para verificar se primeiro busca internamente, remova produtos que estão contidos apenas na API.

O App deve rodar sem nenhum problema apresentando os produtos internos inicialmente e depois do delay apresentando os produtos contidos na API. O código desta atividade pode ser conferido a partir deste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/cca30f2d451c0b16186ac38f6c05311117b65208

@@06
Refatorando o código

Feita a primeira integração entre a aplicação e a API, podemos partir para a refatoração do nosso código, que atualmente possui certa complexidade, e precisamos deixá-lo o mais simples possível, com base no que é estritamente necessário. Vamos começar identificando o que cada uma destas Async Tasks fazem, nomeando-as.
A mais interna delas busca os produtos na API, portanto selecionaremos toda a Async Task e a extrairemos para um método, o buscaProdutosNaApi. Assim, fica muito mais tranquilo entender que, após a atualização do Adapter, buscamos os produtos na API.

Da mesma forma, extrairemos a segunda Async Task para buscaProdutosInternos, entretanto temos alguns detalhes, pois colocamos a call antes mesmo de chamarmos este método, sendo que este recebe a call, necessária apenas em buscaProdutos(). Portanto, a chamada abaixo poderá ser migrada para dentro de buscaProdutosNaApi.

ProdutoService service = new EstoqueRetrofit().getProdutoService();
Call<List<Produto>> call = service.buscaTodos();COPIAR CÓDIGO
Assim, não precisamos mais dos parâmetros Call<List<Produto>> call:

private void buscaProdutosNaApi() {
    ProdutoService service = new EstoqueRetrofit().getProdutoService();
    Call<List<Produto>> call = service.buscaTodos();
// código omitido
}COPIAR CÓDIGO
Tampouco precisamos de argumentos, então deletaremos Call<List<Produto>> call e call do buscaProdutosNaApi() de buscaProdutosInternos(). Também removeremos call de buscaProdutosInternos() de buscaProdutos(). A chamada de busca de produtos fica muito mais limpa.

Agora, lidaremos com buscaProdutosNaApi(), que possui complexidades que podem ser evitadas, como no caso de Thread.sleep(millis: 3000), o qual deletaremos. Também removeremos o segundo catch de BaseAsyncTask, e a verificação de nulo, pois o colocamos quando fazíamos o retorno direto de produtosNovos.

Mas quando fazemos a busca internamente via dao, sempre retornaremos uma referência de uma lista, seja ela vazia ou não. Além disso, podemos realizar a atualização diretamente.

new BaseAsyncTask<>(() -> {
    try {
        Response<List<Produto>> resposta = call.execute();
        List<Produto> produtosNovos = resposta.body();
        dao.salva(produtosNovos);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return dao.buscaTodos();
}, produtosNovos -> 
            adapter.atualiza(produtosNovos))
            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);COPIAR CÓDIGO
Nosso código fica muito mais enxuto e simples de ser compreendido. Vamos executar a aplicação e verificar se tudo funciona conforme esperado. O primeiro teste será confirmar se o único produto existente no banco de dados é carregado, o que ocorre sem problemas. Depois, rotacionaremos a tela, e também não temos nenhuma queixa. Ativaremos a internet e verificaremos que os produtos da API são carregados, rotacionaremos a tela do emulador mais uma vez, colocaremos no modo avião e veremos se o comportamento esperado se mantém.

Um detalhe, comentado desde o começo dos cursos de fundamentos de Android, é que, por mais que estejamos buscando produtos na Activity, ela está com responsabilidades demais, tendo que identificar se a busca é interna ou não, código de requisições HTTP, entre outras tarefas que ela não deveria ter.

De modo geral, a Activity serve como uma controladora, como comentado anteriormente. No máximo, ela estará atualizando seus componentes visuais, então, a seguir veremos como extrair todo o código para uma única classe, capaz de manter responsabilidades para se comunicar com a Activity, flexibilizando esta solução.

@@07
Refatorando o código de busca de produtos

Refatore o código de busca de produtos interno e também da API. Para a refatoração considere técnicas de extração de código, remoção de códigos desnecessários.
Por fim, teste o App após refatoração e veja se tudo funciona como esperado.

O App deve funcionar como esperado, o código pode ser conferido a partir deste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/587e85ffb3828acc34498b0084d12645d569e532

@@08
Criando um repositório

Identificamos a necessidade de migrar os comportamentos de busca de produtos internos e na API para uma classe. Mas qual será a nova classe que manterá estas duas responsabilidades, dado que seu objetivo é manter apenas uma? Considerando ideias provenientes de arquitetura de software, componentes comuns em diversos tipos de projeto, existe um componente bastante utilizado nestes casos.
Ele é conhecido como repositório, ou Repository, em inglês, e lidará com a questão da origem dos dados, enviando-a para quem solicitar. Então, criaremos um repositório a ser mantido pela Activity, que pedirá os dados para ele, e a lógica é mantida seja na API, no banco de dados, ou em qualquer tipo de outra solução que armazene as informações dos nossos produtos. Ou seja, incluiremos esta camada a mais em nosso aplicativo.

Para a implementação, acessaremos "app > java", e com "br.com.alura.estoque" selecionado, usaremos "Alt + Insert" para buscar por package. O nome dele será repository, e em seguida criaremos a classe denominada ProdutoRepository, e então migraremos os métodos de ListaProdutosActivity.java, copiando e colando. Fazemos os importes e temos acesso a todos os comportamentos que a Activity não precisará mais ter, referentes a buscaProdutosNaApi(), buscaProdutosInternos() e buscaProdutos().

Feito isso, teremos que resolver alguns detalhes. Começaremos modificando a Activity — em vez de chamarmos buscaProdutos() como se fosse o membro interno da Activity, criaremos uma instância do Repository, e utilizaremos um método seu, chamado de repository:

EstoqueDatabase db = EstoqueDatabase.getInstance(this);
dao = db.getProdutoDAO();

ProdutoRepository repository = new ProdutoRepository();
repository.buscaProdutos();COPIAR CÓDIGO
Aproveitaremos para deixarmos o método buscaProdutos() como público, e vamos fazendo ajustes conforme necessário. Precisamos adaptar nosso código para que todos os procedimentos necessários ocorram e, quando for o caso, notifiquemos a Activity para que ela consiga atualizar suas informações.

O Repository é um componente que lidará apenas com os dados, portanto chamadas como atualiza() do Adapter, por exemplo, precisa ser feita para quem estiver chamando o Repository, o qual não terá este tipo de responsabilidade. Inclusive, substituiremos a linha adapter.atualiza(resultado) de buscaProdutosInternos() por // notifica que o dado está pronto.

Da mesma forma, em buscaProdutosNaApi(), também removeremos adapter.atualiza(produtosNovos) e deixaremos em seu lugar //notifica que o dado está pronto. No caso do DAO, será uma dependência, portanto criaremos um atributo de tipo ProdutoDAO. Podemos recebê-lo via construtor sem nenhum problema, ou então pedir um contexto, e criar um banco de dados.

Se for o caso, posteriormente fazemos a refatoração para que ele receba o contexto, e montamos o banco de dados, mas por ora faremos assim:

public class ProdutoRepository {

    private final ProdutoDAO dao;

    public ProdutoRepository(ProdutoDAO dao) {
        this.dao = dao;
    }
// código omitido
}COPIAR CÓDIGO
Agora que temos nosso DAO, precisamos fazer alguns ajustes para conseguirmos notificar a Activity, pois do jeito em que está, o buscaProdutos() é chamado, são feitas todas as operações, a busca na API, salvando internamente e depois buscando no banco de dados interno, mas em nenhum momento é feita uma notificação no Adapter.

Utilizaremos a mesma técnica aplicada em Listeners, sendo assim teremos um Listener próprio para o repositório. Ainda em ProdutoRepository.java, então, teremos:

public interface ProdutosCarregadosListener {
    void quandoCarregados(List<Produto> produtos);
}COPIAR CÓDIGO
Teremos que reutilizar este Listener nos pontos em que esta notificação é necessária, e a Activity o implementará e fará a atualização. Podemos criar um atributo ProdutosCarregadosListener, pedindo isto via construtor. Assim, nosso ProdutoRepository será uma classe que fará todos os comportamentos envolvidos na persistência dos produtos, seja interna ou externamente, e não apenas manterá a busca deles.

private final ProdutoDAO dao;
private final ProdutosCarregadosListener listener;

public ProdutoRepository(ProdutoDAO dao,
                                                ProdutosCarregadosListener listener) {
    this.dao = dao;
    this.listener = listener;
}COPIAR CÓDIGO
Então, ações como salvar, editar e remover um produto também serão migradas para esta classe. E se colocarmos um Listener que apenas atualiza o Adapter como se fosse uma lista de produtos que estão sendo recebidos, ficaremos limitados e atrelados a este tipo de solução. Portanto, em vez de recebermos um Listener dentro do construtor cada vez que instanciarmos um Repository, receberemos no momento em que chamamos os nossos produtos, isto é, os buscamos.

Neste momento, pediremos ao nosso cliente, que no caso é a nossa Activity, para que ela faça a implementação do Listener. Desta maneira flexibilizamos este tipo de abordagem, então manteremos o código da seguinte forma:

private final ProdutoDAO dao;

public ProdutoRepository(ProdutoDAO dao) {
    this.dao = dao;
}COPIAR CÓDIGO
Assim, mantemos uma única instância e, para cada comportamento, colocaremos o listener desejado, que teremos que delegar para os comportamentos mais internos, como é o caso da busca interna:

public void buscaProdutos(ProdutosCarregadosListener listener) {
    buscaProdutosInternos(listener);
}COPIAR CÓDIGO
Agora que estamos recebendo este Listener, trocaremos // notifica que o dado está pronto de buscaProdutosInternos() por listener.quandoCarregados(resultado), e é assim que delegamos para a nossa Activity, algo necessário também em buscaProdutosNaApi():

private void buscaProdutosInternos(ProdutosCarregadosListener listener) {
    new BaseAsyncTask<>(dao::buscaTodos,
                    resultado -> {
                        listener.quandoCarregados(resultado);
                        buscaProdutosNaApi(listener);
                    }).execute();
}COPIAR CÓDIGO
E quando for para identificar que houve algo notificável à Activity, quando temos produtos novos, teremos:

new BaseAsyncTask<>(() -> {
    // trecho com try, catch e return omitidos
}, listener::quandoCarregados)
            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);COPIAR CÓDIGO
Esta abordagem é mais interessante inclusive por permitir Method references em vez de uma expressão lambda verbosa. Feito isso, acessaremos a Activity, deletaremos listener de nosso construtor:

EstoqueDatabase db = EstoqueDatabase.getInstance(this);
dao = db.getProdutoDAO();

ProdutoRepository repository = new ProdutoRepository(dao);
repository.buscaProdutos(adapter::atualiza);COPIAR CÓDIGO
Fizemos uma refatoração um pouco mais complexa que exige um pouco mais de compreensão do que está sendo feito, e então podemos executar o aplicativo. Vamos fazer testes similares aos feitos anteriormente, mas começaremos verificando se ele não quebra, depois removeremos dois produtos, que são da API, e manteremos apenas um internamente.

Não tivemos nenhum problema, e deixamos o código muito mais limpo e elegante. A seguir veremos como migrar outros comportamentos.

@@09
Migrando o código para o repositório

Crie um repositório para manter o código de busca de produtos interno e na API. Nesta implementação faça com que o repositório lide apenas com os dados e notifique quem o chamar que o dado ficou pronto.
Você pode usar listeners para notificar quando o dado está pronto.
Após migração, execute o App e veja se é mantido o mesmo comportamentos de antes.

Deve ser mantido o mesmo comportamento de antes, a diferença é que a responsabilidade de pegar os produtos da fonte (banco de dados interno ou API) fica apenas no repositório. Você pode conferir o código a partir deste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/ae2fecdbb2cfc88db5c3cf0ec89377961e4607d8

@@10
O que aprendemos?

Nesta aula, aprendemos a:
Integrar o comportamento de busca de produtos com a API;
Aplicar estratégias de integração;
Peculiaridades de sincronismo entre AsyncTask;
Criação e utilização de repositórios.

#### 27/09/2023

@@01
Criando listener genérico

Da mesma forma como migramos o comportamento de buscar os produtos, vamos fazer com os demais que envolvam o banco de dados, como no caso da remoção, de salvar e da edição. Tudo isso irá para ProdutoRepository, que ficará responsável por lidar com os dados e suas fontes. Começaremos com o comportamento de salvar, e então vamos testando e partimos para a integração com a nossa API.
Recortaremos o trecho abaixo, de ListaProdutosActivity.java:

private void salva(Produto produto) {
    new BaseAsyncTask<>(() -> {
        long id = dao.salva(produto);
        return dao.buscaProduto(id);
    }, produtoSalvo ->
                adapter.adiciona(produtoSalvo))
                .execute();
}COPIAR CÓDIGO
E colaremos em ProdutoRepository.java, logo antes do trecho de código referente a ProdutosCarregadosListener. E então substituiremos a linha adapter.adiciona(produtoSalvo) por //notificar que dado está pronto. E passaremos a utilizar o repositório no momento em que chamávamos o salva.

Porém, para reutilizarmos o repositório em todos os membros da Activity, o primeiro passo é criar seu atributo (repository) no lugar de uma variável local no onCreate() com "Ctrl + Alt + F":

repository = new ProdutoRepository(dao);
repository.buscaProdutos(adapter::atualiza);COPIAR CÓDIGO
E quando fazemos a configuração do dialog para salvarmos um produto, ele recebe um Listener como segundo argumento, então, basicamente, ele envia um produto que foi criado em nosso dialog, e portanto o colocamos como Method reference. Agora que sabemos que estamos criando um produto, que podemos chamar de produtoCriado, abriremos uma expressão lambda para facilitar a compreensão do que acontecerá aqui, usando o repository chamando o salva().

private void abreFormularioSalvaProduto() {
    new SalvaProdutoDialog(context: this, produtoCriado -> {
        repository.salva(produtoCriado);
    }).mostra();
}COPIAR CÓDIGO
Em seguida precisaremos fazer alguns ajustes para adaptação conforme o necessário. Vamos alterar o modificador de acesso (salva), que está como private e precisa estar como public. Isso faz com que o código compile corretamente, depois, faremos com que a Activity seja notificada, para conseguir fazer a atualização desejada, do Adapter.

Para isso, utilizaremos a estratégia de acrescentar um Listener, porém, teremos um problema se usarmos ProdutosCarregadosListener, pois aqui não teremos uma lista de produtos para que seja adicionada em nosso Adapter, em que incluiremos um único produto, e com este Listener não temos como fazê-lo.

Dado que queremos justamente flexibilizar o uso do nosso Listener para n outras situações, uma das abordagens que podemos considerar é criar um Listener genérico. Da mesma forma como criamos uma Async Task genérica, implementaremos uma interface genérica que pode receber qualquer tipo, o FinalizadaListener:

public interface FinalizadaListener<T> {
    void quandoFinalizada(T resultado);
}COPIAR CÓDIGO
Vamos, então, alterar o nome da interface ProdutosCarregadosListener em ProdutoRepository.java:

public interface DadosCarregadosListener <T> {
    void quandoCarregados(T resultado);
}COPIAR CÓDIGO
Desta forma deixamos de uma maneira muito genérica, podemos enviar produtos, e assim por diante. O T vem do Type, que no caso é tipo genérico. Agora, temos um DadosCarregadosListener, que recebe um tipo genérico. E em vez de utilizarmos o Listener que fica bem definido e sempre receberá uma lista de produtos, definiremos em cada um dos parâmetros o que esperamos para este tipo de interface.

Começando por buscaProdutos(), esperamos que o Listener devolva uma lista de produtos via Generics:

public void buscaProdutos(DadosCarregadosListener<List<Produto>> listener) {
    buscaProdutosInternos(listener)
}COPIAR CÓDIGO
Faremos o mesmo para os demais Listeners, que prometem devolver uma lista de produtos, isto é, buscaProdutosInternos() e buscaProdutosNaApi(). E em salva(), utilizaremos novamente DadosCarregadosListener, incluindo um produto apenas. Além disso, faremos a notificação enviando produtoSalvo, a ser carregado em nosso Adapter, e que pode ser um Method reference.

public void salva(Produto produto,
                            DadosCarregadosListener<Produto> listener) {
    new BaseAsyncTask<>(() -> {
        long id = dao.salva(produto);
        return dao.buscaProduto(id);
    }, listener::quandoCarregados)
                    .execute();
}COPIAR CÓDIGO
Feito este ajuste, temos todo o poder de reutilizarmos a mesma solução em vários casos diferentes. Voltaremos a ListaProdutosActivity.java para implementarmos produtoSalvo como Method reference.

private void abreFormularioSalvaProduto() {
    new SalvaProdutoDialog(context: this, produtoCriado ->
        repository.salva(produtoCriado, adapter::adiciona))
        .mostra();
}COPIAR CÓDIGO
Antes de passarmos à integração, vamos testar a aplicação. No caso, a inserção do produto só será realizada de maneira local, no banco de dados interno, e iremos para o externo posteriormente. Testaremos salvando um computador de preço 2000 e quantidade 10, rotacionando a tela do emulador, e não temos nenhum problema.

A migração foi bem sucedida, a partir de uma solução genérica para reutilizar a ideia do Listener sendo que é possível obtermos resultados diferentes, como no caso de um produto a ser devolvido, ou uma lista de produtos.

@@02
Migrando o comportamento de salvar

Caso você precise do projeto com todas as alterações realizadas na aula passada, você pode baixá-lo neste link.
Migre o comportamento de salvar produto para que seja responsabilidade do repositório da mesma forma que foi feito com o comportamento de buscar produtos.

Nesta migração, considere renomear de membros existentes para melhorar a coesão.

Após migração teste o App e veja se a inserção de produtos de maneira offline funciona como esperado.

https://github.com/alura-cursos/android-persistencia-web/archive/aula-3.zip

O comportamento de salvar deve funcionar como antes, a diferença é que a Activity não mantém mais essa responsabilidade. Você pode conferir o código desta atividade a partir deste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/7ebab970d6dcf14a57ba79bb68d7909cef773fae

@@03
Utilizando callbacks do Retrofit

Feita a migração do comportamento de salvar para o nosso repositório, faremos o código que vai integrar este comportamento com a nossa API. Será uma abordagem muito similar ao que fizemos quando buscamos os produtos da API, portanto começaremos criando uma requisição. Em ProdutoService.java criaremos um método chamado salva(), que retornará uma Call, seguindo o padrão conforme a implementação atual do Retrofit.
Precisamos fazer uma requisição que irá atender ao que nossa API espera, POST, a receber um produto via corpo da requisição, devolvendo um produto com ID esperado, isto é, gerado na API. Definiremos o tipo, @POST, que terá o endereço produto. A novidade aqui é entendermos como enviaremos o nosso objeto produto via corpo da requisição.

Isto será feito via parâmetro, e indicaremos o que ele significa dentro da requisição. Já que ele fará parte do corpo, teremos uma notação @Body, e então definiremos o retorno de um único produto, portanto usaremos um Generics. O trecho de código ficará da seguinte maneira:

public interface ProdutoService {

    @GET("produto")
    Call<List<Produto>> buscaTodos();

    @POST("produto")
    Call<Produto> salva(@Body Produto produto);
}COPIAR CÓDIGO
Em seguida, reutilizaremos esta requisição em nosso repositório, então, replicaremos o que foi feito anteriormente em buscaProdutosNaApi() em salva(). Porém, para conseguirmos acessar a Call, também precisamos acessar o service, o que fazemos em mais de um lugar, e basta uma única instância para isto, o tornaremos um membro da nossa classe, do repositório, sendo um atributo.

Assim, recortaremos a linha ProdutoService service = new EstoqueRetrofit().getProdutoService() de buscaProdutosNaApi() e, a colaremos no momento em que construímos a nossa classe, em ProdutoRepository.java, tornando o service acessível via atributo de classe:

public ProdutoRepository(ProdutoDAO dao) {
    this.dao = dao;
    service = new EstoqueRetrofit().getProdutoService();
}COPIAR CÓDIGO
Faremos a implementação em salva() considerando a estratégia que optaremos para o comportamento de salvar um produto, dentre podermos salvar na API, o que traz um retorno que salvamos internamente, e que é atualizado no Adapter, ou salvar internamente, atualizar no Adapter e depois enviar à API.

Dado que estaremos alterando uma informação, e que elas sempre são pegas da API, primeiramente salvaremos na API e nos certificaremos disso, para depois buscarmos estas informações, caso contrário teremos certa inconsistência de dados. Por exemplo, se de repente ficarmos offline e salvarmos o primeiro produto quando na API já existem 5, quando a conexão for restabelecida, buscaremos todos os dados da API, e ela irá sobrescrever o produto recém criado.

Claro, existem técnicas e sincronias para tornar isso possível de ser feito offline, mas isso exige um pouco mais de tempo e conhecimento que não serão vistos neste curso. É por isto que optaremos por salvar, editar e remover tudo na API, e quando tivermos certeza de que isto foi feito lá, fazemos o mesmo internamente.

Inicialmente será apenas a busca que deixaremos funcionando internamente, para depois passarmos à busca na API. Para esta implementação, chamaremos salva() em service enviando o produto recebido, e teremos a Call com todo o comportamento de requisição, como por exemplo criar uma Async Task.

Entretanto, como comentado anteriormente, além de fazer uma execução síncrona com execute(), também temos a opção da execução assíncrona, o que é esperado no caso de uma requisição, pois este processo pode acabar demorando, seja por lentidão da internet ou outras questões que envolvam uma comunicação externa.

Então, vamos entender como fazer tal implementação para que também não seja necessário nos atentarmos à parte do executeOnExecutor() da Async Task. Chamaremos a call e o método enqueue(), que fará a execução de maneira assíncrona, sem precisarmos de uma Async Task. Ele exige uma interface chamada Callback, que sempre receberá o Generics que temos em nossa Call, englobando um produto.

Trata-se de uma implementação que inclui dois comportamentos: onResponse() e onFailure(). No primeiro, significa que conseguimos nos comunicar com o banco de dados com sucesso, porém não necessariamente o resultado será o esperado. No segundo, tentamos fazer uma comunicação e tivemos um erro; pode ocorrer, por exemplo, de conseguirmos nos comunicar com o servidor, mas ele nos devolver um código de erro do HTTP. Se tentamos nos comunicar, mas isto resultar em um Timeout, também cairemos no onFailure().

Ambos são executados na UI Thread, sendo assim temos o mesmo resultado obtido no onPostExecute(), na interface listener::quandoCarregados. Ou seja, são comportamentos que podemos delegar diretamente à nossa Activity. Então, em onResponse(), teremos o mesmo comportamento de quando pegamos a resposta, isto é, acessar body(), verificando se ele carrega as informações esperadas, e notificar. Não precisamos retornar nada.

public void salva(Produto produto,
        DadosCarregadosListener<Produto> listener) {
    Call<Produto> call = service.salva(produto);
    call.enqueue(new Callback<Produto>() {
        @Override
        public void onResponse(Call<Produto> call,
                                                    Response<Produto> response) {
            Produto produtoSalvo = response.body();
            listener.quandoCarregados(produtoSalvo);
        }

        @Override
        public void onFailure(Call<Produto> call,
                    Throwable t) {
        }
    });
    // código omitido
}COPIAR CÓDIGO
Já que recebemos o produto, podemos optar por salvar internamente e só depois notificarmos. Portanto, substituiremos a linha listener.quandoCarregados(produtoSalvo) pelo seguinte trecho, que recortaremos de onde estava:

new BaseAsyncTask<>(() -> {
    long id = dao.salva(produto);
    return dao.buscaProduto(id);
}, listener::quandoCarregados)
            .execute();COPIAR CÓDIGO
Ele pegará o produtoSalvo proveniente da nossa API, que salvaremos internamente — então, substituiremos produto de dao.salva() do trecho acima por produtoSalvo. Outro detalhe, quando utilizamos o enqueue(), não precisamos chamar o execute() feito na Async Task. Além disso, sua Thread não é própria da Async Task, e sim uma nova Thread, desvinculada, executada em paralelo.

Vamos executar a aplicação para verificar se ela funciona da maneira esperada. Lembrando que, por conta da implementação que fizemos até aqui, não conseguiremos salvar os nossos produtos de maneira offline. E já que não salvamos o produto computador, que criamos anteriormente, iremos removê-lo para criarmos novamente.

A princípio, não temos nenhum problema, e conseguimos rotacionar a tela também, como gostaríamos. Então, iremos remover o computador da lista mais uma vez, e verificaremos se conseguimos acessá-lo, pois se a remoção for feita internamente, deixamos de ter acesso a ele, mas como a remoção só está sendo feita internamente, mas mantemos na API, ele deve permanecer como está ao rotacionarmos a tela.

Feito o teste, constatamos que conseguimos salvar o produto com sucesso. Podemos testar salvar o produto offline mesmo sabendo que isso não irá funcionar. É muito importante testarmos tudo que for colocado no aplicativo, especialmente em se tratando de implementações que têm integração entre a API e a solução externa, ou interna, pois é o ponto mais complexo do aplicativo.

Logo mais identificaremos alguns pontos de melhoria do nosso código.

@@04
Salvando o produto na API

Ajuste o comportamento de salvar do repositório para que primeiro salve o produto na API para depois salvar internamente. Nesta implementação ao invés de usar a AsyncTask, utilize o enqueue().
Lembre-se que as chamadas da interface Callback do Retrofit (onResponse() e onFailure()) são executadas na UI Thread.
Após finalizar, teste o App e veja se ao salvar o produto ele é salvo na API e internamente. O App não deve salvar o produto caso esteja sem conexão com a API.

O comportamento de salvar deve funcionar como o esperado. Você pode conferir o código a partir deste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/00f8ee01a32248d54298ca56c89b4360f87b8f24

@@05
Refatorando o Callback

Feita a primeira implementação do Callback, aproveitaremos para fazermos uma refatoração no nosso código, e destacar alguns pontos de atenção neste tipo de implementação. Começaremos por algumas extrações de código, como é o caso da Async Task que salva o nosso produto internamente, em onResponse() de ProdutoRepository.java.
Para isso, selecionaremos o trecho abaixo:

new BaseAsyncTask<>(() -> {
    long id = dao.salva(produtoSalvo);
    return dao.buscaProduto(id);
}, listener::quandoCarregados)
        .execute();COPIAR CÓDIGO
E o extrairemos para ProdutoRepository, em vez da nossa classe anônima, e o chamaremos de salvaInterno. Podemos inclusive alterar o nome do parâmetro, de produtoSalvo para produto, simplesmente. Deste modo, o código ficará da seguinte forma:

public void onResponse(Call<Produto> call,
                                    Response<Produto> response) {
    Produto produtoSalvo = response.body();
    salvaInterno(produtoSalvo, listener);
}COPIAR CÓDIGO
Também faremos uma extração no comportamento em que acessamos uma Call e fazemos uma enqueue(), em salva() do mesmo arquivo:

Call<Produto> call = service.salva(produto);
call.enqueue(new Callback<Produto>() {
    @Override
    public void onResponse(Call<Produto> call,
                                        Response<Produto> response) {
        Produto produtoSalvo = response.body();
        salvaInterno(produtoSalvo, listener);
    }

    @Override
    public void onFailure(Call<Produto> call,
                                        Throwable t) {
    }
});COPIAR CÓDIGO
Estamos fazendo tudo isso para salvarmos na API. Desta vez, extrairemos para um método chamado salvaNaApi. Essa parte de refatoração é bem simples — ao pedirmos para o ProdutoRepository salvar na API, durante este processo ele chama o enqueue(), que fará a implementação de Callback em onResponse(), e então salvamos internamente e notificamos a Activity.

Sobre a parte de Callback, é importante reforçar que quando fazemos este tipo de implementação, há duas situações: quando temos uma resposta e uma falha, e a princípio estamos assumindo que todas as vezes em que temos uma resposta, tudo está funcionando corretamente, sendo que isso pode não ser verdade.

Todas as vezes em que nos comunicamos com um meio externo, existirão pontos de falha. E quando pegamos uma resposta, o primeiro passo implica em nos certificarmos de que ela está funcionando bem, da maneira esperada. Para isso, utilizaremos nosso parâmetro de resposta, dentro do qual incluiremos um método para verificarmos se ele foi bem sucedido, isSuccessful().

E então, conseguimos tentar fazer a operação que queremos, que é pegar e salvar internamente o conteúdo do body().

public void onResponse(Call<Produto> call,
                                    Response<Produto> response) {
    if(response.isSuccessful()) {
        Produto produtoSalvo = response.body();
        salvaInterno(produtoSalvo, listener);
    }

}COPIAR CÓDIGO
Porém, por mais que tenhamos certeza do sucesso do resultado, perceberemos que, durante a comunicação de outras APIs, o sucesso não trará o que for desejado. Talvez esta resposta traga outro valor, diferente de Produto, ou seja, é necessário verificarmos o conteúdo que pegamos no body() também, antes mesmo de trabalharmos nele.

Caso contrário, pode acontecer de trabalharmos com uma referência nula, mesmo tendo tido sucesso na resposta. Sendo assim, acrescentaremos outro if() para verificar se ele é diferente de nulo, pois, caso seja, aí sim poderemos salvar, já que para este caso específico com que estamos trabalhando na API, temos certeza de que funciona da maneira desejada.

public void onResponse(Call<Produto> call,
                                    Response<Produto> response) {
    if(response.isSuccessful()) {
        Produto produtoSalvo = response.body();
        if(produtoSalvo != null){
            salvaInterno(produtoSalvo, listener);
        }
    }

}COPIAR CÓDIGO
Em muitos casos, esta abordagem fará com que evitemos erros comuns no dia a dia. Continuando, nosso inspetor de código destaca os parâmetros do trecho acima, indicando que eles não estão anotados com @EverythingIsNonNull, isso porque são parâmetros cuja implementação do Retrofit garante que sempre virá uma instância.

Por este motivo, não precisaremos verificar se response é uma referência nula, por exemplo. Assim, temos uma verificação a menos, e chamar o isSuccessful() diretamente já é o suficiente. Entretanto, para desativarmos o alarme do Android Studio, temos duas opções:

colocar a anotação @NonNull antes de Call<Produto>;
dentro dos métodos, no caso, onResponse() ou onFailure(), ou seja, logo após @Override de cada um deles, incluir @EverythingIsNonNull.
Indicando que nenhum dos parâmetros terá referência nula. Neste caso, escolheremos a segunda opção. Por fim, também precisaremos verificar as situações em que há falhas, já que salvamos internamente a resposta de sucesso com um produto que não é nulo, mas não definimos o que acontece quando isso não funciona.

Nosso usuário não tem nenhum tipo de feedback ou notificação para entender o que acontece. Portanto, incluiremos um else para termos a responsabilidade de notificarmos uma falha, caso haja.

public void onResponse(Call<Produto> call,
                                    Response<Produto> response) {
    if(response.isSuccessful()) {
        Produto produtoSalvo = response.body();
        if(produtoSalvo != null){
            salvaInterno(produtoSalvo, listener);
        }
    } else {
        // notificar falha
    }
}COPIAR CÓDIGO
Isto também ocorrerá em onFailure():

@Override
@EverythingIsNonNull
public void onFailure(Call<Produto> call,
                                    Throwable t) {
    // notificar falha
}COPIAR CÓDIGO
Precisamos entender como faremos esta notificação; a princípio estamos trabalhando com um Listener com uma única responsabilidade, que é notificar a nossa Activity quando os dados são carregados. Então, implementaremos um Callback próprio, capaz de notificar quando há resultado e falha.

No fim do código, após a implementação de DadosCarregadosListener, implementaremos uma nova interface com o comportamento do Callback:

public interface DadosCarregadosCallback <T> {
    void quandoSucesso(T resultado);
    void quandoFalha(String erro);
}COPIAR CÓDIGO
Ele receberá um resultado genérico com base na implementação, e indicará quando houver falha a partir do método quandoFalha(). Enviaremos uma mensagem em string, indicando o erro, assim, a Activity poderá explorá-la, mostrá-la ao usuário, ou o que for. Ela será notificada e tomará uma ação.

Feito isso, basta substituirmos nosso Listener, DadosCarregadosListener, por DadosCarregadosCallback, alterando também a chamada listener por callback:

public void salva(Produto produto,
                        DadosCarregadosCallback<Produto> callback) {
    salvaNaApi(produto, callback);
}COPIAR CÓDIGO
Faremos o mesmo em salvaNaApi() e em salvaInterno(). Neste, trocaremos quandoCarregados() por quandoSucesso() para indicarmos o produto que está sendo enviado. E então faremos as notificações: em onResponse(), trocaremos a linha // notificar falha dentro de else por callback.quandoFalha(erro: "Resposta mal sucedida").

Da mesma forma, em onFailure(), a linha que contém // notificar falha ficará com outra mensagem genérica, callback.quandoFalha(erro: "Falha de comunicação: " + t.getMessage()), em que deixaremos a mensagem do Throwable concatenada com a string.

Em seguida, iremos a ListaProdutosActivity.java para modificarmos o conceito de Listener, que está desatualizado. Faremos a implementação da interface ProdutoRepository.DadosCarregadosCallback. E em quandoSucesso() e quandoFalha() teremos:

@Override
public void quandoSucesso(Produto produtoSalvo) {
    adapter.adiciona(produtoSalvo);
}

@Override
public void quandoFalha(String erro) {
    Toast.makeText(context: ListaProdutosActivity.this,
            text: "Não foi possível salvar o produto",
            Toast.LENGTH_SHORT).show();
}COPIAR CÓDIGO
Feitas estas modificações, o próximo passo consiste em executarmos a aplicação para testar seu funcionamento. Adicionaremos novos produtos, como um celular de 1200 reais, e quantidade de 20. Sem internet, é exibida a mensagem de erro, portanto conseguimos notificar o usuário como gostaríamos.

Esta abordagem é muito flexível, pois permite que façamos toda a estratégia de salvarmos na API antes de salvarmos internamente, como também de notificação dos problemas, comuns em qualquer tipo de implementação que envolva o Callback ou meios externos, qualquer situação na qual temos risco de falhas. É importante estarmos preparados para lidarmos com isso.

@@06
Refatorando o código do callback

Refatore o código de callback do comportamento de salvar o produto.
Além de técnicas como extração, também aplique os cuidados recomendados para esse tipo de implementação, como a verificação de sucesso da resposta.

Após finalizar, teste o App e veja se tudo funciona como antes e se os novos comportamentos são apresentados como esperado.

O produto deve ser salvo apenas quando tiver acesso à API, quando não tiver acesso deve apresentar uma mensagem de feedback para o usuário. Você pode conferir o código da atividade a partir do commit.

https://github.com/alura-cursos/android-persistencia-web/commit/14ff1f1f96bc65db5699094fb8e7e21f2803d20e

@@07
Para saber mais - Mais cuidados com a resposta do Retrofit

Além de verificar se a resposta foi sucedida, existem outros detalhes que podemos nos atentar ao utilizar o Retrofit.
Neste artigo (em Inglês), são apresentadas técnicas avançadas de tratamento de erro de rede utilizando o Retrofit.

https://medium.com/@tsaha.cse/advanced-retrofit2-part-1-network-error-handling-response-caching-77483cf68620

@@08
O que aprendemos?

Nesta aula, aprendemos a:
Implementar requisições utilizando a execução assíncrona da Call;
Peculiaridades da execução do Callback;
Flexibilizar possibilidades do Callback para a Activity.

#### 29/09/2023

@05-Integrando comportamento de edição

@@01
Utilizando Callback na busca de produtos

Vamos modificar agora o comportamento de busca de produtos para que ele utilize o conceito de Callback. Para isso removeremos a seguinte interface, pois trabalharemos apenas com o Callback:
public interface DadosCarregadosListener <T> {
    void quandoCarregados(T resultado);
}COPIAR CÓDIGO
Isso fará com que alguns pontos deixem de compilar, portanto substituiremos DadosCarregadosListener por DadosCarregadosCallback em buscaProdutos(), alterando a referência listener para callback. Faremos o mesmo nos demais locais usando um atalho do próprio Android Studio.

Em buscaProdutosInternos(), precisaremos altrerar listener para callback e fazer uma modificação de chamada na busca interna, de quandoCarregados() para quandoSucesso(). Modificaremos também a busca na API, alterando o primeiro parâmetro para callback, trocando listener por callback em buscaProdutosNaApi().

Teremos que fazer algumas modificações a mais, porque além de notificar quando há bom funcionamento, precisaremos notificar quando há falhas, e para facilitar tudo isso, podemos utilizar o enqueue(),já que esta execução que estamos fazendo via Async Task é justamente para obtermos o comportamento padrão do enqueue(), que é fazer uma execução e uma Thread separada.

Assim sendo, manteremos o BaseAsyncTask como está, e faremos uma implementação de um callback acima para obtermos o resultado esperado. E então removeremos a linha List<Produto> produtosNovos = resposta.body() e a colaremos dentro do if() de onResponse(), trocando resposta por response.

Acrescentaremos outro if() para verificar se os produtos novos são de referência não nula, isto é, válida. Para isso, copiaremos dao.salva(produtosNovos). Após salvarmos o produto, retornamos buscaTodos() e então notificamos que ele poderá ser carregado.

Quando estávamos usando o Async Task, retornávamos o conteúdo dao.buscaTodos() para que ele ficasse disponível no Listener que faz a notificação (listener::quandoCarregados). Sendo assim, pegaremos o nosso callback, chamaremos quandoSucesso(), dentro do qual buscaremos todos os produtos.

call.enqueue(new Callback<List<Produto>>() {
    @Override
    @EverythingIsNonNull
    public void onResponse(Call<List<Produto>> call,
                                Response<List<Produto>> response) {
        if(response.isSuccessful()){
            List<Produto> produtosNovos = response.body();
            if(produtosNovos != null) {
                dao.salva(produtosNovos);
                callback.quandoSucesso(dao.buscaTodos());
            }
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<List<Produto>> call,
                                Throwable t) {

    }
});COPIAR CÓDIGO
É o mesmo comportamento feito na Async Task, de forma mais resumida, podemos inclusive deletar todo o trecho abaixo:

new BaseAsyncTask<>(() -> {
    try {
        Response<List<Produto>> resposta = call.execute();
        List<Produto> produtosNovos = resposta.body();
        dao.salva(produtosNovos);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return dao.buscaTodos();
}, listener::quandoCarregados)
        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);COPIAR CÓDIGO
Agora, precisamos incluir os pontos de falha, conforme a proposta do nosso Callback. A resposta será bastante genérica, e quem for modificar alguma resposta para o nosso usuário, em específico, será justamente a nossa Activity, que trabalhará com a parte visual.

call.enqueue(new Callback<List<Produto>>() {
    @Override
    @EverythingIsNonNull
    public void onResponse(Call<List<Produto>> call,
                                Response<List<Produto>> response) {
        if(response.isSuccessful()){
            List<Produto> produtosNovos = response.body();
            if(produtosNovos != null) {
                dao.salva(produtosNovos);
                callback.quandoSucesso(dao.buscaTodos());
            }
        } else {
            callback.quandoFalha(erro: "Resposta mal sucedida");
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<List<Produto>> call,
                                Throwable t) {
        callback.quandoFalha(erro: "Falha de comunicação: " + t.getMessage());
    }
});COPIAR CÓDIGO
O que muda é que, na Activity, passaremos a fazer uma implementação da nova interface de Callback no lugar de adapter::atualiza no trecho abaixo, que terá quandoSucesso() e quandoFalha() também. Em seguida, testaremos nosso aplicativo no emulador, no caso de não termos acesso à internet, pois não modificamos nada quando há sucesso, e sim quando há falha.

Apesar de comentado anteriormente que não precisamos mais das Async Tasks, isto não é verdade, pois executamos o onResponse() na Main Thread, e portanto precisamos fazer com que estas operações do nosso banco de dados sejam feitas em uma Async Task.

public void onResponse(Call<List<Produto>> call,
                            Response<List<Produto>> response) {
    if(response.isSuccessful()){
        List<Produto> produtosNovos = response.body();
        if(produtosNovos != null) {
            new BaseAsyncTask<>(() -> {
                dao.salva(produtosNovos);
                return dao.buscaTodos();
            }, callback::quandoSucesso)
                    .execute();
        }
    } else {
        callback.quandoFalha(erro: "Resposta mal sucedida");
    }
}COPIAR CÓDIGO
Em seguida, poderemos extrair o seguinte trecho:

new BaseAsyncTask<>(() -> {
    dao.salva(produtosNovos);
    return dao.buscaTodos();
}, callback::quandoSucesso)
        .execute();COPIAR CÓDIGO
Para indicar que estamos atualizando os produtos. O nome do método será atualizaInterno, com base nos produtos novos, cujo nome poderá ser simplesmente produtos:

if(produtosNovos != null) {
    atualizaInterno(produtosNovos, callback);
}COPIAR CÓDIGO
Agora, sim, faremos a execução esperada — atualizar internamente, salvando os produtos, retornando-os e notificando o usuário com base no Callback. Então, é muito importante que todas as vezes em que executamos algo em onResponse() ou onFailure(), a execução acontece na UI Thread. Assim, levando em consideração o que vimos sobre paralelismo, é melhor evitar execuções que podem quebrar, ou travar.

Testaremos no emulador rotacionando a tela no modo avião, e obteremos a mensagem de erro esperada, removeremos um dos produtos, que existe tanto interna quanto externamente, e eles não serão carregados corretamente, porém a notificação do erro surge conforme desejado. Com a internet, a rotação da tela já funciona sem nenhum problema.

Por mais que tenhamos feito esta adaptação, criamos nosso Callback em buscaProdutosNaApi() de forma bastante similar ao de salvaNaApi(). Então, antes mesmo de fazermos as próximas implementações relacionadas à edição e remoção de produtos, tornaremos estas implementações de Callback genéricas o suficiente, para que possam ser reutilizadas.

@@02
Utilizando o enqueue na busca de produtos

Caso você precise do projeto com todas as alterações realizadas na aula passada, você pode baixá-lo neste link.
Converta o comportamento de busca de produtos para que utilize o Callback do Retrofit.

Nesta conversão, lembre-se de exigir o Callback para que seja possível notificar o usuário sobre possíveis falhas.

Após migração teste o App e veja se funciona como esperado. Lembre-se de simular a situação que não tem internet para conferir o feedback que o usuário vai receber.

https://github.com/alura-cursos/android-persistencia-web/archive/aula-4.zip

O comportamento de busca deve funcionar como antes, a diferença é que ao perder conexão com a internet deve apresentar a mensagem de falha de busca. O código da atividade pode ser conferido a partir deste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/e6c149bf4a1d2299bda7a5478c7d03e6b428315b

@@03
Implementando Callback genérico

Como vimos, temos implementações de Callbacks que são bem similares entre si. De que maneira podemos trabalhar com uma única implementação, a ser reutilizada?
Assim como fizemos em BaseAsyncTask(), criaremos uma referência genérica o suficiente para que seja reutilizada em qualquer situação, evitando repetições desnecessárias de código. Para isto, em "app > java > br.com.alura.estoque > retrofit" criaremos o pacote "callback", em que serão implementados outros, caso necessário.

Nele, criaremos a interface BaseCallback, que precisará receber um tipo genérico, e implementará a Callback do Retrofit, além dos métodos onResponse() e onFailure(). Incluiremos as mesmas chamadas de ProdutoRepository.java, comuns à qualquer uma das implementações, no caso, verificar a resposta bem sucedida, se o conteúdo é diferente de nulo, para então passar à ação desejada.

public class BaseCallback <T> implements Callback<T> {
    @Override
    @EverythingIsNonNull
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.isSuccessful()) {
            T resultado = response.body();
            if(resultado != null){
                // notifica que tem resposta com sucesso
            }
        } else {
            // notifica falha
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<T> call, Throwable t) {
        // notifica falha
    }
}COPIAR CÓDIGO
Feito isso, precisaremos incluir um Callback específico para notificar e fazer uso desta implementação genérica, logo após o código referente onFailure():

public interface RespostaCallback <T> {
    void quandoSucesso(T resultado);
    void quandoFalha(String erro);
}COPIAR CÓDIGO
E então poderemos utilizar a mesma técnica de BaseAsyncTask.java para que isto seja um atributo de classe que exija tal implementação. Na interface que acabamos de implementar, logo antes de @Override, digitaremos:

private final RespostaCallback<T> callback;

public BaseCallback(RespostaCallback<T> callback) {
    this.callback = callback;
}COPIAR CÓDIGO
Agora que temos acesso ao nosso callback via construtor, incluiremos as notificações conforme esperado:

public class BaseCallback <T> implements Callback<T> {
    @Override
    @EverythingIsNonNull
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.isSuccessful()) {
            T resultado = response.body();
            if(resultado != null){
                callback.quandoSucesso(resultado);
            }
        } else {
            callback.quandoFalha(erro: "Resposta mal sucedida");
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<T> call, Throwable t) {
        callback.quandoFalha(erro: "Falha de comunicação: " + t.getMessage());
    }
}COPIAR CÓDIGO
Vamos fazer a reutilização do código?

Voltaremos a ProdutoRepository.java e começaremos pelo comportamento de salvar, que é onde fizemos as primeiras modificações. Faremos uma instância de BaseCallback, e então precisaremos implementar apenas a interface com quandoSucesso() e quandoFalha(). Assim, não precisaremos mais identificar pontos de falha ou sucesso, mensagens e afins.

Como a parte de falha será feita automaticamente, incluiremos apenas o que fazíamos quando obtínhamos a resposta, que é chamar o método internamente, com salvaInterno(produtoSalvo, callback). E em quandoFalha() pegamos nosso callback e o erro.

@Override
public void quandoSucesso(Produto produtoSalvo) {
    salvaInterno(produtoSalvo, callback);
}

@Override
public void quandoFalha(String erro) {
    callback.quandoFalha(erro);
}COPIAR CÓDIGO
Isso fará com que o repositório tenha que lidar com as operações de maneira mais simples. Vamos testar a aplicação e, se tudo estiver funcionando de acordo, passamos ao comportamento de buscar produtos (buscaProdutosNaApi), em que aplicaremos o mesmo procedimento.

@Override
public void quandoSucesso(List<Produto> predutosNovos) {
    atualizaInterno(produtosNovos, callback);
}

@Override
public void quandoFalha(String erro) {
    callback.quandoFalha(erro);
}COPIAR CÓDIGO
Feita esta alteração, testaremos a aplicação mais uma vez, que não apresenta nenhum problema. A nossa implementação está bem mais sucinta. Prosseguiremos às próximas implementações em relação à edição e remoção de produtos mais adiante.

@@04
Criando o callback genérico

Implemente o Callback genérico com os comportamentos comuns entre os comportamentos de busca e inserção de produtos.
Após implementar, utilize o Callback genérico nas chamadas de enqueue() de ambos os comportamentos, teste o App e veja se funciona como esperado.

O App deve apresentar os mesmos comportamentos, a diferença é que agora o código fica mais enxuto. O código desta atividade pode ser conferido neste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/7b51b26a2cacc49560fff04d26d778ee44169d74

@@05
Integrando edição com a API

Para integrar os comportamentos de edição com a nossa API, utilizaremos a mesma abordagem feita na busca e inserção de produtos. Sendo assim, migraremos o comportamento de edita() para o nosso repositório, ProdutoRepository.java, antes do trecho referente a DadosCarregadosCallback e vamos adaptando conforme a necessidade, como tornando-o público.
Também receberemos o nosso Callback como parâmetro e, em vez do Adapter, notificarmos a Activity, e para isto podemos utilizar um Method reference:

public void edita(Produto produto,
                    DadosCarregadosCallback<Produto> callback) {
    new BaseAsyncTask<>(() -> {
        dao.atualiza(produto);
        return produto;
    }, callback::quandoSucesso)
            .execute();
}COPIAR CÓDIGO
Deste modo, identificamos que posicao deixa de ser necessária, portanto a removeremos. Em ListaProdutosActivity.java utilizaremos o repository para chamar edita(), não enviaremos a posicao, implementaremos a interface ProdutoRepository.DadosCarregadosCallback, indicaremos que editaremos nosso Adapter quando recebermos o produto.

Em seguida, quando houver falha, notificaremos o usuário de que não foi possível fazer a edição. Fiquemos atentos aos nomes resultado em quandoSucesso(), quando na verdade estamos trabalhando com um produto editado, e em produtoEditado teremos um produto criado, não editado. E resultado será produtoEditado.

private void abreFormularioEditaProduto(int posicao, Produto produto) {
    new EditaProdutoDialog(context: this, produto,
            produtoCriado -> repository.edita(produtoCriado,
                    new ProdutoRepository.DadosCarregadosCallback<Produto>() {
                @Override
                public void quandoSucesso(Produto resultado) {
                    adapter.edita(posicao, produtoEditado);
                }

                @Override
                public void quandoFalha(String erro) {
                    Toast.makeTest(context: ListaProdutosActivity.this,
                            text: "Não foi possível editar o produto",
                            Toast.LENGTH_SHORT).show();
                }
            }));
}COPIAR CÓDIGO
O próximo passo consiste em criar a requisição em edita(), com service.edita() e todos os argumentos necessários. Entretanto, antes disso, é muito importante garantirmos que a edição interna ainda esteja funcionando, pois se começarmos a fazer a implementação, de repente podemos descobrir um problema interno e isso o tornará sua resolução mais difícil.

Vamos, então, testar a aplicação e verificar seu funcionamento interno. Editaremos o primeiro produto, que ainda não foi enviado à nossa API, e confirmaremos que tudo acontece conforme esperado. Lembrando que tudo que for feito e salvo internamente só será efetivado após a sua integração com a API.

Como vimos no Postman, será necessário um ID, pois o envio terá que ser feito via endereço, e isso pode variar conforme o produto, e em seguida enviaremos o produto a ser editado. Uma técnica que agiliza a implementação é incluir Call<Produto> call = antes de service.edita(), para a inicialização. Assim, o método é criado automaticamente com a Call como resposta.

public void edita(Produto produto,
                    DadosCarregadosCallback<Produto> callback) {

    Call<Produto> call = service.edita(produto.getId(), produto);

    new BaseAsyncTask<>(() -> {
        dao.atualiza(produto);
        return produto;
    }, callback::quandoSucesso)
            .execute();
}COPIAR CÓDIGO
Para a requisição, em ProdutoService.java, incluímos o verbo PUT, teremos uma anotação, e deixamos o endereço produto/ vinculando o ID correspondente a cada produto, com o uso das chaves e um nome de identificador que acharmos melhor. E em nossos parâmetros anotaremos o valor que queremos que entre no endereço, com @Path, o qual exige um identificador contido na URL.

public interface ProdutoService {

    @GET("produto")
    Call<List<Produto>> buscaTodos();

    @POST("produto")
    Call<Produto> salva(@Body Produto produto);

    @PUT("produto/{id}")
    Call<Produto> edita(@Path("id") long id,
                            @Body Produto produto);
}COPIAR CÓDIGO
Em que {id} será computado, inicializado, com o valor de ID colocado no @Path, que neste caso é do long. Criada a requisição, basta implementarmos a call em edita(), no enqueue(), que não nos traz nenhuma novidade: verificaremos a resposta, teremos quandoSucesso() e quandoFalha(), e assim por diante.

No caso, antes mesmo da notificação de sucesso, sabemos que precisamos salvar o produto internamente, e para isso recortaremos o trecho de BaseAsyncTask localizado abaixo do enqueue() e o colaremos dentro de quandoSucesso(). E em quandoFalha(), precisamos apenas incluir a notificação do Callback.

@Override
public void quandoSucesso(Produto resultado) {
    new BaseAsyncTask<>(() -> {
        dao.atualiza(produto);
        return produto;
    }, callback::quandoSucesso)
            .execute();
}

@Override
public void quandoFalha(String erro) {
    callback.quandoFalha(erro);
}COPIAR CÓDIGO
Antes mesmo de testarmos, podemos refatorar para ganhar tempo, deixando o código da seguinte forma:

@Override
public void quandoSucesso(Produto resultado) {
    editaInterno(produto, callback);
}COPIAR CÓDIGO
Do mesmo modo, extrairemos a nossa Call para o método editaNaApi. Assim, em edita() solicitamos que a edição seja feita na API, em editaNaApi() fazemos o enqueue() com base na requisição de edição, e quando houver sucesso editamos internamente. E em editaInterno() a notificação é realizada.

Testaremos a aplicação no emulador para verificar se ela funciona conforme esperado, no caso, se ela exibe a mensagem de erro quando tentamos fazer a edição em modo avião. Em seguida, habilitaremos a internet, rotacionaremos a tela e faremos a mesma edição. Obteremos uma mensagem de erro, portanto consultaremos o Logcat para entender o que houve.

Teremos que a requisição foi realizada, porém ainda não temos o produto específico em nossa API, que somente conseguirá editar algo que efetivamente exista. Então, como a API foi implementada, não teremos capacidade de editar este produto. A abordagem que fica como sugestão, neste caso, será remover este produto salvo internamente, e editar outro produto.

Então, não conseguimos editar um dos produtos por conta de uma peculiaridade da API, uma vez que tínhamos os valores correspondentes a ele apenas internamente. Estamos usando o ID como base, um dos problemas do universo das comunicações externas, e é por isto que não conseguimos fazer esta edição.

Não se trata de um problema no aplicativo, ou algo do tipo, e sim de como a API foi implementada. Ela poderia criar um produto com ID 1, no caso, em sua estratégia, ela não cria outro produto com base no ID recebido, e sim naqueles que ela controla. Se existem 10 produtos criados, ela irá lidar com produtos a partir do ID 11.

A seguir veremos a parte de remoção dos produtos!

@@06
Editando produto internamente e na API

Integre o comportamento de edição com a API. Para isso, primeiro migre a edição interna para o repositório, em seguida, implemente a requisição para edição e a execute.
Mantenha a mesma estratégia da inserção, primeiro edita na API e se tudo der certo, internamente. Reutilize as mesmas técnicas, principalmente o Callback genérico.

Ao finalizar a implementação, teste o App e veja se funciona conforme estratégia esperada.

O App deve editar o produto apenas quando tiver comunicação sucedida com a API. O código desta atividade pode ser conferido neste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/24f04247228b939e57d0565507f83a0854f21ce5

@@07
Para saber mais - Variações nas requisições

Durante o curso, criamos requisições explorando o conteúdo do corpo (body) e caminho (path) da URL.
Mas é possível ir mais além, definindo headers e query parameters, entre outras possibilidades.

Caso tenha necessidade de abordagens diferentes, consulte a documentação e veja os exemplos.

https://square.github.io/retrofit/

@@08
O que aprendemos?

Nesta aula, aprendemos a:
Criar Callbacks genéricos;
Implementar requisições que recebem valores variáveis via URL.

#### 01/10/2023

@06-Integrando comportamento de remoção

@@01
Migrando comportamento de remoção

Para migrar o comportamento de remoção de produtos integrando-o com a nossa API, faremos o mesmo procedimento da edição — recortaremos o trecho referente a remove() de ListaProdutosActivity.java, e o colaremos em nosso repositório, ProdutoRepository.java, pois ele irá manter este comportamento.
Em seguida, aplicaremos os mesmos ajustes feitos anteriormente, deixando este método público, e verificando os parâmetros e as chamadas internas para entendermos o que deve ser mantido. A posicao, por exemplo, é utilizada somente para atualizar o adapter, e remover o produto, e não é necessário, uma vez que a remoção será feita pela Activity.

Da mesma maneira que na edição, produtoRemovido será substituído por produto, e na parte em que fazemos a notificação, precisamos ajustar para que ela seja feita via Callback, e não pelo Adapter. Em se tratando de seu tipo de retorno, como vimos no Postman durante os testes de API, não retornamos nada quando removemos algo da API. Portanto não podemos devolver um produto ou uma lista deles.

Em situações em que fizermos requisições sem retorno no body(), podemos usar como referência o Void, como fazemos na Async Task. Dado que enviamos uma referência nula, e que já indicamos que teremos um Void, para a notificação, utilizaremos o Method reference.

public void remove(Produto produto,
                    DadosCarregadosCallback<Void> callback) {
    new BaseAsyncTask<>(() -> {
        dao.remove(produto);
        return null;
    }, callback::quandoSucesso)
            .execute();
}COPIAR CÓDIGO
Continuando, em ListaProdutosActivity.java precisaremos adaptar nosso código para que, no momento em que um produto é removido, se faça a implementação adequada com o nosso repositório, no Listener do adapter do Recycler View. Daí, se verifica se o menu de contexto que possui o item de remoção foi acionado.

Ele nos obriga a fazermos a implementação de uma interface que tem justamente o objetivo de enviar a posição e o produto escolhido para remoção. Assim, podemos utilizar a expressão lambda, e apesar de estar produtoRemovido, estaremos lidando com um produto escolhido, que ainda não foi removido, portanto iremos renomeá-lo.

listaProdutos.setAdapter(adapter);
adapter.setOnItemClickRemoveContextMenuListener(
        (posicao, produtoEscolhido) -> );COPIAR CÓDIGO
Esta alteração de produtoRemovido para produtoEscolhido pode ser feita na fonte do código, caso se prefira, o que não será feito neste momento por seguirmos o fluxo do projeto.
Precisaremos trabalhar em cima de nosso repositório, e implementar o Callback:

listaProdutos.setAdapter(adapter);
adapter.setOnItemClickRemoveContextMenuListener(
        (posicao, produtoEscolhido) -> {
                repository.remove(produtoEscolhido,
                        new ProdutoRepository.DadosCarregadosCallback<Void>() {
                            @Override
                            public void quandoSucesso(Void resultado) {
                                adapter.remove(posicao);
                            }

                            @Override
                            public void quandoFalha(String erro) {
                                Toast.makeText(context: ListaProdutosActivity.this,
                                        text: "Não foi possível remover o produto",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
});COPIAR CÓDIGO
Feita esta primeira migração, testaremos a remoção interna de um produto, após o qual faremos a integração com a API. Assim, ao removermos um produto e em seguida rotacionarmos a tela, teremos que a remoção não foi realizada, conforme esperado. Da mesma forma como na edição, criaremos a requisição para a remoção em ProdutoRepository.java.

Para a requisição precisamos acessar o ID do produto, mas neste caso não basta pedirmos apenas o ID para realizarmos a remoção, pois ainda precisamos do nosso produto internamente, a não ser que fizéssemos uma modificação em nosso DAO de forma a criarmos uma query que só removesse com ID. E para facilitar nosso trabalho, faremos a inicialização de Call para que se crie um método com seu retorno.

O método será criado, e então podemos adicionar @Path, como fizemos na edição, juntamente à requisição de remoção, @DELETE:

@DELETE("produto/{id}")
Call<Void> remove(@Path("id") long id);COPIAR CÓDIGO
Implementaremos também o BaseCallback, a partir do qual, quando bem sucedido, teremos o comportamento de BaseAsyncTask, que extrairemos para removeInterno. Por fim, a chamada da Call será extraída para removeNaApi.

public void remove(Produto produto,
                    DadosCarregadosCallback<Void> callback) {

    removenaApi(produto, callback);
}COPIAR CÓDIGO
Sabemos que só conseguiremos remover um produto quando isto ocorrer na API, e quando obtivermos uma resposta de sucesso, removemos internamente. Ao testarmos a aplicação e consultarmos o Logcat, teremos que o Retrofit faz a requisição, mas o produto em questão não é removido no emulador. Precisamos entender o porquê disso.

@@02
Adicionando a remoção no repositório

Caso você precise do projeto com todas as alterações realizadas na aula passada, você pode baixá-lo neste link.
Migre o comportamento de remoção para o repositório. Em seguida, implemente a requisição e faça a mesma estratégia feita para a edição.

Teste o App e veja se a remoção acontece na API.

A remoção interna com essa integração será resolvida a seguir.

https://github.com/alura-cursos/android-persistencia-web/archive/aula-5.zip

A remoção na API deve funcionar, mas ainda será apresentado o produto na lista de produtos. O código desta atividade pode ser conferido a partir deste commit.

@@03
Criando Callback sem retorno

Para entendermos o motivo do problema, precisamos verificar a implementação que fizemos para o Callback que estamos reutilizando para cada uma das chamadas com enqueue(). Se acessarmos a implementação via "Ctrl + B", percebemos que em onResponse() consideramos que, ao sucesso da resposta, e ao conseguirmos o conteúdo, uma referência que não seja nula, aí sim chamamos quandoSucesso().
Este é realmente um ponto de falha — estas requisições em que não existe nenhum tipo de retorno no conteúdo do body(). O @DELETE não devolve nenhum tipo de objeto que podemos consumir, e é por isso que temos uma referência nula e não recebemos a notificação de sucesso. Então, o que podemos fazer para flexibilizar esta solução?

Além desta abordagem que estamos utilizando atualmente, podemos implementar outra, a ser reutilizada quando não tivermos resultado. Para tal, criaremos outro Callback específico, no pacote correspondente, com a única diferença de que trabalharemos apenas em cima da resposta de sucesso que teremos.

A nova classe será chamada de CallbackSemRetorno, e lidaremos com um Callback de tipo Void. A implementação nos será familiar:

public class CallbackSemRetorno implements Callback<Void> {
    @Override
    @EverythingIsNonNull
    public void onResponse(Call<Void> call,
                                    Response<Void> response) {
        if(response.isSuccessful()) {
            callback.quandoSucesso();
        } else {
            callback.quandoFalha("Resposta mal sucedida");
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<Void> call,
                                    Throwable t) {
        callback.quandoFalha("Falha de comunicação: " + t.getMessage());
    }
}COPIAR CÓDIGO
Desta vez, ficaremos muito mais fixos ao Void, isto é, não daremos oportunidade para a inclusão do tipo de retorno. E implementaremos algo bastante similar ao que fizemos em RepostaCallback de BaseCallback.java:

public interface RespostaCallback {
    void quandoSucesso();
    void quandoFalha(String erro);
}COPIAR CÓDIGO
A interface que utilizaremos não será genérica, e sim restrita ao tipo que queremos. Assim, logo no início da implementação da classe CallbackSemRetorno, acrescentaremos:

private final RespostaCallback callback;

public CallbackSemRetorno(RespostaCallback callback) {
    this.callback = callback;
}COPIAR CÓDIGO
Agora, sim, podemos fazer a implementação do novo Callback em removeNaApi() de ProdutoRepository.java, em que, no lugar de BaseCallback utilizaremos CallbackSemRetorno. Além disso, removeremos o tipo genérico de RespostaCallback, e modificaremos as assinaturas. Em quandoSucesso(), não receberemos mais nada, portanto nada será mantido entre parênteses.

Na parte de quandoFalha(), tudo se mantêm como estava antes, inclusive incluiremos a notificação de falha, que não havíamos implementado anteriormente:

@Override
public void quandoSucesso() {
    removeInterno(produto, callback);
}

@Override
public void quandoFalha(String erro) {
    callback.quandoFalha(erro);
}COPIAR CÓDIGO
Para tornar mais explícito o que o BaseCallback faz, podemos alterá-lo para CallbackComRetorno. Agora que temos dois Callbacks distintos, e estamos trabalhando com eles, verificaremos seus funcionamentos internamente, pois se tentarmos remover mais um produto que já foi removido anteriormente, e ele trouxer outra resposta que não o sucesso, não conseguiríamos fazê-la internamente.

Nesta API ainda manteremos a resposta 200, portanto este é um ponto de atenção. Se de repente tivéssemos outro código de sucesso em onResponse(), com uma resposta mal sucedida, cairíamos no else e teríamos que fazer outro tipo de tratamento.

Vamos testar a remoção dos produtos no emulador?

Será muito comum trabalharmos com requisições que não retornam, ou retornam algo, e caso se queira flexibilizar soluções por meio da criação de Callbacks que sejam para reutilização, será necessário lidar com estes casos diferentes. Existem várias maneiras de fazer esta flexibilização, esta não é a única.

@@04
Implementando callback sem retorno

Implemente o callback genérico sem retorno. Substitua o callback da requisição de remoção para que utilize o callback sem retorno.
Teste o App e veja se agora a remoção na API e interna funcionam como esperado.

O comportamento de remoção deve funcionar como esperado. O código da atividade pode ser conferido neste commit.

https://github.com/alura-cursos/android-persistencia-web/commit/7900fd3246110ee4f27af15a967f73b3a210a735

@@05
Refatoração do projeto

Feita a integração do aplicativo com a nossa API, aplicaremos a refatoração em nosso código, a fim de finalizarmos o projeto. Começaremos com ListaProdutosActivity.java, que de imediato perceberemos que existe um atributo DAO inutilizado por membros da Activity, pois todos os comportamentos referentes à inserção, edição, remoção e até mesmo a listagem, estão sendo feitos em ProdutoRepository(), classe que criamos para lidar com esta fonte de dados, seja na API ou internamente.
Deste modo, poderemos deletar a linha private ProdutoDAO dao, e dado que não utilizamos mais este banco de dados na Activity, toda a inicialização pode ser enviada diretamente ao nosso ProdutoRepository, no momento em que construímos esta classe. É preciso fazer algumas adaptações: não receberemos mais uma referência de DAO, mantendo-se apenas a referência necessária para instanciarmos o banco de dados do nosso Room, de contexto, e indicaremos que o contexto será recebido via parâmetro:

public ProdutoRepository(Context context) {
    EstoqueDatabase db = EstoqueDatabase.getInstance(context);
    dao = db.getProdutoDAO();
    service = new EstoqueRetrofit().getProdutoService();
}COPIAR CÓDIGO
Em ListaProdutosActivity.java, substituiremos dao do ProdutoRepository() recebido por repository por this. Em seguida, faremos algumas extrações para deixar o código mais sucinto, como no trecho referente a repository.buscaProdutos(), para um método de mesmo nome. Da mesma forma, extrairemos as mensagens enviadas via Toast; a que se encontra em quandoFalha() ficará no método mostraErro().

O Android Studio identificará um padrão em chamadas contidas na Activity, cuja diferença está apenas no conteúdo das mensagens. Em vez de extrairmos a string no método, ela virará um parâmetro e poderá ser reutilizada para outras chamadas. Aceitaremos a sugestão do programa para substituirmos os pontos identificados por ele.

O único ponto que pode ser um pouco desagradável nesta abordagem é que ele deixa o nome do parâmetro como s, sem nenhum contexto ou significado. Neste caso, trocaremos por mensagem, e enviaremos o this diretamente, pois não estamos em uma referência de classe anônima.

private void mostraErro(String mensagem) {
    Toast.makeText(context: this, mensagem,
            Toast.LENGTH_SHORT).show();
}COPIAR CÓDIGO
Caso futuramente queiramos alterar a abordagem de envio das mensagens, basta fazermos a modificação internamente. Há outras mensagens envidas que exigem uma interpretação do que está hardcoded. Podemos extraí-las para algumas constantes com "Ctrl + Alt + C":

@Override
public void quandoFalha(String erro) {
    mostraErro(MENSAGEM_ERRO_BUSCA_PRODUTOS);
}COPIAR CÓDIGO
Faremos o mesmo no momento de remoção de produtos em configuraListaProdutos():

@Override
public void quandoFalha(String erro) {
    mostraErro(MENSAGEM_ERRO_REMOCAO);
}COPIAR CÓDIGO
E em abreFormularioSalvaProduto():

@Override
public void quandoFalha(String erro) {
    mostraErro(MENSAGEM_ERRO_SALVA);
}COPIAR CÓDIGO
E também em abreFormularioEditaProduto():

@Override
public void quandoFalha(String erro) {
    mostraErro(MENSAGEM_ERRO_EDICAO);
}COPIAR CÓDIGO
Na parte superior do código, alteraremos as constantes que acabamos de criar para que fiquem privadas, e não públicas. E para otimizarmos importes, podemos usar "Ctrl + Alt + O". Depois, podemos extrair a ação de remover do repository de configuraListaProdutos() para o método remove(), inclusive utilizando a técnica do Method reference.

adapter.setOnItemClickRemoveContextMenuListener(this::remove);COPIAR CÓDIGO
Faremos o mesmo com a ação de salvar do repository de abreFormularioSalvaProduto(), que será extraída para o método salva():

private void abreFormularioSalvaProduto() {
    new SalvaProdutoDialog(context: this, this::salva)
            .mostra();
}COPIAR CÓDIGO
E na ação de salvar, do repository de abreFormularioEditaProduto(), para o método edita(), que não ficará com Method reference por enviarmos a posicao, que neste caso não recebe via Listener de nosso Dialog.

private void abreFormularioEditaProduto(int posicao, Produto produto) {
    new EditaProdutoDialog(context: this, produto,
            produtoCriado -> edita(posicao, produtoCriado))
            .mostra();
}COPIAR CÓDIGO
Passando ao ProdutoRepository.java, aplicaremos técnicas similares de refatoração, exceto pela parte de Callback e execução, em que não há muito o que fazer, não há nada hardcoded. Poderíamos tirar as linhas em branco, ou fazer extrações para outras classes para não deixarmos todo esse código em nosso repositório, de acordo com o que fazem, como no caso de chamadas na API, por exemplo. Isso ajudaria a "enxugar" linhas de código.

E em CallbackComRetorno.java extrairemos algumas mensagens para constantes, como no caso do else de onResponse(), que terá o conteúdo callback.quandoFalha(MENSAGEM_ERRO_RESPOSTA_NAO_SUCEDIDA). Da mesma forma, alteraremos o onFailure():

public void onFailure(Call<T> call, Throwable t) {
    callback.quandoFalha(erro: MENSAGEM_ERRO_FALHA_COMUNICACAO + t.getMessage());
}COPIAR CÓDIGO
Inclusive, como estamos usando esta mesma mensagem de falha no Callback com retorno e sem, podemos deixá-las em uma interface default, denominada MensagemCallback, e então iremos reutilizá-las. Recortaremos as constantes, que ficarão públicas mesmo, ou então com acesso restrito ao pacote, e as reutilizamos em cada um dos Callbacks criados:

interface MensagensCallback {
        String MENSAGEM_ERRO_RESPOSTA_NAO_SUCEDIDA = "Resposta mal sucedida";
        String MENSAGEM_ERRO_FALHA_COMUNICACAO = "Falha de comunicação";
}COPIAR CÓDIGO
Com isso, basta realizarmos os imports correspondentes em CallbackComRetorno.java. E em CallbackSemRetorno.java faremos o mesmo — em onFailure() usaremos MENSAGEM_ERRO_FALHA_COMUNICACAO, e no else da mensagem de erro, MENSAGEM_ERRO_RESPOSTA_NAO_SUCEDIDA. Outra classe em que podemos fazer uma refatoração é EstoqueRetrofit, em que temos alguns imports inutilizados e chamadas que podem ser extraídas, como no caso da string que representa a URL base, "http://192.168.20.249:8080/", que ficará como URL_BASE.

E a chamada para configurar o Interceptor logging de EstoqueRetrofit() será extraída para o método configuraClient(), em que também poderemos fazer mais extrações, caso desejado. Por fim, mudaremos para private a URL_BASE. Há a possibilidade de utilizarmos também um inspetor de código.

Vamos testar o aplicativo para verificar se os comportamentos são mantidos como queremos. Incluiremos um novo produto, videogame, de valor 1200 e quantidade 8, depois o editaremos, removeremos, e rotacionaremos a tela. Conseguimos fazer a refatoração, e toda a integração necessária, e finalizamos o projeto.

@@06
Refatorando o código

Refatore o código do projeto nos arquivos modificados durante o curso, como a da Activity de lista de produtos, repositório de produtos, inicialização de Retrofit e etc.
Aplique técnicas de extração, renomeação ou qualquer outra que melhore a legibilidade do código. Sinta-se à vontade para usar o inspetor de código do Android Studio.

Após refatoração, teste o App e veja se todos os comportamentos funcionam como esperado.

O App deve apresentar o mesmo comportamento de antes, a diferença está na simplicidade do código. Para verificar todas as mudanças feitas em vídeo, confira o commit.

https://github.com/alura-cursos/android-persistencia-web/commit/ad708d8e0bd1a4d0677d866636e0ebd4c2cfa63b

@@07
Para saber mais - Código fonte da API web

Finalizamos a integração com a API web fornecida pelo curso, porém, nada impede de implementar novos comportamentos, como por exemplo, criar um end point na API que cria um produto novo ao tentar alterar um produto que não existe na API, mas existe no App desde o começo.
Caso tenha essa curiosidade e queira testar novos desafios, faça o clone do projeto, siga as instruções via README do repositório e, além de modificar a API, faça a integração do App com a novidade da API.

Leitura complementar
Como inspiração, leia também o artigo que destaca os principais motivos ao considerar o uso de API web em Apps Android.

https://github.com/alura-cursos/android-persistencia-web-api

https://medium.com/collabcode/quando-considerar-o-uso-de-api-em-projetos-android-ffdb4ba9ad6

@@08
O que aprendemos?

Nesta aula, aprendemos a:
Criar Callbacks genéricos quando não tem conteúdo no body;
Refatorar o código.

@@09
Conclusão

Chegamos ao fim do curso de Persistência web no Android! E se você chegou até aqui, parabéns. Durante o curso, aprendemos bastante conteúdo considerado avançado no mundo Android, e aproveitando este momento de conclusão, iremos rever os pontos pelos quais passamos.
Nosso primeiro problema foi a queixa do nosso cliente, que nos informou que não estava conseguindo utilizar o aplicativo, por não saber como lidar com perdas de dados.

Vimos que os maiores causadores deste problema existem quando consideramos apenas uma solução de persistência interna, porque ela está, sim, suscetível a qualquer ação que prejudique estes dados, como uma limpeza no aplicativo, desinstalação do mesmo ou perda do dispositivo móvel, entre outras situações que podem resultar na perda de dados.

Utilizamos a persistência web, alternativa muito bem vinda para este tipo de situação, e uma das abordagens mais comuns é a integração com APIs web, como no caso de um servidor, que opera numa arquitetura REST.

Aprendemos que, para que os dados sejam mantidos mesmo se houver perda de dados, ter uma integração com uma API é muito vantajoso. No entanto, vimos que isso implica em diversos desafios.

Precisamos, por exemplo, que nosso aplicativo tenha uma comunicação com web via protocolo HTTP, e com isso conseguimos fazer com que o aplicativo faça requisições HTTP. Para isso, o Android oferece várias alternativas, das quais optamos por uma biblioteca, o Retrofit.

A partir dele, começamos a implementar a comunicação com a API. Configuramos o Retrofit por meio da URL base, utilizada para todas as requisições, vimos o ProdutoService, componente dedicado a fazer todas as requisições desejadas.

Também vimos que é possível trabalharmos com diversas configurações, como no caso do uso de conversores diretos em vez de fazermos a conversão do produto da API manualmente.

Além disso, para as requisições, precisamos de permissão da internet via manifests, e que a partir da versão 9 do Android existe um bloqueio da requisição HTTP, enquanto a HTTPS é aceita por padrão, usando-se o atributo usesCleartextTraffic.

Outros detalhes impactantes envolvem a refatoração: vimos que o código de implementação com requisição web e persistência interna possui certa complexidade, exige estratégias de identificação: inicialmente buscamos produtos na API e depois internamente? Começamos salvando na API e depois internamente?

Isso tudo fez com que criássemos um repositório, justamente para manter essa lógica de lidar com os dados, e quando fizemos esta implementação vimos que a complexidade do código aumentou um pouco mais, uma vez que no repositório lidamos com dados e suas fontes, interna ou externamente.

Quanto à parte de atualização, quem se responsabiliza por isso é a nossa Activity, e para isto criamos diversas soluções, como Listeners, Callbacks e assim por diante, para notificarmos a Activity sem que ela tenha que lidar com os dados.

Depois, flexibilizamos as soluções tanto quando há sucesso quanto quando há falha. Fomos evoluindo aos poucos, mas, realmente, não se trata de um conteúdo trivial.

Aproveito para pedir um feedback seu, do que você achou do curso, se você conseguiu aproveitar e gostou da abordagem. Caso haja dúvidas, nos contate no fórum!

@@10
Projeto final

Caso precise consultar o projeto final, fique à vontade em fazer o download do projeto ou acesse a branch da última aula via GitHub.

https://github.com/alura-cursos/android-persistencia-web/archive/aula-6.zip

https://github.com/alura-cursos/android-persistencia-web/tree/aula-6