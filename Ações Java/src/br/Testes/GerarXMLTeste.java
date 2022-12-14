package br.Testes;

import com.sankhya.util.SessionFile;
import com.sankhya.util.UIDGenerator;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class GerarXMLTeste implements AcaoRotinaJava {

	private static final String ServiceContext = null;

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		SessionHandle hnd = null;
		Registro[] registros = contexto.getLinhas();
		Registro r = registros[0];
		//nome arquivo sessao
		String chave = "text_" + UIDGenerator.getNextID();
		try {
			hnd = JapeSession.open();
			EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

			//byte array do arquivo que quer baixar
			byte[] fileContent = "teste teste teste teste teste teste teste teste teste teste teste teste teste teste teste teste teste testearquivoZIP".getBytes();

			//instancia o arquivo zip
			SessionFile sessionFile = SessionFile.createSessionFile("zip.txt", "text", fileContent);

			//sobe o arquivo para a sessao do sankhya para a tela poder baixar
			//ServiceContext.getCurrent().putHttpSessionAttribute(chave, sessionFile);
			
			if (1==1) {
				throw new Exception("O que tem no arquivo <br>" + sessionFile);
			}

		} finally {
			JapeSession.close(hnd);
		}

		contexto.setMensagemRetorno("<a id=\"alink\" href=\"/mge/visualizadorArquivos.mge?chaveArquivo=" 
		+ chave 
		+ "\" target=\"_top\">Baixar Arquivo veiculo codigo: "+ r.getCampo("CODVEICULO"));
		
	}

}
