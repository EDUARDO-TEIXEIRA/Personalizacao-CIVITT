package br.IntegracaoLinCros.model;

import java.util.HashMap;
import java.util.Map;
public class Tabela {

private Integer codigo;
private String nome;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

public Integer getCodigo() {
return codigo;
}

public void setCodigo(Integer codigo) {
this.codigo = codigo;
}

public String getNome() {
return nome;
}

public void setNome(String nome) {
this.nome = nome;
}

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}