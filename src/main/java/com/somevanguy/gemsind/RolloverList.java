package com.somevanguy.gemsind;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RolloverList<T> extends ArrayList<T> {
    public final int maxSize;
    public RolloverList(int maxSize) {
        super(maxSize);
        this.maxSize = maxSize;
    }

    public void push(T element) {
        if (size() + 1 >= maxSize) {
            removeFirst();
        }
        add(element);
    }
}
