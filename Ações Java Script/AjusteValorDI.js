var vlrDI     = getParam("VLRDI");
var NCM       = getParam("NCM");

var aliquota  ;
var aliquotaCred ; 

var notaCompra = linhas[0];

    confirmar('Recalcular Impostos.', 'Deseja Recalcular impostos de PIS/COFINS?', 1);

    if(linhas.length > 1){
        mostraErro('<b>Atenção!</b><br>Selecione apenas 1 registro!');
    }

    if(notaCompra.getCampo("TIPMOV") != "C"){
        mostraErro('<b>Atenção!</b><br>Rotina valida apenas para notas de compra!');	
    }

    // Quando a nota fiscal é iniciada o valor di recebe null 
    if(notaCompra.getCampo("AD_VLRDI") == null) {
        notaCompra.setCampo("AD_VLRDI",0);
        notaCompra.setCampo("AD_BASECOFINS",0);
        notaCompra.setCampo("AD_VLRCOFINS",0);
        notaCompra.setCampo("AD_BASEPIS",0);
        notaCompra.setCampo("AD_VLRPIS",0);
        notaCompra.setCampo("AD_PERCDI",0);
    }


    var totalItens = getQuery();
    totalItens.setParam("nunota", notaCompra.getCampo("NUNOTA")); 
    totalItens.setParam("NCM", NCM);


    totalItens.nativeSelect("SELECT NVL(MIN(AD_VLRDI), 0) AS AD_VLRDI,             "+
                            "       ROUND(SUM(NVL(ITE.BASEICMS, ITE.VLRTOT)), 2) AS BASEICMS, "+
                            "       CAB.NUNOTA,                                    "+ 
                            "       ITE.SEQUENCIA,                                 "+ 
                            " (SUM(ITE.BASEICMS) OVER (PARTITION BY ITE.SEQUENCIA) / SUM(ITE.BASEICMS) OVER ()) AS PERCENTUAL "+
                            " FROM TGFITE ITE  "+
                            "INNER JOIN TGFCAB CAB ON CAB.NUNOTA = ITE.NUNOTA "+
                            "INNER JOIN TGFPRO PRO ON PRO.CODPROD = ITE.CODPROD "+
                            "WHERE ITE.NUNOTA = {nunota} AND PRO.NCM = {NCM} "+
                            "GROUP BY ITE.CODPROD, ITE.BASEICMS, CAB.NUNOTA, ITE.SEQUENCIA");
    
    while(totalItens.next()){
        var base  = totalItens.getDouble("PERCENTUAL") * vlrDI ;

        var BaseItem = parseFloat(base.toFixed(4));
        
        var impostosItem = getQuery();
    
        
        impostosItem.setParam("nunota", totalItens.getInt("NUNOTA")); 
        impostosItem.setParam("sequencia", totalItens.getInt("SEQUENCIA")); 
    

        var consulta = 'SELECT DIN.ALIQUOTA, DIN.CODIMP, DIN.ALIQUOTANORMAL ';
            consulta += 'FROM TGFDIN DIN WHERE DIN.NUNOTA = {nunota} AND SEQUENCIA = {sequencia} AND DIN.CODIMP IN (6, 7)';
        
        impostosItem.nativeSelect(consulta);

        while(impostosItem.next()){
            
            var alter = getQuery();
            var valorCred;
            var valorCheio;

            if (impostosItem.getInt("CODIMP") == 7) 
            {
                percAliqCofins = getQuery();
                percAliqCofins.setParam("NCM", NCM);
    
                percAliqCofins.nativeSelect('SELECT NVL(AD_ALIQCOFINS,0) AD_ALIQCOFINS, NVL(AD_ALIQCOFINSCRED,0) AS AD_ALIQCOFINSCRED , CODNCM FROM TGFNCM WHERE CODNCM = {NCM}')
                
                while (percAliqCofins.next()) 
                {
                    if (percAliqCofins.getInt("AD_ALIQCOFINS")  == 0 && percAliqCofins.getInt("AD_ALIQCOFINS") == null) 
                    {
                    mostraErro("O NCM não possui valor de alíquota para COFINS");
                    }

                aliquota = percAliqCofins.getDouble("AD_ALIQCOFINS") / 100 ;
                aliquotaCred = percAliqCofins.getDouble("AD_ALIQCOFINSCRED") / 100;
                }
                valorCred = BaseItem * aliquotaCred;
                valorCheio = BaseItem * aliquota;

            }
            if (impostosItem.getInt("CODIMP") == 6)
            {

                aliquota = 0.0210;
                aliquotaCred  = 0.0210;

                valorCred = BaseItem * aliquotaCred;
                valorCheio =  BaseItem * aliquota;

            }
           alter.update("UPDATE TGFDIN SET BASE="  + BaseItem + 
                        ",ALIQUOTA              = " + aliquotaCred * 100 + 
                        ", ALIQUOTANORMAL       = " + aliquota * 100 + 
                        ",  BASERED             ="  + BaseItem     +
                        ", VALOR                ="  + valorCheio+
                        ", VLRCRED              ="  + valorCred+
                        " WHERE NUNOTA    ="+totalItens.getInt("NUNOTA")+
                        "   AND SEQUENCIA ="+totalItens.getInt("SEQUENCIA")+
                        "   AND CODIMP    ="+impostosItem.getInt("CODIMP"));

        } // ImpostoItem
    } // Total Itens
    
    consultaImpostos =  getQuery();
    consultaImpostos.setParam("nunota", notaCompra.getCampo("NUNOTA")); 

        var script =  'SELECT SUM(CASE WHEN CODIMP = 6 THEN BASE END) AS BASEPIS,  '+
                '       SUM(CASE WHEN CODIMP = 6 THEN VALOR END) AS VLRPIS,     '+
                '       SUM(CASE WHEN CODIMP = 7 THEN BASE END) AS BASECOFINS,  '+
                '       SUM(CASE WHEN CODIMP = 7 THEN VLRCRED END) AS VLRCOFINS   '+
                '       FROM TGFDIN                                             '+
                '       WHERE NUNOTA = {nunota}                                 '+
                '         AND CODIMP IN (6,7)                                   ';

    consultaImpostos.nativeSelect(script);

    while (consultaImpostos.next()) {
        var valorDIAux = notaCompra.getCampo("VLRNOTA");
        var valorDiNota = parseFloat(valorDIAux);
        

    notaCompra.setCampo("AD_BASEPIS"   ,  consultaImpostos.getDouble("BASEPIS"));
    notaCompra.setCampo("AD_VLRPIS"    ,  consultaImpostos.getDouble("VLRPIS"));
    notaCompra.setCampo("AD_BASECOFINS",  consultaImpostos.getDouble("BASECOFINS"));
    notaCompra.setCampo("AD_VLRCOFINS" ,  consultaImpostos.getDouble("VLRCOFINS"));
    notaCompra.setCampo("AD_VLRDI"     , consultaImpostos.getDouble("BASEPIS"));
    notaCompra.setCampo("AD_PERCDI",  (consultaImpostos.getDouble("BASEPIS") / valorDiNota) ); // % DI
        
    }

    mensagem = "Calculo Realizado com Sucesso!!";
