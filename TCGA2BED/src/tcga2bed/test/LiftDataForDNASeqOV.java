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
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author fabio
 */
public class LiftDataForDNASeqOV {
    
    /* http://genome.ucsc.edu/cgi-bin/hgLiftOver */
    
    private static final String originalGRCh36_coordinates_path = "/Users/fabio/NetBeansProjects/TCGA2BED/package/appdata/GRCh36ToGRCh37_archive/original_GRCh36.txt";
    private static final String convertedGRCh37_coordinates_path = "/Users/fabio/NetBeansProjects/TCGA2BED/package/appdata/GRCh36ToGRCh37_archive/converted_GRCh37.txt";
    private static final String not_converted_coordinates_path = "/Users/fabio/NetBeansProjects/TCGA2BED/package/appdata/GRCh36ToGRCh37_archive/not_converted.txt";
    
    public static void main(String[] args) {
        ArrayList<String> original_coordinates = readList(originalGRCh36_coordinates_path);
        System.err.println("original_coordinates: "+original_coordinates.size());
        ArrayList<String> converted_coordinates = readList(convertedGRCh37_coordinates_path);
        System.err.println("converted_coordinates: "+converted_coordinates.size());
        ArrayList<String> not_converted_coordinates = readList(not_converted_coordinates_path);
        System.err.println("not_converted_coordinates: "+not_converted_coordinates.size());
        System.err.println();
        
        HashMap<String, String> coordinates_map = new HashMap<>();
        int conv_index = 0;
        for (int orig_index = 0; orig_index<original_coordinates.size(); orig_index++) {
            if (not_converted_coordinates.contains(original_coordinates.get(orig_index)))
                coordinates_map.put(original_coordinates.get(orig_index), "NA");
            else {
                coordinates_map.put(original_coordinates.get(orig_index), converted_coordinates.get(conv_index));
                conv_index++;
            }
        }
        System.err.println("coordinates_map: "+coordinates_map.size());
        System.err.println();
        
        for (String c: coordinates_map.keySet())
            System.err.println(c+"\t"+coordinates_map.get(c));
    }
    
    private static ArrayList<String> readList(String path) {
        ArrayList<String> data = new ArrayList<>();
        try {
            InputStream fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    data.add(line.trim());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    
}
