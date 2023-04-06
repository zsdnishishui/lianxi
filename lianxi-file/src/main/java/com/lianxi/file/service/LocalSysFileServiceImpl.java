package com.lianxi.file.service;

import com.lianxi.core.domain.R;
import com.lianxi.file.dto.MinioUploadInfo;
import com.lianxi.file.enity.MergeInfo;
import com.lianxi.file.param.GetMinioUploadInfoParam;
import com.lianxi.file.utils.FileUploadUtils;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 本地文件存储
 *
 * @author ruoyi
 */
@Primary
@Service
@Slf4j
public class LocalSysFileServiceImpl implements ISysFileService {
    /**
     * 资源映射路径 前缀
     */
    @Value("${file.prefix}")
    public String localFilePrefix;

    /**
     * 域名或本机访问地址
     */
    @Value("${file.domain}")
    public String domain;

    /**
     * 上传文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;

    /**
     * 本地文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        String name = FileUploadUtils.upload(localFilePath, file);
        String url = domain + localFilePrefix + name;
        return url;
    }

    private static String BASE_DIR = "D:\\fileTemp\\";

    /**
     * 分片上传
     *
     * @param file       : 文件流
     * @param hash       : 哈希值
     * @param filename   : 文件名
     * @param seq        : 分片序号
     * @param type       : 文件类型
     * @param chunkCount
     * @param chunkSize
     */
    @Override
    public R uploadSlice(MultipartFile file, String hash, String filename, Integer seq, String type, Integer chunkCount, Long chunkSize) {
        RandomAccessFile raf = null;
        try {
            // 创建目标文件夹
            File dir = new File(LocalSysFileServiceImpl.BASE_DIR + hash);
            if (!dir.exists()) {
                dir.mkdir();
            }
            // 创建空格文件 名称带 seq 用于标识分块信息
            raf = new RandomAccessFile(LocalSysFileServiceImpl.BASE_DIR + hash + "\\" + filename + "." + type + seq, "rw");
            // 写入文件流
            raf.write(file.getBytes());
        } catch (IOException e) {
            // 异常处理
            // ...打印异常日志...
            return R.fail("上传异常");
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
                // ...打印异常日志...
            }
        }
        return R.ok();
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 合并文件的业务代码
     *
     * @param filename : 文件名
     * @param hash     : 文件哈希值
     * @param type     : 文件类型
     */
    @Override
    public R uploadMerge(String filename, String type, String hash) {
        // 判断 hash 对应文件夹是否存在
        File dir = new File(LocalSysFileServiceImpl.BASE_DIR + hash);
        if (!dir.exists()) {
            return R.fail("合并失败，请稍后重试");
        }
        // 这里通过 FileChannel 来实现信息流复制
        FileChannel out = null;
        // 获取目标 channel
        try (FileChannel in = new RandomAccessFile(LocalSysFileServiceImpl.BASE_DIR + filename + '.' + type, "rw").getChannel()) {
            // 分片索引递增
            int index = 0;
            // 开始流位置
            long start = 0;
            while (true) {
                // 分片文件名
                String sliceName = LocalSysFileServiceImpl.BASE_DIR + hash + '\\' + filename + '.' + type + index;
                // 到达最后一个分片 退出循环
                if (!new File(sliceName).exists()) {
                    break;
                }
                // 分片输入流
                out = new RandomAccessFile(sliceName, "r").getChannel();
                // 写入目标 channel
                in.transferFrom(out, start, start + out.size());
                // 位移量调整
                start += out.size();
                out.close();
                out = null;
                // 分片索引调整
                index++;
            }
            // 文件合并完毕
            in.close();
            // ...执行本地存储服务/第三方存储服务上传 返回文件地址...
            // 这里假设是 fileSite
            String fileSite = "";
            // 地址存入 redis 实现秒传
            stringRedisTemplate.opsForValue().set("upload:finish:hash:" + hash, fileSite);
            return R.ok(fileSite, "上传成功");
        } catch (IOException e) {
            // ...记录日志..
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return R.fail("上传失败，请稍后重试");
    }

    /**
     * 极速秒传业务代码
     *
     * @param hash : 文件哈希值
     */
    @Override
    public R fastUpload(String hash) {
        R resp = new R<>();
        String key = "upload:finish:hash:" + hash;
        String fileSite = stringRedisTemplate.opsForValue().get(key);
        // 文件已存在 直接返回地址
        if (fileSite != null) {
            return R.ok(fileSite, "极速秒传成功");
        } else {
            return R.fail("", "极速秒传失败");
        }
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
     * @param localPath
     * @return
     */
    @Override
    public String m3u8(String localPath, String destPath) throws IOException {
        testVideo(localPath, destPath);
        return destPath;
    }

    public void testVideo(String src, String dest) throws IOException {

        FFmpeg ffmpeg = new FFmpeg("D:\\develop\\ffmpeg-master-latest-win64-gpl\\bin\\ffmpeg.exe");
        FFprobe ffprobe = new FFprobe("D:\\develop\\ffmpeg-master-latest-win64-gpl\\bin\\ffprobe.exe");

        final FFmpegProbeResult probe = ffprobe.probe(src);

        final List<FFmpegStream> streams = probe.getStreams().stream().filter(fFmpegStream -> fFmpegStream.codec_type != null).collect(Collectors.toList());

        final Optional<FFmpegStream> audioStream = streams.stream().filter(fFmpegStream -> FFmpegStream.CodecType.AUDIO.equals(fFmpegStream.codec_type)).findFirst();

        final Optional<FFmpegStream> videoStream = streams.stream().filter(fFmpegStream -> FFmpegStream.CodecType.VIDEO.equals(fFmpegStream.codec_type)).findFirst();

        if (!audioStream.isPresent()) {
            System.err.println("未发现音频流");
        }

        if (!videoStream.isPresent()) {
            System.err.println("未发现视频流");
        }
//ffmpeg -i TM-20230329091129-760405287-recording-1.mp4 -c:v libx264 -hls_time 60 -hls_list_size 0 -c:a aac -strict -2 -f hls test.m3u8

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(src)
                .overrideOutputFiles(true)
                .addOutput(dest)//输出文件
                .setFormat(probe.getFormat().format_name) //"mp4"
                .setAudioBitRate(audioStream.map(fFmpegStream -> fFmpegStream.bit_rate).orElse(0L))
                .setAudioChannels(1)
                .setAudioCodec("aac")        // using the aac codec
                .setAudioSampleRate(audioStream.get().sample_rate)
                .setAudioBitRate(audioStream.get().bit_rate)
                .setStrict(FFmpegBuilder.Strict.STRICT)
                .setFormat("hls")
                .setPreset("ultrafast")
//                .addExtraArgs("-vsync", "2", "-c:v", "copy", "-c:a", "copy", "-tune", "fastdecode", "-hls_wrap", "0", "-hls_time", "10", "-hls_list_size", "0", "-threads", "12")
                .addExtraArgs("-vsync", "2", "-c:v", "copy", "-c:a", "copy", "-tune", "fastdecode", "-hls_flags", "0", "-hls_time", "10", "-hls_list_size", "0", "-threads", "12")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        // Run a one-pass encode
        executor.createJob(builder).run();
    }
}
