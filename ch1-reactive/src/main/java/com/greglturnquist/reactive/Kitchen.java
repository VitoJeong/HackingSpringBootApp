package com.greglturnquist.reactive;

import reactor.core.publisher.Flux;

public interface Kitchen {
    Flux<Dish> getDishes();
}
