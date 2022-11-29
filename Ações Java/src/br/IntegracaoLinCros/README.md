
![Logo](https://github.com/EDUARDO-TEIXEIRA/Personalizacao-CIVITT/blob/c5f006321b8b2f97a2f0046babee906595bf8b5c/A%C3%A7%C3%B5es%20Java/src/br/IntegracaoLinCros/_imagensProjeto/capa-projeto.png)


# Introdução

Atualmente as cotações são realizadas manualmente, ou seja, acesso a plataforma web, envio algumas informações necessárias para que o sistema da Lincros realize as cotações e informe a melhor transportadora, e apos acesso o pedido de venda que já foi separado pelo WMS, ou seja, pronto para ser faturado, e vinculo a transportadora a este pedido.

## Dados Estatíticos
Foram Faturadas em 2022 uma média de mais de 1300 notas/Mês, o que resulta em um esforço de aproximadamente 22 horas/mês somente para realizar a cotação de frete.

O tempo média gasto, com as telas já abertas do Sankhya e da Lincros, uma vez que já decorei os códigos de todas as transportadoras, é de aproximadamente 1 minuto para cada pedido.


## Funcionalidades
- A rotina de integação deve ser acionada através de um botão de ação, na tela do portal de vendas (TGFCAB), que fara a cotação para todos os pedidos selecionados na tela.
- Deve-se tambem criar um parâmetro para ativar ou não a cotação automaticamente,l ou seja, se estiver ativo, não será necessária a cotação via botão de ação, mas sim, realizar a cotação sempre que a nota for enviada para doca de saida pelo coletor (precisa buscar este status e entender o momento que que ocorre). IMPORTANTE, Este processo não será implementado nesta primeira fase, mas deve estar pronto para o momento que forem utilizar.
- Independente dos itens 1 e 2, sempre que a rotina for acionada, o sistema se conectará à lincros via API e retornará todas as empresas cotadas.
- Este retorno deve ser armazenado em um log, com as informações mínimas de (todos os dados enviados para lincros + Razão Social das transportadoras cotadas + valores de frete + numero único do pedido + usuário Sankhya)
- No pedido, deve-se vincular a transportadora que teve o menor preço dentre as cotadas. Caso a vencedora não tenha cadatro no sankhya, deve-se vincular a próxima com menor preço até que seja encontrata uma que obtenha cadastro. E para o usuário, deve-se retornar uma mensagem informando que ja existem transportadoras com menor preço que não estão cadastradas no Sankhya (Somente apresentar a mensagem se for realiza a cotação pelo botão de ação).

**Sugestão:** Criar um campo texto na tgfcab, para, em casos em que a tansportadora não estiver cadastrada, informar o CNPJ e Razão social da que seria a vencedora, com isso, o faturista visualizará em duas colunas a empressa de menor preço e a empresa que de fato foi vinculada ao pedido. Essa informação será muito útil quando utilizada a cotação automática.

## Screenshots

![App Screenshot](https://via.placeholder.com/468x300?text=App+Screenshot+Here)


## Demonstração

Insira um gif ou um link de alguma demonstração


## FAQ

#### É possível buscar mais de uma única vez o frete de um registro?
Sim, visto que a cada solicitação será armazenado no log de operações.

#### Caso não tenha nenhuma transportadora vinculada

Resposta 2


## Stakeholders

| Nome |Função   |
| ------------ | ------------ |
| Jucimar Marinho  | Usuário Chave  |
| Eduardo Teixeira  |  Desenvolvedor |
| Bruno Bergami  | P.O  |


## Stack Utilizada
**Front-end:** 
- Sankhya ERP

**Back-end:** 
- Java

**IDE Desenvolvimento:**
- Eclipse

**Database:**
- Oracle

## Documentação
- Postman
- Swagger
- Trello
- Bizagi
- TikiWiki
- GitHub

## Outras Ferramentas

- Json Schema
- SQL Developer
- Git



## Referência
- [Cálculo com base nas tabelas já vigentes - LINCROS](https://integracao-api.lincros.com/swagger-tms/#/C%C3%A1lculo/post_v3_calculo_calcularNota)

## Suporte

Para suporte, mande um email para eduardo.teixeira@civitt.com.br

