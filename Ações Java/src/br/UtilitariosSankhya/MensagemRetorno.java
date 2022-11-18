package br.UtilitariosSankhya;

import br.com.sankhya.jape.PersistenceException;

public class MensagemRetorno {
	public void exibirErro(String mensagem, String motivo, String solucao) throws Exception  {
		String contexto = mensagem;
		
		if (motivo != null) {
			contexto += "<br><br><b>Motivo: </b>" + motivo + ".<br><br>"; 
		}
		if (solucao != null) {
			contexto += "<br><b>Solução: </b>" + solucao + ".<br><br>";
		}
		
		throw new PersistenceException("<p align=\"center\"><a href=\"http://www.sankhya.com.br\" target=\"_blank\"><img src=\"http://192.168.1.248:8180/mge/imagens@IMAGEM@ID=75.dbimage\" height=\"40\" width=auto style = \"float:auto;\"></a></p><br/><p align=\"left\"><font size=\"2\" face=\"arial\" color=\"#8B1A1A\"><b>Atenção:  </b>" +
										contexto + 
	                                  "<p align=\"center\"><font size=\"2\" color=\"#008B45\"><b>Informações para a equipe de tecnologia - CIVITT</b></font>"                      
				) ;
	}
}
