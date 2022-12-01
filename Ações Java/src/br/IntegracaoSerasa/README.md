<div align="center">
![capa-serasa](https://user-images.githubusercontent.com/34588048/205099655-1476cde9-e778-426a-b38e-9a77f33a3f61.png)
</div>
# Introdução
O procedimento de inclusão e exclusão do Serasa pelo sistema consiste no envio de informações ao SPC. Para fazer o procedimento é nencessário que o usuário logado no sistema tenha permissão de envios como é mostrado na documentação do desenvolvedor, além disto o parâmetro AMBIENTESERASA deve estar preenchido com a URL do ambiente que será utilizada para enviar os dados ao serviço SPC. 

# História do Usuário
Como Gerente Finaneira, desejo que o processo de exclusão do Serasa seja automatizado, para que o setor possa ganhar produtividade, pois atualmente as negativações / exclusões de clientes inadimplentes são realizadas manualmente e consomem muito tempo, conforme descritivos abaixo:

## Fluxo do Processo
![image](https://user-images.githubusercontent.com/34588048/205098275-a32ba5e9-4f33-4ad5-b62b-f1d2726cd064.png)
## Dados Estatísticos
### Inclusão
1 - Através do site [https://sistema.spc.org.br/spc/insumo/spc/initFilter.action?__idFuncionalidade=50]
2 - São realizadas em média 135 negativações por mês.
3 - O tempo para cada negativação gira em torno de 10 minutos.
4 - O custo para inclusão é de R$3,64 para emissão da carta de aviso que o serasa envia ao cliente.

### Exclusão
1 - Através do site [https://sistema.spc.org.br/spc/insumo/spc/initFilter.action?__idFuncionalidade=50]
2 - São realizadas em média 70 exclusões por mês
3 - O tempo para cada exclusão demora em tordo de 10 minutos
5 - Não existe custo para exclusão.
6 - Atualmente clientes são excluídos após entrarem em contato e realizada a confirmação do pagamento.
7 - Após confirmação do pagamento, não há um prazo para excluir a negativação, a mesma é realizada imediatamente.

## Funcionalidades
1 - O botão de ação deve estar na tela de movimentação financeira
2 - o botão deve ser apresentado somente para usuários com permissão
3 - O dashboard deve estar liberado para os usuários do financeiro
4 - A mensagem de inclusão ou exclusão deve estar clara ao final do processo de execução, tanto para integrações com sucesso ou para erros.

### Regras para a Inclusão
1 - Não deve ser possível renegociar um titulo que tenha sido enviado para o serasa.
2 - Não deve ser possível enviar para negativação duas ou mais vezes um mesmo titulo.
3 - A negativação poderá ser feita em massa, ou seja, deve ser possível enviar dois ou mais titulos de uma só vez para o Serasa simultaneamente.
4 - A negativação será efetuada através de um botão de ação na movimentação financeira.
5 - Só podem ser enviados titulos com vencimentos igual ou superior à 5 dias úteis .
6 - Titulos vinculados à notas que ainda não tiveram suas mercadorias entregues não podem ser negativados, independente do tempo de venciamento. (informação disponível no dash 274D).
7 - Possíveis titulos renegociados, cujo o número da nota seja inexistente, não serão validados pela regra de entrega de produtos descrita acima, ou seja, se forem selecionados e estiverem dentros das demais regras, serão negativados normalmente.
8 - Clientes "exceção" não devem ser negativados. Esta exceção deve ser criada dentro do cadastro de clientes com acesso restrito por usuário.

### Regras para a Exclusão
1 - O titulo deve ter sido enviado para o serasa.
2 - Podem haver mais de um titulo negativado para um mesmo cliente, e somente devem ser excluídos do serasa àqueles selecionados na movimentação financeira.
3 - A exclusão se dará por um botão de ação.

#### Gerais
1 - Todas as integrações tanto de inclusão quanto de exclusão devem apresentar uma mensagem clara na tela sobre seu status, se as inserções ocorreram corretamente, quantas inserções e se houve erro, qual o erro.
2 - Deve existir um log de todas as inclusões / exclusões efetivadas
3 - Deve ser criado um dashboard para acompanhamento das incluões/exclusoes, similar ao da análise de crédito (244D).
3.1 - O dashboard deve ter dois componentes do tipo tabela, um para inclusões e outro para exclusões
3.2 - Cada tabela de conter as colunas Data e Hora da inclusão ou exclusão, codigo do parceiro, razão social do parceiro e usuário que realizou a operação.
3.2 - Devem ter dois componentes de BI do tipo "Valor" para exibir um contador de inclusões/exclusões mensais e diárias,.


## Demonstração
Por conter dados sensíveis, foram ocultados algumas informações por questões de privacidade e segurança.

### Localizando registros na movimentação Financeira
Acesse a tela de movimentação financeira e selecionar um ou vários títulos que deverão ser processados, após isto clique na opção desejada conforme na imagem abaixo
![image](https://user-images.githubusercontent.com/34588048/205098468-f06718fa-7659-4caa-90ab-4d6f07426688.png)

## Registro de Inclusão e Exclusção do Serasa

Após o procedimento realizado, a tela 43D exibe os dados transmitidos ao serasa, caso tenha algum registro com erro a linha ficará vermelha.
![43D Registro Serasa](https://user-images.githubusercontent.com/34588048/205098996-05143ffd-b700-456d-b1dc-045490bd9b69.png)
## Stakeholders

| Nome |Função   |
| ------------ | ------------ |
| Lorena Favalessa | Gerente Financeiro  |
| Jessica Bastos | Operador  |
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
- Json Schema
- SQL Developer
- Git

## FAQ

#### É possível incluir um título no Serasa mais do que uma vez?
Não, para que isto não aconteça é armazenado dentro de um log no sistema.

#### Caso tente excluir um título não enviado ao Serasa, o que acontecerá?
É retornado um erro da própria aplicação alegando que para que seja excluído é necessário que tenha enviado uma única vez.

#### Existe o risco de enviar o título errado?
No sistema foi tratado para que não seja enviado receitas ou provisões, sendo assim, existe outras validações que o próprio sistema faz. 




