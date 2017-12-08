/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.action;

import java.io.File;
import tcga2bed.Main;
import tcga2bed.Settings;
import tcga2bed.util.DataExtractionTool;
import tcga2bed.util.QueryParser;

public class DownloadTCGAMetaAction extends Action {

    private boolean extractPackage = false;

    // QUERY EXAMPLE: http://tcga-data.nci.nih.gov/tcga/damws/jobprocess/xml?disease=BRCA&platformType=C
    
    /*private static final String useXML_ext = ".xml";
     private static final String record_ext = ".tar.gz";
     private static String metadata_query_base0 = "http://tcga-data.nci.nih.gov/tcga/damws/jobprocess/";
     private static String metadata_query_base1 = "xml?"; // OR json?
     private static String metadata_query_base2 = metadata_query_base0 + metadata_query_base1;
	
     private static String metadata_query_param0 = "disease=";
     private static String metadata_query_param1 = "platformType=";
	
     private static String metadata_query_concat_params = "&";*/
    
    @Override
    public void execute(String[] args) {
        try {
            // automatically extract data flag
            if (args[4].equals("1"))
                this.setExtract(true);
        } catch (Exception e) {}

        /*String useXML_ext = ".xml";
        String record_ext_targz = ".tar.gz";
        String record_ext_tar = ".tar";
        String metadata_query_base0 = "http://tcga-data.nci.nih.gov/tcga/damws/jobprocess/";
        String metadata_query_base1 = "xml?"; // OR json?
        String metadata_query_base2 = metadata_query_base0 + metadata_query_base1;
        String metadata_query_param0 = "disease=";
        String metadata_query_param1 = "platformType=";
        String metadata_query_concat_params = "&";*/

        // args[0] : downloadmeta
        /*metadata_query_param0 = metadata_query_param0 + args[1];*/
        /*metadata_query_param1 = metadata_query_param1 + args[2];*/
        String usr_folder = args[3];

        /*String metadata_query_final = metadata_query_base2 + metadata_query_param0 + metadata_query_concat_params + metadata_query_param1;*/

        File wdir = new File(usr_folder);
        if (wdir.exists() && wdir.isDirectory()) {
            Main.printException("METABOT_WORKING_DIR: " + wdir.getAbsolutePath(), true);
        } else {
            Main.printException("METABOT_WORKING_DIR: " + wdir.getAbsolutePath() + " DOES NOT EXIST!", true);
            return;
        }
        /*
        Main.printException("METADATA_QUERY_REQUEST: " + metadata_query_final, true);

        String xml_data_request = wdir.getAbsolutePath() + "/" + args[1] + "_" + args[2] + "_meta00" + useXML_ext;
        Main.printException("XML_DATA_REQUEST: " + xml_data_request, true);
        String request_archive_url = QueryParser.getMetadataUrl(metadata_query_final, xml_data_request);
        if (request_archive_url == null || request_archive_url.equals("")) {
            Main.printException("Unexpected thread death", true);
            return;
        }
        Main.printException("REQUEST_ARCHIVE_URL: " + request_archive_url, true);

        String xml_data_url = wdir.getAbsolutePath() + "/" + metadata_query_param0 + "_" + metadata_query_param1 + "_meta01" + useXML_ext;
        Main.printException("XML_DATA_URL: " + xml_data_url, true);

        String archive_url = "";
        boolean request_accepted = false;
        Main.printException("RETRIEVING ARCHIVE URL ...", true);
        while (!request_accepted) {
            archive_url = QueryParser.getMetadataDataUrl(request_archive_url, xml_data_url);
            Main.printException("... ", false);
            if (archive_url != null && !archive_url.equals("")) {
                Main.printException("ARCHIVE_URL: " + archive_url, true);
                request_accepted = true;
            }
        }

        String[] url_split = archive_url.split("/");
        String archive_name = (url_split[url_split.length - 1]).split("\\.")[0];
        
        String archive_path = wdir.getAbsolutePath() + "/" + args[1] + "_" + archive_name + "_record";
        if (archive_url.toLowerCase().endsWith(".tar.gz")) {
            archive_path += record_ext_targz;
        } else if (archive_url.toLowerCase().endsWith(".tar")) {
            archive_path += record_ext_tar;
        }
        */
        
        String archive_url = Settings.getFTPTcgaOriginalRepositoryURL()+args[1].toLowerCase()+"/meta/"+args[1].toLowerCase()+"_metadata.tar.gz";
        
        if (archive_url.toLowerCase().endsWith(".tar.gz") || archive_url.toLowerCase().endsWith(".tar")) {
            Main.printException("DOWNLOADING_ARCHIVE ...", true);
                        
            if (!QueryParser.getTCGAFile(archive_url, wdir.getAbsolutePath()+"/"+args[1].toLowerCase()+"_metadata.tar.gz")) {
                Main.printException("Unexpected thread death", true);
                return;
            }
            if (extractPackage) {
                File extraction_dir = new File(wdir.getAbsolutePath() + "/" + args[1].toLowerCase() + "_metadata/");
                extraction_dir.mkdir();
                Main.printException("ARCHIVE_EXTRACTION: " + extraction_dir.getAbsolutePath(), true);
                if (!DataExtractionTool.uncompressTarGz(new File(wdir.getAbsolutePath()+"/"+args[1].toLowerCase()+"_metadata.tar.gz"), extraction_dir)) {
                    Main.printException("Unexpected thread death", true);
                    return;
                }
            }
            Main.printException("COMPLETED!", true);
        } else {
            Main.printException("Unexpected thread death", true);
            //return;
        }

    }

    // clinicalORbiospecimen = "CLINICAL" || "BIOSPECIMEN" 
    /*private static boolean storeDataInDB(File clinical_xml_dir, String clinicalORbiospecimen, HashMap<String, String> parameter2value_filterMap, String metaTable, String disease_abbreviation) {
     System.err.println("CLINICAL DIR PATH: " + clinical_xml_dir.getAbsolutePath());
     HashMap<String, HashMap<String, String>> patient2clinicalDataMap = TCGAQueryParser.getMetadata(clinical_xml_dir, clinicalORbiospecimen, parameter2value_filterMap, disease_abbreviation);
     System.err.println("PATIENTS MAP SIZE: " + patient2clinicalDataMap.size());
     for (HashMap<String, String> m: patient2clinicalDataMap.values()) {
     for (String k: m.keySet())
     System.out.println("-> " + k + " : " + m.get(k));
     break;
     }
     Facade facade = new Facade();
     return facade.insertMetadata(patient2clinicalDataMap.keySet(), metaTable);
     }	*/
    
    public void setExtract(boolean bool) {
        extractPackage = bool;
    }

    @Override
    public void setParameters(Object obj) {
        setExtract((boolean) obj);
    }

}
