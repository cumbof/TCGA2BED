/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcga2bed.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MD5Checksum;

/**
 *
 * @author fabio
 */
public class CreateMD5Files {
    
    private static final String bedRoot = "E:/ftp-root/bed/";
    
    public static void main(String[] args) {
        ArrayList<String> dataTypesWithSubTypes = initSubTypes();
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseaseMap = HTTPExpInfo.getDiseaseInfo();
        for (String disease: diseaseMap.keySet()) {
            System.err.println(disease);
            String diseaseRoot = bedRoot + disease.toLowerCase() + "/";
            File[] dataTypes = (new File(diseaseRoot)).listFiles();
            for (File dt: dataTypes) {
                if (dt.isDirectory()) {
                    File[] data = dt.listFiles();
                    boolean containsSubTypes = false;
                    for (File f: data) {
                        if (dataTypesWithSubTypes.contains(f.getName())) {
                            containsSubTypes = true;
                            System.err.println("\t" + dt.getName() + "\t" + f.getName());
                            createMD5Table(f.getAbsolutePath());
                        }
                    }
                    if (!containsSubTypes) {
                        System.err.println("\t" + dt.getName());
                        createMD5Table(dt.getAbsolutePath());
                    }
                }
            }
        }
    }
    
    private static ArrayList<String> initSubTypes() {
        String[] subTypes = new String[] {"isoform.quantification", 
                                    "mirna.quantification",
                                    "exon.quantification",
                                    "gene.quantification",
                                    "spljxn.quantification"};
        return new ArrayList<>(Arrays.asList(subTypes));
    }

    private static void createMD5Table(String dataTypePath) {
        HashMap<String, String> fileName2md5 = new HashMap<>();
        for (File file: (new File(dataTypePath)).listFiles()) {
            if (file.getName().endsWith("bed") || file.getName().endsWith("bed.meta")) 
                fileName2md5.put(file.getName(), MD5Checksum.getMD5Checksum(file.getAbsolutePath()));
        }
        ArrayList<String> fileNames = new ArrayList<>(fileName2md5.keySet());
        Collections.sort(fileNames);
        
        try {
            String md5TablePath = dataTypePath + "/" + "md5table.txt";
            File md5TableFile = new File(md5TablePath);
            if (md5TableFile.exists())
                md5TableFile.delete();
            FileOutputStream fos = new FileOutputStream(md5TablePath);
            PrintStream out = new PrintStream(fos);
            
            for (String fileName: fileNames)
                out.println(fileName + "\t" + fileName2md5.get(fileName));
            
            out.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
