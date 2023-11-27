package com.lianxi.ftp.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2023/2/5 15:04
 *
 * @author HZ
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FtpUtil {

    public static final String DIR_SPLIT = "/";
    private final FtpConfig ftpConfig;

    /**
     * 获取 FTPClient
     *
     * @return FTPClient
     */
    private FTPClient getFTPClient() {
        FTPClient ftpClient = ftpConfig.connect();
        if (ftpClient == null || !FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            throw new RuntimeException("ftp客户端异常");
        }
        return ftpClient;
    }

    /**
     * 获取FTP某一特定目录下的所有文件名称
     *
     * @param ftpDirPath FTP上的目标文件路径
     */
    public List<String> getFileNameList(String ftpDirPath) {
        FTPClient ftpClient = getFTPClient();
        try {
            // 通过提供的文件路径获取 FTPFile 文件列表
            // FTPFile[] ftpFiles = ftpClient.listFiles(ftpDirPath, FTPFile::isFile); // 只获取文件
            // FTPFile[] ftpFiles = ftpClient.listFiles(ftpDirPath, FTPFile::isDirectory); // 只获取目录
            FTPFile[] ftpFiles = ftpClient.listFiles(ftpDirPath);
            if (ftpFiles != null && ftpFiles.length > 0) {
                return Arrays.stream(ftpFiles).map(FTPFile::getName).collect(Collectors.toList());
            }
            log.error(String.format("路径有误，或目录【%s】为空", ftpDirPath));
        } catch (IOException e) {
            log.error("文件获取异常:", e);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 上传文件
     *
     * @param uploadPath 上传路径
     * @param fileName   文件名
     * @param input      文件输入流
     * @return 上传结果
     */
    public boolean upload(String uploadPath, String fileName, InputStream input) {
        FTPClient ftpClient = getFTPClient();
        try {
            // 切换到工作目录
            if (!ftpClient.changeWorkingDirectory(uploadPath)) {
                ftpClient.makeDirectory(uploadPath);
                ftpClient.changeWorkingDirectory(uploadPath);
            }
            // 文件写入
            boolean storeFile = ftpClient.storeFile(fileName, input);
            if (storeFile) {
                log.info("文件:{}上传成功", fileName);
            } else {
                throw new RuntimeException("ftp文件写入异常");
            }
            ftpClient.logout();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 下载文件 *
     *
     * @param ftpPath     FTP服务器文件目录 *
     * @param ftpFileName 文件名称 *
     * @param localPath   下载后的文件路径 *
     * @return
     */
    public boolean download(String ftpPath, String ftpFileName, String localPath) {
        FTPClient ftpClient = getFTPClient();
        OutputStream outputStream = null;
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(ftpPath, file -> file.isFile() && file.getName().equals(ftpFileName));
            if (ftpFiles != null && ftpFiles.length > 0) {
                FTPFile ftpFile = ftpFiles[0];
                File localFile = new File(localPath + DIR_SPLIT + ftpFile.getName());
                // 判断本地路径目录是否存在，不存在则创建
                if (!localFile.getParentFile().exists()) {
                    localFile.getParentFile().mkdirs();
                }
                outputStream = Files.newOutputStream(localFile.toPath());
                ftpClient.retrieveFile(ftpFile.getName(), outputStream);

                log.info("fileName:{},size:{}", ftpFile.getName(), ftpFile.getSize());
                log.info("下载文件成功...");
                return true;
            } else {
                log.info("文件不存在，filePathname:{},", ftpPath + DIR_SPLIT + ftpFileName);
            }
        } catch (Exception e) {
            log.error("下载文件失败...");
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 从FTP服务器删除文件或目录
     * 存在文件的目录无法删除
     *
     * @param ftpPath  服务器文件存储路径
     * @param fileName 服务器文件存储名称
     * @return 删除结果
     */
    public boolean delete(String ftpPath, String fileName) {
        FTPClient ftpClient = getFTPClient();
        try {
            // 在 ftp 目录下获取文件名与 fileName 匹配的文件信息
            FTPFile[] ftpFiles = ftpClient.listFiles(ftpPath, file -> file.getName().equals(fileName));
            // 删除文件
            if (ftpFiles != null && ftpFiles.length > 0) {
                boolean del;
                String deleteFilePath = ftpPath + DIR_SPLIT + fileName;
                FTPFile ftpFile = ftpFiles[0];
                if (ftpFile.isDirectory()) {
                    del = ftpClient.removeDirectory(deleteFilePath);
                } else {
                    del = ftpClient.deleteFile(deleteFilePath);
                }
                log.info(del ? "文件:{}删除成功" : "文件:{}删除失败", fileName);
                return del;
            } else {
                log.warn("文件:{}未找到", fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}


