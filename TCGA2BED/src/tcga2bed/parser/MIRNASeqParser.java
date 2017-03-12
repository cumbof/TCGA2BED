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
import tcga2bed.resources.RetrieveMIRBASEGenomicCoordinates;
import tcga2bed.resources.RetrieveMirnaFromHGNC;
import tcga2bed.util.FileInputParser;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MD5Checksum;
import tcga2bed.util.OutputFormat;

/**
 *
 * @author Fabio
 */
public class MIRNASeqParser extends BioParser {

    @Override
    public HashSet<String> convert(File data_dir, File meta_dir, String disease, String data_type, String data_subtype, String out_dir_path, String out_fileName_prefix, String gendata_ext, String metadata_ext) {
        HashSet<String> patientIds = new HashSet<>();
        try {
            HashMap<String, HashMap<String, String>> mirna2GenomeCoordinates = new HashMap<String, HashMap<String, String>>();
            if (!RetrieveMIRBASEGenomicCoordinates.getMirbaseArchive().equals("null"))
                mirna2GenomeCoordinates = RetrieveMIRBASEGenomicCoordinates.loadTextDB();
            
            HashMap<String, String> mirna2hgncid = null;
            HashMap<String, String> mirna2symbol = null;
            HashMap<String, String> mirna2entrez = null;
            HashSet<String> mirnas = null;
            if (!RetrieveMirnaFromHGNC.getHgncArchive().equals("null")) {
                RetrieveMirnaFromHGNC.loadMirnaTableFromHgnc();
                mirna2hgncid = RetrieveMirnaFromHGNC.getMirna2Hgnc();
                mirna2symbol = RetrieveMirnaFromHGNC.getMirna2symbol();
                mirna2entrez = RetrieveMirnaFromHGNC.getResolved();
                mirnas = RetrieveMirnaFromHGNC.getUnresolved();
            }
            
            int mirnaid_index = 0;
            
            int isoform_coords_index = 1;
            int isoform_readcount_index = 2;
            int isoform_reads_per_million_miRNA_mapped_index = 3;
            int isoform_cross_mapped_index = 4;
            int isoform_mirnaregion_index = 5;
            
            int mirna_readcount_index = 1;
            int mirna_reads_per_million_miRNA_mapped_index = 2;
            int mirna_cross_mapped_index = 3;
            
            HTTPExpInfo.initDiseaseInfo();
            /*File tmp_file = File.createTempFile("http_tcga", "html");
            String out_path = tmp_file.getAbsolutePath();
            String url = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_root_dir");*/

            skipIndexList = new ArrayList<>();
            
            /*HashMap<String, String> folder2date = HTTPExpInfo.getData2Date(url, out_path, disease, data_type, "/", "_data_dir_prefix");
            tmp_file.delete();
            String updatedFolder = HTTPExpInfo.searchForUpdate(folder2date);*/
            
            HashMap<String, HashMap<String, String>> parser_meta = new HashMap<>();
            HashMap<String, HashSet<String>> parser_meta_dict = new HashMap<>();
            for (File f : data_dir.listFiles()) {
                if (!isToSkip(f.getName()) && f.getName().toLowerCase().contains(data_subtype)) {
                    String bcr_aliquot_barcode = FileInputParser.retrievePatientReference(f, data_type);
                    HashMap<String, String[]> data = FileInputParser.retrieveData(f, data_type);
                    try {
                        File output = new File(out_dir_path + bcr_aliquot_barcode + gendata_ext);
                        output.createNewFile();
                        FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
                        PrintStream out = new PrintStream(fos);
                        
                        out.printf(OutputFormat.initDocument(getFormat()));
                        
                        for (String k : data.keySet()) {
                            try {
                                if (!k.equals("header")) {
                                    String[] d = data.get(k);
                                    String mirna_id = d[mirnaid_index];
                                    String coordinates = "";
                                    if (data_subtype.toLowerCase().equals("isoform"))
                                        coordinates = d[isoform_coords_index];
                                    else if (data_subtype.toLowerCase().equals("mirna")) {
                                        if (!mirna2GenomeCoordinates.isEmpty()) {
                                            if (mirna2GenomeCoordinates.containsKey(mirna_id)) {
                                                coordinates+="hg:"+mirna2GenomeCoordinates.get(mirna_id).get("CHR")+":"+mirna2GenomeCoordinates.get(mirna_id).get("START")+"-"+mirna2GenomeCoordinates.get(mirna_id).get("END")+":"+mirna2GenomeCoordinates.get(mirna_id).get("STRAND");
                                            }
                                        }
                                    }
                                    
                                    if (!coordinates.toLowerCase().trim().equals("")) {
                                        //if (data_subtype.toLowerCase().equals("isoform"))
                                            //System.err.println(coordinates);
                                        String[] coords_split = coordinates.split(":");
                                        if (coords_split.length>=4) {
                                            //if (data_subtype.toLowerCase().equals("isoform"))
                                                //System.err.println(coordinates);
                                            String chr = coords_split[1];
                                            String strand = coords_split[3];
                                            int start = Integer.valueOf(coords_split[2].split("-")[0]);
                                            int end = Integer.valueOf(coords_split[2].split("-")[1]);
                                            String genome_version = coords_split[0];

                                            ArrayList<String> tmpArr = new ArrayList<>();
                                            //String tmp = "";
                                            String annotation = "";
                                            if (data_subtype.toLowerCase().equals("mirna")) {
                                                //tmp += chr + "\t";
                                                tmpArr.add(chr);
                                                annotation += chr + "\t";
                                            }
                                            else if (data_subtype.toLowerCase().equals("isoform")) {
                                                //tmp += "chr" + chr + "\t";
                                                tmpArr.add("chr"+chr);
                                                annotation += "chr" + chr + "\t";
                                            }
                                            //tmp += start + "\t" + end + "\t" + strand + "\t" + mirna_id + "\t";
                                            tmpArr.add(String.valueOf(start)); tmpArr.add(String.valueOf(end)); tmpArr.add(strand); tmpArr.add(mirna_id);
                                            annotation += start + "\t" + end + "\t" + strand;
                                            
                                            String read_count;
                                            String reads_per_million_miRNA_mapped;
                                            String cross_mapped;
                                            
                                            if (data_subtype.toLowerCase().equals("isoform")) {
                                                read_count = d[isoform_readcount_index];
                                                reads_per_million_miRNA_mapped = d[isoform_reads_per_million_miRNA_mapped_index]; 
                                                cross_mapped = d[isoform_cross_mapped_index];
                                                String mirna_region = d[isoform_mirnaregion_index];

                                                //tmp += genome_version + "\t";
                                                tmpArr.add(genome_version);

                                                if (!skipIndexList.contains(isoform_readcount_index)) {
                                                    if (read_count.trim().equals("") || read_count.trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += read_count + "\t";
                                                        tmpArr.add(read_count);
                                                    }
                                                }
                                                if (!skipIndexList.contains(isoform_reads_per_million_miRNA_mapped_index)) {
                                                    if (reads_per_million_miRNA_mapped.trim().equals("") || reads_per_million_miRNA_mapped.trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += reads_per_million_miRNA_mapped + "\t";
                                                        tmpArr.add(reads_per_million_miRNA_mapped);
                                                    }
                                                }
                                                if (!skipIndexList.contains(isoform_cross_mapped_index)) {
                                                    if (cross_mapped.trim().equals("") || cross_mapped.trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += cross_mapped + "\t";
                                                        tmpArr.add(cross_mapped);
                                                    }
                                                }
                                                if (!skipIndexList.contains(isoform_mirnaregion_index)) {
                                                    if (mirna_region.trim().equals("") || mirna_region.trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += mirna_region + "\t";
                                                        tmpArr.add(mirna_region);
                                                    }
                                                }
                                            }
                                            else if (data_subtype.toLowerCase().equals("mirna")) {
                                                read_count = d[mirna_readcount_index];
                                                reads_per_million_miRNA_mapped = d[mirna_reads_per_million_miRNA_mapped_index];
                                                cross_mapped = d[mirna_cross_mapped_index];
                                                if (!skipIndexList.contains(mirna_readcount_index)) {
                                                    if (read_count.trim().equals("") || read_count.trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += read_count + "\t";
                                                        tmpArr.add(read_count);
                                                    }
                                                }
                                                if (!skipIndexList.contains(mirna_reads_per_million_miRNA_mapped_index)) {
                                                    if (reads_per_million_miRNA_mapped.trim().equals("") || reads_per_million_miRNA_mapped.trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += reads_per_million_miRNA_mapped + "\t";
                                                        tmpArr.add(reads_per_million_miRNA_mapped);
                                                    }
                                                }
                                                if (!skipIndexList.contains(mirna_cross_mapped_index)) {
                                                    if (cross_mapped.trim().equals("") || cross_mapped.trim().equals("NA")) {
                                                        //tmp += "null\t";
                                                        tmpArr.add("null");
                                                    } else {
                                                        //tmp += cross_mapped + "\t";
                                                        tmpArr.add(cross_mapped);
                                                    }
                                                }
                                            }
                                            
                                            // add entrez gene id
                                            if (mirna2entrez.containsKey(mirna_id))
                                                tmpArr.add(mirna2entrez.get(mirna_id));
                                            else if (mirnas.contains(mirna_id))
                                                tmpArr.add("null");
                                            else {
                                                String hgnc_id = mirna2hgncid.get(mirna_id);
                                                String entrez = RetrieveMirnaFromHGNC.getEntrezFromHGNCid(hgnc_id);
                                                if (entrez.trim().equals("")) {
                                                    RetrieveMirnaFromHGNC.updateUnresolved(mirna_id);
                                                    mirnas = RetrieveMirnaFromHGNC.getUnresolved();
                                                    tmpArr.add("null");
                                                }
                                                else {
                                                    RetrieveMirnaFromHGNC.updateResolved(mirna_id, entrez);
                                                    mirna2entrez = RetrieveMirnaFromHGNC.getResolved();
                                                    tmpArr.add(entrez);
                                                }
                                            }
                                            
                                            //add approved gene symbol
                                            if (mirna2symbol.containsKey(mirna_id))
                                                tmpArr.add(mirna2symbol.get(mirna_id));
                                            else
                                                tmpArr.add("null");

                                            //if (!tmp.trim().equals("")) {
                                            if (!tmpArr.isEmpty()) {
                                                incrementAliquotCount(bcr_aliquot_barcode);
                                                incrementSampleCount(bcr_aliquot_barcode);
                                                incrementPatientCount(bcr_aliquot_barcode);
                                                addAnnotation(annotation);
                                                //out.println(tmp);
                                                String tmp = OutputFormat.createOutLine(getFormat(), tmpArr, getHeader(data_type, data_subtype));
                                                out.print(tmp);
                                            }
                                        }
                                    }
                                }
                                
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        
                        out.printf(OutputFormat.endDocument(getFormat()));
                        out.close();
                        fos.close();
                        
                        String md5sum = MD5Checksum.getMD5Checksum(output.getAbsolutePath());
                        
                        //update metadata
                        HashMap<String, String> metaVals = new HashMap<>();
                        if (parser_meta.containsKey(bcr_aliquot_barcode.replaceAll("_", "-"))) {
                            metaVals = parser_meta.get(bcr_aliquot_barcode.replaceAll("_", "-"));
                        }
                        metaVals.put("manually_curated|id", bcr_aliquot_barcode.replaceAll("_", "-").toLowerCase());
                        metaVals.put("manually_curated|dataType", data_type.toLowerCase());
                        //metaVals.put("manually_curated|meta_biotab_url", meta_biotab_url.toLowerCase());
                        metaVals.put("manually_curated|tumor_tag", disease.toLowerCase());
                        metaVals.put("manually_curated|tumor_description", getAbbreviation2Description().get(disease.toUpperCase()).toLowerCase());
                        metaVals.put("manually_curated|tissue_status", getTissueStatus(bcr_aliquot_barcode).toLowerCase());
                        //metaVals.put("manually_curated|exp_data_url", url + updatedFolder + f.getCanonicalFile().getName());
                        metaVals.put("manually_curated|seqPlatform", HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_platform").toLowerCase());
                        metaVals.put("manually_curated|mirna_seq_exp_type", data_subtype.toLowerCase() + "_quantification".toLowerCase());
                        metaVals.put("manually_curated|exp_data_bed_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + bcr_aliquot_barcode + gendata_ext);
                        metaVals.put("manually_curated|exp_metadata_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + bcr_aliquot_barcode + metadata_ext);
                        metaVals.put("manually_curated|md5sum", md5sum);
                        parser_meta.put(bcr_aliquot_barcode.replaceAll("_", "-"), metaVals);
                        
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
                        metaDictVals.add(bcr_aliquot_barcode.toLowerCase());
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
                        if (parser_meta_dict.containsKey("manually_curated|tissue_status")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|tissue_status");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(getTissueStatus(bcr_aliquot_barcode).toLowerCase());
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
                        if (parser_meta_dict.containsKey("manually_curated|mirna_seq_exp_type")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|mirna_seq_exp_type");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(data_subtype.toLowerCase() + "_quantification".toLowerCase());
                        parser_meta_dict.put("manually_curated|mirna_seq_exp_type", metaDictVals);
                        
                        if (parser_meta_dict.containsKey("manually_curated|exp_data_bed_url")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|exp_data_bed_url");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + bcr_aliquot_barcode + gendata_ext);
                        parser_meta_dict.put("manually_curated|exp_data_bed_url", metaDictVals);
                        if (parser_meta_dict.containsKey("manually_curated|exp_metadata_url")) {
                            metaDictVals = parser_meta_dict.get("manually_curated|exp_metadata_url");
                        } else {
                            metaDictVals = new HashSet<>();
                        }
                        metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + bcr_aliquot_barcode + metadata_ext);
                        parser_meta_dict.put("manually_curated|exp_metadata_url", metaDictVals);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!patientIds.contains(bcr_aliquot_barcode)) {
                        patientIds.add(bcr_aliquot_barcode);
                    }
                }
            }
            setMetadata(parser_meta);
            setMetadataDictionary(parser_meta_dict);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return patientIds;
    }

    @Override
    public String[] getHeader(String data_type, String data_subtype) {
        String[] header;
        if (data_subtype.toLowerCase().equals("mirna")) {
            header = new String[10];
            header[0] = "chr";
            header[1] = "start";
            header[2] = "stop";
            header[3] = "strand";
            header[4] = "mirna_id";
            header[5] = "read_count";
            header[6] = "reads_per_million_miRNA_mapped";
            header[7] = "cross-mapped";
            header[8] = "entrez_gene_id";
            header[9] = "gene_symbol";
            return header;
        }
        else if (data_subtype.toLowerCase().equals("isoform")) {
            header = new String[12];
            header[0] = "chr";
            header[1] = "start";
            header[2] = "stop";
            header[3] = "strand";
            header[4] = "genome_version";
            header[5] = "mirna_id";
            header[6] = "read_count";
            header[7] = "reads_per_million_miRNA_mapped";
            header[8] = "cross-mapped";
            header[9] = "miRNA_region";
            header[10] = "entrez_gene_id";
            header[11] = "gene_symbol";
            return header;
        }
        return null;
    }

    @Override
    public String[] getAttributesType(String data_type, String data_subtype) {
        String[] attr_type;
        if (data_subtype.toLowerCase().equals("mirna")) {
            attr_type = new String[10];
            attr_type[0] = "STRING";
            attr_type[1] = "LONG";
            attr_type[2] = "LONG";
            attr_type[3] = "CHAR";
            attr_type[4] = "STRING";
            attr_type[5] = "INTEGER";
            attr_type[6] = "FLOAT";
            attr_type[7] = "STRING";
            attr_type[8] = "STRING";
            attr_type[9] = "STRING";
            return attr_type;
        }
        else if (data_subtype.toLowerCase().equals("isoform")) {
            attr_type = new String[12];
            attr_type[0] = "STRING";
            attr_type[1] = "LONG";
            attr_type[2] = "LONG";
            attr_type[3] = "CHAR";
            attr_type[4] = "STRING";
            attr_type[5] = "STRING";
            attr_type[6] = "INTEGER";
            attr_type[7] = "FLOAT";
            attr_type[8] = "STRING";
            attr_type[9] = "STRING";
            attr_type[10] = "STRING";
            attr_type[11] = "STRING";
            return attr_type;
        }
        return null;
    }
    
}
