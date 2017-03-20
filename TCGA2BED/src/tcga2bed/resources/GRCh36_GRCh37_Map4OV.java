/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcga2bed.resources;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 *
 * @author fabio
 */
public class GRCh36_GRCh37_Map4OV {
    
    public static String GRCH36_GRCH37_ARCHIVE = "null";

    public static void setGRCh36GRCh37Aarchive(String arch) {
        GRCH36_GRCH37_ARCHIVE = arch;
    }

    public static String getGRCh36GRCh37Aarchive() {
        return GRCH36_GRCH37_ARCHIVE;
    }
    
    public static HashMap<String, String> load_GRCh36_GRCh37_map() {
        HashMap<String, String> map = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(GRCH36_GRCH37_ARCHIVE);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.trim().equals("")) {
                        String[] arr = line.split("\t");
                        map.put(arr[0], arr[1]);
                    }
                } catch (Exception e) {}
            }
            br.close();
            in.close();
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
    
}
