package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysDict;
import com.example.demo.enums.EnableStatusEnum;
import com.example.demo.mapper.SysDictMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DictService extends ServiceImpl<SysDictMapper, SysDict> {
    public List<SysDict> list(String code, Long parentId) {
        return lambdaQuery().eq(StringUtils.hasText(code), SysDict::getCode, code)
                .eq(parentId != null, SysDict::getParentId, parentId)
                .orderByAsc(SysDict::getSort).orderByAsc(SysDict::getId).list();
    }

    public Page<SysDict> page(long current, long size, String code, Long parentId, Integer status) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<SysDict>()
                .eq(StringUtils.hasText(code), SysDict::getCode, code)
                .eq(parentId != null, SysDict::getParentId, parentId)
                .eq(status != null, SysDict::getStatus, status)
                .orderByAsc(SysDict::getSort).orderByAsc(SysDict::getId);
        return page(new Page<>(current, size), wrapper);
    }

    @Transactional
    public Long saveOrUpdateDict(SysDict dict) {
        if (dict == null || !StringUtils.hasText(dict.getCode())
                || !StringUtils.hasText(dict.getDictKey()) || !StringUtils.hasText(dict.getDictValue())) {
            throw new ApiException(400, "字典码、字典键和字典值不能为空");
        }
        if (dict.getSort() == null) {
            dict.setSort(0);
        }
        if (dict.getStatus() == null) {
            dict.setStatus(EnableStatusEnum.ENABLED.getCode());
        }
        saveOrUpdate(dict);
        return dict.getId();
    }
}
