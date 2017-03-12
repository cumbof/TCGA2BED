/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.statistics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MetadataHandler;

/**
 *
 * @author Fabio
 */
public class Meta2DataTypeTable {
    
    public static void main(String[] args) {
        String root = "G:/ftp-root/bed/";
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseaseMap = HTTPExpInfo.getDiseaseInfo();
        for (String disease: diseaseMap.keySet()) {
            String outputFilePath = root+disease.toLowerCase()+"/meta2dataType_table.csv";
            File outFile = new File(outputFilePath);
            if (outFile.exists())
                outFile.delete();
            HashMap<String, File> meta_dictionaries = getMetaDictionariesPaths(root+disease.toLowerCase()+"/", disease, diseaseMap);
            createMeta2DataTypeTable(meta_dictionaries, outputFilePath);
        }
    }
    
    public static HashMap<String, File> getMetaDictionariesPaths(String root, String disease, HashMap<String, HashMap<String, String>> diseaseMap) {
        HashMap<String, File> dataTypes2files = new HashMap<String, File>();
        HashSet<String> dataTypes = new HashSet<String>();
        for (String attrib: diseaseMap.get(disease).keySet()) {
            File attr_dir = new File(root+attrib.toLowerCase().split("_")[0]+"/");
            if (attr_dir.isDirectory())
                dataTypes.add(attrib.toLowerCase().split("_")[0]);
        }
        for (String dataType: dataTypes) {
            if (dataType.toLowerCase().equals("dnaseq") || dataType.toLowerCase().equals("dnamethylation") || dataType.toLowerCase().equals("cnv")) {
                File f = new File(root + dataType + "/metadata_dictionary.txt");
                if (f.exists()) {
                    dataTypes2files.put(dataType.toLowerCase(), f);
                }
            }
            else {
                HashSet<String> dataSubTypes = new HashSet<String>();
                dataSubTypes.add("gene.quantification");
                dataSubTypes.add("exon.quantification");
                dataSubTypes.add("spljxn.quantification");
                dataSubTypes.add("isoform.quantification");
                dataSubTypes.add("mirna.quantification");
                for (String dataSubType: dataSubTypes) {
                    File f = new File(root + dataType + "/" + dataSubType + "/metadata_dictionary.txt");
                    if (f.exists())
                        dataTypes2files.put(dataType.toLowerCase(), f);
                }
            }
        }
        return dataTypes2files;
    }
    
    public static void createMeta2DataTypeTable(HashMap<String, File> metaDictionaries, String outputFilePath) {
        HashSet<String> attributes_tmp = new HashSet<String>();
        //HashMap<String, HashMap<String, HashSet<String>>> dataType2metadataDictionary = new HashMap<String, HashMap<String, HashSet<String>>>();
        HashMap<String, HashMap<String, Integer>> dataType2attribute2patients = new HashMap<String, HashMap<String, Integer>>();
                
        for (String dataType: metaDictionaries.keySet()) {
            HashMap<String, Integer> attr2patients = new HashMap<String, Integer>();
            if (metaDictionaries.get(dataType).getName().toLowerCase().contains("metadata_dictionary")) {
                File dataTypeFolder = new File(metaDictionaries.get(dataType).getParent());
                for (File meta: dataTypeFolder.listFiles()) {
                    if (meta.getName().toLowerCase().endsWith(".meta")) {
                        HashMap<String, HashSet<String>> meta_content = MetadataHandler.readMetaFile(meta);
                        for (String attr: meta_content.keySet()) {
                            if (attr2patients.containsKey(attr))
                                attr2patients.put(attr, attr2patients.get(attr) + 1);
                            else
                                attr2patients.put(attr, 1);
                            attributes_tmp.add(attr);
                        }
                    }
                }
                
                //HashMap<String, HashSet<String>> metadataDictionary = MetadataHandler.readMetaDictionary(metaDictionaries.get(dataType), "all");
                //dataType2metadataDictionary.put(dataType, metadataDictionary);
                //attributes.addAll(metadataDictionary.keySet());
            }
            dataType2attribute2patients.put(dataType, attr2patients);
        }
        ArrayList<String> attributes = new ArrayList<>(attributes_tmp);
        Collections.sort(attributes);
        
        try {
            FileOutputStream fos = new FileOutputStream(outputFilePath);
            PrintStream out = new PrintStream(fos);
            
            out.print(";");
            //for (String dt: dataType2metadataDictionary.keySet())
            for (String dt: dataType2attribute2patients.keySet())
                out.print(dt + ";");
            out.println();
            for (String attribute: attributes) {
                out.print(attribute + ";");
                /*for (String dataType: dataType2metadataDictionary.keySet()) {
                    if (dataType2metadataDictionary.get(dataType).containsKey(attribute))
                        out.print("1;");
                    else out.print("0;");
                }*/
                for (String dataType: dataType2attribute2patients.keySet()) {
                    if (dataType2attribute2patients.get(dataType).containsKey(attribute))
                        out.print(dataType2attribute2patients.get(dataType).get(attribute)+";");
                    else out.print("0;");
                }
                out.println();
            }
            
            out.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
