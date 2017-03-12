/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import tcga2bed.Settings;

import tcga2bed.resources.RetrieveNCBIGenomicCoordinates;
import tcga2bed.util.FileInputParser;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MD5Checksum;
import tcga2bed.util.OutputFormat;
import tcga2bed.util.QueryParser;

public class RNASeqParser extends BioParser {

    @Override
    //public HashMap<String, String> getGendataEntries(File data_dir, HashMap<String, Integer> patient2code, String data_type, String data_subtype) {
    public HashSet<String> convert(File data_dir, File meta_dir, String disease, String data_type, String data_subtype, String out_dir_path, String out_fileName_prefix, String gendata_ext, String metadata_ext) {
        HashSet<String> patientIds = new HashSet<>();

        HashMap<String, HashMap<String, String>> entrez2GenomeCoordinates = null;
        HashSet<String> unresolved = null;
        if (data_subtype.toUpperCase().equals("GENE")) {
            entrez2GenomeCoordinates = RetrieveNCBIGenomicCoordinates.loadTextDB_resolved();
            unresolved = RetrieveNCBIGenomicCoordinates.loadTextDB_unresolved();
        }

        try {
            HashMap<String, HashMap<String, String>> parser_meta = new HashMap<>();
            HashMap<String, HashSet<String>> parser_meta_dict = new HashMap<>();

            HTTPExpInfo.initDiseaseInfo();
            /*File tmp = File.createTempFile("http_tcga", "html");
            String out_path = tmp.getAbsolutePath();
            String url = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_root_dir");

            HashMap<String, String> folder2date = HTTPExpInfo.getData2Date(url, out_path, disease, data_type, "/", "_data_dir_prefix");
            tmp.delete();
            String updatedFolder = HTTPExpInfo.searchForUpdate(folder2date);*/
            //System.err.println(updatedFolder);

            for (File f : data_dir.listFiles()) {
                if (!isToSkip(f.getName())) {
                    boolean right_file = false;
                    HashMap<String, String[]> data = FileInputParser.retrieveData(f, data_type);

                    String patientRef = FileInputParser.retrievePatientReference(f, data_type);
                    if (data_type.toLowerCase().equals("rnaseqv2")) {
                        patientRef = QueryParser.getTCGABarcodeFromUUID(patientRef, meta_dir.getAbsolutePath());
                    }

                    String fileName = f.getName();
                    File output = (new File(out_dir_path + patientRef + gendata_ext));
                    if (data_subtype.toUpperCase().equals("EXON") && (fileName.toUpperCase().contains("EXON.QUANTIFICATION") || fileName.toUpperCase().contains("EXON_QUANTIFICATION"))) {
                        printRegionInfo(data, patientRef, 0, entrez2GenomeCoordinates, output, unresolved, data_type, data_subtype);
                        right_file = true;
                    } else if (data_subtype.toUpperCase().equals("GENE") && fileName.toUpperCase().contains("GENE.QUANTIFICATION")) {
                        printRegionInfo(data, patientRef, 2, entrez2GenomeCoordinates, output, unresolved, data_type, data_subtype);
                        right_file = true;
                    } else if (data_subtype.toUpperCase().equals("SPLJXN") && (fileName.toUpperCase().contains("SPLJXN.QUANTIFICATION") || fileName.toUpperCase().contains("JUNCTION_QUANTIFICATION"))) {
                        printRegionInfo(data, patientRef, 1, entrez2GenomeCoordinates, output, unresolved, data_type, data_subtype);
                        right_file = true;
                    }

                    if (right_file) {
                        String md5sum = MD5Checksum.getMD5Checksum(output.getAbsolutePath());

                        //update metadata
                        HashMap<String, String> metaVals = new HashMap<>();
                        if (parser_meta.containsKey(patientRef.replaceAll("_", "-"))) {
                            metaVals = parser_meta.get(patientRef.replaceAll("_", "-"));
                        }
                        metaVals.put("manually_curated|id", patientRef.replaceAll("_", "-").toLowerCase());
                        metaVals.put("manually_curated|dataType", data_type.toLowerCase());
                        //metaVals.put("manually_curated|meta_biotab_url", meta_biotab_url.toLowerCase());
                        metaVals.put("manually_curated|tumor_tag", disease.toLowerCase());
                        metaVals.put("manually_curated|tumor_description", getAbbreviation2Description().get(disease.toUpperCase()).toLowerCase());
                        metaVals.put("manually_curated|rna_seq_exp_type", data_subtype.toLowerCase() + "_quantification".toLowerCase());
                        if (data_type.toLowerCase().equals("rnaseq")) {
                            metaVals.put("manually_curated|rna_seq_data_unit", "RPKM".toLowerCase());
                        } else if (data_type.toLowerCase().equals("rnaseqv2")) {
                            metaVals.put("manually_curated|rna_seq_data_unit", "RSEM".toLowerCase());
                        }
                        metaVals.put("manually_curated|tissue_status", getTissueStatus(patientRef).toLowerCase());
                        //metaVals.put("manually_curated|exp_data_url", url + updatedFolder + f.getCanonicalFile().getName());
                        metaVals.put("manually_curated|seqPlatform", HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_platform").toLowerCase());
                        metaVals.put("manually_curated|exp_data_bed_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + patientRef + gendata_ext);
                        metaVals.put("manually_curated|exp_metadata_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + patientRef + metadata_ext);
                        metaVals.put("manually_curated|md5sum", md5sum);
                        parser_meta.put(patientRef.replaceAll("_", "-"), metaVals);

                        //update metadata dictionary
                        HashSet<String> metaDictVals = new HashSet<>();
                        
                        if (parser_meta_dict.containsKey("manually_curated|md5sum")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|md5sum");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(md5sum);
                        parser_meta_dict.put("manually_curated|md5sum", metaDictVals);
                        
                        if (parser_meta_dict.containsKey("manually_curated|id")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|id");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(patientRef.replaceAll("_", "-").toLowerCase());
                        parser_meta_dict.put("manually_curated|id", metaDictVals);

                        if (parser_meta_dict.containsKey("manually_curated|dataType")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|dataType");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(data_type.toLowerCase());
                        parser_meta_dict.put("manually_curated|dataType", metaDictVals);

                        /*if (parser_meta_dict.containsKey("manually_curated|meta_biotab_url")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|meta_biotab_url");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(meta_biotab_url.toLowerCase());
                        parser_meta_dict.put("manually_curated|meta_biotab_url", metaDictVals);*/

                        if (parser_meta_dict.containsKey("manually_curated|tumor_tag")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|tumor_tag");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(disease.toLowerCase());
                        parser_meta_dict.put("manually_curated|tumor_tag", metaDictVals);

                        if (parser_meta_dict.containsKey("manually_curated|tumor_description")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|tumor_description");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(getAbbreviation2Description().get(disease.toUpperCase()).toLowerCase());
                        parser_meta_dict.put("manually_curated|tumor_description", metaDictVals);

                        if (parser_meta_dict.containsKey("manually_curated|rna_seq_exp_type")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|rna_seq_exp_type");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(data_subtype.toLowerCase() + "_quantification".toLowerCase());
                        parser_meta_dict.put("manually_curated|rna_seq_exp_type", metaDictVals);

                        if (parser_meta_dict.containsKey("manually_curated|rna_seq_data_unit")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|rna_seq_data_unit");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add("RPKM".toLowerCase());
                        parser_meta_dict.put("manually_curated|rna_seq_data_unit", metaDictVals);

                        if (parser_meta_dict.containsKey("manually_curated|tissue_status")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|tissue_status");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(getTissueStatus(patientRef).toLowerCase());
                        parser_meta_dict.put("manually_curated|tissue_status", metaDictVals);

                        /*if (parser_meta_dict.containsKey("manually_curated|exp_data_url")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|exp_data_url");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(url + updatedFolder + f.getCanonicalFile().getName());
                        parser_meta_dict.put("manually_curated|exp_data_url", metaDictVals);*/

                        if (parser_meta_dict.containsKey("manually_curated|seqPlatform")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|seqPlatform");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_platform").toLowerCase());
                        parser_meta_dict.put("manually_curated|seqPlatform", metaDictVals);

                        if (parser_meta_dict.containsKey("manually_curated|exp_data_bed_url")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|exp_data_bed_url");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + patientRef + gendata_ext);
                        parser_meta_dict.put("manually_curated|exp_data_bed_url", metaDictVals);
                        if (parser_meta_dict.containsKey("manually_curated|exp_metadata_url")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|exp_metadata_url");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + patientRef + metadata_ext);
                        parser_meta_dict.put("manually_curated|exp_metadata_url", metaDictVals);
                        
                        if (!patientIds.contains(patientRef)) {
                            patientIds.add(patientRef);
                        }
                    }

                }

            }
            setMetadata(parser_meta);
            setMetadataDictionary(parser_meta_dict);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return patientIds;
    }

    /*
     * dataType_code : {0=exon ; 1=spljxn ; 2=gene}
     */
    private void printRegionInfo(HashMap<String, String[]> data, String patientRef, int dataType_code, HashMap<String, HashMap<String, String>> entrez2GenomeCoordinates, File output, HashSet<String> unresolved, String data_type, String data_subtype) {
        try {
            FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
            PrintStream out = new PrintStream(fos);
            
            out.printf(OutputFormat.initDocument(getFormat()));

            int regionsInfoIndex = 0;

            for (String k : data.keySet()) {
                try {
                    if (!k.equals("header")) {
                        String chr = "", strand = "";
                        int start = 0, end = 0;
                        String[] d = data.get(k);

                        skipIndexList = new ArrayList<>();
                        if (dataType_code == 0) {
                            skipIndexList.add(regionsInfoIndex);
                        }

                        // EXON
                        if (dataType_code == 0) {
                            String[] regionInfo = d[regionsInfoIndex].split(":");
                            chr = regionInfo[0].replaceAll("chr", "");
                            String[] offset = regionInfo[1].split("-");
                            start = Integer.valueOf(offset[0]);
                            end = Integer.valueOf(offset[1]);
                            strand = regionInfo[2];
                        } // SPLJXN
                        else if (dataType_code == 1) {
                            String[] regionInfo = d[regionsInfoIndex].split(",");
                            String chr_0 = regionInfo[0];
                            String chr_1 = regionInfo[1];
                            String[] chr_0_split = chr_0.split(":");
                            String[] chr_1_split = chr_1.split(":");
                            chr = chr_0_split[0].replaceAll("chr", "");
                            start = Integer.valueOf(chr_0_split[1]);
                            end = Integer.valueOf(chr_1_split[1]);
                            String strand0 = chr_0_split[2];
                            String strand1 = chr_1_split[2];
                            if (!strand0.equals(strand1)) {
                                strand = "*";
                            } else {
                                strand = strand0;
                            }
                        } // GENE
                        else if (dataType_code == 2) {
                            String[] geneInfo = d[0].split("\\|");
                            if (geneInfo.length == 2) {
                                String entrez = (d[0].split("\\|")[1]).split("_")[0];
                                String gene = d[0].split("\\|")[0];
                                if (entrez.equals("?") && !unresolved.contains(d[0])) {
                                    RetrieveNCBIGenomicCoordinates.updateUnresolvedQueries(d[0]);
                                    unresolved = RetrieveNCBIGenomicCoordinates.loadTextDB_unresolved();
                                } else {
                                    if (!RetrieveNCBIGenomicCoordinates.getNcbiArchive().equals("null")) {
                                        if (!entrez2GenomeCoordinates.containsKey(entrez)) {
                                            if (!unresolved.contains(d[0])) {
                                                if (RetrieveNCBIGenomicCoordinates.updateLocalGenomeCoordinatesDB(entrez, gene)) {
                                                    entrez2GenomeCoordinates = RetrieveNCBIGenomicCoordinates.loadTextDB_resolved();
                                                    chr = entrez2GenomeCoordinates.get(entrez).get("CHR");
                                                    start = Integer.valueOf(entrez2GenomeCoordinates.get(entrez).get("START"));
                                                    end = Integer.valueOf(entrez2GenomeCoordinates.get(entrez).get("END"));
                                                    strand = entrez2GenomeCoordinates.get(entrez).get("STRAND");
                                                } else {
                                                    RetrieveNCBIGenomicCoordinates.updateUnresolvedQueries(d[0]);
                                                    unresolved = RetrieveNCBIGenomicCoordinates.loadTextDB_unresolved();
                                                }
                                            }
                                        } else {
                                            chr = entrez2GenomeCoordinates.get(entrez).get("CHR");
                                            start = Integer.valueOf(entrez2GenomeCoordinates.get(entrez).get("START"));
                                            end = Integer.valueOf(entrez2GenomeCoordinates.get(entrez).get("END"));
                                            strand = entrez2GenomeCoordinates.get(entrez).get("STRAND");
                                        }
                                    } else {
                                        HashMap<String, String> entrezCoords = RetrieveNCBIGenomicCoordinates.simpleRetrieveGenomicCoordinates(entrez, gene);
                                        if (!entrezCoords.isEmpty()) {
                                            if (!entrezCoords.get("CHR").equals("") && (Integer.valueOf(entrezCoords.get("START")) > 0 && Integer.valueOf(entrezCoords.get("END")) > 0)) {
                                                chr = entrezCoords.get("CHR");
                                                start = Integer.valueOf(entrezCoords.get("START"));
                                                end = Integer.valueOf(entrezCoords.get("END"));
                                                strand = entrezCoords.get("STRAND");
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (!RetrieveNCBIGenomicCoordinates.getNcbiArchive().equals("null")) {
                                    RetrieveNCBIGenomicCoordinates.updateUnresolvedQueries(d[0]);
                                }
                            }
                        }
                        if (!chr.equals("")) {
                            if (start > end) {
                                int tmp = start;
                                start = end;
                                end = tmp;
                            }

                            ArrayList<String> tmpArr = new ArrayList<>();
                            tmpArr.add("chr"+chr); tmpArr.add(String.valueOf(start)); tmpArr.add(String.valueOf(end)); tmpArr.add(strand);
                            //String tmp = "chr" + chr + "\t" + start + "\t" + end + "\t" + strand + "\t";
                            String annotation = "chr" + chr + "\t" + start + "\t" + end + "\t" + strand + "\t";

                            // EXON
                            if (dataType_code == 0) {
                                String raw_counts = d[1];
                                if (!skipIndexList.contains(1)) {
                                    if (raw_counts.trim().equals("") || raw_counts.trim().equals("NA")) {
                                        //tmp += "null\t";
                                        tmpArr.add("null");
                                    } else {
                                        //tmp += raw_counts + "\t";
                                        tmpArr.add(raw_counts);
                                    }
                                }
                                String median_length_normalized = d[2];
                                if (!skipIndexList.contains(2)) {
                                    if (median_length_normalized.trim().equals("") || median_length_normalized.trim().equals("NA")) {
                                        //tmp += "null\t";
                                        tmpArr.add("null");
                                    } else {
                                        //tmp += median_length_normalized + "\t";
                                        tmpArr.add(median_length_normalized);
                                    }
                                }
                                String RPKM = d[3];
                                if (!skipIndexList.contains(3)) {
                                    if (RPKM.trim().equals("") || RPKM.trim().equals("NA")) {
                                        //tmp += "null";
                                        tmpArr.add("null");
                                    } else {
                                        //tmp += RPKM;
                                        tmpArr.add(RPKM);
                                    }
                                }
                            } // SPLJXN
                            else if (dataType_code == 1) {
                                String raw_counts = d[1];
                                if (!skipIndexList.contains(1)) {
                                    if (raw_counts.trim().equals("") || raw_counts.trim().equals("NA")) {
                                        //tmp += "null\t";
                                        tmpArr.add("null");
                                    } else {
                                        //tmp += raw_counts + "\t";
                                        tmpArr.add(raw_counts);
                                    }
                                }
                                //tmp += (start + 1) + "\t" + (end - 1);
                                tmpArr.add(String.valueOf(start+1)); tmpArr.add(String.valueOf(end+1));
                            } // GENE
                            else if (dataType_code == 2) {
                                String tcga_entrez = d[0].split("\\|")[1].split("_")[0];

                                if (!skipIndexList.contains(0)) {
                                    if (d[0].trim().equals("") || d[0].trim().equals("NA")) {
                                        //tmp += "null" + "\t" + "null" + "\t";
                                        tmpArr.add("null"); tmpArr.add("null");
                                        annotation += "null";
                                    } else {
                                        //tmp += entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_SYMBOL") + "\t" + entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_ENTREZ") + "\t";
                                        tmpArr.add(entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_SYMBOL")); tmpArr.add(entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_ENTREZ"));
                                        annotation += entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_SYMBOL") + "\t" + entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_ENTREZ");
                                    }
                                }
                                String raw_counts = d[1];
                                if (!skipIndexList.contains(1)) {
                                    if (raw_counts.trim().equals("") || raw_counts.trim().equals("NA")) {
                                        //tmp += "null\t";
                                        tmpArr.add("null");
                                    } else {
                                        //tmp += raw_counts + "\t";
                                        tmpArr.add(raw_counts);
                                    }
                                }
                                String median_length_normalized = d[2];
                                if (!skipIndexList.contains(2)) {
                                    if (median_length_normalized.trim().equals("") || median_length_normalized.trim().equals("NA")) {
                                        //tmp += "null\t";
                                        tmpArr.add("null");
                                    } else {
                                        //tmp += median_length_normalized + "\t";
                                        tmpArr.add(median_length_normalized);
                                    }
                                }
                                String RPKM = d[3];
                                if (!skipIndexList.contains(3)) {
                                    if (RPKM.trim().equals("") || RPKM.trim().equals("NA")) {
                                        //tmp += "null";
                                        tmpArr.add("null");
                                    } else {
                                        //tmp += RPKM;
                                        tmpArr.add(RPKM);
                                    }
                                }
                            }

                            //if (!tmp.trim().equals("")) {
                            if (!tmpArr.isEmpty()) {
                                incrementAliquotCount(patientRef);
                                incrementSampleCount(patientRef);
                                incrementPatientCount(patientRef);
                                addAnnotation(annotation);
                                //out.println(tmp);
                                String tmp = OutputFormat.createOutLine(getFormat(), tmpArr, getHeader(data_type, data_subtype));
                                out.print(tmp);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            out.printf(OutputFormat.endDocument(getFormat()));
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getHeader(String data_type, String data_subtype) {
        String[] header;
        if (data_subtype.toLowerCase().equals("gene")) {
            header = new String[9];
            header[0] = "chr";
            header[1] = "start";
            header[2] = "stop";
            header[3] = "strand";
            header[4] = "gene_symbol";
            header[5] = "entrez_gene_id";
            header[6] = "raw_counts";
            header[7] = "median_length_normalized";
            if (data_type.toLowerCase().equals("rnaseq")) {
                header[8] = "rpkm";
            } else if (data_type.toLowerCase().equals("rnaseqv2")) {
                header[8] = "rsem";
            }
            return header;
        } else if (data_subtype.toLowerCase().equals("exon")) {
            header = new String[7];
            header[0] = "chr";
            header[1] = "start";
            header[2] = "stop";
            header[3] = "strand";
            header[4] = "raw_counts";
            header[5] = "median_length_normalized";
            header[6] = "rpkm";
            return header;
        } else if (data_subtype.toLowerCase().equals("spljxn")) {
            header = new String[7];
            header[0] = "chr";
            header[1] = "start";
            header[2] = "stop";
            header[3] = "strand";
            header[4] = "raw_counts";
            header[5] = "inner_left";
            header[6] = "inner_right";
            return header;
        }
        return null;
    }

    @Override
    public String[] getAttributesType(String data_type, String data_subtype) {
        String[] attr_type;
        if (data_subtype.toLowerCase().equals("gene")) {
            attr_type = new String[9];
            attr_type[0] = "STRING";
            attr_type[1] = "LONG";
            attr_type[2] = "LONG";
            attr_type[3] = "CHAR";
            attr_type[4] = "STRING";
            attr_type[5] = "STRING";
            attr_type[6] = "INTEGER";
            attr_type[7] = "FLOAT";
            attr_type[8] = "FLOAT";
            return attr_type;
        } else if (data_subtype.toLowerCase().equals("exon")) {
            attr_type = new String[7];
            attr_type[0] = "STRING";
            attr_type[1] = "LONG";
            attr_type[2] = "LONG";
            attr_type[3] = "CHAR";
            attr_type[4] = "INTEGER";
            attr_type[5] = "FLOAT";
            attr_type[6] = "FLOAT";
            return attr_type;
        } else if (data_subtype.toLowerCase().equals("spljxn")) {
            attr_type = new String[7];
            attr_type[0] = "STRING";
            attr_type[1] = "LONG";
            attr_type[2] = "LONG";
            attr_type[3] = "CHAR";
            attr_type[4] = "INTEGER";
            attr_type[5] = "INTEGER";
            attr_type[6] = "INTEGER";
            return attr_type;
        }
        return null;
    }

}
