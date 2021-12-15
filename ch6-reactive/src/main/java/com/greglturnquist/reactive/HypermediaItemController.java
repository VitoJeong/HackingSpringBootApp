package com.greglturnquist.reactive;

import static org.springframework.hateoas.mediatype.alps.Alps.*;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import com.greglturnquist.reactive.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.alps.Alps;
import org.springframework.hateoas.mediatype.alps.Type;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HypermediaItemController {

    private final ItemRepository repository;

    @GetMapping("/hypermedia")
    Mono<RepresentationModel<?>> root() {
        HypermediaItemController controller =
                methodOn(HypermediaItemController.class);

        Mono<Link> selfLink = linkTo(controller.root()).withSelfRel().toMono();

        Mono<Link> itemsAggregateLink =
                linkTo(controller.findAll())
                        .withRel(IanaLinkRelations.ITEM)
                        .toMono();

        return selfLink.zipWith(itemsAggregateLink)
                .map(links -> Links.of(links.getT1(), links.getT2()))
                .map(links -> new RepresentationModel<>(links.toList()));
    }

    @GetMapping("/hypermedia/items")
    Mono<CollectionModel<EntityModel<Item>>> findAll() {

        return this.repository.findAll()
                .flatMap(item -> findOne(item.getId()))
                .collectList()
                .flatMap(entityModels -> linkTo(methodOn(HypermediaItemController.class)
                        .findAll()).withSelfRel()
                        .toMono()
                        .map(selfLink -> CollectionModel.of(entityModels, selfLink)));
    }

    @GetMapping("/hypermedia/items/{id}")
    Mono<EntityModel<Item>> findOne(@PathVariable String id) {
        HypermediaItemController controller = methodOn(HypermediaItemController.class); // <1>

        Mono<Link> selfLink = linkTo(controller.findOne(id)).withSelfRel().toMono(); // <2>

        Mono<Link> aggregateLink = linkTo(controller.findAll()) //
                .withRel(IanaLinkRelations.ITEM).toMono(); // <3>

        return Mono.zip(repository.findById(id), selfLink, aggregateLink) // <4>
                .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3()))); // <5>
    }

    @GetMapping("/hypermedia/items/{id}/affordances")
    // <1>
    Mono<EntityModel<Item>> findOneWithAffordances(@PathVariable String id) {
        HypermediaItemController controller = //
                methodOn(HypermediaItemController.class);

        Mono<Link> selfLink = linkTo(controller.findOne(id)).withSelfRel() //
                .andAffordance(controller.updateItem(null, id)) // <2>
                .toMono();

        Mono<Link> aggregateLink = linkTo(controller.findAll()).withRel(IanaLinkRelations.ITEM) //
                .toMono();

        return Mono.zip(repository.findById(id), selfLink, aggregateLink) //
                .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));
    }

    @PostMapping("/hypermedia/items")
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<EntityModel<Item>> item) {
        return item //
                .map(EntityModel::getContent) //
                .flatMap(this.repository::save) //
                .map(Item::getId) //
                .flatMap(this::findOne) //
                .map(newModel -> ResponseEntity.created(newModel //
                        .getRequiredLink(IanaLinkRelations.SELF) //
                        .toUri()).build());
    }

    @PutMapping("/hypermedia/items/{id}") // <1>
    public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<EntityModel<Item>> item, // <2>
                                              @PathVariable String id) {
        return item //
                .map(EntityModel::getContent) //
                .map(content -> new Item(id, content.getName(), // <3>
                        content.getDescription(), content.getPrice())) //
                .flatMap(this.repository::save) // <4>
                .then(findOne(id)) // <5>
                .map(model -> ResponseEntity.noContent() // <6>
                        .location(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).build());
    }

    @GetMapping(value = "/hypermedia/items/profile", produces = MediaTypes.ALPS_JSON_VALUE)
    public Alps profile() {
        return alps()
                .descriptor(Collections.singletonList(descriptor() //
                        .id(Item.class.getSimpleName() + "-repr") //
                        .descriptor(Arrays.stream( //
                                Item.class.getDeclaredFields()) //
                                .map(field -> descriptor() //
                                        .name(field.getName()) //
                                        .type(Type.SEMANTIC) //
                                        .build()) //
                                .collect(Collectors.toList())) //
                        .build())) //
                .build();
    }

}