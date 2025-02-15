
# Document Similarity Using Hadoop MapReduce

## Objective

This project computes the Jaccard Similarity between document pairs using MapReduce in Hadoop. The program:

- Extracts words from multiple text documents.
- Identifies common words across documents.
- Computes the Jaccard Similarity for each document pair.
- Outputs document pairs with similarity above 50%.

## Example Input

Given multiple text documents, the program calculates similarity based on unique words.

### Example Documents:

- `doc1.txt`:the quick brown fox jumps over the lazy dog
- `doc2.txt`: the quick fox jumps high over the lazy cat
- `doc3.txt`: a fast brown dog leaps over a sleeping fox

### Jaccard Similarity Formula

The Jaccard Similarity between two documents A and B is calculated as:

\[
J(A, B) = rac{|A \cap B|}{|A \cup B|}
\]

Where:
- |A ∩ B| = Number of common words in both documents
- |A ∪ B| = Total number of unique words in both documents

### Example Calculation:

For `doc1.txt` and `doc2.txt`:

- Common words: {hadoop, is} → 2 words
- Total unique words: {hadoop, is, a, distributed, system, used, for, big, data, processing} → 10 words

Jaccard Similarity = 2/10 = 0.2 (20%)

## Approach and Implementation

### Mapper Function

- Reads input documents line by line.
- Tokenizes words and assigns each word to the document it belongs to.
- Emits intermediate key-value pairs as (word, document_id).

### Reducer Function

- Receives words as keys with associated document lists as values.
- Constructs document pairs that share common words.
- Computes Jaccard Similarity for each document pair.
- Filters results to include only pairs with similarity above 50%.

## Environment Setup: Running Hadoop in Docker

### Step 1: Install Docker & Docker Compose

- Windows: Install Docker Desktop and enable WSL 2 backend.
- MacOS/Linux: Install Docker from the official guide.

### Step 2: Start the Hadoop Cluster

Navigate to your project directory where `docker-compose.yml` is located and run:

```bash
docker-compose up -d
```

This starts NameNode, DataNode, and ResourceManager services.

### Step 3: Access the Hadoop Container

```bash
docker exec -it hadoop-master /bin/bash
```

## Building and Running the MapReduce Job with Maven

### Step 1: Build the JAR File

```bash
mvn clean package
```

This generates a JAR file inside the `target/` directory.

### Step 2: Copy the JAR File to Hadoop Container

```bash
docker cp target/similarity.jar hadoop-master:/opt/hadoop-3.2.1/share/hadoop/mapreduce/similarity.jar
```

## Uploading Data to HDFS

### Step 1: Create an Input Directory in HDFS

```bash
hdfs dfs -mkdir -p /input/dataset
```

### Step 2: Upload Dataset to HDFS

```bash
hdfs dfs -put /opt/hadoop-3.2.1/share/hadoop/mapreduce/doc*.txt /input/dataset/
```

### Step 3: Verify the Uploaded Files

```bash
hdfs dfs -ls /input/dataset
```

## Running the MapReduce Job

Run the MapReduce job inside the container:

```bash
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/similarity.jar DocumentSimilarityDriver /input/dataset /output_similarity /output_final
```

## Retrieving the Output

To view the results stored in HDFS:

```bash
hdfs dfs -cat /output_final/part-r-00000
```

To download the output to your local machine:

```bash
hdfs dfs -get /output_final /path/to/local/output
```

## Challenges Faced and Solutions

1. **Missing Input Directory in HDFS**
   - Issue: `/input/dataset` directory was missing.
   - Solution: Ensured its creation before running the job:
     ```bash
     hdfs dfs -mkdir -p /input/dataset
     ```

2. **Incorrect File Paths**
   - Issue: The dataset was not being found by Hadoop.
   - Solution: Used absolute paths within HDFS and container:
     ```bash
     hdfs dfs -put /opt/hadoop-3.2.1/share/hadoop/mapreduce/doc*.txt /input/dataset/
     ```

3. **Understanding Jaccard Similarity Calculation**
   - Issue: Difficulty in ensuring correct computation in Reducer.
   - Solution: Debugged using print statements and tested with small datasets first.
