package com.xgn.fzc.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-20
 * Time: 4:28 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Mapper
public interface SqlMapper {

    /**
     * update with sql
     *
     * @param sql
     * @return
     */
    @Update("${sql}")
    int update(@Param("sql") String sql);

    /**
     * insert with sql
     *
     * @param sql
     * @return
     */
    @Insert("${sql}")
    int insert(@Param("sql") String sql);

    /**
     * delete with sql
     *
     * @param sql
     * @return
     */
    @Insert("${sql}")
    int delete(@Param("sql") String sql);
}
