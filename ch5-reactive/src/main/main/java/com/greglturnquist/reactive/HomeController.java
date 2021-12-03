package com.greglturnquist.reactive;

import com.greglturnquist.reactive.repository.CartRepository;
import com.greglturnquist.reactive.repository.ItemRepository;
import com.greglturnquist.reactive.service.CartService;
import com.greglturnquist.reactive.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final InventoryService inventoryService;

    @GetMapping
    Mono<Rendering> home() {
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items", inventoryService.getInventory())
                .modelAttribute("cart", inventoryService.getCart("My Cart")
                        .defaultIfEmpty(new Cart("My Cart")))
                .build());
    }


    @PostMapping("/add/{id}")
    Mono<String> addToCart(@PathVariable String id) {
        return inventoryService.addItemToCart("My Cart", id)
                .thenReturn("redirect:/");
    }

    @DeleteMapping("/remove/{id}")
    Mono<String> removeFromCart(@PathVariable String id) {
        return inventoryService.removeOneFromCart("My Cart", id)
                .thenReturn("redirect:/");
    }

    @PostMapping
    Mono<String> createItem(@ModelAttribute Item newItem) {
        return inventoryService.saveItem(newItem) //
                .thenReturn("redirect:/");
    }

    @DeleteMapping("/delete/{id}")
    Mono<String> deleteItem(@PathVariable String id) {
        return inventoryService.deleteItem(id) //
                .thenReturn("redirect:/");
    }

}
