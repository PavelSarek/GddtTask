package name.psarek.gddttask.service;

import name.psarek.gddttask.dao.WordStore;
import name.psarek.gddttask.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class WordService {

    @Autowired
    private WordStore wordStore;

    public Word storeWord(String wordText, Word newWord) {
        Word completedWord = new Word(wordText, newWord.getWordCategory());
        return wordStore.storeWord(completedWord);
    }

    public Word getWord(@NotNull String wordText) {
        return wordStore.getWord(wordText);
    }

    public @NotNull List<Word> listWords(String startAfter, Integer limit) {
        return wordStore.listWords(startAfter, limit);
    }

}
