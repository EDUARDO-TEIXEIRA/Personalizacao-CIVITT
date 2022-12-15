package IntegracaoLincros;

import java.nio.file.Files;

import org.apache.commons.fileupload.disk.DiskFileItem;

import com.sankhya.util.SessionFile;
import com.sankhya.util.UIDGenerator;

import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Util.CabecalhoNotaUtil;
import Util.MensagemExceptionUtil;
import Util.PreferenciaSistemaUtil;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.ws.ServiceContext;

public class EnvioXML implements AcaoRotinaJava {
	@Override
	public void doAction(ContextoAcao contexto) throws Exception {

		System.out.println("Iniciando o envio de XML para a Lincros");
		
		CabecalhoNotaUtil cab = new CabecalhoNotaUtil();
		File file = Util.ArquivosUtil.compactarZip(cab.consultaNFEmitidasDia());
		
		byte [] dados = Files.readAllBytes (file.toPath ());
		
		//IntegracaoLincros.conexaoHTTP(dados);
		
		/*Snipper*/
		int contentLength = dados.length;
		String boundary = "--------------------------" + System.currentTimeMillis();
		URL url = new URL("https://deployment.transpofrete.com.br/api//importacaoXML/upload");

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", "Bearer " + PreferenciaSistemaUtil.getOperador());
		connection.setRequestProperty("User-Agent", "PostmanRuntime/7.29.2");
		// Aparentemente parâmetros opicionais 
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Cache-Control", "no-cache");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		connection.setRequestProperty("Accept", "*/*");
		
		connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
		connection.setRequestProperty("Content-Length", Integer.toString(contentLength));
		connection.setRequestProperty("Host", "deployment.transpofrete.com.br");
		
		connection.setDoOutput(true);
		String encodedFile = "";		

		try (OutputStream os = new DataOutputStream(connection.getOutputStream());) {
			// Pode ser uma conversão de base 64 pois é necessário enviar uma string, mas não faz muito sentido. 			
			//encodedFile = Base64.getEncoder().encodeToString(dados); 
			//os.write(encodedFile.getBytes());
			
			os.write(("key=arquivo" + "&value=" + dados).getBytes());
			os.flush();
		} 	
				
		finally {	
			String chave = "text_" + UIDGenerator.getNextID();
			SessionFile sessionFile = SessionFile.createSessionFile("arquivo.zip", "zip", dados);								
			ServiceContext.getCurrent().putHttpSessionAttribute(chave, sessionFile);
			String mensagem = MensagemExceptionUtil.getMensagemResponseCode(connection.getResponseCode());
						
			mensagem += "<br>"
			+ "<br><b>--------- DADOS DA REQUISIÇÃO ---------</b>"
			+ "<br>- Tipo de Dado enviado: " + encodedFile.getClass()
			+ "<br>- Requisição Token Sistema: " + PreferenciaSistemaUtil.getOperador()
			+ "<br/>"
			+ "<br><b>--------- CORPO DA REQUISIÇÃO ---------</b>"
			+ "<br>- Authorization: " + connection.getRequestProperty("Authorization")
			+ "<br>- Content-Type: " + connection.getRequestProperty("Content-Type")
			+ "<br>- Cache-Control: " + connection.getRequestProperty("Cache-Control")
			+ "<br>- Content-Length: " + connection.getRequestProperty("Content-Length")
			+ "<br/>"
			; 
			
			contexto.setMensagemRetorno(mensagem + "<a id=\"alink\" href=\"/mge/visualizadorArquivos.mge?chaveArquivo=" + chave + "\" target=\"_blank\">Baixar Arquivo ZIP");
		}
		
		
				 
	}

}
