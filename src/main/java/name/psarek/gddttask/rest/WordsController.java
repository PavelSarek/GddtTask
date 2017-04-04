package name.psarek.gddttask.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import name.psarek.gddttask.model.Word;
import name.psarek.gddttask.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/words")
public class WordsController {

    @Autowired
    private WordService wordService;

    @RequestMapping(value="/{wordText}", method=GET)
    public Word getWord(@PathVariable String wordText) {
        if (StringUtils.isEmpty(wordText)) {
            throw new IllegalStateException();  // normally can't happen because of mapping
        }

        Word result = wordService.getWord(wordText);

        if (result == null) {
            throw new EntityNotFoundException("word", wordText);
        }

        return result;
    }

    // alternatively POST the complete word to the /words collection
    @RequestMapping(value="/{wordText}", method=PUT)
    public Word putWord(@PathVariable String wordText,
                        @RequestBody Word newWord) {
        if (newWord == null || newWord.getWordCategory() == null) {
            throw new BadRequestException("The body must represent a word with wordCategory");
        }
        if (newWord.getWord() != null && !newWord.getWord().equals(wordText)) {
            throw new BadRequestException("The text of the word in the path '" + wordText
                                          + "' and body '" + newWord.getWord() + "' must be the same");
        }

        Word result = wordService.storeWord(wordText, newWord);
        if (result == null) {
            // there was already a word with key wordText
            Word savedWord = wordService.getWord(wordText);
            if (savedWord.getWordCategory() == newWord.getWordCategory()) {
                return savedWord;   // if the saved word is the same, report success; PUT must be idempotent
            } else {
                throw new ReadOnlyEntityException("word", wordText);
            }
        }
        return result;
    }

    @JsonRootName("words")
    /*
      Get around problem with list root name (root wrapping is turned on globally).
      @see http://stackoverflow.com/questions/16022795/how-to-wrap-a-list-as-top-level-element-in-json-generated-by-jackson
     */
    public static class WordList {

        private final List<Word> words;

        WordList(List<Word> words) {
            this.words = words;
        }

        @JsonValue
        public List<Word> getWords() {
            return words;
        }

    }   // WordList


    @RequestMapping(method=GET)
    public WordList listWords(@RequestParam(value="startAfter", required=false) String startAfter,
                              @RequestParam(value="limit", required=false) Integer limit) {
        if (startAfter != null && startAfter.isEmpty()) {
            throw new BadRequestException("Parameter startAfter must not be empty if present");
        }
        if (limit != null && limit <= 0) {
            throw new BadRequestException("Parameter limit must be positive if present");
        }

        return new WordList(wordService.listWords(startAfter, limit));
    }

}
