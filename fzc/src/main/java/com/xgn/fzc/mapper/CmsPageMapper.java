package com.xgn.fzc.mapper;

import com.xgn.fzc.entity.CmsPage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-20
 * Time: 3:03 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Mapper
public interface CmsPageMapper {

    @Select("SELECT * FROM cms_page WHERE page_id = #{pageId, javaType=string}")
    @Results(id = "CmsPage", value = {
            @Result(property = "pageId", column = "page_id", id = true),
            @Result(property = "pageName", column = "page_name"),
            @Result(property = "status", column = "status"),
            @Result(property = "type", column = "type"),
            @Result(property = "platform", column = "platform"),
            @Result(property = "editor", column = "editor"),
            @Result(property = "lightPageInfo", column = "light_page_info"),
            @Result(property = "pageInfo", column = "page_info"),
            @Result(property = "createBy", column = "create_by"),
            @Result(property = "editTime", column = "edit_time"),
            @Result(property = "projectId", column = "project_id"),
            @Result(property = "isDelete", column = "is_delete")})
    CmsPage selectCmsPageById(@Param("pageId") String pageId);

    @Select("SELECT * FROM cms_page")
    @ResultMap("CmsPage")
    List<CmsPage> selectCmsPage();
}
