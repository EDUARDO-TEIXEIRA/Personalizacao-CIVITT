package br.IntegracaoLinCros.model;

import java.util.HashMap;
import java.util.Map;

public class Movimento {

private String codigo;
private String observacao;
private Double valor;
private String descricao;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

public String getCodigo() {
return codigo;
}

public void setCodigo(String codigo) {
this.codigo = codigo;
}

public String getObservacao() {
return observacao;
}

public void setObservacao(String observacao) {
this.observacao = observacao;
}

public Double getValor() {
return valor;
}

public void setValor(Double valor) {
this.valor = valor;
}

public String getDescricao() {
return descricao;
}

public void setDescricao(String descricao) {
this.descricao = descricao;
}

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}