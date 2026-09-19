package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.entity.SysNotice;
import com.example.demo.mapper.SysNoticeMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class NoticeService extends ServiceImpl<SysNoticeMapper, SysNotice> {
    public Page<SysNotice> page(long current, long size, String title, Integer category, Integer status) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<SysNotice>()
                .like(StringUtils.hasText(title), SysNotice::getTitle, title)
                .eq(category != null, SysNotice::getCategory, category)
                .eq(status != null, SysNotice::getStatus, status)
                .orderByDesc(SysNotice::getReleaseTime)
                .orderByDesc(SysNotice::getId);
        return page(new Page<>(current, size), wrapper);
    }

    public Page<SysNotice> published(long current, long size) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getStatus, 1)
                .and(w -> w.isNull(SysNotice::getReleaseTime).or().le(SysNotice::getReleaseTime, new Date()))
                .orderByDesc(SysNotice::getReleaseTime)
                .orderByDesc(SysNotice::getId);
        return page(new Page<>(current, size), wrapper);
    }
}
