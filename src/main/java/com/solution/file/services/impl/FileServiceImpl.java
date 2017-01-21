package com.solution.file.services.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.solution.file.services.FileService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Locale;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    private String htmlProcess(String template, Map<String, Object> variables) throws Exception {
        Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_25);
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setLocale(Locale.US);
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        ClassTemplateLoader loader = new ClassTemplateLoader(this.getClass(), "/");
        freemarkerConfig.setTemplateLoader(loader);

        Template tp = freemarkerConfig.getTemplate(template);

        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);

        tp.process(variables, writer);

        String htmlStr = stringWriter.toString();
        writer.flush();
        writer.close();
        return htmlStr;
    }

    @Override
    public byte[] generationPdf(String template, Map<String, Object> variables) throws Exception {
        long startTime = System.currentTimeMillis();
        String htmlStr = htmlProcess(template, variables);
        LOG.info("HTML is completed in {} ms", System.currentTimeMillis() - startTime);

        long htmlToPdfStartTime = System.currentTimeMillis();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();
        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader(htmlStr));
        document.close();

        LOG.info("Generation is completed in {} ms", System.currentTimeMillis() - htmlToPdfStartTime);
        LOG.info("Total generation time is {} ms", System.currentTimeMillis() - startTime);
        return byteArrayOutputStream.toByteArray();
    }
}
