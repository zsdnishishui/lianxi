package com.lianxi.file.enity;

public class UpLoadConstant {
    private final static String uploading = "Uploading:";


    private final static String file = UpLoadConstant.uploading + "file:";

    //记录当前文件上传了多少片
    public final static String uploadChunkNum = UpLoadConstant.file + "chunkNum:";

    //当前文件上传到fastdfs路径

    public final static String fastDfsPath = UpLoadConstant.file + "fastDfsPath:";

    //默认分组
    public final static String DEFAULT_GROUP = "group1";


}
