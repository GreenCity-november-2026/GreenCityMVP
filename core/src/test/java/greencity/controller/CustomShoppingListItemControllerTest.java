package greencity.controller;

import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.CustomShoppingListItemService;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CustomShoppingListItemControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CustomShoppingListItemController controller;

    @Mock
    CustomShoppingListItemService service;

    private CustomShoppingListItemResponseDto createResponseDto() {
        return CustomShoppingListItemResponseDto.builder()
            .id(1L)
            .text("Test item")
            .status(ShoppingListItemStatus.ACTIVE)
            .build();
    }

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllAvailableCustomShoppingListItemsTest() throws Exception {
        when(service.findAllAvailableCustomShoppingListItems(1L, 2L))
                .thenReturn(List.of(createResponseDto()));

        mockMvc.perform(get("/custom/shopping-list-items/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Test item"));

        verify(service).findAllAvailableCustomShoppingListItems(1L, 2L);
    }

    @Test
    void saveUserCustomShoppingListItemsTest() throws Exception {

        when(service.save(any(BulkSaveCustomShoppingListItemDto.class), eq(1L), eq(2L)))
                .thenReturn(List.of(createResponseDto()));

        mockMvc.perform(post("/custom/shopping-list-items/1/2/custom-shopping-list-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(service).save(any(BulkSaveCustomShoppingListItemDto.class), eq(1L), eq(2L));
    }

    @Test
    void updateItemStatusTest() throws Exception {

        when(service.updateItemStatus(1L, 2L, "DONE")).thenReturn(createResponseDto());

        mockMvc.perform(patch("/custom/shopping-list-items/1/custom-shopping-list-items")
                        .param("itemId", "2")
                        .param("status", "DONE"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Test item"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(status().isOk());

        verify(service).updateItemStatus(1L, 2L, "DONE");
    }

    @Test
    void updateItemStatusToDoneTest() throws Exception {
        mockMvc.perform(patch("/custom/shopping-list-items/1/done")
            .param("itemId", "2"))
            .andExpect(status().isOk());

        verify(service).updateItemStatusToDone(1L, 2L);
    }

    @Test
    void bulkDeleteCustomShoppingListItemsTest() throws Exception {
        when(service.bulkDelete("1,2")).thenReturn(List.of(1L, 2L));

        mockMvc.perform(delete("/custom/shopping-list-items/1/custom-shopping-list-items")
                        .param("ids", "1,2"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2))
                .andExpect(status().isOk());

        verify(service).bulkDelete("1,2");
    }

    @Test
    void getAllCustomShoppingItemsByStatusTest() throws Exception {
        when(service.findAllUsersCustomShoppingListItemsByStatus(1L, "ACTIVE"))
            .thenReturn(List.of(createResponseDto()));

        mockMvc.perform(get("/custom/shopping-list-items/1/custom-shopping-list-items")
            .param("status", "ACTIVE"))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].status").value("ACTIVE"))
            .andExpect(status().isOk());

        verify(service).findAllUsersCustomShoppingListItemsByStatus(1L, "ACTIVE");
    }
}
