/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcga2bed.util;

import java.util.ArrayList;

/**
 *
 * @author Fabio
 */
public class OutputFormat {
    
    private static final String endOfLine = "\n";
    private static final String bedSeparator = "\t";
    private static final String csvSeparator = ",";
    private static final String gtfSeparator = "\t";
    private static String prevContentForJSON = "";
    
    /*****************************************************************/
    
    public static String initDocument(String formatExt) {
        if (formatExt.toLowerCase().equals("json"))
            return initJSON();
        else if (formatExt.toLowerCase().equals("xml"))
            return initXML();
        return "";
    }
    
    private static String initJSON() {
        return "{" + endOfLine + "\t\"aliquot\": {" + endOfLine + "\t\t\"data\": [" + endOfLine;
    }
    
    private static String initXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + endOfLine + "<aliquot>" + endOfLine;
    }
    
    /*****************************************************************/
    
    public static String endDocument(String formatExt) {
        if (formatExt.toLowerCase().equals("json"))
            return endJSON();
        else if (formatExt.toLowerCase().equals("xml"))
            return endXML();
        return "";
    }
    
    private static String endJSON() {
        String str = "";
        if (!prevContentForJSON.trim().equals("")) {
            int lastCommaIndex = prevContentForJSON.lastIndexOf(",");
            if (lastCommaIndex > 0)
                str = new StringBuilder(prevContentForJSON).replace(lastCommaIndex, lastCommaIndex+1, "").toString();
        }
        return str + "\t\t]" + endOfLine + "\t}" + endOfLine + "}";
    }
    
    private static String endXML() {
        return "</aliquot>";
    }
    
    /*****************************************************************/
    
    public static String createOutLine(String formatExt, ArrayList<String> values, String[] doc_header) {
        if (formatExt.toLowerCase().equals("bed"))
            return strBED(values);
        else if (formatExt.toLowerCase().equals("csv"))
            return strCSV(values);
        else if (formatExt.toLowerCase().equals("json"))
            return strJSON(values, doc_header);
        else if (formatExt.toLowerCase().equals("xml"))
            return strXML(values, doc_header);
        else if (formatExt.toLowerCase().equals("gtf"))
            return strGTF(values, doc_header);
        return "";
    }

    private static String strBED(ArrayList<String> values) {
        if (!values.isEmpty()) {
            String line = "";
            for (String val: values)
                line = line + val + bedSeparator;
            return line + endOfLine;
        }
        return "";
    }

    private static String strCSV(ArrayList<String> values) {
        if (!values.isEmpty()) {
            String line = "";
            for (String val: values)
                line = line + val + csvSeparator;
            return line + endOfLine;
        }
        return "";
    }
    
    private static String strJSON(ArrayList<String> values, String[] doc_header) {
        String str = "\t\t\t{" + endOfLine;
        int index = 0;
        for (int i=0; i<values.size(); i++) {
            String header = doc_header[index];
            str = str + "\t\t\t\t\""+header+"\": \""+values.get(i)+"\"";
            if ((i+1)>=values.size())
                str = str + endOfLine;
            else
                str = str + "," + endOfLine;
            index++;
        }
        str = str + "\t\t\t}" + "," + endOfLine;
        
        String strToReturn = prevContentForJSON;
        prevContentForJSON = str;
        return strToReturn;
    }

    private static String strXML(ArrayList<String> values, String[] doc_header) {
        String str = "\t<data>" + endOfLine;
        int index = 0;
        for (String val: values) {
            String header = doc_header[index];
            str = str + "\t\t<"+header+">" + val + "</"+header+">" + endOfLine;
            index++;
        }
        str = str + "\t</data>" + endOfLine;
        return str;
    }
    
    private static String strGTF(ArrayList<String> values, String[] doc_header) {
        if (!values.isEmpty()) {
            String chr = values.get(0);
            String source = "TCGA2BED";
            String feature = "TCGA_Region";
            String start = values.get(1);
            String end = values.get(2);
            String score = ".";
            String strand = (values.get(3).trim().equals("*")) ? "." : values.get(3);
            String frame = ".";
            
            String line = chr + gtfSeparator + 
                          source + gtfSeparator + 
                          feature + gtfSeparator + 
                          start + gtfSeparator + 
                          end + gtfSeparator + 
                          score + gtfSeparator + 
                          strand + gtfSeparator + 
                          frame + gtfSeparator;
            
            // skip first 4 elements            
            for (int i=0; i<values.size(); i++) {
                if (i > 3) {
                    line = line + doc_header[i] + " " + "\""+values.get(i)+"\"" + "; ";
                }
            }
            return line + endOfLine;
        }
        return "";
    }

}
