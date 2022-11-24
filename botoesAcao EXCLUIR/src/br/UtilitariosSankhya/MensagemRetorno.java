package br.UtilitariosSankhya;

import br.com.sankhya.jape.PersistenceException;

public class MensagemRetorno {
	public void mostrarErro (String mensagemErro) throws PersistenceException {
		throw new PersistenceException("");
	}
}
