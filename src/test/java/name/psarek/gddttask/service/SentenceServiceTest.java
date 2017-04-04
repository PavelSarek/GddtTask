package name.psarek.gddttask.service;

import name.psarek.gddttask.dao.SentenceStore;
import name.psarek.gddttask.dao.WordStore;
import name.psarek.gddttask.model.Sentence;
import name.psarek.gddttask.model.Word;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SentenceServiceTest {

    private final Word noun = new Word("noun", Word.PartOfSpeech.NOUN);
    private final Word verb = new Word("verb", Word.PartOfSpeech.VERB);
    private final Word adj = new Word("adjective", Word.PartOfSpeech.ADJECTIVE);

    @Mock
    private WordStore wordStoreMock;

    @Mock
    private SentenceStore sentenceStoreMock;

    @InjectMocks
    private SentenceService sentenceService = new SentenceService();

    @Test
    public void generateAndStoreNewSentence() throws Exception {
        when(sentenceStoreMock.generateNextId()).thenReturn(2222L);
        when(wordStoreMock.getRandomWord(Word.PartOfSpeech.NOUN)).thenReturn(noun);
        when(wordStoreMock.getRandomWord(Word.PartOfSpeech.VERB)).thenReturn(verb);
        when(wordStoreMock.getRandomWord(Word.PartOfSpeech.ADJECTIVE)).thenReturn(adj);

        long beforeTimestamp = System.currentTimeMillis();
        Sentence resultSentence = sentenceService.generateAndStoreNewSentence();
        assertEquals(2222, resultSentence.getId());
        assertTrue(resultSentence.getGenerationTimestamp() >= beforeTimestamp);
        assertTrue(resultSentence.getGenerationTimestamp() <= System.currentTimeMillis());
        assertEquals(noun, resultSentence.getNoun());
        assertEquals(verb, resultSentence.getVerb());
        assertEquals(adj, resultSentence.getAdjective());

        verify(sentenceStoreMock, times(1)).storeSentence(resultSentence);
    }

    @Test
    public void testGetSentence() throws Exception {
        Sentence sentence = new Sentence(1001, System.currentTimeMillis(), noun, verb, adj);
        when(sentenceStoreMock.getSentence(1001L)).thenReturn(sentence);

        assertEquals(sentence, sentenceService.getSentence(1001));
    }

    @Test
    public void testListSentences() throws Exception {
        when(sentenceService.listSentences(any(), any())).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), sentenceService.listSentences(42L, 100));
        verify(sentenceStoreMock, times(1)).listSentences(42L, 100);
    }

}