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
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class IntegracaoLincros {
	private BigDecimal codParcTransportadora;
	private String nomeTransportadora = "";
	private String cnpjTransportadora = "";
	private boolean alterouTransportadora = false;
	
	private BigDecimal nunota;
	private String bodyResquest;
	
	public EntityFacade dwf;
	public JdbcWrapper jdbc = null;
	

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

	public void conexaoLincros(Registro[] registro) throws Exception {	
		BigDecimal valor = new BigDecimal(0);
		BigDecimal volumes = new BigDecimal(0); 
		BigDecimal peso = new BigDecimal(0);
		BigDecimal codparc = new BigDecimal(0);
		String cep = "";
	
		for (Registro cabecalhoNota : registro) {
			this.nunota = new BigDecimal(cabecalhoNota.getCampo("NUNOTA").toString());
			valor = new BigDecimal(cabecalhoNota.getCampo("VLRNOTA").toString());
			volumes = new BigDecimal(cabecalhoNota.getCampo("QTDVOL").toString());
			peso = new BigDecimal(cabecalhoNota.getCampo("PESO").toString());
			codparc = new BigDecimal(cabecalhoNota.getCampo("CODPARC").toString());
			
			ParceiroUtil transportadora = new ParceiroUtil();
			Collection<DynamicVO> traVo = transportadora.getParceiro(codparc) ;
			for (DynamicVO dado : traVo) {
				cep = dado.asString("CEP"); 
			}
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
							   + "\r\n\t\"cubagem\": 0,"
							   + "\r\n\t\"pesoCubado\": 0,"
							   + "\r\n\t\"valor\": " + valor + ","
							   + "\r\n\t\"volumes\": " + volumes + ","
							   + "\r\n\t\"abono\": 0,"
							   + "\r\n\t\"percentualValorCliente\": 0,"
							   + "\r\n\t\"cepOrigem\": 26168081,"
							   + "\r\n\t\"cepDestino\": " + cep + ","
							   + "\r\n\t\"modalidadeFrete\": 0,"
							   + "\r\n\t\"tipoOperacao\": 1,"
							   + "\r\n\t\"transportadora\": \"\","
							   + "\r\n\t\"placa\": \"\"\r\n}";

		try(OutputStream os = connection.getOutputStream()) {
		    byte[] input = bodyResquest.getBytes("utf-8");
		    os.write(input, 0, input.length);			
		}
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				        response.append(responseLine.trim());
			}
			inserirLog(responseLine);
		}
	}
	private BigDecimal getParcTransportadora (String cnpj) throws Exception {
		NativeSql sql = new NativeSql(jdbc);
		sql.setNamedParameter("P_CNPJ", cnpj);
		sql.appendSql("SELECT CODPARC FROM TGFPAR WHERE CGC_CPF = :P_CNPJ");
		ResultSet rs = sql.executeQuery();
		
		while (rs.next()) {
			this.codParcTransportadora = rs.getBigDecimal("CODPARC");
		}
		return this.codParcTransportadora;
	}
	
	private void setTransportadoraCabecalhoPedido() throws Exception {
		FinderWrapper finderCabecalhoNota = new FinderWrapper(DynamicEntityNames.CABECALHO_NOTA, "this.NUNOTA = ", new Object[] {this.nunota});
     	Collection<PersistentLocalEntity> finderCabecalhoCPLE = this.dwf.findByDynamicFinder(finderCabecalhoNota);
     	
     	for (PersistentLocalEntity finderFinanceiroPLE  : finderCabecalhoCPLE) 
     	{
     		EntityVO cabEntityVO = finderFinanceiroPLE.getValueObject();
     		DynamicVO cabVO = (DynamicVO) finderFinanceiroPLE;
     		if (alterouTransportadora == true) {
     			cabVO.setProperty("CODPARCTRANSP", this.codParcTransportadora);
			}
     		else {
     			cabVO.setProperty("AD_TRANSPORTADORASUGERIDA", this.cnpjTransportadora + " - " + this.nomeTransportadora);	
     		}
     		
     		finderFinanceiroPLE.setValueObject((EntityVO) cabVO);
     	}
	}
	
	private void inserirLog(String json) throws Exception {

		try {
			JSONObject jsonRetorno = new JSONObject(json.toString());
            JSONArray jsonArray =  jsonRetorno.getJSONArray("transportadoras");
            this.dwf = EntityFacadeFactory.getDWFFacade();
			DynamicVO logVo = (DynamicVO) this.dwf.getDefaultValueObjectInstance("AD_COTFRE");
			jdbc = dwf.getJdbcWrapper();
			jdbc.openSession();
			if (1==1) {
				throw new Exception("Vencemo familia!");
			}
            for (int i = 0; i < jsonArray.length(); i++) {
            	JSONObject dadosTransportadora = jsonArray.getJSONObject(i);
            	
            	this.cnpjTransportadora = dadosTransportadora.getString("cnpj");
            	this.nomeTransportadora = dadosTransportadora.getString("nome");
            	
            	if (i == 0 && this.codParcTransportadora.signum() == 0 ) {
            		setTransportadoraCabecalhoPedido();
				}
            	
            	if (codParcTransportadora.signum() > 0) {
            		if (!this.alterouTransportadora) {
            			setTransportadoraCabecalhoPedido();
            			this.alterouTransportadora = true;
    				}	
				}
            	
            	logVo.setProperty("NUNOTA", this.nunota);
				logVo.setProperty("DTCONSULTA",TimeUtils.getNow());
            	logVo.setProperty("CNPJ", cnpjTransportadora);
            	logVo.setProperty("JSONREQUEST", this.bodyResquest);
            	logVo.setProperty("JSONRESPONSE", json);
            	logVo.setProperty("VALOR", dadosTransportadora.getDouble("valor"));
            	
            	PersistentLocalEntity createEntity = dwf.createEntity("AD_COTFRE", (EntityVO) logVo);
    			DynamicVO save = (DynamicVO) createEntity.getValueObject();
			}
            

		} catch (Exception erro) {
			throw new Exception("Não foi possível gravar o log da Lincros" + erro.toString());
		}

		finally {
			jdbc.closeSession();
		}
	}

}
