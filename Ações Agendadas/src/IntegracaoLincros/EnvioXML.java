package IntegracaoLincros;

import java.nio.file.Files;
import java.io.File;
import Util.ArquivosUtil;
import Util.CabecalhoNotaUtil;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

public class EnvioXML implements AcaoRotinaJava {
	@Override
	public void doAction(ContextoAcao contexto) throws Exception {

		System.out.println("Iniciando o envio de XML para a Lincros");
		
		CabecalhoNotaUtil cab = new CabecalhoNotaUtil();
		File file = ArquivosUtil.compactarZip(cab.consultaNFEmitidasDia());
		
		byte [] dados = Files.readAllBytes (file.toPath ());
		IntegracaoLincros.conexaoHTTP(dados);
		
		/********************************************************************************/
		 // O snipper abaixo é possível obter os dados 
		 // String chave = "text_" + UIDGenerator.getNextID();
		 // SessionFile sessionFile = SessionFile.createSessionFile("arquivo.zip", "zip", dados);								
		 // ServiceContext.getCurrent().putHttpSessionAttribute(chave, sessionFile);
		 // contexto.setMensagemRetorno("<a id=\"alink\" href=\"/mge/visualizadorArquivos.mge?chaveArquivo=" + chave + "\" target=\"_blank\">Baixar Arquivo ");
         /********************************************************************************/
		
		
        
		
				
	}

}
