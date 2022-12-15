package CabecalhoNota;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Collection;

import br.civitt.ConexaoLincros.IntegracaoLincros;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.JapeWrapperImpl;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EnviarXMLLincros implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterInsert(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterUpdate(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub
		DynamicVO cabVO = (DynamicVO) contexto.getVo();
		if (cabVO.getProperty("STATUSNFE") != null) {
			if ((cabVO.getProperty("STATUSNFE").equals("A") && cabVO.getProperty("TIPMOV").equals("V"))) {
				EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
				Collection<DynamicVO> xmlVO = dwfFacade.findByDynamicFinderAsVO(new FinderWrapper("ArquivoNFe",
						"this.NUNOTA = ?", new Object[] { cabVO.asBigDecimal("NUNOTA") }));

				for (DynamicVO linhaNFe : xmlVO) {
					byte[] dadosXML = new String(linhaNFe.asClob("XML")).getBytes();
					IntegracaoLincros.setNunota(linhaNFe.asBigDecimal("NUNOTA"));
					IntegracaoLincros.conexaoHTTP(dadosXML);
					setProtocoloRetornoLincros(cabVO, IntegracaoLincros.protocolo);
				}
			}
		}
	}

	@Override
	public void beforeCommit(TransactionContext contexto) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeDelete(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeInsert(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeUpdate(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub

	}

	public void setProtocoloRetornoLincros(DynamicVO cabVO, BigDecimal protocolo) throws Exception {
		JapeSession.SessionHandle hnd = null;
		JdbcWrapper jdbc = null;
		try {
			hnd = JapeSession.open();
			jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
			jdbc.openSession();
			NativeSql update = new NativeSql(jdbc);

			update.setNamedParameter("P_NUNOTA", cabVO.asBigDecimal("NUNOTA"));
			update.setNamedParameter("P_PROTOCOLO", protocolo);
			update.executeUpdate("UPDATE TGFCAB SET AD_PROTO_ENVIO_XML_LINCROS = :P_PROTOCOLO WHERE NUNOTA = :P_NUNOTA");
			update.setReuseStatements(true);
			update.setBatchUpdateSize(500);
			update.flushBatchTail();
			NativeSql.releaseResources(update);
		} catch (Exception e) {
			throw new Exception("Não foi possível atualizar o protocolo de envio da Lincros");
		} finally {
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}
}
