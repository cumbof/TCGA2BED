/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

public class MetadataHandler {

    private static HashMap<String, Integer> metaIndexMap = new HashMap<>();

    public static HashMap<String, Integer> getMetaIndexMap() {
        initMetaIndexMap();
        return metaIndexMap;
    }

    public static HashMap<String, HashMap<String, String>> readMetadata(File metaFile) {
        initMetaIndexMap();
        HashMap<String, HashMap<String, String>> metaMap = new HashMap<>();
        String type = getType(metaFile);
        if (type != null) {
            try {
                InputStream fstream = new FileInputStream(metaFile.getAbsolutePath());
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                int row_count = 0;
                int rows_to_skip = 1;
                if (metaFile.getName().toLowerCase().contains("clinical")) {
                    rows_to_skip = 2;
                }
                String[] header = null;
                while ((line = br.readLine()) != null) {
                    if (row_count == 0) {
                        header = line.split("\t");
                    } else if (row_count > 0 && row_count > rows_to_skip) {
                        String[] split = line.split("\t");
                        String patient_ref = split[metaIndexMap.get(type)];
                        HashMap<String, String> attrib = new HashMap<>();
                        for (int i = 0; i < header.length; i++) {
                            attrib.put(type+"|"+header[i], split[i].toLowerCase());
                        }
                        if (metaMap.containsKey(patient_ref)) {
                            attrib.putAll(metaMap.get(patient_ref));
                        }
                        metaMap.put(patient_ref, attrib);
                    }
                    row_count++;
                }
                br.close();
                in.close();
                fstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return metaMap;
    }

    public static String getType(File metaFile) {
        String metaName = metaFile.getName().toLowerCase();
        if (metaIndexMap.isEmpty()) {
            initMetaIndexMap();
        }
        for (String type : metaIndexMap.keySet()) {
            if (metaName.contains(type.toLowerCase())) {
                return type;
            }
        }
        return null;
    }

    // index : starting from 0
    private static void initMetaIndexMap() {
        metaIndexMap.put("biospecimen_aliquot", 2);
        metaIndexMap.put("biospecimen_analyte", 2);
        metaIndexMap.put("biospecimen_cqcf", 1);
        metaIndexMap.put("biospecimen_diagnostic_slides", 2);
        metaIndexMap.put("biospecimen_normal_control", 1);
        metaIndexMap.put("biospecimen_portion", 2);
        metaIndexMap.put("biospecimen_protocol", 2);
        metaIndexMap.put("biospecimen_sample", 1);
        metaIndexMap.put("biospecimen_shipment_portion", 2);
        metaIndexMap.put("biospecimen_slide", 2);
        metaIndexMap.put("biospecimen_tumor_sample", 1);
        metaIndexMap.put("clinical_cqcf", 1);
        metaIndexMap.put("clinical_drug", 2);
        metaIndexMap.put("clinical_follow_up", 2);
        metaIndexMap.put("clinical_nte", 1);
        metaIndexMap.put("clinical_omf", 2);
        metaIndexMap.put("clinical_patient", 1);
        metaIndexMap.put("clinical_radiation", 2);
    }

    public static HashMap<String, HashSet<String>> readMetaDictionary(File dictionary, String dataType) {
        HashMap<String, HashSet<String>> result = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(dictionary.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            String attribute = "";
            HashSet<String> values = new HashSet<>();
            
            while ((line = br.readLine()) != null) {
                if (line.trim().equals("")) {
                    if (!attribute.equals(""))
                        result.put(attribute, values);
                    attribute = "";
                    values = new HashSet<>();
                }
                else {
                    if (attribute.equals(""))
                        attribute = line.trim();
                    else {
                        String[] split = line.split("\t");
                        values.add(split[split.length-1].trim());
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        if (dataType.toLowerCase().equals("all") || result.get("manually_curated|dataType").contains(dataType))
            return result;
        return new HashMap<>();
    }
    
    public static HashMap<String, HashSet<String>> readMetaFile(File meta_file) {
        HashMap<String, HashSet<String>> result = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(meta_file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            HashSet<String> values = new HashSet<>();
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] line_split = line.split("\t");
                    values.add(line_split[1]);
                    result.put(line_split[0], values);
                    values = new HashSet<String>();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
}
