package br.civitt.Parceiro;

import java.math.BigDecimal;
import java.sql.ResultSet;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class InsereTipoNegociacaoWMW implements EventoProgramavelJava{
	BigDecimal [] tiposNegociacao = {
			new BigDecimal("1365"),
			new BigDecimal("1366"),
			new BigDecimal("1367"),
			new BigDecimal("1368"),
			new BigDecimal("1369"),
			new BigDecimal("1370"),
			new BigDecimal("1371"),
			new BigDecimal("1372"),
			new BigDecimal("1373"),
			new BigDecimal("1374"),
			new BigDecimal("1375"),
			new BigDecimal("1376"),
			new BigDecimal("1377")
	};
	
	             			  
			
	private EntityFacade dwf = EntityFacadeFactory.getDWFFacade();; 
	private JdbcWrapper jdbc = dwf.getJdbcWrapper(); 
	
	@Override
	public void afterDelete(PersistenceEvent contexto) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent contexto) throws Exception {
		DynamicVO vo = (DynamicVO) contexto.getVo();
		verificaTipoNegInserido(vo.asBigDecimal("CODPARC"));
		
	}

	@Override
	public void afterUpdate(PersistenceEvent contexto) throws Exception {
		DynamicVO vo = (DynamicVO) contexto.getVo();
		verificaTipoNegInserido(vo.asBigDecimal("CODPARC"));
		//inserirTipoNegociacao(BigDecimal.ONE,BigDecimal.valueOf(16801),1362);
		
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
		
	}

	@Override
	public void beforeUpdate(PersistenceEvent contexto) throws Exception {
		
		
	}
	private void verificaTipoNegInserido(BigDecimal codparc) throws Exception {
		jdbc.openSession();
		
		NativeSql sqlBusca = new NativeSql(jdbc);
		for (int i = 0; i < tiposNegociacao.length; i++) {
			sqlBusca.setNamedParameter("P_CODPARC", codparc);
			sqlBusca.setNamedParameter("P_CODTIPVENDA", tiposNegociacao[i]);
			sqlBusca.appendSql("SELECT CODPARC, CODTIPVENDA" + 
								" FROM AD_WMWTPV "
							  + "WHERE CODPARC = :P_CODPARC "
							  + "  AND CODTIPVENDA = :P_CODTIPVENDA");
			ResultSet rs = sqlBusca.executeQuery();
			BigDecimal seq = getCodWMW(codparc);
			if (!rs.next()) {
				seq = seq.add(BigDecimal.ONE);
				inserirTipoNegociacao(seq, codparc, tiposNegociacao[i]);
			}			
		} 
	}
	private BigDecimal getCodWMW(BigDecimal codparc) throws Exception {
		BigDecimal seq = BigDecimal.valueOf(0);
		NativeSql sqlBusca = new NativeSql(jdbc);
		sqlBusca.setNamedParameter("P_CODPARC",codparc );
		sqlBusca.appendSql("SELECT NVL(MAX(COD),0) AS COD FROM AD_WMWTPV WHERE CODPARC = :P_CODPARC");
		ResultSet rs = sqlBusca.executeQuery();
		if (rs.next()) {
			seq = rs.getBigDecimal("COD");
		}		
		
		return seq;
	}

	private void inserirTipoNegociacao(BigDecimal sequencia, BigDecimal cod_parceiro, BigDecimal tiposNegociacao) throws Exception {	 
		try {
			jdbc.openSession();
			//EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
			//JdbcWrapper jb = dwfEntityFacade.getJdbcWrapper();
			//jb.openSession();
						
			DynamicVO wmwVo = (DynamicVO) dwf.getDefaultValueObjectInstance("AD_WMWTPV");
			 
			wmwVo.setProperty("CODPARC", 16801);
			wmwVo.setProperty("COD", 0);
			wmwVo.setProperty("CODTIPVENDA", 1362);

			dwf.createEntity("AD_WMWTPV", (EntityVO) wmwVo);
					
		} catch (Exception erro) {
			throw new Exception("<b>ERRO: </b> " + erro.toString());
		} finally {
			//jdbc.closeSession();
		}
	}
	
	
	

}
