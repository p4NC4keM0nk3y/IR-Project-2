import java.util.*;
import java.io.*;
/**
 * TODO: 
 * Finish making tf for each document within corpus
 * add query search
 */
public class Project2Take2 {
    
    public static void main(String args[]){
        HashMap<Integer, String>DocIdAndTitle = new HashMap<Integer, String>();
        HashMap<Integer, String>DocIdAndContent = new HashMap<Integer,String>();
        Map<Integer,Map<String, Integer>>TermFreqInDocument = new HashMap<Integer,Map<String, Integer>>();
        Map<String,Integer> TermFreqInCorpus = new HashMap<String, Integer>();
        Map<Integer,Map<String,Integer>> TermFrequency = new HashMap<Integer,Map<String,Integer>>();
        buildNumber(DocIdAndTitle);
        buildContent(DocIdAndContent);
        buildFrequencyinDocument(TermFreqInDocument, DocIdAndContent);
        buildFrequencyinCorpus(TermFreqInCorpus);
        TermFreq(DocIdAndContent, TermFrequency);
        int TotalCount = wordCount(TermFreqInCorpus);


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

    //TODO : Finish This Function
    private static Map<Integer,Map<String,Integer>> buildFrequencyinDocument(Map<Integer,Map<String,Integer>> TermFreqInDocument, HashMap<Integer, String> DocIdAndContent){
        //int numTimes = 0; 
        String word = "";
        for(int i:DocIdAndContent.keySet()){
            word = DocIdAndContent.get(i);
            if(TermFreqInDocument.containsValue(word)){

            }

        }
        return TermFreqInDocument;
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
    private static Integer wordCount(Map<String,Integer> TermFreqInCorpus){
        int TotalCount = 0; 
        for(String i:TermFreqInCorpus.keySet()){
            TotalCount+=TermFreqInCorpus.get(i);
        }
        return TotalCount;
    }
    private static Map<Integer,Map<String,Integer>> TermFreq(HashMap<Integer, String> DocIdAndContent , Map<Integer, Map<String, Integer>> TermFrequency){
        for (int i: DocIdAndContent.keySet()){

            String words = DocIdAndContent.get(i);
            Map<String, Integer> termFreqMap = new HashMap<>();
            
            StringTokenizer tokenizer = new StringTokenizer(words);
            while (tokenizer.hasMoreTokens()){
                String word = tokenizer.nextToken();
                word = word.toLowerCase();
                if (word.equals(".a") || word.equals(".i") || word.equals(".w") || word.equals(".b") || word.equals(".t")) {
                    continue;
                }

               
    
                termFreqMap.put(word, termFreqMap.getOrDefault(word, 0)+1);
            }

            TermFrequency.put(i, termFreqMap);
        }
        for(int j: TermFrequency.keySet()){
            System.out.println(TermFrequency.get(j));
        }
      
        return TermFrequency;        
    }


//TODO : Implement queries

}



