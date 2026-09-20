package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysDictBiz;
import com.example.demo.enums.EnableStatusEnum;
import com.example.demo.mapper.SysDictBizMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DictBizService extends ServiceImpl<SysDictBizMapper, SysDictBiz> {
    public List<SysDictBiz> list(String code, Long parentId) {
        return lambdaQuery().eq(StringUtils.hasText(code), SysDictBiz::getCode, code)
                .eq(parentId != null, SysDictBiz::getParentId, parentId)
                .orderByAsc(SysDictBiz::getSort).orderByAsc(SysDictBiz::getId).list();
    }

    public Page<SysDictBiz> page(long current, long size, String code, Long parentId, Integer status) {
        LambdaQueryWrapper<SysDictBiz> wrapper = new LambdaQueryWrapper<SysDictBiz>()
                .eq(StringUtils.hasText(code), SysDictBiz::getCode, code)
                .eq(parentId != null, SysDictBiz::getParentId, parentId)
                .eq(status != null, SysDictBiz::getStatus, status)
                .orderByAsc(SysDictBiz::getSort).orderByAsc(SysDictBiz::getId);
        return page(new Page<>(current, size), wrapper);
    }

    @Transactional
    public Long saveOrUpdateDict(SysDictBiz dict) {
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
