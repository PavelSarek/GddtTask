package name.psarek.gddttask.rest;

import name.psarek.gddttask.model.Word;
import name.psarek.gddttask.service.WordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WordsControllerTest {

    @Mock
    private WordService wordServiceMock;

    @InjectMocks
    private WordsController wordsController = new WordsController();


    @Test
    public void testGetWord() throws Exception {
        when(wordServiceMock.getWord("word")).thenReturn(new Word("word", Word.PartOfSpeech.NOUN));
        assertEquals("word" , wordsController.getWord("word").getWord());
        verify(wordServiceMock, times(1)).getWord("word");

    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetNotFoundWord() throws Exception {
        when(wordServiceMock.getWord(any())).thenReturn(null);
        wordsController.getWord("unknown");
    }

    @Test
    public void testPutWord() throws Exception {
        Word word = new Word("word", Word.PartOfSpeech.NOUN);
        when(wordServiceMock.storeWord("word", new Word(null, Word.PartOfSpeech.NOUN))).thenReturn(word);
        assertEquals(word, wordsController.putWord("word", new Word(null, Word.PartOfSpeech.NOUN)));
    }

    @Test(expected = BadRequestException.class)
    public void testPutWordCheckBodyText() throws Exception {
        wordsController.putWord("word", new Word("different", Word.PartOfSpeech.NOUN));
    }


    @Test
    public void testPutWordIsIdempotent() throws Exception {
        Word word = new Word("word", Word.PartOfSpeech.NOUN);

        when(wordServiceMock.storeWord("word", new Word(null, Word.PartOfSpeech.NOUN))).thenReturn(word);
        wordsController.putWord("word", new Word(null, Word.PartOfSpeech.NOUN));

        when(wordServiceMock.storeWord("word", new Word(null, Word.PartOfSpeech.NOUN))).thenReturn(null);
        when(wordServiceMock.getWord("word")).thenReturn(word);
        assertEquals(word, wordsController.putWord("word", new Word(null, Word.PartOfSpeech.NOUN)));
    }

    @Test(expected = ReadOnlyEntityException.class)
    public void testPutWordRejectModification() throws Exception {
        Word word = new Word("word", Word.PartOfSpeech.NOUN);

        when(wordServiceMock.storeWord("word", new Word(null, Word.PartOfSpeech.NOUN))).thenReturn(word);
        wordsController.putWord("word", new Word(null, Word.PartOfSpeech.NOUN));

        when(wordServiceMock.storeWord("word", new Word(null, Word.PartOfSpeech.VERB))).thenReturn(null);
        when(wordServiceMock.getWord("word")).thenReturn(word);
        wordsController.putWord("word", new Word(null, Word.PartOfSpeech.VERB));
    }

    @Test
    public void testListWords() throws Exception {
        when(wordServiceMock.listWords(any(), any())).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), wordsController.listWords("wrd", 100).getWords());
        verify(wordServiceMock, times(1)).listWords("wrd", 100);
    }

    @Test(expected = BadRequestException.class)
    public void testListSentencesChecksLimit() {
        wordsController.listWords(null, -1);
    }

    @Test(expected = BadRequestException.class)
    public void testListSentencesChecksEmptyStartWith() {
        wordsController.listWords("", -1);
    }

}
