package com.lianxi.file.service;

import com.lianxi.core.domain.R;
import com.lianxi.file.config.MinioPropertiesConfig;
import com.lianxi.file.dto.MinioUploadInfo;
import com.lianxi.file.enity.MergeInfo;
import com.lianxi.file.helper.MinioHelper;
import com.lianxi.file.param.GetMinioUploadInfoParam;
import com.lianxi.file.utils.FileUploadUtils;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Minio 文件存储
 *
 * @author ruoyi
 */
@Primary
@Service
public class MinioSysFileServiceImpl implements ISysFileService {
    @Autowired
    private MinioPropertiesConfig minioPropertiesConfig;

    @Autowired
    private MinioClient client;

    @Autowired
    private MinioHelper minioHelper;

    /**
     * 本地文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = FileUploadUtils.extractFilename(file);
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(minioPropertiesConfig.getBucketName())
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build();
        client.putObject(args);
        return minioPropertiesConfig.getUrl() + "/" + minioPropertiesConfig.getBucketName() + "/" + fileName;
    }

    @Override
    public R<MinioUploadInfo> uploadSlice(MultipartFile file, String hash, String filename, Integer seq, String type, Integer chunkCount, Long chunkSize) {
        double partCount = Math.ceil(file.getSize() / chunkSize);
        MinioUploadInfo uploadInfo = minioHelper.initMultiPartUpload(filename, (int) partCount, file.getContentType());
        return R.ok(uploadInfo);
    }

    @Override
    public R uploadMerge(String filename, String type, String hash) {
        return null;
    }

    @Override
    public R fastUpload(String hash) {
        return null;
    }

    @Override
    public MinioUploadInfo getUploadId(GetMinioUploadInfoParam param) {
        // 计算分片数量
        double partCount = Math.ceil(param.getFileSize() / param.getChunkSize());
        return minioHelper.initMultiPartUpload(param.getFileName(), (int) partCount, param.getContentType());
    }

    @Override
    public R uploadMerges(MergeInfo mergeInfo) {
        String result = minioHelper.mergeMultiPartUpload(mergeInfo.getFilename(), mergeInfo.getUploadId());
        return R.ok(result, "合并成功");
    }
}
