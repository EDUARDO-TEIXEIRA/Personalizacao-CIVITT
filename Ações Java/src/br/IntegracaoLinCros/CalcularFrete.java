package br.IntegracaoLinCros;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import br.UtilitariosSankhya.CabecalhoNotaUtil;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

import br.com.sankhya.extensions.actionbutton.Registro;

public class CalcularFrete implements AcaoRotinaJava {
	List nunotaParceiro = new ArrayList();
	
	
	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		Registro[] linhas = contexto.getLinhas();
		
		if (linhas.length == 0) {
			throw new Exception("Selecione um pedido de vendas");
		}
		// Popula o map
		Map<BigDecimal, BigDecimal> map = new HashMap<>();
		for (Registro registro : linhas) {
			if (!registro.getCampo("TIPMOV").equals("P")) {
				throw new Exception("Não é possível calcular frete de registros que não sejam pedidos de vendas");
			}
			map.put((BigDecimal)registro.getCampo("NUNOTA"), (BigDecimal)registro.getCampo("CODPARC"));
		}
		// Agrupa o pedido dos parceiros
		Map<BigDecimal, List<BigDecimal>> groupedValues = new HashMap<>();

		for (Entry<BigDecimal, BigDecimal> entry : map.entrySet()) {
			
			BigDecimal value = entry.getValue();
			BigDecimal key = entry.getKey();

			List<BigDecimal> indices = groupedValues.getOrDefault(value, new ArrayList<>());
			indices.add(key);
			groupedValues.put(value, indices);
		}
		// Faz a manipulação dos dados
		for (Entry<BigDecimal, List<BigDecimal>> entry : groupedValues.entrySet()) {
			CabecalhoNotaUtil cab = new CabecalhoNotaUtil();
			String criterioConsulta = entry.getValue().toString().replace("[", "(").replace("]", ")");
			cab.buscarPedidosAgrupadasCliente(criterioConsulta);
			BigDecimal totalNota = cab.getTotalNota();
			BigDecimal peso = cab.getPeso();
			BigDecimal qtdVolume = cab.getQtdVolume();
			BigDecimal codParc = cab.getCodParc(); 
					
			IntegracaoLincros lincros = new IntegracaoLincros();
			lincros.setExpressaoConsulta(criterioConsulta);
					
			lincros.conexaoLincros(codParc, totalNota, peso, qtdVolume);
		}
		
	}
}
