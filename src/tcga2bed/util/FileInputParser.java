/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import tcga2bed.Main;
import tcga2bed.Settings;

public class FileInputParser {

    private static HashMap<String, Integer> headerMap;
    private static HashMap<String, int[]> referenceMap;
    private static final String titleSeparator = "\\.";
    private static final String valueSeparator = "\t";

    static {
        initHeaderMap();
        initPatientReferenceMap();
    }

    public static HashMap<String, HashMap<String, String>> readXMLConfigFile(String file_path) {
        HashMap<String, HashMap<String, String>> schedulerMap = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(file_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            HashMap<String, String> operation = null;
            String operationID = null;
            while ((line = br.readLine()) != null) {
                if (line.contains("<operation")) {
                    operation = new HashMap<>();
                    operationID = line.split("\"")[1];
                } else if (line.contains("</operation")) {
                    schedulerMap.put(operationID, operation);
                } else {
                    if (operation != null) {
                        if (line.contains("<cmd")) {
                            String command = line.split("\"")[1];
                            operation.put("cmd", command);
                        } else if (line.contains("<attribute")) {
                            String[] split_line = line.split("\"");
                            if (split_line[0].contains("name=")) {
                                operation.put(split_line[1], split_line[3]);
                            } else {
                                operation.put(split_line[3], split_line[1]);
                            }
                        }
                    }
                }
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
        }
        return schedulerMap;
    }

    public static HashMap<String, String[]> retrieveData(File file, String data_type) {
        HashMap<String, String[]> data = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            int row_count = 0;
            boolean readData = false;
            while ((line = br.readLine()) != null) {
                if (row_count >= headerMap.get(data_type)) {
                    String[] values = line.split(valueSeparator);

                    if (readData) {
                        data.put(String.valueOf(row_count), values);
                    }

                    if (data_type.toLowerCase().equals("dnaseq") && !readData) {
                        if (!values[0].startsWith("#")) {
                            data.put("header", values);
                            readData = true;
                        }
                    } else {
                        if (row_count == headerMap.get(data_type)) {
                            data.put("header", values);
                            readData = true;
                        }
                    }

                }
                row_count++;
                //System.out.println(row_count);
            }

            br.close();
            in.close();
            fstream.close();

	    //System.out.println("\nEND\n");
        } catch (Exception e) {
            Main.printException(e, true);
            return null;
        }

        return data;
    }

    public static String retrievePatientReference(File file, String data_type) {
        try {
            if (data_type.toLowerCase().equals("cnv"))
                return retrievePatientReferenceCNV(file, data_type);
            
            int[] coordinates = referenceMap.get(data_type);
            if (coordinates[0] >= 0) {
                String title = "";

                InputStream fstream = new FileInputStream(file.getAbsolutePath());
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String line;
                int row = 0;
                while ((line = br.readLine()) != null) {
                    if (row == coordinates[0]) {
                        title = (line.split(valueSeparator))[coordinates[1]];
                        break;
                    }
                    row++;
                }

                br.close();
                in.close();
                fstream.close();

                return title;
            } else {
                String[] arr = file.getName().split(titleSeparator);
                String ref = "";
                for (String s : arr) {
                    if (s.startsWith("TCGA")) {
                        ref = s;
                        break;
                    }
                }

                if (ref.equals("")) {
                    ref = arr[2];
                    return ref;
                }

                return ref;
            }
        } catch (IOException e) {
            Main.printException(e, true);
            return null;
        }
    }

    //LE CHIAVI CORRISPONDONO AI datatype
    private static void initHeaderMap() {
        //values: line number : starts from 0
        headerMap = new HashMap<>();
        headerMap.put("generic", 0);
        headerMap.put("RNASeq", 0);
        headerMap.put("RNASeqV2", 0);
        headerMap.put("DNAMethylation450", 1);
        headerMap.put("DNAMethylation27", 1);
        headerMap.put("DNASeq", 1);
        headerMap.put("miRNASeq", 0);
        headerMap.put("CNV", 0);
    }

    //PATIENT ID COORDINATES
    private static void initPatientReferenceMap() {
        referenceMap = new HashMap<>();
        // {row, column} in file ; [-1 = reference on title] ; [length=1 : all rows and specified column]
        referenceMap.put("RNASeq", new int[]{-1, -1});
        referenceMap.put("RNASeqV2", new int[]{-1, -1});
        referenceMap.put("DNAMethylation450", new int[]{0, 1});
        referenceMap.put("DNAMethylation27", new int[]{0, 1});
        referenceMap.put("DNASeq", new int[]{15});
        referenceMap.put("miRNASeq", new int[]{-1, -1});
        referenceMap.put("CNV", new int[]{-1, -1});
    }

    private static String retrievePatientReferenceCNV(File file, String data_type) {
        String hybridization_reference = file.getName().toLowerCase().split("\\.")[0];
        
        String magetab_path = Settings.getMAGETABPath();
        File magetab_sdrf_file = null;
        for (File f: (new File(magetab_path)).listFiles()) {
            if (f.getName().toLowerCase().endsWith("sdrf.txt")) {
                magetab_sdrf_file = f;
                break;
            }
        }
        
        HashMap<String, String[]> magetab_data = retrieveData(magetab_sdrf_file, "generic");
        int barcode_index = -1;
        int hybridization_name_index = -1;
        int index = 0;
        for (String h: magetab_data.get("header")) {
            if (h.toLowerCase().contains("hybridization"))
                hybridization_name_index = index;
            else if (h.toLowerCase().contains("barcode"))
                barcode_index = index;
            if (barcode_index>0 && hybridization_name_index>0)
                break;
            index++;
        }
        
        for (String row: magetab_data.keySet()) {
            if (!row.equals("header")) {
                if (magetab_data.get(row)[hybridization_name_index].toLowerCase().equals(hybridization_reference))
                    return magetab_data.get(row)[barcode_index];
            }
        }
        
        return null;
    }

}
