/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class MetaValues2Samples {
    
    private static HashMap<String, HashMap<String, String>> diseaseMap = new HashMap<>();
    
    public static void main(String[] args) {
        String root = "E:/ftp-root/bed/";
        HTTPExpInfo.initDiseaseInfo();
        diseaseMap = HTTPExpInfo.getDiseaseInfo();
        for (String disease: diseaseMap.keySet()) {
            System.err.println(disease);
            String outputFilePath = root+disease.toLowerCase()+"/meta_values2samples_list.tsv";
            File outFile = new File(outputFilePath);
            if (outFile.exists())
                outFile.delete();
            HashMap<String, HashSet<String>> metavalues2aliquots = getMetaValues2Aliquots(root+disease.toLowerCase()+"/");
            createMetaValues2Samples(metavalues2aliquots, outputFilePath);
        }
    }

    private static HashMap<String, HashSet<String>> getMetaValues2Aliquots(String disease_root) {
        HashMap<String, HashSet<String>> result = new HashMap<>();
        File root = new File(disease_root);
        for (File f: root.listFiles()) {
            if (f.isDirectory()) {
                if (f.getName().startsWith("dnameth") || f.getName().startsWith("dnaseq") || f.getName().startsWith("cnv")) {
                    HashMap<String, HashSet<String>> partialRes = getPartialMetaValues2Aliquots(f.getAbsolutePath());
                    for (String attr: partialRes.keySet()) {
                        HashSet<String> partialAliq = partialRes.get(attr);
                        if (result.containsKey(attr))
                            partialAliq.addAll(result.get(attr));
                        result.put(attr, partialAliq);
                    }
                }
                else if (f.getName().startsWith("rnaseq") || f.getName().startsWith("mirna")) {
                    for (File f2: f.listFiles()) {
                        if (f2.isDirectory()) {
                            HashMap<String, HashSet<String>> partialRes = getPartialMetaValues2Aliquots(f2.getAbsolutePath());
                            for (String attr: partialRes.keySet()) {
                                HashSet<String> partialAliq = partialRes.get(attr);
                                if (result.containsKey(attr))
                                    partialAliq.addAll(result.get(attr));
                                result.put(attr, partialAliq);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private static HashMap<String, HashSet<String>> getPartialMetaValues2Aliquots(String data_root) {
        HashMap<String, HashSet<String>> result = new HashMap<>();
        for (File f: (new File(data_root)).listFiles()) {
            if (f.getName().endsWith("meta")) {
                HashMap<String, HashSet<String>> metadata = MetadataHandler.readMetaFile(f);
                for (String attr: metadata.keySet()) {
                    for (String val: metadata.get(attr)) {
                        String newAttr = attr+"|"+val;
                        HashSet<String> aliquots = new HashSet<>();
                        aliquots.add(f.getName().split("\\.")[0]);
                        if (result.containsKey(newAttr))
                            aliquots.addAll(result.get(newAttr));
                        result.put(newAttr, aliquots);
                    }
                }
            }
        }
        return result;
    }

    private static void createMetaValues2Samples(HashMap<String, HashSet<String>> metavalues2aliquots, String outputFilePath) {
        File output = new File(outputFilePath);
        if (output.exists())
            output.delete();
        try {
            FileOutputStream fos = new FileOutputStream(outputFilePath);
            PrintStream out = new PrintStream(fos);
            ArrayList<String> attrs = new ArrayList<>(metavalues2aliquots.keySet());
            Collections.sort(attrs);
            for (String attr: attrs) {
                out.println(attr + "\t" + metavalues2aliquots.get(attr).size());
            }
            out.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
