package com.lianxi.file.service;

import com.lianxi.core.domain.R;
import com.lianxi.file.dto.MinioUploadInfo;
import com.lianxi.file.enity.MergeInfo;
import com.lianxi.file.param.GetMinioUploadInfoParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口
 *
 * @author ruoyi
 */
public interface ISysFileService {
    /**
     * 文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    public String uploadFile(MultipartFile file) throws Exception;

    R<?> uploadSlice(MultipartFile file, String hash, String filename, Integer seq, String type, Integer chunkCount, Long chunkSize);

    R<String> uploadMerge(String filename, String type, String hash);

    R<String> fastUpload(String hash);

    MinioUploadInfo getUploadId(GetMinioUploadInfoParam param);

    R<String> uploadMerges(MergeInfo mergeInfo);
}
