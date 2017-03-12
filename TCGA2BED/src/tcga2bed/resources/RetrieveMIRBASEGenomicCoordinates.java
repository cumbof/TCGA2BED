/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.resources;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class RetrieveMIRBASEGenomicCoordinates {

    public static String MIRBASE_ARCHIVE = "null";

    public static void setMirbaseAarchive(String arch) {
        MIRBASE_ARCHIVE = arch;
    }

    public static String getMirbaseArchive() {
        return MIRBASE_ARCHIVE;
    }

    public static HashMap<String, HashMap<String, String>> loadTextDB() {
        HashMap<String, HashMap<String, String>> result = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(MIRBASE_ARCHIVE);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("#")) {
                        String[] arr = line.split("\t");
                        HashMap<String, String> info = new HashMap<>();
                        //System.err.println(line);
                        info.put("STRAND", arr[6]);
                        info.put("START", arr[3]);
                        info.put("END", arr[4]);
                        info.put("CHR", arr[0]);

                        String[] id_split = arr[arr.length-1].split(";");
                        String original_symbol = "null";
                        for (String var: id_split) {
                            if (var.toLowerCase().startsWith("name"))
                                original_symbol = var.split("=")[1];
                        }
                        info.put("MIRBASE_SYMBOL", original_symbol);

                        result.put(original_symbol, info);
                    }
                } catch (Exception e) {}
            }
            br.close();
            in.close();
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
