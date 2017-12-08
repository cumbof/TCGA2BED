/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.HashSet;
import tcga2bed.util.QueryParser;

public class RetrieveNCBIGenomicCoordinates {

    public static String NCBI_ARCHIVE = "null";
    public static String unres = "unresolved.txt";
    public static String res = "resolved.txt";

    public static void setNcbiArchive(String arch) {
        NCBI_ARCHIVE = arch;
    }

    public static String getNcbiArchive() {
        return NCBI_ARCHIVE;
    }

    public static void updateUnresolvedQueries(String gene_entrez) {
        File unresFile = new File(NCBI_ARCHIVE + "/" + unres);
        if (unresFile.exists()) {
            
            try {
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(unresFile, true), "UTF-8"));
                output.append(gene_entrez);
                output.newLine();
                output.close();
            } catch (Exception e) {
            }
        }
    }

    public static boolean updateLocalGenomeCoordinatesDB(String entrez, String gene) {
        HashMap<String, String> coordInfo = simpleRetrieveGenomicCoordinates(entrez, gene);
        
        /*for (String k: coordInfo.keySet()) {
            System.err.println(k + " : " + coordInfo.get(k));
        }
        System.err.println();*/
        
        if (!coordInfo.isEmpty()) {
            if (coordInfo.containsKey("CHR")) {
                if (!coordInfo.get("CHR").equals("") && (Integer.valueOf(coordInfo.get("START")) > 0 && Integer.valueOf(coordInfo.get("END")) > 0)) {
                    File resFile = new File(NCBI_ARCHIVE + "/" + res);
                    if (resFile.exists()) {
                        try {
                            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resFile, true), "UTF-8"));
                            output.append(entrez + "\t" + gene + "\t" + coordInfo.get("NCBI_ENTREZ") + "\t" + coordInfo.get("NCBI_SYMBOL") + "\t" + coordInfo.get("CHR") + "\t" + coordInfo.get("START") + "\t" + coordInfo.get("END") + "\t" + coordInfo.get("STRAND"));
                            output.newLine();
                            output.close();
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public static HashSet<String> loadTextDB_unresolved() {
        HashSet<String> unr = new HashSet<>();
        try {
            InputStream fstream = new FileInputStream(NCBI_ARCHIVE + "/" + unres);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                unr.add(line);
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
        }
        return unr;
    }

    public static HashMap<String, HashMap<String, String>> loadTextDB_resolved() {
        HashMap<String, HashMap<String, String>> result = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(NCBI_ARCHIVE + "/" + res);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String[] arr = line.split("\t");
                    HashMap<String, String> info = new HashMap<>();
                    info.put("STRAND", arr[7]);
                    info.put("START", arr[5]);
                    info.put("END", arr[6]);
                    info.put("CHR", arr[4]);
                    info.put("NCBI_SYMBOL", arr[3]);
                    info.put("NCBI_ENTREZ", arr[2]);
                    info.put("TCGA_SYMBOL", arr[1]);
                    info.put("TCGA_ENTREZ", arr[0]);
                    String original_entrez = arr[0];
                    result.put(original_entrez, info);
                }
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void downloadDataFromUrl(String url, String out_path) {
        try {
            URL new_url = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(new_url.openStream());
            FileOutputStream fos = new FileOutputStream(out_path);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> simpleRetrieveGenomicCoordinates(String entrez, String gene) {
        try {
            HashMap<String, String> result = new HashMap<>();
            
            HashMap<String, String> result_tmp = new HashMap<String, String>();
            
            String strand = "";
            String chr = "";
            int start = -1;
            int end = -1;

            //String ncbiBiotabQuery = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi/?db=Gene&id=" + entrez + "&format=biotab";
            String ncbiBiotabQuery = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi/?db=Gene&id=" + entrez;
            File ncbiBiotab_tmp = File.createTempFile("efetch.fcgi", "txt");
            QueryParser.downloadDataFromUrl(ncbiBiotabQuery, ncbiBiotab_tmp.getAbsolutePath(), 0);
            BufferedReader reader = new BufferedReader(new FileReader(ncbiBiotab_tmp.getAbsolutePath()));
            String line = reader.readLine();
            
            String genomeVersion = "grch37";
            boolean genomeMatch = false; // match on GRCh37 genome version
            
            boolean replaced = false;
            boolean db_GeneID = false;
            
            String newGene = "";
            String newEntrez = "";
            while (line != null) {
                /////////////////////////////////////////////////
                if (line.trim().toLowerCase().contains("heading") && line.toLowerCase().contains(genomeVersion))
                    genomeMatch = true;
                /////////////////////////////////////////////////
                if (line.trim().toLowerCase().contains("current-id")) {
                    replaced = true;
                }
                if (replaced) {
                    if (line.trim().toLowerCase().contains("db \"geneid\"")) {
                        db_GeneID = true;
                    }
                    if (db_GeneID) {
                        if (line.trim().toLowerCase().contains("tag id")) {
                            String[] line_split = line.split(" ");
                            newEntrez = line_split[line_split.length-1];
                            result_tmp = simpleRetrieveGenomicCoordinates(newEntrez, gene);
                            result_tmp.put("TCGA_ENTREZ", newEntrez);
                            break;
                        }
                    }
                }
                ////////////////////////////////////////////////
                if (line.trim().toLowerCase().contains("locus")) {
                    if (newGene.equals("")) {
                        String[] lineSplit = line.split(" ");
                        String lastString = lineSplit[lineSplit.length-1].replaceAll("\"", "");
                        newGene = lastString.substring(0, lastString.length()-1);
                        //System.err.println("--- newGene: " + newGene);
                    }
                }
                else if (line.trim().toLowerCase().contains("geneid")) {
                    if (newEntrez.equals("")) {
                        String[] lineSplit = line.split(" ");
                        String lastString = lineSplit[lineSplit.length-1];
                        newEntrez = lastString.substring(0, lastString.length()-1);
                        //System.err.println("--- newEntrez: " + newEntrez);
                    }
                }
                    
                if (genomeMatch) {
                    if (line.trim().toLowerCase().contains("label") && line.trim().toLowerCase().contains("chromosome")) {
                        if (chr.equals("")) {
                            String[] lineSplit = line.split(" ");
                            String lastString = lineSplit[lineSplit.length-1].replaceAll("\"", "");
                            chr = lastString.substring(0, lastString.length()-1);
                            //System.err.println("--- chr: " + chr);
                        }
                    }
                    else if (line.trim().toLowerCase().contains("from")) {
                        if (start == -1) {
                            String[] lineSplit = line.split(" ");
                            String lastString = lineSplit[lineSplit.length-1];
                            start = Integer.valueOf(lastString.substring(0, lastString.length()-1));
                            //System.err.println("--- start: " + start);
                        }
                    }
                    else if (line.trim().toLowerCase().contains("to")) {
                        if (end == -1) {
                            String[] lineSplit = line.split(" ");
                            String lastString = lineSplit[lineSplit.length-1];
                            end = Integer.valueOf(lastString.substring(0, lastString.length()-1));
                            //System.err.println("--- end: " + end);
                        }
                    }
                    else if (line.trim().toLowerCase().contains("strand")) {
                        if (strand.equals("")) {
                            String[] lineSplit = line.split(" ");
                            String lastString = lineSplit[lineSplit.length-1];
                            String strand_ = lastString.substring(0, lastString.length()-1);
                            if (strand_.trim().toLowerCase().equals("minus"))
                                strand = "-";
                            else if (strand_.trim().toLowerCase().equals("plus"))
                                strand = "+";
                            //System.err.println("--- strand: " + strand);
                        }
                    }
                }
                line = reader.readLine();
            }
            reader.close();
            ncbiBiotab_tmp.delete();
            
            //System.err.println();
            
            if (!result_tmp.isEmpty())
                return result_tmp;

            if (!chr.equals("") && (start > 0 && end > 0)) {
                result.put("CHR", chr);
                result.put("START", String.valueOf(start+1)); // +1 : NCBI is 0-based
                result.put("END", String.valueOf(end+1)); // +1 : NCBI is 0-based
                result.put("STRAND", strand);
                result.put("TCGA_ENTREZ", entrez);
                result.put("TCGA_SYMBOL", gene);
                result.put("NCBI_ENTREZ", newEntrez);
                result.put("NCBI_SYMBOL", newGene);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
        
}
