package com.xgn.fzc.entity;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-20
 * Time: 4:01 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Data
public class CmsPage {

    private String pageId;

    private String pageName;

    @Pattern(regexp = "ONLINE|OFFLINE|DRAFT")
    private String status;

    @Pattern(regexp = "HOME|ACTIVITY")
    private String type;

    @Pattern(regexp = "APP|MINIPROGRAM")
    private String platform;

    private String editor;

    private String pageInfo;

    private String lightPageInfo;

    private String projectId;

    private Date createTime;

    private Date editTime;

    private String createBy;

    private int isDelete;
}
