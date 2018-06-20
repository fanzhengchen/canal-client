package com.xgn.fzc;

import com.xgn.fzc.entity.CmsPage;
import com.xgn.fzc.mapper.CmsPageMapper;
import com.xgn.fzc.mapper.SqlMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FzcApplicationTests {

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    CmsPageMapper pageMapper;

    @Autowired
    SqlMapper sqlMapper;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testUpdate() {
        String sql = "UPDATE cms.cms_page SET page_id='1f6c8401-2c80-4240-8358-3aa35a958f3c',page_name='快点发工资',status='DRAFT',create_time='2018-06-20 15:43:17',min_version='12',type='HOME',platform='APP',editor='xiansheng',create_by='xiansheng',edit_time='2018-06-20 16:32:48',project_id='fresh',page_info='[]',light_page_info='[]',shop_id='12',is_delete='0' WHERE page_id='1f6c8401-2c80-4240-8358-3aa35a958f3c';";
        sqlMapper.update(sql);
    }

    @Test
    public void testInsert(){
        String sql = "INSERT INTO cms.cms_page (page_id,page_name,status,create_time,min_version,type,platform,editor,create_by,edit_time,project_id,page_info,shop_id,is_delete) VALUES ('dfe1952f-ad58-4294-8e00-f99417e4d69e','hjhjh','DRAFT','2018-06-20 16:39:01','89','HOME','APP','xiansheng','xiansheng','2018-06-21 00:39:01','fresh','','8','0');";
        sqlMapper.insert(sql);
    }

    @Test
    public void testDelete(){
        String sql = "DELETE FROM cms.cms_page WHERE page_id='c6ca7cb9-9658-4c75-8290-640ca284e714';";
        sqlMapper.delete(sql);
    }
    @Test
    public void selectCmsPage() {
        List<CmsPage> pageList = pageMapper.selectCmsPage();

    }

}
