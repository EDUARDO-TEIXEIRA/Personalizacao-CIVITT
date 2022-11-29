package br.IntegracaoLinCros.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transportadora {

private List<Transportadora__1> transportadoras = null;
private String status;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

public List<Transportadora__1> getTransportadoras() {
return transportadoras;
}

public void setTransportadoras(List<Transportadora__1> transportadoras) {
this.transportadoras = transportadoras;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}