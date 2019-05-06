package com.uetty.cloud.feign.provider.rector;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public class RectorLearn {

    private void rector() {
        Mono.delay(Duration.ofSeconds(5))
                .doOnNext(System.out::println)
                .subscribe();
    }

}
