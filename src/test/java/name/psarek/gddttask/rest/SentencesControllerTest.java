package name.psarek.gddttask.rest;

import name.psarek.gddttask.model.Sentence;
import name.psarek.gddttask.model.Word;
import name.psarek.gddttask.service.SentenceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SentencesControllerTest {

    private final Word noun = new Word("noun", Word.PartOfSpeech.NOUN);
    private final Word verb = new Word("verb", Word.PartOfSpeech.VERB);
    private final Word adj = new Word("adjective", Word.PartOfSpeech.ADJECTIVE);
    private final Sentence sentence = new Sentence(1001, System.currentTimeMillis(), noun, verb, adj);

    @Mock
    private SentenceService sentenceServiceMock;

    @InjectMocks
    private SentencesController sentencesController = new SentencesController();


    @Test
    public void testGenerate() throws Exception {
        when(sentenceServiceMock.generateAndStoreNewSentence()).thenReturn(sentence);

        assertEquals(sentence.getId(), sentencesController.generateSentence().getId());
    }

    @Test
    public void testGet() throws Exception {
        when(sentenceServiceMock.getSentence(1001)).thenReturn(sentence);
        assertEquals("Noun verb adjective." , sentencesController.getSentence(1001L).getText());
        verify(sentenceServiceMock, times(1)).getSentence(1001L);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetNotFound() throws Exception {
        when(sentenceServiceMock.getSentence(anyLong())).thenReturn(null);
        sentencesController.getSentence(1001L);
    }

    @Test
    public void testGetYodaTalk() throws Exception {
        when(sentenceServiceMock.getSentence(1001)).thenReturn(sentence);
        assertEquals("Adjective noun verb." , sentencesController.getYodaTalk(1001L).getText());
        verify(sentenceServiceMock, times(1)).getSentence(1001L);
    }

    @Test
    public void testListSentences() throws Exception {
        when(sentenceServiceMock.listSentences(any(), any())).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), sentencesController.listSentences(42L, 100).getSentences());
        verify(sentenceServiceMock, times(1)).listSentences(42L, 100);
    }

    @Test(expected = BadRequestException.class)
    public void testListSentencesChecksLimit() {
        sentencesController.listSentences(null, -1);
    }

}