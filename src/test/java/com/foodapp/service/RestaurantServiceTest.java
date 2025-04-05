package com.foodapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodapp.authservice.UserSessionService;
import com.foodapp.exceptions.RestaurantException;
import com.foodapp.model.Address;
import com.foodapp.model.Restaurant;
import com.foodapp.repository.AddressDAO;
import com.foodapp.repository.RestaurantDAO;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantDAO restaurantDAO;

    @Mock
    private AddressDAO addressDAO;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant dummyRestaurant;
    private Address dummyAddress;

    @BeforeEach
    void setUp() {
        dummyAddress = new Address("123 Street", "City", "State", "India", "123456");
        dummyRestaurant = new Restaurant(1, "Food Haven", "John Doe", "9876543210", dummyAddress, null);
    }

    @Test
    void testAddRestaurant_Success() throws RestaurantException {
        when(addressDAO.save(any(Address.class))).thenReturn(dummyAddress); // ✅ Mock addressDAO.save()
        when(restaurantDAO.save(any(Restaurant.class))).thenReturn(dummyRestaurant);

        Restaurant savedRestaurant = restaurantService.addRestaurant(dummyRestaurant);

        assertNotNull(savedRestaurant);
        assertEquals("Food Haven", savedRestaurant.getRestaurantName());

        verify(addressDAO, times(1)).save(dummyAddress); // ✅ Verify that addressDAO.save() is called
        verify(restaurantDAO, times(1)).save(dummyRestaurant);
    }

    @Test
    void testUpdateRestaurant_Success() throws RestaurantException {
        when(restaurantDAO.findById(dummyRestaurant.getRestaurantId())).thenReturn(Optional.of(dummyRestaurant));
        when(restaurantDAO.save(any(Restaurant.class))).thenReturn(dummyRestaurant);

        Restaurant updatedRestaurant = restaurantService.updateRestaurant(dummyRestaurant);

        assertNotNull(updatedRestaurant);
        assertEquals("Food Haven", updatedRestaurant.getRestaurantName());

        verify(restaurantDAO, times(1)).findById(dummyRestaurant.getRestaurantId());
        verify(restaurantDAO, times(1)).save(dummyRestaurant);
    }

    @Test
    void testUpdateRestaurant_NotFound() {
        when(restaurantDAO.findById(dummyRestaurant.getRestaurantId())).thenReturn(Optional.empty());

        assertThrows(RestaurantException.class, () -> restaurantService.updateRestaurant(dummyRestaurant));

        verify(restaurantDAO, times(1)).findById(dummyRestaurant.getRestaurantId());
    }

    @Test
    void testRemoveRestaurant_Success() throws RestaurantException {
        when(restaurantDAO.findById(dummyRestaurant.getRestaurantId())).thenReturn(Optional.of(dummyRestaurant));
        doNothing().when(restaurantDAO).delete(dummyRestaurant);

        Restaurant removedRestaurant = restaurantService.removeRestaurant(dummyRestaurant.getRestaurantId());

        assertNotNull(removedRestaurant);
        verify(restaurantDAO, times(1)).findById(dummyRestaurant.getRestaurantId());
        verify(restaurantDAO, times(1)).delete(dummyRestaurant);
    }

    @Test
    void testRemoveRestaurant_NotFound() {
        when(restaurantDAO.findById(dummyRestaurant.getRestaurantId())).thenReturn(Optional.empty());

        assertThrows(RestaurantException.class, () -> restaurantService.removeRestaurant(dummyRestaurant.getRestaurantId()));

        verify(restaurantDAO, times(1)).findById(dummyRestaurant.getRestaurantId());
    }

    @Test
    void testViewRestaurant_Success() throws RestaurantException {
        when(restaurantDAO.findById(dummyRestaurant.getRestaurantId())).thenReturn(Optional.of(dummyRestaurant));

        Restaurant retrievedRestaurant = restaurantService.viewRestaurant(dummyRestaurant.getRestaurantId());

        assertNotNull(retrievedRestaurant);
        assertEquals("Food Haven", retrievedRestaurant.getRestaurantName());

        verify(restaurantDAO, times(1)).findById(dummyRestaurant.getRestaurantId());
    }

    @Test
    void testViewRestaurant_NotFound() {
        when(restaurantDAO.findById(dummyRestaurant.getRestaurantId())).thenReturn(Optional.empty());

        assertThrows(RestaurantException.class, () -> restaurantService.viewRestaurant(dummyRestaurant.getRestaurantId()));

        verify(restaurantDAO, times(1)).findById(dummyRestaurant.getRestaurantId());
    }
}
