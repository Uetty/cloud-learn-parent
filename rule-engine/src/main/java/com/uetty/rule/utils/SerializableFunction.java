package com.uetty.rule.utils;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T,R>, Serializable {
}
