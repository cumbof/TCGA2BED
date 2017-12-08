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
import tcga2bed.resources.RetrieveUCSCGenomicCoordinates;
import tcga2bed.util.FileInputParser;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MD5Checksum;
import tcga2bed.util.OutputFormat;
import tcga2bed.util.QueryParser;

public class RNASeqV2Parser extends BioParser {

    private static HashMap<String, HashMap<String, String>> entrez2GenomeCoordinates = new HashMap<String, HashMap<String, String>>();
    private static HashSet<String> unresolved = new HashSet<String>();
    private static HashMap<String, HashMap<String, String>> transcript2GenomeCoordinates = null;
    
    @Override
    public HashSet<String> convert(File data_dir, File meta_dir, String disease, String data_type, String data_subtype, String out_dir_path, String out_fileName_prefix, String gendata_ext, String metadata_ext) {
        if (data_subtype.toUpperCase().equals("SPLJXN") || data_subtype.toUpperCase().equals("EXON")) {
            BioParser parser = new RNASeqParser();
            parser.convert(data_dir, meta_dir, disease, data_type, data_subtype, out_dir_path, out_fileName_prefix, gendata_ext, metadata_ext);
            setMetadata(parser.getMetadata());
            setMetadataDictionary(parser.getMetadataDictionary());
            setAliquotSet(parser.getAliquotSet());
            setSampleSet(parser.getSampleSet());
            setPatientSet(parser.getPatientSet());
            setAnnotationSet(parser.getAnnotationSet());
            return (new HashSet<>(parser.getMetadata().keySet()));
        } else if (data_subtype.toUpperCase().equals("GENE") || data_subtype.toUpperCase().equals("ISOFORM")) {
            HashSet<String> patientIds = new HashSet<>();
            entrez2GenomeCoordinates = RetrieveNCBIGenomicCoordinates.loadTextDB_resolved();
            unresolved = RetrieveNCBIGenomicCoordinates.loadTextDB_unresolved();

            if (data_subtype.toUpperCase().equals("ISOFORM")) {
                transcript2GenomeCoordinates = RetrieveUCSCGenomicCoordinates.loadTextDB();
            }

            HashSet<String> stores = new HashSet<>();

            try {
                HashMap<String, HashMap<String, String>> parser_meta = new HashMap<>();
                HashMap<String, HashSet<String>> parser_meta_dict = new HashMap<>();

                HTTPExpInfo.initDiseaseInfo();
                /*File file_tmp = File.createTempFile("http_tcga", "html");
                String out_path = file_tmp.getAbsolutePath();
                String url = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_root_dir");

                HashMap<String, String> folder2date = HTTPExpInfo.getData2Date(url, out_path, disease, data_type, "/", "_data_dir_prefix");
                file_tmp.delete();
                String updatedFolder = HTTPExpInfo.searchForUpdate(folder2date);*/

                for (File f_gr : data_dir.listFiles()) {
                    if (!isToSkip(f_gr.getName())) {
                        if (f_gr.getName().toLowerCase().endsWith("genes.results")) {
                            String patientRef_uuid = FileInputParser.retrievePatientReference(f_gr, data_type);
                            String patientRef_barcode = QueryParser.getTCGABarcodeFromUUID(patientRef_uuid, meta_dir.getAbsolutePath());
                            
                            HashMap<String, String[]> data_gr = FileInputParser.retrieveData(f_gr, data_type);
                            File f_ir = new File(getPath(data_dir, patientRef_uuid, "isoforms.results"));
                            HashMap<String, String[]> data_ir = FileInputParser.retrieveData(f_ir, data_type);
                            File f_nr = null;
                            HashMap<String, String[]> data_nr = null;
                            if (data_subtype.toUpperCase().equals("ISOFORM")) {
                                f_nr = new File(getPath(data_dir, patientRef_uuid, "isoforms.normalized_results"));
                                data_nr = FileInputParser.retrieveData(f_nr, data_type);
                            } else if (data_subtype.toUpperCase().equals("GENE")) {
                                f_nr = new File(getPath(data_dir, patientRef_uuid, "genes.normalized_results"));
                                data_nr = FileInputParser.retrieveData(f_nr, data_type);
                            }

                            HashMap<String, String> isoform2gene = new HashMap<>();
                            int list_index = -1;
                            for (int h = 0; h < data_gr.get("header").length; h++) {
                                if (data_gr.get("header")[h].toLowerCase().equals("transcript_id")) {
                                    list_index = h;
                                }
                            }
                            for (String k : data_gr.keySet()) {
                                if (!k.equals("header")) {
                                    String[] d_gr = data_gr.get(k);
                                    String[] transcript_ids = d_gr[list_index].split(",");
                                    for (String t : transcript_ids) {
                                        isoform2gene.put(t, d_gr[0]);
                                    }
                                }
                            }

                            HashMap<String, String[]> data = null;
                            if (data_subtype.toUpperCase().equals("ISOFORM")) {
                                data = data_ir;
                            } else if (data_subtype.toUpperCase().equals("GENE")) {
                                data = data_gr;
                            }

                            int countForSkippedIsoforms = 0;

                            File output = new File(out_dir_path + patientRef_barcode + gendata_ext);
                            FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
                            PrintStream out = new PrintStream(fos);
                            
                            out.printf(OutputFormat.initDocument(getFormat()));
                            
                            for (String k : data.keySet()) {
                                if (!k.equals("header")) {
                                    String chr = "", strand = "";
                                    int start = 0, end = 0;
                                    String[] d = data.get(k);

                                    String gene2entrez = "";
                                    if (data_subtype.toUpperCase().equals("ISOFORM")) {
                                        if (isoform2gene.containsKey(d[0])) {
                                            gene2entrez = isoform2gene.get(d[0]);
                                        }
                                    } else if (data_subtype.toUpperCase().equals("GENE")) {
                                        gene2entrez = d[0];
                                    }

                                    if (!gene2entrez.equals("")) {
                                        HashMap<String, String> genomic_coordinates = askNCBIEntrez(gene2entrez);
                                        
                                        if (!genomic_coordinates.isEmpty()) {
                                            chr = genomic_coordinates.get("chr");
                                            start = Integer.valueOf(genomic_coordinates.get("start"));
                                            end = Integer.valueOf(genomic_coordinates.get("end"));
                                            strand = genomic_coordinates.get("strand");
                                        }
                                    } else {
                                        if (data_subtype.toUpperCase().equals("ISOFORM")) {
                                            String store = d[0];

                                            stores.add(store);
                                            //System.err.println(store);
                                            countForSkippedIsoforms++;
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

                                        if (gene2entrez.trim().equals("") || gene2entrez.trim().equals("NA")) {
                                            try {
                                                String ucsc_symbol = transcript2GenomeCoordinates.get(d[0]).get("UCSC_SYMBOL");
                                                //tmp += ucsc_symbol + "\t" + "null" + "\t";
                                                tmpArr.add(ucsc_symbol); tmpArr.add("null");
                                                annotation += ucsc_symbol + "\t";
                                            } catch (Exception e) {
                                                //tmp += "null" + "\t" + "null" + "\t";
                                                tmpArr.add("null"); tmpArr.add("null");
                                                annotation += "null" + "\t";
                                            }
                                        } else {
                                            String tcga_entrez = gene2entrez.split("\\|")[1].split("_")[0];
                                            //tmp += entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_SYMBOL") + "\t" + entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_ENTREZ") + "\t";
                                            tmpArr.add(entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_SYMBOL")); tmpArr.add(entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_ENTREZ"));
                                            annotation += entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_SYMBOL") + "\t" + entrez2GenomeCoordinates.get(tcga_entrez).get("NCBI_ENTREZ") + "\t";
                                        }

                                        if (data_subtype.toUpperCase().equals("ISOFORM")) {
                                            String isoform_id = d[0];
                                            if (isoform_id.trim().equals("") || isoform_id.trim().equals("NA")) {
                                                //tmp += "null\t";
                                                tmpArr.add("null");
                                            } else {
                                                //tmp += isoform_id + "\t";
                                                tmpArr.add(isoform_id);
                                            }
                                        }

                                        String raw_counts = d[1];
                                        if (raw_counts.trim().equals("") || raw_counts.trim().equals("NA")) {
                                            //tmp += "null\t";
                                            tmpArr.add("null");
                                        } else {
                                            //tmp += raw_counts + "\t";
                                            tmpArr.add(raw_counts);
                                        }

                                        String scaled_estimate = d[2];
                                        if (scaled_estimate.trim().equals("") || scaled_estimate.trim().equals("NA")) {
                                            //tmp += "null\t";
                                            tmpArr.add("null");
                                        } else {
                                            //tmp += scaled_estimate + "\t";
                                            tmpArr.add(scaled_estimate);
                                        }

                                        if (data_subtype.toUpperCase().equals("GENE")) {
                                            String transcript_id = d[3];
                                            if (transcript_id.trim().equals("") || transcript_id.trim().equals("NA")) {
                                                //tmp += "null\t";
                                                tmpArr.add("null");
                                                annotation += "null";
                                            } else {
                                                //tmp += transcript_id + "\t";
                                                tmpArr.add(transcript_id);
                                                annotation += transcript_id;
                                            }
                                        }

                                        String[] d_gnr = data_nr.get(k);
                                        String normalized_counts = d_gnr[1];
                                        if (normalized_counts.trim().equals("") || normalized_counts.trim().equals("NA")) {
                                            //tmp += "null";
                                            tmpArr.add("null");
                                        } else {
                                            //tmp += normalized_counts;
                                            tmpArr.add(normalized_counts);
                                        }

                                        //if (!tmp.trim().equals("")) {
                                        if (!tmpArr.isEmpty()) {
                                            incrementAliquotCount(patientRef_barcode);
                                            incrementSampleCount(patientRef_barcode);
                                            incrementPatientCount(patientRef_barcode);
                                            addAnnotation(annotation);
                                            //out.println(tmp);
                                            String tmp = OutputFormat.createOutLine(getFormat(), tmpArr, getHeader(data_type, data_subtype));
                                            out.print(tmp);
                                        }
                                    }
                                }
                            }

                            if (data_subtype.toUpperCase().equals("ISOFORM")) {
                                System.err.println("Barcode: " + patientRef_barcode.replaceAll("_", "-") + "\tUUID: " + patientRef_uuid + "\tSkipped Isoforms: " + countForSkippedIsoforms);
                                //for (String s: stores)
                                    //System.err.println(s);
                            }
                            
                            out.printf(OutputFormat.endDocument(getFormat()));
                            out.close();
                            fos.close();
                            
                            String md5sum = MD5Checksum.getMD5Checksum(output.getAbsolutePath());

                            //update metadata
                            HashMap<String, String> metaVals = new HashMap<>();
                            if (parser_meta.containsKey(patientRef_barcode.replaceAll("_", "-"))) {
                                metaVals = parser_meta.get(patientRef_barcode.replaceAll("_", "-"));
                            }
                            metaVals.put("manually_curated|id", patientRef_barcode.replaceAll("_", "-").toLowerCase());
                            metaVals.put("manually_curated|dataType", data_type.toLowerCase());
                            //metaVals.put("manually_curated|meta_biotab_url", meta_biotab_url.toLowerCase());
                            metaVals.put("manually_curated|tumor_tag", disease.toLowerCase());
                            metaVals.put("manually_curated|tumor_description", getAbbreviation2Description().get(disease.toUpperCase()).toLowerCase());
                            metaVals.put("manually_curated|rna_seq_exp_type", data_subtype.toLowerCase() + "_quantification".toLowerCase());
                            metaVals.put("manually_curated|rna_seq_data_unit", "RSEM".toLowerCase());
                            metaVals.put("manually_curated|tissue_status", getTissueStatus(patientRef_barcode).toLowerCase());
                            /*if (data_type.toUpperCase().equals("GENE")) {
                                metaVals.put("manually_curated|exp_data_url", url + updatedFolder + f_gr.getCanonicalFile().getName());
                            } else if (data_type.toUpperCase().equals("ISOFORM")) {
                                metaVals.put("manually_curated|exp_data_url", url + updatedFolder + f_ir.getCanonicalFile().getName());
                            }*/
                            //metaVals.put("manually_curated|exp_normalized_data_url", url + updatedFolder + f_nr.getName().toLowerCase());
                            metaVals.put("manually_curated|seqPlatform", HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_platform").toLowerCase());
                            metaVals.put("manually_curated|exp_data_bed_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + patientRef_barcode + gendata_ext);
                            metaVals.put("manually_curated|exp_metadata_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + patientRef_barcode + metadata_ext);
                            metaVals.put("manually_curated|md5sum", md5sum);
                            parser_meta.put(patientRef_barcode.replaceAll("_", "-"), metaVals);

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
                            metaDictVals.add(patientRef_barcode.replaceAll("_", "-").toLowerCase());
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
                            metaDictVals.add("RSEM".toLowerCase());
                            parser_meta_dict.put("manually_curated|rna_seq_data_unit", metaDictVals);

                            if (parser_meta_dict.containsKey("manually_curated|tissue_status")) {
                                metaDictVals = parser_meta_dict.get("manually_curated|tissue_status");
                            } else {
                                metaDictVals = new HashSet<>();
                            }
                            metaDictVals.add(getTissueStatus(patientRef_barcode).toLowerCase());
                            parser_meta_dict.put("manually_curated|tissue_status", metaDictVals);

                            /*if (parser_meta_dict.containsKey("manually_curated|exp_normalized_data_url")) {
                                metaDictVals = parser_meta_dict.get("manually_curated|exp_normalized_data_url");
                            } else {
                                metaDictVals = new HashSet<>();
                            }
                            metaDictVals.add(url + updatedFolder + f_nr.getCanonicalFile().getName());
                            parser_meta_dict.put("manually_curated|exp_normalized_data_url", metaDictVals);*/

                            /*if (parser_meta_dict.containsKey("manually_curated|exp_data_url")) {
                                metaDictVals = parser_meta_dict.get("manually_curated|exp_data_url");
                            } else {
                                metaDictVals = new HashSet<>();
                            }
                            if (data_type.toUpperCase().equals("GENE")) {
                                metaDictVals.add(url + updatedFolder + f_gr.getCanonicalFile().getName());
                            } else if (data_type.toUpperCase().equals("ISOFORM")) {
                                metaDictVals.add(url + updatedFolder + f_ir.getCanonicalFile().getName());
                            }
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
                            metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + patientRef_barcode + gendata_ext);
                            parser_meta_dict.put("manually_curated|exp_data_bed_url", metaDictVals);
                            if (parser_meta_dict.containsKey("manually_curated|exp_metadata_url")) {
                                metaDictVals = parser_meta_dict.get("manually_curated|exp_metadata_url");
                            } else {
                                metaDictVals = new HashSet<>();
                            }
                            metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + data_subtype.toLowerCase()+".quantification" + "/" + patientRef_barcode + metadata_ext);
                            parser_meta_dict.put("manually_curated|exp_metadata_url", metaDictVals);
                            
                            if (!patientIds.contains(patientRef_barcode)) {
                                patientIds.add(patientRef_barcode);
                            }
                        }
                    }
                }
                setMetadata(parser_meta);
                setMetadataDictionary(parser_meta_dict);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (data_subtype.toUpperCase().equals("ISOFORM")) {
                for (String s : stores) {
                    System.err.println(s);
                }
            }

            return patientIds;
        }
        return new HashSet<>();
    }

    private String getPath(File data_dir, String patientRef_uuid, String match) {
        for (File f : data_dir.listFiles()) {
            if (f.getName().contains(patientRef_uuid) && f.getName().contains(match)) {
                return f.getAbsolutePath();
            }
        }
        return null;
    }

    public HashMap<String, String> askNCBIEntrez(String gene2entrez) {
        HashMap<String, String> result = new HashMap<>();
        String[] geneInfo = gene2entrez.split("\\|");
        if (geneInfo.length == 2) {
            String entrez = (geneInfo[1]).split("_")[0];
            String gene = geneInfo[0];
            if (entrez.equals("?") && !unresolved.contains(gene2entrez)) {
                RetrieveNCBIGenomicCoordinates.updateUnresolvedQueries(gene2entrez);
                unresolved = RetrieveNCBIGenomicCoordinates.loadTextDB_unresolved();
            } else {
                if (!RetrieveNCBIGenomicCoordinates.getNcbiArchive().equals("null")) {
                    if (!entrez2GenomeCoordinates.containsKey(entrez)) {
                        if (!unresolved.contains(gene2entrez)) {
                            if (RetrieveNCBIGenomicCoordinates.updateLocalGenomeCoordinatesDB(entrez, gene)) {
                                entrez2GenomeCoordinates = RetrieveNCBIGenomicCoordinates.loadTextDB_resolved();
                                result.put("chr", entrez2GenomeCoordinates.get(entrez).get("CHR"));
                                result.put("start", entrez2GenomeCoordinates.get(entrez).get("START"));
                                result.put("end", entrez2GenomeCoordinates.get(entrez).get("END"));
                                result.put("strand", entrez2GenomeCoordinates.get(entrez).get("STRAND"));
                            } else {
                                RetrieveNCBIGenomicCoordinates.updateUnresolvedQueries(gene2entrez);
                                unresolved = RetrieveNCBIGenomicCoordinates.loadTextDB_unresolved();
                            }
                        }
                    } else {
                        result.put("chr", entrez2GenomeCoordinates.get(entrez).get("CHR"));
                        result.put("start", entrez2GenomeCoordinates.get(entrez).get("START"));
                        result.put("end", entrez2GenomeCoordinates.get(entrez).get("END"));
                        result.put("strand", entrez2GenomeCoordinates.get(entrez).get("STRAND"));
                    }
                } else {
                    HashMap<String, String> entrezCoords = RetrieveNCBIGenomicCoordinates.simpleRetrieveGenomicCoordinates(entrez, gene);
                    if (!entrezCoords.isEmpty()) {
                        if (!entrezCoords.get("CHR").equals("") && (Integer.valueOf(entrezCoords.get("START")) > 0 && Integer.valueOf(entrezCoords.get("END")) > 0)) {
                            result.put("chr", entrezCoords.get("CHR"));
                            result.put("start", entrezCoords.get("START"));
                            result.put("end", entrezCoords.get("END"));
                            result.put("strand", entrezCoords.get("STRAND"));
                        }
                    }
                }
            }
        } else {
            if (!RetrieveNCBIGenomicCoordinates.getNcbiArchive().equals("null")) {
                RetrieveNCBIGenomicCoordinates.updateUnresolvedQueries(gene2entrez);
            }
        }
        return result;
    }

    @Override
    public String[] getHeader(String data_type, String data_subtype) {
        if (data_subtype.toLowerCase().equals("spljxn") || data_subtype.toLowerCase().equals("exon")) {
            return (new RNASeqParser()).getHeader(data_type, data_subtype);
        } else if (data_subtype.toLowerCase().equals("gene")) {
            String[] header = new String[10];
            header[0] = "chr";
            header[1] = "start";
            header[2] = "stop";
            header[3] = "strand";
            header[4] = "gene_symbol";
            header[5] = "entrez_gene_id";
            header[6] = "raw_count";
            header[7] = "scaled_estimate";
            header[8] = "transcript_id";
            header[9] = "normalized_count";
            return header;
        } else if (data_subtype.toLowerCase().equals("isoform")) {
            String[] header = new String[10];
            header[0] = "chr";
            header[1] = "start";
            header[2] = "stop";
            header[3] = "strand";
            header[4] = "gene_symbol";
            header[5] = "entrez_gene_id";
            header[6] = "transcript_id";
            header[7] = "raw_count";
            header[8] = "scaled_estimate";
            header[9] = "normalized_count";
            return header;
        }
        return null;
    }

    @Override
    public String[] getAttributesType(String data_type, String data_subtype) {
        if (data_subtype.toLowerCase().equals("spljxn") || data_subtype.toLowerCase().equals("exon")) {
            return (new RNASeqParser()).getAttributesType(data_type, data_subtype);
        } else if (data_subtype.toLowerCase().equals("gene")) {
            String[] attr_type = new String[10];
            attr_type[0] = "STRING";
            attr_type[1] = "LONG";
            attr_type[2] = "LONG";
            attr_type[3] = "CHAR";
            attr_type[4] = "STRING";
            attr_type[5] = "STRING";
            attr_type[6] = "DOUBLE";
            attr_type[7] = "DOUBLE";
            attr_type[8] = "STRING";
            attr_type[9] = "DOUBLE";
            return attr_type;
        } else if (data_subtype.toLowerCase().equals("isoform")) {
            String[] attr_type = new String[10];
            attr_type[0] = "STRING";
            attr_type[1] = "LONG";
            attr_type[2] = "LONG";
            attr_type[3] = "CHAR";
            attr_type[4] = "STRING";
            attr_type[5] = "STRING";
            attr_type[6] = "STRING";
            attr_type[7] = "DOUBLE";
            attr_type[8] = "DOUBLE";
            attr_type[9] = "DOUBLE";
            return attr_type;
        }
        return null;
    }

}
