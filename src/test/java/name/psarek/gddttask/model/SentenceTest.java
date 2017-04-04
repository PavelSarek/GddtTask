package name.psarek.gddttask.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class SentenceTest {

    private final Word noun = new Word("noun", Word.PartOfSpeech.NOUN);
    private final Word noun2 = new Word("noun2", Word.PartOfSpeech.NOUN);
    private final Word verb = new Word("verb", Word.PartOfSpeech.VERB);
    private final Word adj = new Word("adjective", Word.PartOfSpeech.ADJECTIVE);

    @Test
    public void testGetters() throws Exception {
        long timestamp = System.currentTimeMillis();
        Sentence sentence = new Sentence(1001, timestamp, noun, verb, adj);
        
        assertEquals(1001, sentence.getId());
        assertEquals(timestamp, sentence.getGenerationTimestamp());
        assertSame(noun, sentence.getNoun());
        assertSame(verb, sentence.getVerb());
        assertSame(adj, sentence.getAdjective());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWordCategoriesAreChecked() throws Exception {
        new Sentence(1001, 1, verb, noun, adj);
    }

    @Test
    public void formulateSentence() throws Exception {
        Sentence sentence = new Sentence(1001, 1, noun, verb, adj);
        assertEquals("Noun verb adjective.", sentence.formulateSentence());
    }

    @Test
    public void formulateYodaSentence() throws Exception {
        Sentence sentence = new Sentence(1001, 1, noun, verb, adj);
        assertEquals("Adjective noun verb.", sentence.formulateYodaSentence());
    }

    @Test
    public void testEquals() throws Exception {
        Sentence sentence1 = new Sentence(10, 111, noun, verb, adj);
        Sentence sentence2 = new Sentence(10, 112, noun2, verb, adj);
        Sentence sentence3 = new Sentence(11, 111, noun, verb, adj);

        assertEquals(sentence1, sentence2);
        assertNotEquals(sentence1, sentence3);
    }

}
