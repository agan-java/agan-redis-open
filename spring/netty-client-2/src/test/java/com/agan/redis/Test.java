package com.agan.redis;

import io.lettuce.core.cluster.SlotHash;

public class Test {
    public static void main(String[] args) {

        System.out.println(io.lettuce.core.cluster.SlotHash.getSlot("A"));
        System.out.println(io.lettuce.core.cluster.SlotHash.getSlot("B"));
        System.out.println(io.lettuce.core.cluster.SlotHash.getSlot("C"));
        System.out.println(io.lettuce.core.cluster.SlotHash.getSlot("hello"));
    }
}
