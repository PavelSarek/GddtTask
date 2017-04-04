package name.psarek.gddttask.service;

import name.psarek.gddttask.dao.WordStore;
import name.psarek.gddttask.model.Word;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WordServiceTest {

    @Mock
    private WordStore wordStoreMock;

    @InjectMocks
    private WordService wordService = new WordService();

    @Test
    public void testStoreWord() throws Exception {
        when(wordStoreMock.storeWord(any())).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                return invocation.getArguments()[0];
            }
        });

        Word result = wordService.storeWord("wordText", new Word(null, Word.PartOfSpeech.VERB));

        assertEquals("wordText", result.getWord());
        assertEquals(Word.PartOfSpeech.VERB, result.getWordCategory());

        verify(wordStoreMock, times(1)).storeWord(any());
    }

    @Test
    public void testGetWord() throws Exception {
        Word word = new Word("word", Word.PartOfSpeech.NOUN);
        when(wordStoreMock.getWord("word")).thenReturn(word);

        assertEquals(word, wordService.getWord("word"));
    }

    @Test
    public void testListWords() throws Exception {
        when(wordStoreMock.listWords(any(), any())).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), wordService.listWords("pivot", 100));
        verify(wordStoreMock, times(1)).listWords("pivot", 100);
    }

}