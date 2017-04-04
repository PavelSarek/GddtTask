package name.psarek.gddttask.integration;

import name.psarek.gddttask.model.Word;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

import static net.javacrumbs.jsonunit.JsonAssert.*;


/**
 * Basic integration tests for the application.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)       // all the test will run in order, include stageXX
public class GddtTaskApplicationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

    @Test
    public void stage01_shouldReturnEmptyWordsCollection() {
        ResponseEntity<String> entity = testRestTemplate.getForEntity(getUrlForPath("/words"), String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertJsonEquals("{\"words\": []}", entity.getBody());
    }

    @Test
    public void stage02_creationOfNounAndVerbShouldSucceed() {
        testRestTemplate.put(getUrlForPath("/words/noun"), new Word(null, Word.PartOfSpeech.NOUN));
        testRestTemplate.put(getUrlForPath("/words/verb"), new Word(null, Word.PartOfSpeech.VERB));

        ResponseEntity<String> entity = testRestTemplate.getForEntity(getUrlForPath("/words"), String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertJsonEquals("{\"words\": [{\"word\": \"noun\", \"wordCategory\": \"NOUN\"}, " +
                        "{\"word\": \"verb\", \"wordCategory\": \"VERB\"}]}",
                        entity.getBody());
    }

    @Test
    public void stage03_shouldReturnEmptyCollectionOfSentences() {
        ResponseEntity<String> entity = testRestTemplate.getForEntity(getUrlForPath("/sentences"), String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertJsonEquals("{\"sentences\": []}", entity.getBody());
    }

    @Test
    public void stage04_sentenceGenerationShouldFail() {
        ResponseEntity<String> entity = testRestTemplate.postForEntity("/sentences/generate", null, String.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertJsonPartEquals("Missing at least one word of category/categories: adjective", entity.getBody(), "Map.message");
    }

    @Test
    public void stage05_creationOfAdjectiveShouldSucceed() {
        testRestTemplate.put(getUrlForPath("/words/adjective"), new Word("adjective", Word.PartOfSpeech.ADJECTIVE));
    }

    @Test
    public void stage06_sentenceGenerationShouldSucceed() {
        ResponseEntity<String> entity = testRestTemplate.postForEntity("/sentences/generate", null, String.class);
        assertEquals(HttpStatus.CREATED, entity.getStatusCode());
        assertJsonEquals(
                "{\"sentence\": " + getJsonForSentence("Noun verb adjective.") + "}",
                entity.getBody()
        );
    }

    @Test
    public void stage07_shouldReturnSingletonSentenceCollection() {
        ResponseEntity<String> entity = testRestTemplate.getForEntity(getUrlForPath("/sentences"), String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertJsonEquals(
                "{\"sentences\": [" + getJsonForSentence("Noun verb adjective.") + "]}",
                entity.getBody()
        );
    }

    @Test
    public void state08_shouldBeAbleToGetSentence() {
        ResponseEntity<String> entity = testRestTemplate.getForEntity(getUrlForPath("/sentences/1001"), String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertJsonEquals(
                "{\"sentence\": " + getJsonForSentence("Noun verb adjective.") + "}",
                entity.getBody()
        );

    }

    @Test
    public void state09_testYodaTalk() {
        ResponseEntity<String> entity = testRestTemplate.getForEntity(getUrlForPath("/sentences/1001/yodaTalk"), String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertJsonEquals(
                "{\"sentence\": " + getJsonForSentence("Adjective noun verb.") + "}",
                entity.getBody()
        );

    }

    private String getUrlForPath(String path) {
        return "http://localhost:" + this.port + path;
    }

    private String getJsonForSentence(final String textOfSentence) {
        return "{\"id\": 1001, \"timestamp\": \"${json-unit.any-string}\", \"text\": \"" + textOfSentence + "\"}";
    }

}
