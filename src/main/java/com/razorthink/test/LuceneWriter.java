package com.razorthink.test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneWriter {
	private static LuceneWriter instance;

	private LuceneWriter() {
	}

	public static LuceneWriter getInstance() {
		if (instance == null) {
			instance = new LuceneWriter();
		}
		return instance;
	}

	private Analyzer analyzer = new StandardAnalyzer();
	// private Version v = org.apache.lucene.util.Version.LATEST;
	private IndexWriter w;

	public void load(File indexDir) throws IOException {
		Directory index = FSDirectory.open(indexDir.toPath());
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		w = new IndexWriter(index, config);
	}

	public void add(GeoLocation loc) throws Exception {
		Document doc = new Document();
		// use a string field if you don't want it tokenized
		doc.add(new TextField("id", loc.getId(), Field.Store.YES));
		doc.add(new TextField("place_name", loc.getPlaceName(), Field.Store.YES));
		doc.add(new TextField("city", loc.getCity(), Field.Store.YES));
		doc.add(new TextField("state", loc.getState(), Field.Store.YES));
		doc.add(new TextField("country", loc.getCountry(), Field.Store.YES));
		doc.add(new TextField("zip", loc.getZip(), Field.Store.YES));
		doc.add(new TextField("country_code", loc.getCountryCode(), Field.Store.YES));
		doc.add(new DoubleField("lat", loc.getLat(), Field.Store.YES));
		doc.add(new DoubleField("lng", loc.getLng(), Field.Store.YES));
		doc.add(new IntField("lat_lng_accuracy", loc.getLatLngAccuracy(), Field.Store.YES));
		doc.add(new TextField("admin_name1", loc.getAdminName1(), Field.Store.YES));
		doc.add(new TextField("admin_name2", loc.getAdminName2(), Field.Store.YES));
		doc.add(new TextField("admin_name3", loc.getAdminName3(), Field.Store.YES));
		doc.add(new TextField("admin_code1", loc.getAdminCode1(), Field.Store.YES));
		doc.add(new TextField("admin_code2", loc.getAdminCode2(), Field.Store.YES));
		doc.add(new TextField("admin_code3", loc.getAdminCode3(), Field.Store.YES));

		w.addDocument(doc);
	}

	public void unload() throws IOException {
		w.close();
	}
}