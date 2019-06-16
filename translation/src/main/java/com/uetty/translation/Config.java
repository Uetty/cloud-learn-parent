package com.uetty.translation;

import lombok.Data;

import java.util.List;

@Data
public class Config {

    private String from;

    private List<String> to;

    private String xmlPath;

    private String translationPath;

}
