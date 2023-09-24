#### 22/09/2023

Curso de Android: acessando uma API Web

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