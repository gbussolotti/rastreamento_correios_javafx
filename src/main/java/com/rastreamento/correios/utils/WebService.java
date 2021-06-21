package com.rastreamento.correios.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rastreamento.correios.model.CadItem;
import com.rastreamento.correios.model.CadItemStatus;
import javafx.scene.control.Alert;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author gusta
 */
public class WebService {

    public Object consumirWebService(Object item) throws Erro {
        String objeto = "";
        if (item instanceof CadItem) {
            objeto = "<objetos>" + ((CadItem) item).getTrackCode() + "</objetos>";
        } else if (item instanceof ArrayList) {

            ArrayList<CadItem> lista = (ArrayList<CadItem>) item;
            if (!lista.isEmpty()) {
                objeto = lista.stream().map((i) -> "<objetos>" + i.getTrackCode() + "</objetos>").reduce(objeto, String::concat);
            }
        }

        if (objeto.isEmpty()) {
            throw new Erro(Alert.AlertType.INFORMATION, "Nenhum objeto passado como parâmetro para consulta", null);
        }

        try {
            String responseString;
            String outputString = "";
            String wsURL = "http://webservice.correios.com.br:80/service/rastro";
            URL url = new URL(wsURL);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            String xmlInput = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:res=\"http://resource.webservice.correios.com.br/\">\n"
                    + "<soapenv:Header/>"
                    + "<soapenv:Body>"
                    + "<res:buscaEventosLista>"
                    + "<usuario>UFALCOMPUT</usuario>"
                    + "<senha>C9S>6E8RZJ</senha>"
                    + "<tipo>1</tipo>"
                    + "<resultado>T</resultado>"
                    + "<lingua>101</lingua>"
                    + objeto
                    + "</res:buscaEventosLista>"
                    + "</soapenv:Body>"
                    + "</soapenv:Envelope>";

            byte[] buffer = xmlInput.getBytes();
            bout.write(buffer);
            byte[] b = bout.toByteArray();
            String SOAPAction = "<SOAP action of the webservice to be consumed>";

            httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
            httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            httpConn.setRequestProperty("SOAPAction", SOAPAction);
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            OutputStream out = httpConn.getOutputStream();

            out.write(b);
            out.close();

            InputStreamReader isr = null;
            if (httpConn.getResponseCode() == 200) {
                isr = new InputStreamReader(httpConn.getInputStream());
            } else {
                isr = new InputStreamReader(httpConn.getErrorStream());
            }

            BufferedReader in = new BufferedReader(isr);
            while ((responseString = in.readLine()) != null) {
                outputString = outputString + responseString;
            }

            return efetuarLeituraXML(outputString, item);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new Erro(Alert.AlertType.ERROR, "Erro na consulta ao web service", ex.getMessage());
        }
    }


    private Object efetuarLeituraXML(String str, Object item) throws Erro {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(str.getBytes()));

            NodeList list = doc.getElementsByTagName("objeto");
            if (list.getLength() == 0) {
                throw new Erro(Alert.AlertType.INFORMATION, "Erro na formatação do xml de retorno:\nnNenhum elemento localizado para a tag informada: objeto", null);
            } else {
                //Localizou elementos. Irá verificar se a consulta é através de um objeto ou através de uma lista
                if (item instanceof CadItem) {
                    //A consulta é através de um objeto específico
                    Node node = list.item(0);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String numero = element.getElementsByTagName("numero").item(0).getTextContent();

                        if (numero.equalsIgnoreCase("Erro")) {
                            //Localizou algum erro com a requisição. Ex: Usuário ou senha incorreto
                            String erro = element.getElementsByTagName("erro").item(0).getTextContent();
                            throw new Erro(Alert.AlertType.INFORMATION, "Erro na requisição com o web service", erro);
                        }

                        return leituraInternaXML((CadItem) item, numero, element);

                    }
                } else if (item instanceof ArrayList) {
                    //A consulta será através de uma lista de objetos
                    ArrayList<CadItem> lista = (ArrayList<CadItem>) item;
                    for (int temp = 0; temp < list.getLength(); temp++) {
                        Node node = list.item(temp);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;

                            String numero = element.getElementsByTagName("numero").item(0).getTextContent();

                            if (numero.equalsIgnoreCase("Erro")) {
                                //Localizou algum erro com a requisição. Ex: Usuário ou senha incorreto
                                String erro = element.getElementsByTagName("erro").item(0).getTextContent();
                                throw new Erro(Alert.AlertType.INFORMATION, "Erro na requisição com o web service", erro);
                            }

                            //Não foi localizado nenhum erro, deverá procurar o item na lista de objetos e fazer os setters para retornar a lista
                            for (CadItem i : lista) {
                                if (i.getTrackCode().equalsIgnoreCase(numero)) {
                                    i = leituraInternaXML(i, numero, element);
                                    break;
                                }
                            }
                        }
                    }
                    return lista;
                }

            }
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            ex.printStackTrace();
            throw new Erro("Erro ao consultar o Web Service: " + ex.getMessage());
        }
        throw new Erro(Alert.AlertType.ERROR, "Erro na consulta com web service", "Nenhum objeto válido foi retornado");
    }

    private CadItem leituraInternaXML(CadItem item, String numero, Element element) {

        try {
            //Tag de erro localizada
            String erro = element.getElementsByTagName("erro").item(0).getTextContent();
            if (erro.equalsIgnoreCase("Objeto não encontrado na base de dados dos Correios.")) {
                //Esse objeto está aguardando postagem
                item.setDescriptioUpdate("Objeto aguardando postagem");
                item.setDtUpdate(null);
            }

        } catch (NullPointerException e) {
            //Não foi localizada tag de Erro, pode prosseguir com a leitura do xml
            NodeList listaEventosXML = element.getElementsByTagName("evento");
            for (int i = 0; i < listaEventosXML.getLength(); i++) {
                Node nodeInternoListaEventos = listaEventosXML.item(i);

                if (nodeInternoListaEventos.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementoInternoListaEventos = (Element) nodeInternoListaEventos;

                    if (elementoInternoListaEventos.hasChildNodes()) {
                        CadItemStatus status = new CadItemStatus();

                        NodeList internalList = elementoInternoListaEventos.getChildNodes();

                        String dataXML = "";
                        String horaXML = "";
                        String tipoEvento = "";
                        String statusTexto = "";

                        for (int j = 0; j < internalList.getLength(); j++) {
                            String text = internalList.item(j).getTextContent();
                            switch (internalList.item(j).getNodeName()) {
                                case "tipo":
                                    tipoEvento = text;
                                    status.setTypeEvent(text);
                                    break;
                                case "status":
                                    statusTexto = text;
                                    break;
                                case "data":
                                    dataXML = text;
                                    break;
                                case "hora":
                                    horaXML = text;
                                    break;
                                case "descricao":
                                    status.setEventDescription(text);
                                    break;
                                case "detalhe":
                                    status.setEventDetail(text);
                                    break;
                                case "local":
                                    status.setEventLocal(text);
                                    break;
                                case "cidade":
                                    status.setEventCity(text);
                                    break;
                                case "uf":
                                    status.setEventUf(text);
                                    break;

                                case "destino":
                                    Node listaDestino = internalList.item(j);
                                    if (listaDestino.getNodeType() == Node.ELEMENT_NODE) {
                                        Element elementoInternoListaDestino = (Element) listaDestino;

                                        if (elementoInternoListaDestino.hasChildNodes()) {
                                            NodeList listaInternaDestino = elementoInternoListaDestino.getChildNodes();

                                            for (int k = 0; k < listaInternaDestino.getLength(); k++) {

                                                String textoDestino = listaInternaDestino.item(k).getTextContent();
                                                switch (listaInternaDestino.item(k).getNodeName()) {
                                                    case "local":
                                                        status.setDestinationLocal(textoDestino);
                                                        break;
                                                    case "cidade":
                                                        status.setDestinationCity(textoDestino);
                                                        break;
                                                    case "bairro":
                                                        status.setDestinationBairro(textoDestino);
                                                        break;
                                                    case "uf":
                                                        status.setDestinationUf(textoDestino);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                    break;

                                case "endereco":
                                    Node listaEndereco = internalList.item(j);
                                    if (listaEndereco.getNodeType() == Node.ELEMENT_NODE) {
                                        Element elementoInternoListaEndereco = (Element) listaEndereco;

                                        if (elementoInternoListaEndereco.hasChildNodes()) {
                                            NodeList listaInternaEndereco = elementoInternoListaEndereco.getChildNodes();

                                            for (int k = 0; k < listaInternaEndereco.getLength(); k++) {

                                                String textoEndereco = listaInternaEndereco.item(k).getTextContent();
                                                switch (listaInternaEndereco.item(k).getNodeName()) {
                                                    case "cep":
                                                        status.setAddressCep(textoEndereco);
                                                        break;
                                                    case "logradouro":
                                                        status.setAddressStreet(textoEndereco);
                                                        break;
                                                    case "numero":
                                                        status.setAddressNumber(textoEndereco);
                                                        break;
                                                    case "localidade":
                                                        status.setAddressLocation(textoEndereco);
                                                        break;
                                                    case "uf":
                                                        status.setAddressUf(textoEndereco);
                                                        break;
                                                    case "bairro":
                                                        status.setAddressBairro(textoEndereco);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }

                        try {
                            status.setDtEvent(LocalDateTime.parse(dataXML + " " + horaXML, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                            if (item.getDtUpdate() == null) {
                                item.setDtUpdate(status.getDtEvent());
                                item.setDescriptioUpdate(status.getEventDescription());
                            } else {
                                //Compare dtEvent with last update status
                                //if status is before, update CadItem
                                if (status.getDtEvent().isAfter(item.getDtUpdate())) {
                                    item.setDtUpdate(status.getDtEvent());
                                    item.setDescriptioUpdate(status.getEventDescription());
                                }
                            }

                            //Define the post date
                            if (item.getDtPost() == null && status.getTypeEvent().equals("PO")) {
                                item.setDtPost(status.getDtEvent().toLocalDate());
                            }
                        } catch (DateTimeParseException ex) {
                            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        //Agora que o objeto já está completo, irá percorrer a lista de status do objeto de rastreio e verificar se esse status já havia sido adicionado
                        boolean adicionar = true;
                        if (item.getItemStatusList() != null && item.getItemStatusList().size() > 0) {
                            for (CadItemStatus is : item.getItemStatusList()) {
                                if (is.equals(status)) {
                                    adicionar = false;
                                    break;
                                }
                            }
                        }

                        //Caso não tenha localizado nenhum objeto de status semelhante, irá fazer a adição do item à listade status
                        if (adicionar) {

                            status.setItem(item);
                            if (item.getItemStatusList() == null) {
                                item.setItemStatusList(new ArrayList<>());
                            }

                            item.getItemStatusList().add(status);


                            /*
                                                Além de adicionar, irá verificar se precisa marcar o objeto como concluído para não precisar consultar novamente
                                                Irá checar se o item está marcado para consultar e seguirá a orientação do manual dos correios

                                            Para isso, indicamos que todos os objetos que forem retornados com o evento tipo
                                            BDE, BDI e BDR com status
                                            01, 12, 23, 50, 51, 52, 43, 67, 68, 70, 71, 72, 73, 74, 75, 76 e 80
                                            e FC 11 estão com o histórico concluído. Não será mais necessário enviá-los para novas consultas.

                             */
                            ArrayList<String> listaTiposEventos = new ArrayList<>(Arrays.asList("BDE", "BDI", "BDR"));
                            ArrayList<String> listaCodigos = new ArrayList<>(Arrays.asList("01", "12", "23", "50", "51", "52", "43", "67", "68", "70", "71", "72", "73", "74", "75", "76", "80"));
                            if (item.getToConsult() && ((listaTiposEventos.contains(tipoEvento) && listaCodigos.contains(statusTexto))
                                    || (tipoEvento.equals("FC") && statusTexto.equals("11")))) {
                                item.setToConsult(Boolean.FALSE);
                            }

                        }
                    }
                }
            }
        }
        return item;
    }

}
