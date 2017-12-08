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
            // automatically extract data flag
            if (args[4].equals("1"))
                this.setExtract(true);
        } catch (Exception e) {}
        
        HTTPExpInfo.initDiseaseInfo();
        String disease = args[1];
        String data_type = args[2];
        System.err.println("downloading " + disease + " : " + data_type + " ...");
        String out_path = args[3];

        if (!(new File(out_path)).exists()) {
            (new File(out_path)).mkdirs();
        }
        if (!out_path.endsWith("/")) {
            out_path += "/";
        }

        System.err.println("DISEASE: " + disease);
        System.err.println("DATA TYPE: " + data_type);

        String dataDirPrefix = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_data_dir_prefix");
        String url = Settings.getFTPTcgaOriginalRepositoryURL()+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/"+dataDirPrefix+".tar.gz";
                        
        String magetabDirPrefix = "";
        String magetabUrl = "";

        if (data_type.toLowerCase().equals("cnv")) {
            magetabDirPrefix = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_magetab_dir_prefix");
            magetabUrl = Settings.getFTPTcgaOriginalRepositoryURL()+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/"+magetabDirPrefix+".tar.gz";
        }

        if (!QueryParser.getTCGAFile(url, out_path + dataDirPrefix + ".tar.gz")) {
            Main.printException("Unexpected thread death", true);
            return;
        }

        if (extractPackage) {
            Main.printException("extracting package: "+dataDirPrefix+".tar.gz ... ", true);
            (new File(out_path+"/"+dataDirPrefix+"/")).mkdir();
            if (!CompressionUtil.decompress(out_path+"/"+dataDirPrefix+".tar.gz", out_path+"/"+dataDirPrefix+"/")) {
                Main.printException("Unexpected thread death", true);
                return;
            }
            else
                Main.printException("... extraction completed!", true);
        }

        // CNV exception: Mage-Tab data retrieval
        if (data_type.toLowerCase().equals("cnv")) {
            if (!QueryParser.getTCGAFile(magetabUrl, out_path + magetabDirPrefix + ".tar.gz")) {
                Main.printException("Unexpected thread death", true);
                return;
            }

            if (extractPackage) {
                Main.printException("extracting package: "+magetabDirPrefix+".tar.gz ... ", true);
                (new File(out_path+"/"+magetabDirPrefix+"/")).mkdir();
                if(!CompressionUtil.decompress(out_path+"/"+magetabDirPrefix+".tar.gz", out_path+"/"+magetabDirPrefix+"/")) {
                    Main.printException("Unexpected thread death", true);
                    return;
                }
                else
                    Main.printException("... extraction completed!", true);
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