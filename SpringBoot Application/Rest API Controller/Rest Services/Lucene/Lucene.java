package com.group1.webcrawler.controller.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Lucene {

	static Directory directory = new ByteBuffersDirectory();
	static Analyzer analyzer = new StandardAnalyzer();

	// Retrieve crawled pages for indexing
	public static Map<String, List<String>> retrieveCrawledPages(String fileName) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> list = null;

		List<String> pages = new ArrayList<>();

		try {
			pages = Files.readAllLines(new File(fileName).toPath(), Charset.defaultCharset());
			Iterator<String> it = pages.iterator();
			while (it.hasNext()) {
				String[] elements = it.next().split("@@@@@");
				if (elements.length == 3) {
					list = new ArrayList<>();
					list.add(elements[2]);
					map.put(elements[1], list);
				} else if (elements.length == 4) {
					list = new ArrayList<>();
					list.add(elements[2]);
					list.add(elements[3]);
					map.put(elements[1], list);
				}
			}
			return map;
		} catch (IOException ex) {
			System.out.println(ex);
		}
		return map;
	}

	// Indexing the crawled data
	public static String indexLucene(String[] args) {
		if (args.length <= 0) {
			System.out.println("Usage: java Lucene <data file>");
			System.exit(-1);
		}
		Map<String, List<String>> crawledPagesMap = retrieveCrawledPages(args[0]);

		try {
			// To store an index on disk, use this instead:
			FSDirectory directory = FSDirectory.open(Paths.get("src/main/java/indexedData/luceneIndex"));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter indexWriter = new IndexWriter(directory, config);

			for (Map.Entry<String, List<String>> eachMap : crawledPagesMap.entrySet()) {
				String content = "";
				String subHeader = "";
				List<String> valueList = eachMap.getValue();

				if (eachMap.getValue().size() == 2) {
					docID = eachMap.getValue(0).toString();
					content = eachMap.getValue(1).toString();
				} else if (eachMap.getValue().size() == 3) {
					title = valueList.get(0).toString();
					subHeader = valueList.get(1).toString();
					content = valueList.get(2).toString();
				}

				org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
				doc.add(new TextField("docID", eachMap.getKey(), org.apache.lucene.document.Field.Store.YES));
				doc.add(new TextField("title", title, org.apache.lucene.document.Field.Store.YES));
				doc.add(new TextField("subheading", subHeader, org.apache.lucene.document.Field.Store.YES));
				doc.add(new TextField("content", content, org.apache.lucene.document.Field.Store.YES));

				indexWriter.addDocument(doc);
			}
			indexWriter.close();

		} catch (Exception ex) {
			System.out.println(ex);
		}
		return null;
	}

	// Search function for indexed data
	public static String searchIndex(String queryInput) {
		StringBuilder result = new StringBuilder();
		try {
			System.out.println(directory);
			FSDirectory directory = FSDirectory.open(Paths.get("src/main/java/indexedData/luceneIndex"));
			DirectoryReader indexReader = DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			QueryParser parser = new QueryParser("content", analyzer);
			Query query = parser.parse(queryInput);

			System.out.println(query.toString());

			int topHitCount = 100;
			ScoreDoc[] hits = indexSearcher.search(query, topHitCount).scoreDocs;

			System.out.println(indexSearcher);
			// Iterate through the results:
			for (int rank = 0; rank < hits.length; ++rank) {
				org.apache.lucene.document.Document hitDoc = indexSearcher.doc(hits[rank].doc);
				result.append("Rank-" + (rank + 1) + " (score:" + hits[rank].score + ")");
				result.append("\"Title\": " + hitDoc.get("title") + "\n");
				result.append("\"Sub Heading\": " + hitDoc.get("subheading") + "\n");
				result.append("\"Content\": " + hitDoc.get("content").substring(0, 500) + "...(show more)\n");

				result.append("\n");
			}
			indexReader.close();
			directory.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		return result.toString();
	}
}
