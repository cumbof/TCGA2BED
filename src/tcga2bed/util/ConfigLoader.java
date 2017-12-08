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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import tcga2bed.Controller;
import tcga2bed.Main;
import tcga2bed.Settings;

/**
 *
 * @author Fabio
 */
public class ConfigLoader {

    public static void loadConfig(String config_xml_path) {
        try {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    HashMap<String, HashMap<String, String>> schedulerMap = FileInputParser.readXMLConfigFile(config_xml_path);
                    ArrayList<Integer> idsInteger = sortXMLConfigIds(schedulerMap.keySet());
                    if (idsInteger == null) {
                        Main.printException("Error in XML Config file!", true);
                    }
                    for (Integer id : idsInteger) {
                        try {
                            HashMap<String, String> operation = schedulerMap.get(String.valueOf(id));
                            String[] op_args = null;
                            if (operation.get("cmd").equals("downloadmeta")) {
                                op_args = new String[5];
                                op_args[0] = operation.get("cmd");
                                op_args[1] = operation.get("disease");
                                op_args[2] = "C";
                                op_args[3] = operation.get("output_folder");
                                op_args[4] = operation.get("autoextract");
                            } else if (operation.get("cmd").equals("downloaddata")) {
                                op_args = new String[5];
                                op_args[0] = "downloaddatafast";
                                op_args[1] = operation.get("disease");
                                op_args[2] = operation.get("data_type");
                                op_args[3] = operation.get("output_folder");
                                op_args[4] = operation.get("autoextract");
                            } else if (operation.get("cmd").equals("convert")) {
                                op_args = new String[12];
                                op_args[0] = operation.get("cmd");
                                op_args[1] = operation.get("disease");
                                op_args[2] = operation.get("metadata");
                                op_args[3] = operation.get("additional_metadata");
                                op_args[4] = operation.get("input_folder");
                                op_args[5] = operation.get("output_folder");
                                op_args[6] = operation.get("data_type");
                                op_args[7] = operation.get("data_subtype");
                                op_args[8] = Settings.getNCBIArchive();
                                op_args[9] = Settings.getUCSCArchive();
                                op_args[10] = Settings.getMIRBASEArchive();
                                op_args[11] = operation.get("magetab_folder");
                            }
                            Controller controller = new Controller();
                            controller.computeArgs(op_args);
                        } catch (Exception e) {}
                    }
                }
            };
            thread.start();
            thread.wait();
            System.out.println("COMPLETED!");
        } catch (Exception e) {}
    }

    private static ArrayList<Integer> sortXMLConfigIds(Set<String> schedulerMapKeys) {
        ArrayList<String> idsString = new ArrayList<>(schedulerMapKeys);
        ArrayList<Integer> idsInteger = new ArrayList<>();
        for (String id : idsString) {
            try {
                idsInteger.add(Integer.valueOf(id));
            } catch (Exception e) {
                return null;
            }
        }
        Collections.sort(idsInteger);
        return idsInteger;
    }
    
    public static HashMap<String, String> loadAppConfig(String app_config_path) {
        HashMap<String, String> config_data = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(app_config_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] line_split = line.trim().split("=");
                    config_data.put(line_split[0].trim().toLowerCase(), line_split[1].trim());
                }
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
        }
        return config_data;
    }

}
