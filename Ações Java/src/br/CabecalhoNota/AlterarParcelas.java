package br.CabecalhoNota;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class AlterarParcelas implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		Registro[] registro = contexto.getLinhas();
		if (registro.length == 0) {
			throw new Exception("Selecione um registro");
		}
		int qtdParcelas = (int) contexto.getParam("QTDPARCELAS");
		boolean confirmacao = contexto.confirmarSimNao("Deseja continuar",
				"O financeiro será recalculado em <b>" + qtdParcelas + "</b> parcelas.", 1);
		if (confirmacao) {

			for (Registro linha : registro) {
				excluirParcelasFinanceiro((BigDecimal) linha.getCampo("NUNOTA"));
				DynamicVO topRVO = ComercialUtils
						.getTipoOperacao(new BigDecimal(linha.getCampo("CODTIPOPER").toString()));
				
				gerarParcelasFinanceiro(linha, topRVO, getParcelasTipoNegociacao(linha), qtdParcelas);
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
			// delete.appendSql(" AND CODTIPTIT NOT IN (SELECT CODTIPTIT FROM TGFTIT WHERE
			// ESPDOC = 'NF')");
			delete.executeUpdate();
		} catch (Exception e) {
			throw new Exception(e.toString());
		} finally {
			jdbc.closeSession();

		}
	}

	public int getPrazoMedio(DynamicVO tpvVO) throws Exception {
		int prazoMedio = 1;
		Collection<DynamicVO> registros = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(new FinderWrapper("TipoNegociacao", "this.CODTIPVENDA = ?",
						new Object[] { tpvVO.asBigDecimal("CODTIPVENDA") }));

		for (DynamicVO linha : registros) {
			prazoMedio = linha.asInt("AD_PRAZOMEDIO");
		}
		return prazoMedio;
	}

	public DynamicVO getParcelasTipoNegociacao(Registro cabVo) throws Exception {
		DynamicVO registro = null;
		Collection<DynamicVO> registros = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(new FinderWrapper("ParcelaPagamento", "this.CODTIPVENDA = ?",
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
	public void gerarParcelasFinanceiro(Registro cabVo, DynamicVO topVo, DynamicVO tpvVO, int qtdParcelas)
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
			int prazoMedio = getPrazoMedio(tpvVO);

			/*
			 * A primeira parcela é de GNRE, neste caso, eu coloco que i começa com 1 caso
			 * tenha.
			 */
			for (int i = 0; i <= qtdParcelas; i++) {

				finVo.setProperty("CODEMP", (BigDecimal) cabVo.getCampo("CODEMP"));
				finVo.setProperty("NUFIN", null);
				finVo.setProperty("DTNEG", (Timestamp) cabVo.getCampo("DTNEG"));
				finVo.setProperty("NUNOTA", (BigDecimal) cabVo.getCampo("NUNOTA"));
				finVo.setProperty("DTVENC", TimeUtils.dataAddDay((Timestamp) cabVo.getCampo("DTNEG"), i * prazoMedio));
				finVo.setProperty("CODPARC", (BigDecimal) cabVo.getCampo("CODPARC"));
				finVo.setProperty("NUMNOTA", (BigDecimal) cabVo.getCampo("NUMNOTA"));
				finVo.setProperty("DESDOBRAMENTO", Integer.toString(i + 1));
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
				BigDecimal vlrSubs = new BigDecimal(cabVo.getCampo("VLRSUBST").toString());
				BigDecimal vlrNota = new BigDecimal(cabVo.getCampo("VLRNOTA").toString());
				BigDecimal vlrLiq = vlrNota.subtract(vlrSubs);

				BigDecimal vlrParcela = vlrLiq.divide(BigDecimal.valueOf(qtdParcelas).setScale(2, RoundingMode.HALF_DOWN));							
				switch (i) {
				case 0:
					// Na primeira parcela entra o GNRE
					finVo.setProperty("CODTIPTIT", BigDecimal.valueOf(111));
					finVo.setProperty("DTVENC", TimeUtils.dataAddDay((Timestamp) cabVo.getCampo("DTNEG"), 3));
					finVo.setProperty("VLRDESDOB", vlrSubs);
					vlrTot = vlrTot.add(vlrSubs).setScale(2, RoundingMode.HALF_DOWN);
					break;
				case 1:
					// Na segunda parcela é adicionado o GNRE + Valor da parcela
					finVo.setProperty("VLRDESDOB", vlrLiq.divide(BigDecimal.valueOf(qtdParcelas)).add(vlrSubs).setScale(2, RoundingMode.HALF_DOWN));
					vlrTot = vlrTot.add(vlrParcela).add(vlrSubs).setScale(2, RoundingMode.HALF_DOWN);
					break;
				default:
					finVo.setProperty("VLRDESDOB", vlrParcela);
					vlrTot = vlrTot.add(vlrParcela).setScale(2, RoundingMode.HALF_DOWN);;
					break;
				}
				
				if (i == qtdParcelas) {
					/* Adiciona a diferença na última parcela */
					BigDecimal difTot = vlrNota.add(vlrSubs).subtract(vlrTot).add(vlrParcela);
					finVo.setProperty("VLRDESDOB", difTot);
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
}
