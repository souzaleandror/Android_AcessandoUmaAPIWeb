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