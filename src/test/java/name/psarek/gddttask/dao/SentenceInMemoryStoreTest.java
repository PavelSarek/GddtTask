package name.psarek.gddttask.dao;

import name.psarek.gddttask.model.Sentence;
import name.psarek.gddttask.model.Word;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SentenceInMemoryStoreTest {

    private SentenceStore storeUnderTest;

    private final Word noun = new Word("noun", Word.PartOfSpeech.NOUN);
    private final Word noun2 = new Word("noun2", Word.PartOfSpeech.NOUN);
    private final Word verb = new Word("verb", Word.PartOfSpeech.VERB);
    private final Word verb2 = new Word("verb2", Word.PartOfSpeech.VERB);
    private final Word adj = new Word("adjective", Word.PartOfSpeech.ADJECTIVE);

    @Before
    public void setUp() throws Exception {
        storeUnderTest = new SentenceInMemoryStore();
    }

    @Test
    public void testGenerateNextId() throws Exception {
        Long id1 = storeUnderTest.generateNextId();
        Long id2 = storeUnderTest.generateNextId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testStoreAndGetSentence() throws Exception {
        Sentence aSentence = new Sentence(1, 0, noun, verb, adj);
        storeUnderTest.storeSentence(aSentence);

        Sentence gotSentence = storeUnderTest.getSentence(1L);
        assertEquals(aSentence, gotSentence);
        assertEquals(aSentence.getNoun(), gotSentence.getNoun());
        assertEquals(aSentence.getVerb(), gotSentence.getVerb());
        assertEquals(aSentence.getAdjective(), gotSentence.getAdjective());

        assertNull(storeUnderTest.getSentence(7L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotStoreSentenceWithDuplicateId() throws Exception {
        Sentence sentence1 = new Sentence(1, 0, noun, verb, adj);
        Sentence sentence2 = new Sentence(1, 0, noun2, verb, adj);
        storeUnderTest.storeSentence(sentence1);
        storeUnderTest.storeSentence(sentence2);
    }

    @Test
    public void listSentences() throws Exception {
        long timestamp = System.currentTimeMillis();
        Sentence sentence1 = new Sentence(storeUnderTest.generateNextId(), timestamp, noun, verb, adj);
        Sentence sentence2 = new Sentence(storeUnderTest.generateNextId(), timestamp + 1, noun2, verb, adj);
        Sentence sentence3 = new Sentence(storeUnderTest.generateNextId(), timestamp + 2, noun, verb2, adj);

        storeUnderTest.storeSentence(sentence1);
        storeUnderTest.storeSentence(sentence2);
        storeUnderTest.storeSentence(sentence3);

        assertEquals(Arrays.asList(sentence1, sentence2, sentence3), storeUnderTest.listSentences(null, null));

        assertEquals(Arrays.asList(sentence1), storeUnderTest.listSentences(null, 1));
        assertEquals(Arrays.asList(sentence2, sentence3), storeUnderTest.listSentences(sentence1.getId(), 2));
        assertEquals(Arrays.asList(sentence3), storeUnderTest.listSentences(sentence2.getId(), 100));
        assertTrue(storeUnderTest.listSentences(sentence3.getId(),null).isEmpty());
    }

}