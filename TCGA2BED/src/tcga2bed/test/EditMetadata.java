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
import tcga2bed.util.FileOutputWriter;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MetadataHandler;

/**
 *
 * @author Fabio
 */
public class EditMetadata {
    
    public static void main(String[] args) {
        String root = "D:/ftp-root/bed/";
        String tcga_original_path = "D:/ftp-root/tcga_original/";
        
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseaseMap = HTTPExpInfo.getDiseaseInfo();
        HashSet<String> dataTypes_tmp = new HashSet<String>();
        dataTypes_tmp.add("dnamethylation"); 
        dataTypes_tmp.add("dnaseq"); 
        dataTypes_tmp.add("rnaseq"); 
        dataTypes_tmp.add("rnaseqv2"); 
        dataTypes_tmp.add("mirnaseq");
        dataTypes_tmp.add("cnv");
        editMeta(root, (new HashSet<String>(diseaseMap.keySet())), dataTypes_tmp, tcga_original_path);
        for (String disease: diseaseMap.keySet()) {
            //if (disease.toLowerCase().equals("acc")) {
                System.err.println(disease);
                
                HashSet<String> dataTypes = new HashSet<String>();
                for (String attrib: diseaseMap.get(disease).keySet())
                    dataTypes.add(attrib.toLowerCase().split("_")[0]);
                HashSet<String> disease_tmp = new HashSet<>();
                disease_tmp.add(disease);
                editMeta(root+disease.toLowerCase()+"/", disease_tmp, dataTypes, tcga_original_path);
                for (String dataType: dataTypes) {
                    //if (dataType.toLowerCase().equals("dnaseq")) {
                        HashSet<String> diseases_4edit = new HashSet<String>(); diseases_4edit.add(disease);
                        HashSet<String> dataTypes_4edit = new HashSet<String>(); dataTypes_4edit.add(dataType);
                        System.err.println("\t"+dataType);
                        String dataType_path = root+disease.toLowerCase()+"/"+dataType.toLowerCase()+"/";
                        if (dataType.toLowerCase().equals("dnaseq") || dataType.toLowerCase().equals("dnamethylation") || dataType.toLowerCase().equals("cnv"))
                            editMeta(dataType_path, diseases_4edit, dataTypes_4edit, tcga_original_path);
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
                                editMeta(dataSubType_path, diseases_4edit, dataTypes_4edit, tcga_original_path);
                            }
                        }
                    //}
                }
            //}
        }
    }
    
    public static void editMeta(String dir_path, HashSet<String> diseases, HashSet<String> dataTypes, String tcga_original_path) {
        try {
            File dir = new File(dir_path);
            if (dir.exists() && dir.isDirectory()) {
                HashSet<String> meta_files = new HashSet<String>();
                File[] files = dir.listFiles();
                for (File f: files) {
                    if (f.getName().toLowerCase().endsWith("meta") || f.getName().toLowerCase().contains("metadata"))
                        meta_files.add(f.getCanonicalPath());
                }
                for (String meta_path: meta_files) {
                    File meta_file = new File(meta_path);
                    if (meta_file.exists()) {
                        HashMap<String, HashSet<String>> tmp = new HashMap<String, HashSet<String>>();
                        if (meta_file.getName().toLowerCase().contains("metadata"))
                            tmp = MetadataHandler.readMetaDictionary(meta_file, "all");
                        else if (meta_file.getName().toLowerCase().endsWith("meta"))
                            tmp = MetadataHandler.readMetaFile(meta_file);
                        tmp = editValues(tmp, "exp_data_url", diseases, dataTypes, tcga_original_path);
                        meta_file.delete();
                        if (meta_file.getName().toLowerCase().contains("metadata"))
                            FileOutputWriter.writeMetaDictionary((new File(meta_path)), tmp);
                        else if (meta_file.getName().toLowerCase().endsWith("meta"))
                            FileOutputWriter.writeMetaFile((new File(meta_path)), tmp);
                    }
                }
            }
        }
        catch (Exception e) {}
    }

    public static HashMap<String, HashSet<String>> editValues(HashMap<String, HashSet<String>> tmp, String attribute, HashSet<String> diseases, HashSet<String> dataTypes, String tcga_original_path) {
        try {
            /*if (tmp.containsKey(attribute)) {
                HashSet<String> values = tmp.get(attribute);
                tmp.put("tissue_status", values);
                tmp.remove("patient_status");
            }*/
            if (tmp.containsKey(attribute)) {
                HashSet<String> values = tmp.get(attribute);
                HashSet<String> values_tmp = new HashSet<String>();
                for (String val: values) {
                    String[] val_split = val.split("/");
                    String file_name = val_split[val_split.length-1];
                    
                    String to_remove = val_split[val_split.length-2] + "/" + val_split[val_split.length-1];
                    String url_base = val.replaceAll(to_remove, "");
                    
                    for (String disease: diseases) {
                        for (String dataType: dataTypes) {
                            String url_base_tmp = "";
                            
                            File file_tmp = File.createTempFile("http_tcga", "html");
                            String out_path = file_tmp.getAbsolutePath();
        // --------->
                            /*HashMap<String, String> folder2date = HTTPExpInfo.getData2Date(url_base, out_path, disease, dataType, "/", "_data_dir_prefix");
                            file_tmp.delete();

                            for (String dir: folder2date.keySet()) {
                                file_tmp = File.createTempFile("http_tcga", "html");
                                out_path = file_tmp.getAbsolutePath();
                                String tmp_folder_url = url_base + dir + "/";
                                HashSet<String> tcga_files = HTTPExpInfo.getFiles(tmp_folder_url, out_path, "/");
                                file_tmp.delete();
                                if (tcga_files.contains(file_name)) {
                                    url_base_tmp = url_base + dir + "/";
                                    break;
                                }
                            }*/
                            
                            if (!url_base_tmp.equals("")) {
                                String file_path = tcga_original_path+disease+"/"+dataType+"/"+file_name;
                                File original_file = new File(file_path);
                                if (original_file.exists()) {
                                    val = url_base_tmp+original_file.getCanonicalFile().getName();
                                    break;
                                }
                            }
                            else {
                                throw new Exception();
                            }
                        }
                    }
                    values_tmp.add(val);
                }
                tmp.put(attribute, values_tmp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }
    
}
