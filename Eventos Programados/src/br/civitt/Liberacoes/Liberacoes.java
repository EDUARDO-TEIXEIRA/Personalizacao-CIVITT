package br.civitt.Liberacoes;

import java.util.Date;

import br.civitt.Utilitarios.DatasUtil;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;


public class Liberacoes implements EventoProgramavelJava {

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
		DynamicVO libVO = (DynamicVO) contexto.getVo();

		if (libVO.getProperty("DHLIB") != null) {
			Date dataSolicit = (Date) libVO.getProperty("DHSOLICIT");
			String tempoLeadTime = null;
			DatasUtil difDatasIni = new DatasUtil();
			tempoLeadTime = difDatasIni.getDifHoras((Date) libVO.getProperty("DHLIB"), dataSolicit);
			libVO.setProperty("AD_LEADTIMELIB", tempoLeadTime);
		} else {
			libVO.setProperty("AD_LEADTIMELIB", null);
		}
	}


}
