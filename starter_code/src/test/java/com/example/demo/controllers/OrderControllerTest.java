package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository  = mock(UserRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController(userRepository,orderRepository);
        TestUtils.InjectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.InjectObjects(orderController, "userRepository", userRepository);
    }

    public User user(){
        User user = new User();
        user.setUsername("Sam");
        user.setPassword("testMainTest");
        user.setId(1L);
        user.setCart(cart());
        return user;
    }

    public Cart cart(){
        Cart cart = new Cart();
        cart.setId(1L);
        cart.addItem(item());
        return cart;
    }
    public Item item(){
        Item item = new Item();
        item.setId(2L);
        item.setName("Spoon");
        item.setDescription("Used for eating");
        item.setPrice(new BigDecimal(2));
        return item;
    }

    public List<UserOrder> TestcreateFromCart(Cart cart) {
        UserOrder order = UserOrder.createFromCart(cart);
        List orderList = new ArrayList<UserOrder>();
        orderList.add(order);
        return orderList;
    }
    @Test
    public void submit(){
        UserOrder orderTest = new UserOrder();
        when(userRepository.findByUsername("Sam")).thenReturn(user());
        User user = userRepository.findByUsername(user().getUsername());
        assertThat(user.getUsername()).isEqualTo(user().getUsername());


        ResponseEntity<UserOrder> responseEntity= orderController.submit("Sam");
        UserOrder response = responseEntity.getBody();
        assertNotNull(response);

        assertThat(response.getItems().size()).isEqualTo(1);
        assertThat(response.getTotal()).isEqualTo(item().getPrice());

    }

    @Test
    public void getOrderForUSer(){
        when(userRepository.findByUsername("Sam")).thenReturn(user());
        when(orderRepository.findByUser(any())).thenReturn(TestcreateFromCart(cart()));

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("Sam");
        assertNotNull(responseEntity);

        List<UserOrder> orders = responseEntity.getBody();
        assertThat(orders.size()).isEqualTo(1);

    }


}
