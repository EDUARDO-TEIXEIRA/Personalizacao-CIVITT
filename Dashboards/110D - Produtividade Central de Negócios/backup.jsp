<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8" isELIgnored="false" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ page import="java.util.*" %>
		<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
			<%@ taglib prefix="snk" uri="/WEB-INF/tld/sankhyaUtil.tld" %>
				<html>
					<meta charset="UTF-8">
                    <head>
                     <!-- Querys -->
                        <snk:query var="vendedor">
                            SELECT CAST(VEN.CODVEND AS VARCHAR(5)) AS CODVEND ,
                                   DECODE(:P_CODVEND, 0, '0-TODOS', VEN.CODVEND || ' - ' || VEN.APELIDO) AS APELIDO 
                              FROM TGFVEN VEN 
                             WHERE VEN.CODVEND IN :P_CODVEND
                        </snk:query>
                        
                        <snk:query var="tarefas">
                           SELECT COUNT(CASE WHEN STATUS_SITUACAO = 'Fora do Prazo' THEN 1 END) FORA_PRAZO,
                                  COUNT(CASE WHEN STATUS_SITUACAO = 'No Prazo' THEN 1 END) NO_PRAZO,
                                  COUNT(CASE WHEN STATUS_SITUACAO = 'Não cumpriu' THEN 1 END) NAO_CUMPRIU,
                                  COUNT(CASE WHEN STATUS_SITUACAO = 'Aguardando Agendamento' THEN 1 END) AG_AGENDAMENTO,
                                  COUNT(CASE WHEN STATUS_SITUACAO = 'Não Classificado' THEN 1 END) NAO_CLASSIFICADO
                             FROM (SELECT CASE WHEN TRUNC(DHPROXCHAM) > TRUNC(DHCHAMADA) THEN 'Fora do Prazo' 
                                               WHEN TRUNC(DHCHAMADA) >= TRUNC(DHPROXCHAM) THEN 'No Prazo'
                                               WHEN TRUNC(DHPROXCHAM) < TRUNC(SYSDATE) AND TRUNC(DHPROXCHAM) IS NULL THEN 'Não cumpriu'
                                               WHEN TRUNC(DHPROXCHAM) > TRUNC(DHCHAMADA) THEN 'Aguardando Agendamento'
                                               ELSE 'Não Classificado'
                                           END AS STATUS_SITUACAO
                                          FROM TGFTEL TEL
                                         INNER JOIN TSIUSU USU 
                                            ON USU.CODUSU = TEL.CODUSU
                                         INNER JOIN TGFVEN VEN
                                            ON VEN.CODVEND = USU.CODVEND   
                                         WHERE USU.CODVEND IN DECODE(:P_CODVEND, 0, USU.CODVEND, :P_CODVEND)
                                           AND (TRUNC(DHCHAMADA) BETWEEN :P_PERIODO.INI AND :P_PERIODO.FIN)
                                           AND VEN.AD_CODCOR IN (:P_CODCOR)
                                           
                                           )
                        </snk:query>

                        <snk:query var="vendas">
                          SELECT 'R$ ' || TO_CHAR(ROUND(SUM(NVL(TOTALLIQ,0)),2), 'FM999G999G999D90') AS VALOR, 
                                  COUNT(DISTINCT QTDREGISTROS) AS QTDREGISTROS
                            FROM (SELECT  (SELECT SUM(ITE.VLRTOT - ITE.VLRDESC - ITE.VLRREPRED) AS TOTALLIQ     
                                           FROM TGFITE ITE                                                    
                                          WHERE ITE.NUNOTA = CAB.NUNOTA) AS TOTALLIQ,                         
                                        CAB.VLRNOTA AS TOTALBRU,                                              
                                        CAB.NUNOTA,                                                           
                                        (SELECT SUM(FUN_CVT_CUSTO_MEDIO_COM_ICMS (ITE.CODPROD, CAB.DTFATUR, CAB.CODEMP)
                                                 * ITE.QTDNEG)                            
                                           FROM TGFITE ITE                                                    
                                          WHERE ITE.NUNOTA = CAB.NUNOTA) AS CMV,
                                          CAB.NUNOTA AS QTDREGISTROS                               
                                  FROM TGFCAB CAB                                                             
                                 INNER JOIN TGFTOP TOP                                                        
                                    ON TOP.CODTIPOPER=CAB.CODTIPOPER                                          
                                   AND TOP.DHALTER=CAB.DHTIPOPER         
                                 INNER JOIN TGFVEN VEN
                                   ON VEN.CODVEND = CAB.AD_CODVEND
                                 WHERE CAB.TIPMOV='P'                                                         
                                   AND GRUPO IN ('PED.VENDA','TROCA')                                         
                                   AND VEN.ATIVO = 'S'
                                   AND VEN.AD_HIERARQUIA IN (25, 30)
                                   AND VEN.AD_CODCOR IN (:P_CODCOR)
                                   AND CAB.DTNEG BETWEEN :P_PERIODO.INI AND :P_PERIODO.FIN
                                   AND CAB.AD_CODVEND IN DECODE(:P_CODVEND, 0, CAB.AD_CODVEND, :P_CODVEND)
                                   AND CAB.NUNOTA NOT IN (SELECT C2.NUNOTA                                    
                                                            FROM TGFCAB C1                                    
                                                           INNER JOIN TGFVAR V1                               
                                                              ON V1.NUNOTAORIG=C1.NUNOTA                      
                                                           INNER JOIN TGFCAB C2                               
                                                              ON C2.NUNOTA = V1.NUNOTA                        
                                                           INNER JOIN TGFTOP T1                               
                                                              ON T1.CODTIPOPER=C1.CODTIPOPER AND T1.DHALTER = C1.DHTIPOPER
                                                           INNER JOIN TGFTOP T2                               
                                                              ON T2.CODTIPOPER=C2.CODTIPOPER AND T2.DHALTER = C2.DHTIPOPER
                                                           INNER JOIN TGFVEN V2
                                                              ON V2.CODVEND = C2.AD_CODVEND
                                                           WHERE T1.GRUPO IN ('PED.VENDA','TROCA')            
                                                             AND T2.GRUPO IN ('PED.VENDA','TROCA')            
                                                             AND C2.DTNEG BETWEEN :P_PERIODO.INI AND :P_PERIODO.FIN
                                                             AND V2.AD_HIERARQUIA IN (25, 30)
                                                             AND V2.AD_CODCOR IN (:P_CODCOR)
                                                             AND V2.CODVEND IN DECODE(:P_CODVEND, 0, V2.CODVEND, :P_CODVEND)
                                                          GROUP BY C2.NUNOTA))
                        </snk:query>

                        <snk:query var="faturamento">
                            SELECT 'Faturamento' AS DESCRICAO, 
                                    'R$ ' || TO_CHAR(NVL(ROUND(SUM(VALOR),2),0), 'FM999G999G999D90') AS VALOR
                               FROM (SELECT  NVL(((ITE.VLRTOT - ITE.VLRDESC-ITE.VLRREPRED) * 
                                             CASE WHEN TOP.BONIFICACAO = 'S' 
                                              AND TOP.GRUPO = 'DEV.VENDAS'
                                             THEN 0 
                                             ELSE 1 END),0) AS VALOR, 
                                             FUN_CVT_CUSTO_MEDIO_COM_ICMS (ITE.CODPROD, CAB.DTFATUR, CAB.CODEMP)* (ITE.QTDNEG - ITE.QTDENTREGUE) AS CMV,
                                             ITE.NUNOTA
                               
                                        FROM TGFCAB CAB
                                       INNER JOIN  TGFITE ITE
                                          ON ITE.NUNOTA = CAB.NUNOTA
                                       INNER JOIN TGFTOP TOP 
                                          ON TOP.CODTIPOPER = CAB.CODTIPOPER 
                                         AND TOP.DHALTER = CAB.DHTIPOPER 
                                       INNER JOIN TGFVEN VEN
                                          ON VEN.CODVEND = CAB.AD_CODVEND   
                                       WHERE TOP.GOLSINAL = -1 
                                         AND CAB.AD_CODVEND IN DECODE(:P_CODVEND, 0, CAB.AD_CODVEND, :P_CODVEND)
                                         AND VEN.ATIVO = 'S'
                                         AND VEN.AD_HIERARQUIA IN (25, 30)
                                         AND VEN.AD_CODCOR IN (:P_CODCOR)
                                         AND CAB.DTNEG BETWEEN :P_PERIODO.INI AND :P_PERIODO.FIN
                                         AND CAB.STATUSNOTA = 'L'
                                         AND TOP.GRUPO IN('VENDAS'))
	                      </snk:query>

                        <snk:query var="devolucao">
                          SELECT 'Devoluções' AS DESCRICAO, 
                                 'R$ ' || TO_CHAR(NVL(ROUND(SUM(VALOR),2),0), 'FM999G999G999D90') AS VALOR
                            FROM (SELECT  NVL(((ITE.VLRTOT - ITE.VLRDESC-ITE.VLRREPRED) * 
                                    CASE WHEN TOP.BONIFICACAO = 'S' 
                                     AND TOP.GRUPO = 'DEV.VENDAS'
                                    THEN 0  ELSE 1 END),0) AS VALOR, 
                                   FUN_CVT_CUSTO_MEDIO_COM_ICMS (ITE.CODPROD, CAB.DTFATUR, CAB.CODEMP)* (ITE.QTDNEG - ITE.QTDENTREGUE) AS CMV,
                                   ITE.NUNOTA
                              FROM TGFCAB CAB
                             INNER JOIN  TGFITE ITE
                                ON ITE.NUNOTA = CAB.NUNOTA
                             INNER JOIN TGFTOP TOP 
                                ON TOP.CODTIPOPER = CAB.CODTIPOPER 
                               AND TOP.DHALTER = CAB.DHTIPOPER 
                             INNER JOIN TGFVEN VEN
                                ON VEN.CODVEND = CAB.AD_CODVEND   
                             WHERE TOP.GOLSINAL = -1 
                               AND CAB.AD_CODVEND IN DECODE(:P_CODVEND, 0, CAB.AD_CODVEND, :P_CODVEND)
                               AND VEN.ATIVO = 'S'
                               AND VEN.AD_HIERARQUIA IN (25, 30)
                               AND VEN.AD_CODCOR IN (:P_CODCOR)
                               AND CAB.DTNEG BETWEEN :P_PERIODO.INI AND :P_PERIODO.FIN
                               AND CAB.STATUSNOTA = 'L'
                               AND TOP.GRUPO IN('DEV.VENDAS'))
                        </snk:query> 

                        <snk:query var="faturamentoliquido">
                          SELECT 'Faturamento - Devolução ' AS DESCRICAO,
                                 'R$ ' || TO_CHAR(NVL(ROUND(SUM(VALOR),2),0), 'FM999G999G999D90') AS VALOR
                            FROM (
                          SELECT CASE WHEN TOP.GRUPO = 'VENDAS' THEN 
                                          ITE.VLRTOT - ITE.VLRDESC-ITE.VLRREPRED 
                                    ELSE (ITE.VLRTOT - ITE.VLRDESC-ITE.VLRREPRED) * 
                                          CASE WHEN TOP.BONIFICACAO = 'S' 
                                                AND TOP.GRUPO = 'DEV.VENDAS'
                                              THEN 0 
                                              ELSE 1 
                                                END * -1
                                      END AS VALOR, 
                                  CASE WHEN  TOP.GRUPO = 'VENDAS' THEN 
                                  (FUN_CVT_CUSTO_MEDIO_COM_ICMS (ITE.CODPROD, CAB.DTFATUR, CAB.CODEMP) * (ITE.QTDNEG)) 
                                    END  AS CMVVENDA,
                                  CASE WHEN  TOP.GRUPO = 'DEV.VENDAS' THEN 
                                  (FUN_CVT_CUSTO_MEDIO_COM_ICMS (ITE.CODPROD, CAB.DTFATUR, CAB.CODEMP) * (ITE.QTDNEG)) 
                                    END  AS CMVDEV,
                                  DECODE(TOP.GRUPO, 'VENDAS', ITE.NUNOTA) AS NUNOTA
                         
                             FROM TGFCAB CAB
                            INNER JOIN  TGFITE ITE
                               ON ITE.NUNOTA = CAB.NUNOTA
                            INNER JOIN TGFTOP TOP 
                               ON TOP.CODTIPOPER = CAB.CODTIPOPER 
                              AND TOP.DHALTER = CAB.DHTIPOPER 
                            INNER JOIN TGFVEN VEN
                               ON VEN.CODVEND = CAB.AD_CODVEND  
                            WHERE TOP.GOLSINAL = -1 
                              AND CAB.DTNEG BETWEEN :P_PERIODO.INI AND :P_PERIODO.FIN
                              
                              AND CAB.STATUSNOTA = 'L'
                              AND CAB.AD_CODVEND IN DECODE(:P_CODVEND, 0, CAB.AD_CODVEND, :P_CODVEND)
                              AND VEN.ATIVO = 'S'
                              AND VEN.AD_HIERARQUIA IN (25, 30)
                              AND VEN.AD_CODCOR IN (:P_CODCOR)
                              AND TOP.GRUPO IN('VENDAS', 'DEV.VENDAS'))
                  
                        </snk:query>

                        <snk:query var="prazomedio">
                          SELECT NVL(ROUND(AVG((SELECT AVG(PPG.PRAZO) AS PRAZO
                                              FROM TGFPPG PPG
                                             WHERE PPG.CODTIPVENDA = CAB.CODTIPVENDA)),0),0) AS PRAZO
                              FROM TGFCAB CAB
                             INNER JOIN  TGFITE ITE
                                ON ITE.NUNOTA = CAB.NUNOTA
                             INNER JOIN TGFTOP TOP 
                                ON TOP.CODTIPOPER = CAB.CODTIPOPER 
                               AND TOP.DHALTER = CAB.DHTIPOPER 
                             INNER JOIN TGFVEN VEN
                                ON VEN.CODVEND = CAB.AD_CODVEND   
                             WHERE TOP.GOLSINAL = -1 
                               AND CAB.AD_CODVEND IN DECODE(:P_CODVEND, 0, CAB.AD_CODVEND, :P_CODVEND)
                               AND VEN.ATIVO = 'S'
                               AND VEN.AD_HIERARQUIA IN (25, 30)
                               AND VEN.AD_CODCOR IN (:P_CODCOR)
                               AND CAB.DTNEG BETWEEN :P_PERIODO.INI AND :P_PERIODO.FIN
                               AND CAB.STATUSNOTA = 'L'
                               AND TOP.GRUPO IN('VENDAS')
	                      </snk:query>

                        <snk:query var="precomedio">
                          SELECT 'R$ ' || TO_CHAR(NVL(ROUND(AVG(VALOR),2),0), 'FM999G999G999D90') AS AVGREGISTROS 
                           FROM (SELECT NVL(((ITE.VLRTOT - ITE.VLRDESC-ITE.VLRREPRED) * 
                                        CASE WHEN TOP.BONIFICACAO = 'S' 
                                        AND TOP.GRUPO = 'DEV.VENDAS'
                                        THEN 0 
                                        ELSE 1 END),0) AS VALOR 
                                   FROM TGFCAB CAB
                                  INNER JOIN  TGFITE ITE
                                     ON ITE.NUNOTA = CAB.NUNOTA
                                  INNER JOIN TGFTOP TOP 
                                     ON TOP.CODTIPOPER = CAB.CODTIPOPER 
                                    AND TOP.DHALTER = CAB.DHTIPOPER 
                                  INNER JOIN TGFVEN VEN
                                     ON VEN.CODVEND = CAB.AD_CODVEND   
                                  WHERE TOP.GOLSINAL = -1 
                                    AND CAB.AD_CODVEND IN DECODE(:P_CODVEND, 0, CAB.AD_CODVEND, :P_CODVEND)
                                    AND VEN.ATIVO = 'S'
                                    AND VEN.AD_HIERARQUIA IN (25, 30)
                                    AND VEN.AD_CODCOR IN (:P_CODCOR)
                                    AND CAB.DTNEG BETWEEN :P_PERIODO.INI AND :P_PERIODO.FIN
                                    AND CAB.STATUSNOTA = 'L'
                                    AND TOP.GRUPO IN('VENDAS'))
                                </snk:query>  
                        <snk:query var="mix">
                          SELECT COUNT(DISTINCT ITE.CODPROD) QTDSKU
                            FROM TGFCAB CAB
                           INNER JOIN  TGFITE ITE
                              ON ITE.NUNOTA = CAB.NUNOTA
                           INNER JOIN TGFVEN VEN
                              ON VEN.CODVEND = CAB.AD_CODVEND   
                           INNER JOIN TGFTOP TOP 
                              ON TOP.CODTIPOPER = CAB.CODTIPOPER 
                             AND TOP.DHALTER = CAB.DHTIPOPER 
                           WHERE TOP.GOLSINAL = -1 
                             AND VEN.ATIVO = 'S'
                             AND VEN.AD_HIERARQUIA IN (25, 30)
                             AND VEN.AD_CODCOR IN (:P_CODCOR)
                             AND CAB.AD_CODVEND IN DECODE(:P_CODVEND, 0, CAB.AD_CODVEND, :P_CODVEND)
                             AND CAB.DTNEG BETWEEN :P_PERIODO.INI AND :P_PERIODO.FIN
                             AND CAB.STATUSNOTA = 'L'
                             AND TOP.GRUPO IN('VENDAS')
                        </snk:query> 

                    </head>

                    <style>
                      h4 {
                        margin-top: 5px;
                        text-align: center;
                        margin-bottom: -5px;
                      }
                        body {
                          font-family: "Poppins", sans-serif;
                          background-color: #fafafa;
                          margin: 0 auto;
                          padding: 0px;
                          }
                        .row {
                          display: flex;
                          justify-content: center;
                          align-items: center;
                          margin-top: -5px;
                        }
                        .card {
                          border-radius: 5px;
                          box-shadow: 7px 7px 13px 0px rgba(50, 50, 50, 0.22);
                          padding: 5px;
                          margin: 5px 10px 10px 10px;
                          width: 400px;
                          transition: all 0.3s ease-out;
                        }
                    
                        .card:hover {
                          transform: translateY(-5px);
                          cursor: pointer;
                        }
                     
                        .card p {
                          color: #a3a5ae;
                          font-size: 16px;
                        }
                        
                        .image {
                          float: right;
                          max-width: 64px;
                          max-height: 64px;
                        }
                        .blue {
                          border-left: 3px solid #4895ff;
                        }
                        
                        .green {
                          border-left: 3px solid #3bb54a;
                        }
                        
                        .red {
                          border-left: 3px solid #b3404a;
                        }
                        .black {
                          border-left: 3px solid rgb(168, 168, 168);
                        }
                        .purple {
                          border-left: 3px solid rgb(63, 35, 192);
                        }
                        
                        /* Add some padding inside the card container */
                        .container {
                          padding: 2px 8px;
                        }
                        
                        /*Imagem circurlar*/
                        
                        img    {
                          border-radius: 7px;
                          max-width: 120px;
                          max-height: 120px;
                          margin-left: auto;
                          margin-right: auto;
                          display: block;
                        }
                        
                        h3 {
                          color: black;
                        }
                        /*menu*/
                        #menu-h{
                          margin: 0 auto;
                          padding: 0;
                          background-color: #000000;
                        }
                        
                        #menu-h ul {
                          max-width: 1200px;
                          list-style: none;
                          padding: 0;
                        }
                        
                        #menu-h ul li {
                          display: inline;
                        }
                        
                        #menu-h ul li  {
                          color: #FFF;
                          padding: 13px;
                          display: inline-block;
                          text-decoration: none;
                          transition: background .4s;
                        }
                        #menu-h ul li b  {
                          color: rgb(255, 255, 255);   
                        }
                        
                        #menu-h ul li :hover {
                          color: #F38B00;
                        }
                      </style>
                      
                      <link
                          href="https://fonts.googleapis.com/css2?family=Poppins:ital,wght@0,200;0,400;0,600;1,200;1,400;1,600&display=swap"
                          rel="stylesheet"/>
                    </head>
                    <body>
                       <h4>
                          <c:forEach items="${vendedor.rows}" var="row">
                              ${row.APELIDO}
                          </c:forEach>
                  </h4>
                        <nav id="menu-h">
                            <ul>
                                <li>Tarefas no Prazo <b><c:forEach items="${tarefas.rows}" var="row">${row.NO_PRAZO}</c:forEach></b></li>
                                <li>Fora do Prazo <b><c:forEach items="${tarefas.rows}" var="row">${row.FORA_PRAZO}</c:forEach></b></li>
                                <li>Não Cumpriu <b><c:forEach items="${tarefas.rows}" var="row">${row.NAO_CUMPRIU}</c:forEach></b></li>
                                <li>Aguard. Agendamento <b><c:forEach items="${tarefas.rows}" var="row">${row.AG_AGENDAMENTO}</c:forEach></b></li>
                                <li>Não Classificado <b><c:forEach items="${tarefas.rows}" var="row">${row.NAO_CLASSIFICADO}</c:forEach></b></li>
                            </ul>
                        </nav>   
                    
                    <br>
                    <div class="row">
                      <div class="card blue">
                        <h3><span style="margin-top: 2px">Vendas</span></h3>
                        <p>
                            <c:forEach items="${vendas.rows}" var="row">
                                ${row.VALOR}
                            </c:forEach>

                        </p>
                    </div>
                 
                      <div class="card green">
                        <h3>Faturamento</h3>
                        <p>
                            <c:forEach items="${faturamento.rows}" var="row">
                                ${row.VALOR}
                            </c:forEach>
                        </p>
                      </div>
                 
                      <div class="card red">
                        <h3>Devoluções</h3>
                        <p>
                          <c:forEach items="${devolucao.rows}" var="row">
                            ${row.VALOR}
                          </c:forEach>
                        </p>
                      </div>
                      <div class="card purple">
                        <h3>Total Líquido </h3>
                        <p>
                          <c:forEach items="${faturamentoliquido.rows}" var="row">
                            ${row.VALOR}
                          </c:forEach></p>

                        </p>
                      </div>
                    </div>
                    </div>
                
                    <div class="row">
                        <div class="card black">
                          <h3><span style="margin-top: 2px">Mix de Produtos</span></h3>
                          <p><c:forEach items="${mix.rows}" var="row">
                            ${row.QTDSKU}
                        </c:forEach></p>
                      </div>
                   
                        <div class="card black">
                          <h3>Prazo</h3>
                          <p><c:forEach items="${prazomedio.rows}" var="row">
                            ${row.PRAZO}
                        </c:forEach>
                      </p>
                        </div>
                   
                        <div class="card black">
                          <h3>Preço Médio</h3>
                          <p><c:forEach items="${precomedio.rows}" var="row">
                            ${row.AVGREGISTROS}
                        </c:forEach>
                      </p>
                        </div>
                        <div class="card black">
                          <h3>Positivações</h3>
                          <p><c:forEach items="${vendas.rows}" var="row">
                            ${row.QTDREGISTROS}
                        </c:forEach></p>
                        </div>
                      </div>
                  
                      </div>  
                      
                    
                    
                        
                    </body>
				</html>