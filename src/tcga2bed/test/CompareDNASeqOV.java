/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcga2bed.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author fabio
 */
public class CompareDNASeqOV {
    
    private static final String dnaseq_root = "E:/ftp-root/tcga_original/ov/dnaseq/";
    
    public static void main(String[] args) {
        HashMap<String, HashSet<String>> maf2aliquots = new HashMap<>();
        String[] files_path = (new File(dnaseq_root)).list();
        for (String p: files_path) {
            if (p.toLowerCase().trim().endsWith("maf")) {
                maf2aliquots.put((new File(dnaseq_root+p)).getName(), extractAliquots(dnaseq_root+p));
            }
        }
        
        // it work with two MAFs only
        HashSet<String> aliquot_intersection = new HashSet<>();
        for (String maf1: maf2aliquots.keySet()) {
            HashSet<String> aliquots1 = maf2aliquots.get(maf1);
            System.err.println(maf1+": "+aliquots1.size());
            for (String maf2: maf2aliquots.keySet()) {
                if (!maf1.equals(maf2)) {
                    HashSet<String> aliquots2 = maf2aliquots.get(maf2);
                    System.err.println(maf2+": "+aliquots2.size());
                    for (String a1: aliquots1) {
                        if (aliquots2.contains(a1))
                            aliquot_intersection.add(a1);
                    }
                    break;
                }
            }
            break;
        }
        
        /*if (aliquot_intersection.isEmpty())
            System.err.println("no common aliquots");
        else {
            for (String aliquot: aliquot_intersection)
                System.err.println(aliquot);
        }*/
        
        System.err.println(aliquot_intersection.size());
    }
    
    private static HashSet<String> extractAliquots(String maf_path) {
        int aliquot_index = 15;
        HashSet<String> aliquots = new HashSet<>();
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
                        String aliquot_field = line_split[aliquot_index];
                        //System.err.println(aliquot_field);
                        if (startReading)
                            aliquots.add(aliquot_field);
                        if (aliquot_field.trim().toLowerCase().equals("tumor_sample_barcode"))
                            startReading = true;
                    }
                    catch (ArrayIndexOutOfBoundsException e) {}
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return aliquots;
    }
    
}
