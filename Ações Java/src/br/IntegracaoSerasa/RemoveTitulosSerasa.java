package br.IntegracaoSerasa;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

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
	String xmlRequest = "";
	
	
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
	        
			this.nuFin = (BigDecimal) linha.getCampo("NUFIN");			
			this.nuNota = (BigDecimal) linha.getCampo("NUNOTA");
			this.numNota = (BigDecimal) linha.getCampo("NUMNOTA");
			this.codParc = (BigDecimal) linha.getCampo("CODPARC");
			this.codEmp = (BigDecimal) linha.getCampo("CODEMP");
			
			if (linha.getCampo("PROVISAO").equals("S")) {
				throw new Exception("Não é possível enviar provisões ao Serasa");
			} else if (((BigDecimal) linha.getCampo("RECDESP")).intValue() != 1) {
				throw new Exception("Não é possível enviar despesas ou provisões ao Serasa ");
			} else if(linha.getCampo("AD_SERASA") == null) {
				throw new NullPointerException("O título não pode ser excluído pois não foi enviado ao Serasa");
			} else if (linha.getCampo("AD_SERASA").equals("N")) {
				throw new Exception("O registro não foi enviado ao Serasa.");
			} else if(linha.getCampo("DHBAIXA") != null && linha.getCampo("CODTIPOPERBAIXA") != "0") {
				throw new Exception("O registro número único " + nuFin + " já foi baixado e não pode ser enviado ao Serasa.");
			} 	
			
			final boolean confirmaOperacao = contexto.confirmarSimNao("Deseja continuar?", "Foram selecionado (s) " + linhas.length + " registro (s) para enviar ao Serasa.", 0);
			
			if (confirmaOperacao) {
				getBuscaParceiro(this.codParc);
				String numContrato = this.cgc_cpf + "C"+ this.nuFin;
			   this.xmlRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.spc.insumo.spcjava.spcbrasil.org/\"> <soapenv:Header/> <soapenv:Body> <web:excluirSpc> <excluir> <tipo-pessoa>J</tipo-pessoa> <dados-pessoa-juridica> <cnpj numero=\"" + this.cgc_cpf + "\"/> <razao-social>" + this.razaoSocial + "</razao-social> <nome-comercial>" + this.nomeFantasia+ "</nome-comercial> </dados-pessoa-juridica> <data-vencimento>"+linha.getCampo("DTVENC") + "T00:00:00" + "</data-vencimento> <numero-contrato>" + numContrato + "</numero-contrato> <motivo-exclusao> <id>1</id> </motivo-exclusao> </excluir> </web:excluirSpc> </soapenv:Body> </soapenv:Envelope>"; 
			    integracao();
			    contexto.setMensagemRetorno("Dados excluídos com sucesso!");
				linha.setCampo("AD_SERASA", "N");
				linha.save();
			}
		}
	}
	private void integracao() throws Exception {
		IntegracaoSankhyaSerasa integrador = new IntegracaoSankhyaSerasa();
		integrador.setXmlBody(this.xmlRequest);
		integrador.getValidaEnvioSerasa(codUsuLogado);		
		integrador.setCodEmpresaOperador(this.codEmp);
		integrador.validaRegistroLog(this.nuFin, "E");
		integrador.inserirLog(this.nuFin, this.codParc, this.codUsuLogado, "E");
		integrador.operacaoSerasa();
		}
	
	private void getBuscaParceiro(BigDecimal codParceiro) throws Exception {
		NativeSql buscaEnderecoParceiro = null;
		Collection<DynamicVO> filtroParceiroVO = (Collection<DynamicVO>) 
		EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(
		new FinderWrapper(DynamicEntityNames.PARCEIRO, "this.CODPARC = ?", codParceiro));
	
		for (DynamicVO linha : filtroParceiroVO) 
		{
			this.cgc_cpf = linha.asString("CGC_CPF");
			this.nomeFantasia = linha.asString("NOMEPARC");
			this.razaoSocial = linha.asString("RAZAOSOCIAL");
		}
	}
}
		