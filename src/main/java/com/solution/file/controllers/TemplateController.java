package com.solution.file.controllers;

import com.mongodb.gridfs.GridFSDBFile;
import com.solution.file.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api/template")
public class TemplateController {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateController.class);

    @Autowired
    private GridFsOperations gridFsTemplate;

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity get() {
        try {
            List<GridFSDBFile> result = gridFsTemplate.find(new Query().addCriteria(GridFsCriteria.whereFilename().is("pdf.pdf")));
            for (GridFSDBFile file : result) {
                file.writeTo("newpdf.pdf");
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.info("Get is completed");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/getpdf", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadUserAvatarImage() {
        GridFSDBFile gridFsFile = gridFsTemplate.find(new Query().addCriteria(GridFsCriteria.whereFilename().is("pdf.pdf"))).get(0);

        return ResponseEntity.ok()
                .contentLength(gridFsFile.getLength())
                .contentType(MediaType.parseMediaType(gridFsFile.getContentType()))
                .body(new InputStreamResource(gridFsFile.getInputStream()));
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ResponseEntity create() {
        try {
            InputStream is = new FileInputStream("pdf.pdf");
            gridFsTemplate.store(is, "pdf.pdf", "application/pdf");
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.info("Create is completed");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ResponseEntity delete() {
        gridFsTemplate.delete(new Query().addCriteria(GridFsCriteria.whereFilename().is("pdf.pdf")));

        LOG.info("Delete is completed");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/generation", method = RequestMethod.GET)
    public ResponseEntity generation() {
        byte [] pdf;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("blogTitle", "Freemarker Template Demo");
            map.put("message", "Getting started with Freemarker.<br/>Find a simple Freemarker demo.");
            List<String> references = new ArrayList<>();
            references.add("URL One");
            references.add("URL Two");
            references.add("URL Three");
            map.put("references", references);

            pdf = fileService.generationPdf("pdf-template.ftl", map);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(new ByteArrayInputStream(pdf)));
    }
}
