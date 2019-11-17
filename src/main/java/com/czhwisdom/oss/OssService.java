package com.czhwisdom.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.czhwisdom.oss.common.StreamUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.Date;
import java.util.List;

public class OssService {

    private OSSClient ossClient;

    /**
     * 使用指定的OSS Endpoint、阿里云颁发的Access Id/Access Key构造一个新的{@link OssService}对象。
     *
     * @param endpoint        OSS服务的Endpoint。
     * @param accessKeyId     访问OSS的Access Key ID。
     * @param secretAccessKey 访问OSS的Secret Access Key。
     */
    public OssService(String endpoint, String accessKeyId, String secretAccessKey) {
        ossClient = new OSSClient(endpoint, accessKeyId, secretAccessKey);
    }

    /**
     * 使用指定的OSS Endpoint、STS提供的临时Token信息(Access Id/Access Key/Security Token) 构造一个新的
     * {@link OssService}对象。
     *
     * @param endpoint        OSS服务的Endpoint。
     * @param accessKeyId     STS提供的临时访问ID。
     * @param secretAccessKey STS提供的访问密钥。
     * @param securityToken   STS提供的安全令牌。
     */
    OssService(String endpoint, String accessKeyId, String secretAccessKey, String securityToken) {
        ossClient = new OSSClient(endpoint, accessKeyId, secretAccessKey, securityToken);
    }

    /**
     * 判断Bucket是否存在
     *
     * @param bucketName
     * @return
     */
    public boolean isBucketExist(String bucketName) {
        return ossClient.doesBucketExist(bucketName);
    }

    /**
     * 获取Bucket地址
     *
     * @param bucketName
     * @return
     */
    public String getBucketLocation(String bucketName) {
        return ossClient.getBucketLocation(bucketName);
    }

    /**
     * 上传文件
     *
     * @param bucketName 用于存储的bucket
     * @param key        保存Object对应的key
     * @param file       保存的文件
     * @return 返回新创建的{@link com.aliyun.oss.model.OSSObject}的ETag值
     * @throws FileNotFoundException
     */
    public String putObject(String bucketName, String key, File file) throws FileNotFoundException {
        // 上传Object.
        PutObjectResult result = ossClient.putObject(bucketName, key, file);

        // 打印ETag
        return result.getETag();
    }

    /**
     * 上传文件 {@link OssService#putObject(String, String, File)}
     *
     * @param bucketName 用于存储的bucket
     * @param key        保存Object对应的key
     * @param filePath   保存的文件
     * @return
     * @throws FileNotFoundException
     */
    public String putObject(String bucketName, String key, String filePath) throws FileNotFoundException {
        return putObject(bucketName, key, new File(filePath));
    }

    /**
     * 上传{@link OSSObject}
     *
     * @param bucketName  用于存储的bucket
     * @param key         保存Object对应的key
     * @param inputStream Object输入流
     * @param metadata    {@link ObjectMetadata} 用户对该object的描述，由一系列name-value对组成；其中ContentLength是必须设置的
     * @return
     */
    public String putObject(String bucketName, String key, InputStream inputStream, ObjectMetadata metadata) {
        // 上传Object.
        PutObjectResult result = ossClient.putObject(bucketName, key, inputStream, metadata);

        // 打印ETag
        return result.getETag();
    }

    /**
     * 上传{@link OSSObject}
     *
     * @param bucketName
     * @param key
     * @param object
     * @return
     */
    public String putObject(String bucketName, String key, byte[] object) {
        return putObject(bucketName, key, object, null);
    }

    public String putObject(String bucketName, String key, byte[] object, Date expirationTime) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(object);

        // 创建上传Object的Metadata
        ObjectMetadata metadata = new ObjectMetadata();
        // 必须设置ContentLength
        metadata.setContentLength(object.length);
        if (expirationTime != null) {
            metadata.setExpirationTime(expirationTime);
        }
        return putObject(bucketName, key, inputStream, metadata);
    }


    /**
     * 上传{@link OSSObject}
     *
     * @param bucketName
     * @param key
     * @param object
     * @return
     * @throws IOException
     */
    public String putObject(String bucketName, String key, Object object) throws IOException {

        byte[] data = StreamUtils.read(object);

        return putObject(bucketName, key, data, null);
    }

    /**
     * 列出Bucket内符合条件的{@link OSSObject}相关信息，非Oject本身内容
     *
     * @param bucketName 用于对Object名字进行分组的字符。所有名字包含指定的前缀且第一次出现Delimiter字符之间的object作为一组元素 :
     *                   CommonPrefixes。
     * @param prefix     限定返回的object key必须以Prefix作为前缀。注意使用prefix查询时，返回的key中仍会包含Prefix。
     * @param delimiter
     * @return
     */
    public ObjectListing listObjects(String bucketName, String prefix, String delimiter) {
        ListObjectsRequest request = new ListObjectsRequest(bucketName);

        if (StringUtils.isNotBlank(prefix)) {
            request.setPrefix(prefix);
        }

        if (StringUtils.isNotBlank(delimiter)) {
            request.setDelimiter(delimiter);
        }

        return ossClient.listObjects(request);
    }

    /**
     * 返回Bucket内符合条件的{@link OSSObject}摘要信息。
     * <p>
     * {@link OssService#listObjects(String, String, String)}
     * </p>
     *
     * @param bucketName
     * @param prefix
     * @param delimiter
     * @return
     */
    public List<OSSObjectSummary> listObjectSummaries(String bucketName, String prefix, String delimiter) {
        return listObjects(bucketName, prefix, delimiter).getObjectSummaries();
    }

    /**
     * 获取{@link OSSObject}
     *
     * @param bucketName
     * @param key
     * @return
     */
    public OSSObject getObject(String bucketName, String key) {
        return ossClient.getObject(bucketName, key);
    }

    /**
     * 获取{@link OSSObject}的content
     *
     * @param bucketName
     * @param key
     * @return
     */
    public byte[] getObjectContent(String bucketName, String key) {
        OSSObject object = getObject(bucketName, key);

        InputStream inputStream = object.getObjectContent();

        try {
            return StreamUtils.read(inputStream);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean doesObjectExist(String bucketName, String key) {
        return ossClient.doesObjectExist(bucketName, key);
    }

    /**
     * 从默认的bucket中取数据
     *
     * @param key
     * @return
     */

    public void delete(String bucketName, String key) {
        ossClient.deleteObject(bucketName, key);
    }

    /**
     * @return the ossClient
     */
    public OSSClient getOssClient() {
        return ossClient;
    }

    /**
     * @param ossClient the ossClient to set
     */
    public void setOssClient(OSSClient ossClient) {
        this.ossClient = ossClient;
    }


}
