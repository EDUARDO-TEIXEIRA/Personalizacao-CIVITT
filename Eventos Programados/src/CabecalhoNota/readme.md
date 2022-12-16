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
<img src="https://user-images.githubusercontent.com/34588048/208096202-e0902326-907f-457d-9e93-cc277c23c7c8.png">
</div>


Após chamar o método abaixo, é necessário também de uma classe para enviar esses dados a API e foi aí que usei a Classe [DataOutputStream](https://docs.oracle.com/javase/7/docs/api/java/io/DataOutputStream.html).

```java
    request = new DataOutputStream(connection.getOutputStream());
```

<div align="center">
  <img src="https://user-images.githubusercontent.com/34588048/208107175-db7e9ee5-3346-41cc-8819-88d9597e2ea4.png">
</div>

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
