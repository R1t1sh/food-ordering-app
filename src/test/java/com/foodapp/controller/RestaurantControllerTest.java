package com.foodapp.controller;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionService;
import com.foodapp.exceptions.RestaurantException;
import com.foodapp.model.Restaurant;
import com.foodapp.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private RestaurantServiceController restaurantController;

    private final Integer VALID_RESTAURANT_ID = 1;
    private final Integer INVALID_RESTAURANT_ID = 100;
    private Restaurant dummyRestaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dummyRestaurant = new Restaurant();
        dummyRestaurant.setRestaurantId(VALID_RESTAURANT_ID);
        dummyRestaurant.setRestaurantName("Test Restaurant");
    }

    @Test
    void testAddRestaurant_AsAdmin() throws RestaurantException, AuthorizationException {
        when(userSessionService.getUserRole("ADMIN_KEY")).thenReturn("ADMIN");
        when(restaurantService.addRestaurant(any(Restaurant.class))).thenReturn(dummyRestaurant);

        ResponseEntity<Restaurant> response = restaurantController.saveResturant(dummyRestaurant, "ADMIN_KEY");

        assertNotNull(response.getBody());
        assertEquals("Test Restaurant", response.getBody().getRestaurantName());
        verify(restaurantService, times(1)).addRestaurant(any(Restaurant.class));
    }

    @Test
    void testAddRestaurant_AsUser_ShouldThrowException() throws AuthorizationException, RestaurantException {
        when(userSessionService.getUserRole("USER_KEY")).thenReturn("USER");

        AuthorizationException exception = assertThrows(AuthorizationException.class,
                () -> restaurantController.saveResturant(dummyRestaurant, "USER_KEY"));

        assertEquals("Access Denied: Only ADMIN can add restaurants.", exception.getMessage());
        verify(restaurantService, never()).addRestaurant(any(Restaurant.class));
    }

    @Test
    void testDeleteRestaurant_AsAdmin() throws RestaurantException, AuthorizationException {
        when(userSessionService.getUserRole("ADMIN_KEY")).thenReturn("ADMIN");
        when(restaurantService.removeRestaurant(VALID_RESTAURANT_ID)).thenReturn(dummyRestaurant);

        ResponseEntity<Restaurant> response = restaurantController.deleteRestaurant(VALID_RESTAURANT_ID, "ADMIN_KEY");

        assertNotNull(response.getBody());
        assertEquals(VALID_RESTAURANT_ID, response.getBody().getRestaurantId());
        verify(restaurantService, times(1)).removeRestaurant(VALID_RESTAURANT_ID);
    }

    @Test
    void testDeleteRestaurant_AsUser_ShouldThrowException() throws AuthorizationException, RestaurantException {
        when(userSessionService.getUserRole("USER_KEY")).thenReturn("USER");

        AuthorizationException exception = assertThrows(AuthorizationException.class,
                () -> restaurantController.deleteRestaurant(VALID_RESTAURANT_ID, "USER_KEY"));

        assertEquals("Access Denied: Only ADMIN can remove restaurants.", exception.getMessage());
        verify(restaurantService, never()).removeRestaurant(anyInt());
    }

    @Test
    void testGetRestaurantById_WithValidSession() throws RestaurantException, AuthorizationException {
        when(userSessionService.getUserSessionId("VALID_KEY")).thenReturn(1);
        when(restaurantService.viewRestaurant(VALID_RESTAURANT_ID)).thenReturn(dummyRestaurant);

        ResponseEntity<Restaurant> response = restaurantController.getByResturantId(VALID_RESTAURANT_ID, "VALID_KEY");

        assertNotNull(response.getBody());
        assertEquals("Test Restaurant", response.getBody().getRestaurantName());
        verify(restaurantService, times(1)).viewRestaurant(VALID_RESTAURANT_ID);
    }

    @Test
    void testGetRestaurantById_WithInvalidSession_ShouldThrowException() throws AuthorizationException {
        when(userSessionService.getUserSessionId("INVALID_KEY")).thenReturn(null);

        assertThrows(RestaurantException.class, () ->
                restaurantController.getByResturantId(VALID_RESTAURANT_ID, "INVALID_KEY"));
    }
}
