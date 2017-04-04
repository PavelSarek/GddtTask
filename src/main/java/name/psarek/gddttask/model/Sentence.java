package name.psarek.gddttask.model;

import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * Represents a sentence consisting of 3 words: noun, verb, adjective.
 */
public class Sentence {

    private final long id;
    private final long generatedTimestamp;
    private final Word noun;
    private final Word verb;
    private final Word adjective;

    public Sentence(long id, long generatedTimestamp, @NotNull Word noun, @NotNull Word verb, @NotNull Word adjective) {
        this.id = id;

        this.generatedTimestamp = generatedTimestamp;

        if (noun.getWordCategory() != Word.PartOfSpeech.NOUN) {
            throw new IllegalArgumentException("noun: " + noun);
        }
        this.noun = noun;

        if (verb.getWordCategory() != Word.PartOfSpeech.VERB) {
            throw new IllegalArgumentException("verb: " + verb);
        }
        this.verb = verb;

        if (adjective.getWordCategory() != Word.PartOfSpeech.ADJECTIVE) {
            throw new IllegalArgumentException("adjective: " + adjective);
        }
        this.adjective = adjective;
    }

    public long getId() {
        return id;
    }

    public long getGenerationTimestamp() {
        return generatedTimestamp;
    }

    public Word getNoun() {
        return noun;
    }

    public Word getVerb() {
        return verb;
    }

    public Word getAdjective() {
        return adjective;
    }

    public String formulateSentence() {
        return connectWords(getNoun(), getVerb(), getAdjective());
    }

    public String formulateYodaSentence() {
        return connectWords(getAdjective(), getNoun(), getVerb());
    }

    private static String connectWords(Word first, Word second, Word third) {
        return StringUtils.capitalize(first.getWord()) + " " + second.getWord() + " " + third.getWord() + ".";
    }

    @Override
    /**
     * Equals based on id.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sentence sentence = (Sentence) o;

        return id == sentence.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Sentence{" +
                "id=" + id +
                ", noun=" + noun +
                ", verb=" + verb +
                ", adjective=" + adjective +
                '}';
    }

}
