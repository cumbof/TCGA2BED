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
import java.io.IOException;
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
import tcga2bed.util.XMLReader;

public class DNAMethylation450Parser extends BioParser {

    private HashMap<String, HashMap<String, String>> entrez2GenomeCoordinates = null;
    private HashMap<String, HashMap<String, String>> gene2GenomeCoordinates = null;
    int difference_tcgaSymbolKeySet = 0;
    private HashSet<String> unresolved = null;

    @Override
    public HashSet<String> convert(File data_dir, File meta_dir, String disease, String data_type, String data_subtype, String out_dir_path, String out_fileName_prefix, String gendata_ext, String metadata_ext) {
        HashSet<String> patientIds = new HashSet<>();
        try {
            if (!RetrieveNCBIGenomicCoordinates.getNcbiArchive().equals("null")) {
                entrez2GenomeCoordinates = RetrieveNCBIGenomicCoordinates.loadTextDB_resolved();
                unresolved = RetrieveNCBIGenomicCoordinates.loadTextDB_unresolved();
            }

            //int patientRefIndex = 15;
            int startIndex = 4;
            int chrIndex = 3;
            int geneIndex = 2;
            int compositeElementIndex = 0;
            int betaValueIndex = 1;

            skipIndexList = new ArrayList<>();
            //skipIndexList.add(patientRefIndex);
            //skipIndexList.add(chrIndex);
            //skipIndexList.add(startIndex);
            //skipIndexList.add(endIndex);
            //skipIndexList.add(strandIndex);

            HTTPExpInfo.initDiseaseInfo();
            /*File tmp_file = File.createTempFile("http_tcga", "html");
            String out_path = tmp_file.getAbsolutePath();
            String url = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_root_dir");

            HashMap<String, String> folder2date = HTTPExpInfo.getData2Date(url, out_path, disease, data_type, "/", "_data_dir_prefix");
            tmp_file.delete();
            String updatedFolder = HTTPExpInfo.searchForUpdate(folder2date);*/

            HashMap<String, HashMap<String, String>> parser_meta = new HashMap<>();
            HashMap<String, HashSet<String>> parser_meta_dict = new HashMap<>();
            for (File f : data_dir.listFiles()) {
                if (!isToSkip(f.getName())) {
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
                                    String strand = "+";
                                    String geneSymbol = d[geneIndex].split(";")[0];
                                    String entrez = "";

                                    if (!geneSymbol.equals("NA") && !geneSymbol.equals("") && !geneSymbol.equals("?")) {

                                        int start = Integer.MAX_VALUE, end = Integer.MAX_VALUE;
                                        String gene = "";
                                        start = Integer.valueOf(d[startIndex]);
                                        //end = start+1;
                                        end = start;

                                        if (entrez2GenomeCoordinates != null) {
                                            HashMap<String, String> geneInfo = getGeneInfoInDB(geneSymbol);
                                            if (!geneInfo.isEmpty()) {
                                                //start = Integer.valueOf(geneInfo.get("START"));
                                                //end = Integer.valueOf(geneInfo.get("END"));
                                                strand = geneInfo.get("STRAND");
                                                gene = geneInfo.get("NCBI_SYMBOL");
                                            } else if (!isInUnres(geneSymbol, unresolved)) {
                                                String hugoFetchQuery = "http://rest.genenames.org/fetch/symbol/" + geneSymbol;
                                                File hugoXml_tmp = File.createTempFile("hugo_tmp", "tmp");
                                                QueryParser.downloadDataFromUrl(hugoFetchQuery, hugoXml_tmp.getAbsolutePath(), 0);
                                                entrez = XMLReader.getEntrezFromHugo(hugoXml_tmp.getAbsolutePath());
                                                //hugoXml_tmp.delete();
                                                if (!entrez.equals("")) {
                                                    if (RetrieveNCBIGenomicCoordinates.updateLocalGenomeCoordinatesDB(entrez, geneSymbol)) {
                                                        entrez2GenomeCoordinates = RetrieveNCBIGenomicCoordinates.loadTextDB_resolved();
                                                        strand = entrez2GenomeCoordinates.get(entrez).get("STRAND");
							//start = Integer.valueOf(entrez2GenomeCoordinates.get(entrez).get("START"));
                                                        //end = Integer.valueOf(entrez2GenomeCoordinates.get(entrez).get("END"));
                                                        gene = entrez2GenomeCoordinates.get(entrez).get("NCBI_SYMBOL");
                                                    } else {
                                                        RetrieveNCBIGenomicCoordinates.updateUnresolvedQueries(geneSymbol + "|NaN_" + geneSymbol);
                                                        unresolved = RetrieveNCBIGenomicCoordinates.loadTextDB_unresolved();
                                                    }
                                                } else {
                                                    RetrieveNCBIGenomicCoordinates.updateUnresolvedQueries(geneSymbol + "|NaN_" + geneSymbol);
                                                    unresolved = RetrieveNCBIGenomicCoordinates.loadTextDB_unresolved();
                                                }
                                            }
                                        } else {
                                            String hugoFetchQuery = "http://rest.genenames.org/fetch/symbol/" + geneSymbol;
                                            File hugoXml_tmp = File.createTempFile("hugo_tmp", "tmp");
                                            QueryParser.downloadDataFromUrl(hugoFetchQuery, hugoXml_tmp.getAbsolutePath(), 0);
                                            entrez = XMLReader.getEntrezFromHugo(hugoXml_tmp.getAbsolutePath());
                                            hugoXml_tmp.delete();
                                            if (!entrez.equals("")) {
                                                if (RetrieveNCBIGenomicCoordinates.updateLocalGenomeCoordinatesDB(entrez, geneSymbol)) {
                                                    entrez2GenomeCoordinates = RetrieveNCBIGenomicCoordinates.loadTextDB_resolved();
                                                    strand = entrez2GenomeCoordinates.get(entrez).get("STRAND");
									        		//start = Integer.valueOf(entrez2GenomeCoordinates.get(entrez).get("START"));
                                                    //end = Integer.valueOf(entrez2GenomeCoordinates.get(entrez).get("END"));
                                                    gene = entrez2GenomeCoordinates.get(entrez).get("NCBI_SYMBOL");
                                                } else {
                                                    RetrieveNCBIGenomicCoordinates.updateUnresolvedQueries(geneSymbol + "|NaN_" + geneSymbol);
                                                    unresolved = RetrieveNCBIGenomicCoordinates.loadTextDB_unresolved();
                                                }
                                            } else {
                                                RetrieveNCBIGenomicCoordinates.updateUnresolvedQueries(geneSymbol + "|NaN_" + geneSymbol);
                                                unresolved = RetrieveNCBIGenomicCoordinates.loadTextDB_unresolved();
                                            }
                                        }
                                        
                                        if (entrez.equals("")) {
                                            boolean found = false;
                                            
                                            //search in resolved
                                            HashMap<String, String> geneInfo = getGeneInfoInDB(geneSymbol);
                                            if (!geneInfo.isEmpty()) {
                                                entrez = geneInfo.get("NCBI_ENTREZ");
                                                if (!entrez.equals("") && !entrez.equals("null"))
                                                    found = true;
                                            }
                                            
                                            if (!found) {
                                                //search in unresolved
                                                for (String unr: unresolved) {
                                                    if (unr.startsWith(gene) || unr.startsWith(geneSymbol)) {
                                                        String[] unr_split = unr.split("\\|");
                                                        if (unr_split.length >= 2) {
                                                            if (unr_split[1].startsWith("?") || unr_split[1].startsWith("NaN"))
                                                                entrez = unr_split[1];
                                                            else
                                                                entrez = "null";
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            
                                            if (entrez.equals(""))
                                                entrez = "null";
                                        }

                                        if (start != Integer.MAX_VALUE && end != Integer.MAX_VALUE) {

                                            String chr = d[chrIndex];
                                            if (start > end) {
                                                int tmp = start;
                                                start = end;
                                                end = tmp;
                                            }
                                            String compositeElement = d[compositeElementIndex];
                                            String betaValue = d[betaValueIndex];
                                            
                                            

                                            ArrayList<String> tmpArr = new ArrayList<>();
                                            tmpArr.add("chr"+chr); tmpArr.add(String.valueOf(start)); tmpArr.add(String.valueOf(end)); tmpArr.add(strand);
                                            //String tmp = "chr" + chr + "\t" + start + "\t" + end + "\t" + strand + "\t";
                                            //String annotation = "chr"+chr + "\t" + start + "\t" + end + "\t" + strand + "\t";
                                            if (!skipIndexList.contains(0)) {
                                                if (compositeElement.trim().equals("") || compositeElement.trim().equals("NA")) {
                                                    //tmp += "null\t";
                                                    tmpArr.add("null");
                                                } else {
                                                    //tmp += compositeElement + "\t";
                                                    tmpArr.add(compositeElement);
                                                }
                                            }
                                            if (!skipIndexList.contains(1)) {
                                                if (betaValue.trim().equals("") || betaValue.trim().equals("NA")) {
                                                    //tmp += "null\t";
                                                    tmpArr.add("null");
                                                } else {
                                                    //tmp += betaValue + "\t";
                                                    tmpArr.add(betaValue);
                                                }
                                            }
                                            if (!skipIndexList.contains(2)) {
                                                if (gene.trim().equals("") || gene.trim().equals("NA")) {
                                                    //tmp += "null";
                                                    tmpArr.add("null");
                                                    //annotation += "null";
                                                } else {
                                                    //tmp += gene;
                                                    tmpArr.add(gene);
                                                    //annotation += gene;
                                                }
                                            }
                                            
                                            // add entrez info
                                            tmpArr.add(entrez);

                                            //if (!tmp.trim().equals("")) {
                                            if (!tmpArr.isEmpty()) {
                                                incrementAliquotCount(bcr_aliquot_barcode);
                                                incrementSampleCount(bcr_aliquot_barcode);
                                                incrementPatientCount(bcr_aliquot_barcode);
                                                //addAnnotation(annotation);
                                                //out.println(tmp);
                                                String tmp = OutputFormat.createOutLine(getFormat(), tmpArr, getHeader(data_type, data_subtype));
                                                out.print(tmp);
                                            }
                                        }

                                    }
                                }

                            } catch (Exception e) {
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

                        metaVals.put("manually_curated|exp_data_bed_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + bcr_aliquot_barcode + gendata_ext);
                        metaVals.put("manually_curated|exp_metadata_url", Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + bcr_aliquot_barcode + metadata_ext);
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

                        if (parser_meta_dict.containsKey("manually_curated|exp_data_bed_url")) {
                                metaDictVals = parser_meta_dict.get("manually_curated|exp_data_bed_url");
                            } else {
                                metaDictVals = new HashSet<>();
                            }
                            metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + bcr_aliquot_barcode + gendata_ext);
                            parser_meta_dict.put("manually_curated|exp_data_bed_url", metaDictVals);
                            if (parser_meta_dict.containsKey("manually_curated|exp_metadata_url")) {
                                metaDictVals = parser_meta_dict.get("manually_curated|exp_metadata_url");
                            } else {
                                metaDictVals = new HashSet<>();
                            }
                            metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + bcr_aliquot_barcode + metadata_ext);
                            parser_meta_dict.put("manually_curated|exp_metadata_url", metaDictVals);
                    } catch (IOException e) {
                    }

                    if (!patientIds.contains(bcr_aliquot_barcode)) {
                        patientIds.add(bcr_aliquot_barcode);
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

    private boolean isInUnres(String geneSymbol, HashSet<String> unresolved) {
        for (String s : unresolved) {
            String gene = s.split("\\|")[0];
            if (gene.toUpperCase().equals(geneSymbol.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private HashMap<String, String> getGeneInfoInDB(String geneSymbol) {
        if (gene2GenomeCoordinates == null) {
            if (entrez2GenomeCoordinates != null) {
                gene2GenomeCoordinates = new HashMap<>();
                for (String e : entrez2GenomeCoordinates.keySet()) {
                    if (gene2GenomeCoordinates.containsKey(entrez2GenomeCoordinates.get(e).get("TCGA_SYMBOL"))) {
                        difference_tcgaSymbolKeySet++;
                    } else {
                        gene2GenomeCoordinates.put(entrez2GenomeCoordinates.get(e).get("TCGA_SYMBOL"), entrez2GenomeCoordinates.get(e));
                    }
                }
            }
        } else if (gene2GenomeCoordinates.size() < (entrez2GenomeCoordinates.size() - difference_tcgaSymbolKeySet)) {
            gene2GenomeCoordinates = new HashMap<>();
            for (String e : entrez2GenomeCoordinates.keySet()) {
                if (gene2GenomeCoordinates.containsKey(entrez2GenomeCoordinates.get(e).get("TCGA_SYMBOL"))) {
                    difference_tcgaSymbolKeySet++;
                } else {
                    gene2GenomeCoordinates.put(entrez2GenomeCoordinates.get(e).get("TCGA_SYMBOL"), entrez2GenomeCoordinates.get(e));
                }
            }
        }
        //System.err.println(gene2GenomeCoordinates.size() + "  :  " + (entrez2GenomeCoordinates.size()-difference_tcgaSymbolKeySet));
        return gene2GenomeCoordinates.get(geneSymbol) == null ? (new HashMap<>()) : gene2GenomeCoordinates.get(geneSymbol);
    }

    @Override
    public String[] getHeader(String data_type, String data_subtype) {
        String[] header = new String[8];
        header[0] = "chr";
        header[1] = "start";
        header[2] = "stop";
        header[3] = "strand";
        header[4] = "composite_element_ref";
        header[5] = "beta_value";
        header[6] = "gene_symbol";
        header[7] = "entrez_gene_id";
        return header;
    }

    @Override
    public String[] getAttributesType(String data_type, String data_subtype) {
        String[] attr_type = new String[8];
        attr_type[0] = "STRING";
        attr_type[1] = "LONG";
        attr_type[2] = "LONG";
        attr_type[3] = "CHAR";
        attr_type[4] = "STRING";
        attr_type[5] = "FLOAT";
        attr_type[6] = "STRING";
        attr_type[7] = "STRING";
        return attr_type;
    }

}
