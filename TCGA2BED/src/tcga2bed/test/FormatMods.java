/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcga2bed.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import tcga2bed.util.FileOutputWriter;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MD5Checksum;
import tcga2bed.util.MetadataHandler;

/**
 *
 * @author Fabio
 */
public class FormatMods {
    
    public static String bed_root = "E:/ftp-root/bed/";
    
    public static void main(String[] args) {
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseaseMap = HTTPExpInfo.getDiseaseInfo();
        for (String disease: diseaseMap.keySet()) {
            //if (disease.trim().toLowerCase().equals("acc")) {
                System.err.println(disease);
                HashSet<String> dataTypes = new HashSet<>();
                for (String attrib: diseaseMap.get(disease).keySet()) {
                    if (!attrib.toLowerCase().startsWith("disease"))
                        dataTypes.add(attrib.toLowerCase().split("_")[0]);
                }
                for (String dataType: dataTypes) {
                    //if (dataType.trim().toLowerCase().equals("dnamethylation450")) {
                        System.err.println("\t"+dataType);
                        String dataType_path = bed_root+disease.toLowerCase()+"/"+dataType.toLowerCase()+"/";
                        if (dataType.toLowerCase().equals("dnaseq") || dataType.toLowerCase().startsWith("dnamethylation") || dataType.toLowerCase().equals("cnv")) {
                            System.err.println("\t\trecreating *.meta");
                            recreateAllMeta(dataType_path, dataType);
                            System.err.println("\t\trecreating dictionary");
                            recreateDictionary(dataType_path);
                            System.err.println("\t\trecreating header");
                            recreateHeader(dataType_path);
                        }
                        else {
                            HashSet<String> dataSubTypes = new HashSet<>();
                            if (dataType.toLowerCase().equals("rnaseq")) {
                                dataSubTypes.add("gene.quantification");
                                dataSubTypes.add("exon.quantification");
                                dataSubTypes.add("spljxn.quantification");
                            }
                            else if (dataType.toLowerCase().equals("rnaseqv2")) {
                                dataSubTypes.add("gene.quantification");
                                dataSubTypes.add("exon.quantification");
                                dataSubTypes.add("spljxn.quantification");
                                dataSubTypes.add("isoform.quantification");
                            }
                            else if (dataType.toLowerCase().equals("mirnaseq")) {
                                dataSubTypes.add("isoform.quantification");
                                dataSubTypes.add("mirna.quantification");
                            }
                            for (String dataSubType: dataSubTypes) {
                                System.err.println("\t\t"+dataSubType);
                                String dataSubType_path = dataType_path+dataSubType+"/";
                                System.err.println("\t\t\trecreating *.meta");
                                recreateAllMeta(dataSubType_path, dataType);
                                System.err.println("\t\t\trecreating dictionary");
                                recreateDictionary(dataSubType_path);
                                System.err.println("\t\t\trecreating header");
                                recreateHeader(dataSubType_path);
                            }
                        }
                    //}
                }
            //}
        }
    }

    private static void recreateAllMeta(String dataType_path, String dataType) {
        if ((new File(dataType_path)).exists()) {
            for (File f: (new File(dataType_path)).listFiles()) {
                if (f.getName().toLowerCase().endsWith("meta")) {
                    HashMap<String, HashSet<String>> metadata = MetadataHandler.readMetaFile(f);
                    if (metadata.containsKey("manually_curated|exp_data_url"))
                        metadata.remove("manually_curated|exp_data_url");
                    if (metadata.containsKey("manually_curated|exp_nocnv_data_url"))
                        metadata.remove("manually_curated|exp_nocnv_data_url");
                    if (metadata.containsKey("manually_curated|meta_biotab_url"))
                        metadata.remove("manually_curated|meta_biotab_url");
                    if (metadata.containsKey("manually_curated|exp_normalized_data_url"))
                        metadata.remove("manually_curated|exp_normalized_data_url");
                    
                    if (metadata.containsKey("manually_curated|dataType")) {
                        if (dataType.toLowerCase().equals("dnamethylation450") || dataType.toLowerCase().equals("dnamethylation27")) {
                            metadata.remove("manually_curated|dataType");
                            HashSet<String> values = new HashSet<>();
                            values.add(dataType.toLowerCase());
                            metadata.put("manually_curated|dataType", values);
                        }
                    }
                    /*String[] aliquot_split = f.getName().split("\\.");
                    String aliquot = aliquot_split[0]+"."+aliquot_split[1];
                    String aliquot_path = dataType_path+aliquot;
                    String md5sum = MD5Checksum.getMD5Checksum(aliquot_path);
                    HashSet<String> values = new HashSet<>();
                    values.add(md5sum);
                    metadata.put("manually_curated|md5sum", values);*/
                    FileOutputWriter.writeMetaFile(f, metadata);
                }
            }
        }
    }

    private static void recreateDictionary(String dataType_path) {
        if ((new File(dataType_path)).exists()) {
            HashMap<String, HashSet<String>> dictionary = new HashMap<>();
            for (File f: (new File(dataType_path)).listFiles()) {
                if (f.getName().toLowerCase().endsWith("meta")) {
                    HashMap<String, HashSet<String>> metadata = MetadataHandler.readMetaFile(f);
                    for (String attribute: metadata.keySet()) {
                        HashSet<String> values = new HashSet<>();
                        if (dictionary.containsKey(attribute))
                            values = dictionary.get(attribute);
                        values.addAll(metadata.get(attribute));
                        dictionary.put(attribute, values);
                    }
                }
            }
            File metadata_dictionary = new File(dataType_path + "metadata_dictionary.txt");
            FileOutputWriter.writeMetaDictionary(metadata_dictionary, dictionary);
        }
    }

    private static void recreateHeader(String dataType_path) {
        if ((new File(dataType_path)).exists()) {
            ArrayList<String> headers = new ArrayList<>();
            ArrayList<String> types = new ArrayList<>();
            
            try {
                InputStream fstream = new FileInputStream(dataType_path + "header.schema");
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().equals("")) {
                        if (line.toLowerCase().contains("<field") && 
                                !line.toLowerCase().contains("strand") &&
                                !line.toLowerCase().contains("stop") &&
                                !line.toLowerCase().contains("start") &&
                                !line.toLowerCase().contains("chr")) {
                            String[] line_split = line.split(">");
                            String header_str = line_split[1].split("<")[0];
                            headers.add(header_str);
                            String type_str = line_split[0].split("=")[1].replaceAll("\"", "");
                            types.add(type_str);
                        }
                    }
                }
            }
            catch (Exception e) {}
            
            if (!headers.contains("strand")) {
                headers.add(0, "strand");
                types.add(0, "CHAR");
            }
            if (!headers.contains("stop")) {
                headers.add(0, "stop");
                types.add(0, "LONG");
            }
            if (!headers.contains("start")) {
                headers.add(0, "start");
                types.add(0, "LONG");
            }
            if (!headers.contains("chr")) {
                headers.add(0, "chr");
                types.add(0, "STRING");
            }
            
            File header_schema = new File(dataType_path + "header.schema");
            FileOutputWriter.writeGendataHeader(header_schema, headers.toArray(new String[headers.size()]), types.toArray(new String[types.size()]));
        }
    }
    
}
