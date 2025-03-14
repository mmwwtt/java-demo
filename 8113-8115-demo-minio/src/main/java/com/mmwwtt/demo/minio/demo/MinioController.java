package com.mmwwtt.demo.minio.demo;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/minio")
@Slf4j
public class MinioController {
    @Autowired
    private MinioUtil minioUtil;

    @PostMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile imgFile) {
        String s = minioUtil.putObject(imgFile);
        log.info(s);
        String fileName = getFileName(s);
        return "文件上传成功:" + fileName;
    }

    public String getFileName(String allName) {
            allName = allName.substring(0, allName.indexOf('?'));
        if (allName != null && allName.contains("/")) {
            return allName.substring(allName.lastIndexOf('/') + 1);
        }
        return "";
    }
    /**
     * 获取图片的路径【带问号后面的内容】
     * @param filename
     * @return
     */
    @ResponseBody
    @GetMapping("/fileDownload/{filename}")
    public String download(@PathVariable("filename") String filename, HttpServletResponse response) {
        minioUtil.getObject(filename,response);
        return "文件下载成功";
    }

}
