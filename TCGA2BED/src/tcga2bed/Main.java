/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import tcga2bed.action.Action;
import tcga2bed.action.TCGA2GenDataAction;
import tcga2bed.util.ConfigLoader;

public class Main {

    /*
     *  QUERY EXAMPLE - [WARNING: NOT VALID -> TCGA DATA PORTAL IS NO LONGER AVAILABLE: June 30th, 2016] 
     *  http://tcga-data.nci.nih.gov/tcgadccws/GetHTML?query=Archive[Platform[@name=Genome_Wide_SNP_6]][@isLatest=1][ArchiveType[@type=Level_3]][Disease[@abbreviation=BRCA]]
     */
    private static BufferedReader br;
    private static String _String;

    public static void start(String[] args) {

        if (args != null && args.length == 1) {
            ConfigLoader.loadConfig(args[0]);
        } else if (args != null && args.length > 1) {
            System.out.println("INVALID ARGUMENTS!");
        } else if (args != null && args.length == 0) {

            //Controller controller = new Controller();
            
            System.out.println();
            System.out.println("> type 'q' to exit TCGAinBED");
            System.out.println("> type 'help' for a list of available commands");
            System.out.println();
            System.out.print("> ");
            /*String command = "";
             while (!command.equals("q")) {
             //command syntax: space separated values
             command = readString();
             if (!command.equals("q")) {
             controller.compute(command);
             System.out.print("> ");
             }
             }
             System.out.println();
             System.out.println("> application terminated\n");*/

            /* DOWNLOAD DATA EXAMPLE */
            /*args = new String[7];
             args[0] = "downloaddata";                                                                                      //action
             args[1] = "IlluminaGA_DNASeq_curated";                                                                         //platform
             args[2] = "1";                                                                                                 //isLatest
             args[3] = "Level_2";                                                                                           //data_level
             args[4] = "BLCA";                                                                                              //tumor_type
             args[5] = "DNASeq";                                                                                            //data_type
             args[6] = "c:/users/fabio/desktop/data_download/";                                                             //download_folder
             Action action = new DownloadTCGADataAction();*/
            
            /* DOWNLOAD METADATA EXAMPLE */
            /*args = new String[4];
             args[0] = "downloadmeta";                                                                                      //action
             args[1] = "UVM";                                                                                               //tumor_type
             args[2] = "C";                                                                                                 //tcga_parameter_for_metadata_download
             args[3] = "C:/Users/Fabio/Desktop/meta_download/";                                                             //download_folder
             Action action = new DownloadTCGAMetaAction();*/
            
            /* CONVERSION EXAMPLE */
            /*args = new String[12];
            args[0] = "convert";                                                                                            //action
            args[1] = "KIRP";                                                                                               //tumor_type
            args[2] = "D:/tcga/kirp/meta/Clinical/Biotab/";                                                                 //biotab_metadata_folder
            args[3] = "null";                                                                                               //additional_biotab_metadata_folder
            args[4] = "D:/tcga/kirp/rnaseq/unc.edu_KIRP.IlluminaHiSeq_RNASeq.Level_3.1.0.0/";                               //tcga_input_folder
            args[5] = "D:/tcga/kirp/rnaseq/gene/";                                                                          //gendata_output_folder
            args[6] = "RNASeq";                                                                                             //data_type
            args[7] = "gene";                                                                                               //data_subtype : for RNASeq only [gene, exon, spljxn]
            args[8] = "C:/Users/Fabio/Desktop/config/ncbi_archive/";                                                        //entrezId_to_geneSymbol_local_db_dir
            args[9] = "C:/Users/Fabio/Desktop/config/ucsc_archive/knownGene.txt";                                           //knownGene UCSC db table
            args[10] = "C:/Users/Fabio/Desktop/appdata/mirbase_archive/hsa.gff3";                                           //MIRBase db table
            args[11] = "D:/ftp-root/tcga_original/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/mage-tab/";         //Mage-Tab: for CNV data type only
            Action action = new TCGA2GenDataAction();

            System.err.println("DISEASE: " + args[1].toUpperCase());
            //System.err.println("DATA-TYPE: " + args[6] + "  --  DATA-SUBTYPE: " + args[7]);
            System.err.println("DATA-TYPE: " + args[6]);
            Date start = new Date();
            System.out.println("START: " + start.toString());
            action.execute(args);
            Date end = new Date();
            System.out.println("END: " + end.toString());*/

            int skip = 0;
            
            ArrayList<String> diseases = new ArrayList<>();
            diseases.add("ACC");
            /*diseases.add("BLCA");
            diseases.add("BRCA");
            diseases.add("CESC");
            diseases.add("CHOL");
            diseases.add("COAD");
            diseases.add("DLBC");
            diseases.add("ESCA");
            diseases.add("GBM");
            diseases.add("HNSC");
            diseases.add("KICH");
            diseases.add("KIRC");
            diseases.add("KIRP");
            diseases.add("LAML");
            diseases.add("LGG");
            diseases.add("LIHC");
            diseases.add("LUAD");
            diseases.add("LUSC");
            diseases.add("MESO");
            diseases.add("OV");
            diseases.add("PAAD");
            diseases.add("PCPG");
            diseases.add("PRAD");
            diseases.add("READ");
            diseases.add("SARC");
            diseases.add("SKCM");
            diseases.add("STAD");
            diseases.add("TGCT");
            diseases.add("THCA");
            diseases.add("THYM");
            diseases.add("UCEC");
            diseases.add("UCS");
            diseases.add("UVM");*/

            ArrayList<String> data_types = new ArrayList<>();
            //data_types.add("DNAMethylation450");
            //data_types.add("DNASeq");
            //data_types.add("RNASeq");
            //data_types.add("RNASeqV2");
            //data_types.add("miRNASeq");
            data_types.add("CNV");
            //data_types.add("DNAMethylation27");

            ArrayList<String> st_tmp = new ArrayList<>();
            /*st_tmp.add("gene");
            st_tmp.add("exon");
            st_tmp.add("spljxn");
            st_tmp.add("mirna");
            st_tmp.add("isoform");*/

            int count = 0;
            for (String disease: diseases) {
                if (count >= skip) {
                    for (String data_type: data_types) {
                        ArrayList<String> st = st_tmp;
                        if (data_type.toLowerCase().equals("dnamethylation27") || data_type.toLowerCase().equals("dnamethylation450") || data_type.toLowerCase().equals("dnaseq") || data_type.toLowerCase().equals("cnv")) {
                            st = new ArrayList<>();
                            st.add("bed");
                        }
                        for (String subtype: st) {
                            args = new String[13];
                            args[0] = "convert";
                            args[1] = disease;
                            args[2] = "E:/ftp-root/tcga_original/"+disease.toLowerCase()+"/meta/Clinical/Biotab/";
                            args[3] = "null";
                            File inDir = new File("E:/ftp-root/tcga_original/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/");
                            args[4] = "E:/ftp-root/tcga_original/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/";
                            //args[4] = "G:/brca_dnameth_tcga/";
                            if (subtype.toLowerCase().equals("bed")) {
                                //args[5] = "G:/ftp-root/bed/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/";
                                if (inDir.exists()) {
                                    File outDir = new File("E:/newBeds/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/");
                                    if (!outDir.exists())
                                        outDir.mkdirs();
                                }
                                args[5] = "E:/newBeds/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/";
                                //args[5] = "F:/test/";
                            }
                            else {
                                //args[5] = "E:/ftp-root/bed/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/"+subtype.toLowerCase()+".quantification/";
                                if (inDir.exists()) {
                                    File outDir = new File("E:/newBeds/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/"+subtype.toLowerCase()+".quantification/");
                                    if (!outDir.exists())
                                        outDir.mkdirs();
                                }
                                args[5] = "E:/newBeds/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/"+subtype.toLowerCase()+".quantification/";
                                //args[5] = "F:/test/";
                            }
                            args[6] = data_type;
                            args[7] = subtype;
                            args[8] = "C:/Users/fabio/Documents/NetBeansProjects/TCGA2BED/package/appdata/ncbi_archive/";
                            args[9] = "C:/Users/fabio/Documents/NetBeansProjects/TCGA2BED/package/appdata/ucsc_archive/knownGene.txt";
                            args[10] = "C:/Users/fabio/Documents/NetBeansProjects/TCGA2BED/package/appdata/mirbase_archive/hsa.gff3";
                            args[11] = "C:/Users/fabio/Documents/NetBeansProjects/TCGA2BED/package/appdata/hgnc_archive/";
                            args[12] = "E:/ftp-root/tcga_original/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/mage-tab/"; // only for cnv
                            
                            if (inDir.exists()) {
                                Action action = new TCGA2GenDataAction();
                                System.err.println("DISEASE: " + args[1].toUpperCase());
                                System.err.println("DATA-TYPE: " + args[6] + "  --  DATA-SUBTYPE: " + args[7]);
                                Date start = new Date();
                                System.out.println("START: " + start.toString());
                                action.execute(args);
                                Date end = new Date();
                                System.out.println("END: " + end.toString());
                                System.out.println();
                            }
                        }
                    }
                }
                count++;
             }
        }
    }

    public static void main(String[] args) {
        start(args);
    }

    public static String readString() {
        br = new BufferedReader(new InputStreamReader(System.in));
        try {
            _String = br.readLine();
        } catch (IOException e) {
            Main.printException(e, true);
        }
        return (_String);
    }

    public static void printException(Exception e, boolean newLine) {
        e.printStackTrace();
        GUI.printText(e.getMessage(), newLine);
    }

    public static void printException(String message, boolean newLine) {
        if (newLine) {
            System.err.println(message);
        } else {
            System.err.print(message);
        }
        GUI.printText(message, newLine);
    }

}
