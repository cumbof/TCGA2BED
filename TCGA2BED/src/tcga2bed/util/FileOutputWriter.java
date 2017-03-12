/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import tcga2bed.Main;

public class FileOutputWriter {

    private static HashMap<String, String> nulled_meta_terms = new HashMap<>();

    public static void initMetaNulledTerms() {
        nulled_meta_terms.put("[not available]", "null");
        nulled_meta_terms.put("[not evaluated]", "null");
        nulled_meta_terms.put("[not applicable]", "null");
        nulled_meta_terms.put("[unknown]", "null");
        nulled_meta_terms.put("lce", "null");
    }

    public static void writeGendataHeader(File output, String[] header, String[] types) {
        try {
            output.createNewFile();
            FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
            PrintStream out = new PrintStream(fos);

            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<gmqlSchemaCollection name=\"GLOBAL_SCHEMAS\" xmlns=\"http://genomic.elet.polimi.it/entities\">");
            out.println("\t<gmqlSchema type=\"tab\">");

            for (int i = 0; i < header.length; i++) {
                out.println("\t\t<field type=\"" + types[i] + "\">" + header[i] + "</field>");
            }

            out.println("\t</gmqlSchema>");
            out.println("</gmqlSchemaCollection>");

            out.close();
            fos.close();
        } catch (IOException e) {
            Main.printException(e, true);
        }
    }

    public static void writeMetaDictionary(File output, HashMap<String, HashSet<String>> metadata_dictionary) {
        try {
            initMetaNulledTerms();
            output.createNewFile();
            FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
            PrintStream out = new PrintStream(fos);

            List<String> meta_keys = new ArrayList<>(metadata_dictionary.keySet());
            Collections.sort(meta_keys);
            for (String k : meta_keys) {
                List<String> meta_values = new ArrayList<>(metadata_dictionary.get(k));
                Collections.sort(meta_values);
                for (String s : nulled_meta_terms.keySet()) {
                    if (meta_values.contains(s)) {
                        meta_values.remove(s);
                    }
                }
                if (!meta_values.isEmpty()) {
                    if (meta_values.size() > 0 && !meta_values.get(0).startsWith("CDE_ID:")) {
                        out.println(k);
                        for (String v : meta_values) {
                            if ((metadata_dictionary.get(k).size() > 1 && !v.equals("")) || (metadata_dictionary.get(k).size() == 1)) {
                                if (!nulled_meta_terms.containsKey(v) && !v.startsWith("CDE_ID:")) {
                                    out.println("\t" + v);
                                }
                            }
                        }
                        out.println();
                    }
                }
            }

            out.close();
            fos.close();
        } catch (IOException e) {
            Main.printException(e, true);
        }
    }

    public static void writeMetadata(String outPath, String outSuffix, String ext, HashMap<String, HashMap<String, HashMap<String, String>>> metadata, HashSet<String> gendataPatients, String dataType) {
        initMetaNulledTerms();
        //metadata = fixMetadata(metadata, gendataPatients);

        for (String p : gendataPatients) {
            HashMap<String, String> patient_meta = new HashMap<>();
            //System.err.println(p);
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
                List<String> meta_keys = new ArrayList<>(patient_meta.keySet());
                Collections.sort(meta_keys);
                try {
                    File output = new File(outPath + p + ext);
                    output.createNewFile();
                    FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
                    PrintStream out = new PrintStream(fos);

                    for (String k : meta_keys) {
                        if (!nulled_meta_terms.containsKey(patient_meta.get(k))) {
                            out.println(k + "\t" + patient_meta.get(k));
                        }
                    }

                    out.close();
                    fos.close();
                } catch (IOException e) {
                    Main.printException(e, true);
                }
            }
        }
    }

    /*private static HashMap<String, HashMap<String, String>> fixMetadata(HashMap<String, HashMap<String, HashMap<String, String>>> metadata, HashSet<String> gendataPatients) {
     HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String,String>>();
		
     for (String ref: gendataPatients) {
     for (String type: metadata.keySet()) {
     HashMap<String, String> meta = new HashMap<String, String>();
     for (String metaRef: metadata.get(type).keySet()) {
     if (ref.replaceAll("_", "-").contains(metaRef)) {
     meta.putAll(metadata.get(metaRef));
     if (metadata.containsKey(ref))
     meta.putAll(metadata.get(ref));
     }
     }
     if (!meta.isEmpty())
     result.put(ref.replaceAll("_", "-"), meta);
     }
     }
		
     return result;
     }*/
    public static void writeTabularInformationData(File output, HashSet<String> aliquotSet, HashSet<String> sampleSet, HashSet<String> patientSet) {
        try {
            FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
            PrintStream out = new PrintStream(fos);

            out.println("aliquot_count\t" + aliquotSet.size());
            out.println("sample_count\t" + sampleSet.size());
            out.println("patient_count\t" + patientSet.size());

            out.close();
            fos.close();
        } catch (Exception e) {
            Main.printException(e, true);
        }
    }

    public static void writeAnnotationFile(String outPath, HashSet<String> data) {
        try {
            FileOutputStream fos = new FileOutputStream(outPath);
            PrintStream out = new PrintStream(fos);

            for (String row : data) {
                out.println(row);
            }

            out.close();
            fos.close();
        } catch (Exception e) {
            Main.printException(e, true);
        }
    }

    public static void writeMetaFile(File output, HashMap<String, HashSet<String>> metadata) {
        try {
            initMetaNulledTerms();
            output.createNewFile();
            FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
            PrintStream out = new PrintStream(fos);

            List<String> meta_keys = new ArrayList<>(metadata.keySet());
            Collections.sort(meta_keys);
            for (String k : meta_keys) {
                List<String> meta_values = new ArrayList<>(metadata.get(k));
                Collections.sort(meta_values);
                for (String s : nulled_meta_terms.keySet()) {
                    if (meta_values.contains(s)) {
                        meta_values.remove(s);
                    }
                }
                if (!meta_values.isEmpty()) {
                    if (meta_values.size() > 0 && !meta_values.get(0).startsWith("CDE_ID:")) {
                        out.println(k+"\t"+meta_values.get(0));
                    }
                }
            }

            out.close();
            fos.close();
        } catch (IOException e) {
            Main.printException(e, true);
        }
    }
    
}
