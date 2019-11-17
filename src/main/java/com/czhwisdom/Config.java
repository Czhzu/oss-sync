package com.czhwisdom;

import lombok.Data;

@Data
public class Config {
    private String endpoint;
    private String bucket;
    private String accessKeyId;
    private String accessKeySecret;
    private String watchPath;
    private String aliyunPath;
}
