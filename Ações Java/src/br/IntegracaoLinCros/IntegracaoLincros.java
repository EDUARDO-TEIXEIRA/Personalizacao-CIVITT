package br.IntegracaoLinCros;

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
			String jsonInputString = "{
					"cnpjUnidade": "32463085000130",
					"remetente": "32463085000130",
					"peso": 255,
					"cubagem": 0,
					"pesoCubado": 0,
					"valor": 5300,
					"volumes": 4,
					"abono": 0,
					"percentualValorCliente": 0,
					"cepOrigem": 26168081,
					"cepDestino": 68440000,
					"data": "2022-11-25",
					"modalidadeFrete": 0,
					"tipoOperacao": 1,
					"transportadora": "",
					"placa": ""
				}

			connection.setDoInput(true);
			
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
