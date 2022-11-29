package br.Testes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.sound.midi.Soundbank;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sankhya.util.TimeUtils;

public class TestesValores {
	 
	  public  static  void  main ( String args []) throws Exception  
	  {  	        
		  	URL url = new URL("https://deployment.transpofrete.com.br/api/v3/calculo/calcularNota");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer eBY9YydOYRH8cv3mT03gntbUWImFPsG8R0h4feVcaD");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", "<calculated when request is sent>");
			connection.setRequestProperty("Host", "<calculated when request is sent>");
			connection.setDoOutput(true);
			String bodyResquest = "{\r\n\t\"cnpjUnidade\": \"32463085000130\",\r\n\t\"remetente\": \"32463085000130\",\r\n\t\"peso\": 255,\r\n\t\"cubagem\": 0,\r\n\t\"pesoCubado\": 0,\r\n\t\"valor\": 5300,\r\n\t\"volumes\": 4,\r\n\t\"abono\": 0,\r\n\t\"percentualValorCliente\": 0,\r\n\t\"cepOrigem\": 26168081,\r\n\t\"cepDestino\": 68440000,\r\n\t\"data\": \"2022-11-25\",\r\n\t\"modalidadeFrete\": 0,\r\n\t\"tipoOperacao\": 1,\r\n\t\"transportadora\": \"\",\r\n\t\"placa\": \"\"\r\n}";
			
			try(OutputStream os = connection.getOutputStream()) {
			    byte[] input = bodyResquest.getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}
			
			try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					        response.append(responseLine.trim());
					    }
			    System.out.println(response.toString());
			}
				
	    } 
	  private void manipulandoXML() throws TransformerException {
	  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse("modelo.requisicao_inclusao.xml");
			doc.getDocumentElement().normalize();
			Element raiz = doc.getDocumentElement();
			
			System.out.println(raiz.getNodeName());				
			NodeList corpoXML = doc.getElementsByTagName("insumoSpc");
			
			for (int i = 0; i < corpoXML.getLength(); i++) {
				Node node = corpoXML.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element dados = (Element) node;
					dados.setAttribute(dados.getElementsByTagName("razao-social").item(i).getTextContent(), "teste");
					
					System.out.println("Informação: " + dados.getElementsByTagName("razao-social").item(i).getTextContent());
					TransformerFactory docSave = TransformerFactory.newInstance();
					docSave.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
					Transformer xformer = docSave.newTransformer();
					xformer.setOutputProperty(OutputKeys.INDENT, "yes");
					Writer output = new StringWriter();
					xformer.transform(new DOMSource(doc), new StreamResult(output));
					
					System.out.println("Informação: " + dados.getElementsByTagName("razao-social").item(i).getTextContent());
				}
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	  public static void operacaoSerasa() throws Exception {
			URL url = new URL("https://treina.spc.org.br/spc/remoting/ws/insumo/spc/spcWebService?wsdl\"");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			//String requestBodyXML = "<?xml version='1.0' encoding='UTF-8'?> <S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"> <S:Body> <ns2:Fault xmlns:ns2=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns3=\"http://www.w3.org/2003/05/soap-envelope\"> <faultcode>IE_SPC005.E10</faultcode> <faultstring>Registro não encontrado para a exclusão</faultstring> </ns2:Fault> </S:Body> </S:Envelope>";
			String requestBodyXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.spc.insumo.spcjava.spcbrasil.org/\"> <soapenv:Header/> <soapenv:Body> <web:excluirSpc> <excluir> <tipo-pessoa>J</tipo-pessoa> <dados-pessoa-juridica> <cnpj numero=\"17262755000167\"/> <razao-social>PR LUNA DE AZEVEDO FILHO</razao-social> <nome-comercial>RB DISTRIBUIDORA</nome-comercial> </dados-pessoa-juridica> <data-vencimento>2022-11-01T00:00:00</data-vencimento> <numero-contrato>97803</numero-contrato> <motivo-exclusao> <id>1</id> </motivo-exclusao> </excluir> </web:excluirSpc> </soapenv:Body> </soapenv:Envelope>";
			StringBuffer response = new StringBuffer();
			String inputLine = "";
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Basic NzI2NTkzMjpXUzA4MTEyMDIy");
			connection.setRequestProperty("Content-Length", "<calculated when request is sent>");
			connection.setRequestProperty("Host", "<calculated when request is sent>");
			connection.setRequestProperty("User-Agent", "PostmanRuntime/7.29.2");
			connection.setRequestProperty("Accept", "application/xml?");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Content-Type", "text/xml charset=utf-8");
			
			connection.setDoOutput(true);
			connection.setReadTimeout(30000);
	        connection.setConnectTimeout(30000);
	                
	        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	        wr.writeBytes(requestBodyXML);
	        wr.flush();
	        wr.close();

			try {
				int responseCode = connection.getResponseCode();

				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				 System.out.println(in.readLine());
				 
				 in.close();
				System.out.println("Resposta: " + connection.getResponseMessage());
			} catch (Exception erro) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
				String resultado = in.readLine();
				 String xmlReturn = ""; 
				int inicio = resultado.indexOf("<faultcode>");
		         int fim = resultado.indexOf("</faultcode>");
		         xmlReturn  = resultado.substring(inicio + 11, fim);
		         inicio = resultado.indexOf("<faultstring>");
		         fim = resultado.indexOf("</faultstring>");
	 	         
		         xmlReturn += "-" + resultado.substring(inicio + 13, fim);
	 	         
	 	         System.out.println(xmlReturn);

				 
			} finally {
				connection.disconnect();
			}
		}
}