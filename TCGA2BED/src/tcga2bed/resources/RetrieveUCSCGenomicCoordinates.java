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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class RetrieveUCSCGenomicCoordinates {

    public static String UCSC_ARCHIVE = "null";

    public static void setUcscAarchive(String arch) {
        UCSC_ARCHIVE = arch;
    }

    public static String getUcscArchive() {
        return UCSC_ARCHIVE;
    }

    public static HashMap<String, HashMap<String, String>> loadTextDB() {
        HashMap<String, HashMap<String, String>> result = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(UCSC_ARCHIVE);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split("\t");
                HashMap<String, String> info = new HashMap<>();
                info.put("STRAND", arr[2]);
                try {
                    info.put("START", arr[3] + 1); // UCSC uses 0-based start position
                }
                catch (Exception e) {
                    info.put("START", arr[3]);
                }
                info.put("END", arr[4]); // UCSC uses 1-based start position
                info.put("CHR", arr[1]);
                info.put("UCSC_SYMBOL", arr[10]);
                String original_transcript = arr[0];
                result.put(original_transcript, info);
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
