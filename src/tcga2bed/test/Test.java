/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.test;

public class Test {
	
	public static void main(String[] args) {
		/** checkUUID("C:/Users/Fabio/Desktop/gendata2020/", new HashMap<String, HashSet<String>>()); */
		/** checkUpdates(); **/
		/**downloadMeta(); **/
	}
	
	/**
	public static void downloadMeta() {
		String[] diseases = new String[33];
		diseases[0] = "ACC";
		diseases[1] = "BLCA";
		diseases[2] = "BRCA";
		diseases[3] = "CESC";
		diseases[4] = "CHOL";
		diseases[5] = "COAD";
		diseases[6] = "DLBC";
		diseases[7] = "ESCA";
		diseases[8] = "GBM";
		diseases[9] = "HNSC";
		diseases[10] = "KICH";
		diseases[11] = "KIRC";
		diseases[12] = "KIRP";
		diseases[13] = "LAML";
		diseases[14] = "LGG";
		diseases[15] = "LIHC";
		diseases[16] = "LUAD";
		diseases[17] = "LUSC";
		diseases[18] = "MESO";
		diseases[19] = "OV";
		diseases[20] = "PAAD";
		diseases[21] = "PCPG";
		diseases[22] = "PRAD";
		diseases[23] = "READ";
		diseases[24] = "SARC";
		diseases[25] = "SKCM";
		diseases[26] = "STAD";
		diseases[27] = "TGCT";
		diseases[28] = "THCA";
		diseases[29] = "THYM";
		diseases[30] = "UCEC";
		diseases[31] = "UCS";
		diseases[32] = "UVM";
		
		for (String disease: diseases) {
			Action dmeta = new DownloadTCGAMetaAction();
			String[] args = new String[4];
			args[0] = "downloadmeta";
			args[1] = disease;
			args[2] = "C";
			args[3] = "d:/meta/";
			dmeta.execute(args);
		}	
	}
	**/
	
	/**
	public static void checkUpdates() {
		HashMap<String, String> diseaseDT2folderDate = new HashMap<String, String>();
		HTTPExpInfo.initDiseaseInfo();
		
		String[] diseases = new String[33];
		diseases[0] = "acc";
		diseases[1] = "blca";
		diseases[2] = "brca";
		diseases[3] = "cesc";
		diseases[4] = "chol";
		diseases[5] = "coad";
		diseases[6] = "dlbc";
		diseases[7] = "esca";
		diseases[8] = "gbm";
		diseases[9] = "hnsc";
		diseases[10] = "kich";
		diseases[11] = "kirc";
		diseases[12] = "kirp";
		diseases[13] = "laml";
		diseases[14] = "lgg";
		diseases[15] = "lihc";
		diseases[16] = "luad";
		diseases[17] = "lusc";
		diseases[18] = "meso";
		diseases[19] = "ov";
		diseases[20] = "paad";
		diseases[21] = "pcpg";
		diseases[22] = "prad";
		diseases[23] = "read";
		diseases[24] = "sarc";
		diseases[25] = "skcm";
		diseases[26] = "stad";
		diseases[27] = "tgct";
		diseases[28] = "thca";
		diseases[29] = "thym";
		diseases[30] = "ucec";
		diseases[31] = "ucs";
		diseases[32] = "uvm";	
		
		String[] data_types = new String[3];
		data_types[0] = "dnaseq";
		data_types[1] = "dnamethylation";
		data_types[2] = "rnaseq";
		
		for (String disease: diseases) {
			System.err.println(disease);
			for (String data_type: data_types) {
				System.err.println("\t"+data_type);
				try {
					File tmp = File.createTempFile("http_tcga", "html");
					String out_path = tmp.getAbsolutePath();
					String url = HTTPExpInfo.getDiseaseInfo().get(disease.toLowerCase()).get(data_type.toLowerCase()+"_root_dir");
					HashMap<String, String> folder2date = HTTPExpInfo.getData2Date(url, out_path, disease, data_type, "/");
					tmp.delete();
					String updatedFolder = HTTPExpInfo.searchForUpdate(folder2date);
					diseaseDT2folderDate.put(disease+":"+data_type, updatedFolder+":"+folder2date.get(updatedFolder));
				} 
				catch (Exception e) { 
					e.printStackTrace();
				}
			}
		}
		
		System.err.println("size: " + diseaseDT2folderDate.size());
		for (String diseaseDT: diseaseDT2folderDate.keySet())
			System.err.println(diseaseDT + "  =  " + diseaseDT2folderDate.get(diseaseDT));
	}
	**/
	
	
	/** check uuid 
	 
	public static void checkUUID(String currentDir, HashMap<String, HashSet<String>> folder2uuids) {
		File[] files = (new File(currentDir)).listFiles();
		for (File f: files) {
			if (f.isDirectory())
				checkUUID(f.getAbsolutePath(), folder2uuids);
			else {
				if (currentDir.toLowerCase().contains("gendata")) {
					if (f.getName().endsWith(".meta")) {
						//System.err.println(f.getName());
						String uuid = getSampleUUID(f);
						HashSet<String> uuids = new HashSet<String>();
						if (folder2uuids.containsKey(currentDir))
							uuids = folder2uuids.get(currentDir);
						uuids.add(uuid);
						folder2uuids.put(currentDir, uuids);
					}
				}
			}
		}
	
		//System.err.println("\n\n#################################################\n\n");
		//System.err.println("size: " + folder2uuids.size());
		//for (String path: folder2uuids.keySet()) {
			//System.err.println(path + " : " + folder2uuids.get(path).size());
		//}
	
		HashMap<Integer, HashSet<String>> count2uuids = countUUID(folder2uuids);
		System.err.println("\n\n#################################################\n\n");
		System.err.println("size: " + count2uuids.size());
		boolean g_one = false; 
		for (int count: count2uuids.keySet()) {
			if (count>1) {
				System.err.println(count + " : " + count2uuids.get(count));
				g_one = true;
			}
		}
		if (!g_one)
			System.err.println("no UUID duplicates found");
		
	}
	
	private static HashMap<Integer, HashSet<String>> countUUID(HashMap<String, HashSet<String>> folder2uuids) {
		
		//folder2uuids = new HashMap<String, ArrayList<String>>();
		//ArrayList<String> temp = new ArrayList<String>();
		//temp.add("01");
		//temp.add("02");
		//temp.add("03");
		//folder2uuids.put("f1", temp);
		//temp = new ArrayList<String>();
		//temp.add("04");
		//temp.add("02");
		//temp.add("05");
		//folder2uuids.put("f2", temp);
		//temp = new ArrayList<String>();
		//temp.add("06");
		//temp.add("07");
		//temp.add("08");
		//folder2uuids.put("f3", temp);
		//temp = new ArrayList<String>();
		//temp.add("04");
		//temp.add("02");
		//temp.add("09");
		//folder2uuids.put("f4", temp);
		
		HashMap<Integer, HashSet<String>> count2uuids = new HashMap<Integer, HashSet<String>>();
		for (String folder: folder2uuids.keySet()) {
			HashSet<String> uuids = folder2uuids.get(folder);
			for (String uuid: uuids) {
				//System.err.println("br0: " + uuid);
				
				if (count2uuids.isEmpty()) {
					HashSet<String> tmp = new HashSet<String>();
					tmp.add(uuid);
					count2uuids.put(1, tmp);
					//System.err.println("br1: 1 + " + tmp);
				}
				else {
					boolean added = false;
					@SuppressWarnings("unchecked")
					HashMap<Integer, ArrayList<String>> clone = (HashMap<Integer, ArrayList<String>>) count2uuids.clone(); 
					for (int count: clone.keySet()) {
						if (count2uuids.get(count).contains(uuid)) {
							//if (!uuid.equals("_NULL_"))
								//System.err.println(uuid + " --------> " + folder);
							HashSet<String> tmp = count2uuids.get(count);
							tmp.remove(uuid);
							if (!tmp.isEmpty()) {
								count2uuids.put(count, tmp);
								//System.err.println("br2: "+count+" + " + tmp);
							}
							else
								count2uuids.remove(count);
							int next_count = count+1;
							if (count2uuids.containsKey(next_count)) {
								HashSet<String> tmp2 = count2uuids.get(next_count);
								tmp2.add(uuid);
								count2uuids.put(next_count, tmp2);
								//System.err.println("br3: "+next_count+" + " + tmp2);
								added = true;
							}
							else {
								HashSet<String> new_tmp = new HashSet<String>();
								new_tmp.add(uuid);
								count2uuids.put(next_count, new_tmp);
								//System.err.println("br4: "+next_count+" + " + new_tmp);
								added = true;
							}
							break;
						}
					}
					if (!added) {
						HashSet<String> new_tmp = new HashSet<String>(); 
						if (count2uuids.containsKey(1))
							new_tmp = count2uuids.get(1);
						new_tmp.add(uuid);
						count2uuids.put(1, new_tmp);
						//System.err.println("br3: 1 + " + new_tmp);
					}
				}
				
			}
		}
		return count2uuids;
	}

	public static String getSampleUUID(File meta) {
		try {
			InputStream fstream = new FileInputStream(meta.getAbsolutePath());
	        DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String line;
	        while ((line = br.readLine()) != null) {
	        	if (line.toLowerCase().startsWith("bcr_sample_uuid")) {
	        		String uuid = line.split("\t")[1].toLowerCase();
	        		br.close();
	        		in.close();
	        		fstream.close();
	        		return uuid;
	        	}
	        }
	        br.close();
	        in.close();
	        fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "_NULL_";
	}
	**/
	
} 
