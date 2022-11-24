package br.IntegracaoSerasa;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import br.UtilitariosSankhya.FinanceiroUtil;
import br.UtilitariosSankhya.MensagemRetornoUtil;
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
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;


public class IncluirTitulosSerasa implements AcaoRotinaJava {
	MensagemRetornoUtil msg = new MensagemRetornoUtil();
	FinanceiroUtil financeiro = new FinanceiroUtil();
		
	private String cgc_cpf;
	private String logradouro;
	private String numEndereco;
	private String nomeBairro;
	private String cep;
	
	private String razaoSocial;
	private String nomeFantasia;
	private BigDecimal codUsuLogado;
	private EntityFacade dwf;
	private String xmlRequest;
	
	JdbcWrapper jdbc = null;
	
	@Override
	public void doAction(final ContextoAcao contexto) throws Exception {
		this.codUsuLogado = contexto.getUsuarioLogado();
		
		Registro[] linhas = contexto.getLinhas();
		
		if (linhas.length == 0 ) {
			throw new Exception("Selecione uma linha");
			//msg.exibirErro("Operação inválida","Selecione uma linha", null);
		}
		
		for (Registro linha : linhas) 
		{	        
			Calendar dtInicio = new GregorianCalendar();
	        dtInicio.setTime((Date) linha.getCampo("DTVENC"));
	        Calendar dtFinal = Calendar.getInstance();
	        dtFinal.setTime(dtFinal.getTime());
	        
	        
	        Long difMillis = dtFinal.getTimeInMillis() - dtInicio.getTimeInMillis();
	        Long diffDias = TimeUnit.DAYS.convert(difMillis, TimeUnit.MILLISECONDS);
	        
	        this.financeiro.setCodEmp((BigDecimal) linha.getCampo("CODEMP"));
			this.financeiro.setCodParc((BigDecimal) linha.getCampo("CODPARC"));
	        this.financeiro.setNuFin((BigDecimal) linha.getCampo("NUFIN"));
			this.financeiro.setNuNota((BigDecimal) linha.getCampo("NUNOTA"));
			this.financeiro.setNumNota((BigDecimal) linha.getCampo("NUMNOTA"));
				
			if (linha.getCampo("PROVISAO").equals("S")) {
				throw new Exception("Não é possível enviar provisões ao Serasa");
				//msg.exibirErro("Operação Interrompida", "Não é possível enviar provisões ao Serasa", null);
			} else if (((BigDecimal) linha.getCampo("RECDESP")).intValue() != 1) {
				throw new Exception("Não é possível enviar despesas ou provisões ao Serasa");
				//msg.exibirErro("Operação Interrompida", "Não é possível enviar despesas ou provisões ao Serasa", null);
			}  else if(linha.getCampo("AD_SERASA") != null ) {
				if (linha.getCampo("AD_SERASA").equals("S")) {
					throw new Exception("O registro de número único " + this.financeiro.getNuFin() + " já foi enviado ao Serasa.");
				//msg.exibirErro("Operação interrompida", "O registro de número único " + this.financeiro.getNuFin() + " já foi enviado ao Serasa.", null);
				}
			} else if(linha.getCampo("DHBAIXA") != null && linha.getCampo("CODTIPOPERBAIXA") != "0") {
				throw new Exception("O registro número único " + this.financeiro.getNuFin() + " já foi baixado e não pode ser enviado ao Serasa.");
			} else if(linha.getCampo("NUMNOTA") == null) {
				throw new Exception("Documento sem número de nota informado no registro " + this.financeiro.getNuFin());
			}
				getDiasEnvioSerasa(diffDias);
				getValidaParceiro(this.financeiro.getCodParc());
				getValidaStatusRastreioMercadoria();	
				
				
			final boolean confirmaOperacao = contexto.confirmarSimNao("Deseja continuar?", "Foram selecionado (s) " + linhas.length + " registro (s) para enviar ao Serasa.", 0);
			
			if (confirmaOperacao) {
				BigDecimal numContrato = this.financeiro.getNumNota();
				this.xmlRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.spc.insumo.spcjava.spcbrasil.org/\"> <soapenv:Header/> <soapenv:Body> <web:incluirSpc> <insumoSpc> <tipo-pessoa>J</tipo-pessoa> <dados-pessoa-juridica> <cnpj numero=\"" + this.cgc_cpf + "\"/> <razao-social> " + this.razaoSocial +  " </razao-social> <nome-comercial>" + this.nomeFantasia + "</nome-comercial> </dados-pessoa-juridica> <data-compra>" + linha.getCampo("DTNEG") + "T00:00:00" + "</data-compra> <data-vencimento>" +linha.getCampo("DTVENC") + "T00:00:00" + "</data-vencimento> <codigo-tipo-devedor>C</codigo-tipo-devedor> <numero-contrato>" + numContrato + "</numero-contrato> <valor-debito> " + linha.getCampo("VLRDESDOB") + "</valor-debito> <natureza-inclusao> <id>1</id> </natureza-inclusao> <endereco-pessoa> <cep>-" + this.cep+ "</cep> <logradouro>" +this.logradouro +"</logradouro> <bairro>" + this.nomeBairro + "</bairro> <numero>" + this.numEndereco + "</numero> </endereco-pessoa> </insumoSpc> </web:incluirSpc> </soapenv:Body> </soapenv:Envelope>";
				integracao();
				contexto.setMensagemRetorno("Registros processados, verifique o log para mais informações!");
			}
		}
	}
	private void getValidaParceiro(BigDecimal codParceiro) throws Exception {
		NativeSql buscaEnderecoParceiro = null;
		Collection<DynamicVO> filtroParceiroVO = (Collection<DynamicVO>) 
		EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(
		new FinderWrapper(DynamicEntityNames.PARCEIRO, "this.CODPARC = ?", codParceiro));
	
		for (DynamicVO linha : filtroParceiroVO) 
		{
			if (linha.asString("TIPPESSOA").equals("F")) {
				throw new Exception("Não é possível enviar registros ao Serasa de pessoas físicas.");
			}
			if (linha.asString("AD_IGNORAENVIOSERASA") != null) {
				if(linha.asString("AD_IGNORAENVIOSERASA").equals("S")) { 
					throw new Exception("O parceiro <b>" + codParceiro + " - " + linha.asString("RAZAOSOCIAL").toString() + "</b> não pode ser enviado ao Serasa!");
				}
			} 
			
			this.dwf = EntityFacadeFactory.getDWFFacade();
			jdbc = dwf.getJdbcWrapper();
			jdbc.openSession();
			
			buscaEnderecoParceiro = new NativeSql(jdbc);
			buscaEnderecoParceiro.setNamedParameter("P_CODPARC", codParceiro);
			buscaEnderecoParceiro.loadSql(getClass(), "consultasSQL/buscaDadosEndereco.sql");
			
			ResultSet dataSet = buscaEnderecoParceiro.executeQuery();
			
			while (dataSet.next()) {

				this.cep = dataSet.getString("CEP");
				this.nomeBairro = dataSet.getString("NOMEBAI");
				this.numEndereco = dataSet.getString("NUMEND");
				this.logradouro = dataSet.getString("NOMEEND");
			}
			
			this.cgc_cpf = linha.asString("CGC_CPF");
			this.nomeFantasia = linha.asString("NOMEPARC");
			this.razaoSocial = linha.asString("RAZAOSOCIAL");
		}
				
		jdbc.closeSession();
	}
	
	private void getDiasEnvioSerasa(long diasVencimento) throws Exception {
		Collection<DynamicVO> filtroPreferenciaVO = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.PARAMETRO_SISTEMA, "this.CHAVE = 'QTDDIASSERASA'")); 
		for (DynamicVO linha : filtroPreferenciaVO) {
			if (linha.asInt("INTEIRO") > diasVencimento) {
				throw new Exception("O título possui " + diasVencimento + " dia (s) de vencimento e está abaixo do critério definido na preferências do sistema <b>'QTDDIASSERASA'</b>: " + linha.getProperty("INTEIRO") + " dias.");
			}
		}
	}
	
	private void getValidaStatusRastreioMercadoria() throws Exception {
		NativeSql sql = null;
		ResultSet dataSet = null;
		SessionHandle hnd = null;
		boolean status = false; 
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);
			
			if ( this.financeiro.getNuNota() != null) {
				sql.setNamedParameter("P_NUNOTA", this.financeiro.getNuNota());
				sql.appendSql("SELECT FUN_CVT_RASTREIO_PEDIDO(:P_NUNOTA) AS STATUS FROM DUAL");	
			} else {
				sql.setNamedParameter("P_NUMNOTA", this.financeiro.getNumNota());
				sql.setNamedParameter("P_CODEMP", this.financeiro.getCodEmp());
				sql.appendSql("SELECT FUN_CVT_RASTREIO_PEDIDO(NUNOTA) AS STATUS FROM TGFCAB WHERE NUMNOTA||CODEMP = :P_NUMNOTA ||:P_CODEMP AND TIPMOV = 'V'");
			}
			
			dataSet = sql.executeQuery();

			if (dataSet.next()) {
				status = (!"ENTREGUE".equals(dataSet.getString("STATUS")));
			}			
			
			if (status) {
				throw new Exception("A mercadoria do documento " + this.financeiro.getNumNota() +  " não foi entregue e não pode ser enviada ao Serasa");
			}
			
		} catch (Exception erro) {
			throw new Exception(erro.toString());
			
		} finally {
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);

		}
	}
	private void integracao() throws Exception {
		IntegracaoSankhyaSerasa integrador = new IntegracaoSankhyaSerasa();
		integrador.setXmlBody(this.xmlRequest);
		integrador.getValidaEnvioSerasa(codUsuLogado);
		integrador.setCodEmpresaOperador(this.financeiro.getCodEmp() );
		
		integrador.operacaoSerasa(this.financeiro.getNuFin(), this.financeiro.getCodParc(), this.codUsuLogado, "I");
		}
}
		