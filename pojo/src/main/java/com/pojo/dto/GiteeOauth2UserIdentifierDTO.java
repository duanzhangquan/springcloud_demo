package com.pojo.dto;

import lombok.Data;

/**
 * gitee oauth2.0用户唯一标识
 */
@Data
public class GiteeOauth2UserIdentifierDTO {
    private String userName;
    private Integer userId;
    private String registrationId;

    public GiteeOauth2UserIdentifierDTO(String userName, Integer userId, String registrationId) {
        this.userName = userName;
        this.userId = userId;
        this.registrationId = registrationId;
    }

    public GiteeOauth2UserIdentifierDTO() {
    }


}
