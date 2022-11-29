package br.IntegracaoLinCros;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;

import com.sankhya.util.TimeUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;



public class IntegracaoLincros {
	public EntityFacade dwf;
	public JdbcWrapper jdbc = null;
	private String getOperador() throws Exception {
		String token = "";
		Collection<DynamicVO> filtroOperador = (Collection<DynamicVO>) 
				EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(
				new FinderWrapper(DynamicEntityNames.EMPRESA, "this.CODEMP = 1"));
		
		for (DynamicVO linha : filtroOperador) 
		{
			if (linha.asString("AD_TOKEN_LINCROS") == null ) {
				throw new NullPointerException("Não foi definido o token de acesso para a LINCROS na empresa 1");
			}
			token = linha.asString("AD_TOKEN_LINCROS");
		}
		return token;
	}
		
	public void conexaoLincros () throws Exception {
		URL url = new URL("https://deployment.transpofrete.com.br/api/v3/calculo/calcularNota");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", "Bearer " + getOperador());
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Content-Length", "<calculated when request is sent>");
		connection.setRequestProperty("Host", "<calculated when request is sent>");
		connection.setDoOutput(true);
		String bodyResquest = "{\r\n\t\"cnpjUnidade\": \"32463085000130\",\r\n\t\"remetente\": \"32463085000130\",\r\n\t\"peso\": 255,\r\n\t\"cubagem\": 0,\r\n\t\"pesoCubado\": 0,\r\n\t\"valor\": 5300,\r\n\t\"volumes\": 4,\r\n\t\"abono\": 0,\r\n\t\"percentualValorCliente\": 0,\r\n\t\"cepOrigem\": 26168081,\r\n\t\"cepDestino\": 68440000,\r\n\t\"data\": \"2022-11-25\",\r\n\t\"modalidadeFrete\": 0,\r\n\t\"tipoOperacao\": 1,\r\n\t\"transportadora\": \"\",\r\n\t\"placa\": \"\"\r\n}";
		
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
		}		
	}
	private void inserirLog(BigDecimal nufin, BigDecimal codParc, BigDecimal codUsuLogado, String operacao) throws Exception  {

		try {	   	 
	            this.dwf = EntityFacadeFactory.getDWFFacade();
	            DynamicVO logVo = (DynamicVO) this.dwf.getDefaultValueObjectInstance("AD_LOGSERASA");  
	            jdbc = dwf.getJdbcWrapper();
	            jdbc.openSession();
	            
	            logVo.setProperty("NUFIN", nufin);
	            if (operacao != "00") {
	            	logVo.setProperty("CODOPERACAO", operacao);	
				} else {
					SimpleDateFormat format = new SimpleDateFormat("hh:mm:sss");
					logVo.setProperty("CODOPERACAO",format.format(TimeUtils.getNow()));
				} 
	            
	            logVo.setProperty("DTALTERACAO",TimeUtils.getNow());
	            logVo.setProperty("CODPARC", codParc);
	            logVo.setProperty("CODUSU", codUsuLogado);
	            logVo.setProperty("XML", xmlBody.toCharArray());
	            char [] xmlConvertido = xmlReturn.toCharArray();
	            logVo.setProperty("RETURNAPI", xmlConvertido );

	            PersistentLocalEntity createEntity = dwf.createEntity("AD_LOGSERASA", (EntityVO) logVo);
	            DynamicVO save = (DynamicVO) createEntity.getValueObject();
	            
	            System.out.println("Log de inclusão do serasa gravado com sucesso!");

	            // Atualização do campo Serasa no Financeiro
	                        
	            try {		     
	            	if (operacao != "00") {
			            FinderWrapper finderFinanceiro = new FinderWrapper("Financeiro", "NUFIN = " + nufin);
			        	Collection<PersistentLocalEntity> finderFinanceiroCPLE = this.dwf.findByDynamicFinder(finderFinanceiro);
			        	for (PersistentLocalEntity finderFinanceiroPLE  : finderFinanceiroCPLE) 
			        	{
			        		EntityVO finderFinanceiroEVO = finderFinanceiroPLE.getValueObject();
			        		DynamicVO financeiroVO = (DynamicVO) finderFinanceiroEVO;
			        		if (operacao == "I") {
			        			financeiroVO.setProperty("AD_SERASA", "S");	
							}else {
								financeiroVO.setProperty("AD_SERASA", "N");
							}
			        		
			        		finderFinanceiroPLE.setValueObject((EntityVO) financeiroVO);		
			        	}
	            	}
			    	
				} catch (Exception e) {
					
				}

	        } catch (Exception erro){
	        	throw new Exception("Não foi possível gravar o log da inclusão do serasa." + erro.toString());
	        	//msg.exibirErro("Não foi possível gravar o log da inclusão do serasa." + erro.toString(), null, null);
	        }
	        
	        finally {
	            jdbc.closeSession();
	        }
		}
	
}
