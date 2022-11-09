package br.IntegracaoSerasa;

import java.awt.PageAttributes.MediaType;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;


public class RemoveTitulosSerasa implements AcaoRotinaJava{
	private BigDecimal nuNota;
	private BigDecimal numNota ;
	private BigDecimal nuFin;
	private BigDecimal codEmp ;
	private BigDecimal codParc;
	private String cgc_cpf;
	private String razaoSocial;
	private String nomeFantasia;
	private BigDecimal codUsuLogado;
	private EntityFacade dwf;
	private StringBuffer mensagem;
	
	
	@Override
	public void doAction(final ContextoAcao contexto) throws Exception {
		this.codUsuLogado = contexto.getUsuarioLogado();
		
		Registro[] linhas = contexto.getLinhas();
		if (linhas.length == 0) {
			contexto.setMensagemRetorno("Operação inválida! Nenhuma linha selecionada!");
		}
		
		for (Registro linha : linhas) 
		{
			Calendar dtInicio = new GregorianCalendar();
	        dtInicio.setTime((Date) linha.getCampo("DTVENC"));
	        Calendar dtFinal = Calendar.getInstance();
	        dtFinal.setTime(dtFinal.getTime());
	        
	        Long difMillis = dtFinal.getTimeInMillis() - dtInicio.getTimeInMillis();
	        Long diffDias = TimeUnit.DAYS.convert(difMillis, TimeUnit.MILLISECONDS);
	        
			BigDecimal nuFin = (BigDecimal) linha.getCampo("NUFIN");
			this.nuNota = (BigDecimal) linha.getCampo("NUNOTA");
			this.numNota = (BigDecimal) linha.getCampo("NUMNOTA");
			this.codParc = (BigDecimal) linha.getCampo("CODPARC");
			this.codEmp = (BigDecimal) linha.getCampo("CODEMP");
			
			if (linha.getCampo("PROVISAO").equals("S")) {
				throw new Exception("Não é possível enviar provisões ao Serasa");
			} else if (((BigDecimal) linha.getCampo("RECDESP")).intValue() != 1) {
				throw new Exception("Não é possível enviar despesas ou provisões ao Serasa ");
			} else if(linha.getCampo("AD_SERASA") != null) {
				if (linha.getCampo("AD_SERASA").equals("S")) {
					throw new Exception("O registro de número único " + nuFin + " já foi enviado ao Serasa.");
				}
			} else if(linha.getCampo("DHBAIXA") != null && linha.getCampo("CODTIPOPERBAIXA") != "0") {
				throw new Exception("O registro número único " + nuFin + " já foi baixado e não pode ser enviado ao Serasa.");
			} 
				getDiasEnvioSerasa(diffDias);
				getValidaEnvioSerasa(codUsuLogado);
				getValidaParceiro(this.codParc);
				getValidaStatusRastreioMercadoria(this.nuNota);
			
			final boolean confirmaOperacao = contexto.confirmarSimNao("Deseja continuar?", "Foram selecionado (s) " + linhas.length + " registro (s) para enviar ao Serasa.", 0);
			
			if (confirmaOperacao) {
				exclusaoSerasa();
			}
		}
	}
	private void getValidaEnvioSerasa(BigDecimal codUsuario) throws Exception {
		Collection<DynamicVO> filtroUsuarioVO = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.USUARIO, "this.CODUSU = ?", codUsuario));
		for (DynamicVO linha : filtroUsuarioVO) {
			if(linha.asString("AD_PERM_ENV_SERASA") == null) {
				throw new NullPointerException("O usuário não possui permissão para enviar títulos ao Serasa. ");
			} else if (linha.asString("AD_PERM_ENV_SERASA").equals("N") ) {
				throw new Exception("O usuário <b>" + linha.asString("NOMEUSU") + "</b> não possui permissão para enviar títulos ao Serasa: " + linha.asString("AD_PERM_ENV_SERASA"));
			} 
		}
	}
	
	private void getValidaParceiro(BigDecimal codParceiro) throws Exception {
		Collection<DynamicVO> filtroParceiroVO = (Collection<DynamicVO>) 
		EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(
		new FinderWrapper(DynamicEntityNames.PARCEIRO, "this.CODPARC = ?", codParceiro));
	
		for (DynamicVO linha : filtroParceiroVO) 
		{
			if (linha.asString("TIPPESSOA").equals("F")) {
				throw new Exception("Não é possível enviar registros ao Serasa de pessoas físicas.");
			}
			if (linha.asString("AD_IGNORAENVIOSERASA") != null) {
				if(linha.asString("AD_IGNORAENVIOSERASA").equals("N")) { 
					throw new Exception("O parceiro " + codParceiro + " - " + linha.asString("RAZAOSOCIAL").toString() + " não pode ser enviado ao Serasa!");
				}
			}  
			
			this.cgc_cpf = linha.asString("CGC_CPF");
			this.nomeFantasia = linha.asString("NOMEPARC");
			this.razaoSocial = linha.asString("RAZAOSOCIAL");
		}
	}
	
	private void getDiasEnvioSerasa(long diasVencimento) throws Exception {
		Collection<DynamicVO> filtroPreferenciaVO = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.PARAMETRO_SISTEMA, "this.CHAVE = 'QTDDIASSERASA'")); 
		for (DynamicVO linha : filtroPreferenciaVO) {
			if (linha.asInt("INTEIRO") > diasVencimento) {
				throw new Exception("O título possui " + diasVencimento + " dia (s) de vencimento e está abaixo do critério definido na preferências do sistema <b>'QTDDIASSERASA'</b>: " + linha.getProperty("INTEIRO") + " dias.");
			}
		}
	}
	
	private String getURLSerasa() throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql movSQL = null;
		String url = "";

		try {
			EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
			jdbc = dwf.getJdbcWrapper();
			jdbc.openSession();
			movSQL = new NativeSql(jdbc);
			
			movSQL.loadSql(getClass(), "consultasSQL/buscaAmbienteSerasa.sql");
			ResultSet result = movSQL.executeQuery();
			while (result.next()) {
				url = result.getString("TEXTO");
			}
			
		} finally {
			jdbc.closeSession();
		}
		return url;
	}
	
	private String getOperador(BigDecimal codEmpresa) throws Exception {
		String token = "";
		String usuarioBase64 = "";
		Collection<DynamicVO> filtroOperador = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(new FinderWrapper("Empresa", "this.CODEMP = ?", codEmpresa));
		for (DynamicVO linha : filtroOperador) 
		{
			if (linha.asString("AD_SENHA") == null || linha.asString("AD_OPERADOR") == null) {
				throw new NullPointerException("Usuário CDL Serasa não está configurado para a empresa " + codEmpresa);
			}
			token = String.valueOf(linha.asString("AD_OPERADOR") + ":" + linha.asString("AD_SENHA"));
		}
		return usuarioBase64 = Base64.getEncoder().encodeToString(token.getBytes());
	}
	private void getValidaStatusRastreioMercadoria(BigDecimal nunota) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		boolean status = false; 
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);
			sql.setNamedParameter("P_NUNOTA", this.nuNota);
			sql.appendSql("SELECT FUN_CVT_RASTREIO_PEDIDO(:P_NUNOTA) AS STATUS FROM DUAL");

			rset = sql.executeQuery();

			if (rset.next()) {
				status = "ENTREGUE".equals(rset.getString("STATUS"));
			}			
			
			if (status) {
				throw new Exception("A mercadoria do documento " + this.numNota +  " já foi entregue e não pode ser enviada ao Serasa");
			}
			
		} catch (Exception erro) {
			throw new Exception(erro.toString());
			
		} finally {
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);

		}
	}
	
	private void exclusaoSerasa() throws Exception {
		URL url = new URL(getURLSerasa());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", "Basic " + getOperador(this.codEmp));
		connection.setRequestProperty("Content-Length", "<calculated when request is sent>");
		connection.setRequestProperty("Host", "<calculated when request is sent>");
		connection.setRequestProperty("User-Agent", "PostmanRuntime/7.29.2");
		connection.setRequestProperty("Accept", "*/*");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Content-Type", "text/xml charset=utf-8");
		
		connection.setDoOutput(true);
		connection.setReadTimeout(30000);
        connection.setConnectTimeout(30000);
        String bodyResponse = "<soapenv:Envelope xmlns:soapenv=\\\"http://schemas.xmlsoap.org/soap/envelope/\\\" xmlns:web=\\\"http://webservice.spc.insumo.spcjava.spcbrasil.org/\\\"> "
        						+ "<soapenv:Header/> "
        						+ "<soapenv:Body> "
        							+ "<web:excluirSpc> "
        								+ "<excluir> "
        									+ "<tipo-pessoa>J</tipo-pessoa> "
        									+ "<dados-pessoa-juridica> "
        										+ "<cnpj numero=\\\"17262755000167\\\"/> "
        										+ "<razao-social>ASSOCIACAO DE CONTRIBUICAO SOCIAL DO BRASIL</razao-social> "
        										+ "<nome-comercial>ASSOCIADOS PATRIARCA</nome-comercial> "
        									+ "</dados-pessoa-juridica> "
        									+ "<data-vencimento>2022-08-22T21:02:14</data-vencimento> "
        									+ "<numero-contrato>45CCH888888CCC</numero-contrato> "
        									+ "<motivo-exclusao> <id>1</id> </motivo-exclusao> "
        								+ "</excluir> </web:excluirSpc> "
        						+ "</soapenv:Body> "
        						+ "</soapenv:Envelope>";
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(bodyResponse);
        wr.flush();
        wr.close();
        wr.close();

		try {
			 String responseStatus = connection.getResponseMessage();
			 System.out.println(responseStatus);
			 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			 String inputLine;
			 StringBuffer response = new StringBuffer();
			 
			 while ((inputLine = in.readLine()) != null) {
				 response.append(inputLine);
			 }
			 in.close();
			 
			if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new IOException("Retorno durante a conexão: Code Response: " + connection.getResponseCode());
			}
			
		} catch (IOException erro) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			StringBuffer mensagem = new StringBuffer();
			erro.printStackTrace(pw);
    		mensagem.append("Erro Exceção: " + erro.getMessage() + sw.toString());
    		throw new Exception(mensagem.toString());
		} finally {
			connection.disconnect();
		}
	}
	
	private void insereLogInclusao(String tipoMovimento) throws MGEModelException {
			JapeSession.SessionHandle hnd = null;
			try {
				hnd = JapeSession.open();
				hnd.setCanTimeout(false);

				hnd.execWithTX(new JapeSession.TXBlock() {
					public void doWithTx() throws Exception {
						//chamadaMetodoInsert(param1,param2);
					}
				});

			} catch (Exception e) {
				MGEModelException.throwMe(e);
			} finally {
				JapeSession.close(hnd);
			}
		
	}

}
		