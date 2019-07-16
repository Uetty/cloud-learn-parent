package com.uetty.invoice;

import com.itextpdf.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.io.OutputStream;

public class InvoiceApplication {

    public static void main(String[] args) throws IOException, DocumentException {
        Config config = readConfig();
    }



    /**
     * 生成 PDF 文件
     *
     * @param out  输出流
     * @param html HTML字符串
     * @throws IOException       IO异常
     * @throws DocumentException Document异常
     */
    public static void createPDF(OutputStream out, String html) throws IOException, com.lowagie.text.DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        // 解决中文支持问题
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFont("pdf/font/fangsong.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont("pdf/font/PingFangSC.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.layout();
        renderer.createPDF(out);
    }

    private static Config readConfig() throws IOException {
        String property = System.getProperty("user.dir");
        String json = FileUtil.readFile(property + "/config.json");
        return JacksonUtil.jackson.json2Obj(json, Config.class);
    }

}
