/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.action;

import java.io.File;
import java.util.HashMap;

import tcga2bed.Main;
import tcga2bed.util.DataExtractionTool;
import tcga2bed.util.QueryParser;

public class DownloadTCGADataAction extends Action {

    private boolean extractPackage = false;

    private final boolean useXML = true;
    private final String useXML_ext = ".xml";
    private final String tcga = "http://tcga-data.nci.nih.gov";
    private String query_base = tcga + "/tcgadccws/Get";
    private final String record_ext_targz = ".tar.gz";
    private final String record_ext_tar = ".tar";

    @Override
    public void execute(String[] args) {
		//args[0] : downloaddata

		//SE VENGONO AGGIUNTI NUOVI PARAMETRI:
        //TODO AGGIUNGERE EVENTUALI NUOVI PARAMETRI IN FileOutputWriter.writeDataInfo()
        //TODO AGGIUNGERE COMMENTI A Parameters IN FileInputParser.loadConfig()		
        String platform_name = "[Platform[@name=" + args[1] + "]]";
        String islatest = "[@isLatest=" + args[2] + "]";
        String level_type = "[ArchiveType[@type=" + args[3] + "]]";
        String disease_abbreviation = "[Disease[@abbreviation=" + args[4] + "]]";
        String data_type = args[5];
        String wdir_path = args[6];
        System.err.println(args[5]);

        if (useXML) {
            query_base += "XML";
        } else {
            query_base += "HTML";
        }
        query_base += "?query=";
        String query_parameters_00 = "Archive" + platform_name + islatest + level_type + disease_abbreviation;
        String query_list_00 = query_base + query_parameters_00;

        Main.printException("QUERY_00: " + query_list_00, true);
        File wdir = new File(wdir_path);
        if (wdir.exists() && wdir.isDirectory()) {
            Main.printException("WORKING_DIR: " + wdir.getAbsolutePath(), true);
        } else {
            Main.printException("WORKING_DIR: " + wdir.getAbsolutePath() + " DOES NOT EXIST!", true);
            return;
        }

        File wdir_path_dir = new File(wdir_path);
        if (wdir_path_dir.isDirectory()) {

			//////////////////////////////////////////////////////////////////////////////////////////////
            String _00 = wdir.getAbsolutePath() + "/" + args[1] + "_" + args[4] + "_" + data_type + "_data00" + useXML_ext;
            Main.printException("XML_DATA_00: " + _00, true);
            HashMap<String, HashMap<String, String>> _00_records_tag2value = QueryParser.getRecordsFromURL(query_list_00, _00);

            //FILTER RECORDS FOR DATATYPE
            HashMap<String, HashMap<String, String>> filterForDatatype = new HashMap<>();
            int count_datatype = 1;
            for (String record : _00_records_tag2value.keySet()) {
                String archive_id = _00_records_tag2value.get(record).get("id");
                String query_parameters_01 = "Platform&Archive[@id=" + archive_id + "]&roleName=platform";
                String query_list_01 = query_base + query_parameters_01;

                Main.printException("XML_DATA_01: " + query_list_01, true);

                String _01 = wdir.getAbsolutePath() + "/" + platform_name + "_" + disease_abbreviation + "_" + data_type + "_data01" + useXML_ext;
                HashMap<String, HashMap<String, String>> _01_records_tag2value = QueryParser.getRecordsFromURL(query_list_01, _01);

                String platform_id = _01_records_tag2value.get("1").get("id");
                String query_parameters_02 = "Datatype&Platform[@id=" + platform_id + "]&Archive[@id=" + archive_id + "]&roleName=platform&roleName=baseDataType";
                String query_list_02 = query_base + query_parameters_02;

                Main.printException("XML_DATA_02: " + query_list_02, true);

                String _02 = wdir.getAbsolutePath() + "/" + platform_name + "_" + disease_abbreviation + "_" + data_type + "_data02" + useXML_ext;
                HashMap<String, HashMap<String, String>> _02_records_tag2value = QueryParser.getRecordsFromURL(query_list_02, _02);

				//System.err.println(_02_records_tag2value.get("1").get("name").replaceAll(" ", "_") + " : " + data_type);
                if (_02_records_tag2value.get("1").get("name").replaceAll(" ", "_").equals(data_type)) {
                    filterForDatatype.put(String.valueOf(count_datatype), _00_records_tag2value.get(record));
                    count_datatype++;
                } else if (data_type.equals("DNASeq") && _02_records_tag2value.get("1").get("name").replaceAll(" ", "_").equals("Somatic_Mutations")) {
                    filterForDatatype.put(String.valueOf(count_datatype), _00_records_tag2value.get(record));
                    count_datatype++;
                } else if (data_type.equals("DNAMethylation") && _02_records_tag2value.get("1").get("name").replaceAll(" ", "_").equals("DNA_Methylation")) {
                    filterForDatatype.put(String.valueOf(count_datatype), _00_records_tag2value.get(record));
                    count_datatype++;
                }
            }

			//System.err.println(filterForDatatype.isEmpty());
            if (filterForDatatype.isEmpty()) {
                Main.printException("Unexpected thread death [errcode:00]", true);
                return;
            }
            Main.printException("RECORD FOR DATATYPE " + data_type + " : " + filterForDatatype.size(), true);

            //FILTER RECORDS FOR DATA
            HashMap<String, String> lastRecord = new HashMap<>();
            String[] currentDate = {"01", "01", "1000"}; //MONTH, DAY, YEAR
            for (String record : _00_records_tag2value.keySet()) {
                //TCGA DATE FORMAT EXAMPLE : 12-30-1989
                String[] date = _00_records_tag2value.get(record).get("addedDate").split("-");
                if (Integer.valueOf(date[2]) > Integer.valueOf(currentDate[2])) {
                    currentDate = date;
                    lastRecord = _00_records_tag2value.get(record);
                } else if (Integer.valueOf(date[2]) == Integer.valueOf(currentDate[2])) {
                    if (Integer.valueOf(date[0]) > Integer.valueOf(currentDate[0])) {
                        currentDate = date;
                        lastRecord = _00_records_tag2value.get(record);
                    } else if (Integer.valueOf(date[0]) == Integer.valueOf(currentDate[0])) {
                        if (Integer.valueOf(date[1]) > Integer.valueOf(currentDate[1])) {
                            currentDate = date;
                            lastRecord = _00_records_tag2value.get(record);
                        }
                    }
                }
            }
            Main.printException("LAST RECENTLY RECORD: " + "MONTH=" + currentDate[0] + " - DAY=" + currentDate[1] + " - YEAR=" + currentDate[2], true);

            String record_data_link = tcga + lastRecord.get("deployLocation");
            Main.printException("RECORD_DATA_LINK: " + record_data_link, true);

            String record_data_path = wdir.getAbsolutePath() + "/" + lastRecord.get("name") + "_record";
            if (record_data_link.toLowerCase().endsWith(".tar")) {
                record_data_path += record_ext_tar;
            } else if (record_data_link.toLowerCase().endsWith(".tar.gz")) {
                record_data_path += record_ext_targz;
            }
            Main.printException("RECORD_DATA_PATH: " + record_data_path, true);
            if (record_data_link.toLowerCase().endsWith(".tar.gz") || record_data_link.toLowerCase().endsWith(".tar")) {
                if (!QueryParser.getTCGAFile(record_data_link, record_data_path)) {
                    Main.printException("Unexpected thread death [errcode:01]", true);
                    return;
                }

                if (extractPackage) {
                    String record_out_dir = wdir.getAbsolutePath() + "/";
                    Main.printException("RECORD_OUT_DIR: " + record_out_dir, true);
                    Main.printException("DATA EXTRACTION... ", true);
                    //EXTRACT record_data_path IN record_out_dir
                    if (!DataExtractionTool.uncompressTarGz(new File(record_data_path), new File(record_out_dir))) {
                        Main.printException("Unexpected thread death [errcode:02]", true);
                        return;
                    }
                }
                Main.printException("...COMPLETED", true);
            }
        }
    }

    public void setExtract(boolean bool) {
        extractPackage = bool;
    }

    @Override
    public void setParameters(Object obj) {
        setExtract((boolean) obj);
    }

}
