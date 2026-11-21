
package greencity.validator;

import greencity.annotations.ValidLanguage;
import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LanguageValidatorTest {

    @Mock
    private LanguageService languageService;

    @Mock
    private ValidLanguage validLanguage;

    @Mock
    private ConstraintValidatorContext context;

    @InjectMocks
    private LanguageValidator languageValidator;

    @BeforeEach
    void setUp() {
        when(languageService.findAllLanguageCodes())
                .thenReturn(List.of("en", "ua"));

        languageValidator.initialize(validLanguage);
    }

    @Test
    void initializeLoadsLanguageCodes() {
        languageValidator.initialize(validLanguage);

        verify(languageService, times(2)).findAllLanguageCodes();
    }

    @Test
    void isValidReturnsTrueForSupportedEnglishLanguage() {
        Locale locale = Locale.ENGLISH;

        boolean result = languageValidator.isValid(locale, context);

        assertTrue(result);
    }

    @Test
    void isValidReturnsTrueForSupportedUkrainianLanguage() {
        Locale locale = new Locale("ua");

        boolean result = languageValidator.isValid(locale, context);

        assertTrue(result);
    }

    @Test
    void isValidReturnsFalseForUnsupportedLanguage() {
        Locale locale = Locale.FRENCH;

        boolean result = languageValidator.isValid(locale, context);

        assertFalse(result);
    }
}
