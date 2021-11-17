package com.greglturnquist.reactive;

public class Dish {

    private String description;
    private boolean delivered = false;

    /**
     * Server가 제공하는 데이터셋을 받아서
     *  서비스의 컨슈머가 원하는대로 변환하고 조작할 수 있다.
     *
     * 함수형 프로그래밍에서는 기존 객체의 상태를 변환하는 대신,
     *  변환된 상태를 가진 새 객체를 만들어 사용하는 방식을 선호한다.
     *
     * @param dish
     * @return
     */
    public static Dish deliver(Dish dish) {
        Dish deliveredDish = new Dish(dish.description);
        deliveredDish.delivered = true;
        return deliveredDish;
    }

    Dish(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDelivered() {
        return delivered;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "description='" + description + '\'' +
                ", delivered=" + delivered +
                '}';
    }
}
