package br.IntegracaoSerasa;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.Spring;

import com.sankhya.util.TimeUtils;

import br.UtilitariosSankhya.MensagemRetornoUtil;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.comercial.IFBlock;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class IntegracaoSankhyaSerasa {
	MensagemRetornoUtil msg = new MensagemRetornoUtil();
	public EntityFacade dwf;
	public JdbcWrapper jdbc = null;
	
	private BigDecimal codEmpresaOperador;
	private String xmlBody;
	private String xmlReturn; 
	
	public String getXmlBody() {
		return xmlBody;
	}
	public void setXmlBody(String xmlBody) {
		this.xmlBody = xmlBody;
	}
	public BigDecimal getCodEmpresaOperador() {
		return codEmpresaOperador;
	}
	public void setCodEmpresaOperador(BigDecimal codEmpresaOperador) {
		this.codEmpresaOperador = codEmpresaOperador;
	}

	public String getOperador(BigDecimal codemp) throws Exception {
		codEmpresaOperador = codemp;
		String token = "";
		String usuarioBase64 = "";

		Collection<DynamicVO> filtroOperador = (Collection<DynamicVO>) 
				EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(
				new FinderWrapper(DynamicEntityNames.EMPRESA, "this.CODEMP = ?", codemp));
		
		for (DynamicVO linha : filtroOperador) 
		{
			if (linha.asString("AD_SENHA") == null || linha.asString("AD_OPERADOR") == null) {
				throw new NullPointerException("Usuário CDL Serasa não está configurado para a empresa ");
			}
			token = String.valueOf(linha.asString("AD_OPERADOR") + ":" + linha.asString("AD_SENHA"));
		}
		return usuarioBase64 = Base64.getEncoder().encodeToString(token.getBytes());
	}
	
	public void getValidaEnvioSerasa(BigDecimal codUsuario) throws Exception {
		Collection<DynamicVO> filtroUsuarioVO = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.USUARIO, "this.CODUSU = ?", codUsuario));
		for (DynamicVO linha : filtroUsuarioVO) {
			if(linha.asString("AD_PERM_ENV_SERASA") == null) {
				throw new NullPointerException("O usuário não possui permissão para enviar títulos ao Serasa. ");
			} else if (linha.asString("AD_PERM_ENV_SERASA").equals("N") ) {
				throw new Exception("O usuário <b>" + linha.asString("NOMEUSU") + "</b> não possui permissão para enviar títulos ao Serasa: " + linha.asString("AD_PERM_ENV_SERASA"));
			} 
		}
	}
	
	private String getURLSerasa() throws Exception {
		String url = "";
		Collection<DynamicVO> filtropreferencias = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade().
				findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.PARAMETRO_SISTEMA, "this.CHAVE = 'AMBIENTESERASA'"));
		if (filtropreferencias.isEmpty()) {
			throw new Exception("Não foi encontrado url nas preferências do sistema");
		}
		for (DynamicVO linha : filtropreferencias) 
		{
			url = linha.asString("TEXTO");
		}
		return url;	
	}
	
	public void operacaoSerasa(BigDecimal nufin, BigDecimal codParc, BigDecimal codUsuLogado, String operacao) throws Exception {

		URL url = new URL(getURLSerasa() );
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		StringBuffer response = new StringBuffer();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", "Basic " + getOperador(codEmpresaOperador));
		connection.setRequestProperty("Content-Length", "<calculated when request is sent>");
		connection.setRequestProperty("Host", "<calculated when request is sent>");
		connection.setRequestProperty("User-Agent", "PostmanRuntime/7.29.2");
		connection.setRequestProperty("Accept", "application/xml?");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Content-Type", "text/xml charset=utf-8");
		
		connection.setDoOutput(true);
		connection.setReadTimeout(30000);
        connection.setConnectTimeout(30000);
		      
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(this.xmlBody);
        wr.flush();
        wr.close();        
		try {
			
			int responseCode = connection.getResponseCode();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			 if (responseCode == HttpURLConnection.HTTP_OK && 
				 responseCode == HttpURLConnection.HTTP_CREATED || 
				 responseCode == HttpURLConnection.HTTP_ACCEPTED) {
				 throw new Exception("Erro de conexão, veja mais informações: ");
			 }
			
			 
		     xmlReturn = in.readLine();
		     in.close();
		     
		     inserirLog(nufin, codParc, codUsuLogado, operacao);
		     
		} catch (Exception erro) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
			String resultado = in.readLine();
			xmlReturn = resultado;
			inserirLog(nufin, codParc, codUsuLogado, "00");
	        in.close();
	        
		} finally {
			connection.disconnect();
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
