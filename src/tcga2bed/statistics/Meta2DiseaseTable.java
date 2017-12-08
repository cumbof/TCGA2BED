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
import java.util.HashMap;
import java.util.HashSet;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MetadataHandler;
import static tcga2bed.test.EditMetadata.editMeta;

/**
 *
 * @author Fabio
 */
public class Meta2DiseaseTable {
    
    public static void main(String[] args) {
        String root = "G:/ftp-root/bed/";
        String outputFilePath = root+"meta2disease_table.csv";
        File outFile = new File(outputFilePath);
        if (outFile.exists())
            outFile.delete();
        ArrayList<File> meta_dictionaries = getMetaDictionariesPaths(root);
        createMeta2DiseaseTable(meta_dictionaries, outputFilePath);
    }
    
    public static ArrayList<File> getMetaDictionariesPaths(String root) {
        ArrayList<File> files = new ArrayList<File>();
        
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseaseMap = HTTPExpInfo.getDiseaseInfo();
        for (String disease: diseaseMap.keySet()) {
            System.err.println(root + disease.toLowerCase() + "/" + "metadata_dictionary_"+disease.toLowerCase()+".txt");
            File f = new File(root + disease.toLowerCase() + "/" + "metadata_dictionary_"+disease.toLowerCase()+".txt");
            if (f.exists())
                files.add(f);
        }
        return files;
    }
    
    public static void createMeta2DiseaseTable(ArrayList<File> metaDictionaries, String outputFilePath) {
        HashSet<String> attributes = new HashSet<String>();
        HashMap<String, HashMap<String, HashSet<String>>> disease2metadataDictionary = new HashMap<String, HashMap<String, HashSet<String>>>();
        for (File m: metaDictionaries) {
            if (m.getName().toLowerCase().contains("metadata_dictionary")) {
                String disease = m.getName().toLowerCase().split("\\.")[0].split("_")[2];
                HashMap<String, HashSet<String>> metadataDictionary = MetadataHandler.readMetaDictionary(m, "all");
                disease2metadataDictionary.put(disease, metadataDictionary);
                attributes.addAll(metadataDictionary.keySet());
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(outputFilePath);
            PrintStream out = new PrintStream(fos);
            
            out.print(";");
            for (String disease: disease2metadataDictionary.keySet())
                out.print(disease + ";");
            out.println();
            for (String attribute: attributes) {
                out.print(attribute + ";");
                for (String disease: disease2metadataDictionary.keySet()) {
                    if (disease2metadataDictionary.get(disease).containsKey(attribute))
                        out.print("1;");
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
