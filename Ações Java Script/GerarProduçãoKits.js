var recImposto = newJava("br.com.sankhya.modelcore.comercial.impostos.ImpostosHelpper");
var qtdKitProducao = getParam("QTDKITPROD");
var simpleDateFormat = newJava("java.text.SimpleDateFormat");
simpleDateFormat.applyPattern("dd/MM/yyyy");
var dataAtual = simpleDateFormat.format(new Date());
var dateFormatHour = newJava("java.text.SimpleDateFormat");
dateFormatHour.applyPattern("dd/MM/yyyy HH:mm:ss");

if (qtdKitProducao <= 0) {
    mostraErro("Informe uma quantidade de kit acima de 0.");
    
}

var nuTab = getQuery(); 
nuTab.nativeSelect("SELECT CODTAB FROM TGFPAR WHERE CODPARC = 4");
if (nuTab.next()) {
    var codtabela = nuTab.getInt("CODTAB");
    if (codtabela == null) {
        mostraErro("O cliente [4] não possui tabela de preço cadastrada")
    } 
}

var v_QueryDhTipOperMP = getQuery(); 
v_QueryDhTipOperMP.nativeSelect("SELECT MAX(DHALTER) DHALTER FROM TGFTOP TOP WHERE CODTIPOPER = 1052");

if(v_QueryDhTipOperMP.next()) {

    var v_dataAltTopSaida = dateFormatHour.format(v_QueryDhTipOperMP.getDate("DHALTER"));
}

v_QueryDhTipOperMP.close();

var v_QueryDhTipOperPA = getQuery(); 
v_QueryDhTipOperPA.nativeSelect("SELECT MAX(DHALTER) DHALTER FROM TGFTOP TOP WHERE CODTIPOPER = 1352");

if(v_QueryDhTipOperPA.next()) {
    var v_dataAltTopEntrada = dateFormatHour.format(v_QueryDhTipOperPA.getDate("DHALTER"));
}
v_QueryDhTipOperPA.close();


for(var i = 0; i < linhas.length; i++){
    var linha = linhas[i];
    /*********************************************************************************
     * Cabeçalho da Entrada do Produto MP
     *********************************************************************************/
    var notaProdutoMP = novaLinha("TGFCAB");

    notaProdutoMP.setCampo("CODEMP", 1);
    notaProdutoMP.setCampo("CODTIPOPER", 1052);
    notaProdutoMP.setCampo("DHTIPOPER", v_dataAltTopSaida);
    notaProdutoMP.setCampo("DTNEG", dataAtual);
    notaProdutoMP.setCampo("DTALTER", dataAtual);
    notaProdutoMP.setCampo("CIF_FOB", "C");
    notaProdutoMP.setCampo("CODPARC", 4);
    notaProdutoMP.setCampo("TIPMOV", "P");
    notaProdutoMP.setCampo("NUMNOTA", 0);
    notaProdutoMP.setCampo("CODNAT", 3010101);
    notaProdutoMP.setCampo("CODCENCUS",10301);
    notaProdutoMP.setCampo("CODTIPVENDA", 100);
    notaProdutoMP.setCampo("OBSERVACAO", "KIT");
    
    notaProdutoMP.save();

    var produtosAlternativos = getQuery();
    produtosAlternativos.setParam("CODPROD", linha.getCampo("CODPROD"));
    
    produtosAlternativos.nativeSelect("SELECT ROWNUM, PAL.CODPRODALT, " +
                       "                      TRUNC(SYSDATE) AS DTNEG," +
                       "              PRO.CODVOL,  " + 
                       "              AD_QTDREF," +
                       "              (SELECT FUN_CVT_CUSTO_MEDIO_COM_ICMS(PAL.CODPRODALT, NULL, 1) FROM DUAL) AS CUSMEDICM,"+
                       "              NVL((SELECT SUM (EST.ESTOQUEVOLPAD-EST.SAIDASPEND+ENTRADASPEND) "+
                       "              FROM TGWEST EST "+
                       "             INNER JOIN TGWEND ENDE "+
                       "                ON (ENDE.CODEND=EST.CODEND) " +
                       "             WHERE (EST.CODPROD = PRO.CODPROD) "+
                       "               AND (ENDE.DESCREND LIKE '%01%' "+
                       "                OR ENDE.DESCREND LIKE '%BLOCADO%' "+
                       "                OR ENDE.DESCREND LIKE '%02%') "+
                       "               AND ENDE.BLOQUEADO = 'N'),0) AS SALDOESTOQUE  "+
                       "  FROM TGFPAL PAL "+
                       " INNER JOIN TGFPRO PRO ON "+ 
                       " PRO.CODPROD = PAL.CODPRODALT "+
                       " WHERE PAL.CODPROD = {CODPROD}");
    var registros = new Array();
    while(produtosAlternativos.next())
    {
        if (produtosAlternativos.getBigDecimal("SALDOESTOQUE") == 0) {
            registros.push(produtosAlternativos.getInt("CODPRODALT"))
        }
        else if (produtosAlternativos.getBigDecimal("SALDOESTOQUE") < (produtosAlternativos.getInt("AD_QTDREF") * qtdKitProducao) ) {
            registros.push(produtosAlternativos.getInt("CODPRODALT"))
        }

    var registroProdutoMP = novaLinha("TGFITE");
    registroProdutoMP.setCampo("NUNOTA", notaProdutoMP.getCampo("NUNOTA"));
    registroProdutoMP.setCampo("SEQUENCIA", produtosAlternativos.getInt("ROWNUM"));
    registroProdutoMP.setCampo("CODPROD", produtosAlternativos.getInt("CODPRODALT"));
    registroProdutoMP.setCampo("CODLOCALORIG", 10000000);
    registroProdutoMP.setCampo("VLRUNIT", produtosAlternativos.getBigDecimal("CUSMEDICM"));
    registroProdutoMP.setCampo("QTDNEG", qtdKitProducao * produtosAlternativos.getInt("AD_QTDREF"));
    registroProdutoMP.setCampo("CODVOL", produtosAlternativos.getString("CODVOL"));
    registroProdutoMP.setCampo("VLRTOT", registroProdutoMP.getCampo("VLRUNIT") * registroProdutoMP.getCampo("QTDNEG"));
     
    registroProdutoMP.save();

    recImposto.setForcarRecalculo(true);
    recImposto.calcularImpostos(registroProdutoMP.getCampo("NUNOTA"));

    }
    var totalItens = getQuery();
    totalItens.nativeSelect("SELECT SUM(VLRTOT) AS VLRTOT FROM TGFITE WHERE NUNOTA = " + registroProdutoMP.getCampo("NUNOTA"));
    while(totalItens.next()) {
        totalItensConvert = totalItens.getBigDecimal("VLRTOT");
    }
    totalItens.close();

    var alterNota = getQuery();
    alterNota.setParam("VLRTOTAL", totalItensConvert);
    alterNota.update("UPDATE TGFCAB SET VLRNOTA = {VLRTOTAL} WHERE NUNOTA = " + registroProdutoMP.getCampo("NUNOTA"));

    if (registros.length > 1) {    
        mostraErro("Existe produtos <b>sem estoque</b> para esta produção, códigos: ("+ registros.join(",") + ")");
    }
    
    if (registros.length == 1 ) {    
        mostraErro("O produto "+ registros[0] + " <b>não possui estoque suficiente para produção</b>.");
    }
    
    
    var produtoAcabado = getQuery();
    produtoAcabado.nativeSelect("SELECT ROWNUM, PRO.CODPROD AS CODPRODPA, "+
                                "       PRO.CODVOL "+
                                " FROM TGFPRO PRO "+
                                "WHERE PRO.CODPROD = " + linha.getCampo("CODPROD"));
    
    
    while(produtoAcabado.next()) {
        var notaProdutoPA = novaLinha("TGFCAB");

        notaProdutoPA.setCampo("CODEMP", 1);
        notaProdutoPA.setCampo("CODTIPOPER", 1352);
        notaProdutoPA.setCampo("DHTIPOPER", v_dataAltTopEntrada);
        notaProdutoPA.setCampo("DTNEG", dataAtual);
        notaProdutoPA.setCampo("DTALTER", dataAtual);
        notaProdutoPA.setCampo("CIF_FOB", "C");
        notaProdutoPA.setCampo("CODPARC", 4);
        notaProdutoPA.setCampo("TIPMOV", "O");
        notaProdutoPA.setCampo("NUMNOTA", 0);
        notaProdutoPA.setCampo("CODNAT", 3010101);
        notaProdutoPA.setCampo("CODCENCUS",10301);
        notaProdutoPA.setCampo("CODTIPVENDA", 100);
        notaProdutoPA.setCampo("OBSERVACAO", "ENTRADA SIMBOLICA PARA TROCA DE UNIDADE DE MEDIDA REF");
        notaProdutoPA.setCampo("VLRNOTA", totalItensConvert);
        notaProdutoPA.setCampo("AD_NUNOTAORIGEM", notaProdutoMP.getCampo("NUNOTA"));
        notaProdutoPA.save();

        var registroProdutoPA = novaLinha("TGFITE");

        registroProdutoPA.setCampo("NUNOTA", notaProdutoPA.getCampo("NUNOTA"));
        registroProdutoPA.setCampo("SEQUENCIA", 1);
        registroProdutoPA.setCampo("CODPROD", linha.getCampo("CODPROD"));
        registroProdutoPA.setCampo("CODLOCALORIG", 10000000);
        registroProdutoPA.setCampo("VLRUNIT", totalItensConvert);
        registroProdutoPA.setCampo("QTDNEG", qtdKitProducao);
        registroProdutoPA.setCampo("VLRTOT", totalItensConvert);
        registroProdutoPA.setCampo("CODVOL", produtoAcabado.getString("CODVOL"));
        registroProdutoPA.save();    
        
        recImposto.setForcarRecalculo(true);
        recImposto.calcularImpostos(registroProdutoPA.getCampo("NUNOTA"));
        
    } 
    produtoAcabado.close();
    produtosAlternativos.close(); 


  mensagem = 'Registros criado com sucesso!\n' + 
             'Nro Único pedido de Venda ' + notaProdutoMP.getCampo("NUNOTA")  
             + '\nNro Único pedido de Compra ' + notaProdutoPA.getCampo("NUNOTA")
             ;
}
