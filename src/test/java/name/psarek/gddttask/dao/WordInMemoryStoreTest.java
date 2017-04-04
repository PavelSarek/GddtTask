package name.psarek.gddttask.dao;

import name.psarek.gddttask.model.Word;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class WordInMemoryStoreTest {

    private WordStore storeUnderTest;

    @Before
    public void setUp() throws Exception {
        storeUnderTest = new WordInMemoryStore();
    }

    @Test
    public void testStoringAndGettingWord() throws Exception {
        Word wordA = new Word("A", Word.PartOfSpeech.NOUN);
        assertEquals(wordA, storeUnderTest.storeWord(wordA));
        assertEquals(wordA, storeUnderTest.getWord("A"));
        assertNull(storeUnderTest.getWord("B"));
    }

    @Test
    public void testCannotAddDuplicateWord() throws Exception {
        Word wordA = new Word("A", Word.PartOfSpeech.NOUN);
        Word wordADuplicate = new Word("A", Word.PartOfSpeech.ADJECTIVE);
        assertNotNull(storeUnderTest.storeWord(wordA));
        assertNull(storeUnderTest.storeWord(wordADuplicate));
    }

    @Test
    public void testListWords() throws Exception {
        Word word1 = new Word("1", Word.PartOfSpeech.NOUN);
        Word word2 = new Word("2", Word.PartOfSpeech.NOUN);
        Word word3 = new Word("3", Word.PartOfSpeech.NOUN);

        assertNotNull(storeUnderTest.storeWord(word1));
        assertNotNull(storeUnderTest.storeWord(word2));
        assertNotNull(storeUnderTest.storeWord(word3));

        assertEquals(Arrays.asList(word1, word2, word3), storeUnderTest.listWords(null, null));

        assertEquals(Arrays.asList(word1), storeUnderTest.listWords(null, 1));
        assertEquals(Arrays.asList(word2, word3), storeUnderTest.listWords(word1.getWord(), 2));
        assertEquals(Arrays.asList(word3), storeUnderTest.listWords(word2.getWord(), 100));
        assertTrue(storeUnderTest.listWords(word3.getWord(), null).isEmpty());
    }

    @Test
    public void testRandomWord() throws Exception {
        Word noun1 = new Word("apple", Word.PartOfSpeech.NOUN);
        Word noun2 = new Word("pear", Word.PartOfSpeech.NOUN);
        Word verb = new Word("is", Word.PartOfSpeech.VERB);

        assertNotNull(storeUnderTest.storeWord(noun1));
        assertNotNull(storeUnderTest.storeWord(noun2));
        assertNotNull(storeUnderTest.storeWord(verb));

        assertEquals(Word.PartOfSpeech.NOUN, storeUnderTest.getRandomWord(Word.PartOfSpeech.NOUN).getWordCategory());
        assertEquals(verb, storeUnderTest.getRandomWord(Word.PartOfSpeech.VERB));
        assertNull(storeUnderTest.getRandomWord(Word.PartOfSpeech.ADJECTIVE));
    }

    @Test
    public void testParallelUsage() throws Exception {
        Word[] words = {
                new Word("A", Word.PartOfSpeech.NOUN),
                new Word("B", Word.PartOfSpeech.VERB),
                new Word("C", Word.PartOfSpeech.ADJECTIVE),
                new Word("D", Word.PartOfSpeech.NOUN),
        };

        class StoreUser implements Callable<Boolean> {

            private final int index;

            public StoreUser(int index) {
                this.index = index;
            }

            @Override
            public Boolean call() {
                storeUnderTest.storeWord(words[index]);
                for (int i = 0; i < 1000; i++) {
                    Word word = storeUnderTest.getWord(words[index].getWord());
                    if (word == null) {
                        throw new IllegalStateException("getWord");
                    }
                    Word randomWord = storeUnderTest.getRandomWord(words[index].getWordCategory());
                    if (randomWord == null) {
                        throw new IllegalStateException("getRandomWord");
                    }
                }
                return true;
            }
        }

        List<StoreUser> tasks = new ArrayList(words.length);
        for (int index = 0; index < words.length; index++) {
            tasks.add(new StoreUser(index));
        }


        ExecutorService executorService = Executors.newFixedThreadPool(words.length);
        List<Future<Boolean>> futures = executorService.invokeAll(tasks);
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(3, TimeUnit.SECONDS));
        executorService.shutdownNow();

        for (Future<Boolean> eachFuture : futures) {
            assertTrue(eachFuture.isDone());
            assertFalse(eachFuture.isCancelled());
            assertTrue(eachFuture.get());
        }
    }

}
