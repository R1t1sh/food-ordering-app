package com.foodapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.foodapp.exceptions.CategoryException;
import com.foodapp.model.Category;
import com.foodapp.repository.CategoryDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


class CategoryServiceTest {

    @Mock
    private CategoryDAO categoryDAO;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testCategory = new Category(1, "Fast Food");
    }

    @Test
    void testAddCategory_Success() throws CategoryException {
        when(categoryDAO.findById(testCategory.getCategoryId())).thenReturn(Optional.empty());
        when(categoryDAO.save(any(Category.class))).thenReturn(testCategory);

        Category savedCategory = categoryService.addCategory(testCategory);

        assertNotNull(savedCategory);
        assertEquals("Fast Food", savedCategory.getCategoryName());
        verify(categoryDAO, times(1)).save(testCategory);
    }

    @Test
    void testAddCategory_AlreadyExists() {
        when(categoryDAO.findById(testCategory.getCategoryId())).thenReturn(Optional.of(testCategory));

        assertThrows(CategoryException.class, () -> categoryService.addCategory(testCategory));
        verify(categoryDAO, never()).save(any(Category.class));
    }

    @Test
    void testUpdateCategory_Found() throws CategoryException {
        when(categoryDAO.findById(testCategory.getCategoryId())).thenReturn(Optional.of(testCategory));
        when(categoryDAO.save(any(Category.class))).thenReturn(testCategory);

        Category updatedCategory = categoryService.updateCategory(testCategory);

        assertNotNull(updatedCategory);
        assertEquals("Fast Food", updatedCategory.getCategoryName());
        verify(categoryDAO, times(1)).findById(testCategory.getCategoryId());
        verify(categoryDAO, times(1)).save(testCategory);
    }

    @Test
    void testUpdateCategory_NotFound() {
        when(categoryDAO.findById(testCategory.getCategoryId())).thenReturn(Optional.empty());

        assertThrows(CategoryException.class, () -> categoryService.updateCategory(testCategory));
        verify(categoryDAO, times(1)).findById(testCategory.getCategoryId());
        verify(categoryDAO, never()).save(any(Category.class));
    }

    @Test
    void testViewCategory_Found() throws CategoryException {
        when(categoryDAO.findById(testCategory.getCategoryId())).thenReturn(Optional.of(testCategory));

        Category foundCategory = categoryService.viewCategory(testCategory.getCategoryId());

        assertNotNull(foundCategory);
        assertEquals("Fast Food", foundCategory.getCategoryName());
        verify(categoryDAO, times(1)).findById(testCategory.getCategoryId());
    }

    @Test
    void testViewCategory_NotFound() {
        when(categoryDAO.findById(testCategory.getCategoryId())).thenReturn(Optional.empty());

        assertThrows(CategoryException.class, () -> categoryService.viewCategory(testCategory.getCategoryId()));
        verify(categoryDAO, times(1)).findById(testCategory.getCategoryId());
    }

    @Test
    void testRemoveCategory_Found() throws CategoryException {
        when(categoryDAO.findById(testCategory.getCategoryId())).thenReturn(Optional.of(testCategory));

        Category removedCategory = categoryService.removeCategory(testCategory.getCategoryId());

        assertNotNull(removedCategory);
        assertEquals("Fast Food", removedCategory.getCategoryName());
        verify(categoryDAO, times(1)).delete(testCategory);
    }

    @Test
    void testRemoveCategory_NotFound() {
        when(categoryDAO.findById(testCategory.getCategoryId())).thenReturn(Optional.empty());

        assertThrows(CategoryException.class, () -> categoryService.removeCategory(testCategory.getCategoryId()));
        verify(categoryDAO, never()).delete(any(Category.class));
    }

    @Test
    void testViewAllCategories_Found() throws CategoryException {
        List<Category> categoryList = Arrays.asList(
                new Category(1, "Fast Food"),
                new Category(2, "Desserts")
        );
        when(categoryDAO.findAll()).thenReturn(categoryList);

        List<Category> foundCategories = categoryService.viewAllCategory();

        assertNotNull(foundCategories);
        assertEquals(2, foundCategories.size());
        verify(categoryDAO, times(1)).findAll();
    }

    @Test
    void testViewAllCategories_NotFound() {
        when(categoryDAO.findAll()).thenReturn(Collections.emptyList());

        assertThrows(CategoryException.class, () -> categoryService.viewAllCategory());
        verify(categoryDAO, times(1)).findAll();
    }

}
