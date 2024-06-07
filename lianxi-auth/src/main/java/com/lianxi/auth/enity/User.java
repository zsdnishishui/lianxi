package com.lianxi.auth.enity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    /**
     * 表主键id
     */
    private Integer id;
    private String username;
    private String password;
    private String phone;
}
