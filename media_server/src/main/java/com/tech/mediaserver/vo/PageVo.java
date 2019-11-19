package com.tech.mediaserver.vo;

import lombok.Data;

import java.util.List;

import com.tech.mediaserver.utils.Pagination;

/**
 * @author chudichen
 * @date 2018/9/19
 */
@Data
public class PageVo<T> {
    private List<T> list;
    private Pagination pagination;
    private String type;
}
