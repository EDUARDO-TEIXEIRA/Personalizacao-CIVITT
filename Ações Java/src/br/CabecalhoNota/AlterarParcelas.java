package br.CabecalhoNota;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import com.sankhya.util.TimeUtils;

import br.UtilitariosSankhya.FinanceiroUtil;
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
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
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

				// excluirParcelasFinanceiro(new
				// BigDecimal(linha.getCampo("NUNOTA").toString()));
				Collection<DynamicVO> cab = getLinhaFinanceiro(registro);
				

			}
		}
	}

	public Collection<DynamicVO> getLinhaFinanceiro(Object nunota) throws Exception {
		Collection<DynamicVO> filtroTitulos = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade()
				.findByDynamicFinderAsVO(new FinderWrapper(DynamicEntityNames.FINANCEIRO,
						"this.NUNOTA = ? AND ROWNUM = 1", new Object[] { nunota }));

		return filtroTitulos;
	}

	public void excluirParcelasFinanceiro(BigDecimal nunota) throws Exception {
		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = dwf.getJdbcWrapper();
		try {
			NativeSql delete = new NativeSql(jdbc);
			delete.setNamedParameter("P_NUNOTA", (BigDecimal) nunota);
			delete.appendSql("DELETE FROM TGFFIN WHERE NUNOTA = :P_NUNOTA");
			delete.executeUpdate();
		} catch (Exception e) {
			throw new Exception(e.toString());
		} finally {
			jdbc.closeSession();
		}
	}

	public void gerarParcelasFinanceiro(DynamicVO financeiroVo, int qtdParcelas, int prazo) throws Exception {
		int i = 1;
		while (i != qtdParcelas) {

			EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
			DynamicVO finVo = financeiroVo.buildClone();
			finVo.setProperty("DESDOBRAMENTO", i);
			
			finVo.setProperty("DTVENC", 1);
			
			dwf.createEntity(DynamicEntityNames.ITEM_NOTA, (EntityVO) finVo);
			i++;
		}

	}

}
