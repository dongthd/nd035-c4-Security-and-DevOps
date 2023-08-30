package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    public static final String USER_1 = "user1";
    public static final String USER_2 = "user2";
    public static final String PASSWORD = "Hashed";
    public static final String ITEM_1 = "Item 1";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddToCart() {
        User userFake = getUser();
        when(userRepository.findByUsername(USER_1)).thenReturn(userFake);

        Item itemFake = getItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of((itemFake)));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(10);
        modifyCartRequest.setUsername(USER_1);
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(155.0), cart.getTotal());
    }

    @Test
    public void testAddToCartWithUserNotExists() {
        when(userRepository.findByUsername(USER_2)).thenReturn(null);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(10);
        modifyCartRequest.setUsername(USER_2);

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNull(cart);
    }

    @Test
    public void testAddToCartWithItemNotExists() {
        User userFake = getUser();
        when(userRepository.findByUsername(USER_1)).thenReturn(userFake);
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setQuantity(10);
        modifyCartRequest.setUsername(USER_1);

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNull(cart);
    }

    @Test
    public void testRemoveFromCart() throws Exception {
        User userFake = getUser();
        when(userRepository.findByUsername(USER_1)).thenReturn(userFake);

        Item itemFake = getItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of((itemFake)));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername(USER_1);
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        ModifyCartRequest modifyCartRequest2 = new ModifyCartRequest();
        modifyCartRequest2.setItemId(1L);
        modifyCartRequest2.setQuantity(1);
        modifyCartRequest2.setUsername(USER_1);
        ResponseEntity<Cart> response2 = cartController.removeFromCart(modifyCartRequest2);

        assertNotNull(response2);
        assertEquals(200, response2.getStatusCodeValue());
        Cart cart = response2.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(15.5), cart.getTotal());
    }

    @Test
    public void testRemoveFromCartWithUserNotExists() throws Exception {
        when(userRepository.findByUsername(USER_2)).thenReturn(null);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(10);
        modifyCartRequest.setUsername(USER_2);

        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNull(cart);
    }

    @Test
    public void testRemoveFromCartWithItemNotExists() throws Exception {
        User userFake = getUser();
        when(userRepository.findByUsername(USER_1)).thenReturn(userFake);
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setQuantity(10);
        modifyCartRequest.setUsername(USER_1);

        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNull(cart);
    }

    @Test
    public void testRemoveFromCartWithQuantityMoreExists() throws Exception {
        User userFake = getUser();
        when(userRepository.findByUsername(USER_1)).thenReturn(userFake);

        Item itemFake = getItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of((itemFake)));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername(USER_1);
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        ModifyCartRequest modifyCartRequest2 = new ModifyCartRequest();
        modifyCartRequest2.setItemId(1L);
        modifyCartRequest2.setQuantity(10);
        modifyCartRequest2.setUsername(USER_1);

        thrown.expect(Exception.class);
        thrown.expectMessage("Quantity remove more than quantity exists");
        cartController.removeFromCart(modifyCartRequest2);
    }


    private User getUser() {
        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername(USER_1);
        user.setPassword(PASSWORD);
        user.setCart(cart);
        return user;
    }

    private Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName(ITEM_1);
        BigDecimal price = BigDecimal.valueOf(15.5);
        item.setPrice(price);
        item.setDescription("Description item 1");
        return item;
    }
}
