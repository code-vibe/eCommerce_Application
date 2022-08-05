package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @Test
    public void addToCart(){
        ModifyCartRequest request = modifyCartRequest();
        when(userRepository.findByUsername(request.getUsername())).thenReturn(user());
        User user = userRepository.findByUsername(request.getUsername());
        assertThat(user.getUsername()).isEqualTo(request.getUsername());
        Item item = item();
        request.setItemId(item.getId());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item()));
        Optional<Item> itemrequested = itemRepository.findById(item.getId());
        assertThat(itemrequested.get()).isEqualTo(item);

        Cart cart = user.getCart();
        when(cartRepository.save(cart)).thenReturn(cart());

        ResponseEntity<Cart> responseEntity= cartController.addTocart(request);
        Cart returnedCart = responseEntity.getBody();
        Assert.assertNotNull(returnedCart);

        Assert.assertEquals(returnedCart.getId(), cart.getId());
        Assert.assertEquals(returnedCart.getItems(), cart.getItems());
    }

    @Test
    public void removeFromCart(){
        User user = user();
        user.getCart().addItem(item());
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item().getId());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user());
        when(itemRepository.findById(item().getId())).thenReturn(Optional.of(item()));


        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(request);
        Cart cart = cartResponseEntity.getBody();
        Assert.assertEquals(cart.getItems(), null);


    }

    public ModifyCartRequest modifyCartRequest(){

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("Tobi");
        modifyCartRequest.setQuantity(10);


        return modifyCartRequest;
    }

    public Item item(){
        Item item = new Item();
        item.setId(2L);
        item.setName("Spoon");
        item.setDescription("Used for eating");
        item.setPrice(new BigDecimal(5));
        return item;
    }

    public User user(){
        User user = new User();
        user.setUsername("Tobi");
        user.setPassword("testMainTest");
        user.setId(1L);
        user.setCart(cart());
        return user;
    }

    public Cart cart(){
        Cart cart = new Cart();
        cart.setId(1L);
        return cart;
    }

}
