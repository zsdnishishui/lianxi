package com.lianxi.file.controller;


import com.lianxi.common.tika.utile.TikaBasicUtil;
import com.lianxi.core.domain.R;
import com.lianxi.file.dto.MinioUploadInfo;
import com.lianxi.file.enity.MergeInfo;
import com.lianxi.file.enity.Status;
import com.lianxi.file.enity.SysFile;
import com.lianxi.file.enity.User;
import com.lianxi.file.param.GetMinioUploadInfoParam;
import com.lianxi.file.service.ISysFileService;
import com.lianxi.file.utils.FileUtils;
import org.simpleframework.xml.core.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

/**
 * 文件请求处理
 *
 * @author ruoyi
 */
@RestController
public class SysFileController {
    private static final Logger log = LoggerFactory.getLogger(SysFileController.class);

    @Autowired
    private ISysFileService sysFileService;
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 文件上传请求
     */
    @PostMapping("upload")
    public R<SysFile> upload(MultipartFile file) {
        try {
            // 上传并返回访问地址
            String url = sysFileService.uploadFile(file);
            SysFile sysFile = new SysFile();
            sysFile.setName(FileUtils.getName(url));
            sysFile.setUrl(url);
            return R.ok(sysFile);
        } catch (Exception e) {
            SysFileController.log.error("上传文件失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 上传分片的接口
     *
     * @param file     : 文件信息
     * @param hash     : 文件哈希值
     * @param filename : 文件名
     * @param seq      : 分片序号
     * @param type     : 文件类型
     */
    @PostMapping("/uploadSlice")
    public R<?> uploadSlice(@RequestParam(value = "file") MultipartFile file,
                            @RequestParam(value = "hash") String hash,
                            @RequestParam(value = "filename") String filename,
                            @RequestParam(value = "seq") Integer seq,
                            @RequestParam(value = "type") String type,
                            @RequestParam(value = "chunkCount") Integer chunkCount,
                            @RequestParam(value = "chunkSize") Long chunkSize
    ) {
        // 返回上传结果
        return sysFileService.uploadSlice(file, hash, filename, seq, type, chunkCount, chunkSize);
    }

    @PostMapping("/merge")
    public R merge(@RequestBody MergeInfo mergeInfo) {
        if (mergeInfo != null) {
            String filename = mergeInfo.getFilename();
            String type = mergeInfo.getType();
            String hash = mergeInfo.getHash();
            return sysFileService.uploadMerge(filename, type, hash);
        }
        return R.fail("文件合并失败");
    }

    /**
     * 极速秒传接口
     *
     * @param hash : 文件哈希值
     */
    @GetMapping("/check")
    public R check(String hash) {
        return sysFileService.fastUpload(hash);
    }

    /**
     * 获取上传 url
     *
     * @param param 参数
     * @return
     */
    @PostMapping("/getUrl")
    public R<MinioUploadInfo> getUploadId(@Validate @RequestBody GetMinioUploadInfoParam param) {
        MinioUploadInfo minioUploadId = sysFileService.getUploadId(param);
        return R.ok(minioUploadId);
    }

    @PostMapping("/merges")
    public R merges(MergeInfo mergeInfo) {
        if (mergeInfo != null) {
            return sysFileService.uploadMerges(mergeInfo);
        }
        return R.fail("文件合并失败");
    }

    @GetMapping("/m3u8")
    public R m3u8() throws IOException {
        String localPath = "C:\\**.mp4";
        String destPath = "C:\\***.m3u8";
        String filePath = sysFileService.m3u8(localPath, destPath);
        return R.ok(filePath);
    }

    @PostMapping("/fileToTxt")
    public R fileToTxt(MultipartFile file) throws IOException {
        return R.ok(TikaBasicUtil.fileToTxt(file.getInputStream()));
    }

    @PostMapping("/insertMG")
    public R insertMG() throws IOException {
        // 设置用户信息
        User user = new User()
                .setId("10")
                .setAge(22)
                .setSex("男")
                .setRemake("无")
                .setSalary(1500)
                .setName("zhangsan")
                .setBirthday(new Date())
                .setStatus(new Status().setHeight(180).setWeight(150));
        // 插入一条用户数据，如果文档信息已经存在就抛出异常
        User newUser = mongoTemplate.insert(user, "test");
        return R.ok(newUser);
    }
}