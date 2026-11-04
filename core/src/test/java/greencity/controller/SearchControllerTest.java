package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.service.LanguageService;
import greencity.service.SearchService;
import greencity.validator.LanguageValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    private static final String searchLink = "/search";

    private MockMvc mockMvc;

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    @Mock
    private LanguageService languageService;

    @BeforeEach
    void setup() {
        LanguageValidator languageValidator = new LanguageValidator();
        when(languageService.findAllLanguageCodes()).thenReturn(List.of("en"));
        org.springframework.test.util.ReflectionTestUtils.setField(languageValidator, "languageService",
            languageService);
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setConstraintValidatorFactory(new ConstraintValidatorFactory() {
            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key.equals(LanguageValidator.class)) {
                    return (T) languageValidator;
                }
                try {
                    return key.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void releaseInstance(ConstraintValidator<?, ?> instance) {
            }
        });
        validatorFactoryBean.afterPropertiesSet();

        this.mockMvc = MockMvcBuilders
            .standaloneSetup(searchController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(validatorFactoryBean)
            .build();
    }

    @Test
    void searchTest() throws Exception {
        SearchResponseDto responseDto = SearchResponseDto.builder()
            .ecoNews(List.of())
            .countOfResults(0L)
            .build();

        when(searchService.search(eq("eco"), any(String.class))).thenReturn(responseDto);

        mockMvc.perform(get(searchLink)
            .param("searchQuery", "eco")
            .header("Accept-Language", "en"))
            .andExpect(status().isOk());

        verify(searchService).search(eq("eco"), any(String.class));
    }

    @Test
    void searchEcoNewsTest() throws Exception {
        PageableDto<SearchNewsDto> pageableDto = new PageableDto<>(List.of(), 0L, 0, 0);

        when(searchService.searchAllNews(any(Pageable.class), eq("eco"), any(String.class)))
            .thenReturn(pageableDto);

        mockMvc.perform(get(searchLink + "/econews")
            .param("searchQuery", "eco")
            .param("page", "0")
            .param("size", "5")
            .header("Accept-Language", "en"))
            .andExpect(status().isOk());

        verify(searchService).searchAllNews(any(Pageable.class), eq("eco"), any(String.class));
    }

    @Test
    void searchMissingQueryTest() throws Exception {
        lenient().when(languageService.findAllLanguageCodes()).thenReturn(List.of("en"));
        mockMvc.perform(get(searchLink)
            .header("Accept-Language", "en"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(searchService);
    }
}