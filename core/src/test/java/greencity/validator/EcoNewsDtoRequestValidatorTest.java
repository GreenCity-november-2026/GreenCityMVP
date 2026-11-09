package greencity.validator;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getAddEcoNewsDtoRequest;
import static org.junit.jupiter.api.Assertions.assertTrue;

import greencity.exception.exceptions.WrongCountOfTagsException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class EcoNewsDtoRequestValidatorTest {
    @InjectMocks
    private EcoNewsDtoRequestValidator validator;

    @Test
    void isValidTrueTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource("https://eco-lavca.ua/");
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValidWithEmptyTagsThrowsExceptionTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setTags(List.of());

        assertThrows(WrongCountOfTagsException.class,
            () -> validator.isValid(request, null));
    }

    @Test
    void isValidWithTooManyTagsThrowsExceptionTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setTags(List.of("News", "Eco", "Nature", "Green"));

        assertThrows(WrongCountOfTagsException.class,
            () -> validator.isValid(request, null));
    }

    @Test
    void isValidWithMaximumTagsTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setTags(List.of("News", "Eco", "Nature"));

        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValidWithNullSourceTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource(null);

        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValidWithEmptySourceTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource("");

        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValidWithInvalidSourceTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource("invalid");

        assertThrows(InvalidURLException.class,
            () -> validator.isValid(request, null));
    }

    @Test
    void isValidWithOneTagTest() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setTags(List.of("News"));

        assertTrue(validator.isValid(request, null));
    }

    @Test
    void initializeTest() {
        validator.initialize(null);
    }
}
