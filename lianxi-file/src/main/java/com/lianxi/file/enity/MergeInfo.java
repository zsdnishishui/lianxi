package com.lianxi.file.enity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MergeInfo implements Serializable {
    private static Long serialVersionUID = 1351063126163421L;
    /* 文件名 */
    private String filename;
    /* 文件类型 */
    private String type;
    /* 文件哈希值 */
    private String hash;

    /* 上传的文件id */
    private String uploadId;
}
