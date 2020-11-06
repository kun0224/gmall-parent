package com.zhao.gmall.product.controller;

import com.zhao.gmall.common.result.Result;
import io.swagger.annotations.Api;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api(tags = "文件上传接口")
@RestController
@RequestMapping("admin/product")
public class FileUploadController {

    @Value("${fileServer.url}")
    private String fileUrl;

    /**
     *
     * @param file
     * @return
     */
    @PostMapping("fileUpload")
    public Result<String> fileUpload(MultipartFile file) throws IOException, MyException {

        /*
        1.先读取到配置文件tracker.conf
        2.数据初始化
        3.创建tracker
        4.创建storage
        5.执行上传
         */
        //读取到配置文件
        String configFile = this.getClass().getResource("/tracker.conf").getFile();

        //返回路径
        String path = "";
        //判断
        if(configFile!= null){
            //初始化
            ClientGlobal.init(configFile);
            //获取到trackerServer
            TrackerClient trackerClient = new TrackerClient();
            //获取到trackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            //创建stroage
            StorageClient1 storageClient1 =  new StorageClient1(trackerServer, null);
            //上传文件
            //获取文件的后缀名
            String extName = FilenameUtils.getExtension(file.getOriginalFilename());
            path = storageClient1.upload_appender_file1(file.getBytes(), extName, null);

            System.out.println("文件路径："+path);

        }
        return Result.ok(fileUrl+path);

    }

}
