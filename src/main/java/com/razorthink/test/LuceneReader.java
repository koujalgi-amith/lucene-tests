package com.razorthink.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

import com.google.gson.GsonBuilder;

public class LuceneReader {
	private static LuceneReader instance;
	private Analyzer analyzer = new StandardAnalyzer();
	private IndexReader reader;
	private IndexSearcher searcher;

	private LuceneReader() {
	}

	public static synchronized LuceneReader getInstance() {
		if (instance == null) {
			instance = new LuceneReader();
		}
		return instance;
	}

	public void load(File indexDir) throws Exception {
		RAMDirectory index = new RAMDirectory(FSDirectory.open(indexDir.toPath()), IOContext.READ);
		try {
			reader = DirectoryReader.open(index);
		} catch (Exception e) {
			if (e instanceof org.apache.lucene.index.IndexNotFoundException) {
				throw new Exception("Index directory not found at " + indexDir);
			}
		}
		searcher = new IndexSearcher(reader);
	}

	public void unload() throws IOException {
		reader.close();
	}

	public int getTotalDocs() {
		return reader.maxDoc();
	}

	public List<GeoLocation> search(String query, int maxDocsToReturn) throws Exception {
		Query q = new QueryParser("city", analyzer).parse(query);
		TopDocs docs = searcher.search(q, maxDocsToReturn);
		ScoreDoc[] hits = docs.scoreDocs;
		System.out.println("Found " + hits.length + " hits for query text: \"" + query + "\"");
		List<GeoLocation> locations = new ArrayList<GeoLocation>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			GeoLocation loc = new GeoLocation();
			loc.setCity(d.get("city"));
			loc.setCountry(d.get("country"));
			loc.setCountryCode(d.get("country_code"));
			loc.setLat(Double.parseDouble(d.get("lat")));
			loc.setLng(Double.parseDouble(d.get("lng")));
			loc.setState(d.get("state"));
			loc.setZip(d.get("zip"));
			loc.setPlaceName(d.get("place_name"));

			loc.setAdminCode1(d.get("admin_code1"));
			loc.setAdminCode2(d.get("admin_code2"));
			loc.setAdminCode3(d.get("admin_code3"));

			loc.setAdminName1(d.get("admin_name1"));
			loc.setAdminName2(d.get("admin_name2"));
			loc.setAdminName3(d.get("admin_name3"));
			loc.setId(d.get("id"));
			locations.add(loc);
		}
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(locations));
		return locations;
	}

}