package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysRegion;
import com.example.demo.entity.SysRegionNode;
import com.example.demo.mapper.SysRegionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RegionService extends ServiceImpl<SysRegionMapper, SysRegion> {
    public Page<SysRegion> page(long current, long size, String code, String name, String parentCode, Integer status) {
        LambdaQueryWrapper<SysRegion> wrapper = new LambdaQueryWrapper<SysRegion>()
                .like(StringUtils.hasText(code), SysRegion::getCode, code)
                .like(StringUtils.hasText(name), SysRegion::getName, name)
                .eq(StringUtils.hasText(parentCode), SysRegion::getParentCode, parentCode)
                .eq(status != null, SysRegion::getStatus, status)
                .orderByAsc(SysRegion::getSort)
                .orderByAsc(SysRegion::getCode);
        return page(new Page<>(current, size), wrapper);
    }

    public List<SysRegion> lazyList(String parentCode, String code, String name) {
        return baseMapper.lazyList(parentCode, blankToNull(code), blankToNull(name));
    }

    public List<SysRegionNode> lazyTree(String parentCode, String code, String name) {
        return baseMapper.lazyTree(parentCode, blankToNull(code), blankToNull(name));
    }

    public List<SysRegion> select(String parentCode) {
        return lambdaQuery().eq(SysRegion::getStatus, 1)
                .eq(StringUtils.hasText(parentCode), SysRegion::getParentCode, parentCode)
                .orderByAsc(SysRegion::getSort).orderByAsc(SysRegion::getCode).list();
    }

    @Transactional
    public Long saveOrUpdateRegion(SysRegion region) {
        if (region == null || !StringUtils.hasText(region.getCode()) || !StringUtils.hasText(region.getName())) {
            throw new ApiException(400, "行政区域编码和名称不能为空");
        }
        SysRegion old = region.getId() == null ? null : getById(region.getId());
        Long sameCode = lambdaQuery().eq(SysRegion::getCode, region.getCode())
                .ne(region.getId() != null, SysRegion::getId, region.getId()).count();
        if (sameCode > 0) {
            throw new ApiException(400, "行政区域编码已存在");
        }
        if (StringUtils.hasText(region.getParentCode())) {
            if (region.getCode().equals(region.getParentCode())) {
                throw new ApiException(400, "行政区域不能以自身作为父级");
            }
            SysRegion parent = lambdaQuery().eq(SysRegion::getCode, region.getParentCode()).one();
            if (parent == null) {
                throw new ApiException(400, "父级行政区域不存在");
            }
            region.setAncestors(buildAncestors(parent));
        } else {
            region.setParentCode(null);
            region.setAncestors("");
        }
        if (region.getStatus() == null) {
            region.setStatus(1);
        }
        if (region.getSort() == null) {
            region.setSort(0);
        }
        saveOrUpdate(region);
        return region.getId();
    }

    @Transactional
    public void removeRegion(Long id) {
        SysRegion region = getById(id);
        if (region == null) {
            return;
        }
        long children = lambdaQuery().eq(SysRegion::getParentCode, region.getCode()).count();
        if (children > 0) {
            throw new ApiException(400, "请先删除子级行政区域");
        }
        removeById(id);
    }

    private String buildAncestors(SysRegion parent) {
        if (!StringUtils.hasText(parent.getAncestors())) {
            return parent.getCode();
        }
        return parent.getAncestors() + "," + parent.getCode();
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value : null;
    }
}
