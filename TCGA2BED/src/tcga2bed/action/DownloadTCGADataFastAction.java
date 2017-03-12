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
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.QueryParser;
import tcga2bed.util.CompressionUtil;

public class DownloadTCGADataFastAction extends Action{

    private static boolean extractPackage = false;

    @Override
    public void execute(String[] args) {
        try {
            if (args[4].equals("1"))
                this.setExtract(true);
        } catch (Exception e) {}
        
        /*String[] diseases = new String[33];
        diseases[0] = "acc";
        diseases[1] = "blca";
        diseases[2] = "brca";
        diseases[3] = "cesc";
        diseases[4] = "chol";
        diseases[5] = "coad";
        diseases[6] = "dlbc";
        diseases[7] = "esca";
        diseases[8] = "gbm";
        diseases[9] = "hnsc";
        diseases[10] = "kich";
        diseases[11] = "kirc";
        diseases[12] = "kirp";
        diseases[13] = "laml";
        diseases[14] = "lgg";
        diseases[15] = "lihc";
        diseases[16] = "luad";
        diseases[17] = "lusc";
        diseases[18] = "meso";
        diseases[19] = "ov";
        diseases[20] = "paad";
        diseases[21] = "pcpg";
        diseases[22] = "prad";
        diseases[23] = "read";
        diseases[24] = "sarc";
        diseases[25] = "skcm";
        diseases[26] = "stad";
        diseases[27] = "tgct";
        diseases[28] = "thca";
        diseases[29] = "thym";
        diseases[30] = "ucec";
        diseases[31] = "ucs";
        diseases[32] = "uvm";*/
		
        /*String[] data_types = new String[1];
        data_types[0] = "dnaseq";
        data_types[1] = "dnamethylation";
        data_types[2] = "rnaseq";
        data_types[3] = "rnaseqv2";
        data_types[4] = "mirnaseq";
        data_types[0] = "cnv";*/
        HTTPExpInfo.initDiseaseInfo();
        //for (String disease: diseases) {
            //for (String data_type: data_types) {
                //System.err.println(disease + " : " + data_type);
                //if (HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).containsKey(data_type.toLowerCase()+"_root_dir")) {
                    /*try {*/
                        String disease = args[1];
                        String data_type = args[2];
                        
                        System.err.println("downloading " + disease + " : " + data_type + " ...");
                        
                        String out_path = args[3];
                        //String disease_path = "d:/ftp-root/tcga_original/"+disease+"/";
                        if (!(new File(out_path)).exists()) {
                            (new File(out_path)).mkdirs();
                        }
                        if (!out_path.endsWith("/")) {
                            out_path += "/";
                        }
                        
                        /*File tmp = File.createTempFile("http_tcga", "html");
                        String tmp_path = tmp.getAbsolutePath();*/
                        System.err.println("DISEASE: " + disease);
                        System.err.println("DATA TYPE: " + data_type);
                        /*String url = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_root_dir");*/
                        
                        String dataDirPrefix = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_data_dir_prefix");
                        String url = Settings.getFTPTcgaOriginalRepositoryURL()+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/"+dataDirPrefix+".tar.gz";
                        
                        String magetabDirPrefix = "";
                        String magetabUrl = "";
                        
                        /*HashMap<String, String> data2date = HTTPExpInfo.getData2Date(url, tmp_path, disease, data_type, ".tar.gz", "_data_dir_prefix");
                        tmp.delete();*/
                        //String updatedData = HTTPExpInfo.searchForUpdate(data2date);
                        
                        /*HashMap<String, String> magetab2date = new HashMap<>();*/
                        //String updatedMageTab = "";
                        if (data_type.toLowerCase().equals("cnv")) {
                            /*tmp = File.createTempFile("http_tcga", "html");
                            magetab2date = HTTPExpInfo.getData2Date(url, tmp_path, disease, data_type, ".tar.gz", "_magetab_dir_prefix");
                            tmp.delete();*/
                            //updatedMageTab = HTTPExpInfo.searchForUpdate(magetab2date);
                            magetabDirPrefix = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_magetab_dir_prefix");
                            magetabUrl = Settings.getFTPTcgaOriginalRepositoryURL()+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/"+magetabDirPrefix+".tar.gz";
                        }

                        //File lastupdate_file = new File(disease_path + data_type+".lastupdate.txt");
                        //String currentDate = "";
                        /*if (lastupdate_file.exists()) {
                            InputStream fstream = new FileInputStream(lastupdate_file.getAbsolutePath());
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            String line;
                            while ((line = br.readLine()) != null) {
                                try {
                                    currentDate = line.split("\t")[1];
                                } catch (Exception e) {
                                }
                                break;
                            }
                            br.close();
                            in.close();
                            fstream.close();
                        }*/

                        //if (currentDate.equals("")) {
                            /*if (!data2date.isEmpty()) {*/
                                /*for (String updatedData: data2date.keySet()) {*/
                                    /*if (!QueryParser.getTCGAFile(url + updatedData, out_path + updatedData)) {*/
                                    if (!QueryParser.getTCGAFile(url, out_path + dataDirPrefix + ".tar.gz")) {
                                        Main.printException("Unexpected thread death", true);
                                        return;
                                    }

                                    /*FileOutputStream fos = new FileOutputStream(lastupdate_file.getAbsolutePath());
                                    PrintStream out = new PrintStream(fos);
                                    out.println("last update\t" + data2date.get(updatedData));
                                    out.close();
                                    fos.close();
                                    System.err.println(disease + " : " + data_type + " -> " + out_path + updatedData + "  :  " + data2date.get(updatedData) + "\n");
                                    */
                                    if (extractPackage) {
                                        /*Main.printException("extracting package: "+updatedData+" ... ", true);*/
                                        Main.printException("extracting package: "+dataDirPrefix+".tar.gz ... ", true);
                                        //EXTRACT record_data_path IN record_out_dir
                                        //if (!DataExtractionTool.uncompressTarGz(new File(out_path + updatedData), new File(out_path))) {
                                        (new File(out_path+"/"+dataDirPrefix+"/")).mkdir();
                                        /*if (!CompressionUtil.decompress(out_path + updatedData, out_path+"/"+dataDirPrefix+"/")) {*/
                                        if (!CompressionUtil.decompress(out_path+"/"+dataDirPrefix+".tar.gz", out_path+"/"+dataDirPrefix+"/")) {
                                            Main.printException("Unexpected thread death", true);
                                            return;
                                        }
                                        else
                                            Main.printException("... extraction completed!", true);
                                    }
                                /*}*/

                                if (data_type.toLowerCase().equals("cnv")) {
                                    /*for (String updatedMageTab: magetab2date.keySet()) {*/
                                        /*if (!QueryParser.getTCGAFile(url + updatedMageTab, out_path + updatedMageTab)) {*/
                                        if (!QueryParser.getTCGAFile(magetabUrl, out_path + dataDirPrefix + ".tar.gz")) {
                                            Main.printException("Unexpected thread death", true);
                                            return;
                                        }

                                        if (extractPackage) {
                                            /*Main.printException("extracting package: "+updatedMageTab+" ... ", true);*/
                                            Main.printException("extracting package: "+magetabDirPrefix+".tar.gz ... ", true);
                                            //if (!DataExtractionTool.uncompressTarGz(new File(out_path + updatedMageTab), new File(out_path))) {
                                            (new File(out_path+"/"+magetabDirPrefix+"/")).mkdir();
                                            /*if(!CompressionUtil.decompress(out_path + updatedMageTab, out_path+"/"+magetabDirPrefix+"/")) {*/
                                            if(!CompressionUtil.decompress(out_path+"/"+magetabDirPrefix+".tar.gz", out_path+"/"+magetabDirPrefix+"/")) {
                                                Main.printException("Unexpected thread death", true);
                                                return;
                                            }
                                            else
                                                Main.printException("... extraction completed!", true);
                                        }
                                    }
                                /*}*/
                            /*}*/
                            /*else {
                                Main.printException("TCGA Repository is currently unreachable, please try again later.", true);
                            }*/
                            
                        /*} else {
                            String[] updatedDate_split = data2date.get(updatedData).split("-");
                            String[] currentDate_split = currentDate.split("-");
                            boolean to_update = false;
                            if (Integer.valueOf(currentDate_split[0]) >= Integer.valueOf(updatedDate_split[0])) {
                                if (Integer.valueOf(currentDate_split[1]) >= Integer.valueOf(updatedDate_split[1])) {
                                    if (Integer.valueOf(currentDate_split[2]) >= Integer.valueOf(updatedDate_split[2])) {
                                        System.err.println("dataset already up to date");
                                    } else {
                                        to_update = true;
                                    }
                                } else {
                                    to_update = true;
                                }
                            } else {
                                to_update = true;
                            }

                            if (to_update) {
                                if (!QueryParser.getTCGAFile(url + updatedData, out_path + updatedData)) {
                                    Main.printException("Unexpected thread death", true);
                                    return;
                                }
                                if (data_type.toLowerCase().equals("cnv")) {
                                    if (!QueryParser.getTCGAFile(url + updatedMageTab, out_path + updatedMageTab)) {
                                        Main.printException("Unexpected thread death", true);
                                        return;
                                    }
                                }
                                //FileOutputStream fos = new FileOutputStream(lastupdate_file.getAbsolutePath());
                                //PrintStream out = new PrintStream(fos);
                                //out.println("last update\t" + data2date.get(updatedData));
                                //out.close();
                                //fos.close();
                                //System.err.println(disease + " : " + data_type + " -> " + out_path + updatedData + "  :  " + data2date.get(updatedData) + "\n");
                                
                                if (extractPackage) {
                                    Main.printException("DATA EXTRACTION... ", true);
                                    //EXTRACT record_data_path IN record_out_dir
                                    if (!DataExtractionTool.uncompressTarGz(new File(out_path + updatedData), new File(out_path))) {
                                        Main.printException("Unexpected thread death", true);
                                        return;
                                    }
                                    else
                                        Main.printException("... EXTRACTION COMPLETED!", true);
                                    if (data_type.toLowerCase().equals("cnv")) {
                                        if (!DataExtractionTool.uncompressTarGz(new File(out_path + updatedMageTab), new File(out_path))) {
                                            Main.printException("Unexpected thread death", true);
                                            return;
                                        }
                                        else
                                            Main.printException("... EXTRACTION COMPLETED!", true);
                                    }
                                }
                            }
                        }*/
                    /*} catch (IOException | NumberFormatException e) {
                        e.printStackTrace();
                    }*/
                //}
            //}
        //}
    }

    public void setExtract(boolean bool) {
        extractPackage = bool;
    }

    @Override
    public void setParameters(Object obj) {
        setExtract((boolean) obj);
    }

}