package com.uetty.cloud.utils.translate.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ResponseYouDao {
    private String errorCode;//错误返回码
    private String query;//源语言
    private List<String> translation;//翻译结果
    private String l;//翻译结果
    private List<Map<String,Object>> web;
    private Map<String,Object> dict;
    private Map<String,Object> webdict;
}
