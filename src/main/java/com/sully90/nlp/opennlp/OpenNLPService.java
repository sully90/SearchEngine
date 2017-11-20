package com.sully90.nlp.opennlp;

import com.sully90.util.Configuration;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
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
    private static String DEFAULT_STOP_WORD = "The ";

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenNLPService.class);

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
        if (!nameFinderModels.containsKey(field)) {
            throw new RuntimeException(String.format("Could not find field [%s], possible values %s", field, nameFinderModels.keySet()));
        }

        // If content contains only one word, prepend the default stop word to help OpenNLP
        int nwords = countWords(content);
        if (nwords == 1) {
            content = new StringBuilder(DEFAULT_STOP_WORD).append(content).toString();
        }

        TokenNameFinderModel model = nameFinderModels.get(field);

        Set<String> nameSet = new HashSet<>();

        TokenizerME tokenizer = new TokenizerME(this.loadTokenizerModel());

        List<String> ngrams = generateNgramsUpto(content, 5);

        // Combine the ngrams into a single sentence with commas, which we will use for the named
        // entity extraction.
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = ngrams.iterator();

        while (it.hasNext()) {
            String gram = it.next();
            sb.append(gram);
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(".");

        String[] simpleTokens = tokenizer.tokenize(sb.toString());
        Span[] spans = new NameFinderME(model).find(simpleTokens);
        String[] names = Span.spansToStrings(spans, simpleTokens);

        for (String name : names) {
            nameSet.add(name);
        }

        return nameSet;
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

        Map<String, Set<String>> cleanedNamedEntities = new HashMap<>();

        for (String model : models) {
            Set<String> entitySet = namedEntities.get(model);
            Iterator<String> entitySetIterator = entitySet.iterator();

            Set<String> cleanEntities = new HashSet<>();

            while (entitySetIterator.hasNext()) {
                String entity = entitySetIterator.next();

                // Clean this entity against all other models
                for (String otherModel : models) {
                    if (otherModel.equals(model)) continue;

                    Set<String> otherEntitySet = namedEntities.get(otherModel);
                    Iterator<String> otherEntitySetIterator = otherEntitySet.iterator();

                    while (otherEntitySetIterator.hasNext()) {
                        String otherEntity = otherEntitySetIterator.next();
                        if (entity.contains(otherEntity)) {
                            System.out.println(entity + " : " + otherEntity);
                            entity = entity.replace(otherEntity, "").trim();
                        }
                    }
                }
                // Entity has been cleaned
                cleanEntities.add(entity);
            }
            cleanedNamedEntities.put(model, cleanEntities);
        }

        return cleanedNamedEntities;
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

    public static List<String> generateNgramsUpto(String str, int maxGramSize) {

        List<String> sentence = Arrays.asList(str.split("[\\W+]"));

        List<String> ngrams = new LinkedList<>();
        int ngramSize = 0;
        StringBuilder sb = null;

        //sentence becomes ngrams
        for (ListIterator<String> it = sentence.listIterator(); it.hasNext();) {
            String word = it.next();

            //1- add the word itself
            sb = new StringBuilder(word);
            ngrams.add(word);
            ngramSize=1;
            it.previous();

            //2- insert prevs of the word and add those too
            while(it.hasPrevious() && ngramSize<maxGramSize){
                sb.insert(0,' ');
                sb.insert(0,it.previous());
                ngrams.add(sb.toString() + ",");
                ngramSize++;
            }

            //go back to initial position
            while(ngramSize>0){
                ngramSize--;
                it.next();
            }
        }
        return ngrams;
    }

    private static int countWords(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }

        String[] words = content.split("\\s+");
        return words.length;
    }

    public static void main(String[] args) {
        OpenNLPService service = new OpenNLPService();

        String content = "New York Italy";
        Map<String, Set<String>> entities = service.getNamedEntities(content);

        System.out.println(entities);
    }

}
