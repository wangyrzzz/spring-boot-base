package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.SysRegion;
import com.example.demo.entity.SysRegionNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRegionMapper extends BaseMapper<SysRegion> {
    @Select("select r.*, exists(select 1 from sys_region c where c.parent_code = r.code and c.is_deleted = 0) as has_children "
            + "from sys_region r where r.is_deleted = 0 and r.status = 1 "
            + "and (#{parentCode} is null or r.parent_code = #{parentCode}) "
            + "and (#{code} is null or r.code like concat('%', #{code}, '%')) "
            + "and (#{name} is null or r.name like concat('%', #{name}, '%')) "
            + "order by r.sort, r.code")
    List<SysRegion> lazyList(@Param("parentCode") String parentCode,
                             @Param("code") String code,
                             @Param("name") String name);

    @Select("select r.code as id, r.parent_code as parent_id, r.name as title, r.code as value, "
            + "r.code as `key`, exists(select 1 from sys_region c where c.parent_code = r.code and c.is_deleted = 0) as has_children "
            + "from sys_region r where r.is_deleted = 0 and r.status = 1 "
            + "and (#{parentCode} is null or r.parent_code = #{parentCode}) "
            + "and (#{code} is null or r.code like concat('%', #{code}, '%')) "
            + "and (#{name} is null or r.name like concat('%', #{name}, '%')) "
            + "order by r.sort, r.code")
    List<SysRegionNode> lazyTree(@Param("parentCode") String parentCode,
                                 @Param("code") String code,
                                 @Param("name") String name);
}
