package name.psarek.gddttask.service;

import name.psarek.gddttask.dao.SentenceStore;
import name.psarek.gddttask.dao.WordStore;
import name.psarek.gddttask.model.Sentence;
import name.psarek.gddttask.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
public class SentenceService {

    @Autowired
    private SentenceStore sentenceStore;

    @Autowired
    private WordStore wordStore;

    @ResponseStatus(value=HttpStatus.NOT_FOUND)
        // not sure with the mapping to HTTP status code, 404 allows further requests; other option would be 500
    public static class WordCategoryNotAvailable extends RuntimeException {

        private final boolean missingNoun, missingVerb, missingAdjective;

        WordCategoryNotAvailable(Word noun, Word verb, Word adjective) {
            missingNoun = noun == null;
            missingVerb = verb == null;
            missingAdjective = adjective == null;
        }

        @Override
        public String getMessage() {
            StringBuilder result = new StringBuilder();
            result.append("Missing at least one word of category/categories: ");
            boolean comma = appendFlag(result, missingNoun, "noun", false);
            comma = appendFlag(result, missingVerb, "verb", comma);
            appendFlag(result, missingAdjective, "adjective", comma);
            return result.toString();
        }

        private boolean appendFlag(StringBuilder builder, boolean condition, String name, boolean comma) {
            if (condition) {
                if (comma) {
                    builder.append(", ");
                }
                builder.append(name);
                return true;
            }
            return false;
        }

    }   // WordCategoryNotAvailable


    /**
     * @return  a newly generated sentence
     * @throws  WordCategoryNotAvailable    if some word category is empty
     */
    public Sentence generateAndStoreNewSentence() {
        Word noun = wordStore.getRandomWord(Word.PartOfSpeech.NOUN);
        Word verb = wordStore.getRandomWord(Word.PartOfSpeech.VERB);
        Word adjective = wordStore.getRandomWord(Word.PartOfSpeech.ADJECTIVE);

        if (noun == null || verb == null || adjective == null) {
            throw new WordCategoryNotAvailable(noun, verb, adjective);
        }

        long generatedTimestamp = System.currentTimeMillis();
        long id = sentenceStore.generateNextId();

        Sentence result = new Sentence(id, generatedTimestamp, noun, verb, adjective);
        sentenceStore.storeSentence(result);
        return result;
    }

    public Sentence getSentence(long sentenceId) {
        return sentenceStore.getSentence(sentenceId);
    }

    public List<Sentence> listSentences(Long startAfter, Integer limit) {
        return sentenceStore.listSentences(startAfter, limit);
    }

}
