package greencity.controller;

import greencity.GreenCityApplication;
import greencity.service.LanguageService;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;

@WebMvcTest(LanguageController.class)
@ContextConfiguration(classes = GreenCityApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class LanguageControllerTest {
    private static final String languageLink = "/language";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LanguageService languageService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Test
    void getAllLanguageCodesTest() throws Exception {
        List<String> expectedCodes = List.of("en", "ua", "fr");
        when(languageService.findAllLanguageCodes()).thenReturn(expectedCodes);

        mockMvc.perform(get(languageLink))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0]").value("en"))
            .andExpect(jsonPath("$[1]").value("ua"))
            .andExpect(jsonPath("$[2]").value("fr"));

        verify(languageService).findAllLanguageCodes();
    }
}
