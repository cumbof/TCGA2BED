/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.test;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import tcga2bed.Settings;
import tcga2bed.util.FileOutputWriter;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MetadataHandler;

/**
 *
 * @author Fabio
 */
public class RecreateMetadata {
    
    public static String root = "G:/ftp-root/bed/";
    public static String tcga_root = "G:/ftp-root/tcga_original/";
    
    public static void main(String[] args) {
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseaseMap = HTTPExpInfo.getDiseaseInfo();
        for (String disease: diseaseMap.keySet()) {
            //if (disease.toLowerCase().equals("cesc")) {
                System.err.println(disease);
                
                HashSet<String> dataTypes = new HashSet<String>();
                for (String attrib: diseaseMap.get(disease).keySet())
                    dataTypes.add(attrib.toLowerCase().split("_")[0]);
                HashSet<String> disease_tmp = new HashSet<>();
                disease_tmp.add(disease);
                for (String dataType: dataTypes) {
                    //if (dataType.toLowerCase().equals("dnaseq")) {
                        HashSet<String> diseases_4edit = new HashSet<String>(); diseases_4edit.add(disease);
                        HashSet<String> dataTypes_4edit = new HashSet<String>(); dataTypes_4edit.add(dataType);
                        System.err.println("\t"+dataType);
                        String dataType_path = root+disease.toLowerCase()+"/"+dataType.toLowerCase()+"/";
                        if (dataType.toLowerCase().equals("dnaseq") || dataType.toLowerCase().equals("dnamethylation") || dataType.toLowerCase().equals("cnv"))
                            recreateMeta(dataType_path, disease, dataType, "");
                        else {
                            HashSet<String> dataSubTypes = new HashSet<String>();
                            dataSubTypes.add("gene.quantification");
                            dataSubTypes.add("exon.quantification");
                            dataSubTypes.add("spljxn.quantification");
                            dataSubTypes.add("isoform.quantification");
                            dataSubTypes.add("mirna.quantification");
                            for (String dataSubType: dataSubTypes) {
                                System.err.println("\t\t"+dataSubType);
                                String dataSubType_path = dataType_path+dataSubType+"/";
                                recreateMeta(dataSubType_path, disease, dataType, dataSubType);
                            }
                        }
                    //}
                }
            //}
        }
    }
    
    public static void recreateMeta(String dir_path, String disease, String dataType, String dataSubType) {
        try {
            File dir = new File(dir_path);
            if (dir.exists() && dir.isDirectory()) {
                HashSet<String> meta_files = new HashSet<String>();
                HashSet<String> bed_files = new HashSet<String>();
                File[] files = dir.listFiles();
                for (File f: files) {
                    if (f.getName().toLowerCase().endsWith("meta") || f.getName().toLowerCase().contains("metadata"))
                        meta_files.add(f.getCanonicalPath());
                    else if (f.getName().toLowerCase().endsWith("bed"))
                        bed_files.add(f.getCanonicalPath());
                }
                for (String meta_path: meta_files) {
                    File meta_file = new File(meta_path);
                    if (meta_file.exists()) {
                        HashMap<String, HashSet<String>> tmp = new HashMap<String, HashSet<String>>();
                        if (meta_file.getName().toLowerCase().contains("metadata")) {
                            tmp = MetadataHandler.readMetaDictionary(meta_file, "all");
                            tmp = addValues(tmp, "metadata", meta_files, bed_files, meta_file.getName(), disease, dataType, dataSubType);
                            meta_file.delete();
                            FileOutputWriter.writeMetaDictionary((new File(meta_path)), tmp);
                        }
                        else if (meta_file.getName().toLowerCase().endsWith("meta")) {
                            tmp = MetadataHandler.readMetaFile(meta_file);
                            tmp = addValues(tmp, "meta", meta_files, bed_files, meta_file.getName(), disease, dataType, dataSubType);
                            meta_file.delete();
                            FileOutputWriter.writeMetaFile((new File(meta_path)), tmp);
                        }
                    }
                }
            }
        }
        catch (Exception e) {}
    }
    
    public static HashMap<String, HashSet<String>> viewAttributes(HashMap<String, HashSet<String>> tmp, String disease) {
        HashMap<String, HashSet<String>> result = new HashMap<>();
        String tcga_meta_dir = tcga_root + disease.toLowerCase() + "/meta/Clinical/Biotab/";
        File[] meta_files = new File(tcga_meta_dir).listFiles();
        for (File biotab: meta_files) {
            HashMap<String, HashMap<String, String>> biotab_meta = MetadataHandler.readMetadata(biotab);
            for (String p: biotab_meta.keySet()) {
                System.err.println(p);
                for (String attr: biotab_meta.get(p).keySet()) {
                    System.err.println("\t" + attr + "\t" + biotab_meta.get(p).get(attr));
                }
                System.err.println();
            }
        }
        return result;
    }

    public static HashMap<String, HashSet<String>> addValues(HashMap<String, HashSet<String>> tmp, String meta_type, HashSet<String> meta_files, HashSet<String> bed_files, String file_name, String disease, String dataType, String dataSubType) {
        String ftp_url_base = Settings.getFTPBedRepositoryURL();
        try {
            if (!tmp.containsKey("exp_data_bed_url") && !tmp.containsKey("exp_metadata_url")) {
                String dataType_subType = dataType+"/";
                if (!dataSubType.equals(""))
                    dataType_subType += dataSubType+"/";
                HashSet<String> exp_data_bed_url_values = new HashSet<String>();
                HashSet<String> exp_metadata_url_values = new HashSet<String>();
                
                if (meta_type.toLowerCase().equals("meta")) {
                    exp_data_bed_url_values.add(ftp_url_base + disease.toLowerCase() + "/" + dataType_subType + file_name.split("\\.")[0] + ".bed");
                    exp_metadata_url_values.add(ftp_url_base + disease.toLowerCase() + "/" + dataType_subType + file_name.split("\\.")[0] + ".bed.meta");
                }
                else if (meta_type.toLowerCase().equals("metadata")) {
                    for (String bed_path: bed_files) {
                        File bed = new File(bed_path);
                        exp_data_bed_url_values.add(ftp_url_base + disease.toLowerCase() + "/" + dataType_subType + bed.getName().split("\\.")[0] + ".bed");
                        exp_metadata_url_values.add(ftp_url_base + disease.toLowerCase() + "/" + dataType_subType + bed.getName().split("\\.")[0] + ".bed.meta");
                    }
                }
                tmp.put("exp_data_bed_url", exp_data_bed_url_values);
                tmp.put("exp_metadata_url", exp_metadata_url_values);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }
    
}
