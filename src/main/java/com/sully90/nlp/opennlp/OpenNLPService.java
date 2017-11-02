package com.sully90.nlp.opennlp;

import com.google.common.collect.Sets;
import com.sully90.util.Configuration;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OpenNLPService {

    private static final String CONFIGURATION_KEY = "opennlp.entities.model.";
    private static final String MODEL_DIR = "src/main/resources/models/";

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
        // Performs named entity extraction using one of the available models in conf/application.conf
        if (!nameFinderModels.containsKey(field)) {
            throw new RuntimeException(String.format("Could not find field [%s], possible values %s", field, nameFinderModels.keySet()));
        }

        TokenNameFinderModel nameFinderModel = nameFinderModels.get(field);
        if (threadLocal.get() == null || !threadLocal.get().equals(nameFinderModel)) {
            threadLocal.set(nameFinderModel);
        }

        String[] tokens = SimpleTokenizer.INSTANCE.tokenize(content);
        Span[] spans = new NameFinderME(nameFinderModel).find(tokens);
        String[] names = Span.spansToStrings(spans, tokens);
        return Sets.newHashSet(names);
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

    public static void main(String[] args) {
        OpenNLPService openNLPService = new OpenNLPService();

        String test = "David should have read more of his thesis today, in preparation for his trip to Brighton next week";

        String[] models = new String[] {
                "persons",
                "dates",
                "locations"
        };

        for (String model : models) {
            System.out.println(openNLPService.find(test, model));
        }
    }

}
