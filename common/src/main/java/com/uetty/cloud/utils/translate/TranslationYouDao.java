package com.uetty.cloud.utils.translate;

import com.google.common.collect.Maps;
import com.uetty.cloud.utils.Convert;
import com.uetty.cloud.utils.JacksonUtil;
import com.uetty.cloud.utils.encryption.SHA256Util;
import com.uetty.cloud.utils.translate.response.ResponseYouDao;
import lombok.Data;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * 有道翻译
 */
public class TranslationYouDao {

    /**
     * HTTP地址
     */
    private static final String HTTP_URL = "http://openapi.youdao.com/api";

    private static final String HTTPS_URL = "https://openapi.youdao.com/api";

    private static final String KEY = "RTTDltboSnci3VD1hL421NlWf7dRok88";

    private static final JacksonUtil JACKSON_UTIL = new JacksonUtil().withIgnoreUnknownPro();

    /**
     * @param content 要翻译的文本
     * @param from    语言
     * @return 翻译多个语言
     */
    public String translateMore(String content, String from, String to) throws IOException {
        Map<String, String> resultMap = Maps.newHashMap();
        RequestYouDao request = new RequestYouDao();
        request.setQ(content);
        request.setFrom(from);
        request.setTo(to);
        FormBody.Builder builder = new FormBody.Builder();
        Map<String, String> requestMap = JACKSON_UTIL.bean2Obj(request);
        for (Map.Entry<String, String> entry : requestMap.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        OkHttpClient client = new OkHttpClient();
        Request requset = new Request.Builder()
                .url(HTTPS_URL)
                .post(builder.build())
                .header("Content-Type", "application/json")
                .build();
        Response execute = client.newCall(requset).execute();
        assert execute.body() != null;
        ResponseYouDao responseYouDao = JACKSON_UTIL.json2Obj(execute.body().string(), ResponseYouDao.class);
        return responseYouDao.getTranslation().stream().findAny().orElse("");
    }

    public static void main(String[] args) throws IOException {
        new TranslationYouDao().translateMore("one","en", "ja");
    }

    @Data
    class RequestYouDao {

        private String q;//要翻译的文本
        private String from;//语言列表 (可设置为auto)
        private String to;//目标语言 (可设置为auto)
        //        private String ext;//翻译结果音频格式，支持mp3
//        private String voice;//翻译结果发音选择，0为女声，1为男声，默认为女声
        private String signType = "v3";//签名类型
        private String curtime = Convert.toString(System.currentTimeMillis() / 1000);//当前UTC时间戳
        private String appKey = "1d21671543c52c04";//应用标识（应用ID）
        private String salt = String.valueOf(System.currentTimeMillis());//随机字符串,最好是UUID
        private String sign;//签名信息(sha256(appKey+input+salt+curtime+密钥))


        public void setQ(String q) {
            this.q = q;
            String input = this.q.length() > 20 ? this.q.substring(0, 10) + this.q.length() + this.q.substring(this.q.length() - 10) : this.q;
            this.sign = SHA256Util.getDigest(this.appKey + input + this.salt + this.curtime + KEY);
        }
    }
}
