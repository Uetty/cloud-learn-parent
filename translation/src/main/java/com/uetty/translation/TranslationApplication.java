package com.uetty.translation;

import com.uetty.cloud.utils.file.FileUtil;

public class TranslationApplication {

    public static void main(String[] args) {
        String property = System.getProperty("user.dir");
        String json = FileUtil.readFile(property + "/config.json");
    }

}
