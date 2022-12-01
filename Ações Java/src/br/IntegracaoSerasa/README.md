<div align="center">
<img  width="60%" src="https://user-images.githubusercontent.com/34588048/205103039-26c0a2a1-b03b-456e-b735-0b6fd7f5bd85.png">
</div>

# Introdução
O procedimento de inclusão e exclusão do Serasa pelo sistema consiste no envio de informações ao SPC. Para fazer o procedimento é nencessário que o usuário logado no sistema tenha permissão de envios como é mostrado na documentação do desenvolvedor, além disto o parâmetro AMBIENTESERASA deve estar preenchido com a URL do ambiente que será utilizada para enviar os dados ao serviço SPC. 

# História do Usuário
Como Gerente Finaneira, desejo que o processo de exclusão do Serasa seja automatizado, para que o setor possa ganhar produtividade, pois atualmente as negativações / exclusões de clientes inadimplentes são realizadas manualmente e consomem muito tempo, conforme descritivos abaixo:

## Fluxo do Processo
![image](https://user-images.githubusercontent.com/34588048/205098275-a32ba5e9-4f33-4ad5-b62b-f1d2726cd064.png)
## Dados Estatísticos
### Inclusão
- Através do [site](https://sistema.spc.org.br/spc/insumo/spc/initFilter.action?__idFuncionalidade=50)
- São realizadas em média 135 negativações por mês.
- O tempo para cada negativação gira em torno de 10 minutos.
- O custo para inclusão é de R$3,64 para emissão da carta de aviso que o serasa envia ao cliente.

### Exclusão
- Através do [site](https://sistema.spc.org.br/spc/insumo/spc/initFilter.action?__idFuncionalidade=50)
- São realizadas em média 70 exclusões por mês
- O tempo para cada exclusão demora em tordo de 10 minutos
- Não existe custo para exclusão.
- Atualmente clientes são excluídos após entrarem em contato e realizada a confirmação do pagamento.
- Após confirmação do pagamento, não há um prazo para excluir a negativação, a mesma é realizada imediatamente.

## Funcionalidades
- O botão de ação deve estar na tela de movimentação financeira
- o botão deve ser apresentado somente para usuários com permissão
- O dashboard deve estar liberado para os usuários do financeiro
- A mensagem de inclusão ou exclusão deve estar clara ao final do processo de execução, tanto para integrações com sucesso ou para erros.

### Regras para a Inclusão
- Não deve ser possível renegociar um titulo que tenha sido enviado para o serasa.
- Não deve ser possível enviar para negativação duas ou mais vezes um mesmo titulo.
- A negativação poderá ser feita em massa, ou seja, deve ser possível enviar dois ou mais titulos de uma só vez para o Serasa simultaneamente.
- A negativação será efetuada através de um botão de ação na movimentação financeira.
- Só podem ser enviados titulos com vencimentos igual ou superior à 5 dias úteis .
- Titulos vinculados à notas que ainda não tiveram suas mercadorias entregues não podem ser negativados, independente do tempo de venciamento. (informação disponível no dash 274D).
- Possíveis titulos renegociados, cujo o número da nota seja inexistente, não serão validados pela regra de entrega de produtos descrita acima, ou seja, se forem selecionados e estiverem dentros das demais regras, serão negativados normalmente.
- 8 - Clientes "exceção" não devem ser negativados. Esta exceção deve ser criada dentro do cadastro de clientes com acesso restrito por usuário.

### Regras para a Exclusão
- O titulo deve ter sido enviado para o serasa.
- Podem haver mais de um titulo negativado para um mesmo cliente, e somente devem ser excluídos do serasa àqueles selecionados na movimentação financeira.
- A exclusão se dará por um botão de ação.

#### Gerais
- Todas as integrações tanto de inclusão quanto de exclusão devem apresentar uma mensagem clara na tela sobre seu status, se as inserções ocorreram corretamente, quantas inserções e se houve erro, qual o erro.
- Deve existir um log de todas as inclusões / exclusões efetivadas
- Deve ser criado um dashboard para acompanhamento das incluões/exclusoes, similar ao da análise de crédito (244D).
    - O dashboard deve ter dois componentes do tipo tabela, um para inclusões e outro para exclusões
    - Cada tabela de conter as colunas Data e Hora da inclusão ou exclusão, codigo do parceiro, razão social do parceiro e usuário que realizou a operação.
    - Devem ter dois componentes de BI do tipo "Valor" para exibir um contador de inclusões/exclusões mensais e diárias,.


## Demonstração
Por conter dados sensíveis, foram ocultados algumas informações por questões de privacidade e segurança.

### Localizando registros na movimentação Financeira
Acesse a tela de movimentação financeira e selecionar um ou vários títulos que deverão ser processados, após isto clique na opção desejada conforme na imagem abaixo
![image](https://user-images.githubusercontent.com/34588048/205098468-f06718fa-7659-4caa-90ab-4d6f07426688.png)

## Registro de Inclusão e Exclusção do Serasa

Após o procedimento realizado, a tela 43D exibe os dados transmitidos ao serasa, caso tenha algum registro com erro a linha ficará vermelha.
![43D Registro Serasa](https://user-images.githubusercontent.com/34588048/205098996-05143ffd-b700-456d-b1dc-045490bd9b69.png)

## FAQ

#### É possível incluir um título no Serasa mais do que uma vez?
Não, para que isto não aconteça é armazenado dentro de um log no sistema.

#### Caso tente excluir um título não enviado ao Serasa, o que acontecerá?
É retornado um erro da própria aplicação alegando que para que seja excluído é necessário que tenha enviado uma única vez.

#### Existe o risco de enviar o título errado?
No sistema foi tratado para que não seja enviado receitas ou provisões, sendo assim, existe outras validações que o próprio sistema faz. 




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
