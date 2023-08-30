package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    public static final String USER_1 = "user1";
    public static final String USER_2 = "user2";
    public static final String PASSWORD = "Hashed";
    public static final String PASSWORD_1 = "password1";
    public static final String PASSWORD_2 = "password2";
    public static final String PASSWORD_MIN_LENGTH = "pas";

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Before
    public void init() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void testCreateUserSuccess() throws Exception {
        when(bCryptPasswordEncoder.encode(PASSWORD_1)).thenReturn(PASSWORD);
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USER_1);
        createUserRequest.setPassword(PASSWORD_1);
        createUserRequest.setConfirmPassword(PASSWORD_1);
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals(USER_1, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
    }

    @Test
    public void testFindUserByUserName() throws Exception {
        User userFake = getUser();
        when(userRepo.findByUsername(USER_1)).thenReturn(userFake);
        final ResponseEntity<User> response = userController.findByUserName(USER_1);
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(userFake.getUsername(), user.getUsername());
    }

    @Test
    public void testFindUserByUserNameNotExists() throws Exception {
        when(userRepo.findByUsername(USER_2)).thenReturn(null);
        final ResponseEntity<User> response = userController.findByUserName(USER_2);
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void testCreateUserNameExists() throws Exception {
        User userFake = getUser();
        when(userRepo.findByUsername(USER_1)).thenReturn(userFake);

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USER_1);
        createUserRequest.setPassword(PASSWORD_1);

        thrown.expect(Exception.class);
        thrown.expectMessage("Username is exists");
        userController.createUser(createUserRequest);
    }

    @Test
    public void testCreateUserPasswordNotSame() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USER_1);
        createUserRequest.setPassword(PASSWORD_1);
        createUserRequest.setConfirmPassword(PASSWORD_2);

        thrown.expect(Exception.class);
        thrown.expectMessage("Confirm password is not same");
        userController.createUser(createUserRequest);
    }

    @Test
    public void testCreateUserPasswordLengthNotMin() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USER_1);
        createUserRequest.setPassword(PASSWORD_MIN_LENGTH);
        createUserRequest.setConfirmPassword(PASSWORD_MIN_LENGTH);

        thrown.expect(Exception.class);
        thrown.expectMessage("Password must more 6 character");
        userController.createUser(createUserRequest);
    }

    @Test
    public void testFindUserById() throws Exception {
        User userFake = getUser();
        when(userRepo.findById(0L)).thenReturn(Optional.of((userFake)));
        final ResponseEntity<User> response = userController.findById(0L);
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(userFake.getUsername(), user.getUsername());
    }

    @Test
    public void testFindUserByIdNotExists() throws Exception {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
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
}
