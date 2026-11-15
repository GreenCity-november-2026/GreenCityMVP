package greencity.controller;

import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.service.LanguageService;
import org.springframework.http.MediaType;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import greencity.GreenCityApplication;
import greencity.converters.UserArgumentResolver;
import org.springframework.context.annotation.Import;

import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.user.UserVO;
import greencity.enums.HabitRate;
import greencity.service.HabitStatisticService;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(HabitStatisticController.class)
@ContextConfiguration(classes = GreenCityApplication.class)
@Import(UserArgumentResolver.class)

class HabitStatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HabitStatisticService habitStatisticService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    LanguageService languageService;

    @Test
    void findAllByHabitId() throws Exception {
        Long habitId = 1L;

        when(habitStatisticService.findAllStatsByHabitId(habitId))
            .thenReturn(null);

        mockMvc.perform(get("/habit/statistic/{habitId}", habitId)
            .with(user("test@example.com").roles("USER")))
            .andExpect(status().isOk());

        verify(habitStatisticService).findAllStatsByHabitId(habitId);
    }

    @Test
    void findAllStatsByHabitAssignId() throws Exception {
        Long habitAssignId = 1L;

        when(habitStatisticService.findAllStatsByHabitAssignId(habitAssignId))
            .thenReturn(null);

        mockMvc.perform(get("/habit/statistic/assign/{habitAssignId}", habitAssignId)
            .with(user("test@example.com").roles("USER")))
            .andExpect(status().isOk());

        verify(habitStatisticService).findAllStatsByHabitAssignId(habitAssignId);
    }

    @Test
    void saveHabitStatistic() {
        Long habitId = 1L;
        Long userId = 1L;

        AddHabitStatisticDto dto = AddHabitStatisticDto.builder()
            .amountOfItems(1)
            .habitRate(HabitRate.GOOD)
            .createDate(ZonedDateTime.parse("2026-11-12T12:00:00Z"))
            .build();

        HabitStatisticDto responseDto = HabitStatisticDto.builder()
            .id(1L)
            .habitRate(HabitRate.GOOD)
            .createDate(ZonedDateTime.parse("2026-11-12T12:00:00Z"))
            .amountOfItems(1)
            .habitAssignId(1L)
            .build();

        String email = "test@example.com";

        UserVO userVO = mock(UserVO.class);

        when(userVO.getId()).thenReturn(userId);
        when(userService.findByEmail(email)).thenReturn(userVO);
        when(habitStatisticService.saveByHabitIdAndUserId(habitId, userId, dto))
            .thenReturn(responseDto);
    }

    @Test
    void updateStatistic() throws Exception {
        Long id = 1L;
        Long userId = 1L;
        String email = "test@example.com";

        UpdateHabitStatisticDto dto = UpdateHabitStatisticDto.builder()
            .amountOfItems(2)
            .habitRate(HabitRate.GOOD)
            .build();

        UserVO userVO = Mockito.mock(UserVO.class);

        when(userVO.getId()).thenReturn(userId);
        when(userService.findByEmail(email)).thenReturn(userVO);

        when(habitStatisticService.update(id, userId, dto))
            .thenReturn(dto);

        mockMvc.perform(put("/habit/statistic/{id}", id)
            .with(user(email).roles("USER"))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        verify(userService).findByEmail(email);
        verify(habitStatisticService).update(id, userId, dto);
    }

    @Test
    void getTodayStatisticsForAllHabitItems() throws Exception {
        Locale locale = Locale.ENGLISH;

        HabitItemsAmountStatisticDto dto = HabitItemsAmountStatisticDto.builder()
            .habitItem("Water")
            .notTakenItems(5)
            .build();

        List<HabitItemsAmountStatisticDto> response = List.of(dto);

        when(languageService.findAllLanguageCodes())
            .thenReturn(List.of("en", "ua", "ru"));

        when(habitStatisticService.getTodayStatisticsForAllHabitItems("en"))
            .thenReturn(response);

        mockMvc.perform(get("/habit/statistic/todayStatisticsForAllHabitItems")
            .locale(locale)
            .with(user("test@example.com").roles("USER")))
            .andExpect(status().isOk());

        verify(habitStatisticService).getTodayStatisticsForAllHabitItems("en");
    }

    @Test
    void findAmountOfAcquiredHabits() throws Exception {
        Long userId = 1L;
        Long amount = 5L;

        when(habitStatisticService.getAmountOfAcquiredHabitsByUserId(userId))
            .thenReturn(amount);

        mockMvc.perform(get("/habit/statistic/acquired/count")
            .param("userId", userId.toString())
            .with(user("test@example.com").roles("USER")))
            .andExpect(status().isOk());

        verify(habitStatisticService).getAmountOfAcquiredHabitsByUserId(userId);
    }

    @Test
    void findAmountOfHabitsInProgress() throws Exception {
        Long userId = 1L;
        Long amount = 5L;

        when(habitStatisticService.getAmountOfHabitsInProgressByUserId(userId))
            .thenReturn(amount);

        mockMvc.perform(get("/habit/statistic/in-progress/count")
            .param("userId", userId.toString())
            .with(user("test@example.com").roles("USER")))
            .andExpect(status().isOk());

        verify(habitStatisticService).getAmountOfHabitsInProgressByUserId(userId);
    }
}
