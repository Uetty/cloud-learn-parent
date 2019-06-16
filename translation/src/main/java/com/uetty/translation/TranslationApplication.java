package com.uetty.translation;

import com.uetty.cloud.utils.JacksonUtil;
import com.uetty.cloud.utils.excel.ExcelUtil;
import com.uetty.cloud.utils.file.FileUtil;
import com.uetty.cloud.utils.translate.TranslationYouDao;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TranslationApplication {

    public static void main(String[] args) throws IOException {
        String property = System.getProperty("user.dir");
        String json = FileUtil.readFile(property + "/config.json");
        Config config =  JacksonUtil.jackson.json2Obj(json, Config.class);
        Map<String, Map<String, List<String>>> data = ExcelUtil.getData(config.getXmlPath());
        String ss = JacksonUtil.jackson.obj2Json(data);
        System.err.println(ss);
        System.err.println(new String(ss.getBytes(), "UTF-8"));
        transAndOut(config, data);
    }

    /**
     * 翻译并输出
     */
    private static void transAndOut(Config config, Map<String, Map<String, List<String>>> data) {
        String fileName = config.getXmlPath().substring(config.getXmlPath().lastIndexOf("/") + 1);
        TranslationYouDao youDao = new TranslationYouDao();
        config.getTo().forEach(la -> {
            //翻译
            Map<String, Map<String, List<String>>> copy = new HashMap<>(data);
            copy.values().forEach(a -> a.forEach((key, value) -> a.put(key, value.stream().map(content -> youDao.translateMore(content, config.getFrom(), la)).collect(Collectors.toList()))));
            //输出
            String[] split = fileName.split("\\.");
            String translateFileName = split[0] + "-" + la + "."+split[1];
            ExcelUtil.writeData(config.getTranslationPath()+translateFileName,copy);
        });

    }

}
