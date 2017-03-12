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
import tcga2bed.parser.BioParser;
import tcga2bed.parser.GenericParser;
import tcga2bed.util.HTTPExpInfo;

/**
 *
 * @author Fabio
 */
public class Statistics {
    
    public static void main(String[] args) {
        String rootFolder = "D:/ftp-root/bed/";
        HashMap<String, HashMap<String, HashSet<String>>> gendata_statistics = new HashMap<>();
            
        File[] diseases = (new File(rootFolder)).listFiles();
        for (File disease: diseases) {
            if (disease.isDirectory()) {
                File[] data_types = disease.listFiles();

                HashMap<String, HashSet<String>> dataType_data = new HashMap<>();
                for (File data_type: data_types) {
                    if (data_type.isDirectory()) {
                        File[] data_dirs = data_type.listFiles();
                        HashSet<String> patients = null;
                        for (File data_dir: data_dirs) {
                            String dir_name = data_dir.getName().toLowerCase();
                            if (data_dir.isDirectory() && (dir_name.contains("gene") || dir_name.contains("exon") || dir_name.contains("isoform") || dir_name.contains("spljxn") || dir_name.contains("mirna"))) {
                                patients = new HashSet<>();

                                File[] files = data_dir.listFiles();
                                for (File f: files) {
                                    if (f.getName().toLowerCase().contains("bed")) {
                                        patients.add(f.getName().toLowerCase().split("\\.")[0]);
                                    }
                                }
                                
                                String datatype_identifier = data_type.getName().toLowerCase();
                                if (!dir_name.contains("dnamethylation") && !dir_name.contains("dnaseq") && !dir_name.contains("cnv"))
                                    datatype_identifier = datatype_identifier + "-" + dir_name.split("\\.")[0];
                                dataType_data.put(datatype_identifier, patients);
                            }
                            else if (data_dir.isFile() && data_dir.getName().toLowerCase().endsWith("bed")) {
                                if (patients == null)
                                    patients = new HashSet<String>();
                                patients.add(data_dir.getName().toLowerCase().split("\\.")[0]);
                            }
                        }
                        if (data_type.getName().toLowerCase().startsWith("dnamethylation") || data_type.getName().toLowerCase().equals("dnaseq") || data_type.getName().toLowerCase().equals("cnv")) {
                            String datatype_identifier = data_type.getName().toLowerCase();
                            dataType_data.put(datatype_identifier, patients);
                        }
                    }
                }

                gendata_statistics.put(disease.getName().toLowerCase(), dataType_data);
            }
        }
        
        createStatisticsFile(rootFolder, gendata_statistics);
    }

    private static void createStatisticsFile(String rootFolder, HashMap<String, HashMap<String, HashSet<String>>> gendata_statistics) {
        try {
            HTTPExpInfo.initDiseaseInfo();
            
            FileOutputStream fos = new FileOutputStream(rootFolder+"/statistics.json");
            PrintStream out = new PrintStream(fos);
            
            out.println("{\n\"newData\": [");
            
            int gendata_statistics_length = gendata_statistics.size();
            for (String disease: gendata_statistics.keySet()) {
                ArrayList<String> dts = new ArrayList<>(gendata_statistics.get(disease).keySet());
                Collections.sort(dts);
                for (String data_type: dts) {
                    String dt_aliquot = printCount(gendata_statistics.get(disease).get(data_type), "aliquot");
                    String dt_patient = printCount(gendata_statistics.get(disease).get(data_type), "patient");
                    String dt_sample = printCount(gendata_statistics.get(disease).get(data_type), "sample");
                    out.println("[\""+disease+"\",\""+HTTPExpInfo.getDiseaseInfo().get(disease).get("disease")+"\",\""+data_type+"\","+dt_aliquot+","+dt_sample+","+dt_patient+"],");
                }
                String dt_aliquot = printCount(mergeDataTypes(gendata_statistics.get(disease)), "aliquot");
                String dt_patient = printCount(mergeDataTypes(gendata_statistics.get(disease)), "patient");
                String dt_sample = printCount(mergeDataTypes(gendata_statistics.get(disease)), "sample");
                out.print("[\""+disease+"\",\""+HTTPExpInfo.getDiseaseInfo().get(disease).get("disease")+"\",\"total\","+dt_aliquot+","+dt_sample+","+dt_patient+"]");
                gendata_statistics_length--;
                if (gendata_statistics_length>0)
                    out.println(",");
                else
                    out.println();
                
            }
            
            out.println("]\n}");
            
            out.close();
            fos.close();
        } catch (Exception e) {}
    }

    private static String printCount(HashSet<String> set, String barcode) {
        BioParser parser = new GenericParser();
        for (String s: set) {
            if (barcode.toLowerCase().equals("aliquot"))
                parser.incrementAliquotCount(s);
            else if (barcode.toLowerCase().equals("sample"))
                parser.incrementSampleCount(s);
            else if (barcode.toLowerCase().equals("patient"))
                parser.incrementPatientCount(s);
        }
        if (barcode.toLowerCase().equals("aliquot"))
            return String.valueOf(parser.getAliquotCount());
        else if (barcode.toLowerCase().equals("sample"))
            return String.valueOf(parser.getSampleCount());
        else if (barcode.toLowerCase().equals("patient"))
            return String.valueOf(parser.getPatientCount());
        return "undefined";
    }

    private static HashSet<String> mergeDataTypes(HashMap<String, HashSet<String>> data_types) {
        HashSet<String> result = new HashSet<>();
        for (HashSet<String> set: data_types.values()) {
            result.addAll(set);
        }
        return result;
    }
    
}
