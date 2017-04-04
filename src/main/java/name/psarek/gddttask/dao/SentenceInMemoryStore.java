package name.psarek.gddttask.dao;

import name.psarek.gddttask.model.Sentence;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class SentenceInMemoryStore implements SentenceStore {

    private final AtomicLong idGenerator = new AtomicLong(1000);

    private final ConcurrentNavigableMap<Long, Sentence> keyToSentence = new ConcurrentSkipListMap<>();

    @Override
    public Long generateNextId() {
        return idGenerator.incrementAndGet();
    }

    @Override
    public void storeSentence(Sentence newSentence) {
        if (keyToSentence.putIfAbsent(newSentence.getId(), newSentence) != null) {
            throw new IllegalArgumentException("Duplicate id " + newSentence);
        }
    }

    @Override
    public Sentence getSentence(Long sentenceId) {
        return keyToSentence.get(sentenceId);
    }

    @Override
    public List<Sentence> listSentences(Long startAfter, Integer limit) {
        ConcurrentNavigableMap<Long, Sentence> sentencesMapAfterStart;
        if (startAfter != null) {
            sentencesMapAfterStart = keyToSentence.tailMap(startAfter, false);
        } else {
            sentencesMapAfterStart = keyToSentence;
        }
        Stream<Sentence> sentenceStream = sentencesMapAfterStart.values().stream();
        if (limit != null && limit > 0) {
            sentenceStream = sentenceStream.limit(limit);
        }
        return sentenceStream.collect(Collectors.toList());
    }

}
