package IntegracaoLincros;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import com.sankhya.util.SessionFile;
import com.sankhya.util.UIDGenerator;

import Util.MensagemExceptionUtil;
import Util.PreferenciaSistemaUtil;
import br.com.sankhya.ws.ServiceContext;

public abstract class IntegracaoLincros {
	
	public static void conexaoHTTP(byte[] arquivozip) throws Exception {
		
		
		int contentLength = arquivozip.length;
		String boundary = "--------------------------" + System.currentTimeMillis();
		URL url = new URL("https://deployment.transpofrete.com.br/api//importacaoXML/upload");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", "Bearer " + PreferenciaSistemaUtil.getOperador());
		connection.setRequestProperty("User-Agent", "PostmanRuntime/7.29.2");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
		connection.setRequestProperty("Content-Length", Integer.toString(contentLength));
		connection.setRequestProperty("Host", "deployment.transpofrete.com.br");
		
		connection.setDoOutput(true);
		String encodedFile;
		try (OutputStream os = connection.getOutputStream();) {
			encodedFile = Base64.getEncoder().encodeToString(arquivozip); 
			os.write(encodedFile.getBytes());
			os.flush();
		}
		
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			String chave = "text_" + UIDGenerator.getNextID();
			SessionFile sessionFile = SessionFile.createSessionFile("arquivo.zip", "zip", arquivozip);
			ServiceContext.getCurrent().putHttpSessionAttribute(chave, sessionFile);
			throw new Exception(MensagemExceptionUtil.getMensagemResponseCode(connection.getResponseCode())
					+ "<br>"
					+ "<br>--------- DADOS DA REQUISIÇÃO ---------"
					+ "<br>- Tipo de Dado enviado: " + encodedFile.getClass()
					+ "<br>- Arquivo em Base64: " + encodedFile
					+ "<br>- Tamanho do Arquivo ZIP: " + Integer.toString(contentLength) 
					+ "<br>- Bondary: " + boundary 
					+ "<br/>" 
					+ "<a id=\"alink\" href=\"/mge/visualizadorArquivos.mge?chaveArquivo=" + chave + "\" target=\"_blank\">Baixar Arquivo "

		 								
		 // 
		 // contexto.setMensagemRetorno();
					
			);
		} 
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
			StringBuilder retornoAPI = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				retornoAPI.append(responseLine.trim());
			}
				
			System.out.println("Retorno API: " + retornoAPI  + ", status code: " + connection.getResponseCode());
			br.close();
		} 
	}
	
}
