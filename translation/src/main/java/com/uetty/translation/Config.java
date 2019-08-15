package com.uetty.translation;

import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

@Data
public class Config {

    private String from;

    private List<String> to;

    private String xmlPath;

    private String translationPath;

    public static void main(String[] args) {
        System.out.println(Stream.ofNullable(null).count());
    }

}
