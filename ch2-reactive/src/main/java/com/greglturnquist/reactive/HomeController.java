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
import org.springframework.web.bind.annotation.RequestParam;
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
        // Webflux 컨테이너인 Mono<Rendering>을 반환
        // view(): 렌더링에 사용할 템플릿 이름 지정
        // modelAttribute(): 템플릿에서 사용될 데이터 지정
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items",
                        itemRepository.findAll())
                .modelAttribute("cart",
                        cartRepository.findById(myCart)
                                // 장바구니를 조회해서 없으면 새로운 Cart 생성
                                .defaultIfEmpty(new Cart(myCart)))
                .build());
    }

    // 비즈니스 로직이 아닌 웹 요청 처리만 컨트롤러가 담당하도록 설계해야함.
    // @PostMapping("/add/{id}")
    // Mono<String> addToCart(@PathVariable String id) {
    //     // Item을 장바구니에 추가하는 API
    //     return cartRepository.findById(myCart)
    //             .defaultIfEmpty(new Cart(myCart))
    //             .flatMap(cart -> cart.getCartItems().stream()
    //                     .filter(cartItem -> cartItem.getItem()
    //                             .getId().equals(id))
    //                     // 새로 담은 상품과 동일한 상품이 이미 있는지 확인
    //                     .findAny()
    //                     // 이미 같은 상품이 장바구니에 있다면
    //                     .map(cartItem -> {
    //                         // 상품의 수량만 증가시켜 장바구니를 Mono에 담아 반환
    //                         cartItem.increment();
    //                         return Mono.just(cart);
    //                     })
    //                     .orElseGet(() -> {
    //                         // 새로 담은 상품이 장바구니에 담겨 있지 않다면
    //                         return itemRepository.findById(id)
    //                                 // 상품을 조회 한 후 수량을 1로 지정한 후
    //                                 .map(item -> new CartItem(item))
    //                                 .map(cartItem -> {
    //                                     // CartItem을 장바구니에 추가한 후 장바구니에 담아 반환
    //                                     cart.getCartItems().add(cartItem);
    //                                     return cart;
    //                                 });
    //                     }))
    //             // 업데이트된 장바구니를 DB에 저장
    //             .flatMap(cart -> cartRepository.save(cart))
    //             // 리다이렉트
    //             .thenReturn("redirect:/");
    // }

    // 상품 담기 기능을 서비스로 위임해서 간결해진 메서드
    @PostMapping("/add/{id}")
    Mono<String> addToCart(@PathVariable String id) {
        return cartService.addToCart(myCart, id)
                .thenReturn("redirect:/");
    }

    @GetMapping("/search")
    Mono<Rendering> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam boolean useAnd) {
        return Mono.just(Rendering.view("search.html")
                .modelAttribute("items",
                        inventoryService.searchByExample(name, description, useAnd))
                .modelAttribute("cart",
                        this.cartRepository.findById("My Cart")
                                .defaultIfEmpty(new Cart("My Cart")))
                .build());
    }
}
