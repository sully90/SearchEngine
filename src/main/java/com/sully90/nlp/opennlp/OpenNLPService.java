package com.sully90.nlp.opennlp;

import com.sully90.util.Configuration;
import com.sully90.util.StringUtils;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OpenNLPService {

    private static final String CONFIGURATION_KEY = "opennlp.entities.model.file.";
    private static final String MODEL_DIR = "src/main/resources/models/";

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenNLPService.class);

    protected double probabilityLowerLimit = 0.75d;

    // TokenNameFinder is not thread safe, so use a threadLocal hack
    private ThreadLocal<TokenNameFinderModel> threadLocal = new ThreadLocal<>();
    private Map<String, TokenNameFinderModel> nameFinderModels = new ConcurrentHashMap<>();

    public OpenNLPService() {
        // Loop through application.conf entries and load all OpenNLP models
        for (Enumeration<?> e = Configuration.config().propertyNames(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            String filename = Configuration.config().getProperty(key);
            // now you have name and value
            if (key.startsWith(CONFIGURATION_KEY)) {
                String modelName = key.replace(CONFIGURATION_KEY, "");
                TokenNameFinderModel nameFinderModel = getTokenNameFinderModel(filename);

                if (nameFinderModel != null) this.nameFinderModels.put(modelName, nameFinderModel);
            }
        }
    }

    public Set<String> find(String content, String field) {
        try {
            if (!nameFinderModels.containsKey(field)) {
                throw new RuntimeException(String.format("Could not find field [%s], possible values %s", field, nameFinderModels.keySet()));
            }

            TokenNameFinderModel model = nameFinderModels.get(field);
            if (threadLocal.get() == null || !threadLocal.get().equals(model)) {
                threadLocal.set(model);
            }
            Set<String> nameSet = new HashSet<>();

            TokenizerME tokenizer = new TokenizerME(loadTokenizerModel());

            int maxNgramSize = WhitespaceTokenizer.INSTANCE.tokenize(content).length;
            List<String> nGrams = StringUtils.generateNgramsUpto(content, maxNgramSize);

            for (String nGram : nGrams) {
                String[] tokens = tokenizer.tokenize(field + " " + nGram);

                // Perform the named entity extraction
                Span[] spans = new NameFinderME(model).find(tokens);
                String[] names = Span.spansToStrings(spans, tokens);

                // Add to the named entity set if we pass the confidence test
                for (int i = 0; i < names.length; i++) {
                    String name = names[i];
                    Span span = spans[i];
//                    System.out.println(field + " : " + name + " : " + span.getProb());
                    if (span.getProb() >= this.probabilityLowerLimit && content.contains(name)) nameSet.add(name);
                }
            }

            return nameSet;
        } finally {
            threadLocal.remove();
        }
    }

    public Map<String, Set<String>> getNamedEntities(String content) {
        Set<String> modelKeySet = this.nameFinderModels.keySet();
        String[] models = modelKeySet.stream().toArray(String[]::new);
        return this.getNamedEntities(content, models);
    }

    public Map<String, Set<String>> getNamedEntities(String content, String[] models) {
        Map<String, Set<String>> namedEntities = new HashMap<>();

        for (String model : models) {
            if (nameFinderModels.containsKey(model)) {
                namedEntities.put(model, this.find(content, model));
            } else {
                throw new RuntimeException(String.format("Could not find field [%s], possible values %s", model, nameFinderModels.keySet()));
            }
        }

        return namedEntities;
    }

    private TokenNameFinderModel getTokenNameFinderModel(String fileName) {
//        URL url = ClassLoader.getSystemResource(fileName);
        String fileNameWithPath = MODEL_DIR + fileName;
        System.out.println(fileNameWithPath);

//        try(InputStream is = new FileInputStream(url.getFile())) {
        try(InputStream is = new FileInputStream(fileNameWithPath)) {
            TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(is);
            return nameFinderModel;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SentenceModel loadSentenceModel() {
        String filename = Configuration.config().getProperty("opennlp.tokenizer.file.sentences");

        try (InputStream is = new FileInputStream(MODEL_DIR + filename)) {
            SentenceModel sentenceModel = new SentenceModel(is);
            return sentenceModel;
        } catch (IOException e) {
            LOGGER.error("Unable to load sentences model", e);
        }

        return null;
    }

    private TokenizerModel loadTokenizerModel() {
        String filename = Configuration.config().getProperty("opennlp.tokenizer.file.token");

        try (InputStream is = new FileInputStream(MODEL_DIR + filename)) {
            TokenizerModel tokenizerModel = new TokenizerModel(is);
            return tokenizerModel;
        } catch (IOException e) {
            LOGGER.error("Unable to load sentences model", e);
        }

        return null;
    }

    public double getProbabilityLowerLimit() {
        return probabilityLowerLimit;
    }

    public void setProbabilityLowerLimit(double probabilityLowerLimit) {
        this.probabilityLowerLimit = probabilityLowerLimit;
    }

    private static int countWords(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }

        String[] words = content.split("\\s+");
        return words.length;
    }

    private static boolean containsCurrencySymbol(String str) {
        for (char c : str.toCharArray()) {
            if (Character.getType(c) == Character.CURRENCY_SYMBOL) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        OpenNLPService service = new OpenNLPService();

//        String content = "London population";
        String content = "James Bond Istanbul";
//        String content = "David Sullivan Newport";
        long startTime = System.currentTimeMillis();
        Map<String, Set<String>> entities = service.getNamedEntities(content);
        long endTime = System.currentTimeMillis();

        System.out.println(entities);
        System.out.println("Took " + (endTime - startTime) + " milliseconds.");
    }

}
