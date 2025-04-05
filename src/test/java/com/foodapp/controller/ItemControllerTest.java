package com.foodapp.controller;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionServiceImpl;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Item;
import com.foodapp.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemServiceControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private UserSessionServiceImpl userSessionService;

    @InjectMocks
    private ItemServiceController itemController;

    private Item dummyItem;
    private final Integer VALID_ITEM_ID = 1;
    private final Integer INVALID_ITEM_ID = 100;

    @BeforeEach
    void setUp() {
        dummyItem = new Item();
        dummyItem.setItemId(VALID_ITEM_ID);
        dummyItem.setItemName("Pizza");
        dummyItem.setQuantity(2);
        dummyItem.setCost(299.99);
    }

    @Test
    void testAddItem_Authorized() throws AuthorizationException, ItemException {
        when(userSessionService.getUserRole("valid_key")).thenReturn("ADMIN");
        when(itemService.addItem(any(Item.class))).thenReturn(dummyItem);

        ResponseEntity<Item> response = itemController.addItem(dummyItem, "valid_key");

        assertNotNull(response.getBody());
        assertEquals("Pizza", response.getBody().getItemName());
        verify(itemService, times(1)).addItem(any(Item.class));
    }

    @Test
    void testAddItem_Unauthorized() throws AuthorizationException {
        when(userSessionService.getUserRole("invalid_key")).thenReturn("USER");

        assertThrows(AuthorizationException.class, () -> itemController.addItem(dummyItem, "invalid_key"));
    }

    @Test
    void testUpdateItem_Authorized() throws AuthorizationException, ItemException {
        when(userSessionService.getUserRole("valid_key")).thenReturn("MANAGER");
        when(itemService.updateItem(any(Item.class))).thenReturn(dummyItem);

        ResponseEntity<Item> response = itemController.updateItem(dummyItem, "valid_key");

        assertNotNull(response.getBody());
        assertEquals("Pizza", response.getBody().getItemName());
        verify(itemService, times(1)).updateItem(any(Item.class));
    }

    @Test
    void testUpdateItem_Unauthorized() throws AuthorizationException {
        when(userSessionService.getUserRole("invalid_key")).thenReturn("USER");

        assertThrows(AuthorizationException.class, () -> itemController.updateItem(dummyItem, "invalid_key"));
    }

    @Test
    void testGetItemById_Found() throws ItemException {
        when(itemService.viewItem(VALID_ITEM_ID)).thenReturn(dummyItem);

        ResponseEntity<Item> response = itemController.getItem(VALID_ITEM_ID);

        assertNotNull(response.getBody());
        assertEquals("Pizza", response.getBody().getItemName());
        verify(itemService, times(1)).viewItem(VALID_ITEM_ID);
    }

    @Test
    void testGetItemById_NotFound() throws ItemException {
        when(itemService.viewItem(INVALID_ITEM_ID)).thenThrow(new ItemException("Item not found with ID: " + INVALID_ITEM_ID));

        Exception exception = assertThrows(ItemException.class, () -> itemController.getItem(INVALID_ITEM_ID));
        assertEquals("Item not found with ID: 100", exception.getMessage());
    }

    @Test
    void testRemoveItem_Authorized() throws AuthorizationException, ItemException {
        when(userSessionService.getUserRole("valid_key")).thenReturn("ADMIN");
        when(itemService.removeItem(VALID_ITEM_ID)).thenReturn(dummyItem);

        ResponseEntity<Item> response = itemController.removeItem(VALID_ITEM_ID, "valid_key");

        assertNotNull(response.getBody());
        assertEquals("Pizza", response.getBody().getItemName());
        verify(itemService, times(1)).removeItem(VALID_ITEM_ID);
    }

    @Test
    void testRemoveItem_Unauthorized() throws AuthorizationException {
        when(userSessionService.getUserRole("invalid_key")).thenReturn("USER");

        assertThrows(AuthorizationException.class, () -> itemController.removeItem(VALID_ITEM_ID, "invalid_key"));
    }

    @Test
    void testGetAllItems() throws ItemException {
        List<Item> items = Arrays.asList(dummyItem, new Item());
        when(itemService.viewAllItems()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getAllItems();

        assertEquals(2, response.getBody().size());
        verify(itemService, times(1)).viewAllItems();
    }
}
