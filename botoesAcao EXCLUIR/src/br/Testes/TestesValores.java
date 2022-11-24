package br.Testes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.crypto.dsig.Transform;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestesValores {
	  public  static  void  main ( String args [])  
	    { 
		  try {
			  inclusaoSerasa();
		} catch (Exception e) {
			// TODO: handle exception
		}
	    } 
	  private void manipulandoXML () throws TransformerException {
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
	  private static void inclusaoSerasa() throws Exception {
			URL url = new URL("https://treina.spc.org.br/spc/remoting/ws/insumo/spc/spcWebService?wsdl");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Content-Type", "application/xml");
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Basic NzI2NTkzMjpXUzA4MTEyMDIy" );
			connection.setRequestProperty("Content-Length", "<calculated when request is sent>");
			connection.setRequestProperty("Host", "<calculated when request is sent>");
			connection.setRequestProperty("User-Agent", "PostmanRuntime/7.29.2");
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Content-Type", "text/xml charset=utf-8");
			
			connection.setDoOutput(true);
			connection.setReadTimeout(30000);
	        connection.setConnectTimeout(30000);
	        String bodyResponse = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.spc.insumo.spcjava.spcbrasil.org/\"> <soapenv:Header/> <soapenv:Body> <web:incluirSpc> <insumoSpc> <tipo-pessoa>J</tipo-pessoa> <dados-pessoa-juridica> <cnpj numero=\"17262755000167\"/> <razao-social>ASSOCIACAO DE CONTRIBUICAO SOCIAL DO BRASIL</razao-social> <nome-comercial>ASSOCIADOS PATRIARCA</nome-comercial> </dados-pessoa-juridica> <data-compra>2014-02-01T21:02:14</data-compra> <data-vencimento>2022-08-22T21:02:14</data-vencimento> <codigo-tipo-devedor>C</codigo-tipo-devedor> <numero-contrato>45CCH888888CCC</numero-contrato> <valor-debito>100.00</valor-debito> <natureza-inclusao> <id>1</id> </natureza-inclusao> <endereco-pessoa> <cep>-11740000</cep> <logradouro>Avenida Condessa</logradouro> <bairro>Centro</bairro> <numero>999</numero> </endereco-pessoa> </insumoSpc> </web:incluirSpc> </soapenv:Body> </soapenv:Envelope>";
	        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	        wr.writeBytes(bodyResponse);
	        wr.flush();
	        wr.close();
	        wr.close();

			try {
				 String responseStatus = connection.getResponseMessage();
				 System.out.println(responseStatus);
				 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				 String inputLine;
				 StringBuffer response = new StringBuffer();
				 
				 while ((inputLine = in.readLine()) != null) {
					 response.append(inputLine);
				 }
				 in.close();
				 System.out.println("response:" + response.toString());

				if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
	                throw new IOException("Retorno durante a conexão: Code Response: " + connection.getResponseCode());
				}
				
			} catch (IOException erro) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				StringBuffer mensagem = new StringBuffer();
				erro.printStackTrace(pw);
	    		//mensagem.append("Erro Exceção: " + erro.getMessage() + sw.toString());
				mensagem.append("Erro de conexão: " + connection.getResponseMessage());
	    		
	    		throw new Exception(mensagem.toString());
			} finally {
				connection.disconnect();
			}
		}
}