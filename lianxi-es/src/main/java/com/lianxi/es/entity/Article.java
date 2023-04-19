package com.lianxi.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


/**
 * @Author: Ljx
 * @Date: 2021/12/3 13:14
 * @role:
 */

//indexName 指定索引名称

@Document(indexName = "lx-sd")

@Data

public class Article {

    @Id

    @Field(index = false, type = FieldType.Integer)

    private Integer id;

    /**
     * index:是否设置分词  默认为true
     * <p>
     * analyzer：储存时使用的分词器
     * <p>
     * searchAnalyze:搜索时使用的分词器
     * <p>
     * store：是否存储  默认为false
     * <p>
     * type：数据类型  默认值是FieldType.Auto
     */

    @Field(analyzer = "ik_smart", searchAnalyzer = "ik_smart", store = true, type = FieldType.Text)

    private String title;

    @Field(analyzer = "ik_smart", searchAnalyzer = "ik_smart", store = true, type = FieldType.Text)

    private String context;

    @Field(store = true, type = FieldType.Integer)

    private Integer hits;

}
