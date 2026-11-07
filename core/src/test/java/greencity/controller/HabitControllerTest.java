package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.habit.HabitDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.service.HabitService;
import greencity.service.TagsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitControllerTest {

    @Mock
    private HabitService habitService;

    @Mock
    private TagsService tagsService;

    @InjectMocks
    private HabitController habitController;

    @Test
    void getHabitByIdReturnsHabitDto() {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;

        HabitDto habitDto = mock(HabitDto.class);

        when(habitService.getByIdAndLanguageCode(1L, "en"))
                .thenReturn(habitDto);

        ResponseEntity<HabitDto> response =
                habitController.getHabitById(habitId, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(habitDto, response.getBody());

        verify(habitService).getByIdAndLanguageCode(1L, "en");
    }

    @Test
    void getAllReturnsHabitsForCurrentUser() {
        UserVO userVO = mock(UserVO.class);
        Locale locale = Locale.ENGLISH;
        Pageable pageable = PageRequest.of(0, 10);

        PageableDto<HabitDto> pageableDto = mock(PageableDto.class);

        when(habitService.getAllHabitsByLanguageCode(
                userVO, pageable, "en"))
                .thenReturn(pageableDto);

        ResponseEntity<PageableDto<HabitDto>> response =
                habitController.getAll(userVO, locale, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pageableDto, response.getBody());

        verify(habitService).getAllHabitsByLanguageCode(
                userVO, pageable, "en");
    }


    @Test
    void getAllByTagsAndLanguageCodeReturnsHabits() {
        Locale locale = Locale.ENGLISH;
        List<String> tags = List.of("sport", "health");
        Pageable pageable = PageRequest.of(0, 10);

        PageableDto<HabitDto> pageableDto = mock(PageableDto.class);

        when(habitService.getAllByTagsAndLanguageCode(pageable, tags, "en"))
                .thenReturn(pageableDto);

        ResponseEntity<PageableDto<HabitDto>> response =
                habitController.getAllByTagsAndLanguageCode(locale, tags, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pageableDto, response.getBody());

        verify(habitService).getAllByTagsAndLanguageCode(pageable, tags, "en");
    }


    @Test
    void getAllByDifferentParametersReturnsHabits() {
        UserVO userVO = mock(UserVO.class);
        Locale locale = Locale.ENGLISH;

        Optional<List<String>> tags = Optional.of(List.of("sport"));
        Optional<Boolean> isCustomHabit = Optional.empty();
        Optional<List<Integer>> complexities = Optional.empty();

        Pageable pageable = PageRequest.of(0, 10);

        PageableDto<HabitDto> pageableDto = mock(PageableDto.class);

        when(habitService.getAllByDifferentParameters(
                userVO, pageable, tags, isCustomHabit, complexities, "en"))
                .thenReturn(pageableDto);

        ResponseEntity<PageableDto<HabitDto>> response =
                habitController.getAllByDifferentParameters(
                        userVO, locale, tags, isCustomHabit, complexities, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pageableDto, response.getBody());

        verify(habitService).getAllByDifferentParameters(
                userVO, pageable, tags, isCustomHabit, complexities, "en");
    }

    @Test
    void getAllByDifferentParametersThrowsBadRequestException() {
        UserVO userVO = mock(UserVO.class);
        Locale locale = Locale.ENGLISH;

        Optional<List<String>> tags = Optional.empty();
        Optional<Boolean> isCustomHabit = Optional.empty();
        Optional<List<Integer>> complexities = Optional.empty();

        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(BadRequestException.class, () ->
                habitController.getAllByDifferentParameters(
                        userVO, locale, tags, isCustomHabit, complexities, pageable));

        verifyNoInteractions(habitService);
    }

    @Test
    void getShoppingListItemsReturnsList() {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;

        List<ShoppingListItemDto> items =
                List.of(mock(ShoppingListItemDto.class));

        when(habitService.getShoppingListForHabit(habitId, "en"))
                .thenReturn(items);

        ResponseEntity<List<ShoppingListItemDto>> response =
                habitController.getShoppingListItems(habitId, locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(items, response.getBody());

        verify(habitService).getShoppingListForHabit(habitId, "en");
    }


    @Test
    void findAllHabitsTagsReturnsTags() {
        Locale locale = Locale.ENGLISH;
        List<String> tags = List.of("sport", "health");

        when(tagsService.findAllHabitsTags("en")).thenReturn(tags);

        ResponseEntity<List<String>> response =
                habitController.findAllHabitsTags(locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tags, response.getBody());

        verify(tagsService).findAllHabitsTags("en");
    }


    @Test
    void addCustomHabitReturnsCreated() {
        AddCustomHabitDtoRequest request = mock(AddCustomHabitDtoRequest.class);
        MultipartFile image = mock(MultipartFile.class);
        Principal principal = mock(Principal.class);

        AddCustomHabitDtoResponse habitResponse =
                mock(AddCustomHabitDtoResponse.class);

        when(principal.getName()).thenReturn("testUser");

        when(habitService.addCustomHabit(request, image, "testUser"))
                .thenReturn(habitResponse);

        ResponseEntity<AddCustomHabitDtoResponse> response =
                habitController.addCustomHabit(request, image, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(habitResponse, response.getBody());

        verify(habitService).addCustomHabit(request, image, "testUser");
    }

    @Test
    void getFriendsAssignedToHabitProfilePicturesReturnsList() {
        Long habitId = 1L;
        UserVO userVO = mock(UserVO.class);

        when(userVO.getId()).thenReturn(10L);

        List<UserProfilePictureDto> pictures =
                List.of(mock(UserProfilePictureDto.class));

        when(habitService.getFriendsAssignedToHabitProfilePictures(habitId, 10L))
                .thenReturn(pictures);

        ResponseEntity<List<UserProfilePictureDto>> response =
                habitController.getFriendsAssignedToHabitProfilePictures(
                        habitId, userVO
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pictures, response.getBody());

        verify(habitService)
                .getFriendsAssignedToHabitProfilePictures(habitId, 10L);
    }
}
