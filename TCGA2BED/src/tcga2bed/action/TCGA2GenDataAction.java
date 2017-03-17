/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.action;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import tcga2bed.Main;
import tcga2bed.Settings;
import tcga2bed.parser.BioParser;
import tcga2bed.parser.CNVParser;
import tcga2bed.parser.DNAMethylation27Parser;
import tcga2bed.parser.DNAMethylation450Parser;
import tcga2bed.parser.DNASeqParser;
import tcga2bed.parser.MIRNASeqParser;
import tcga2bed.parser.RNASeqParser;
import tcga2bed.parser.RNASeqV2Parser;
import tcga2bed.resources.RetrieveMIRBASEGenomicCoordinates;
import tcga2bed.resources.RetrieveMirnaFromHGNC;
import tcga2bed.resources.RetrieveNCBIGenomicCoordinates;
import tcga2bed.resources.RetrieveUCSCGenomicCoordinates;
import tcga2bed.util.FileOutputWriter;
import tcga2bed.util.QueryParser;

public class TCGA2GenDataAction extends Action {

    private final String header_ext = ".schema";
    private final String metadata_dict_ext = ".txt";
    private String gendata_ext = ".bed";
    private String out_format = "bed";
    private String metadata_ext = ".meta";
    private final String tabular_info_ext = ".tsv";
    private final String annotation_ext = ".tsv";
    
    @Override
    public void execute(String[] args) {
        //args[0] : convert
        String disease_abbreviation = args[1];
        String meta_dir_path = args[2];
        String other_meta_dir_path = args[3];
        String data_dir_path = args[4];
        String out_dir_path = args[5];
        String data_type = args[6];

        String data_subtype;
        try {
            data_subtype = args[7];
        } catch (ArrayIndexOutOfBoundsException e) {
            data_subtype = "null";
        }

        String NCBI_ARCHIVE;
        try {
            NCBI_ARCHIVE = args[8];
        } catch (ArrayIndexOutOfBoundsException e) {
            NCBI_ARCHIVE = "null";
        }
        RetrieveNCBIGenomicCoordinates.setNcbiArchive(NCBI_ARCHIVE);

        String UCSC_ARCHIVE;
        try {
            UCSC_ARCHIVE = args[9];
        } catch (ArrayIndexOutOfBoundsException e) {
            UCSC_ARCHIVE = "null";
        }
        RetrieveUCSCGenomicCoordinates.setUcscAarchive(UCSC_ARCHIVE);
        
        String MIRBASE_ARCHIVE;
        try {
            MIRBASE_ARCHIVE = args[10];
        } catch (ArrayIndexOutOfBoundsException e) {
            MIRBASE_ARCHIVE = "null";
        }
        RetrieveMIRBASEGenomicCoordinates.setMirbaseAarchive(MIRBASE_ARCHIVE);
        
        String HGNC_ARCHIVE;
        try {
            HGNC_ARCHIVE = args[11];
        } catch (ArrayIndexOutOfBoundsException e) {
            HGNC_ARCHIVE = "null";
        }
        RetrieveMirnaFromHGNC.setHgncArchive(HGNC_ARCHIVE);
        
        String MAGETAB_PATH;
        try {
            MAGETAB_PATH = args[12];
        } catch (ArrayIndexOutOfBoundsException e) {
            MAGETAB_PATH = "null";
        }
        Settings.setMAGETABPath(MAGETAB_PATH);
        
        try {
            gendata_ext = "."+args[13].toLowerCase();
            out_format = args[13].toLowerCase();
            metadata_ext = gendata_ext + metadata_ext;
        } catch (ArrayIndexOutOfBoundsException e) {
            gendata_ext = ".bed";
            out_format = "bed";
            metadata_ext = gendata_ext + metadata_ext;
        }

        File meta_dir = new File(meta_dir_path);
        File other_meta_dir = null;
        if (!other_meta_dir_path.equals("null")) {
            other_meta_dir = new File(other_meta_dir_path);
        }
        File data_dir = new File(data_dir_path);
        File out_dir = new File(out_dir_path);

        if (!meta_dir.isDirectory()) {
            Main.printException("YOU HAVE TO SPECIFY A VALID CLINICAL BIOTAB METADATA DIRECTORY PATH...", true);
            return;
        }
        if (other_meta_dir != null) {
            if (!other_meta_dir.isDirectory()) {
                Main.printException("YOU HAVE TO SPECIFY A VALID ADDITIONAL METADATA DIRECTORY PATH...", true);
                return;
            }
        }
        if (!data_dir.isDirectory()) {
            Main.printException("YOU HAVE TO SPECIFY A VALID DATA DIRECTORY PATH...", true);
            return;
        }
        if (!out_dir.isDirectory()) {
            Main.printException("YOU HAVE TO SPECIFY A VALID OUTPUT DIRECTORY PATH...", true);
            return;
        }

        //String meta_biotab_url = "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/" + disease_abbreviation.toLowerCase() + "/bcr/biotab/clin/";

        //metadata
        Main.printException("META BIOTAB...", true);
        HashMap<String, HashMap<String, HashMap<String, String>>> meta = QueryParser.getMetadata(meta_dir, null, new HashMap<>(), null, "biotab");
        /*HashMap<String, HashMap<String, HashMap<String, String>>> other_meta = new HashMap<String, HashMap<String, HashMap<String, String>>>();
         if (other_meta_dir!=null) {
         System.err.print("ADDITIONAL METADATA...");
         other_meta = QueryParser.getMetadata(other_meta_dir, null, new HashMap<String, String>(), null, "biotab");
         System.err.println(" COMPLETED");
         }*/
        Main.printException(" COMPLETED", true);

        for (File f: out_dir.listFiles()) {
            if (f.getName().trim().toLowerCase().equals("metadata_dictionary.txt"))
                f.delete();
            else if (f.getName().trim().toLowerCase().endsWith(".meta"))
                f.delete();
        }
        
        //gendata
        Main.printException("START CONVERTING TO BED FORMAT...", true);
        BioParser parser = null;
        switch (data_type.toLowerCase()) {
            case "rnaseq":
                parser = new RNASeqParser();
                break;
            case "rnaseqv2":
                parser = new RNASeqV2Parser();
                break;
            case "dnaseq":
                parser = new DNASeqParser();
                break;
            case "dnamethylation450":
                parser = new DNAMethylation450Parser();
                break;
            case "dnamethylation27":
                parser = new DNAMethylation27Parser();
                break;
            case "mirnaseq":
                parser = new MIRNASeqParser();
                break;
            case "cnv":
                parser = new CNVParser();
                break;
        }
        parser.setFormat(out_format);
        String out_fileName_prefix = disease_abbreviation + "_" + data_type;
        //HashSet<String> gendataPatients = parser.convert(data_dir, meta_dir, disease_abbreviation, meta_biotab_url, data_type, data_subtype, out_dir_path, out_fileName_prefix, gendata_ext, metadata_ext);
        HashSet<String> gendataPatients = parser.convert(data_dir, meta_dir, disease_abbreviation, data_type, data_subtype, out_dir_path, out_fileName_prefix, gendata_ext, metadata_ext);
        Main.printException("COMPLETED", true);

        HashMap<String, HashMap<String, HashMap<String, String>>> metadata = mergeMeta(meta, parser.getMetadata());
        Main.printException("WRITING METADATA...", true);
        FileOutputWriter.writeMetadata(out_dir_path, out_fileName_prefix, metadata_ext, metadata, gendataPatients, data_type);
        Main.printException(" COMPLETED", true);

        //metadata dictionary
        Main.printException("META BIOSPECIMEN DICTIONARY...", true);
        HashMap<String, HashSet<String>> metadata_dictionary = makeDictionary(metadata, gendataPatients);
        /*HashMap<String, HashSet<String>> other_meta_dictionary = new HashMap<String, HashSet<String>>();
         if (other_meta_dir!=null) {
         System.err.print("ADDITIONAL METADATA DICTIONARY...");
         other_meta_dictionary = makeDictionary(other_meta);
         System.err.println(" COMPLETED");
         }*/
        Main.printException(" COMPLETED", true);

        HashMap<String, HashSet<String>> meta_dictionary = mergeMetaDictionary(metadata_dictionary, parser.getMetadataDictionary());
        Main.printException("WRITING METADATA DICTIONARY...", true);
        FileOutputWriter.writeMetaDictionary((new File(out_dir_path + "metadata_dictionary" + metadata_dict_ext)), meta_dictionary);
        Main.printException(" COMPLETED", true);

        Main.printException("WRITING HEADER...", true);
        FileOutputWriter.writeGendataHeader((new File(out_dir_path + "header" + header_ext)), parser.getHeader(data_type, data_subtype), parser.getAttributesType(data_type, data_subtype));
        Main.printException(" COMPLETED", true);

        Main.printException("WRITING TABULAR INFORMATION DATA...", true);
        FileOutputWriter.writeTabularInformationData((new File(out_dir_path + "exp_info" + tabular_info_ext)), parser.getAliquotSet(), parser.getSampleSet(), parser.getPatientSet());
        Main.printException(" COMPLETED", true);

        if (data_type.toUpperCase().equals("RNASEQ") || data_type.toUpperCase().equals("RNASEQV2") || data_type.toUpperCase().equals("MIRNASEQ")) {
            Main.printException("WRITING ANNOTATION DATA...", true);
            FileOutputWriter.writeAnnotationFile(out_dir_path + "annotations" + annotation_ext, parser.getAnnotationSet());
            Main.printException(" COMPLETED", true);
        }

        parser.deleteExtraFiles(out_dir_path);
    }

    private static HashMap<String, HashSet<String>> mergeMetaDictionary(HashMap<String, HashSet<String>> meta_dictionary, HashMap<String, HashSet<String>> other_meta_dictionary) {
        HashMap<String, HashSet<String>> result = new HashMap<>();
        result.putAll(meta_dictionary);
        for (String s : other_meta_dictionary.keySet()) {
            HashSet<String> mc = new HashSet<>();
            if (result.containsKey(s)) {
                mc = result.get(s);
            }
            for (String v : other_meta_dictionary.get(s)) {
                mc.add(v);
            }
            result.put(s, mc);
        }
        return result;
    }

    private static HashMap<String, HashMap<String, HashMap<String, String>>> mergeMeta(HashMap<String, HashMap<String, HashMap<String, String>>> meta, HashMap<String, HashMap<String, String>> other_meta) {
        HashMap<String, HashMap<String, HashMap<String, String>>> result = new HashMap<>();
        result.putAll(meta);
        result.put("additional_info", other_meta);
        return result;
    }

    private static HashMap<String, HashSet<String>> makeDictionary(HashMap<String, HashMap<String, HashMap<String, String>>> metadata, HashSet<String> gendataPatients) {
        HashMap<String, HashSet<String>> dictionary = new HashMap<>();
        for (String p : gendataPatients) {

            HashMap<String, String> patient_meta = new HashMap<>();
            String[] pSplit = p.split("-");

            for (String type : metadata.keySet()) {
                String ref = "";
                try {
                    if (type.toLowerCase().equals("biospecimen_aliquot") || type.toLowerCase().equals("additional_info")) {
                        ref = p;
                        patient_meta.putAll(metadata.get(type).get(ref));
                    } else if (type.toLowerCase().equals("biospecimen_analyte") || type.toLowerCase().equals("biospecimen_protocol")) {
                        ref = "";
                        for (int i = 0; i < 4; i++) {
                            ref += pSplit[i] + "-";
                        }
                        ref += pSplit[4];
                        patient_meta.putAll(metadata.get(type).get(ref));
                    } else if (type.toLowerCase().equals("biospecimen_cqcf") || type.toLowerCase().equals("biospecimen_normal_control") || type.toLowerCase().equals("biospecimen_tumor_sample") || type.toLowerCase().equals("clinical_cqcf") || type.toLowerCase().equals("clinical_nte") || type.toLowerCase().equals("clinical_patient")) {
                        ref = "";
                        for (int i = 0; i < 2; i++) {
                            ref += pSplit[i] + "-";
                        }
                        ref += pSplit[2];
                        patient_meta.putAll(metadata.get(type).get(ref));
                    } else if (type.toLowerCase().equals("biospecimen_diagnostic_slides") || type.toLowerCase().equals("biospecimen_sample")) {
                        ref = "";
                        for (int i = 0; i < 3; i++) {
                            ref += pSplit[i] + "-";
                        }
                        ref += pSplit[3];
                        patient_meta.putAll(metadata.get(type).get(ref));
                    } else if (type.toLowerCase().equals("biospecimen_portion")) {
                        ref = "";
                        for (int i = 0; i < 4; i++) {
                            ref += pSplit[i] + "-";
                        }
                        ref += pSplit[4].substring(0, pSplit[4].length() - 1);
                        for (String pat : metadata.get(type).keySet()) {
                            if (pat.startsWith(ref)) {
                                patient_meta.putAll(metadata.get(type).get(pat));
                                break;
                            }
                        }
                    } else if (type.toLowerCase().equals("biospecimen_shipment_portion") || type.toLowerCase().equals("biospecimen_slide")) {
                        ref = "";
                        for (int i = 0; i < 3; i++) {
                            ref += pSplit[i] + "-";
                        }
                        ref += pSplit[3];
                        for (String pat : metadata.get(type).keySet()) {
                            if (pat.startsWith(ref)) {
                                patient_meta.putAll(metadata.get(type).get(pat));
                                break;
                            }
                        }
                    } else if (type.toLowerCase().equals("clinical_drug") || type.toLowerCase().equals("clinical_follow_up") || type.toLowerCase().equals("clinical_omf") || type.toLowerCase().equals("clinical_radiation")) {
                        ref = "";
                        for (int i = 0; i < 2; i++) {
                            ref += pSplit[i] + "-";
                        }
                        ref += pSplit[2];
                        for (String pat : metadata.get(type).keySet()) {
                            if (pat.startsWith(ref)) {
                                patient_meta.putAll(metadata.get(type).get(pat));
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    //System.err.println(type + " - " + ref);
                }
            }

            if (!patient_meta.isEmpty()) {
                for (String attr : patient_meta.keySet()) {
                    HashSet<String> vals = new HashSet<>();
                    if (dictionary.containsKey(attr)) {
                        vals = dictionary.get(attr);
                    }
                    vals.add(patient_meta.get(attr));
                    dictionary.put(attr, vals);
                }
            }

            /*for (String type: metadata.keySet()) {
             for (HashMap<String, String> val: metadata.get(type).values()) {
             for (String k_val: val.keySet()) {
             HashSet<String> vals = null;
             if (dictionary.containsKey(k_val))
             vals = dictionary.get(k_val);
             else
             vals = new HashSet<String>();
             vals.add(val.get(k_val));
             dictionary.put(k_val, vals);
             }
             }
             }*/
        }
        return dictionary;
    }

    @Override
    public void setParameters(Object obj) {
    }

}
