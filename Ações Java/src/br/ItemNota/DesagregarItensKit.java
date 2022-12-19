package br.ItemNota;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Collection;

import com.sankhya.util.JdbcUtils;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.modelcore.comercial.impostos.ImpostosHelpper;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class DesagregarItensKit implements AcaoRotinaJava {
	BigDecimal sequencia = BigDecimal.ZERO;

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		Registro[] itens = contexto.getLinhas();
		if (itens.length == 0) {
			throw new Exception("Selecione um produto");
		}

		for (Registro linha : itens) {
			if (linha.getCampo("CODVOLD").equals("KT")) {
				// Método busca os itens do Kit
				obterIntensKit(itens);
				// Método que recalcula nota
				calcularImposto(new BigDecimal(linha.getCampo("calcularImposto").toString()));

			} else {
				String mensagem = String.format("O item %s não é um kit", linha.getCampo("CODPROD").toString());
				throw new Exception(mensagem);
			}
		}
	}

	public void obterIntensKit(Registro[] itens) throws Exception {
		JdbcWrapper jdbc = null;
		NativeSql sql = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();

			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			sql = new NativeSql(jdbc);
			for (Registro linha : itens) {
				sql.setNamedParameter("P_CODPROD", linha.getCampo("CODPROD"));
				sql.appendSql("SELECT CODMATPRIMA, ");
				sql.appendSql("QTDMISTURA, ");
				sql.appendSql("CODVOL ");
				sql.appendSql("FROM TGFICP ");
				sql.appendSql("WHERE CODPROD = :P_CODPROD");

				rset = sql.executeQuery();

				excluirKit(linha.getCampo("CODPROD"), linha.getCampo("SEQUENCIA"));
				if (rset.next()) {
					// Método que excluir o registro
					inserirItem(linha);
				}

			}

		} catch (Exception erro) {
			throw new Exception("<b>ERRO:</b> ao tentar buscar registros " + erro);
		} finally {
			JdbcUtils.closeResultSet(rset);
			NativeSql.releaseResources(sql);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}

	public void excluirKit(Object nunota, Object sequencia) throws Exception {
		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
		FinderWrapper itemFinder = new FinderWrapper(DynamicEntityNames.ITEM_NOTA,
				"this.SEQUENCIA = ? AND this.NUNOTA = ?" + new Object[] { sequencia, nunota });
		dwf.removeByCriteria(itemFinder);
	}

	public void inserirItem(Registro produto) throws Exception {
		JdbcWrapper jdbc = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();

			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			ItemNotaVO itemVo = (ItemNotaVO) entity.getDefaultValueObjectInstance(DynamicEntityNames.ITEM_NOTA);

			itemVo.setProperty("CODEMP", produto.getCampo("CODEMP"));
			itemVo.setProperty("NUNOTA", produto.getCampo("NUNOTA"));
			itemVo.setProperty("CODPROD", produto.getCampo("CODEMP"));
			itemVo.setProperty("QTDNEG", produto.getCampo("QTDNEG"));
			itemVo.setProperty("CODVOL", produto.getCampo("CODPROD"));
			itemVo.setProperty("CODLOCALORIG", produto.getCampo("CODLOCALORIG"));
			itemVo.setProperty("VLRUNIT", produto.getCampo("VLRUNIT"));
			itemVo.setProperty("VLRTOT", produto.getCampo("VLRTOT"));
			itemVo.setProperty("VLRDESC", produto.getCampo("VLRDESC"));
			itemVo.setProperty("PERCDESC", produto.getCampo("PERCDESC"));
			itemVo.setProperty("BASEICMS", produto.getCampo("BASEICMS"));
			itemVo.setProperty("VLRICMS", produto.getCampo("VLRICMS"));
			itemVo.setProperty("ALIQICMS", produto.getCampo("ALIQICMS"));
			itemVo.setProperty("BASEIPI", produto.getCampo("BASEIPI"));
			itemVo.setProperty("VLRIPI", produto.getCampo("VLRIPI"));
			itemVo.setProperty("ALIQIPI", produto.getCampo("ALIQIPI"));

			entity.createEntity(DynamicEntityNames.ITEM_NOTA, itemVo);

		} catch (Exception erro) {
			throw new Exception("<b>ERRO:</b> ao tentar buscar registros " + erro);
		} finally {
			JdbcUtils.closeResultSet(rset);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}

	public void calcularImposto(BigDecimal nunota) throws Exception {
		ImpostosHelpper imposto = new ImpostosHelpper();

		imposto.carregarNota(nunota);
		imposto.calcularImpostos(nunota);
		imposto.calcularPIS();
		imposto.calcularCOFINS();
	}

}
