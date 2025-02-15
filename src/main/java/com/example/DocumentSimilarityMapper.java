package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {

    @Override
    protected void map(Object key, Text value, Context context) 
            throws IOException, InterruptedException {
        // Process the input value
        processInput(value, context);
    }

    private void processInput(Text value, Context context) 
            throws IOException, InterruptedException {
        // Split the input line into document ID and content
        String[] parts = splitInput(value.toString());
        if (parts.length < 2) return;

        String documentId = extractDocumentId(parts);
        String content = extractContent(parts);

        // Extract unique words from the content
        HashSet<String> wordSet = extractUniqueWords(content);

        // Emit the document ID and the word set as a single string
        emitOutput(documentId, wordSet, context);
    }

    private String[] splitInput(String input) {
        // Split the input line into document ID and content
        return input.split("\\s+", 2);
    }

    private String extractDocumentId(String[] parts) {
        // Extract the document ID from the split parts
        return parts[0];
    }

    private String extractContent(String[] parts) {
        // Extract the content from the split parts
        return parts[1];
    }

    private HashSet<String> extractUniqueWords(String content) {
        // Tokenize the content and store unique words in a HashSet
        HashSet<String> wordSet = new HashSet<>();
        StringTokenizer tokenizer = new StringTokenizer(content);
        while (tokenizer.hasMoreTokens()) {
            wordSet.add(tokenizer.nextToken().toLowerCase());
        }
        return wordSet;
    }

    private void emitOutput(String documentId, HashSet<String> wordSet, Context context) 
            throws IOException, InterruptedException {
        // Emit the document ID and the word set as a single string
        context.write(new Text(documentId), new Text(String.join(",", wordSet)));
    }
}