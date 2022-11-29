package br.IntegracaoLinCros;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Collection;

import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class IntegracaoLincros {
	
	public String getOperador() throws Exception {
		String token = "";
		
		Collection<DynamicVO> filtroOperador = (Collection<DynamicVO>) 
				EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(
				new FinderWrapper(DynamicEntityNames.EMPRESA, "this.CODEMP = 1"));
		
		for (DynamicVO linha : filtroOperador) 
		{
			if (linha.asString("AD_TOKEN_LINCROS") == null) {
				throw new NullPointerException("Token não configurado para a empresa 1");
			}
			token = String.valueOf(linha.asString("AD_TOKEN_LINCROS"));
		}
		return token;
	}
	
	public void conexaoLincros (CabecalhoNotaVO nota) throws Exception {
		try {
			URL url = new URL("https://deployment.transpofrete.com.br/api/v3/calculo/calcularNota");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer " + getOperador());
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", "<calculated when request is sent>");
			connection.setRequestProperty("Host", "<calculated when request is sent>");
			connection.setDoInput(true);
			String jsonInputString = "{}";
			
			try(OutputStream os = connection.getOutputStream()) {
			    byte[] input = jsonInputString.getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}
			try(BufferedReader br = new BufferedReader(
					  new InputStreamReader(connection.getInputStream(), "utf-8"))) {
					    StringBuilder response = new StringBuilder();
					    String responseLine = null;
					    while ((responseLine = br.readLine()) != null) {
					        response.append(responseLine.trim());
					    }
			}
			
			
			
			if (connection.getResponseCode() <= 299) {
				throw new Exception("Conectou");
			} else {
				throw new Exception("Não Conectou");
			}
			
			
		} catch (Exception erro) {
			throw new Exception("Erro de Conexão: " + erro.toString());
		}
		 
	}
}
