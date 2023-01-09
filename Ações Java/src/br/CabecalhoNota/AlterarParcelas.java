package br.CabecalhoNota;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import com.sankhya.util.TimeUtils;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class AlterarParcelas implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		Registro[] registro = contexto.getLinhas();
		if (registro.length == 0) {
			throw new Exception("Selecione um registro");
		}
		boolean confirmacao = contexto.confirmarSimNao("Deseja continuar", "O financeiro será recalculado", 1);
		if (confirmacao) {
			int qtdParcelas = (int) contexto.getParam("QTDPARCELAS");
			int prazo = (int) contexto.getParam("PRAZO");
			for (Registro linha : registro) {
				excluirParcelasFinanceiro((BigDecimal) linha.getCampo("NUNOTA"));
				gerarParcelasFinanceiro(linha, getTipoOperacao(linha), getParcelasTipoNegociacao(linha), qtdParcelas,
						prazo);
			}
		}
		contexto.setMensagemRetorno("Financeiro atualizado");
	}

	public void excluirParcelasFinanceiro(Object nunota) throws Exception {
		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = dwf.getJdbcWrapper();
		try {
			NativeSql delete = new NativeSql(jdbc);
			// Excluindo registros que não fazem parte das GUIAS de impostos
			delete.setNamedParameter("P_NUNOTA", (BigDecimal) nunota);
			delete.appendSql("DELETE FROM TGFFIN WHERE NUNOTA = :P_NUNOTA");
			delete.appendSql(" AND CODTIPTIT NOT IN (SELECT CODTIPTIT FROM TGFTIT WHERE ESPDOC = 'NF')");
			delete.executeUpdate();
		} catch (Exception e) {
			throw new Exception(e.toString());
		} finally {
			jdbc.closeSession();

		}
	}

	public DynamicVO getParcelasTipoNegociacao(Registro cabVo) throws Exception {
		DynamicVO registro = null;
		Collection<DynamicVO> registros = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(
						new FinderWrapper("ParcelaPagamento", "this.CODTIPVENDA = ? AND CODTIPTITPAD NOT IN (111)",
								new Object[] { cabVo.getCampo("CODTIPVENDA") }));

		if (registros.isEmpty()) {
			throw new Exception(
					"É necessário que tenha ao menos uma parcela p/ geração do financeiro que não seja um GNRE.");
		}

		for (DynamicVO linha : registros) {
			registro = linha;
		}
		return registro;
	}

	public DynamicVO getTipoOperacao(Registro cabVo) throws Exception {
		DynamicVO registro = null;
		Collection<DynamicVO> registros = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(
						new FinderWrapper("TipoOperacao", "this.CODTIPOPER = ? AND this.DHALTER = ?",
								new Object[] { cabVo.getCampo("CODTIPOPER"), cabVo.getCampo("DHTIPOPER") }));
		
		for (DynamicVO linha : registros) {
			registro = linha;
		}
		return registro;
	}

	public void gerarParcelasFinanceiro(Registro cabVo, DynamicVO topVo, DynamicVO tpvVO, int qtdParcelas, int prazo)
			throws Exception {
		JdbcWrapper jdbc = null;
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();

			EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
			jdbc = dwf.getJdbcWrapper();
			jdbc.openSession();

			DynamicVO finVo = (DynamicVO) dwf.getDefaultValueObjectInstance(DynamicEntityNames.FINANCEIRO);
			String atualizaFinanceiro = "";
			if (topVo.asString("TIPATUALFIN").equals("P")) {
				atualizaFinanceiro = "S";
			} else {
				atualizaFinanceiro = "N";
			}

			BigDecimal vlrTot = BigDecimal.ZERO;
			for (int i = 1; i <= qtdParcelas; i++) {
				finVo.setProperty("CODEMP", (BigDecimal) cabVo.getCampo("CODEMP"));
				finVo.setProperty("NUFIN", null);
				finVo.setProperty("DTNEG", (Timestamp) cabVo.getCampo("DTNEG"));
				finVo.setProperty("NUNOTA", (BigDecimal) cabVo.getCampo("NUNOTA"));
				finVo.setProperty("DTVENC", TimeUtils.dataAddDay((Timestamp) cabVo.getCampo("DTNEG"), i * prazo));
				finVo.setProperty("CODPARC", (BigDecimal) cabVo.getCampo("CODPARC"));
				finVo.setProperty("NUMNOTA", (BigDecimal) cabVo.getCampo("NUMNOTA"));
				finVo.setProperty("DESDOBRAMENTO", Integer.toString(i));
				finVo.setProperty("ORIGEM", "E");
				finVo.setProperty("PROVISAO", atualizaFinanceiro);
				finVo.setProperty("CODTIPOPER", (BigDecimal) cabVo.getCampo("CODTIPOPER"));
				finVo.setProperty("DHTIPOPER", (Timestamp) cabVo.getCampo("DHALTER"));
				finVo.setProperty("CODNAT", (BigDecimal) cabVo.getCampo("CODNAT"));
				finVo.setProperty("CODCTABCOINT", tpvVO.asBigDecimal("CODCTABCOINT"));
				finVo.setProperty("RECDESP", topVo.asBigDecimal("ATUALFIN"));
				finVo.setProperty("ORDEMCARGA", (BigDecimal) cabVo.getCampo("ORDEMCARGA"));
				finVo.setProperty("CODCENCUS", (BigDecimal) cabVo.getCampo("CODCENCUS"));
				finVo.setProperty("CODPROJ", (BigDecimal) cabVo.getCampo("CODPROJ"));
				finVo.setProperty("CODVEND", (BigDecimal) cabVo.getCampo("CODVEND"));
				finVo.setProperty("CODTIPTIT", tpvVO.asBigDecimal("CODTIPTITPAD"));
				BigDecimal vlrParcela = new BigDecimal(cabVo.getCampo("VLRNOTA").toString())
						.divide(new BigDecimal(qtdParcelas)).setScale(2, BigDecimal.ROUND_HALF_DOWN);

				finVo.setProperty("VLRDESDOB", vlrParcela);
				vlrTot = vlrTot.add(vlrParcela);

				if (i == qtdParcelas) {
					BigDecimal difTot = new BigDecimal(cabVo.getCampo("VLRNOTA").toString()).subtract(vlrTot);
					finVo.setProperty("VLRDESDOB", difTot.add(vlrParcela));
				}

				PersistentLocalEntity createEntity = dwf.createEntity(DynamicEntityNames.FINANCEIRO, (EntityVO) finVo);
				DynamicVO save = (DynamicVO) createEntity.getValueObject();
			}

		} catch (Exception erro) {
			throw new Exception(erro.toString());
		} finally {
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}

	}

	public String addDiasData(java.util.Date date, int qtddias) {
		Date dt = new Date();
		Calendar dtInicio = new GregorianCalendar();
		dtInicio.setTime(date);
		dtInicio.add(Calendar.DATE, qtddias);
		dt = dtInicio.getTime();

		return "";
	}

}
