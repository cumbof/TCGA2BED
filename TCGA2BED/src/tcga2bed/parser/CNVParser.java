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
import tcga2bed.util.FileInputParser;
import tcga2bed.util.HTTPExpInfo;
import tcga2bed.util.MD5Checksum;
import tcga2bed.util.OutputFormat;

/**
 *
 * @author fabio
 */
public class CNVParser extends BioParser {

    @Override
    //public HashSet<String> convert(File data_dir, File meta_dir, String disease, String meta_biotab_url, String data_type, String data_subtype, String out_dir_path, String out_fileName_prefix, String gendata_ext, String metadata_ext) {
    public HashSet<String> convert(File data_dir, File meta_dir, String disease, String data_type, String data_subtype, String out_dir_path, String out_fileName_prefix, String gendata_ext, String metadata_ext) {
        HashSet<String> patientIds = new HashSet<>();
        try {
            //int sample_index = 0;
            int chr_index = 1;
            int start_index = 2;
            int end_index = 3;
            int num_probes_index = 4;
            int segment_mean_index = 5;
            
            HTTPExpInfo.initDiseaseInfo();
            File tmp_file = File.createTempFile("http_tcga", "html");
            String out_path = tmp_file.getAbsolutePath();
            String url = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase() + "_root_dir");

            skipIndexList = new ArrayList<>();
            
            /*HashMap<String, String> folder2date = HTTPExpInfo.getData2Date(url, out_path, disease, data_type, "/", "_data_dir_prefix");
            tmp_file.delete();
            String updatedFolder = HTTPExpInfo.searchForUpdate(folder2date);*/
            
            HashMap<String, HashMap<String, String>> parser_meta = new HashMap<>();
            HashMap<String, HashSet<String>> parser_meta_dict = new HashMap<>();
            String last_genome_version = "";
            for (File f : data_dir.listFiles()) {
                if (!isToSkip(f.getName()) && f.getName().toLowerCase().endsWith(".seg.txt")) {
                    String[] split = f.getName().toLowerCase().split("\\.");
                    String[] version_tmp = split[split.length-3].split("_");
                    if (version_tmp.length>1)
                        last_genome_version = version_tmp[1];
                    else last_genome_version = version_tmp[0];
                }
            }
            
            for (File f : data_dir.listFiles()) {
                if (!isToSkip(f.getName()) && f.getName().toLowerCase().contains(last_genome_version) && !f.getName().toLowerCase().contains("nocnv")) {
                    String f_nocnv_path = "";
                    String[] f_split = f.getAbsolutePath().split("\\.");
                    f_split[f_split.length-3] = "nocnv_"+last_genome_version;
                    for (int i=0; i<f_split.length-1; i++)
                        f_nocnv_path += f_split[i] + ".";
                    f_nocnv_path += f_split[f_split.length-1];
                    
                    String bcr_aliquot_barcode = FileInputParser.retrievePatientReference(f, data_type);
                    if (bcr_aliquot_barcode!=null) {
                        HashMap<String, String[]> data = FileInputParser.retrieveData(f, data_type);
                        File f_nocnv = new File(f_nocnv_path);
                        HashMap<String, String[]> nocnv_data = FileInputParser.retrieveData(f_nocnv, data_type);

                        HashMap<String, HashMap<String, String[]>> data_map = new HashMap<String, HashMap<String, String[]>>();
                        data_map.put("cnv", data);
                        data_map.put("nocnv", nocnv_data);
                        try {
                            File output = new File(out_dir_path + bcr_aliquot_barcode + gendata_ext);
                            output.createNewFile();
                            FileOutputStream fos = new FileOutputStream(output.getAbsolutePath());
                            PrintStream out = new PrintStream(fos);
                            
                            out.printf(OutputFormat.initDocument(getFormat()));

                            for (String type: data_map.keySet()) {
                                for (String k: data_map.get(type).keySet()) {
                                    try {
                                        if (!k.equals("header")) {
                                            String[] d = data_map.get(type).get(k);
                                            String chr = "chr"+d[chr_index];
                                            int start = Integer.valueOf(d[start_index]);
                                            int end = Integer.valueOf(d[end_index]);
                                            String num_probes = d[num_probes_index];
                                            String segment_mean = d[segment_mean_index];
                                            String nocnv = "N";
                                            if (type.toLowerCase().equals("nocnv"))
                                                nocnv = "Y";

                                            // strand is undefined
                                            String strand = "*";
                                            
                                            ArrayList<String> tmpArr = new ArrayList<>();
                                            tmpArr.add(chr); tmpArr.add(String.valueOf(start)); tmpArr.add(String.valueOf(end)); tmpArr.add(strand);
                                            //String tmp = chr + "\t" + start + "\t" + end + "\t" + strand + "\t";
                                            if (!skipIndexList.contains(num_probes_index)) {
                                                if (num_probes.trim().equals("") || num_probes.trim().equals("NA")) {
                                                    //tmp += "null\t";
                                                    tmpArr.add("null");
                                                } else {
                                                    //tmp += num_probes + "\t";
                                                    tmpArr.add(num_probes);
                                                }
                                            }
                                            if (!skipIndexList.contains(segment_mean_index)) {
                                                if (segment_mean.trim().equals("") || segment_mean.trim().equals("NA")) {
                                                    //tmp += "null\t";
                                                    tmpArr.add("null");
                                                } else {
                                                    //tmp += segment_mean + "\t";
                                                    tmpArr.add(segment_mean);
                                                }
                                            }
                                            //tmp += nocnv;
                                            tmpArr.add(nocnv);

                                            //if (!tmp.trim().equals("")) {
                                            if (!tmpArr.isEmpty()) {
                                                incrementAliquotCount(bcr_aliquot_barcode);
                                                incrementSampleCount(bcr_aliquot_barcode);
                                                incrementPatientCount(bcr_aliquot_barcode);
                                                //out.println(tmp);
                                                String tmp = OutputFormat.createOutLine(getFormat(), tmpArr, getHeader(data_type, data_subtype));
                                                out.print(tmp);
                                            }
                                        }
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
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
                            //metaVals.put("manually_curated|exp_nocnv_data_url", url + updatedFolder + f_nocnv.getCanonicalFile().getName());
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
                            parser_meta_dict.put("manually_curated|exp_data_url", metaDictVals);
                            if (parser_meta_dict.containsKey("manually_curated|exp_nocnv_data_url")) {
                                metaDictVals = parser_meta_dict.get("manually_curated|exp_nocnv_data_url");
                            } else {
                                metaDictVals = new HashSet<>();
                            }
                            metaDictVals.add(url + updatedFolder + f_nocnv.getCanonicalFile().getName());
                            parser_meta_dict.put("manually_curated|exp_nocnv_data_url", metaDictVals);*/
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
                            metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + bcr_aliquot_barcode + gendata_ext);
                            parser_meta_dict.put("manually_curated|exp_data_bed_url", metaDictVals);
                            if (parser_meta_dict.containsKey("manually_curated|exp_metadata_url")) {
                                metaDictVals = parser_meta_dict.get("manually_curated|exp_metadata_url");
                            } else {
                                metaDictVals = new HashSet<>();
                            }
                            metaDictVals.add(Settings.getFTPBedRepositoryURL() + disease.toLowerCase() + "/" + data_type.toLowerCase() + "/" + bcr_aliquot_barcode + metadata_ext);
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
        String[] header = new String[7];
        header[0] = "chr";
        header[1] = "start";
        header[2] = "stop";
        header[3] = "strand";
        header[4] = "Num_Probes";
        header[5] = "Segment_Mean";
        header[6] = "is_nocnv";
        return header;
    }

    @Override
    public String[] getAttributesType(String data_type, String data_subtype) {
        String[] attr_type = new String[7];
        attr_type[0] = "STRING";
        attr_type[1] = "LONG";
        attr_type[2] = "LONG";
        attr_type[3] = "CHAR";
        attr_type[4] = "INTEGER";
        attr_type[5] = "FLOAT";
        attr_type[6] = "STRING";
        return attr_type;
    }
    
}
