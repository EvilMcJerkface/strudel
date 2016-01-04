package com.nec.strudel.bench.auction.interactions.entity;

public class EntityPair<E1, E2> {
    public static <E1, E2>
        EntityPair<E1, E2> of(E1 first, E2 second) {
        return new EntityPair<E1, E2>(first, second);
    }
    private final E1 first;
    private final E2 second;
    public EntityPair(E1 first, E2 second) {
        this.first = first;
        this.second = second;
    }
    public E1 getFirst() {
        return first;
    }
    public E2 getSecond() {
        return second;
    }
}
