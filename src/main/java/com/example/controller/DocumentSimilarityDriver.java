package com.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class DocumentSimilarityDriver {

    public static void main(String[] args) throws Exception {
        // Run the document similarity job
        int exitCode = runDocumentSimilarityJob(args);
        System.exit(exitCode);
    }

    private static int runDocumentSimilarityJob(String[] args) throws Exception {
        // Configure and set up the Hadoop job
        Configuration conf = new Configuration();
        Job job = createJob(conf);

        // Set input and output paths
        setInputOutputPaths(job, args[0], args[1]);

        // Execute the job and return the exit code
        return job.waitForCompletion(true) ? 0 : 1;
    }

    private static Job createJob(Configuration conf) throws Exception {
        // Create and configure the Hadoop job
        Job job = Job.getInstance(conf, "Document Similarity");

        // Set job-specific classes
        setJobClasses(job);

        // Set output key and value types
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        return job;
    }

    private static void setJobClasses(Job job) {
        // Set the Mapper, Reducer, and Driver classes
        job.setJarByClass(DocumentSimilarityDriver.class);
        job.setMapperClass(DocumentSimilarityMapper.class);
        job.setReducerClass(DocumentSimilarityReducer.class);
    }

    private static void setInputOutputPaths(Job job, String inputPath, String outputPath) throws Exception {
        // Set the input and output paths for the job
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
    }
}