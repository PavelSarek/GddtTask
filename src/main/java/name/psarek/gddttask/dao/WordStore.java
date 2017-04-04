package name.psarek.gddttask.dao;

import name.psarek.gddttask.model.Word;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface WordStore {

    /**
     * Retrieve a word from storage
     * @param   wordText  the word text as key
     * @return  the word or <code>null</code>
     */
    Word getWord(@NotNull String wordText);

    /**
     * Retrieve a collection of words.
     * @param   startAfter  start after this word (excluded)
     * @param   limit       maximal number of word requested
     * @return  a list with sorted words of size between 0 and limit
     */
    @NotNull List<Word> listWords(String startAfter, Integer limit);

    /**
     * Store a new word
     * @param   wordToAdd   the word to store
     * @return  the stored word, <code>null</code> if word with the same key was already in store,
     *          and the new one was not stored
     */
    Word storeWord(@NotNull Word wordToAdd);

    /**
     * Return a randomly selected word of the specified word category.
     * @param   ofType  non-null word category
     * @return  a word of type or <code>null</code> if no such word stored so far
     */
    Word getRandomWord(@NotNull Word.PartOfSpeech ofType);

}
