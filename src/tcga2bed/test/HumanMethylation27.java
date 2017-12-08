/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcga2bed.test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Fabio
 */
public class HumanMethylation27 {
    
    private static final String input_download_path = "e:/download/";
    private static final String out_folder_path = "e:/ftp-root/tcga_original/";
    
    private static HashMap<String, ArrayList<File>> tumor2files = new HashMap<>();
    
    public static void main(String[] args) {
        System.err.println("search datasets");
        searchDatasets();
        for (String tumor: tumor2files.keySet())
            System.err.println(tumor+": "+tumor2files.get(tumor).size());
        
        System.err.println("create tumor folders and organize datasets");
        for (String tumor: tumor2files.keySet()) {
            String out_path = out_folder_path + tumor.toLowerCase() + "/dnamethylation27/";
            if (!(new File(out_path)).exists())
                (new File(out_path)).mkdirs();
            for (File file: tumor2files.get(tumor)) {
                Path out_filepath = (new File(out_path+file.getName())).toPath();
                try {
                    Files.copy(file.toPath(), out_filepath, REPLACE_EXISTING);
                    System.err.println((new File(out_path+file.getName())).getAbsolutePath());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void searchDatasets() {
        for (File folder: (new File(input_download_path)).listFiles()) {
            if (folder.isDirectory()) {
                for (File file: (new File(folder.getAbsolutePath())).listFiles()) {
                    if (file.getName().toLowerCase().contains("lvl-3")) {
                        String tumor_tmp = file.getName().split("_")[1];
                        String tumor = tumor_tmp.split("\\.")[0];
                        ArrayList<File> files = new ArrayList<>();
                        if (tumor2files.containsKey(tumor))
                            files = tumor2files.get(tumor);
                        files.add(file);
                        tumor2files.put(tumor, files);
                    }
                }
            }
        }
    }
    
}
