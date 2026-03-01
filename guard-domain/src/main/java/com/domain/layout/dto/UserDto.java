package com.domain.layout.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 畫面顯示用-登入者資料
 *
 * @Author: Eton.Lin
 * @Date: 2026/1/14 上午12:03
 */
@Data
public class UserDto implements Serializable {
    private String userName;
    private String email;
    private String avatar;
}
