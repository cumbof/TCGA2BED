/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class BioParser {
    
    String defaultFormat = "bed";
    public String getFormat() {
        return defaultFormat;
    }
    public void setFormat(String format) {
        defaultFormat = format;
    }

    HashSet<String> aliquot_set = new HashSet<>();
    HashSet<String> sample_set = new HashSet<>();
    HashSet<String> patient_set = new HashSet<>();

    HashSet<String> annotation_set = new HashSet<>();

    public void addAnnotation(String annotation) {
        annotation_set.add(annotation);
    }

    public void setAnnotationSet(HashSet<String> ann_set) {
        annotation_set.addAll(ann_set);
    }

    public HashSet<String> getAnnotationSet() {
        return annotation_set;
    }

    public void incrementAliquotCount(String aliquot) {
        aliquot_set.add(aliquot.toLowerCase());
    }

    public void setAliquotSet(HashSet<String> set) {
        aliquot_set = set;
    }

    public HashSet<String> getAliquotSet() {
        return aliquot_set;
    }

    public int getAliquotCount() {
        return aliquot_set.size();
    }

    public void incrementSampleCount(String aliquot) {
        String[] aliquot_split = aliquot.split("-");
        String sample = "";
        for (int i = 0; i < 3; i++) {
            sample = sample + "-" + aliquot_split[i];
        }
        sample = sample + aliquot_split[3];
        sample_set.add(sample.toLowerCase());
    }

    public void setSampleSet(HashSet<String> set) {
        sample_set = set;
    }

    public HashSet<String> getSampleSet() {
        return sample_set;
    }

    public int getSampleCount() {
        return sample_set.size();
    }

    public void incrementPatientCount(String aliquot) {
        String[] aliquot_split = aliquot.split("-");
        String patient = "";
        for (int i = 0; i < 2; i++) {
            patient = patient + "-" + aliquot_split[i];
        }
        patient = patient + aliquot_split[2];
        patient_set.add(patient.toLowerCase());
    }

    public void setPatientSet(HashSet<String> set) {
        patient_set = set;
    }

    public HashSet<String> getPatientSet() {
        return patient_set;
    }

    public int getPatientCount() {
        return patient_set.size();
    }

    private HashMap<String, String> abbreviation2description = new HashMap<>();
    private ArrayList<String> skipFiles = new ArrayList<>();
    protected ArrayList<Integer> skipIndexList;
	//protected HashMap<String, String> customHeader2Type = new HashMap<String, String>();

    public HashMap<String, HashMap<String, String>> metadata;
    public HashMap<String, HashSet<String>> meta_dictionary;
	//public HashSet<String> customHeader = new HashSet<String>();

    /*public void setCustomHeader2Type(HashMap<String, String> header2type) {
     this.customHeader2Type = header2type;
     }
     public HashMap<String, String> getCustomHeader2Type() {
     return this.customHeader2Type;
     }*/
    /*public void setCustomHeader(HashSet<String> header) {
     this.customHeader = header;
     }
     public HashSet<String> getCustomHeader() {
     return this.customHeader;
     }*/
    public void setMetadata(HashMap<String, HashMap<String, String>> meta) {
        this.metadata = meta;
    }

    public HashMap<String, HashMap<String, String>> getMetadata() {
        return this.metadata;
    }

    public void setMetadataDictionary(HashMap<String, HashSet<String>> meta_dict) {
        this.meta_dictionary = meta_dict;
    }

    public HashMap<String, HashSet<String>> getMetadataDictionary() {
        return this.meta_dictionary;
    }

    //public abstract HashMap<String, String> getGendataEntries(File data_dir, HashMap<String, Integer> patient2code, String data_type, String data_subtype);
    //public abstract HashSet<String> convert(File data_dir, File meta_dir,String disease, String meta_biotab_url, String data_type, String data_subtype, String out_dir_path, String out_fileName_prefix, String gendata_ext, String metadata_ext);
    public abstract HashSet<String> convert(File data_dir, File meta_dir,String disease, String data_type, String data_subtype, String out_dir_path, String out_fileName_prefix, String gendata_ext, String metadata_ext);

    public ArrayList<Integer> getSkipIndexList() {
        return skipIndexList;
    }

    public boolean isToSkip(String name) {
        return name.toUpperCase().endsWith(".TAR.GZ") || name.toUpperCase().equals("CHANGES_DCC.TXT") || name.toUpperCase().equals("DESCRIPTION.TXT") || name.toUpperCase().equals("MANIFEST.TXT") || name.toUpperCase().equals("README_DCC.TXT") || name.toUpperCase().equals("DATA_USE_DISCLAIMER.TXT");
    }

    //TODO TO ABSTRACT getHeader and getAttributesType
    public abstract String[] getHeader(String data_type, String data_subtype);

    public abstract String[] getAttributesType(String data_type, String data_subtype);
    /*public String[] getHeader(File data_dir, String data_type) {
     for (File d: data_dir.listFiles()) {
     if (!isToSkip(d.getName())) {
     HashMap<String, String[]> data = FileInputParser.retrieveData(d, data_type);
				
				
     String[] newHeader = new String[data.get("header").length+4];
     newHeader[0] = data.get("header")[0];
     if (data_type.equals("DNASeq")) {
     newHeader = new String[data.get("header").length+5];
     newHeader[0] = "_NULL_";
     }
     newHeader[1] = "chrom";
     newHeader[2] = "chromStart";
     newHeader[3] = "chromEnd";
     newHeader[4] = "strand";
     int currentIndex = 5;
     if (data_type.equals("DNASeq")) {
     newHeader[5] = "Hugo_Symbol";
     currentIndex = 6;
     }
     for (int i=currentIndex; i<newHeader.length; i++)
     newHeader[i] = data.get("header")[i-currentIndex+1];
     return newHeader;
     //return data.get("header");
     }
     }
     return null;
     }
     public String[] getAttributesType(File data_dir, String data_type) {
     initCustomHeader2Type();
     for (File d: data_dir.listFiles()) {
     if (!isToSkip(d.getName())) {
     HashMap<String, String[]> data = FileInputParser.retrieveData(d, data_type);
     for (String da: data.keySet()) {
     if (!da.equals("header")) {
     String[] types = new String[data.get(da).length];
     int count = 0;
     for (int i=0; i<data.get(da).length; i++) { //String s: data.get(da)) {
     if (customHeader2Type.containsKey(data.get("header")[i]))
     types[count] = customHeader2Type.get(data.get("header")[i]);
     else {
     try {
     Float.valueOf(data.get(da)[i]);
     types[count] = "FLOAT";
     } catch(ClassCastException e) {
     types[count] = "STRING";
     } catch(NumberFormatException e) {
     types[count] = "STRING";
     }
     }							
     count++;
     }
						
     String[] newTypes = new String[types.length+4];
     if (data_type.equals("DNASeq"))
     newTypes = new String[types.length+5];
     newTypes[0] = "STRING";
     newTypes[1] = "FLOAT";
     newTypes[2] = "FLOAT";
     newTypes[3] = "STRING"; // strand type : STRAND
     int currentIndex = 4;
     if (data_type.equals("DNASeq")) {
     newTypes[4] = "STRING";
     currentIndex = 5;
     }
     for (int i=currentIndex; i<newTypes.length; i++){
     newTypes[i] = types[i-currentIndex];
     }
     return newTypes;
     //return types;
     }
     }
				
     return data.get("header");
     }
     }
     return null;
     }
	
     private void initCustomHeader2Type() {
     customHeader2Type.put("Entrez_Gene_Id", "STRING");
     }*/

    public int retriveGendataCode(String patientRef, HashMap<String, Integer> patient2code) {
        patientRef = patientRef.replaceAll("-", "_");
        for (String p : patient2code.keySet()) {
            if (patientRef.contains(p.replaceAll("-", "_"))) {
                return patient2code.get(p);
            }
        }
        return -1;
    }

    public void deleteExtraFiles(String folderPath) {
        if (skipFiles.isEmpty()) {
            skipFiles.add("header.schema");
            skipFiles.add("metadata_dictionary.txt");
        }
        for (File f : (new File(folderPath)).listFiles()) {
            if (!skipFiles.contains(f.getName())) {
                if (f.getName().endsWith(".bed")) {
                    if (!(new File(f.getAbsolutePath() + ".meta")).exists()) {
                        f.delete();
                    }
                }
            }
        }
    }

    public HashMap<String, String> getAbbreviation2Description() {
        this.abbreviation2description.put("ACC", "Adrenocortical Carcinoma");
        this.abbreviation2description.put("BLCA", "Bladder Urothelial Carcinoma");
        this.abbreviation2description.put("BRCA", "Breast Invasive Carcinoma");
        this.abbreviation2description.put("CESC", "Cervical Squamous Cell Carcinoma And Endocervical Adenocarcinoma");
        this.abbreviation2description.put("CHOL", "Cholangiocarcinoma");
        this.abbreviation2description.put("COAD", "Colon Adenocarcinoma");
        this.abbreviation2description.put("DLBC", "Lymphoid Neoplasm Diffuse Large B-Cell Lymphoma");
        this.abbreviation2description.put("ESCA", "Esophageal Carcinoma");
        this.abbreviation2description.put("FPPP", "FFPE Pilot Phase II");
        this.abbreviation2description.put("GBM", "Glioblastoma Multiforme");
        this.abbreviation2description.put("HNSC", "Head And Neck Squamous Cell Carcinoma");
        this.abbreviation2description.put("KICH", "Kidney Chromophobe");
        this.abbreviation2description.put("KIRC", "Kidney Renal Clear Cell Carcinoma");
        this.abbreviation2description.put("KIRP", "Kidney Renal Papillary Cell Carcinoma");
        this.abbreviation2description.put("LAML", "Acute Myeloid Leukemia");
        this.abbreviation2description.put("LGG", "Brain Lower Grade Glioma");
        this.abbreviation2description.put("LIHC", "Liver Hepatocellular Carcinoma");
        this.abbreviation2description.put("LUAD", "Lung Adenocarcinoma");
        this.abbreviation2description.put("LUSC", "Lung Squamous Cell Carcinoma");
        this.abbreviation2description.put("MESO", "Mesothelioma");
        this.abbreviation2description.put("OV", "Ovarian Serous Cystadenocarcinoma");
        this.abbreviation2description.put("PAAD", "Pancreatic Adenocarcinoma");
        this.abbreviation2description.put("PCPG", "Pheochromocythoma And Paraganglioma");
        this.abbreviation2description.put("PRAD", "Prostate Adenocarcinoma");
        this.abbreviation2description.put("READ", "Rectum Adenocarcinoma");
        this.abbreviation2description.put("SARC", "Sarcoma");
        this.abbreviation2description.put("SKCM", "Skin Cutaneous Melanoma");
        this.abbreviation2description.put("STAD", "Stomach Adenocarcinoma");
        this.abbreviation2description.put("TGCT", "Testicular Germ Cell Tumors");
        this.abbreviation2description.put("THCA", "Thyroid Carcinoma");
        this.abbreviation2description.put("THYM", "Thymoma");
        this.abbreviation2description.put("UCEC", "Uterine Corpus Endometrial Carcinoma");
        this.abbreviation2description.put("UCS", "Uterine Carcinosarcoma");
        this.abbreviation2description.put("UVM", "Uveal Melanoma");
        this.abbreviation2description.put("CNTL", "Controls");
        return this.abbreviation2description;
    }

    public String getTissueStatus(String patientRef) {
        try {
            String[] pSplit = patientRef.replaceAll("_", "-").split("-");
            String tumor_normal_ctrl = pSplit[3];
            if (tumor_normal_ctrl.startsWith("0")) {
                return "tumoral";
            } else if (tumor_normal_ctrl.startsWith("1")) {
                return "normal";
            } else if (tumor_normal_ctrl.startsWith("2")) {
                return "control";
            } else {
                return "undefined";
            }
        } catch (Exception e) {
            return "undefined";
        }
    }

}
