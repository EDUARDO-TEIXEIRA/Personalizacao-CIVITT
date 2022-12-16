<div align="center">
<img src="https://github.com/EDUARDO-TEIXEIRA/Personalizacao-CIVITT/blob/0877a84391e1809bb9f6236a9f51779cae3c4d41/M%C3%ADdias/_imagensProjeto/Integra%C3%A7%C3%A3o%20Lincros/capa-projeto.png">
</div>

# Introdução

Ao enviar uma anotação de coleta ao sistema Lincros, o usuário fornece as informações necessárias para que a transportadora possa realizar a coleta e entrega da mercadoria. A notificação XML enviada ao sistema Lincros serve para alertar a transportadora com as informações necessárias para coletar a mercadoria no local desejado.
Ao receber a notificação, o sistema Lincros verifica os dados fornecidos e faz o encaminhamento da mercadoria para a transportadora. O sistema também pode fornecer ao usuário informações importantes sobre a entrega, como horário, local e data prevista.

# História do Usuário
Diariamente, no inicio do expediente, o colaborador Jucimar acessa o Sankhya, e gera um arquivo ZIP com todas as notas fiscais de transportadoras emitidas no dia anterior, e envia para lincros através da plataforma web.

## Roadmap

## Aprendizados
Uma dos desafios encontrados no projeto foi o envio do XML, neste código abaixo foi desenvolvido um método que recebe a chave e também o arquivo, no meu caso é necessário enviar conforme a figura abaixo.

<div align="center">
![image](https://user-images.githubusercontent.com/34588048/208096202-e0902326-907f-457d-9e93-cc277c23c7c8.png)
</div>

Após chamar o método abaixo, é necessário também de uma classe para enviar esses dados a API e foi aí que usei a Classe [DataOutputStream](https://docs.oracle.com/javase/7/docs/api/java/io/DataOutputStream.html).

```java
    request = new DataOutputStream(connection.getOutputStream());
```

<iframe
  src="https://carbon.now.sh/embed?bg=rgba%2818%2C168%2C240%2C0.87%29&t=panda-syntax&wt=none&l=text%2Fx-java&width=680&ds=false&dsyoff=20px&dsblur=68px&wc=true&wa=true&pv=56px&ph=56px&ln=false&fl=1&fm=Fira+Code&fs=14px&lh=152%25&si=false&es=2x&wm=false&code=public%2520static%2520void%2520addArquivoRequisicao%28String%2520fieldName%252C%2520File%2520uploadFile%29%2520throws%2520IOException%2520%257B%250A%2509%2509String%2520fileName%2520%253D%2520uploadFile.getName%28%29%253B%250A%2509%2509request.writeBytes%28twoHyphens%2520%252B%2520boundary%2520%252B%2520crlf%29%253B%250A%2509%2509request.writeBytes%28%250A%2509%2509%2509%2509%2522Content-Disposition%253A%2520form-data%253B%2520name%253D%255C%2522%2522%2520%252B%2520fieldName%2520%252B%2520%2522%255C%2522%253Bfilename%253D%255C%2522%2522%2520%252B%2520fileName%2520%252B%2520%2522%255C%2522%2522%2520%252B%2520crlf%29%253B%250A%2509%2509request.writeBytes%28crlf%29%253B%250A%250A%2509%2509byte%255B%255D%2520bytes%2520%253D%2520Files.readAllBytes%28uploadFile.toPath%28%29%29%253B%250A%2509%2509request.write%28bytes%29%253B%250A%2509%257D"
  style="width: 1024px; height: 439px; border:0; transform: scale(1); overflow:hidden;"
  sandbox="allow-scripts allow-same-origin">
</iframe>

Você pode conferir o snippet de todo o código dessa classe [aqui](https://gist.github.com/EDUARDO-TEIXEIRA/db2fe0726e8a715c6a73b44da42115ae)

## Stakeholders

| Nome |Função   |
| ------------ | ------------ |
| Jucimar | Operador  |
| Eduardo Teixeira  |  Desenvolvedor |
| Bruno Bergami  | P.O  |


## Stack Utilizada
**Back-end:** 
- Java

**IDE Desenvolvimento:**
- Eclipse

**Database:**
- Oracle

## Documentação
- Postman
- Trello
- Bizagi
- TikiWiki
- GitHub

## Outras Ferramentas
- Sankhya ERP
- SQL Developer
- Git
