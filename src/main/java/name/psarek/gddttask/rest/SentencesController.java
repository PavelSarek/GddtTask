package name.psarek.gddttask.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import name.psarek.gddttask.model.Sentence;
import name.psarek.gddttask.service.SentenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController()
@RequestMapping("/sentences")
public class SentencesController {

    @Autowired
    private SentenceService sentenceService;

    @JsonRootName("sentence")
    public static class SentenceRepresentation {

        private final long id;
        private final Date timestamp;
        private final String text;

        private SentenceRepresentation(long id, Date timestamp, String text) {
            this.id = id;
            this.timestamp = timestamp;
            this.text = text;
        }

        public long getId() {
            return id;
        }

        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssZ")
        public Date getTimestamp() {
            return timestamp;
        }

        public String getText() {
            return text;
        }

        private static SentenceRepresentation from(Sentence sentence) {
            return from(sentence, false);
        }

        private static SentenceRepresentation from(Sentence sentence, boolean yoda) {
            return new SentenceRepresentation(
                    sentence.getId(),
                    new Date(sentence.getGenerationTimestamp()),
                    yoda ? sentence.formulateYodaSentence() : sentence.formulateSentence()
            );
        }

    }   // Sentence Representation

    @RequestMapping(value="/generate", method=POST)
    @ResponseStatus(HttpStatus.CREATED)
    public SentenceRepresentation generateSentence() {
        return SentenceRepresentation.from(sentenceService.generateAndStoreNewSentence());
    }

    @RequestMapping(value="/{id}", method=GET)
    public SentenceRepresentation getSentence(@PathVariable Long id) {
        return commonGet(id, false);
    }

    @RequestMapping(value="/{id}/yodaTalk", method=GET)
    public SentenceRepresentation getYodaTalk(@PathVariable Long id) {
        return commonGet(id, true);
    }

    private SentenceRepresentation commonGet(@PathVariable Long id, boolean isYoda) {
        Sentence sentence = sentenceService.getSentence(id);
        if (sentence == null) {
            throw new EntityNotFoundException("sentence", id);
        }
        return SentenceRepresentation.from(sentence, isYoda);
    }

    @JsonRootName("sentences")
    public static class SentencesList {

        private final List<SentenceRepresentation> sentences;

        SentencesList(List<Sentence> sentences) {
            this.sentences = sentences.stream().map(SentenceRepresentation::from).collect(Collectors.toList());
        }

        @JsonValue
        public List<SentenceRepresentation> getSentences() {
            return sentences;
        }

    }   // SentencesList


    @RequestMapping(method=GET)
    public SentencesList listSentences(@RequestParam(value="startAfter", required=false) Long startAfter,
                                        @RequestParam(value="limit", required=false) Integer limit) {
        if (limit != null && limit <= 0) {
            throw new BadRequestException("Parameter limit must be positive if present");
        }

        return new SentencesList(sentenceService.listSentences(startAfter, limit));
    }

}
