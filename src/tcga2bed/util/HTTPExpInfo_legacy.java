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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.HashSet;

public class HTTPExpInfo_legacy {

    private static HashMap<String, HashMap<String, String>> diseaseInfo = new HashMap<>();

    public static HashMap<String, HashMap<String, String>> getDiseaseInfo() {
        return diseaseInfo;
    }

    public static void initDiseaseInfo() {
        diseaseInfo = new HashMap<>();

        HashMap<String, String> info = new HashMap<>();
        info.put("disease", "Adrenocortical carcinoma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/acc/gsc/broad.mit.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_ACC.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/acc/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_ACC.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/acc/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_ACC.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/acc/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_ACC.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/acc/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_ACC.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_ACC.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/acc/bcr/biotab/clin");
        diseaseInfo.put("acc", info);

        info = new HashMap<>();
        info.put("disease", "Bladder Urothelial Carcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/blca/cgcc/unc.edu/illuminahiseq_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "unc.edu_BLCA.IlluminaHiSeq_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_HiSeq_2000");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/blca/gsc/broad.mit.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_BLCA.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/blca/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_BLCA.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/blca/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_BLCA.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/blca/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_BLCA.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/blca/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_BLCA.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_BLCA.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/blca/bcr/biotab/clin");
        diseaseInfo.put("blca", info);

        info = new HashMap<>();
        info.put("disease", "Breast Invasive carcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/brca/cgcc/unc.edu/illuminahiseq_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "unc.edu_BRCA.IlluminaHiSeq_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_HiSeq_2000");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/brca/gsc/genome.wustl.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "genome.wustl.edu_BRCA.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/brca/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_BRCA.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/brca/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_BRCA.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/brca/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_BRCA.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/brca/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_BRCA.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_BRCA.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/brca/bcr/biotab/clin");
        diseaseInfo.put("brca", info);

        info = new HashMap<>();
        info.put("disease", "Cervical squamous cell carcinoma and endocervical adenocarcinoma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/cesc/gsc/genome.wustl.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "genome.wustl.edu_CESC.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/cesc/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_CESC.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/cesc/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_CESC.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/cesc/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_CESC.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/cesc/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_CESC.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_CESC.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/cesc/bcr/biotab/clin");
        diseaseInfo.put("cesc", info);

        info = new HashMap<>();
        info.put("disease", "Cholangiocarcinoma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/chol/gsc/hgsc.bcm.edu/illuminaga_dnaseq_automated/mutations/");
        info.put("dnaseq_data_dir_prefix", "hgsc.bcm.edu_CHOL.IlluminaGA_DNASeq_automated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/chol/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_CHOL.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/chol/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_CHOL.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/chol/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_CHOL.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/chol/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_CHOL.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_CHOL.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/chol/bcr/biotab/clin");
        diseaseInfo.put("chol", info);

        info = new HashMap<>();
        info.put("disease", "Colon adenocarcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/coad/cgcc/unc.edu/illuminaga_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "unc.edu_COAD.IlluminaGA_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_GA2");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/coad/gsc/hgsc.bcm.edu/illuminaga_dnaseq/mutations/");
        info.put("dnaseq_data_dir_prefix", "hgsc.bcm.edu_COAD.IlluminaGA_DNASeq.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/coad/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_COAD.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/coad/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_COAD.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/coad/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_COAD.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/coad/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_COAD.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_COAD.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/coad/bcr/biotab/clin");
        diseaseInfo.put("coad", info);

        info = new HashMap<>();
        info.put("disease", "Lymphoid Neoplasm Diffuse Large B-cell Lymphoma");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/dlbc/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_DLBC.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/dlbc/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_DLBC.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/dlbc/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_DLBC.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/dlbc/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_DLBC.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_DLBC.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/dlbc/bcr/biotab/clin");
        diseaseInfo.put("dlbc", info);

        info = new HashMap<>();
        info.put("disease", "Esophageal carcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/esca/cgcc/bcgsc.ca/illuminahiseq_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "bcgsc.ca_ESCA.IlluminaHiSeq_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_HiSeq_2000");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/esca/gsc/broad.mit.edu/illuminaga_dnaseq_automated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_ESCA.IlluminaGA_DNASeq_automated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/esca/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_ESCA.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/esca/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_ESCA.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/esca/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_ESCA.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_ESCA.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/esca/bcr/biotab/clin");
        diseaseInfo.put("esca", info);

        info = new HashMap<>();
        info.put("disease", "Glioblastoma multiforme");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/gbm/gsc/broad.mit.edu/illuminaga_dnaseq/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_GBM.IlluminaGA_DNASeq.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/gbm/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_GBM.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/gbm/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_GBM.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/gbm/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_GBM.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/gbm/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_GBM.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_GBM.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/gbm/bcr/biotab/clin");
        diseaseInfo.put("gbm", info);

        info = new HashMap<>();
        info.put("disease", "Head and Neck squamous cell carcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/hnsc/cgcc/unc.edu/illuminahiseq_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "unc.edu_HNSC.IlluminaHiSeq_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_HiSeq_2000");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/hnsc/gsc/broad.mit.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_HNSC.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/hnsc/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_HNSC.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/hnsc/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_HNSC.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/hnsc/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_HNSC.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/hnsc/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_HNSC.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_HNSC.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/hnsc/bcr/biotab/clin");
        diseaseInfo.put("hnsc", info);

        info = new HashMap<>();
        info.put("disease", "Kidney Chromophobe");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kich/gsc/broad.mit.edu/illuminaga_dnaseq/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_KICH.IlluminaGA_DNASeq.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kich/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_KICH.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kich/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_KICH.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kich/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_KICH.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kich/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_KICH.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_KICH.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kich/bcr/biotab/clin");
        diseaseInfo.put("kich", info);

        info = new HashMap<>();
        info.put("disease", "Kidney renal clear cell carcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirc/cgcc/unc.edu/illuminahiseq_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "unc.edu_KIRC.IlluminaHiSeq_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_HiSeq_2000");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirc/gsc/broad.mit.edu/illuminaga_dnaseq_automated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_KIRC.IlluminaGA_DNASeq_automated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirc/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_KIRC.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirc/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_KIRC.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirc/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_KIRC.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirc/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_KIRC.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_KIRC.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirc/bcr/biotab/clin");
        diseaseInfo.put("kirc", info);

        info = new HashMap<>();
        info.put("disease", "Kidney renal papillary cell carcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirp/cgcc/unc.edu/illuminahiseq_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "unc.edu_KIRP.IlluminaHiSeq_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_HiSeq_2000");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirp/gsc/hgsc.bcm.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "hgsc.bcm.edu_KIRP.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirp/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_KIRP.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirp/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_KIRP.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirp/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_KIRP.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirp/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_KIRP.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_KIRP.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/kirp/bcr/biotab/clin");
        diseaseInfo.put("kirp", info);

        info = new HashMap<>();
        info.put("disease", "Acute Myeloid Leukemia");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/laml/cgcc/bcgsc.ca/illuminaga_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "bcgsc.ca_LAML.IlluminaGA_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_GA2");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/laml/gsc/genome.wustl.edu/illuminaga_dnaseq/mutations/");
        info.put("dnaseq_data_dir_prefix", "genome.wustl.edu_LAML.IlluminaGA_DNASeq.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/laml/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_LAML.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/laml/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_LAML.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/laml/cgcc/bcgsc.ca/illuminaga_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_LAML.IlluminaGA_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_GA2");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/laml/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_LAML.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_LAML.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/laml/bcr/biotab/clin");
        diseaseInfo.put("laml", info);

        info = new HashMap<>();
        info.put("disease", "Brain Lower Grade Glioma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lgg/gsc/broad.mit.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_LGG.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lgg/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_LGG.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lgg/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_LGG.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lgg/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_LGG.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lgg/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_LGG.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_LGG.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lgg/bcr/biotab/clin");
        diseaseInfo.put("lgg", info);

        info = new HashMap<>();
        info.put("disease", "Liver hepatocellular carcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lihc/cgcc/unc.edu/illuminahiseq_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "unc.edu_LIHC.IlluminaHiSeq_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_HiSeq_2000");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lihc/gsc/hgsc.bcm.edu/illuminaga_dnaseq_automated/mutations/");
        info.put("dnaseq_data_dir_prefix", "hgsc.bcm.edu_LIHC.IlluminaGA_DNASeq_automated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lihc/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_LIHC.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lihc/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_LIHC.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lihc/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_LIHC.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lihc/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_LIHC.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_LIHC.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lihc/bcr/biotab/clin");
        diseaseInfo.put("lihc", info);

        info = new HashMap<>();
        info.put("disease", "Lung adenocarcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/luad/cgcc/unc.edu/illuminahiseq_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "unc.edu_LUAD.IlluminaHiSeq_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_HiSeq_2000");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/luad/gsc/broad.mit.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_LUAD.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/luad/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_LUAD.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/luad/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_LUAD.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/luad/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_LUAD.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/luad/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_LUAD.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_LUAD.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/luad/bcr/biotab/clin");
        diseaseInfo.put("luad", info);

        info = new HashMap<>();
        info.put("disease", "Lung squamous cell carcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lusc/cgcc/unc.edu/illuminahiseq_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "unc.edu_LUSC.IlluminaHiSeq_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_HiSeq_2000");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lusc/gsc/broad.mit.edu/illuminaga_dnaseq/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_LUSC.IlluminaGA_DNASeq.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lusc/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_LUSC.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lusc/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_LUSC.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lusc/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_LUSC.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lusc/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_LUSC.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_LUSC.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/lusc/bcr/biotab/clin");
        diseaseInfo.put("lusc", info);

        info = new HashMap<>();
        info.put("disease", "Mesothelioma");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/meso/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_MESO.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/meso/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_MESO.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/meso/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_MESO.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/meso/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_MESO.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_MESO.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/meso/bcr/biotab/clin");
        diseaseInfo.put("meso", info);

        info = new HashMap<>();
        info.put("disease", "Ovarian serous cystadenocarcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ov/cgcc/bcgsc.ca/illuminahiseq_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "bcgsc.ca_OV.IlluminaHiSeq_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_HiSeq_2000");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ov/gsc/genome.wustl.edu/illuminaga_dnaseq/mutations/");
        info.put("dnaseq_data_dir_prefix", "genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ov/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_OV.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ov/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_OV.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ov/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_OV.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ov/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_OV.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_OV.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ov/bcr/biotab/clin");
        diseaseInfo.put("ov", info);

        info = new HashMap<>();
        info.put("disease", "Pancreatic adenocarcinoma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/paad/gsc/broad.mit.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_PAAD.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/paad/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_PAAD.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/paad/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_PAAD.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/paad/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_PAAD.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/paad/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_PAAD.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_PAAD.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/paad/bcr/biotab/clin");
        diseaseInfo.put("paad", info);

        info = new HashMap<>();
        info.put("disease", "Pheochromocytoma and Paraganglioma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/pcpg/gsc/hgsc.bcm.edu/illuminaga_dnaseq_automated/mutations/");
        info.put("dnaseq_data_dir_prefix", "hgsc.bcm.edu_PCPG.IlluminaGA_DNASeq_automated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/pcpg/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_PCPG.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/pcpg/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_PCPG.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/pcpg/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_PCPG.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/pcpg/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_PCPG.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_PCPG.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/pcpg/bcr/biotab/clin");
        diseaseInfo.put("pcpg", info);

        info = new HashMap<>();
        info.put("disease", "Prostate adenocarcinoma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/prad/gsc/broad.mit.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_PRAD.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/prad/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_PRAD.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/prad/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_PRAD.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/prad/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_PRAD.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/prad/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_PRAD.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_PRAD.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/prad/bcr/biotab/clin");
        diseaseInfo.put("prad", info);

        info = new HashMap<>();
        info.put("disease", "Rectum adenocarcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/read/cgcc/unc.edu/illuminaga_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "unc.edu_READ.IlluminaGA_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_GA2");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/read/gsc/hgsc.bcm.edu/illuminaga_dnaseq/mutations/");
        info.put("dnaseq_data_dir_prefix", "hgsc.bcm.edu_READ.IlluminaGA_DNASeq.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/read/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_READ.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/read/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_READ.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/read/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_READ.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/read/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_READ.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_READ.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/read/bcr/biotab/clin");
        diseaseInfo.put("read", info);

        info = new HashMap<>();
        info.put("disease", "Sarcoma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/sarc/gsc/ucsc.edu/illuminaga_dnaseq_automated/mutations/");
        info.put("dnaseq_data_dir_prefix", "ucsc.edu_SARC.IlluminaGA_DNASeq_automated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/sarc/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_SARC.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/sarc/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_SARC.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/sarc/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_SARC.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/sarc/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_SARC.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_SARC.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/sarc/bcr/biotab/clin");
        diseaseInfo.put("sarc", info);

        info = new HashMap<>();
        info.put("disease", "Skin Cutaneous Melanoma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/skcm/gsc/broad.mit.edu/illuminaga_dnaseq/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_SKCM.IlluminaGA_DNASeq.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/skcm/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_SKCM.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/skcm/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_SKCM.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/skcm/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_SKCM.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/skcm/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_SKCM.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_SKCM.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/skcm/bcr/biotab/clin");
        diseaseInfo.put("skcm", info);

        info = new HashMap<>();
        info.put("disease", "Stomach adenocarcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/stad/cgcc/bcgsc.ca/illuminaga_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "bcgsc.ca_STAD.IlluminaGA_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_GA2");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/stad/gsc/broad.mit.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_STAD.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/stad/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_STAD.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/stad/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_STAD.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/stad/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_STAD.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_STAD.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/stad/bcr/biotab/clin");
        diseaseInfo.put("stad", info);

        info = new HashMap<>();
        info.put("disease", "Testicular Germ Cell Tumors");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/tgct/gsc/broad.mit.edu/illuminaga_dnaseq_automated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_TGCT.IlluminaGA_DNASeq_automated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/tgct/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_TGCT.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/tgct/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_TGCT.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/tgct/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_TGCT.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/tgct/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_TGCT.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_TGCT.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/tgct/bcr/biotab/clin");
        diseaseInfo.put("tgct", info);

        info = new HashMap<>();
        info.put("disease", "Thyroid carcinoma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/thca/gsc/broad.mit.edu/illuminaga_dnaseq/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_THCA.IlluminaGA_DNASeq.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/thca/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_THCA.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/thca/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_THCA.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/thca/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_THCA.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/thca/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_THCA.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_THCA.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/thca/bcr/biotab/clin");
        diseaseInfo.put("thca", info);

        info = new HashMap<>();
        info.put("disease", "Thymoma");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/thym/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_THYM.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/thym/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_THYM.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/thym/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_THYM.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/thym/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_THYM.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_THYM.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/thym/bcr/biotab/clin");
        diseaseInfo.put("thym", info);

        info = new HashMap<>();
        info.put("disease", "Uterine Corpus Endometrial Carcinoma");
        info.put("rnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucec/cgcc/unc.edu/illuminaga_rnaseq/rnaseq/");
        info.put("rnaseq_data_dir_prefix", "unc.edu_UCEC.IlluminaGA_RNASeq.Level_3");
        info.put("rnaseq_platform", "Illumina_GA2");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucec/gsc/broad.mit.edu/illuminaga_dnaseq/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_UCEC.IlluminaGA_DNASeq.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucec/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_UCEC.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucec/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_UCEC.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucec/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_UCEC.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucec/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_UCEC.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_UCEC.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucec/bcr/biotab/clin");
        diseaseInfo.put("ucec", info);

        info = new HashMap<>();
        info.put("disease", "Uterine Carcinosarcoma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucs/gsc/broad.mit.edu/illuminaga_dnaseq_curated/mutations/");
        info.put("dnaseq_data_dir_prefix", "broad.mit.edu_UCS.IlluminaGA_DNASeq_curated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucs/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_UCS.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucs/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_UCS.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucs/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_UCS.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucs/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_UCS.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_UCS.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/ucs/bcr/biotab/clin");
        diseaseInfo.put("ucs", info);

        info = new HashMap<>();
        info.put("disease", "Uveal Melanoma");
        info.put("dnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/uvm/gsc/hgsc.bcm.edu/illuminaga_dnaseq_automated/mutations/");
        info.put("dnaseq_data_dir_prefix", "hgsc.bcm.edu_UVM.IlluminaGA_DNASeq_automated.Level_2");
        info.put("dnaseq_platform", "Illumina_GA2");
        info.put("dnamethylation_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/uvm/cgcc/jhu-usc.edu/humanmethylation450/methylation/");
        info.put("dnamethylation_data_dir_prefix", "jhu-usc.edu_UVM.HumanMethylation450.Level_3");
        info.put("dnamethylation_platform", "HumanMethylation450");
        info.put("rnaseqv2_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/uvm/cgcc/unc.edu/illuminahiseq_rnaseqv2/rnaseqv2/");
        info.put("rnaseqv2_data_dir_prefix", "unc.edu_UVM.IlluminaHiSeq_RNASeqV2.Level_3");
        info.put("rnaseqv2_platform", "Illumina_HiSeq_2000");
        info.put("mirnaseq_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/uvm/cgcc/bcgsc.ca/illuminahiseq_mirnaseq/mirnaseq/");
        info.put("mirnaseq_data_dir_prefix", "bcgsc.ca_UVM.IlluminaHiSeq_miRNASeq.Level_3");
        info.put("mirnaseq_platform", "Illumina_HiSeq_2000");
        info.put("cnv_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/uvm/cgcc/broad.mit.edu/genome_wide_snp_6/snp/");
        info.put("cnv_data_dir_prefix", "broad.mit.edu_UVM.Genome_Wide_SNP_6.Level_3");
        info.put("cnv_magetab_dir_prefix", "broad.mit.edu_UVM.Genome_Wide_SNP_6.mage-tab");
        info.put("cnv_platform", "Genome_Wide_SNP_6");
        info.put("meta_root_dir", "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/uvm/bcr/biotab/clin");
        diseaseInfo.put("uvm", info);
    }

    // if folder : endsWith="/"  -  id data : endsWith=".tar.gz"
    public static HashMap<String, String> getData2Date(String url, String out_path, String disease, String data_type, String endsWith, String attribute) {
        try {
            HashMap<String, String> folder2date = new HashMap<>();
            System.err.println("URL: " + url);
            URL tcga_url = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(tcga_url.openStream());
            FileOutputStream fos = new FileOutputStream(out_path);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();

            InputStream fstream = new FileInputStream(out_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("Name") && !line.contains("Last modified") && !line.contains("Size") && !line.contains("Parent Directory")) {
                    if (line.contains("a href")) {
                        String[] arr = line.split("\"");
                        String link = arr[1];
                        if (link.startsWith(diseaseInfo.get(disease.toLowerCase()).get(data_type.toLowerCase() + attribute)) && link.endsWith(endsWith)) {
                            String[] lineSplit = line.replaceAll("\\s+", "\\|").split("\\|");
                            String date = lineSplit[lineSplit.length - 3];
                            folder2date.put(link, date);
                        }
                    }
                }
            }
            br.close();
            in.close();
            fstream.close();

            return folder2date;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static HashSet<String> getFiles(String url, String out_path, String notEndsWith) {
        HashSet<String> files = new HashSet<String>();
        try {
            URL tcga_url = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(tcga_url.openStream());
            FileOutputStream fos = new FileOutputStream(out_path);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            
            InputStream fstream = new FileInputStream(out_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("Name") && !line.contains("Last modified") && !line.contains("Size") && !line.contains("Parent Directory")) {
                    if (line.contains("a href")) {
                        String[] arr = line.split("\"");
                        String link = arr[1];
                        if (!link.endsWith(notEndsWith)) {
                            files.add(link);
                        }
                    }
                }
            }
            br.close();
            in.close();
            fstream.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return files;
    }

    public static String searchForUpdate(HashMap<String, String> data2date) {
        //date format : YYYY-MM-DD
        String updatedFolder = "";
        String updatedDate = "";
        for (String f : data2date.keySet()) {
            if (updatedDate.equals("")) {
                updatedDate = data2date.get(f);
                updatedFolder = f;
            } else {
                String[] splitUpdatedDate = updatedDate.split("-");
                String[] currentDate = data2date.get(f).split("-");
                if (Integer.valueOf(currentDate[0]) > Integer.valueOf(splitUpdatedDate[0])) {
                    updatedDate = data2date.get(f);
                    updatedFolder = f;
                } else if (Integer.valueOf(currentDate[0]) == Integer.valueOf(splitUpdatedDate[0])) {
                    if (Integer.valueOf(currentDate[1]) > Integer.valueOf(splitUpdatedDate[1])) {
                        updatedDate = data2date.get(f);
                        updatedFolder = f;
                    } else if (Integer.valueOf(currentDate[1]) == Integer.valueOf(splitUpdatedDate[1])) {
                        if (Integer.valueOf(currentDate[2]) > Integer.valueOf(splitUpdatedDate[2])) {
                            updatedDate = data2date.get(f);
                            updatedFolder = f;
                        }
                    }
                }
            }
        }
        return updatedFolder;
    }

    /*public static void main(String[] args) {
        try {
            initDiseaseInfo();
            String disease = "KIRP";
            String data_type = "RNASeq";
            File tmp = File.createTempFile("test_http_tcga", "html");
            String out_path = tmp.getAbsolutePath();
            String url = diseaseInfo.get(disease.toLowerCase()).get(data_type.toLowerCase()+"_root_dir");
			
            HashMap<String, String> folder2date = getFolders2Date(url, out_path, disease, data_type);
            tmp.delete();
            String updatedFolder = searchForUpdatedFolder(folder2date);
            System.err.println(url+updatedFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    
    /*public static void main(String[] args) {
        initDiseaseInfo();
        ArrayList<String> diseases = new ArrayList<>(diseaseInfo.keySet());
        Collections.sort(diseases);
        for (String disease: diseases) {
            System.err.println("> "+disease);
            for (String data: diseaseInfo.get(disease).keySet()) {
                if (data.toLowerCase().endsWith("dir_prefix"))
                    System.err.println("\tinfo.put(\""+data+"\", \""+diseaseInfo.get(disease).get(data)+"\");");
            }
        }
    }*/
}
