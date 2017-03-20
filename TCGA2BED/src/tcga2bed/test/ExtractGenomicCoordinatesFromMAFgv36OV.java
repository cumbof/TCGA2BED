/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcga2bed.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 *
 * @author fabio
 */
public class ExtractGenomicCoordinatesFromMAFgv36OV {
    
    private static final String maf_path = "E:/ftp-root/tcga_original/ov/dnaseq/genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.2.0.0.somatic.maf";
    
    public static void main(String[] args) {
        int chr_index = 4;
        int start_index = 5;
        int end_index = 6;
        HashSet<String> coordinates = new HashSet<>();
        try {
            InputStream fstream = new FileInputStream(maf_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            boolean startReading = false;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] line_split = line.trim().split("\t");
                    try {
                        String chr = line_split[chr_index];
                        String start = line_split[start_index];
                        String end = line_split[end_index];
                        if (startReading)
                            coordinates.add("chr"+chr+":"+start+"-"+end);
                        if (line_split[chr_index].trim().toLowerCase().equals("chromosome"))
                            startReading = true;
                    }
                    catch (ArrayIndexOutOfBoundsException e) {}
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        for (String c: coordinates)
            System.err.println(c);
    }
    
}
