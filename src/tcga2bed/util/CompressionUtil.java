/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class CompressionUtil {
    
    final static int BUFFER = 2048;

    /**
     * Command line arguments :
     * argv[0]-----> Source tar.gz file.
     * argv[1]-----> DestarInation directory.
     **/
    /*public static void main(String[] args) {
        String archiveFolderPath = "D:/downloads/";
        File[] files = (new File(archiveFolderPath)).listFiles();
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseaseMap = HTTPExpInfo.getDiseaseInfo();
        for (String disease: diseaseMap.keySet()) {
            ArrayList<String> dt_done = new ArrayList<String>();
            for (String dataTypeExt: diseaseMap.get(disease).keySet()) {
                if (!dataTypeExt.toLowerCase().contains("disease")) {
                    String dataType = dataTypeExt.split("_")[0];
                    if (!dt_done.contains(dataTypeExt.split("_")[0])) {
                       String outputFolderPath = "E:/ftp-root/tcga_original/"+disease.toLowerCase()+"/"+dataType+"/";
                       System.err.println(disease + " - " + dataType);
                       (new File(outputFolderPath)).mkdirs();
                       dataType = diseaseMap.get(disease).get(dataType+"_data_dir_prefix");
                       for (File f: files) {
                          if (f.getName().toLowerCase().contains(dataType.toLowerCase()))
                              decompress(f.getAbsolutePath(), outputFolderPath);
                       }
                       dt_done.add(dataTypeExt.split("_")[0]);
                    }
                }
            }
        }
    }*/
    
    /*public static void main(String[] args) {
        String baseDir = "G:/ftp-root/tcga_original/";
        HTTPExpInfo.initDiseaseInfo();
        HashMap<String, HashMap<String, String>> diseases = HTTPExpInfo.getDiseaseInfo();
        for (String disease: diseases.keySet()) {
            HashMap<String, String> diseaseInfo = diseases.get(disease);
            HashSet<String> dataTypes = new HashSet<String>();
            for (String dt: diseaseInfo.keySet()) {
                String[] dt_split = dt.split("_");
                if (dt_split.length > 1)
                    dataTypes.add(dt_split[0]);
            }
            if (!dataTypes.isEmpty()) {
                for (String dataType: dataTypes) {
                    System.err.println(disease + "\t" + dataType);
                    if (!dataType.equals("meta")) {
                        String data_dir_prefix = diseases.get(disease).get(dataType + "_data_dir_prefix");
                        String data_dir = baseDir+disease+"/"+dataType+"/";
                        compress(data_dir, data_dir+data_dir_prefix+".tar.gz", true);
                        if (dataType.equals("cnv")) {
                            String magetab_dir_prefix = diseases.get(disease).get(dataType + "_magetab_dir_prefix");
                            String magetab_dir = baseDir+disease+"/"+dataType+"/mage-tab/";
                            compress(magetab_dir, data_dir+magetab_dir_prefix+".tar.gz", true);
                        }
                    }
                    else {
                        String meta_dir_prefix = disease.toUpperCase()+"_metadata";
                        String meta_dir = baseDir+disease+"/meta/";
                        compress(meta_dir, meta_dir+meta_dir_prefix+".tar.gz", false);
                    }
                }
            }
        }
    }*/
    
    // compress files only, excludes folders
    public static boolean compress(String dirPath, String tarGzAbsolutePath, boolean excludeFolders) {
        try {
            File folder = new File(dirPath);
            if (folder.exists()) {
                File[] listFiles = folder.listFiles();
                FileOutputStream fOut = new FileOutputStream(new File(tarGzAbsolutePath));
                BufferedOutputStream bOut = new BufferedOutputStream(fOut);
                GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bOut);
                TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut);
                
                for (File f: listFiles)
                    addFileToTarGz(tOut, f.getAbsolutePath(), "", excludeFolders);
                
                tOut.finish();
                tOut.close();
                gzOut.close();
                bOut.close();
                fOut.close();
                
                return true;
            }
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base, boolean excludeFolders) {
        try {
            File f = new File(path);
            String entryName = base + f.getName();

            if (f.isFile()) {
                TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
                tOut.putArchiveEntry(tarEntry);
                IOUtils.copy(new FileInputStream(f), tOut);
                tOut.closeArchiveEntry();
            } else {
                if (!excludeFolders) {
                    TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
                    tOut.putArchiveEntry(tarEntry);
                    tOut.closeArchiveEntry();
                    File[] children = f.listFiles();
                    if (children != null) {
                        for (File child : children) {
                            //System.out.println(child.getName());
                            addFileToTarGz(tOut, child.getAbsolutePath(), entryName + "/", excludeFolders);
                        }
                    }
                }
            }
        } catch (Exception e) {}
    }
    
    public static boolean decompress(String tarGzArchivePath, String outputFolderPath) {
        try {
            /** create a TarArchiveInputStream object. **/

            FileInputStream fin = new FileInputStream(tarGzArchivePath);
            BufferedInputStream in = new BufferedInputStream(fin);
            GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
            TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn);

            TarArchiveEntry entry = null;
            /** Read the tar entries using the getNextEntry method **/
            while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
                String[] entrySplit = entry.getName().split("/");
                //System.out.println("Extracting: " + entrySplit[entrySplit.length-1]);
                /** If the entry is a directory, create the directory. **/
                if (entry.isDirectory()) {
                    //File f = new File(outputFolderPath + entry.getName());
                    //f.mkdirs();
                }
                /**
                 * If the entry is a file,write the decompressed file to the disk
                 * and close destination stream.
                 **/
                else {
                    int count;
                    byte data[] = new byte[BUFFER];

                    FileOutputStream fos = new FileOutputStream(outputFolderPath + entrySplit[entrySplit.length-1]);
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = tarIn.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.close();
                }
           }
           /** Close the input stream **/
           tarIn.close();
           gzIn.close();
           in.close();
           fin.close();
           System.err.println("\t"+tarGzArchivePath + " : untar completed successfully!!");
           return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
 
}
