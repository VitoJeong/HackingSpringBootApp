package com.greglturnquist.reactive;

import com.greglturnquist.reactive.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ApiItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    Flux<Item> findAll() {
        return itemRepository.findAll();
    }

    @GetMapping("/{id}")
    Mono<Item> findOne(@PathVariable String id) {
        return itemRepository.findById(id);
    }

    @PostMapping
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<Item> item) {

        return item.flatMap(itemRepository::save)
                .map(savedItem -> ResponseEntity
                        .created(URI.create("/api/items/" + savedItem.getId()))
                        .body(savedItem));
    }

    @PutMapping("/{id}")
    Mono<ResponseEntity<?>> updateItem(@PathVariable String id, @RequestBody Mono<Item> item) {

        return item
                .map(content ->
                        new Item(id, content.getName(),
                                content.getDescription(), content.getPrice()))
                .flatMap(itemRepository::save)
                .map(ResponseEntity::ok);

    }

}
