/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import tcga2bed.util.FileOutputWriter;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MetadataHandler;

/**
 *
 * @author Fabio
 */
public class BigDictionary {
    
    private static final String input = "E:/ftp-root/bed/";
    private static final String output = "E:/ftp-root/bed/";
    private static String dataType = "all";
    //dnaseq
    //dnamethylation450
    //dnamethylation27
    //rnaseq
    //rnaseqv2
    //cnv
    //mirnaseq
    //all
    
    public static void main(String[] args) {
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseaseInfo = HTTPExpInfo.getDiseaseInfo();
        
        /*for (String disease: diseaseInfo.keySet()) {
            System.err.println(disease);
            createBigDictionaryDisease(disease);         
        }*/
        
        dataType = "dnamethylation450";
        String[] dataTypes = new String[7];
            dataTypes[0] = "dnamethylation450";
            dataTypes[1] = "dnamethylation27";
            dataTypes[2] = "dnaseq";
            dataTypes[3] = "rnaseq";
            dataTypes[4] = "rnaseqv2";
            dataTypes[5] = "mirnaseq";
            dataTypes[6] = "cnv";
        String[] dataSubTypes = new String[6];
            dataSubTypes[0] = "";
            dataSubTypes[1] = "gene";
            dataSubTypes[2] = "exon";
            dataSubTypes[3] = "spljxn";
            dataSubTypes[4] = "isoform";
            dataSubTypes[5] = "mirna";
        HashMap<String, HashSet<String>> bigDatatypeDictionary = new HashMap<String, HashSet<String>>();
        for (String disease: diseaseInfo.keySet()) {
            for (String dt: dataTypes) {
                if (dt.toLowerCase().equals(dataType.toLowerCase()) || dataType.toLowerCase().equals("all")) {
                    String disease_path = input+disease+"/";
                    for (String dst: dataSubTypes) {
                        String optional_subtype_path = "";
                        if (!dst.trim().equals(""))
                            optional_subtype_path = dst+".quantification/";
                        File datatype_dictionary_path = new File (disease_path+dt+"/"+optional_subtype_path+"metadata_dictionary.txt");
                        if (datatype_dictionary_path.exists()) {
                            System.err.println(datatype_dictionary_path);
                            HashMap<String, HashSet<String>> tmp = MetadataHandler.readMetaDictionary(datatype_dictionary_path, dataType);
                            if (bigDatatypeDictionary.isEmpty())
                                bigDatatypeDictionary.putAll(tmp);
                            else {
                                for (String attr: tmp.keySet()) {
                                    if (bigDatatypeDictionary.containsKey(attr)) {
                                        HashSet<String> values = bigDatatypeDictionary.get(attr);
                                        values.addAll(tmp.get(attr));
                                        bigDatatypeDictionary.put(attr, values);
                                    }
                                    else
                                        bigDatatypeDictionary.put(attr, tmp.get(attr));
                                }
                            }
                        }
                    }
                }
            }
        }
        File output_file = new File(output+"metadata_dictionary_"+dataType+".txt");
        if (output_file.exists())
            output_file.delete();
        FileOutputWriter.writeMetaDictionary(new File(output_file.getAbsolutePath()), bigDatatypeDictionary);
    }

    private static void createBigDictionaryDisease(String disease) {
        try {
          //File[] dictionaries = (new File(input+disease+"/")).listFiles();
            String disease_path = input+disease+"/";
            
            ArrayList<File> dictionaries = new ArrayList<File>();
            File disease_path_dnameth450 = new File (disease_path+"dnamethylation450/metadata_dictionary.txt");
            if (disease_path_dnameth450.exists())
                dictionaries.add(disease_path_dnameth450);
            File disease_path_dnameth27 = new File (disease_path+"dnamethylation27/metadata_dictionary.txt");
            if (disease_path_dnameth27.exists())
                dictionaries.add(disease_path_dnameth27);
            File disease_path_dnaseq = new File (disease_path+"dnaseq/metadata_dictionary.txt");
            if (disease_path_dnaseq.exists())
                dictionaries.add(disease_path_dnaseq);
            File disease_path_rnaseq_exon = new File (disease_path+"rnaseq/exon.quantification/metadata_dictionary.txt");
            if (disease_path_rnaseq_exon.exists())
                dictionaries.add(disease_path_rnaseq_exon);
            File disease_path_rnaseq_gene = new File (disease_path+"rnaseq/gene.quantification/metadata_dictionary.txt");
            if (disease_path_rnaseq_gene.exists())
                dictionaries.add(disease_path_rnaseq_gene);
            File disease_path_rnaseq_spljxn = new File (disease_path+"rnaseq/spljxn.quantification/metadata_dictionary.txt");
            if (disease_path_rnaseq_spljxn.exists())
                dictionaries.add(disease_path_rnaseq_spljxn);
            File disease_path_rnaseqv2_exon = new File (disease_path+"rnaseqv2/exon.quantification/metadata_dictionary.txt");
            if (disease_path_rnaseqv2_exon.exists())
                dictionaries.add(disease_path_rnaseqv2_exon);
            File disease_path_rnaseqv2_gene = new File (disease_path+"rnaseqv2/gene.quantification/metadata_dictionary.txt");
            if (disease_path_rnaseqv2_gene.exists())
                dictionaries.add(disease_path_rnaseqv2_gene);
            File disease_path_rnaseqv2_isoform = new File (disease_path+"rnaseqv2/isoform.quantification/metadata_dictionary.txt");
            if (disease_path_rnaseqv2_isoform.exists())
                dictionaries.add(disease_path_rnaseqv2_isoform);
            File disease_path_rnaseqv2_spljxn = new File (disease_path+"rnaseqv2/spljxn.quantification/metadata_dictionary.txt");
            if (disease_path_rnaseqv2_spljxn.exists())
                dictionaries.add(disease_path_rnaseqv2_spljxn);
            File disease_path_mirnaseq_mirna = new File (disease_path+"mirnaseq/mirna.quantification/metadata_dictionary.txt");
            if (disease_path_mirnaseq_mirna.exists())
                dictionaries.add(disease_path_mirnaseq_mirna);
            File disease_path_mirnaseq_isoform = new File (disease_path+"mirnaseq/isoform.quantification/metadata_dictionary.txt");
            if (disease_path_mirnaseq_isoform.exists())
                dictionaries.add(disease_path_mirnaseq_isoform);
            File disease_path_cnv = new File (disease_path+"cnv/metadata_dictionary.txt");
            if (disease_path_cnv.exists())
                dictionaries.add(disease_path_cnv);
            
            HashMap<String, HashSet<String>> metadata = new HashMap<>();
            for (File dictionary: dictionaries) {
                
                //File dictionary = new File(input_path + "metadata_dictionary.txt");
            
                HashMap<String, HashSet<String>> tmp = MetadataHandler.readMetaDictionary(dictionary, dataType);
                if (metadata.isEmpty())
                    metadata.putAll(tmp);
                else {
                    for (String attr: tmp.keySet()) {
                        if (metadata.containsKey(attr)) {
                            HashSet<String> values = metadata.get(attr);
                            values.addAll(tmp.get(attr));
                            metadata.put(attr, values);
                        }
                        else
                            metadata.put(attr, tmp.get(attr));
                    }
                }
                //System.err.println(metadata.size());
            }
            //printResult(metadata, dataType);
            //FileOutputWriter.writeMetaDictionary(new File(output+"/metadata_dictionary_"+disease+"_"+dataType+".txt"), metadata);
            File output_file = new File(output+disease+"/metadata_dictionary_"+disease+".txt");
            if (output_file.exists())
                output_file.delete();
            FileOutputWriter.writeMetaDictionary(new File(output_file.getAbsolutePath()), metadata);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
