package com.mmwwtt.demo.minio.demo;


import com.google.common.io.ByteStreams;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Component
public class MinioUtil {

    @Value("${minio.url}")
    private String url;
    @Value("${minio.username}")
    private String username;
    @Value("${minio.password}")
    private String password;
    @Value("${minio.bucketName}")
    private String bucketName;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder().endpoint(url)
                .credentials(username, password).build();
    }
    //桶操作

    /**
     * 查看bucket是否存在
     *
     * @return boolean
     */
    public boolean bucketExists() {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            log.error("查看bucket是否存在 {}", e);
            return false;
        }
    }

    /**
     * 创建存储bucket
     *
     * @return Boolean
     */
    public boolean createBucket() {
        if (bucketExists()) {
            return true;
        }
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            return true;
        } catch (Exception e) {
            log.error("创建桶失败", e);
            return false;
        }
    }

    /**
     * 删除存储bucket
     *
     * @return boolean
     */
    public boolean removeBucket() {
        if (!bucketExists()) {
            return true;
        }
        //获取桶中所有的对象
        List<Item> items = getBucketObjects();
        if (items.size() > 0) {
            //有对象文件，则删除失败
            return false;
        }
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            return true;
        } catch (Exception e) {
            log.error("根据名称删除桶失败", e);
            return false;
        }
    }

    /**
     * 获取存储桶策略
     *
     * @return json
     */
    public String getBucketPolicy() {
        String bucketPolicy = null;
        try {
            bucketPolicy = minioClient.getBucketPolicy(GetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            log.error("获取存储桶策略失败", e);
        }
        return bucketPolicy;
    }

    /**
     * 根据bucketName获取信息
     * <p>
     * return 如果不存在返回null
     */
    public Bucket getBucket() {
        try {
            return minioClient.listBuckets()
                    .stream()
                    .filter(b -> b.name().equals(bucketName))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            log.error("根据bucketName获取桶信息", e);
        }
        return null;
    }

    /**
     * 获取全部bucket
     */
    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            log.error("获取所有的桶信息", e);
        }
        return null;
    }

    /**
     * 创建文件夹或目录
     *
     * @param directoryName 目录路径
     */
    public boolean createDirectory(String directoryName) {
        if (!bucketExists()) {
            createBucket();
        }

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(directoryName)
                    .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                    .build());
            return true;
        } catch (Exception e) {
            log.error("创建文件夹或目录失败", e);
            return false;
        }
    }

    // 文件操作

    /**
     * 判断文件是否存在
     *
     * @param objectName 对象
     * @return 存在返回true，不存在发生异常返回false
     */
    public boolean objectExist(String objectName) {
        if (!bucketExists()) {
            return false;
        }
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return true;
        } catch (Exception e) {
            log.error("判断文件是否存在失败", e);
            return false;
        }
    }

    /**
     * 判断文件夹是否存在【注意是文件夹而不是目录】
     *
     * @param folderName 文件夹名称（去掉前后的/）
     * @return
     */
    public boolean folderExist(String folderName) {
        if (!bucketExists()) {
            return false;
        }
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(folderName)
                    .recursive(false)
                    .build());
            if (results != null) {
                for (Result<Item> result : results) {
                    Item item = result.get();
                    folderName += "/";
                    if (item.isDir() && folderName.equals(item.objectName())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("判断文件夹是否存在失败", e);
            return false;
        }
        return false;
    }

    /**
     * 文件上传
     *
     * @param multipartFile 待上传文件
     * @param folderName    目录
     * @param aimFileName   最终保存到minio中的文件名，不需要后缀
     * @return
     */
    public String putObject(MultipartFile multipartFile, String folderName, String aimFileName) {
        if (!bucketExists()) {
            createBucket();
        }

        if (!StringUtils.hasText(aimFileName)) {
            aimFileName = UUID.randomUUID().toString();
        }
        //获取文件后缀
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        aimFileName += suffix;

        //带路径的文件名
        String lastFileName = "";
        if (StringUtils.hasText(folderName)) {
            lastFileName = "/" + folderName + "/" + aimFileName;
        } else {
            lastFileName = aimFileName;
        }

        try (InputStream inputStream = multipartFile.getInputStream();) {
            //上传文件到指定目录,文件名称相同会覆盖
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(lastFileName)
                    .stream(inputStream, multipartFile.getSize(), -1)
                    .contentType(multipartFile.getContentType())
                    .build());
            return getObjectUrl(lastFileName);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return null;
        }

    }

    /**
     * 上传文件【不指定文件夹】
     *
     * @param multipartFile
     * @param fileName
     * @return
     */
    public String putObject(MultipartFile multipartFile, String fileName) {
        return putObject(multipartFile, null, fileName);
    }

    /**
     * 上传文件【不指定文件夹,不指定目标文件名】
     *
     * @param multipartFile
     * @return
     */
    public String putObject(MultipartFile multipartFile) {
        return putObject(multipartFile, null, null);
    }

    /**
     * 自动创建桶并存储文件
     *
     * @param inputStream
     * @param aimFileName 必须，minio桶中文件的名字，需要带后缀
     * @return
     */
    public String putObject(InputStream inputStream, String aimFileName) {
        if (!bucketExists()) {
            createBucket();
        }
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(aimFileName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            inputStream.close();
            return getObjectUrl(aimFileName);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return null;
        }
    }

    /**
     * 拷贝文件
     *
     * @param objectName    文件名称
     * @param srcBucketName 目标bucket名称
     * @param srcObjectName 目标文件名称
     */
    public boolean copyObject(String srcBucketName, String srcObjectName, String objectName) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .source(CopySource.builder()
                                    .bucket(srcBucketName)
                                    .object(srcObjectName)
                                    .build())
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return true;
        } catch (Exception e) {
            log.error("拷贝文件失败", e);
            return false;
        }
    }

    /**
     * 文件下载
     *
     * @param fileName 文件名称
     * @param response response
     * @return Boolean
     */
    public void getObject(String fileName, HttpServletResponse response) {
        if (!bucketExists()) {
            return;
        }
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build();

        try (ServletOutputStream outputStream = response.getOutputStream();
             GetObjectResponse objectResponse = minioClient.getObject(getObjectArgs)) {

            response.setCharacterEncoding("utf-8");
            //设置强行下载不打开
            //response.setContentType("application/force-download");
            //response.setContentType("APPLICATION/OCTET-STREAM");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            ByteStreams.copy(objectResponse, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            log.error("文件下载失败", e);
        }
    }

    /**
     * 以流的形式获取一个文件对象
     *
     * @param objectName 对象名称
     * @return {@link InputStream}
     */
    public InputStream getObject(String objectName) {
        if (!bucketExists()) {
            return null;
        }
        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            if (statObjectResponse.size() > 0) {
                // 获取objectName的输入流。
                return minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
            }
        } catch (Exception e) {
            log.error("文件下载失败", e);
        }
        return null;
    }

    /**
     * 获取文件信息, 如果抛出异常则说明文件不存在
     *
     * @param objectName 文件名称
     */
    public StatObjectResponse getObjectInfo(String objectName) {
        if (!bucketExists()) {
            return null;
        }

        StatObjectResponse statObjectResponse = null;
        try {
            statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("获取文件信息失败", e);
        }
        return statObjectResponse;
    }

    /**
     * 获取图片的路径
     *
     * @param fileName
     * @return
     */
    public String getObjectUrl(String fileName) {
        try {
            if (fileName.startsWith("/")) {
                fileName = fileName.substring(1);
            }
            GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .method(Method.GET)
                    //过期时间(分钟数)
                    .expiry(60 * 60)
                    .build();
            return minioClient.getPresignedObjectUrl(build);
        } catch (Exception e) {
            log.error("获取文件路径失败", e);
        }
        return null;
    }

    /**
     * 断点下载
     *
     * @param objectName 文件名称
     * @param offset     起始字节的位置
     * @param length     要读取的长度
     * @return 流
     */
    public InputStream getObject(String objectName, long offset, long length) {
        if (!bucketExists()) {
            return null;
        }
        GetObjectResponse objectResponse = null;
        try {
            objectResponse = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .offset(offset)
                    .length(length)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectResponse;
    }

    /**
     * 获取指定桶中的所有文件对象
     *
     * @return 存储bucket内文件对象信息
     */
    public List<Item> getBucketObjects() {
        if (!bucketExists()) {
            return null;
        }

        List<Item> items = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .build());
        if (results != null) {
            try {
                for (Result<Item> result : results) {
                    items.add(result.get());
                }
            } catch (Exception e) {
                log.error("获取指定桶中的所有文件对象", e);
            }
        }
        return items;
    }

    /**
     * 获取路径下文件列表
     *
     * @param prefix    路径名称
     * @param recursive 是否递归查找，如果是false,就模拟文件夹结构查找
     * @return 二进制流
     */
    public Iterable<Result<Item>> getObjects(String prefix, boolean recursive) {
        if (!bucketExists()) {
            return null;
        }
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .recursive(recursive)
                .build());
        return results;
    }

    /**
     * 根据文件前置查询文件
     *
     * @param prefix    前缀
     * @param recursive 是否递归查询
     * @return MinioItem 列表
     */
    public List<Item> getAllObjectsByPrefix(String prefix, boolean recursive) {
        if (!bucketExists()) {
            return null;
        }
        List<Item> items = new ArrayList<>();
        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .recursive(recursive)
                        .build());
        if (objectsIterator != null) {
            try {
                for (Result<Item> o : objectsIterator) {
                    Item item = o.get();
                    items.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return
     * @throws Exception
     */
    public boolean removeObject(String fileName) {
        if (!bucketExists()) {
            return false;
        }
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
            return true;
        } catch (Exception e) {
            log.error("根据文件删除文件失败", e);
            return false;
        }
    }

    /**
     * 批量删除文件对象【没有测试成功】
     *
     * @param objects 需要删除的文件列表
     */
    public boolean removeObjects(String... objects) {
        if (!bucketExists()) {
            return false;
        }
        List<DeleteObject> deleteObjects = new LinkedList<>();
        Arrays.stream(objects).forEach(s -> {
            deleteObjects.add(new DeleteObject(s));
        });

        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder()
                .bucket(bucketName)
                .objects(deleteObjects)
                .build());

        //Minio处理批量删除的时候, 采用的延迟执行, 需要通过迭代返回的Iterable<Result<DeleteError>>以执行删除
        if (results != null) {
            try {
                for (Result<DeleteError> result : results) {
                    DeleteError error = result.get();
                    log.error("Error in deleting object " + error.objectName() + "; " + error.message());
                }
            } catch (Exception e) {
                log.error("批量删除文件失败", e);
            }
        }

        return true;
    }

}