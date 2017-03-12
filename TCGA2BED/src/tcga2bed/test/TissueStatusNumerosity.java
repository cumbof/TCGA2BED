/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MetadataHandler;

/**
 *
 * @author fabio
 */
public class TissueStatusNumerosity {

    private static final String input = "d:/ftp-root/bed/";
    
    public static void main(String[] args) {
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseaseInfo = HTTPExpInfo.getDiseaseInfo();
        
        for (String disease: diseaseInfo.keySet()) {
            String disease_path = input+disease+"/";
            
            ArrayList<String> dataTypes = new ArrayList<String>();
            dataTypes.add("rnaseqv2");
            
            ArrayList<String> dataSubTypes = new ArrayList<String>();
            dataSubTypes.add("gene");
            dataSubTypes.add("exon");
            dataSubTypes.add("spljxn");
            dataSubTypes.add("isoform");
            
            for (String dt: dataTypes) {
                for (String dst: dataSubTypes) {
                    String optional_subtype_path = dst+".quantification/";
                    File datatype_dictionary_path = new File (disease_path+dt+"/"+optional_subtype_path);
                    if (datatype_dictionary_path.exists()) {
                        int control_count = 0;
                        int tumoral_count = 0;
                        int normal_count = 0;
                        for (File meta: datatype_dictionary_path.listFiles()) {
                            if (meta.getName().toLowerCase().endsWith(".meta")) {
                                HashMap<String, HashSet<String>> metadata = MetadataHandler.readMetaFile(meta);
                                String[] arr = new String[metadata.get("tissue_status").size()];
                                String tissue_status = metadata.get("tissue_status").toArray(arr)[0];
                                if (tissue_status.toLowerCase().equals("control"))
                                    control_count++;
                                else if (tissue_status.toLowerCase().equals("tumoral"))
                                    tumoral_count++;
                                else if (tissue_status.toLowerCase().equals("normal"))
                                    normal_count++;
                            }
                        }
                        System.err.println(disease + " : " + dt + " : " + dst + " : " + "control="+control_count + " : " + "tumoral="+tumoral_count + " : " + "normal="+normal_count);
                    }
                }
            }
        }
    }
    
}
