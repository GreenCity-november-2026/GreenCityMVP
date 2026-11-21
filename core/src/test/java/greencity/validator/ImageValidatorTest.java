package greencity.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class ImageValidatorTest {

    @InjectMocks
    private ImageValidator imageValidator;

    @Mock
    private MultipartFile multipartFile;

    @Test
    void isValid_NullFile_ReturnsTrue() {
        assertTrue(imageValidator.isValid(null, null));
    }

    @Test
    void isValid_ValidPngType_ReturnsTrue() {
        when(multipartFile.getContentType()).thenReturn("image/png");
        assertTrue(imageValidator.isValid(multipartFile, null));
    }

    @Test
    void isValidValidJpegTypeReturnsTrue() {
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        assertTrue(imageValidator.isValid(multipartFile, null));
    }

    @Test
    void isValidValidJpgTypeReturnsTrue() {
        when(multipartFile.getContentType()).thenReturn("image/jpg");
        assertTrue(imageValidator.isValid(multipartFile, null));
    }

    @Test
    void isValidInvalidGifTypeReturnsFalse() {
        when(multipartFile.getContentType()).thenReturn("image/gif");
        assertFalse(imageValidator.isValid(multipartFile, null));
    }

    @Test
    void isValidInvalidPdfTypeReturnsFalse() {
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        assertFalse(imageValidator.isValid(multipartFile, null));
    }

    @Test
    void isValidNullContentTypeReturnsFalse() {
        when(multipartFile.getContentType()).thenReturn(null);
        assertFalse(imageValidator.isValid(multipartFile, null));
    }
}
