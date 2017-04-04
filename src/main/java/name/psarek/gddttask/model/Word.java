package name.psarek.gddttask.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.validation.constraints.NotNull;

/**
 * Represents a word consisting of the text forming the word and it part of speech category.
 */
@JsonRootName("word")
public class Word {

    public enum PartOfSpeech {
        NOUN, VERB, ADJECTIVE
    }

    private final String word;
    private final PartOfSpeech wordCategory;

    @JsonCreator
    public Word(@JsonProperty("word") String word, @JsonProperty("wordCategory") @NotNull PartOfSpeech wordCategory) {
        this.word = word;

        if (wordCategory == null) {
            throw new NullPointerException("partOfSpeech");
        }
        this.wordCategory = wordCategory;
    }

    public String getWord() {
        return word;
    }

    public PartOfSpeech getWordCategory() {
        return wordCategory;
    }

    @Override
    /**
     * Equals base on word and wordCategory.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word1 = (Word) o;

        if (word != null ? !word.equals(word1.word) : word1.word != null) return false;
        return wordCategory == word1.wordCategory;
    }

    @Override
    public int hashCode() {
        int result = word != null ? word.hashCode() : 0;
        result = 31 * result + wordCategory.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", wordCategory=" + wordCategory +
                '}';
    }

}
