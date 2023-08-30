package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);

    public static final String ITEM_1 = "Item 1";

    @Before
    public void init() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void testGetItems() {
        List<Item> items = new ArrayList<>();
        Item itemFake = getItem();
        items.add(itemFake);
        when(itemRepo.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> returned = response.getBody();
        assertNotNull(returned);
    }

    @Test
    public void testGetItemById() {
        Item itemFake = getItem();
        when(itemRepo.findById(0L)).thenReturn(Optional.of((itemFake)));
        final ResponseEntity<Item> response = itemController.getItemById(0L);
        Item item = response.getBody();
        assertNotNull(item);
        assertEquals(itemFake.getId(), item.getId());
    }

    @Test
    public void testGetItemByIdNotExists() {
        final ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetItemsByName() {
        List<Item> items = new ArrayList<>();
        Item itemFake = getItem();
        items.add(itemFake);
        when(itemRepo.findByName(itemFake.getName())).thenReturn(items);
        final ResponseEntity<List<Item>> response = itemController.getItemsByName(itemFake.getName());
        List<Item> itemResponse = response.getBody();
        assertNotNull(itemResponse);
        assertEquals(itemResponse.get(0).getName(), itemFake.getName());
    }

    @Test
    public void testGetItemsByNameNotExists() {
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Some item other");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
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
