/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.util;

import java.io.File;
import java.util.HashMap;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLReader {

	//private static ArrayList<String> acceptedItems;
    public static String getMetadataURL(String file_path) {
        String status_check_url = "";
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(file_path));

            // normalize text representation
            doc.getDocumentElement().normalize();
            //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            NodeList listOfClasses = doc.getElementsByTagName("job-process");

            for (int i = 0; i < listOfClasses.getLength(); i++) {
                Node node = listOfClasses.item(i);
                for (int j = 0; j < node.getChildNodes().getLength(); j++) {

                    Node child = node.getChildNodes().item(j);
                    if (child.getNodeName().equals("status-check-url")) {
                        //PRENDI IL VALUE DEL FIELD
                        status_check_url = child.getTextContent();
                    }

                }
            }

        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return status_check_url;
    }

    public static String getMetadataDataURL(String file_path) {
        String metadata_archive_url = "";
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(file_path));

            // normalize text representation
            doc.getDocumentElement().normalize();
            //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            NodeList listOfClasses = doc.getElementsByTagName("job-status");

            for (int i = 0; i < listOfClasses.getLength(); i++) {
                Node node = listOfClasses.item(i);
                for (int j = 0; j < node.getChildNodes().getLength(); j++) {

                    Node child = node.getChildNodes().item(j);
                    if (child.getNodeName().equals("archive-url")) {
                        //PRENDI IL VALUE DEL FIELD
                        metadata_archive_url = child.getTextContent();
                    }

                }
            }

        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return metadata_archive_url;
    }

    public static HashMap<String, HashMap<String, String>> getData(String file_path) {
        //acceptedItems = initItemsList();
        HashMap<String, HashMap<String, String>> result = new HashMap<>();
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(file_path));

            // normalize text representation
            doc.getDocumentElement().normalize();
            //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            NodeList listOfClasses = doc.getElementsByTagName("class");

            for (int i = 0; i < listOfClasses.getLength(); i++) {
                HashMap<String, String> record = new HashMap<>();

                Node node = listOfClasses.item(i);
                for (int j = 0; j < node.getChildNodes().getLength(); j++) {

                    Node child = node.getChildNodes().item(j);
                    if (child.getNodeName().equals("field")) {
                        if (child.getAttributes().getLength() > 1) {
                            //CERCA HREF E PRENDI IL VALUE DELL'ATTRIBUTO
                            String name = "";
                            String value = "";
                            for (int l = 0; l < child.getAttributes().getLength(); l++) {
                                if (child.getAttributes().item(l).toString().split("\"")[0].startsWith("xlink:href")) {
                                    value = child.getAttributes().item(l).toString().split("\"")[1];
                                } else if (child.getAttributes().item(l).toString().split("\"")[0].startsWith("name")) {
                                    name = child.getAttributes().item(l).toString().split("\"")[1];
                                }
                            }
                            record.put(name, value);

                        } else {
                            //CERCA NAME E PRENDI IL VALUE DEL FIELD
                            String name = "";
                            for (int l = 0; l < child.getAttributes().getLength(); l++) {
                                if (child.getAttributes().item(l).toString().split("\"")[0].startsWith("name")) {
                                    name = child.getAttributes().item(l).toString().split("\"")[1];
                                }
                            }
                            record.put(name, child.getTextContent());

                        }
                    }

                }
                try {
                    result.put(node.getAttributes().getNamedItem("recordNumber").toString().split("\"")[1], record);
                } catch (NullPointerException e) {
                }
            }

        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> getMetadata(File metaFile, HashMap<String, String> parameter2value_filterMap, String shared_id) {
        HashMap<String, String> metadata = new HashMap<>();
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(metaFile);

            // normalize text representation
            doc.getDocumentElement().normalize();
            //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            NodeList listOfClasses = doc.getElementsByTagName(shared_id.toLowerCase() + ":patient");

            for (int i = 0; i < listOfClasses.getLength(); i++) {
                Node node = listOfClasses.item(i);
                for (int j = 0; j < node.getChildNodes().getLength(); j++) {

                    Node child = node.getChildNodes().item(j);

                    if (child.getNodeName().startsWith("shared:")) {
                        //PRENDI IL VALUE DEL FIELD
                        metadata.put(child.getNodeName().split(":")[1], child.getTextContent());
                    }

                }
            }

        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        for (String param : parameter2value_filterMap.keySet()) {
            //if (metadata.containsKey("shared:"+param)) {
            if (metadata.containsKey(param)) {
                if (param.toUpperCase().equals("DAYS_TO_BIRTH")) {
                    String filter_age = parameter2value_filterMap.get(param);
                    //String meta_days = metadata.get("shared:"+param).toUpperCase();
                    String meta_days = metadata.get(param).toUpperCase();
                    String meta_age = DateUtils.retrieveAgeFromDaysToBirth(meta_days);
                    if (!meta_age.equals(filter_age)) {
                        return (new HashMap<>());
                    }
                } //else if (!metadata.get("shared:"+param).toUpperCase().equals(parameter2value_filterMap.get(param).toUpperCase()))
                else if (!metadata.get(param).toUpperCase().equals(parameter2value_filterMap.get(param).toUpperCase())) {
                    return (new HashMap<>());
                }
            }
        }

        return metadata;
    }

    public static String getEntrezFromHugo(String hugoXmlFile) {
        String entrez = "";
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(hugoXmlFile));

            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList docNodes = doc.getElementsByTagName("doc");
            Node docNode = docNodes.item(0);
            //System.out.println("Node Length: "+docNodes.getLength());

            if (docNodes.getLength() > 0) {
                for (int i = 0; i < docNode.getChildNodes().getLength(); i++) {
                    Node child = docNode.getChildNodes().item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element elem = (Element) child;
	            		//System.err.println("-> " + elem.getNodeName());
                        //System.out.println("NAME: " + elem.getNodeName() + "\tVALUE: " + elem.getTextContent() + "\tATT_NAME: " + elem.getAttributes().getNamedItem("entrez_id").toString());
                        if (elem.getAttributes().item(0).toString().equals("name=\"entrez_id\"")) {
                            //System.out.println("ATT: " + elem.getAttributes().item(0));
                            entrez = elem.getTextContent();
                            //System.out.println("ENTREZ: " + entrez);
                            break;
                        }
                    }
                }
            } else {
                entrez = "";
            }

        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return entrez;
    }

}
