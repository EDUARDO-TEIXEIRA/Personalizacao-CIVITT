package br.IntegracaoLinCros;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sankhya.util.TimeUtils;

import br.UtilitariosSankhya.ParceiroUtil;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class IntegracaoLincros {
	private BigDecimal codParcTransportadora = BigDecimal.valueOf(0);
	private String nomeTransportadora = "";
	private String cnpjTransportadora = "";
	private boolean alterouTransportadora = false;
	private String transportadoraSugerida = "";
	private String expressaoConsulta = "";

	StringBuilder responseJsonAPI = new StringBuilder();
	
	private String bodyResquest;
	
	public EntityFacade dwf;
	public JdbcWrapper jdbc = null;
	
	public void setExpressaoConsulta(String expressaoConsulta) {
		this.expressaoConsulta = expressaoConsulta;
	}
	public String getExpressaoConsulta() {
		return expressaoConsulta;
	}
	private String getOperador() throws Exception {
		String token = "";
		Collection<DynamicVO> filtroOperador = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.EMPRESA, "this.CODEMP = 1"));

		for (DynamicVO linha : filtroOperador) {
			if (linha.asString("AD_TOKEN_LINCROS") == null) {
				throw new NullPointerException("Não foi definido o token de acesso para a LINCROS na empresa 1");
			}
			token = linha.asString("AD_TOKEN_LINCROS");
		}
		return token;
	}

	public void conexaoLincros(BigDecimal codCliente, BigDecimal valorNota, BigDecimal pesoProdutos, BigDecimal qtdVolume) throws Exception {	
		
		BigDecimal valor = valorNota;
		BigDecimal volumes = qtdVolume; 
		BigDecimal peso = pesoProdutos;
		BigDecimal codparc = codCliente;
		String cep = "";
	
		ParceiroUtil cliente = new ParceiroUtil();
		Collection<DynamicVO> traVo = cliente.getParceiro(codparc) ;
		for (DynamicVO dado : traVo) {
			cep = dado.asString("CEP"); 
		}

		URL url = new URL("https://ws-tms.lincros.com/api/v3/calculo/calcularNota");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", "Bearer " + getOperador());
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Content-Length", "<calculated when request is sent>");
		connection.setRequestProperty("Host", "<calculated when request is sent>");
		connection.setDoOutput(true);
		
		this.bodyResquest = "{\r\n\t\"cnpjUnidade\": \"32463085000130\","
							   + "\r\n\t\"remetente\": \"32463085000130\","
							   + "\r\n\t\"peso\": " + peso + ","
							   + "\r\n\t\"valor\": " + valor + ","
							   + "\r\n\t\"volumes\": " + volumes + ","
							   + "\r\n\t\"cepOrigem\": 29168081,"
							   + "\r\n\t\"cepDestino\": " + cep + ","
							   + "\r\n\t\"modalidadeFrete\": 0,"
							   + "\r\n\t\"tipoOperacao\": 1}";

		try(OutputStream os = connection.getOutputStream()) {
		    byte[] input = bodyResquest.getBytes("utf-8");
		    os.write(input, 0, input.length);			
		}
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
			
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				        responseJsonAPI.append(responseLine.trim());
			}
			if (HttpURLConnection.HTTP_OK != connection.getResponseCode()) {
				throw new Exception("Erro de Conexão com a Lincros");	
			} 
			br.close();
			
			inserirLog();
		} 
	}
	private BigDecimal getParcTransportadora (String cnpj) throws Exception {
		ParceiroUtil transportadora = new ParceiroUtil();
		Collection<DynamicVO> traVo = transportadora.getParceiro(cnpj);
		
		for (DynamicVO linha : traVo) {
			this.codParcTransportadora = linha.asBigDecimal("CODPARC");
		}
		return this.codParcTransportadora;
	}
	
	private void setTransportadoraCabecalhoPedido() throws Exception {
		this.dwf = EntityFacadeFactory.getDWFFacade();
		FinderWrapper finderCabecalhoNota = new FinderWrapper(DynamicEntityNames.CABECALHO_NOTA, "this.NUNOTA IN " + expressaoConsulta  );
		Collection<PersistentLocalEntity> finderCabecalhoCPLE = this.dwf.findByDynamicFinder(finderCabecalhoNota);
		
     	for (PersistentLocalEntity linha  : finderCabecalhoCPLE) 
     	{
     		this.codParcTransportadora = getParcTransportadora(this.cnpjTransportadora); // Procura uma transportadora
     		
     		EntityVO finderCabecalhoEVO = linha.getValueObject();
    		DynamicVO cabVO = (DynamicVO) finderCabecalhoEVO;

    		if (this.codParcTransportadora.signum() > 0) {
				this.alterouTransportadora = true;
			}
    		if (this.alterouTransportadora) {
    			cabVO.setProperty("CODPARCTRANSP", this.codParcTransportadora);	
			} else {
				cabVO.setProperty("AD_TRANSPORTADORASUGERIDA", this.transportadoraSugerida);
			}	
    		
    		linha.setValueObject((EntityVO) cabVO);

     	}
	}	
	private void inserirLog() throws Exception {
		String notasReplace = expressaoConsulta.replace("(", "").replace(")", "");
		BigDecimal codusulogado = (BigDecimal) JapeSessionContext.getRequiredProperty("usuario_logado");

		try {
			JSONObject jsonRetorno = new JSONObject(responseJsonAPI.toString());
            
            this.dwf = EntityFacadeFactory.getDWFFacade();
			DynamicVO logVo = (DynamicVO) this.dwf.getDefaultValueObjectInstance("AD_COTFRE");
			jdbc = dwf.getJdbcWrapper();
			jdbc.openSession();

			if (!jsonRetorno.getString("status").equals("CALCULADO")) {
				NativeSql sql = new NativeSql(jdbc);
            	
            	ResultSet rs = sql.executeQuery("SELECT NUNOTA FROM TGFCAB CAB WHERE NUNOTA IN " + expressaoConsulta);
				while (rs.next()) {
					logVo.setProperty("NUNOTA", rs.getBigDecimal("NUNOTA"));
					logVo.setProperty("SEQUENCIA", BigDecimal.valueOf(0));
					logVo.setProperty("DTCONSULTA", TimeUtils.getNow());

					logVo.setProperty("JSONREQUEST", this.bodyResquest.toCharArray());
					char[] jsonConvertido = responseJsonAPI.toString().toCharArray();
					logVo.setProperty("JSONRESPONSE", jsonConvertido);
					logVo.setProperty("CODUSU", codusulogado);
					PersistentLocalEntity createEntity = dwf.createEntity("AD_COTFRE", (EntityVO) logVo);
					DynamicVO save = (DynamicVO) createEntity.getValueObject();
				}
			}else {

				JSONArray jsonArray =  jsonRetorno.getJSONArray("transportadoras");
	            for (int i = 0; i < jsonArray.length(); i++) {
	            	
	            	JSONObject dadosTransportadora = jsonArray.getJSONObject(i);
	            	
	            	this.cnpjTransportadora = dadosTransportadora.getString("cnpj");
	            	this.nomeTransportadora = dadosTransportadora.getString("nome");
	            	// Inserir a transportadora várias vezes 
	            	NativeSql sql = new NativeSql(jdbc);
	            	ResultSet rs = sql.executeQuery("SELECT NUNOTA FROM TGFCAB CAB WHERE NUNOTA IN " + expressaoConsulta);
	            	
	            	while (rs.next()) {
	            		logVo.setProperty("NUNOTA", rs.getBigDecimal("NUNOTA"));
	            		logVo.setProperty("SEQUENCIA", BigDecimal.valueOf(i));
		            	logVo.setProperty("DTCONSULTA", TimeUtils.getNow());
		            	logVo.setProperty("CNPJ", cnpjTransportadora);
		            	logVo.setProperty("NOMETRANSPORTADORA", nomeTransportadora);
		            	logVo.setProperty("JSONREQUEST", this.bodyResquest.toCharArray());
		            	logVo.setProperty("PREVISAOENTREGA", dadosTransportadora.getString("previsaoEntrega"));
		            	char [] jsonConvertido = responseJsonAPI.toString().toCharArray();
		            	logVo.setProperty("JSONRESPONSE", jsonConvertido);
		            	logVo.setProperty("NUNOTAGROUP", notasReplace);
		            	logVo.setProperty("CODUSU", codusulogado);
		            	logVo.setProperty("VALOR", BigDecimal.valueOf(dadosTransportadora.getDouble("valor")));

		            	PersistentLocalEntity createEntity = dwf.createEntity("AD_COTFRE", (EntityVO) logVo);
		    			DynamicVO save = (DynamicVO) createEntity.getValueObject();
	            	}	

	            	if (!this.alterouTransportadora) {
	            		if (i == 0) {
	            			this.transportadoraSugerida = this.cnpjTransportadora + " - " + this.nomeTransportadora;	
						}
	            		setTransportadoraCabecalhoPedido();
					}
				}
			}

		} catch (Exception erro) {
			throw new Exception("Não foi possível gravar o log da Lincros<br/><b>ERRO:<b/> " + erro.toString());
		} 
		finally {
			jdbc.closeSession();
		}
	}
	
}
