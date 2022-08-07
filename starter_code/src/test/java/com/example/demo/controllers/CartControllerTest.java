package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class CartControllerTest {
    private CartController cartController;
    private CartRepository cartRepository = mock(CartRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp(){
        cartController= new CartController(userRepository,cartRepository,itemRepository);
        TestUtils.InjectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.InjectObjects(cartController, "userRepository", userRepository);
        TestUtils.InjectObjects(cartController, "itemRepository", itemRepository);
    }
    public ModifyCartRequest modifyCartRequest(){

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("Sam");
        cartRequest.setQuantity(10);


        return cartRequest;
    }

    public Item item(){
        Item Items = new Item();
        Items.setId(2L);
        Items.setName("Book");
        Items.setDescription("Reading");
        Items.setPrice(new BigDecimal(5));
        return Items;
    }

    public User user(){
        User appUser = new User();
        appUser.setUsername("Sam");
        appUser.setPassword("testMainTest");
        appUser.setId(1L);
        appUser.setCart(cart());
        return appUser;
    }

    public Cart cart(){
        Cart cart = new Cart();
        cart.setId(1L);
        return cart;
    }


    @Test
    public void addToCart(){
        ModifyCartRequest modifyCartRequest = modifyCartRequest();
        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(user());
        User user = userRepository.findByUsername(modifyCartRequest.getUsername());
        assertThat(user.getUsername()).isEqualTo(modifyCartRequest.getUsername());
        Item newItem = item();
        modifyCartRequest.setItemId(newItem.getId());

        when(itemRepository.findById(newItem.getId())).thenReturn(Optional.of(item()));
        Optional<Item> optionalItem = itemRepository.findById(newItem.getId());
        assertThat(optionalItem.get()).isEqualTo(newItem);

        Cart cart = user.getCart();
        when(cartRepository.save(cart)).thenReturn(cart());

        ResponseEntity<Cart> entity= cartController.addTocart(modifyCartRequest);
        Cart cart1 = entity.getBody();

        assertEquals(cart1.getId(), cart.getId());
        assertEquals(cart1.getItems(), cart.getItems());
    }

}
