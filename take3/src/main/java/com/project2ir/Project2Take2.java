package com.project2ir;
import java.util.*;
import java.io.*;
import org.apache.lucene.search.similarities.ClassicSimilarity;
public class Project2Take2 {
    
    public static void main(String args[]){
        HashMap<Integer, String>DocIdAndTitle = new HashMap<Integer, String>();
        HashMap<Integer, String>DocIdAndContent = new HashMap<Integer,String>();
        //Map<Integer,Map<String, Integer>>TermFreqInDocument = new HashMap<Integer,Map<String, Integer>>();
        Map<String,Integer> TermFreqInCorpus = new HashMap<String, Integer>();
        Map<Integer,Map<String,Double>> TermFrequency = new HashMap<Integer,Map<String,Double>>();
        buildNumber(DocIdAndTitle);
        buildContent(DocIdAndContent);
        buildFrequencyinCorpus(TermFreqInCorpus);
        TermFreq(DocIdAndContent, TermFrequency);
        Map<Integer, Map<String, Double>> tfidfScore = getScores(TermFrequency);
        Map<Integer, String> queries = getTheQueries();
        List<Integer> queryIDs = new ArrayList<>(queries.keySet());
        Collections.shuffle(queryIDs);
        int numQueries = Math.min(20, queryIDs.size());
        for (int i = 0; i < numQueries; i++) {
        int queryID = queryIDs.get(i);
        String query = queries.get(queryID);
        System.out.println(".I " + queryID);
        System.out.println(query);
        List<Integer> relDocs = QuerySearch(query, tfidfScore);
        System.out.println("Top relevant documents for the query:");
        for (int docId : relDocs) {
            System.out.println("Document ID: " + docId);
        }
        System.out.println();
    }



    }
    private static HashMap<Integer,String> buildNumber(HashMap<Integer,String> DocIdAndTitle){
        int docID = 0;
        String title = "";
        try {
            File myObj = new File("cran-1.all.1400");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
              String data = myReader.nextLine();
              if(data.contains(".I")){
                data = data.substring(2).trim();
                docID =  Integer.parseInt(data);
               // System.out.println(docID);
                
              }
              if (data.contains(".T")) {
                title = "";
                data = myReader.nextLine();
                while (!data.contains(".A")) {
                    title += data + " ";
                    data = myReader.nextLine();
                }
                //System.out.println(title);
                DocIdAndTitle.put(docID, title.trim());
            }
              
            }
            myReader.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
          for (int i : DocIdAndTitle.keySet()) {
            System.out.println("key: " + i + " value: " + DocIdAndTitle.get(i));
          }
    
        return DocIdAndTitle;
    }
    


    private static HashMap<Integer, String> buildContent(HashMap<Integer, String> DocIdAndContent) {
        int docID = 0;
        String content = "";
    
        try {
            File filename = new File("cran-1.all.1400");
            Scanner FileRead = new Scanner(filename);
    
            while (FileRead.hasNextLine()) {
                String data = FileRead.nextLine();
                switch (data.substring(0,2)) {
                    case ".I":
                        docID = Integer.parseInt(data.substring(3).trim());
                        break;
                    case ".W":
                        content = "";
                        while (FileRead.hasNextLine()) {
                            data = FileRead.nextLine();
                            if (data.startsWith(".I")) {
                                DocIdAndContent.put(docID, content.trim());
                                docID = Integer.parseInt(data.substring(3).trim());
                                content = "";
                                break;
                            }
                            content += data + " ";
                        }
                        if (!content.isEmpty()) {
                            DocIdAndContent.put(docID, content.trim());
                        }
                        break;
                    default:
                        break;
                }

            }
            FileRead.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        for(int i:DocIdAndContent.keySet()){
        System.out.println("key:"+i + " value: " + DocIdAndContent.get(i)+ "\n");
        }
    
        return DocIdAndContent;
    }

    private static Map<String, Integer> buildFrequencyinCorpus(Map<String, Integer> TermFreqInCorpus) {
        String word = "";
        
    
        try {
            File filename = new File("cran-1.all.1400");
            Scanner FileRead = new Scanner(filename);
    
            while (FileRead.hasNextLine()) {
                StringTokenizer tokenizer = new StringTokenizer(FileRead.nextLine());
    
                while (tokenizer.hasMoreTokens()) {
                    word = tokenizer.nextToken();
    
                    switch (word) {
                        case ".A":
                        case ".I":
                        case ".W":
                        case ".B":
                        case ".T":
                            continue;
                        default:
                            if (!TermFreqInCorpus.containsKey(word)) {
                                TermFreqInCorpus.put(word, 1);
                            } else {
                                TermFreqInCorpus.put(word, TermFreqInCorpus.get(word) + 1);
                            }
                            break;
                    }
                }
            }
    
            FileRead.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    /** 
        for (String term : TermFreqInCorpus.keySet()) {
            System.out.println(term + ":" + TermFreqInCorpus.get(term));
        }
    */  
        return TermFreqInCorpus;
    }
    /**
    *private static Integer wordCount(Map<String,Integer> TermFreqInCorpus){
    *   int TotalCount = 0; 
    *   for(String i:TermFreqInCorpus.keySet()){
    *       TotalCount+=TermFreqInCorpus.get(i);
    *   }
    *   return TotalCount;
    *}
    **/
    private static Map<Integer, Map<String, Double>> TermFreq(HashMap<Integer, String> DocIdAndContent, Map<Integer, Map<String, Double>> TermFrequency) {
        for (int i : DocIdAndContent.keySet()) {
            String words = DocIdAndContent.get(i);
            Map<String, Double> termFreqMap = new HashMap<>();
            StringTokenizer tokenizer = new StringTokenizer(words);
            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken().toLowerCase();
                if (word.equals(".a") || word.equals(".i") || word.equals(".w") || word.equals(".b") || word.equals(".t")) {
                    continue;
                }
                termFreqMap.put(word, termFreqMap.getOrDefault(word, 0.0) + 1);
            }
            TermFrequency.put(i, termFreqMap);
        }
        return TermFrequency;        
    }
    
    private static Map<Integer, Map<String, Double>> getScores(Map<Integer, Map<String, Double>> TermFrequency) {
        ClassicSimilarity similarity = new ClassicSimilarity();
        Map<Integer, Map<String, Double>> tfidfScore = new HashMap<>();
        for (int i : TermFrequency.keySet()) {
            Map<String, Double> termFreqMap = TermFrequency.get(i);
            Map<String, Double> tfid = new HashMap<>();
            for (String word : termFreqMap.keySet()) {
                int termFreq = termFreqMap.get(word).intValue();
                int docFreq = getDocFreq(TermFrequency, word);
                int numDocs = TermFrequency.size();
                double tfidf = similarity.tf(termFreq) * similarity.idf(docFreq, numDocs);
                tfid.put(word, tfidf);
            }       
            tfidfScore.put(i, tfid);
        }
        return tfidfScore;
    }
    private static int getDocFreq(Map<Integer, Map<String, Double>> termFrequency, String word) {
        int docFreq = 0;
        for (Map<String, Double> termFreqMap : termFrequency.values()) {
            if (termFreqMap.containsKey(word)) {
                docFreq++;
            }
        }
        return docFreq;
    }
    
    private static List<Integer> QuerySearch(String query, Map<Integer, Map<String, Double>> tfidfScore) {
        String[] queried = query.toLowerCase().split("\\s+");
        Map<String, Double> calcQuery = new HashMap<>();
        Map<Integer, Double> Scores = new HashMap<>();
        int returnNum = 10;
        
        for (String word : queried) {
            int docFreq = getDocFreq(tfidfScore, word);
            int numDocs = tfidfScore.size();
            double idf = Math.log((double) numDocs / (docFreq + 1));
            calcQuery.put(word, idf);
        }
        
        for (int docID : tfidfScore.keySet()) {
            double simScore = calcSim(calcQuery, tfidfScore.get(docID));
            Scores.put(docID, simScore);
        }
        
        List<Integer> RelDocs = new ArrayList<>(Scores.keySet());
        Collections.sort(RelDocs, (a, b) -> Double.compare(Scores.get(b), Scores.get(a)));
        //System.out.println(RelDocs.subList(0, Math.min(returnNum, RelDocs.size())));
        return RelDocs.subList(0, Math.min(returnNum, RelDocs.size()));
    }
    private static double calcSim(Map<String, Double> queried, Map<String, Double> Scores) {
        double dotProd = 0.0;
        double queryNorm = 0.0;
        double docNorm = 0.0;
        
        for (String term : queried.keySet()) {
            if (Scores.containsKey(term)) {
                dotProd += Scores.get(term) * queried.get(term);
            }
            queryNorm += Math.pow(queried.get(term), 2);
        }
        
        for (double number : Scores.values()) {
            docNorm += Math.pow(number, 2);
        }
        
        return dotProd / (Math.sqrt(queryNorm) * Math.sqrt(docNorm));

}
private static Map<Integer, String> getTheQueries() {
    String filename = "cran.qry";
    Map<Integer, String> queries = new HashMap<>();
    int queryID = 0;
    String query = "";

    try {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.startsWith(".I")) {
                if (!query.isEmpty()) {
                    queries.put(queryID, query.trim());
                    query = "";  // Reset query for the next one
                }
                queryID = Integer.parseInt(line.substring(3).trim());
            } else if (line.startsWith(".W")) {
                query = "";
                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if (line.startsWith(".I")) {
                        queries.put(queryID, query.trim());
                        queryID = Integer.parseInt(line.substring(3).trim());
                        query = "";
                        break;  // Break to start processing the next query
                    } else {
                        query += line + " ";
                    }
                }
                queries.put(queryID, query.trim());
            }
        }

        // Add the last query if there's any
        if (!query.isEmpty()) {
            queries.put(queryID, query.trim());
        }

        scanner.close();
    } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }

    return queries;
}

}

