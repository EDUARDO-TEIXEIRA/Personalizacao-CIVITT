
<div align="center">
<img src="https://github.com/EDUARDO-TEIXEIRA/Personalizacao-CIVITT/blob/0877a84391e1809bb9f6236a9f51779cae3c4d41/M%C3%ADdias/_imagensProjeto/Integra%C3%A7%C3%A3o%20Lincros/capa-projeto.png">
</div>

# Introdução

Atualmente as cotações são realizadas manualmente, ou seja, acesso a plataforma web, envio algumas informações necessárias para que o sistema da Lincros realize as cotações e informe a melhor transportadora, e apos acesso o pedido de venda que já foi separado pelo WMS, ou seja, pronto para ser faturado, e vinculo a transportadora a este pedido.

![fluxograma](https://user-images.githubusercontent.com/34588048/206234921-8f2c282d-ec3e-4e08-8430-4e961d18a366.png)

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


### Estrutura do JSON de Retorno
A API retorna o detalhamento dos impostos e taxas adicionais em um array, além disto é possível listar por ordem as transportadoras com menor valor conforme a imagem abaixo.
![Estrutura Json](https://user-images.githubusercontent.com/34588048/205348557-1f8a1085-9710-4c6f-9832-6c87a34f41cb.png)

## Demonstração

Insira um gif ou um link de alguma demonstração

## Stakeholders

| Nome |Função   |
| ------------ | ------------ |
| Jucimar Marinho  | Usuário Chave  |
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
- Swagger
- Trello
- Bizagi
- TikiWiki
- GitHub

## Outras Ferramentas
- Sankhya ERP
- Json Schema
- SQL Developer
- Git

## Referência
- [Cálculo com base nas tabelas já vigentes - LINCROS](https://integracao-api.lincros.com/swagger-tms/#/C%C3%A1lculo/post_v3_calculo_calcularNota)