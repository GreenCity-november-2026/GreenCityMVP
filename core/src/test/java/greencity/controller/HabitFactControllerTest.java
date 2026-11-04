package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HabitFactControllerTest {
    private static final String factsLink = "/facts";

    private MockMvc mockMvc;

    @Mock
    HabitFactService habitFactService;

    @InjectMocks
    private HabitFactController habitFactController;

    @Mock
    private CustomExceptionHandler customExceptionHandler;

    @Mock
    private ModelMapper mapper;

    @Mock
    private Validator validator;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(habitFactController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(customExceptionHandler)
                .setValidator(validator)
                .build();
    }

    @Test
    void getHabitFactOfTheDayTest() throws Exception {
        when(habitFactService.getHabitFactOfTheDay(1L)).thenReturn(new LanguageTranslationDTO());
        mockMvc.perform(get(factsLink + "/dayFact/1"))
                .andExpect(status().isOk());

        verify(habitFactService).getHabitFactOfTheDay(1L);
    }

    @Test
    void deleteTest() throws Exception {
        when(habitFactService.delete(1L)).thenReturn(1L);
        mockMvc.perform(delete(factsLink + "/1"))
                .andExpect(status().isOk());

        verify(habitFactService).delete(1L);
    }

    @Test
    void deleteNotFoundTest() throws Exception {
        when(habitFactService.delete(999L))
                .thenThrow(new BadRequestException("Invalid habit fact id"));
        when(customExceptionHandler.handleBadRequestException(any(), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        mockMvc.perform(delete(factsLink + "/999"))
                .andExpect(status().isBadRequest());

        verify(habitFactService).delete(999L);
    }

    @Test
    void saveTest() throws Exception {
        HabitFactVO habitFactVO = new HabitFactVO();
        when(habitFactService.save(any(HabitFactPostDto.class))).thenReturn(habitFactVO);
        when(mapper.map(habitFactVO, HabitFactDtoResponse.class)).thenReturn(new HabitFactDtoResponse());

        mockMvc.perform(post(factsLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"translations\": [], \"habit\": {\"habitId\": 1}}"))
                .andExpect(status().isCreated());

        verify(habitFactService).save(any(HabitFactPostDto.class));
        verify(mapper).map(habitFactVO, HabitFactDtoResponse.class);
    }

    @Test
    void updateTest() throws Exception {
        HabitFactVO habitFactVO = new HabitFactVO();
        when(habitFactService.update(any(HabitFactUpdateDto.class), eq(1L))).thenReturn(habitFactVO);
        when(mapper.map(habitFactVO, HabitFactPostDto.class)).thenReturn(new HabitFactPostDto());

        mockMvc.perform(put(factsLink + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"translations\": [], \"habit\": {\"habitId\": 1}}"))
                .andExpect(status().isOk());

        verify(habitFactService).update(any(HabitFactUpdateDto.class), eq(1L));
        verify(mapper).map(habitFactVO, HabitFactPostDto.class);
    }

    @Test
    void getRandomFactByHabitIdTest() throws Exception {
        when(habitFactService.getRandomHabitFactByHabitIdAndLanguage(1L, "en"))
                .thenReturn(new LanguageTranslationDTO());

        mockMvc.perform(get(factsLink + "/random/1")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk());

        verify(habitFactService).getRandomHabitFactByHabitIdAndLanguage(1L, "en");
    }

    @Test
    void getAllTest() throws Exception {
        PageableDto<LanguageTranslationDTO> pageableDto =
                new PageableDto<>(List.of(new LanguageTranslationDTO()), 1L, 0, 1);

        when(habitFactService.getAllHabitFacts(any(Pageable.class), eq("en")))
                .thenReturn(pageableDto);

        mockMvc.perform(get(factsLink)
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk());

        verify(habitFactService).getAllHabitFacts(any(Pageable.class), eq("en"));
    }
}
