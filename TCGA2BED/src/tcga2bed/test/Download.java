/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.QueryParser;

/**
 *
 * @author fabio
 */
public class Download {
    
    public static void main(String[] args) {
        String download_dir = "d:/downloads/";
        HashMap<String, String> level2_datatypes = dataType2date2url("http://tcga-data.nci.nih.gov/datareports/resources/latestarchive?archiveType=Level_2");
        HashMap<String, String> level3_datatypes = dataType2date2url("http://tcga-data.nci.nih.gov/datareports/resources/latestarchive?archiveType=Level_3");
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseaseMap = HTTPExpInfo.getDiseaseInfo();
        for (String disease: diseaseMap.keySet()) {
            for (String attr: diseaseMap.get(disease).keySet()) {
                if (attr.toLowerCase().endsWith("data_dir_prefix")) {
                    if (attr.toLowerCase().startsWith("dnaseq")) {
                        for (String pack: level2_datatypes.keySet()) {
                            if (pack.toLowerCase().startsWith(diseaseMap.get(disease).get(attr).toLowerCase())) {
                                String url = level2_datatypes.get(pack).split("\t")[1];
                                String pack_name = url.split("/")[url.split("/").length-1];
                                System.err.println("downloading "+pack_name+" ...");
                                boolean download = QueryParser.getTCGAFile(url, download_dir+pack_name);
                                if (download) System.err.println("... completed\n");
                                else System.err.println("... ERROR\n");
                            }
                        }
                    }
                    else {
                        for (String pack: level3_datatypes.keySet()) {
                            if (pack.toLowerCase().startsWith(diseaseMap.get(disease).get(attr).toLowerCase())) {
                                String url = level3_datatypes.get(pack).split("\t")[1];
                                String pack_name = url.split("/")[url.split("/").length-1];
                                System.err.println("downloading "+pack_name+" ...");
                                boolean download = QueryParser.getTCGAFile(url, download_dir+pack_name);
                                if (download) System.err.println("... completed\n");
                                else System.err.println("... ERROR\n");
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static HashMap<String, String> dataType2date2url(String url_string) {
        HashMap<String, String> result = new HashMap<String, String>();
        try {
            URL url = new URL(url_string);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null){
                String[] line_split = line.split("\t");
                result.put(line_split[0], line_split[1]+"\t"+line_split[2]);
            }
            in.close();
        }
        catch (Exception e) { }
        return result;
    }
    
}
