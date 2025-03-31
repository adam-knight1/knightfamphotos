package com.fam.knightfam.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {
    private String bucket;
    private String region;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getRegion(){
        return region;
    }

    public void setRegion(String region){
        this.bucket = bucket;
    }
}

