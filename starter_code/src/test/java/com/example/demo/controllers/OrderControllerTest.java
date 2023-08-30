package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    public static final String USER_1 = "user1";
    public static final String USER_2 = "user2";
    public static final String PASSWORD = "Hashed";
    public static final String ITEM_1 = "Item 1";

    @Before
    public void init() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController,"orderRepository", orderRepository);
    }

    @Test
    public void testSubmitOrder() {
        User userFake = getUser();
        when(userRepository.findByUsername(USER_1)).thenReturn(userFake);

        ResponseEntity<UserOrder> response = orderController.submit(USER_1);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
    }

    @Test
    public void testSubmitOrderWithUserNotExists() {
        when(userRepository.findByUsername(USER_2)).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit(USER_2);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersForUser() {
        User userFake = getUser();
        when(userRepository.findByUsername(USER_1)).thenReturn(userFake);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USER_1);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
    }

    @Test
    public void testGetOrdersForUserWithUserNotExists() {
        when(userRepository.findByUsername(USER_2)).thenReturn(null);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USER_2);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private User getUser() {
        Item item = getItem();

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername(USER_1);
        user.setPassword(PASSWORD);
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(Collections.singletonList(item));
        BigDecimal total = BigDecimal.valueOf(15.5);
        cart.setTotal(total);
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
