package com.example.demo.controllers;

import com.example.demo.TestUtils;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    public void Close(){
        userRepository.deleteAll();
    }

    @Test
    public void createUser() throws Exception{
        CreateUserRequest userRequest=createUserRequest();
        final ResponseEntity<User> response =userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        verify(cartRepository).save(any());
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).isEqualTo(user);

    }

    @Test
    public void findByUserName() {
        CreateUserRequest request=createUserRequest();
        ResponseEntity<User> entity = userController.createUser(request);
        User body = entity.getBody();
        when(userRepository.findById(body.getId())).thenReturn(Optional.of(body));
        ResponseEntity<User> user = userController.findById(body.getId());
        assertEquals(200, user.getStatusCodeValue());
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findById(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(body.getId());

    }

    @Test
    public void testWrongUserId(){
        User USER = new User();
        final ResponseEntity<User> responseEntity = userController.findById(USER.getId());
        User user2 = responseEntity.getBody();
        assertThat(user2).isEqualTo(null);
    }




    @Test
    public void signUp(){
        CreateUserRequest userRequest = createUserRequest();
        ResponseEntity<User> responseEntity = userController.createUser(userRequest);
        assertNotNull(responseEntity);
        assertThat(responseEntity.getBody().getUsername()).isEqualTo(createUserRequest().getUsername());
        assertThat(responseEntity.getBody().getPassword()).isEqualTo("thisIsHashed");

    }

    public CreateUserRequest createUserRequest(){

        when(encoder.encode("newPassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("newPassword");
        request.setConfirmPassword("newPassword");
        return request;
    }



}
