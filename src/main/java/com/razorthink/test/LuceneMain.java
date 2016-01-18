package com.razorthink.test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import au.com.bytecode.opencsv.CSVReader;

public class LuceneMain {
	public static void main(String[] args) throws Exception {
		File indexDir = new File(
				System.getProperty("user.home") + File.separator + "Lucene" + File.separator + "index");

		boolean indexExists = doesIndexDirExist(indexDir);
		System.out.println(indexExists == true ? "Index exists. New one won't be created"
				: "Index doesn't exist. Creating a new one...");
		if (!indexExists) {
			createIndexForGeoData(indexDir);
			// createDummyData(indexDir, 110000);
		}
		performTestSearches(indexDir);
		// Thread.sleep(10000000);
	}

	public static void performTestSearches(File indexDir) throws Exception {
		try {
			// open index for reading and searching
			LuceneReader.getInstance().load(indexDir);
			System.out.println("Loading index from " + indexDir.getAbsolutePath() + " for reading...");
			System.out.println("Total records found in Lucene index: " + LuceneReader.getInstance().getTotalDocs());
			LuceneReader.getInstance().search("admin_name1:Coalburg | admin_name2:Coalburg | admin_name3:Coalburg", 10);
			LuceneReader.getInstance().search("admin_name1:Dallas | admin_name2:Dallas | admin_name3:Dallas", 10);
			LuceneReader.getInstance().search("country:us", 10);
			LuceneReader.getInstance().unload();
		} catch (Exception e) {
			throw e;
		}
	}

	public static void createIndexForGeoData(File indexDir) throws Exception {
		try {
			List<GeoLocation> locations = getGeoDataFromCSV("US.txt");
			// open index for writing
			System.out.println("Loading index from " + indexDir.getAbsolutePath() + " for writing...");
			LuceneWriter.getInstance().load(indexDir);
			for (GeoLocation l : locations) {
				LuceneWriter.getInstance().add(l);
			}
			LuceneWriter.getInstance().unload();
		} catch (Exception e) {
			throw e;
		}
	}

	public static boolean doesIndexDirExist(File indexDir) {
		try {
			LuceneReader.getInstance().load(indexDir);
			LuceneReader.getInstance().unload();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// public static List<GeoLocation> getGeoLocations(String file) throws
	// IOException {
	// ClassLoader classLoader = LuceneMain.class.getClassLoader();
	// File fl = new File(classLoader.getResource(file).getFile());
	// List<GeoLocation> locations = new ArrayList<GeoLocation>();
	// List<String> lines = Files.readAllLines(fl.toPath());
	// // ignore first line
	// lines.remove(0);
	// for (String line : lines) {
	// String[] params = line.split(",");
	// GeoLocation loc = new GeoLocation();
	// loc.setCity(params[3]);
	// loc.setCountry(params[0]);
	// loc.setCountryCode(params[0]);
	// loc.setLat(Double.parseDouble(params[4]));
	// loc.setLng(Double.parseDouble(params[5]));
	// loc.setState(params[2]);
	// loc.setZip(params[1]);
	// loc.setId(DigestUtils.md5Hex("" + loc.getLat() + loc.getLng()));
	// locations.add(loc);
	// }
	// return locations;
	// }

	public static void createDummyData(File indexDir, int numRecordsToGenerate) throws Exception {
		List<GeoLocation> locations = getDummyData(numRecordsToGenerate);
		LuceneWriter.getInstance().load(indexDir);
		for (GeoLocation l : locations) {
			LuceneWriter.getInstance().add(l);
		}
		LuceneWriter.getInstance().unload();
	}

	public static List<GeoLocation> getDummyData(int numRecordsToGenerate) {
		List<GeoLocation> locations = new ArrayList<GeoLocation>();
		for (int i = 0; i < numRecordsToGenerate; i++) {
			GeoLocation loc = new GeoLocation();
			String s = UUID.randomUUID().toString();
			loc.setAdminCode1(s);
			loc.setAdminCode2(s);
			loc.setAdminCode3(s);
			loc.setAdminName1(s);
			loc.setAdminName2(s);
			loc.setAdminName3(s);
			loc.setCity(s);
			loc.setCountry(s);
			loc.setCountryCode(s);
			loc.setId(s);
			loc.setLat(Double.MAX_VALUE);
			loc.setLng(Double.MAX_VALUE);
			loc.setLatLngAccuracy(Integer.MAX_VALUE);
			loc.setPlaceName(s);
			loc.setState(s);
			loc.setZip(s);
			locations.add(loc);
		}
		return locations;
	}

	public static List<GeoLocation> getGeoDataFromCSV(String file) throws Exception {
		List<GeoLocation> locations = new ArrayList<GeoLocation>();
		ClassLoader classLoader = LuceneMain.class.getClassLoader();
		File fl = new File(classLoader.getResource(file).getFile());
		@SuppressWarnings("resource")
		CSVReader reader = new CSVReader(new FileReader(fl), '\t', '"', 0);
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine != null) {
				GeoLocation loc = new GeoLocation();
				if (nextLine[0] == null || nextLine[0].trim().isEmpty()) {
					loc.setCountry("");
				} else {
					loc.setCountry(nextLine[0]);
				}
				loc.setCountryCode(loc.getCountry());
				loc.setCity("");
				loc.setState("");
				if (nextLine[9] == null || nextLine[9].trim().isEmpty()) {
					loc.setLat(0);
				} else {
					loc.setLat(Double.parseDouble(nextLine[9]));
				}
				if (nextLine[10] == null || nextLine[10].trim().isEmpty()) {
					loc.setLng(0);
				} else {
					loc.setLng(Double.parseDouble(nextLine[10]));
				}
				if (nextLine[1] == null || nextLine[1].trim().isEmpty()) {
					loc.setZip("");
				} else {
					loc.setZip(nextLine[1]);
				}
				if (nextLine[2] == null || nextLine[2].trim().isEmpty()) {
					loc.setPlaceName("");
				} else {
					loc.setPlaceName(nextLine[2]);
				}
				if (nextLine[3] == null || nextLine[3].trim().isEmpty()) {
					loc.setAdminName1("");
				} else {
					loc.setAdminName1(nextLine[3]);
				}
				if (nextLine[5] == null || nextLine[5].trim().isEmpty()) {
					loc.setAdminName2("");
				} else {
					loc.setAdminName2(nextLine[5]);
				}
				if (nextLine[7] == null || nextLine[7].trim().isEmpty()) {
					loc.setAdminName3("");
				} else {
					loc.setAdminName3(nextLine[7]);
				}
				if (nextLine[4] == null || nextLine[4].trim().isEmpty()) {
					loc.setAdminCode1("");
				} else {
					loc.setAdminCode1(nextLine[4]);
				}
				if (nextLine[6] == null || nextLine[6].trim().isEmpty()) {
					loc.setAdminCode2("");
				} else {
					loc.setAdminCode2(nextLine[6]);
				}
				if (nextLine[8] == null || nextLine[8].trim().isEmpty()) {
					loc.setAdminCode3("");
				} else {
					loc.setAdminCode3(nextLine[8]);
				}
				if (nextLine[11] == null || nextLine[11].trim().isEmpty()) {
					loc.setLatLngAccuracy(0);
				} else {
					loc.setLatLngAccuracy(Integer.parseInt(nextLine[11]));
				}
				loc.setId(DigestUtils.md5Hex("" + loc.getLat() + loc.getLng()));
				locations.add(loc);
			}
		}
		return locations;
	}
}