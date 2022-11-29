package br.IntegracaoLinCros.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transportadora__1 {

private String prazoCliente;
private String previsaoEntrega;
private Double pesoCubado;
private Double peso;
private Double valor;
private String nome;
private List<Movimento> movimentos = null;
private String cnpj;
private Object valorCliente;
private String status;
private Tabela tabela;
private Integer diasEntrega;
private String possuiInstrucaoEntrega;
private String modal;
private Integer tipoConhecimento;
private List<Object> redespachos = null;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

public String getPrazoCliente() {
return prazoCliente;
}

public void setPrazoCliente(String prazoCliente) {
this.prazoCliente = prazoCliente;
}

public String getPrevisaoEntrega() {
return previsaoEntrega;
}

public void setPrevisaoEntrega(String previsaoEntrega) {
this.previsaoEntrega = previsaoEntrega;
}

public Double getPesoCubado() {
return pesoCubado;
}

public void setPesoCubado(Double pesoCubado) {
this.pesoCubado = pesoCubado;
}

public Double getPeso() {
return peso;
}

public void setPeso(Double peso) {
this.peso = peso;
}

public Double getValor() {
return valor;
}

public void setValor(Double valor) {
this.valor = valor;
}

public String getNome() {
return nome;
}

public void setNome(String nome) {
this.nome = nome;
}

public List<Movimento> getMovimentos() {
return movimentos;
}

public void setMovimentos(List<Movimento> movimentos) {
this.movimentos = movimentos;
}

public String getCnpj() {
return cnpj;
}

public void setCnpj(String cnpj) {
this.cnpj = cnpj;
}

public Object getValorCliente() {
return valorCliente;
}

public void setValorCliente(Object valorCliente) {
this.valorCliente = valorCliente;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public Tabela getTabela() {
return tabela;
}

public void setTabela(Tabela tabela) {
this.tabela = tabela;
}

public Integer getDiasEntrega() {
return diasEntrega;
}

public void setDiasEntrega(Integer diasEntrega) {
this.diasEntrega = diasEntrega;
}

public String getPossuiInstrucaoEntrega() {
return possuiInstrucaoEntrega;
}

public void setPossuiInstrucaoEntrega(String possuiInstrucaoEntrega) {
this.possuiInstrucaoEntrega = possuiInstrucaoEntrega;
}

public String getModal() {
return modal;
}

public void setModal(String modal) {
this.modal = modal;
}

public Integer getTipoConhecimento() {
return tipoConhecimento;
}

public void setTipoConhecimento(Integer tipoConhecimento) {
this.tipoConhecimento = tipoConhecimento;
}

public List<Object> getRedespachos() {
return redespachos;
}

public void setRedespachos(List<Object> redespachos) {
this.redespachos = redespachos;
}

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}