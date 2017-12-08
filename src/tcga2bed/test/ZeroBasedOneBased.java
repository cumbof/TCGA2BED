/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 *
 * @author Fabio
 */
public class ZeroBasedOneBased {

    public static void main(String[] args) {
        String[] diseases = new String[33];
        diseases[0] = "ACC";
        diseases[1] = "BLCA";
        diseases[2] = "BRCA";
        diseases[3] = "CESC";
        diseases[4] = "CHOL";
        diseases[5] = "COAD";
        diseases[6] = "DLBC";
        diseases[7] = "ESCA";
        diseases[8] = "GBM";
        diseases[9] = "HNSC";
        diseases[10] = "KICH";
        diseases[11] = "KIRC";
        diseases[12] = "KIRP";
        diseases[13] = "LAML";
        diseases[14] = "LGG";
        diseases[15] = "LIHC";
        diseases[16] = "LUAD";
        diseases[17] = "LUSC";
        diseases[18] = "MESO";
        diseases[19] = "OV";
        diseases[20] = "PAAD";
        diseases[21] = "PCPG";
        diseases[22] = "PRAD";
        diseases[23] = "READ";
        diseases[24] = "SARC";
        diseases[25] = "SKCM";
        diseases[26] = "STAD";
        diseases[27] = "TGCT";
        diseases[28] = "THCA";
        diseases[29] = "THYM";
        diseases[30] = "UCEC";
        diseases[31] = "UCS";
        diseases[32] = "UVM";

        ArrayList<String> data_types = new ArrayList<String>();
        //data_types.add("DNAMethylation");
        //data_types.add("DNASeq");
        data_types.add("RNASeq");
        data_types.add("RNASeqV2");
        //data_types.add("miRNASeq");
        //data_types.add("CNV");

        ArrayList<String> st_tmp = new ArrayList<String>();
        st_tmp.add("gene");
        /*st_tmp.add("exon");
        st_tmp.add("spljxn");
        st_tmp.add("mirna");*/
        st_tmp.add("isoform");
        
        for (String disease: diseases) {
            for (String data_type: data_types) {
                for (String subtype: st_tmp) {
                    try {
                        File[] bed_files = new File("E:/ftp-root/bed/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/"+subtype.toLowerCase()+".quantification/").listFiles();
                        for (File old_bed_file: bed_files) {
                            if (old_bed_file.getName().toLowerCase().endsWith(".bed") || old_bed_file.getName().toLowerCase().equals("annotations.tsv")) {
                                System.err.println(old_bed_file.getAbsolutePath());
                                try {
                                    String new_bed_content = "";
                                    InputStream fstream = new FileInputStream(old_bed_file.getAbsolutePath());
                                    DataInputStream in = new DataInputStream(fstream);
                                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                                    String line;
                                    while ((line = br.readLine()) != null) {
                                        try {
                                            String[] line_split = line.split("\t");
                                            int start = Integer.valueOf(line_split[1]);
                                            start++;
                                            int end = Integer.valueOf(line_split[2]);
                                            end++;
                                            String new_line = "";
                                            for (int i=0; i<line_split.length-1; i++) {
                                                if (i == 1)
                                                    new_line = new_line + start + "\t";
                                                else if (i == 2)
                                                    new_line = new_line + end + "\t";
                                                else
                                                    new_line = new_line + line_split[i] + "\t";
                                            }
                                            new_line = new_line + line_split[line_split.length-1];
                                            new_bed_content = new_bed_content + new_line + "\n";
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    br.close();
                                    in.close();
                                    fstream.close();

                                    new_bed_content = new_bed_content.trim();
                                    String isoform_file_name = old_bed_file.getName();
                                    old_bed_file.delete();

                                    File new_bed_file = new File("E:/ftp-root/bed/"+disease.toLowerCase()+"/"+data_type.toLowerCase()+"/"+subtype.toLowerCase()+".quantification/"+isoform_file_name);
                                    new_bed_file.createNewFile();
                                    FileOutputStream fos = new FileOutputStream(new_bed_file.getAbsolutePath());
                                    PrintStream out = new PrintStream(fos);
                                    out.print(new_bed_content);
                                    out.close();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // NCBI
                                // read bed file and store it in a data structure
                                // change start position -> start + 1 ; end position -> end + 1
                                // delete old bed file
                                // write new bed file
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
}
