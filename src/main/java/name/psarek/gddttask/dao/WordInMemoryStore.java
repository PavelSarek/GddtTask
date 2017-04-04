package name.psarek.gddttask.dao;

import name.psarek.gddttask.model.Word;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class WordInMemoryStore implements WordStore {

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final SortedMap<String, Word> keyToWord = new TreeMap<>();

    private final List<Word> nouns = new ArrayList<>();
    private final List<Word> verbs = new ArrayList<>();
    private final List<Word> adjectives = new ArrayList<>();

    private final Random random = new Random();

    @Override
    public Word getWord(String wordText) {
        rwLock.readLock().lock();
        try {
            return keyToWord.get(wordText);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public List<Word> listWords(String startAfter, Integer limit) {
        rwLock.readLock().lock();
        try {
            Map<String, Word> mapAfterStart;
            if (startAfter == null) {
                mapAfterStart = keyToWord;
            } else {
                mapAfterStart = keyToWord.tailMap(startAfter + '\0');   // exclude the startAfter
            }
            Stream<Word> wordStream = mapAfterStart.values().stream();
            if (limit != null && limit > 0) {
                wordStream = wordStream.limit(limit);
            }
            return wordStream.collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public Word storeWord(Word wordToAdd) {
        if (wordToAdd.getWord().isEmpty()) {
            throw new IllegalArgumentException("wordToAdd is empty");
        }
        List<Word> insertIntoList = chooseListByCatagory(wordToAdd.getWordCategory());
        rwLock.writeLock().lock();
        try {
            if (keyToWord.containsKey(wordToAdd.getWord())) {
                return null;
            } else {
                keyToWord.put(wordToAdd.getWord(), wordToAdd);
                insertIntoList.add(wordToAdd);
                return wordToAdd;
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private List<Word> chooseListByCatagory(Word.PartOfSpeech partOfSpeech) {
        switch (partOfSpeech) {
            case NOUN:      return nouns;
            case VERB:      return verbs;
            case ADJECTIVE: return adjectives;
            default:        throw new IllegalStateException();
        }
    }

    @Override
    public Word getRandomWord(Word.PartOfSpeech ofType) {
        List<Word> readList = chooseListByCatagory(ofType);
        rwLock.readLock().lock();
        try {
            if (readList.isEmpty()) {
                return null;
            } else {
                int selectWordIndex = random.nextInt(readList.size());
                return readList.get(selectWordIndex);
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

}
