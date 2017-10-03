/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcga2bed.test;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import tcga2bed.parser.BioParser;
import tcga2bed.parser.CNVParser;
import tcga2bed.parser.DNAMethylation450Parser;
import tcga2bed.parser.DNASeqParser;
import tcga2bed.parser.MIRNASeqParser;
import tcga2bed.parser.RNASeqParser;
import tcga2bed.parser.RNASeqV2Parser;
import tcga2bed.util.FileOutputWriter;
import tcga2bed.util.HTTPExpInfo;

/**
 *
 * @author fabio
 */
public class RecreateHeaders {
    
    public static void main(String args[]) {
        String root = "D:/ftp-root/bed/";
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseaseMap = HTTPExpInfo.getDiseaseInfo();
        
        for (String disease: diseaseMap.keySet()) {
            System.err.println(disease);
            HashSet<String> dataTypes = new HashSet<String>();
            for (String attrib: diseaseMap.get(disease).keySet())
                dataTypes.add(attrib.toLowerCase().split("_")[0]);
            for (String dataType: dataTypes) {
                System.err.println("\t"+dataType);
                String dataType_path = root+disease.toLowerCase()+"/"+dataType.toLowerCase()+"/";
                if (dataType.toLowerCase().equals("dnaseq") || dataType.toLowerCase().contains("dnamethylation") || dataType.toLowerCase().equals("cnv")) {
                    File header_schema_file = new File(dataType_path+"header.schema");
                    if (header_schema_file.exists()) {
                        header_schema_file.delete();
                        createHeaderFile(header_schema_file, dataType.toLowerCase(), null);
                    }
                }
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
                        File header_schema_file = new File(dataSubType_path+"header.schema");
                        if (header_schema_file.exists()) {
                            header_schema_file.delete();
                            createHeaderFile(header_schema_file, dataType.toLowerCase(), dataSubType.toLowerCase());
                        }
                    }
                }
            }
        }
    }

    private static void createHeaderFile(File header_schema_file, String dataType, String dataSubType) {
        if (dataSubType != null)
            dataSubType = dataSubType.split("\\.")[0];
        
        BioParser parser = null;
        if (dataType.toLowerCase().equals("dnaseq"))
            parser = new DNASeqParser();
        else if (dataType.toLowerCase().equals("cnv"))
            parser = new CNVParser();
        else if (dataType.toLowerCase().contains("dnamethylation"))
            parser = new DNAMethylation450Parser(); // or DNAMethylation27Parser()
        else if (dataType.toLowerCase().equals("mirnaseq"))
            parser = new MIRNASeqParser();
        else if (dataType.toLowerCase().equals("rnaseq"))
            parser = new RNASeqParser();
        else if (dataType.toLowerCase().equals("rnaseqv2"))
            parser = new RNASeqV2Parser();
        
        if (parser != null) {
            String[] header = parser.getHeader(dataType, dataSubType);
            String[] types = parser.getAttributesType(dataType, dataSubType);
            FileOutputWriter.writeGendataHeader(header_schema_file, header, types);
        }
    }
    
}
