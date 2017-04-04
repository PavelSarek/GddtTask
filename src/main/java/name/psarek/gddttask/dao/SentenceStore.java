package name.psarek.gddttask.dao;

import name.psarek.gddttask.model.Sentence;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface SentenceStore {

    /**
     * Generate a new id
     * @return  next id
     */
    @NotNull Long generateNextId();

    /**
     * Store a new sentence
     * @param   newSentence     the sentence to store
     * @throws  IllegalArgumentException    if sentence with the same already stored
     */
    void storeSentence(@NotNull Sentence newSentence);

    /**
     * Retrieve a sentence from storage
     * @param   sentenceId  id
     * @return  the sentence or <code>null</code>
     */
    Sentence getSentence(@NotNull Long sentenceId);

    /**
     * Retrieve a collection of sentences.
     * @param   startAfter  start after this sentence (excluded)
     * @param   limit       maximal number of sentences requested
     * @return  a list with sentence ordered by id of size between 0 and limit
     */
    @NotNull List<Sentence> listSentences(Long startAfter, Integer limit);

}
