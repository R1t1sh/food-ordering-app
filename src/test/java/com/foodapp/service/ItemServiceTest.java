package com.foodapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.foodapp.repository.CategoryDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Category;
import com.foodapp.model.Item;
import com.foodapp.repository.ItemDAO;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemDAO itemDAO;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private CategoryDAO categoryDAO;


    private Item dummyItem;
    private Category dummyCategory;

    @BeforeEach
    void setUp() {
        dummyCategory = new Category(1, "Beverages");
        dummyItem = new Item(1, "Burger", 2, 150.0, null, dummyCategory, null);
    }

    @Test
    void testAddItem_Success() throws ItemException {

        when(categoryDAO.findById(dummyCategory.getCategoryId())).thenReturn(Optional.of(dummyCategory));

        when(itemDAO.save(any(Item.class))).thenReturn(dummyItem);

        Item savedItem = itemService.addItem(dummyItem);

        assertNotNull(savedItem);
        assertEquals("Burger", savedItem.getItemName());


        verify(categoryDAO, times(1)).findById(dummyCategory.getCategoryId());
        verify(itemDAO, times(1)).save(any(Item.class));
    }


    @Test
    void testUpdateItem_Success() throws ItemException {

        when(categoryDAO.findById(dummyCategory.getCategoryId())).thenReturn(Optional.of(dummyCategory));

        when(itemDAO.findById(dummyItem.getItemId())).thenReturn(Optional.of(dummyItem));

        when(itemDAO.save(any(Item.class))).thenReturn(dummyItem);

        Item updatedItem = itemService.updateItem(dummyItem);

        assertNotNull(updatedItem);
        assertEquals("Burger", updatedItem.getItemName());

        verify(categoryDAO, times(1)).findById(dummyCategory.getCategoryId());
        verify(itemDAO, times(1)).findById(dummyItem.getItemId());
        verify(itemDAO, times(1)).save(any(Item.class));
    }


    @Test
    void testUpdateItem_NotFound() {
        when(itemDAO.findById(dummyItem.getItemId())).thenReturn(Optional.empty());

        assertThrows(ItemException.class, () -> itemService.updateItem(dummyItem));
        verify(itemDAO, times(1)).findById(dummyItem.getItemId());
    }

    @Test
    void testViewItem_Success() throws ItemException {
        when(itemDAO.findById(dummyItem.getItemId())).thenReturn(Optional.of(dummyItem));

        Item retrievedItem = itemService.viewItem(dummyItem.getItemId());
        assertNotNull(retrievedItem);
        assertEquals("Burger", retrievedItem.getItemName());

        verify(itemDAO, times(1)).findById(dummyItem.getItemId());
    }

    @Test
    void testViewItem_NotFound() {
        when(itemDAO.findById(dummyItem.getItemId())).thenReturn(Optional.empty());

        assertThrows(ItemException.class, () -> itemService.viewItem(dummyItem.getItemId()));
        verify(itemDAO, times(1)).findById(dummyItem.getItemId());
    }

    @Test
    void testRemoveItem_Success() throws ItemException {
        when(itemDAO.findById(dummyItem.getItemId())).thenReturn(Optional.of(dummyItem));
        doNothing().when(itemDAO).deleteById(dummyItem.getItemId());

        Item removedItem = itemService.removeItem(dummyItem.getItemId());
        assertNotNull(removedItem);

        verify(itemDAO, times(1)).findById(dummyItem.getItemId());
        verify(itemDAO, times(1)).deleteById(dummyItem.getItemId());
    }

    @Test
    void testRemoveItem_NotFound() {
        when(itemDAO.findById(dummyItem.getItemId())).thenReturn(Optional.empty());

        assertThrows(ItemException.class, () -> itemService.removeItem(dummyItem.getItemId()));
        verify(itemDAO, times(1)).findById(dummyItem.getItemId());
    }

    @Test
    void testViewAllItems_Success() throws ItemException {
        List<Item> items = Arrays.asList(dummyItem);
        when(itemDAO.findAll()).thenReturn(items);

        List<Item> retrievedItems = itemService.viewAllItems();
        assertFalse(retrievedItems.isEmpty());
        assertEquals(1, retrievedItems.size());

        verify(itemDAO, times(1)).findAll();
    }

    @Test
    void testViewAllItems_NotFound() {
        when(itemDAO.findAll()).thenReturn(Arrays.asList());

        assertThrows(ItemException.class, () -> itemService.viewAllItems());
        verify(itemDAO, times(1)).findAll();
    }
}
