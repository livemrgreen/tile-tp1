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
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import sun.jvm.hotspot.memory.Dictionary;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {

        /*********************************************** INDEXATION ***************************************************/

        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
        Directory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);

        IndexWriter indexWriter;
        String inputFilePath = null;
        String currentLine = null;
        String currentTitle = null;
        String currentMovieID = null;
        String currentGenre = null;
        try {
            indexWriter = new IndexWriter(index, config);

            inputFilePath = "/Users/yannick/Dropbox/Polytech/IG5/TILE/TP_IG5/movies.dat";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFilePath));

            Field titleField = null;
            while ((currentLine = bufferedReader.readLine()) != null) {
                String splittedLine[] = currentLine.split("::");
                currentMovieID = splittedLine[0];
                currentTitle = splittedLine[1];
                currentGenre = splittedLine[2];

                titleField = new Field("title", currentTitle, TextField.TYPE_STORED);
                Field genreField = new Field("genre", currentGenre, TextField.TYPE_STORED);
                Field movieIDField = new Field("movieID", currentMovieID, TextField.TYPE_STORED);

                Document currentDocument = new Document();
                currentDocument.add(titleField);
                currentDocument.add(genreField);
                currentDocument.add(movieIDField);

                indexWriter.addDocument(currentDocument);
            }

            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        /************************************************ RATINGS *****************************************************/

        inputFilePath = "/Users/yannick/Dropbox/Polytech/IG5/TILE/TP1/ml-10M100K/ratings.dat";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFilePath));

        Map moviesToBestRatingsCount = new HashMap();

        String movieID = null;
        String currentRating = null;

        while ((currentLine = bufferedReader.readLine()) != null) {
            String splittedLine[] = currentLine.split("::");

            movieID = splittedLine[1];

            currentRating = splittedLine[2];
            float currentFloatRating = Float.parseFloat(currentRating);

            boolean ratingsShouldBeUpdated = (currentFloatRating > 3.5);
            if (ratingsShouldBeUpdated) {
                String updatedCount = null;

                String matchedCount = (String)moviesToBestRatingsCount.get(movieID);
                if (matchedCount != null) {
                    int currentCount = Integer.parseInt(matchedCount);

                    updatedCount = String.valueOf((currentCount + 1));
                } else {
                    updatedCount = String.valueOf(1);
                }
                moviesToBestRatingsCount.put(movieID, updatedCount);
            }
        }


        /*********************************************** SPELLCHECK ****************************************************/

        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Indexation done, type your query:");
        String userInput = bufferedReader.readLine();

        IndexReader reader = null;
        try {
            reader = IndexReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser =  new QueryParser(Version.LUCENE_40, "title", analyzer);
            Query query = parser.parse(userInput);
            ScoreDoc[] hits = searcher.search(query, null, 10).scoreDocs;

            int sizeOfHits = hits.length;

            SpellChecker spellChecker = new SpellChecker(index);
            LuceneDictionary dictionary = new LuceneDictionary(reader, "title");
            spellChecker.indexDictionary(dictionary, config, false);

            String[] similarWords = null;
            String  suggestion = null;

            while (sizeOfHits == 0) {
                similarWords = spellChecker.suggestSimilar(userInput, 1);

                if (similarWords.length != 0) {
                    suggestion = similarWords[0];
                    System.out.println("Did you mean: " + suggestion + "?");
                } else {
                    System.out.println("Not suggestions found, type new query:");
                }

                userInput = bufferedReader.readLine();

                parser =  new QueryParser(Version.LUCENE_40, "title", analyzer);
                query = parser.parse(userInput);
                hits = searcher.search(query, null, 10).scoreDocs;

                sizeOfHits = hits.length;
            }

            ArrayList hitsArrayList = new ArrayList();
            for (int i = 0; i < sizeOfHits; i++) {
                Document hitDocument = searcher.doc(hits[i].doc);

                hitsArrayList.add(hitDocument);
            }

            Collections.sort(hitsArrayList , new MovieComparator(moviesToBestRatingsCount));

            for (int i = 0; i < sizeOfHits; i++) {
                Document hitDocument = (Document)hitsArrayList.get(i);

                currentTitle = hitDocument.get("title");
                currentMovieID = hitDocument.get("movieID");
                currentRating = (String)moviesToBestRatingsCount.get(currentMovieID);

                System.out.println(currentTitle);
                System.out.println(currentRating);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
