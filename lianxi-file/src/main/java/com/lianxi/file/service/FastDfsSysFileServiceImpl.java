package com.lianxi.file.service;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.exception.FdfsIOException;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.lianxi.core.domain.R;
import com.lianxi.file.dto.MinioUploadInfo;
import com.lianxi.file.enity.MergeInfo;
import com.lianxi.file.enity.UpLoadConstant;
import com.lianxi.file.param.GetMinioUploadInfoParam;
import com.lianxi.file.utils.FileTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * FastDFS 文件存储
 *
 * @author ruoyi
 */

@Service
public class FastDfsSysFileServiceImpl implements ISysFileService {
    /**
     * 域名或本机访问地址
     */
    @Value("${fdfs.domain}")
    public String domain;

    //视频缓存时间,秒:1天
    private final Integer cacheTime = 60 * 60 * 24;

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * FastDfs文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                FileTypeUtils.getExtension(file), null);
        return domain + "/" + storePath.getFullPath();
    }

    @Override
    public R uploadSlice(MultipartFile file, String hash, String filename, Integer seq, String type, Integer totalChunk, Long chunkSize) {
        StorePath path = null;
        String groundPath = null;
        long historyUpload = seq * chunkSize;
        try {
            if (seq == 0) {
                path = appendFileStorageClient.uploadAppenderFile(UpLoadConstant.DEFAULT_GROUP, file.getInputStream(),
                        file.getSize(), filename);
                if (path == null) {
                    return R.fail("", "上传第一个就错了");
                }
                groundPath = path.getPath();
                stringRedisTemplate.opsForValue().set(UpLoadConstant.fastDfsPath + hash, groundPath, 1, TimeUnit.DAYS);

            } else {
                groundPath = stringRedisTemplate.opsForValue().get(UpLoadConstant.fastDfsPath + hash);
                appendFileStorageClient.modifyFile(UpLoadConstant.DEFAULT_GROUP, groundPath, file.getInputStream(),
                        file.getSize(), historyUpload);
//                Integer chunkNum = Integer.valueOf(stringRedisTemplate.opsForValue().get(UpLoadConstant.uploadChunkNum + hash));
//                chunkNum = chunkNum + 1;
            }
            stringRedisTemplate.opsForValue().set(UpLoadConstant.uploadChunkNum + hash, seq + "", 1, TimeUnit.DAYS);
            if (totalChunk == (seq + 1)) {
                stringRedisTemplate.delete(UpLoadConstant.uploadChunkNum + hash);
                stringRedisTemplate.delete(UpLoadConstant.fastDfsPath + hash);
                return R.ok(groundPath, "上传成功");
            }
        } catch (FdfsIOException | SocketTimeoutException e) {
            return R.fail("重新发送");
        } catch (Exception e) {
            e.printStackTrace();
            stringRedisTemplate.delete(UpLoadConstant.uploadChunkNum + hash);
            stringRedisTemplate.delete(UpLoadConstant.fastDfsPath + hash);
            return R.fail("", "upload error");
        }
        System.out.println("path=" + groundPath);
        return R.ok(groundPath);
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
        return null;
    }

    @Override
    public R uploadMerges(MergeInfo mergeInfo) {
        return null;
    }

    /**
     * 本地文件转m3u8
     *
     * @param path
     * @param localPath
     * @return
     */
    @Override
    public String m3u8(String path, String localPath) {
        return null;
    }
}
