/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed;

import tcga2bed.util.OSValidator;

/**
 *
 * @author Fabio
 */
public class Settings {
    
    private static final boolean repository_isProtected = false;
    public static boolean repository_isProtected() {
        return repository_isProtected;
    }
    
    private static final String repository_username = "guest";
    public static String getRepositoryUsername() {
        return repository_username;
    }
    
    private static final String repository_password = "gendata";
    public static String getRepositoryPassword() {
        return repository_password;
    }
    
    private static final boolean fast_download = true;
    private static final boolean debug = false;
    
    public static String magetab_path = "";
    
    public static String getMAGETABPath() {
        return magetab_path;
    }
    
    public static void setMAGETABPath(String mt_path) {
        magetab_path = mt_path;
    }
    
    public static String getHGNCArchive() {
        if (debug) return "C:/Users/fabio/Documents/NetBeansProjects/TCGA2BED/package/appdata/hgnc_archive/";
        return "./appdata/hgnc_archive/";
    }
    
    public static String getGRCh36ToGRCh37Archive4OV() {
        if (debug) return "C:/Users/fabio/Documents/NetBeansProjects/TCGA2BED/package/appdata/GRCh36ToGRCh37_archive/table.txt";
        return "./appdata/GRCh36ToGRCh37_archive/table.txt";
    }

    public static String getNCBIArchive() {
        if (debug) return "C:/Users/fabio/Documents/NetBeansProjects/TCGA2BED/package/appdata/ncbi_archive/";
        return "./appdata/ncbi_archive/";
    }

    public static String getUCSCArchive() {
        if (debug) return "C:/Users/fabio/Documents/NetBeansProjects/TCGA2BED/package/appdata/ucsc_archive/knownGene.txt";
        return "./appdata/ucsc_archive/knownGene.txt";
    }
    
    public static String getMIRBASEArchive() {
        if (debug) return "C:/Users/fabio/Documents/NetBeansProjects/TCGA2BED/package/appdata/mirbase_archive/hsa.gff3";
        return "./appdata/mirbase_archive/hsa.gff3";
    }

    public static String getWGETAbsolutePath() {
        if (debug) return "C:/Users/fabio/Documents/NetBeansProjects/TCGA2BED/package/appdata/wget.exe";
        else {
            if (OSValidator.isWindows())
                return "./appdata/wget.exe";
            else if (OSValidator.isUnix())
                return "/usr/bin/wget";
            else if (OSValidator.isMac())
                return "/usr/bin/wget";
        }
        return "./appdata/wget.exe";
    }

    public static boolean fastDownload() {
        return fast_download;
    }

    public static String getHelpURL() {
        return "http://bioinf.iasi.cnr.it/tcga2bed/";
    }
    
    public static String getFTPBedRepositoryURL() {
        return "ftp://bioinf.iasi.cnr.it/bed/";
    }
    
    public static String getFTPTcgaOriginalRepositoryURL() {
        return "ftp://bioinf.iasi.cnr.it/tcga_original/";
    }
    
    public static String[] getDiseases() {
        String[] arr = new String[34];
        arr[0] = "";
        arr[1] = "ACC";
        arr[2] = "BLCA";
        arr[3] = "BRCA";
        arr[4] = "CESC";
        arr[5] = "CHOL";
        arr[6] = "COAD";
        arr[7] = "DLBC";
        arr[8] = "ESCA";
        arr[9] = "GBM";
        arr[10] = "HNSC";
        arr[11] = "KICH";
        arr[12] = "KIRC";
        arr[13] = "KIRP";
        arr[14] = "LAML";
        arr[15] = "LGG";
        arr[16] = "LIHC";
        arr[17] = "LUAD";
        arr[18] = "LUSC";
        arr[19] = "MESO";
        arr[20] = "OV";
        arr[21] = "PAAD";
        arr[22] = "PCPG";
        arr[23] = "PRAD";
        arr[24] = "READ";
        arr[25] = "SARC";
        arr[26] = "SKCM";
        arr[27] = "STAD";
        arr[28] = "TGCT";
        arr[29] = "THCA";
        arr[30] = "THYM";
        arr[31] = "UCEC";
        arr[32] = "UCS";
        arr[33] = "UVM";
        return arr;
    }

    public static String[] getDataTypes4Download() {
        String[] arr = new String[1];
        arr[0] = "";
        /*arr[1] = "DNA Methylation";
        arr[2] = "Somatic Mutations";
        arr[3] = "RNA-Seq";
        arr[4] = "RNA-Seq V2";
        arr[5] = "miRNASeq";
        arr[6] = "Copy Number Variations";*/
        return arr;
    }
    
    public static String[] getDataTypes4Conversion() {
        String[] arr = new String[14];
        arr[0] = "";
        arr[1] = "DNA Methylation 450";
        arr[2] = "DNA Methylation 27";
        arr[3] = "Somatic Mutations";
        arr[4] = "RNA-Seq gene-quantification";
        arr[5] = "RNA-Seq exon-quantification";
        arr[6] = "RNA-Seq spljxn-quantification";
        arr[7] = "RNA-Seq V2 gene-quantification";
        arr[8] = "RNA-Seq V2 exon-quantification";
        arr[9] = "RNA-Seq V2 spljxn-quantification";
        arr[10] = "RNA-Seq V2 isoform-quantification";
        arr[11] = "miRNASeq mirna-quantification";
        arr[12] = "miRNASeq isoform-quantification";
        arr[13] = "Copy Number Variations";
        return arr;
    }
    
    public static String[] getPlatforms() {
        String[] arr = new String[6];
        arr[0] = "";
        arr[1] = "Illumina_GA2";
        arr[2] = "HumanMethylation450";
        arr[3] = "HumanMethylation27";
        arr[4] = "Illumina_HiSeq_2000";
        arr[5] = "Genome_Wide_SNP_6";
        return arr;
    }
    
    public static String[] getLevels() {
        String[] arr = new String[4];
        arr[0] = "";
        arr[1] = "Level_1";
        arr[2] = "Level_2";
        arr[3] = "Level_3";
        return arr;
    }
    
}
