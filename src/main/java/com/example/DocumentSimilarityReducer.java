package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {

    private List<String> docIds = new ArrayList<>();
    private List<HashSet<String>> wordSets = new ArrayList<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) 
            throws IOException, InterruptedException {
        // Process each value and store document ID and word set
        for (Text value : values) {
            HashSet<String> wordSet = new HashSet<>(Arrays.asList(value.toString().split(",")));
            docIds.add(key.toString());
            wordSets.add(wordSet);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // Calculate and output document similarity
        calculateAndOutputSimilarity(context);
    }

    private void calculateAndOutputSimilarity(Context context) 
            throws IOException, InterruptedException {
        int numDocs = docIds.size();

        // Iterate through all pairs of documents
        for (int i = 0; i < numDocs; i++) {
            for (int j = i + 1; j < numDocs; j++) {
                double similarity = calculateJaccardSimilarity(wordSets.get(i), wordSets.get(j));

                // Output pairs with similarity greater than 50%
                if (similarity > 0.5) {
                    String outputKey = formatOutputKey(docIds.get(i), docIds.get(j));
                    String outputValue = formatOutputValue(similarity);
                    context.write(new Text(outputKey), new Text(outputValue));
                }
            }
        }
    }

    private double calculateJaccardSimilarity(HashSet<String> set1, HashSet<String> set2) {
        // Calculate Jaccard similarity
        HashSet<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2); // Intersection of words

        HashSet<String> union = new HashSet<>(set1);
        union.addAll(set2); // Union of words

        return (double) intersection.size() / union.size();
    }

    private String formatOutputKey(String docId1, String docId2) {
        // Format the output key as (docId1, docId2)
        return "(" + docId1 + ", " + docId2 + ")";
    }

    private String formatOutputValue(double similarity) {
        // Format the output value as a percentage
        return String.format("%.2f%%", similarity * 100);
    }
}