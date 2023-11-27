package com.lianxi.ftp.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.SocketException;

/**
 * hbcx工区FTP服务器配置
 */

@Slf4j
@Configuration
public class FtpConfig {

    /**
     * FTP IP地址
     */
    @Value("${upload.host}")
    private String ip;

    /**
     * FTP端口号
     */
    @Value("${upload.port}")
    private Integer port;

    /**
     * FTP登录账号
     */
    @Value("${upload.username}")
    private String username;

    /**
     * FTP登录密码
     */
    @Value("${upload.password}")
    private String password;

    private FTPClient ftpClient;

    /**
     * 连接 FTP 服务
     */
    public FTPClient connect() {
        ftpClient = new FTPClient();
        try {
            // 设置编码
            ftpClient.setControlEncoding("UTF-8");
            // 设置连接超时时间(单位：毫秒)
            ftpClient.setConnectTimeout(10 * 1000);
            // 连接
            ftpClient.connect(ip, port);
            // 登录
            ftpClient.login(username, password);
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                log.error("未连接到FTP，用户名或密码错误");
                // 拒绝连接
                ftpClient.disconnect();
            } else {
                log.info("连接到FTP成功");
                // 设置二进制方式传输文件
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                // 设置被动工作模式
                ftpClient.enterLocalPassiveMode();
            }
            return ftpClient;
        } catch (SocketException e) {
            e.printStackTrace();
            log.error("FTP的IP地址错误");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("FTP的端口错误");
        }
        return ftpClient;
    }

    /**
     * 断开 FTP 服务
     */
    public void closeConnect() {
        log.warn("关闭ftp服务器");
        try {
            if (ftpClient != null) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

