/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed;

import java.util.HashMap;

import tcga2bed.action.Action;

public class Controller {

    private HashMap<String, String> command2action;

    public Controller() {
        init();
    }

    public void computeArgs(String[] args) {
        if (command2action.containsKey(args[0])) {
            Action action;
            try {
                action = (Action) Class.forName(command2action.get(args[0])).newInstance();
                action.execute(args);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                Main.printException(e, true);
            }
        } else {
            System.out.println("INVALID CONFIGURATION FILE!");
        }
    }

    public void compute(String cmd) {
        if (!cmd.trim().equals("")) {
            String[] args = cmd.split(" ");
            if (command2action.containsKey(args[0])) {
                Action action;
                try {
                    action = (Action) Class.forName(command2action.get(args[0])).newInstance();
                    action.execute(args);
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    Main.printException(e, true);
                }
            } else {
                System.out.println("> command not found");
            }
        }
    }

    private void init() {
        command2action = new HashMap<>();
        command2action.put("downloaddata", "tcga2bed.action.DownloadTCGADataAction");
        command2action.put("downloaddatafast", "tcga2bed.action.DownloadTCGADataFastAction");
        command2action.put("downloadmeta", "tcga2bed.action.DownloadTCGAMetaAction");
        command2action.put("convert", "tcga2bed.action.TCGA2GenDataAction");
        command2action.put("help", "tcga2bed.action.HelpAction");
        command2action.put("q", "_UNDEF_");
    }

}
