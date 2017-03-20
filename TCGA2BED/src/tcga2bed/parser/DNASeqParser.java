/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import tcga2bed.Settings;
import tcga2bed.resources.GRCh36_GRCh37_Map4OV;

import tcga2bed.util.FileInputParser;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MD5Checksum;
import tcga2bed.util.OutputFormat;

public class DNASeqParser extends BioParser {

    @Override
    //public HashMap<String, String> getGendataEntries(File data_dir, HashMap<String, Integer> patient2code, String data_type, String data_subtype) {
    public HashSet<String> convert(File data_dir, File meta_dir, String disease, String data_type, String data_subtype, String out_dir_path, String out_fileName_prefix, String gendata_ext, String metadata_ext) {
        HashSet<String> patientIds = new HashSet<>();

        int patientRefIndex = 15;
        int chrIndex = 4;
        int startIndex = 5;
        int endIndex = 6;
        int strandIndex = 7;

        int hugoSymbolIndex = 0;
        int entrezGeneIdIndex = 1;
        int variantClassificationIndex = 8;
        int variantTypeIndex = 9;
        int referenceAlleleIndex = 10;
        int tumorSeqAllele1Index = 11;
        int tumorSeqAllele2Index = 12;
        int dbSNP_RSIndex = 13;
        int tumorSampleBarcodeIndex = 15;
        int matchedNormSampleBarcodeIndex = 16;
        int matchNormSeqAllele1Index = 17;
        int matchNormSeqAllele2Index = 18;
        int matchedNormSampleUUIDIndex = 33;

        //to add in meta and meta_dictionary
        int centerIndex = 2;
        int NCBI_BuildIndex = 3;
        int sequence_sourceIndex = 27;
        int sequencerIndex = 31;
        int tumor_sample_uuidIndex = 32;

        skipIndexList = new ArrayList<>();
        //skipIndexList.add(patientRefIndex);
        skipIndexList.add(chrIndex);
        skipIndexList.add(startIndex);
        skipIndexList.add(endIndex);
        skipIndexList.add(strandIndex);

        //String url = "";
        //String updatedFolder = "";
        HTTPExpInfo.initDiseaseInfo();
        /*try {
            File f_tmp = File.createTempFile("http_tcga", "html");
            String out_path = f_tmp.getAbsolutePath();
            url = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_root_dir");

            HashMap<String, String> folder2date = HTTPExpInfo.getData2Date(url, out_path, disease, data_type, "/", "_data_dir_prefix");
            f_tmp.delete();
            updatedFolder = HTTPExpInfo.searchForUpdate(folder2date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/
        
        HashMap<String, String> coordinates_GRCh36_to_GRCh37 = new HashMap<>();
        if (disease.toLowerCase().trim().equals("ov"))
            coordinates_GRCh36_to_GRCh37 = GRCh36_GRCh37_Map4OV.load_GRCh36_GRCh37_map();

        HashMap<String, HashMap<String, String>> parser_meta = new HashMap<>();
        HashMap<String, HashSet<String>> parser_meta_dict = new HashMap<>();

        for (File f : data_dir.listFiles()) {

            if (!isToSkip(f.getName()) && f.getName().toUpperCase().endsWith("MAF")) {
                HashMap<String, String[]> data = FileInputParser.retrieveData(f, data_type);

                HashSet<String> alreadyDone = new HashSet<>();
                File[] out_folder = (new File(out_dir_path)).listFiles();
                for (File file : out_folder) {
                    String p = file.getName().split("\\.")[0].replaceAll("_", "-");
                    alreadyDone.add(p);
                    if (!patientIds.contains(p)) {
                        patientIds.add(p);
                    }

                    for (String k : data.keySet()) {
                        try {
                            if (!k.equals("header")) {
                                String[] d = data.get(k);
                                String patientRef = d[patientRefIndex];
                                if (patientRef.equals(p)) {
                                    String md5sum = MD5Checksum.getMD5Checksum(file.getAbsolutePath());
                                    /*boolean genomeVersion37 = true;
                                    if (!d[NCBI_BuildIndex].toLowerCase().trim().contains("37"))
                                        genomeVersion37 = false;*/
                                    
                                    //Update metadata
                                    HashMap<String, String> metaVals = new HashMap<>();
                                    if (parser_meta.containsKey(p)) {
                                        metaVals = parser_meta.get(p);
                                    }
                                    metaVals.put("manually_curated|center", d[centerIndex].toLowerCase());
                                    metaVals.put("manually_curated|ncbi_build", d[NCBI_BuildIndex].toLowerCase());
                                    metaVals.put("manually_curated|sequence_source", d[sequence_sourceIndex].toLowerCase());
                                    metaVals.put("manually_curated|sequencer", d[sequencerIndex].toLowerCase());
                                    metaVals.put("manually_curated|tumor_sample_uuid", d[tumor_sample_uuidIndex].toLowerCase());
                                    //metaVals.put("manually_curated|meta_biotab_url", meta_biotab_url.toLowerCase());
                                    metaVals.put("manually_curated|dataType", data_type.toLowerCase());
                                    metaVals.put("manually_curated|id", patientRef.toLowerCase());
                                    metaVals.put("manually_curated|tumor_tag", disease.toLowerCase());
                                    metaVals.put("manually_curated|tumor_description", getAbbreviation2Description().get(disease.toUpperCase()).toLowerCase());
                                    //metaVals.put("manually_curated|exp_data_url", url + updatedFolder + f.getCanonicalFile().getName());
                                    metaVals.put("manually_curated|seqPlatform", HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_platform").toLowerCase());
                                    metaVals.put("manually_curated|tissue_status", getTissueStatus(patientRef).toLowerCase());
                                    metaVals.put("manually_curated|exp_data_bed_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + patientRef + gendata_ext);
                                    metaVals.put("manually_curated|exp_metadata_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + patientRef + metadata_ext);
                                    metaVals.put("manually_curated|md5sum", md5sum);
                                    //metaVals.put("manually_curated|converted_to_GRCh37", genomeVersion37 ? "false" : "true");
                                    parser_meta.put(patientRef, metaVals);

                                    //Update metadata dictionary
                                    HashSet<String> metaDictVals = new HashSet<>();
                                    
                                    /*if (parser_meta_dict.containsKey("manually_curated|converted_to_GRCh37")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|converted_to_GRCh37");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(genomeVersion37 ? "false" : "true");
                                    parser_meta_dict.put("manually_curated|converted_to_GRCh37", metaDictVals);*/
                                    
                                    if (parser_meta_dict.containsKey("manually_curated|md5sum")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|md5sum");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(md5sum);
                                    parser_meta_dict.put("manually_curated|md5sum", metaDictVals);
                                    
                                    if (parser_meta_dict.containsKey("manually_curated|tissue_status")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|tissue_status");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(getTissueStatus(patientRef).toLowerCase());
                                    parser_meta_dict.put("manually_curated|tissue_status", metaDictVals);
                                    
                                    if (parser_meta_dict.containsKey("manually_curated|seqPlatform")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|seqPlatform");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_platform").toLowerCase());
                                    parser_meta_dict.put("manually_curated|seqPlatform", metaDictVals);
                                    
                                    /*if (parser_meta_dict.containsKey("manually_curated|exp_data_url")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|exp_data_url");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(url + updatedFolder + f.getCanonicalFile().getName());
                                    parser_meta_dict.put("manually_curated|exp_data_url", metaDictVals);*/
                                    
                                    if (parser_meta_dict.containsKey("manually_curated|tumor_description")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|tumor_description");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(getAbbreviation2Description().get(disease.toUpperCase()).toLowerCase());
                                    parser_meta_dict.put("manually_curated|tumor_description", metaDictVals);
                                    
                                    if (parser_meta_dict.containsKey("manually_curated|tumor_tag")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|tumor_tag");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(disease.toLowerCase());
                                    parser_meta_dict.put("manually_curated|tumor_tag", metaDictVals);
                                    
                                    if (parser_meta_dict.containsKey("manually_curated|center")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|center");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(d[centerIndex].toLowerCase());
                                    parser_meta_dict.put("manually_curated|center", metaDictVals);
                                    
                                    if (parser_meta_dict.containsKey("manually_curated|ncbi_build")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|ncbi_build");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(d[NCBI_BuildIndex].toLowerCase());
                                    parser_meta_dict.put("manually_curated|ncbi_build", metaDictVals);
                                    
                                    if (parser_meta_dict.containsKey("manually_curated|sequence_source")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|sequence_source");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(d[sequence_sourceIndex].toLowerCase());
                                    parser_meta_dict.put("manually_curated|sequence_source", metaDictVals);
                                    
                                    if (parser_meta_dict.containsKey("manually_curated|sequencer")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|sequencer");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(d[sequencerIndex].toLowerCase());
                                    parser_meta_dict.put("manually_curated|sequencer", metaDictVals);
                                    
                                    if (parser_meta_dict.containsKey("manually_curated|tumor_sample_uuid")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|tumor_sample_uuid");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(d[tumor_sample_uuidIndex].toLowerCase());
                                    parser_meta_dict.put("manually_curated|tumor_sample_uuid", metaDictVals);

                                    /*if (parser_meta_dict.containsKey("manually_curated|meta_biotab_url")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|meta_biotab_url");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(meta_biotab_url.toLowerCase());
                                    parser_meta_dict.put("manually_curated|meta_biotab_url", metaDictVals);*/

                                    if (parser_meta_dict.containsKey("manually_curated|dataType")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|dataType");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(data_type.toLowerCase());
                                    parser_meta_dict.put("manually_curated|dataType", metaDictVals);

                                    if (parser_meta_dict.containsKey("manually_curated|id")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|id");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(patientRef.toLowerCase());
                                    parser_meta_dict.put("manually_curated|id", metaDictVals);

                                    if (parser_meta_dict.containsKey("manually_curated|exp_data_bed_url")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|exp_data_bed_url");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + patientRef + gendata_ext);
                                    parser_meta_dict.put("manually_curated|exp_data_bed_url", metaDictVals);
                                    
                                    if (parser_meta_dict.containsKey("manually_curated|exp_metadata_url")) {
                                        metaDictVals = parser_meta_dict.get("manually_curated|exp_metadata_url");
                                    } else {
                                        metaDictVals = new HashSet<>();
                                    }
                                    metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + patientRef + metadata_ext);
                                    parser_meta_dict.put("manually_curated|exp_metadata_url", metaDictVals);
                                    
                                    break;
                                }
                            }
                        } catch (Exception e) {}
                    }
                }

                HashSet<String> patients = new HashSet<>();
                for (String l : data.keySet()) {
                    if (!l.equals("header")) {
                        String patient = data.get(l)[patientRefIndex];
                        patients.add(patient);
                    }
                }

                try {
                    for (String patient : patients) {
                        if (!alreadyDone.contains(patient)) {

                            System.err.print("WRITING GENDATA FOR " + patient + "...");
                            int entries = 0;
                            int skippedEntries = 0;

                            File output = new File(out_dir_path + patient + gendata_ext);
                            output.createNewFile();
                            FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
                            PrintStream out = new PrintStream(fos);
                                                        
                            out.printf(OutputFormat.initDocument(getFormat()));

                            for (String k : data.keySet()) {
                                try {
                                    if (!k.equals("header")) {
                                        String[] d = data.get(k);
                                        String patientRef = d[patientRefIndex];
                                        if (patient.equals(patientRef)) {
                                            
                                            String chr = d[chrIndex];
                                            boolean genomeVersion37 = true;
                                            if (!d[NCBI_BuildIndex].toLowerCase().trim().contains("37"))
                                                genomeVersion37 = false;
                                            
                                            int start = Integer.valueOf(d[startIndex]);
                                            int end = Integer.valueOf(d[endIndex]);
                                            if (start > end) {
                                                int tmp = start;
                                                start = end;
                                                end = tmp;
                                            }
                                            
                                            String coordinates = "";
                                            if (!genomeVersion37 && disease.toLowerCase().trim().equals("ov")) {
                                                coordinates = coordinates_GRCh36_to_GRCh37.get("chr"+chr+":"+start+"-"+end);
                                                if (!coordinates.trim().toLowerCase().equals("na")) {
                                                    String[] coordinates_split = coordinates.split(":");
                                                    chr = coordinates_split[0].replaceAll("chr", "");
                                                    String[] startEnd_split = coordinates_split[1].split("-");
                                                    start = Integer.valueOf(startEnd_split[0]);
                                                    end = Integer.valueOf(startEnd_split[1]);
                                                }
                                            }
                                            String strand = d[strandIndex];

                                            if (!coordinates.trim().toLowerCase().equals("na")) {
                                                ArrayList<String> tmpArr = new ArrayList<>();
                                                tmpArr.add("chr"+chr); tmpArr.add(String.valueOf(start)); tmpArr.add(String.valueOf(end)); tmpArr.add(strand);
                                                //String tmp = "chr" + chr + "\t" + start + "\t" + end + "\t" + strand + "\t";
                                                //String annotation = "chr"+chr + "\t" + start + "\t" + end + "\t" + strand + "\t";

                                                if (!skipIndexList.contains(hugoSymbolIndex)) {
                                                    if (d[hugoSymbolIndex].trim().equals("") || d[hugoSymbolIndex].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                        //annotation += "null";
                                                    } else {
                                                        //tmp += d[hugoSymbolIndex] + "\t";
                                                        tmpArr.add(d[hugoSymbolIndex]);
                                                        //annotation += d[hugoSymbolIndex];
                                                    }
                                                }
                                                if (!skipIndexList.contains(entrezGeneIdIndex)) {
                                                    if (d[entrezGeneIdIndex].trim().equals("") || d[entrezGeneIdIndex].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[entrezGeneIdIndex] + "\t";
                                                        tmpArr.add(d[entrezGeneIdIndex]);
                                                    }
                                                }
                                                if (!skipIndexList.contains(variantClassificationIndex)) {
                                                    if (d[variantClassificationIndex].trim().equals("") || d[variantClassificationIndex].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[variantClassificationIndex] + "\t";
                                                        tmpArr.add(d[variantClassificationIndex]);
                                                    }
                                                }
                                                if (!skipIndexList.contains(variantTypeIndex)) {
                                                    if (d[variantTypeIndex].trim().equals("") || d[variantTypeIndex].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[variantTypeIndex] + "\t";
                                                        tmpArr.add(d[variantTypeIndex]);
                                                    }
                                                }
                                                if (!skipIndexList.contains(referenceAlleleIndex)) {
                                                    if (d[referenceAlleleIndex].trim().equals("") || d[referenceAlleleIndex].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[referenceAlleleIndex] + "\t";
                                                        tmpArr.add(d[referenceAlleleIndex]);
                                                    }
                                                }
                                                if (!skipIndexList.contains(tumorSeqAllele1Index)) {
                                                    if (d[tumorSeqAllele1Index].trim().equals("") || d[tumorSeqAllele1Index].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[tumorSeqAllele1Index] + "\t";
                                                        tmpArr.add(d[tumorSeqAllele1Index]);
                                                    }
                                                }
                                                if (!skipIndexList.contains(tumorSeqAllele2Index)) {
                                                    if (d[tumorSeqAllele2Index].trim().equals("") || d[tumorSeqAllele2Index].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[tumorSeqAllele2Index] + "\t";
                                                        tmpArr.add(d[tumorSeqAllele2Index]);
                                                    }
                                                }
                                                if (!skipIndexList.contains(dbSNP_RSIndex)) {
                                                    if (d[dbSNP_RSIndex].trim().equals("") || d[dbSNP_RSIndex].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[dbSNP_RSIndex] + "\t";
                                                        tmpArr.add(d[dbSNP_RSIndex]);
                                                    }
                                                }
                                                if (!skipIndexList.contains(tumorSampleBarcodeIndex)) {
                                                    if (d[tumorSampleBarcodeIndex].trim().equals("") || d[tumorSampleBarcodeIndex].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[tumorSampleBarcodeIndex] + "\t";
                                                        tmpArr.add(d[tumorSampleBarcodeIndex]);
                                                    }
                                                }
                                                if (!skipIndexList.contains(matchedNormSampleBarcodeIndex)) {
                                                    if (d[matchedNormSampleBarcodeIndex].trim().equals("") || d[matchedNormSampleBarcodeIndex].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[matchedNormSampleBarcodeIndex] + "\t";
                                                        tmpArr.add(d[matchedNormSampleBarcodeIndex]);
                                                    }
                                                }
                                                if (!skipIndexList.contains(matchNormSeqAllele1Index)) {
                                                    if (d[matchNormSeqAllele1Index].trim().equals("") || d[matchNormSeqAllele1Index].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[matchNormSeqAllele1Index] + "\t";
                                                        tmpArr.add(d[matchNormSeqAllele1Index]);
                                                    }
                                                }
                                                if (!skipIndexList.contains(matchNormSeqAllele2Index)) {
                                                    if (d[matchNormSeqAllele2Index].trim().equals("") || d[matchNormSeqAllele2Index].trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[matchNormSeqAllele2Index] + "\t";
                                                        tmpArr.add(d[matchNormSeqAllele2Index]);
                                                    }
                                                }
                                                if (!skipIndexList.contains(matchedNormSampleUUIDIndex)) {
                                                    if (d[matchedNormSampleUUIDIndex].trim().equals("") || d[matchedNormSampleUUIDIndex].trim().equals("NA")) {
                                                        //tmp += "null";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += d[matchedNormSampleUUIDIndex];
                                                        tmpArr.add(d[matchedNormSampleUUIDIndex]);
                                                    }
                                                }

                                                //if (!tmp.trim().equals("")) {
                                                if (!tmpArr.isEmpty()) {
                                                    incrementAliquotCount(patient);
                                                    incrementSampleCount(patient);
                                                    incrementPatientCount(patient);
                                                    //addAnnotation(annotation);
                                                    //out.println(tmp);
                                                    String tmp = OutputFormat.createOutLine(getFormat(), tmpArr, getHeader(data_type, data_subtype));
                                                    out.print(tmp);
                                                }

                                                String md5sum = MD5Checksum.getMD5Checksum(output.getAbsolutePath());
                                                /*boolean genomeVersion37 = true;
                                                if (!d[NCBI_BuildIndex].toLowerCase().trim().contains("37"))
                                                    genomeVersion37 = false;*/

                                                //Update metadata
                                                if (!parser_meta.containsKey(patientRef)) {
                                                    HashMap<String, String> metaVals = new HashMap<>();
                                                    metaVals.put("manually_curated|center", d[centerIndex].toLowerCase());
                                                    metaVals.put("manually_curated|ncbi_build", d[NCBI_BuildIndex].toLowerCase());
                                                    metaVals.put("manually_curated|sequence_source", d[sequence_sourceIndex].toLowerCase());
                                                    metaVals.put("manually_curated|sequencer", d[sequencerIndex].toLowerCase());
                                                    metaVals.put("manually_curated|tumor_sample_uuid", d[tumor_sample_uuidIndex].toLowerCase());
                                                    //metaVals.put("manually_curated|meta_biotab_url", meta_biotab_url.toLowerCase());
                                                    metaVals.put("manually_curated|dataType", data_type.toLowerCase());
                                                    metaVals.put("manually_curated|id", patientRef.toLowerCase());
                                                    metaVals.put("manually_curated|tumor_tag", disease.toLowerCase());
                                                    metaVals.put("manually_curated|tumor_description", getAbbreviation2Description().get(disease.toUpperCase()).toLowerCase());
                                                    //metaVals.put("manually_curated|exp_data_url", url + updatedFolder + f.getName().toLowerCase());
                                                    metaVals.put("manually_curated|seqPlatform", HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_platform").toLowerCase());
                                                    metaVals.put("manually_curated|tissue_status", getTissueStatus(patientRef).toLowerCase());
                                                    metaVals.put("manually_curated|exp_data_bed_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + patientRef + gendata_ext);
                                                    metaVals.put("manually_curated|exp_metadata_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + patientRef + metadata_ext);
                                                    metaVals.put("manually_curated|md5sum", md5sum);
                                                    //metaVals.put("manually_curated|converted_to_GRCh37", genomeVersion37 ? "false" : "true");
                                                    parser_meta.put(patientRef, metaVals);
                                                }

                                                //Update metadata dictionary
                                                HashSet<String> metaDictVals = new HashSet<>();

                                                /*if (parser_meta_dict.containsKey("manually_curated|converted_to_GRCh37")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|converted_to_GRCh37");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(genomeVersion37 ? "false" : "true");
                                                parser_meta_dict.put("manually_curated|converted_to_GRCh37", metaDictVals);*/

                                                if (parser_meta_dict.containsKey("manually_curated|md5sum")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|md5sum");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(md5sum);
                                                parser_meta_dict.put("manually_curated|md5sum", metaDictVals);

                                                if (parser_meta_dict.containsKey("manually_curated|tissue_status")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|tissue_status");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(getTissueStatus(patientRef).toLowerCase());
                                                parser_meta_dict.put("manually_curated|tissue_status", metaDictVals);

                                                if (parser_meta_dict.containsKey("manually_curated|seqPlatform")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|seqPlatform");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_platform").toLowerCase());
                                                parser_meta_dict.put("manually_curated|seqPlatform", metaDictVals);

                                                /*if (parser_meta_dict.containsKey("manually_curated|exp_data_url")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|exp_data_url");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(url + updatedFolder + f.getName().toLowerCase());
                                                parser_meta_dict.put("manually_curated|exp_data_url", metaDictVals);*/

                                                if (parser_meta_dict.containsKey("manually_curated|tumor_description")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|tumor_description");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(getAbbreviation2Description().get(disease.toUpperCase()).toLowerCase());
                                                parser_meta_dict.put("manually_curated|tumor_description", metaDictVals);

                                                if (parser_meta_dict.containsKey("manually_curated|tumor_tag")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|tumor_tag");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(disease.toLowerCase());
                                                parser_meta_dict.put("manually_curated|tumor_tag", metaDictVals);

                                                /*if (parser_meta_dict.containsKey("manually_curated|meta_biotab_url")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|meta_biotab_url");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(meta_biotab_url.toLowerCase());
                                                parser_meta_dict.put("manually_curated|meta_biotab_url", metaDictVals);*/

                                                if (parser_meta_dict.containsKey("manually_curated|id")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|id");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(patientRef.toLowerCase());
                                                parser_meta_dict.put("manually_curated|id", metaDictVals);

                                                if (parser_meta_dict.containsKey("manually_curated|data_type")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|data_type");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(data_type.toLowerCase());
                                                parser_meta_dict.put("manually_curated|dataType", metaDictVals);

                                                if (parser_meta_dict.containsKey("manually_curated|center")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|center");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(d[centerIndex].toLowerCase());
                                                parser_meta_dict.put("manually_curated|center", metaDictVals);

                                                if (parser_meta_dict.containsKey("manually_curated|ncbi_build")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|ncbi_build");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(d[NCBI_BuildIndex].toLowerCase());
                                                parser_meta_dict.put("manually_curated|ncbi_build", metaDictVals);

                                                if (parser_meta_dict.containsKey("manually_curated|sequence_source")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|sequence_source");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(d[sequence_sourceIndex].toLowerCase());
                                                parser_meta_dict.put("manually_curated|sequence_source", metaDictVals);

                                                if (parser_meta_dict.containsKey("manually_curated|sequencer")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|sequencer");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(d[sequencerIndex].toLowerCase());
                                                parser_meta_dict.put("manually_curated|sequencer", metaDictVals);

                                                if (parser_meta_dict.containsKey("manually_curated|tumor_sample_uuid")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|tumor_sample_uuid");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(d[tumor_sample_uuidIndex].toLowerCase());
                                                parser_meta_dict.put("manually_curated|tumor_sample_uuid", metaDictVals);

                                                if (parser_meta_dict.containsKey("manually_curated|exp_data_bed_url")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|exp_data_bed_url");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + patientRef + gendata_ext);
                                                parser_meta_dict.put("manually_curated|exp_data_bed_url", metaDictVals);

                                                if (parser_meta_dict.containsKey("manually_curated|exp_metadata_url")) {
                                                    metaDictVals = parser_meta_dict.get("manually_curated|exp_metadata_url");
                                                } else {
                                                    metaDictVals = new HashSet<>();
                                                }
                                                metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + patientRef + metadata_ext);
                                                parser_meta_dict.put("manually_curated|exp_metadata_url", metaDictVals);

                                                if (!patientIds.contains(patientRef)) {
                                                    patientIds.add(patientRef);
                                                }
                                            }
                                            else
                                                skippedEntries++;
                                            entries++;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            out.printf(OutputFormat.endDocument(getFormat()));
                            out.close();
                            fos.close();
                            
                            System.err.println(" COMPLETED! "+(entries-skippedEntries)+"/"+entries);
                        } else {
                            System.out.println(patient);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

        //Update metadata and metadata_dictionary
        setMetadata(parser_meta);
        setMetadataDictionary(parser_meta_dict);

        return patientIds;
    }

    @Override
    public String[] getHeader(String data_type, String data_subtype) {
        String[] header = new String[17];
        header[0] = "chr";
        header[1] = "start";
        header[2] = "stop";
        header[3] = "strand";
        header[4] = "hugo_symbol";
        header[5] = "entrez_gene_id";
        header[6] = "variant_classification";
        header[7] = "variant_type";
        header[8] = "reference_allele";
        header[9] = "tumor_seq_allele1";
        header[10] = "tumor_seq_allele2";
        header[11] = "dbsnp_rs";
        header[12] = "tumor_sample_barcode";
        header[13] = "matched_norm_sample_barcode";
        header[14] = "match_norm_seq_allele1";
        header[15] = "match_norm_seq_allele2";
        header[16] = "matched_norm_sample_uuid";
        return header;
    }

    @Override
    public String[] getAttributesType(String data_type, String data_subtype) {
        String[] attr_type = new String[17];
        attr_type[0] = "STRING";
        attr_type[1] = "LONG";
        attr_type[2] = "LONG";
        attr_type[3] = "CHAR";
        attr_type[4] = "STRING";
        attr_type[5] = "STRING";
        attr_type[6] = "STRING";
        attr_type[7] = "STRING";
        attr_type[8] = "STRING";
        attr_type[9] = "STRING";
        attr_type[10] = "STRING";
        attr_type[11] = "STRING";
        attr_type[12] = "STRING";
        attr_type[13] = "STRING";
        attr_type[14] = "STRING";
        attr_type[15] = "STRING";
        attr_type[16] = "STRING";
        return attr_type;
    }

}
