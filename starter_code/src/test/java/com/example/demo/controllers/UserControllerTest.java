package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController(userRepository,cartRepository);
        TestUtils.InjectObjects(userController, "userRepository", userRepository);
        TestUtils.InjectObjects(userController, "cartRepository", cartRepository);
        TestUtils.InjectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @After
    public void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    public void createUser() throws Exception{
        CreateUserRequest userRequest=createUserRequest();
        final ResponseEntity<User> response =userController.createUser(userRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        Assert.assertNotNull(u);
        verify(cartRepository).save(any());
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).isEqualTo(u);

    }

    @Test
    public void findByUserName() {
        CreateUserRequest userRequest=createUserRequest();
        ResponseEntity<User> responseEntity = userController.createUser(userRequest);
        User user = responseEntity.getBody();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        final ResponseEntity<User> user1 = userController.findById(user.getId());
        Assert.assertNotNull(user1);
        Assert.assertEquals(200, user1.getStatusCodeValue());
        ArgumentCaptor<Long> userArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findById(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).isEqualTo(user.getId());

    }

    @Test
    public void testWrongUserId(){
        User user = new User();
        final ResponseEntity<User> responseEntity = userController.findById(user.getId());
        User user1 = responseEntity.getBody();
        assertThat(user1).isEqualTo(null);
    }



    @Test
    public void findByUsername(){
        CreateUserRequest userRequest = createUserRequest();
        ResponseEntity<User> responseEntity = userController.createUser(userRequest);
        User user = responseEntity.getBody();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        final ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        User user1 = response.getBody();
        Assert.assertEquals(user.getUsername(),user1.getUsername());
        verify(userRepository).findByUsername(anyString());
        ArgumentCaptor<String> userArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByUsername(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).isEqualTo(user.getUsername());

    }

    @Test
    public void signUp(){
        CreateUserRequest userRequest = createUserRequest();
        ResponseEntity<User> responseEntity = userController.createUser(userRequest);
        Assert.assertNotNull(responseEntity);
        assertThat(responseEntity.getBody().getUsername()).isEqualTo(createUserRequest().getUsername());
        assertThat(responseEntity.getBody().getPassword()).isEqualTo("thisIsHashed");

    }

    public CreateUserRequest createUserRequest(){

        when(encoder.encode("newPassword")).thenReturn("thisIsHashed");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("newPassword");
        userRequest.setConfirmPassword("newPassword");
        return userRequest;
    }



}
