package com.group1.webcrawler.controller.service;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Hadoop {
    public static class Doc {
        String docID;
        String title;
        String headers;
        String body;
        HashMap<String, Double> tf;
        HashMap<String, Double> tfidf;
        double tfidfS;

        public Doc(String docID) {
            this.docID = docID;
            this.title = "";
            this.headers = "";
            this.body = "";
            this.tf = new HashMap<>();
            this.tfidf = new HashMap<>();
            this.tfidfS = 0.0;
        }
    }

    public static class Word {
        String word;
        List<String> docs;
        double idf;

        public Word(String word) {
            this.word = word;
            this.docs = new ArrayList<String>();
            this.idf = 0.0;
        }
    }

    public static class rData {
        HashMap<String, Doc> docs = new HashMap<>();
        HashMap<String, Word> words = new HashMap<>();

        HashMap<String, Double> calculateQueryTFIDF(String query) {
            HashMap<String, Double> qTFIDF = new HashMap<>();
            List<String> tQuery = Arrays.asList(query.split(" "));

            for (String q : tQuery) {
                if (qTFIDF.containsKey(q)) {
                    qTFIDF.put(q, (double) qTFIDF.get(q) + 1);
                } else {
                    qTFIDF.put(q, (double) 1);
                }
            }
            for (String q : qTFIDF.keySet()) {
                if (this.words.containsKey(q)) {
                    qTFIDF.put(q, ((double) qTFIDF.get(q) / tQuery.size()) * this.words.get(q).idf);
                } else {
                    qTFIDF.put(q, (double) 0);
                }
            }

            return qTFIDF;
        }

        public String caclulateRank(String query) {
            query = query.toLowerCase();
            HashMap<String, Double> output = new HashMap<String, Double>();
            List<String> tQuery = Arrays.asList(query.split(" "));
            List<String> contain = new ArrayList<>();

            for (String word : tQuery) {
                System.out.println("=" + word + "=");
                if (this.words.containsKey(word)) {
                    System.out.println(word);
                    contain.addAll(new ArrayList<String>(this.words.get(word).docs));
                }
            }
            Set<String> uniqueDocs = new HashSet<String>(contain);

            HashMap<String, Double> qTFIDF = this.calculateQueryTFIDF(query);

            for (String docID : uniqueDocs) {
                double numerator = 0.0;
                double qDenum = 0.0;
                for (String word : qTFIDF.keySet()) {
                    if (this.docs.get(docID).tfidf.containsKey(word)) {
                        numerator += this.docs.get(docID).tfidf.get(word) * qTFIDF.get(word);
                        qDenum += (double) Math.pow(qTFIDF.get(word), 2);
                    }
                }
                double result = (numerator / (Math.sqrt(this.docs.get(docID).tfidfS) * qDenum));
                output.put(docID, result);
                System.out.println(docID);
            }

            return output.toString();
        }

        public void loadFiles(String docs, String words) {
            try (BufferedReader br = Files.newBufferedReader(Paths.get(docs), StandardCharsets.UTF_8)) {
                System.out.println("Reading docs.dat");
                for (String line = null; (line = br.readLine()) != null;) {
                    String[] elements = line.split("@@@@@");

                    elements[1] = elements[1].replace("{", "");
                    elements[1] = elements[1].replace("}", "");
                    String[] tf = elements[1].split(", ");

                    elements[2] = elements[2].replace("{", "");
                    elements[2] = elements[2].replace("}", "");
                    String[] tfidf = elements[2].split(", ");

                    if (this.docs.containsKey(elements[0])) {
                        this.docs.get(elements[0]).tfidfS = Double.valueOf(elements[3]);
                        for (String s : tf) {
                            String[] temp = s.split("=");
                            if (temp.length > 1) {
                                this.docs.get(elements[0]).tf.put(temp[0], Double.valueOf(temp[1]));
                            }
                        }
                        for (String s : tfidf) {
                            String[] temp = s.split("=");
                            if (temp.length > 1) {
                                this.docs.get(elements[0]).tfidf.put(temp[0], Double.valueOf(temp[1]));
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("reading docs.dat error");
                System.out.println(ex);
            }
            System.out.println("Reading words.dat");
            try (BufferedReader br = Files.newBufferedReader(Paths.get(words), StandardCharsets.UTF_8)) {
                for (String line = null; (line = br.readLine()) != null;) {
                    String[] elements = line.split("@@@@@");

                    elements[1] = elements[1].replace("[", "");
                    elements[1] = elements[1].replace("]", "");
                    String[] documents = elements[1].split(", ");

                    Word word = new Word(elements[0]);
                    for (String s : documents) {
                        word.docs.add(s);
                    }
                    word.idf = Double.valueOf(elements[2]);
                    this.words.put(elements[0], word);
                }
            } catch (IOException ex) {
                System.out.println("reading words.dat error");
                System.out.println(ex);
            }
        }

        public void loadData(String data, String docs, String words) {
            try (BufferedReader br = Files.newBufferedReader(Paths.get(data), StandardCharsets.UTF_8)) {
                for (String line = null; (line = br.readLine()) != null;) {
                    String[] elements = line.split("@@@@@");
                    if (elements.length > 2) {
                        String title = "";
                        String headers = "";
                        String body = "";

                        if (elements.length == 3) {
                            title = elements[1];
                            body = elements[2];

                            line = elements[1] + " " + elements[2];
                        } else if (elements.length == 4) {
                            title = elements[1];
                            headers = elements[2];
                            body = elements[3];

                            line = elements[1] + " " + elements[2] + " " + elements[3];
                        }
                        if (line.contains("@@@@@")) {
                            continue;
                        }

                        Doc tDoc = new Doc(elements[0]);
                        tDoc.title = title;
                        tDoc.headers = headers;
                        tDoc.body = body;

                        this.docs.put(elements[0], tDoc);
                    }
                }
            } catch (IOException ex) {
                System.out.println("reading dd.dat error");
                System.out.println(ex);
            }
            System.out.println(this.docs.keySet().size());

            this.loadFiles(docs, words);
        }
    }
}
