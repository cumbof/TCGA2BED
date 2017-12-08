/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.action;

import java.util.HashMap;

public class HelpAction extends Action {

    /*
     * PARAMETERS:
     * 0: command
     * 1: command for help
     * 
     */
    private static HashMap<String, String> helpMap;

    static {
        initHelpMap();
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1 || args.length == 2) {
            if (args.length > 1) {
                if (helpMap.containsKey(args[1])) {
                    System.out.println(helpMap.get(args[1]));
                } else {
                    System.out.println("> no help found for the command " + args[1]);
                }
            } else {
                if (helpMap.get("_DEFAULT_").equals("_LIST_")) {
                    System.out.println("\nCOMMANDS LIST:\n");
                    int count = 1;
                    for (String s : helpMap.keySet()) {
                        if (!s.equals("_DEFAULT_")) {
                            System.out.println(count + ". " + s);
                            count++;
                        }
                    }
                    System.out.println();
                }
            }
        } else {
            System.out.println("> is this the right number of parameters for " + args[0] + " ?");
        }
    }

    private static void initHelpMap() {
        helpMap = new HashMap<>();
        helpMap.put("_DEFAULT_", "_LIST_");
        helpMap.put("help", "> It's the help...");
        helpMap.put("downloadmeta", "> syntax: downloadmeta [disease_abbreviation] [platform_type] [output_folder]");
        helpMap.put("downloaddata", "> syntax: downloaddata [platform_type] [is_latest (1 | 0)] [level] [disease_abbreviation] [data_type] [output_folder]");
        helpMap.put("convert", "> syntax: convert [disease_abbreviation] [metadata_input_folder] [additional_metadata_file_path] [data_input_folder] [output_folder] [data_type] [data_subtype]");
    }

    @Override
    public void setParameters(Object obj) {
    }

}
