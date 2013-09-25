package com.company;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;

// Indexe et recherche UN document
public class Main {

    public static void main(String[] args) {
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
        Directory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);

        IndexWriter w;
        try {
            w = new IndexWriter(index, config);

            Field starWars = new Field("title", "star wars", TextField.TYPE_STORED);

            Document document = new Document();
            document.add(starWars);

            w.addDocument(document);

            w.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        IndexReader reader = null;
        try {
            reader = IndexReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser =  new QueryParser(Version.LUCENE_40, "title", analyzer);
            Query query = parser.parse("indiana jones");
            ScoreDoc[] hits = searcher.search(query, null, 10).scoreDocs;
            Document hitDocument = searcher.doc(hits[0].doc);
            System.out.println(hitDocument.get("title"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
