package com.tech.mediaserver.utils;

import org.springframework.beans.BeanUtils;

/**
 * @author chudichen
 * @date 2018/9/27
 */
public interface PageUtil {

    /**
     * <h2>获取分页Bean</h2>
     * @param pagination {@link Pagination}分页信息
     * @return {@link PageBean}
     */
    static PageBean getPageBean(Pagination pagination) {
        int start = (pagination.getCurrent() - 1) * 10;
        PageBean pageBean = new PageBean();
        BeanUtils.copyProperties(pagination, pageBean);
        pageBean.setStart(start);
        pageBean.setSize(pagination.getPageSize());
        return pageBean;
    }
}
