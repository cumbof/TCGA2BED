/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcga2bed.resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import tcga2bed.Settings;
import tcga2bed.util.QueryParser;
import tcga2bed.util.XMLReader;

/**
 *
 * @author Fabio
 */
public class RetrieveMirnaFromHGNC {
    
    public static String HGNC_ARCHIVE = "null";
    public static String db = "node_set476.txt";
    public static String unres = "unresolved.txt";
    public static String res = "resolved.txt";
    private static HashMap<String, String> mirna2hgncid = new HashMap<String, String>();
    private static HashMap<String, String> mirna2symbol = new HashMap<String, String>();
    
    public static HashMap<String, String> getMirna2Hgnc() {
        return mirna2hgncid;
    }
    
    public static HashMap<String, String> getMirna2symbol() {
        return mirna2symbol;
    }
    
    public static void setHgncArchive(String arch) {
        HGNC_ARCHIVE = arch;
    }

    public static String getHgncArchive() {
        return HGNC_ARCHIVE;
    }
    
    public static void loadMirnaTableFromHgnc() {
        if (mirna2hgncid.isEmpty() && mirna2symbol.isEmpty()) {
            try {
                InputStream fstream = new FileInputStream(HGNC_ARCHIVE + "/" + db);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                int line_count = 0;
                while ((line = br.readLine()) != null) {
                    if (line_count > 0) {
                        if (!line.trim().equals("")) {
                            String[] line_split = line.split("\t");
                            
                            String hgnc_id = line_split[0];
                            String symbol = line_split[1];
                            String mirna_ids = line_split[5];
                            String[] mirna_ids_split = mirna_ids.split(",");
                            for (String mirna_id: mirna_ids_split) {
                                String id = mirna_id.replaceAll(",", "").trim();
                                mirna2hgncid.put(id, hgnc_id);
                                mirna2symbol.put(id, symbol);
                            }
                            
                        }
                    }
                    line_count++;
                }
                br.close();
                in.close();
                fstream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String getEntrezFromHGNCid(String hgnc_id) {
        String entrez = "";
        try {
            String hugoFetchQuery = "http://rest.genenames.org/fetch/hgnc_id/" + hgnc_id;
            File hugoXml_tmp = File.createTempFile("hugo_tmp", "tmp");
            QueryParser.downloadDataFromUrl(hugoFetchQuery, hugoXml_tmp.getAbsolutePath(), 0);
            entrez = XMLReader.getEntrezFromHugo(hugoXml_tmp.getAbsolutePath());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return entrez;
    }
    
    public static HashMap<String, String> getResolved() {
        HashMap<String, String> mirna2entrez = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(HGNC_ARCHIVE + "/" + res);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] line_split = line.split("\t");
                    mirna2entrez.put(line_split[0].trim(), line_split[1].trim());
                }
            }
            br.close();
            in.close();
            fstream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mirna2entrez;
    }
    
    public static HashSet<String> getUnresolved() {
        HashSet<String> mirnas = new HashSet<>();
        try {
            InputStream fstream = new FileInputStream(HGNC_ARCHIVE + "/" + unres);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.equals(""))
                    mirnas.add(line.trim());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mirnas;
    }
    
    public static boolean updateResolved(String mirna_id, String entrez_id) {
        File unresFile = new File(HGNC_ARCHIVE + "/" + res);
        if (unresFile.exists()) {
            try {
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(unresFile, true), "UTF-8"));
                output.append(mirna_id + "\t" + entrez_id);
                output.newLine();
                output.close();
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean updateUnresolved(String mirna_id) {
        File unresFile = new File(HGNC_ARCHIVE + "/" + unres);
        if (unresFile.exists()) {
            try {
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(unresFile, true), "UTF-8"));
                output.append(mirna_id);
                output.newLine();
                output.close();
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
    
}
