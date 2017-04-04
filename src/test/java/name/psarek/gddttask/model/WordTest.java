package name.psarek.gddttask.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class WordTest {

    @Test
    public void testGetters() throws Exception {
        Word word = new Word("palindrom", Word.PartOfSpeech.ADJECTIVE);
        assertEquals("palindrom", word.getWord());
        assertEquals(Word.PartOfSpeech.ADJECTIVE, word.getWordCategory());
    }

    @Test
    public void testEquals() throws Exception {
        Word word1 = new Word("equals", Word.PartOfSpeech.NOUN);
        Word word1Dupl = new Word("equals", Word.PartOfSpeech.NOUN);
        Word word2 = new Word("equals", Word.PartOfSpeech.ADJECTIVE);
        Word word3 = new Word("test", Word.PartOfSpeech.VERB);

        assertEquals(word1, word1Dupl);
        assertNotEquals(word1, word2);
        assertNotEquals(word1, word3);
    }

}
