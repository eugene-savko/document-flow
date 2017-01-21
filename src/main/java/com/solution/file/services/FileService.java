package com.solution.file.services;

import java.util.Map;

public interface FileService {

    byte[] generationPdf(String template, Map<String, Object> variables) throws Exception;

}
