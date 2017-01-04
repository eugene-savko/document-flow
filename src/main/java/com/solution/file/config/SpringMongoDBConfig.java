package com.solution.file.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class SpringMongoDBConfig extends AbstractMongoConfiguration {

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Bean
    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient("localhost");
    }

    @Override
    protected String getDatabaseName() {
        return "fileStore";
    }

}
