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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

import tcga2bed.Main;
import tcga2bed.Settings;

public class QueryParser {
    
    private static int _downloadRecursiveRequestsLimit = 100;

    // clinicalORbiospecimen = "CLINICAL" || "BIOSPECIMEN" || "OMF" 
    public static HashMap<String, HashMap<String, HashMap<String, String>>> getMetadata(File clinical_dir, String clinicalORbiospecimenORomf, HashMap<String, String> parameter2value_filterMap, String shared_id, String format) {
        HashMap<String, HashMap<String, HashMap<String, String>>> metaMap = new HashMap<>();
        if (format.equals("biotab")) {
            for (File f : clinical_dir.listFiles()) {
                String type = MetadataHandler.getType(f);
                HashMap<String, HashMap<String, String>> clinical_map = MetadataHandler.readMetadata(f);
                if (metaMap.isEmpty() || !metaMap.containsKey(type)) {
                    metaMap.put(type, clinical_map);
                } else {
                    HashMap<String, HashMap<String, String>> tmp = metaMap.get(type);
                    for (String p : clinical_map.keySet()) {
                        if (metaMap.get(type).containsKey(p)) {
                            HashMap<String, String> tmp_vals = metaMap.get(type).get(p);
                            tmp_vals.putAll(clinical_map.get(p));
                            tmp.put(p, tmp_vals);
                        } else {
                            tmp.put(p, clinical_map.get(p));
                        }
                    }
                    metaMap.put(type, tmp);
                }
            }
            return metaMap;
        }
        return null;
    }

    public static boolean downloadDataFromUrl(String url, String out_path, int count) {
        try {
            //System.err.println("URL: "+url);
            URL tcga_url = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(tcga_url.openStream());
            FileOutputStream fos = new FileOutputStream(out_path);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            return true;
        } catch (MalformedURLException e) {
            Main.printException(e, true);
            return false;
        } catch (FileNotFoundException e) {
            Main.printException(e, true);
            return false;
        } catch (UnknownHostException e) {
            //System.out.println(url + "\t" + count);
            count++;
            if (count > _downloadRecursiveRequestsLimit)
                return false;
            return downloadDataFromUrl(url, out_path, count);		// if malformed url : loop!!!
        } catch (IOException e) {
            //Main.printException(e, true);
            //return false;
            //System.err.println(url + "\t" + count);
            count++;
            if (count > _downloadRecursiveRequestsLimit)
                return false;
            return downloadDataFromUrl(url, out_path, count);
        }
    }

    public static String getMetadataDataUrl(String url, String out_path) {
        downloadDataFromUrl(url, out_path, 0);
        if ((new File(out_path)).exists()) {
            return XMLReader.getMetadataDataURL(out_path);
        } else {
            return null;
        }
    }

    public static String getMetadataUrl(String url, String out_path) {
        downloadDataFromUrl(url, out_path, 0);
        if ((new File(out_path)).exists()) {
            return XMLReader.getMetadataURL(out_path);
        } else {
            return null;
        }
    }

    public static HashMap<String, HashMap<String, String>> getRecordsFromURL(String url, String out_path) {
        HashMap<String, HashMap<String, String>> result = new HashMap<>();

        downloadDataFromUrl(url, out_path, 0);

        if ((new File(out_path)).exists()) {
            result = XMLReader.getData(out_path);
        }

        return result;
    }

    public static boolean getTCGAFile(String url, String out_file_path) {
        try {
            Runtime rt = Runtime.getRuntime();
            
            String rep_user = "";
            String rep_pass = "";
            if (Settings.repository_isProtected()) {
                rep_user = Settings.getRepositoryUsername();
                rep_pass = Settings.getRepositoryPassword();
            }
            
            String exe_path = Settings.getWGETAbsolutePath();
            String ftp_params = Settings.repository_isProtected() ? " --user="+rep_user+" --password="+rep_pass+" " : " ";
            String cmd_tmp = exe_path + " " + url + " --no-check-certificate -O " + out_file_path;
            Main.printException("WGET: " + cmd_tmp, true);
            
            String cmd_final = exe_path + ftp_params + url + " --no-check-certificate -O " + out_file_path;
            Process proc = rt.exec(cmd_final);

            // Any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), System.err);

            // Any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), System.out);

            errorGobbler.start();
            outputGobbler.start();

            // Any error?
            @SuppressWarnings("unused")
            int exitVal = proc.waitFor();
            errorGobbler.join();   // Handle condition where the
            outputGobbler.join();  // process ends before the threads finish

            return true;

        } catch (Exception e) {
            Main.printException(e, true);
            return false;
        }
    }

    public static class StreamGobbler extends Thread {

        InputStream is;
        PrintStream os;

        StreamGobbler(InputStream is, PrintStream os) {
            this.is = is;
            this.os = os;
        }

        @Override
        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1) //os.print((char) c);
                {
                    Main.printException(Character.toString((char) c), false);
                }
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    }

    public static String getTCGABarcodeFromUUID(String patientRef_uuid, String metadata_dir_path) {
        try {
            String barcode2uuid_file_name_match = "biospecimen_aliquot";
            // starting from 0
            int barcode_index = 2;
            int uuid_index = 3;
            int rows_to_skip = 2;
            
            File biospecimen_aliquot_file = null;
            File meta_dir = new File(metadata_dir_path);
            for (File m: meta_dir.listFiles()) {
                if (m.getName().toLowerCase().contains(barcode2uuid_file_name_match.toLowerCase())) {
                    biospecimen_aliquot_file = m;
                    break;
                }
            }
            if (biospecimen_aliquot_file != null) {
                String barcode = "";
                InputStream fstream = new FileInputStream(biospecimen_aliquot_file.getAbsolutePath());
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                int line_count = 0;
                String line;
                while ((line = br.readLine()) != null) {
                    if (line_count >= rows_to_skip) {
                        String[] line_split = line.split("\t");
                        if (patientRef_uuid.toLowerCase().equals(line_split[uuid_index].toLowerCase())) {
                            barcode = line_split[barcode_index];
                            break;
                        }
                    }
                    line_count++;
                }
                br.close();
                in.close();
                fstream.close();
                return barcode;
            }
            else
                return "_null_";
            
            
            /*String result = "";
            String restApiUUID2Barcode = "https://tcga-data.nci.nih.gov/uuid/uuidws/mapping/json/uuid/" + patientRef_uuid;
            File tmp = File.createTempFile(patientRef_uuid, "txt");
            downloadDataFromUrl(restApiUUID2Barcode, tmp.getAbsolutePath(), 0);

            InputStream fstream = new FileInputStream(tmp.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("validationError")) {
                    result = "_null_";
                } else {
                    result = line.split(",")[0].split(":")[1].replaceAll("\"", "");
                }
                break;
            }
            br.close();
            in.close();
            fstream.close();

            tmp.delete();
            if (result.equals("")) {
                result = "_null_";
            }
            return result;*/
        } catch (Exception e) {
            e.printStackTrace();
            return "_null_";
        }
    }

    public static String getEntrezFromGeneHugo(String geneSymbol) {
        try {
            String result = "";
            String restApiUUID2Barcode = "http://rest.genenames.org/fetch/symbol/" + geneSymbol;
            File tmp = File.createTempFile(geneSymbol, "txt");
            downloadDataFromUrl(restApiUUID2Barcode, tmp.getAbsolutePath(), 0);

            InputStream fstream = new FileInputStream(tmp.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("entrez_id")) {
                    result = line.split(">")[1].split("<")[0].trim();
                    break;
                }
            }
            br.close();
            in.close();
            fstream.close();

            tmp.delete();
            if (result.equals("")) {
                result = "_null_";
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "_null_";
        }
    }

}
