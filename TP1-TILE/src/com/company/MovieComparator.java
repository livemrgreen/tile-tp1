package com.company;


import org.apache.lucene.document.Document;

import java.util.Comparator;
import java.util.Map;

public class MovieComparator implements Comparator {
    private Map moviesToRatings;

    public MovieComparator(Map moviesToRatings){
        this.moviesToRatings = moviesToRatings;
    }

    public int compare(Object movieDocument1, Object movieDocument2){
        String movieID1 = ((Document)movieDocument1).get("movieID");
        String movieID2 = ((Document)movieDocument2).get("movieID");

        String ratingCount1 = (String)moviesToRatings.get(movieID1);
        String ratingCount2 = (String)moviesToRatings.get(movieID2);

        int intRatingCount1 = Integer.parseInt(ratingCount1);
        int intRatingCount2 = Integer.parseInt(ratingCount2);

        return (intRatingCount2 - intRatingCount1);
    }
}