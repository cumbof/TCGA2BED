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
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import tcga2bed.Main;

public class DataExtractionTool {

    public static boolean uncompressTarGz(File tarFile, File dest) {
        try {
            dest.mkdir();
            TarArchiveInputStream tarIn = null;

            if (tarFile.getName().toLowerCase().endsWith(".tar")) {
                tarIn = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile)));
            } else if (tarFile.getName().toLowerCase().endsWith(".tar.gz")) {
                tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(tarFile))));
            }

            TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
            // tarIn is a TarArchiveInputStream
            while (tarEntry != null) {// create a file with the same name as the tarEntry
                File destPath = new File(dest, tarEntry.getName());
                System.out.println("working: " + destPath.getCanonicalPath());
                if (tarEntry.isDirectory()) {
                    destPath.mkdirs();
                } else {

                    destPath.getParentFile().mkdirs();

                    destPath.createNewFile();
                    //byte [] btoRead = new byte[(int)tarEntry.getSize()];
                    byte[] btoRead = new byte[1024];
		            //FileInputStream fin 
                    //  = new FileInputStream(destPath.getCanonicalPath());
                    BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
                    int len = 0;

                    while ((len = tarIn.read(btoRead)) != -1) {
                        bout.write(btoRead, 0, len);
                    }

                    bout.close();
                    btoRead = null;

                }
                tarEntry = tarIn.getNextTarEntry();
            }
            tarIn.close();

            return (dest.exists() && dest.isDirectory());
        } catch (IOException e) {
            Main.printException(e, true);
            return false;
        }
    }

}
