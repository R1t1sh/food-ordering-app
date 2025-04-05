package com.foodapp.controller;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionService;
import com.foodapp.exceptions.CategoryException;
import com.foodapp.model.Category;
import com.foodapp.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CategoryServiceControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private CategoryServiceController categoryController;

    private Category dummyCategory;

    @BeforeEach
    void setUp() {
        dummyCategory = new Category();
        dummyCategory.setCategoryId(1);
        dummyCategory.setCategoryName("Fast Food");
    }

    @Test
    void testAddCategory_Authorized() throws Exception {
        when(userSessionService.getUserRole("validKey")).thenReturn("ADMIN");
        when(categoryService.addCategory(any(Category.class))).thenReturn(dummyCategory);

        ResponseEntity<Category> response = categoryController.addCategory(dummyCategory, "validKey");

        assertNotNull(response.getBody());
        assertEquals("Fast Food", response.getBody().getCategoryName());
        verify(categoryService, times(1)).addCategory(any(Category.class));
    }

    @Test
    void testAddCategory_Unauthorized() throws AuthorizationException {
        when(userSessionService.getUserRole("invalidKey")).thenReturn("USER");

        Exception exception = assertThrows(Exception.class, () -> categoryController.addCategory(dummyCategory, "invalidKey"));
        assertEquals("Access Denied: Only ADMIN and MANAGER can add categories.", exception.getMessage());
    }

    @Test
    void testUpdateCategory() throws Exception {
        when(userSessionService.getUserRole("validKey")).thenReturn("ADMIN");
        when(categoryService.updateCategory(any(Category.class))).thenReturn(dummyCategory);

        ResponseEntity<Category> response = categoryController.updateCategory(dummyCategory, "validKey");

        assertEquals("Fast Food", response.getBody().getCategoryName());
        verify(categoryService, times(1)).updateCategory(any(Category.class));
    }

    @Test
    void testViewCategory() throws Exception {
        when(userSessionService.getUserSessionId("validKey")).thenReturn(123);
        when(categoryService.viewCategory(1)).thenReturn(dummyCategory);

        ResponseEntity<Category> response = categoryController.getCategory(1, "validKey");

        assertEquals("Fast Food", response.getBody().getCategoryName());
        verify(categoryService, times(1)).viewCategory(1);
    }

    @Test
    void testRemoveCategory() throws Exception {
        when(userSessionService.getUserRole("validKey")).thenReturn("ADMIN");
        when(categoryService.removeCategory(1)).thenReturn(dummyCategory);

        ResponseEntity<Category> response = categoryController.removeCategory(1, "validKey");

        assertEquals("Fast Food", response.getBody().getCategoryName());
        verify(categoryService, times(1)).removeCategory(1);
    }

    @Test
    void testViewAllCategories() throws Exception {
        List<Category> categories = Arrays.asList(dummyCategory, new Category());
        when(categoryService.viewAllCategory()).thenReturn(categories);

        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        assertEquals(2, response.getBody().size());
        verify(categoryService, times(1)).viewAllCategory();
    }
}
