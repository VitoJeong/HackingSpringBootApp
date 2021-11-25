package com.greglturnquist.reactive;

import com.greglturnquist.reactive.repository.CartRepository;
import com.greglturnquist.reactive.repository.ItemRepository;
import com.greglturnquist.reactive.service.CartService;
import com.greglturnquist.reactive.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final InventoryService inventoryService;

    private String myCart = "My Cart";

    /**
     * Mono Redering: View와 Attribute를 포함하는 웹플럭스 컨테이너
     *
     * @return
     */
    @GetMapping
    Mono<Rendering> home() {
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items",
                        itemRepository.findAll().doOnNext(item -> home().log()))
                .modelAttribute("cart",
                        cartRepository.findById(myCart)
                                .defaultIfEmpty(new Cart(myCart)))
                .build());
    }


    @PostMapping("/add/{id}")
    Mono<String> addToCart(@PathVariable String id) {
        return inventoryService.addItemToCart(myCart, id)
                .thenReturn("redirect:/");
    }

}
