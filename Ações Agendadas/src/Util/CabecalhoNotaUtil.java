package Util;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.ZipUtils;


public class CabecalhoNotaUtil{
	EntityFacade dwfEntity = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper	jdbc = dwfEntity.getJdbcWrapper();
		
	//public byte[] consultaNFEmitidasDia() throws Exception {
	public List<File> consultaNFEmitidasDia() throws Exception {
		List<File> listaArquivo = new ArrayList<>();

		jdbc.openSession();
		NativeSql buscaNFe = new NativeSql(jdbc);
		buscaNFe.loadSql(getClass(), "consultasSQL/BuscaNFe.sql");
		ResultSet dataSet = buscaNFe.executeQuery();

		while (dataSet.next()) {
			File xml = ArquivosUtil.gerarArquivo(dataSet.getClob("XML"));
			listaArquivo.add(xml);
		}
		
		jdbc.closeSession();
		//return listaArquivo.toString().getBytes("UTF-8"); // return em byte
		return listaArquivo;
	}

	public void setCabecalhoNotaEnvioLincros() throws Exception {
		try {
			jdbc.openSession();
			NativeSql buscaNFe = new NativeSql(jdbc);
			buscaNFe.loadSql(getClass(), "consultasSQL/BuscaNFe.sql");
			ResultSet dataSet = buscaNFe.executeQuery();

			while (dataSet.next()) {
				NativeSql update = new NativeSql(jdbc);
				update.setNamedParameter("P_NUNOTA", (BigDecimal) dataSet.getBigDecimal("NUNOTA"));
				update.appendSql("UPDATE TGFCAB SET AD_ENVIADOLINCROS = 'S' WHERE NUNOTA = :P_NUNOTA");
				update.executeQuery();
			}
		} catch (Exception e) {
			System.out.println("Erro ao tentar atualizar o campo AD_ENVIADOLINCROS. " + e.toString());
		} finally {
			jdbc.closeSession();
		}

	}
	
}
