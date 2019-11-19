package com.tech.mediaserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.dto.SearchDto;
import com.tech.mediaserver.utils.PageBean;

/**
 * @author chudichen
 * @date 2018/10/16
 */
public interface BaseDao<T> {

    /**
     * 获取总的记录数
     *
     * @param pageBean page参数
     * @return 总记录数
     */
    int getCount(PageBean pageBean);

    /**
     * 分页获取
     *
     * @param  pageBean pageBean
     * @return List<ChannelVo>
     */
    List<T> getList(PageBean pageBean);//,@Param("board_list")List<Integer> board_list, @Param("module_list")List<Integer> module_list);

    /**
     * 保存
     *
     * @param entity 保存对象
     * @return 主键ID
     */
    Integer save(T entity);
    
    /**
     * 保存多条
     */
    int saveList(List<T> items);
    
    /**
     * 更新
     *
     * @param entity 更新对象
     */
    void update(T entity);

    /**
     * 通过id删除
     *
     * @param ids id
     */
    void delete(String... ids);

    /**
     * 通过Channel id来更新
     *
     * @param entity ChannelTranscode
     */
    void updateByChannelId(T entity);

    /**
     * 通过ChannelId来查询
     * @param channelId channelid
     * @return 结果
     */
    T getByChannelId(String channelId);

    /**
     * 通过ChannelId List来查询
     * @param channelId channelid
     * @return 结果
     */
    List<T> getByChannelIdList(List<String> channelIds);
    
    /***
     * 通过Id来查询channel
     *
     * @param idList id列表
     * @return 相关内容
     */
    List<T> getListByIdList(String... idList);

    /**
     * 更新所有节目为未下发
     */
    void updateAllIssueState();

    /**
     * 获取全部
     *
     * @param searchDto 搜索参数
     * @return 返回全部的结果
     */
    List<T> getAll(SearchDto searchDto);
    
    List<T> getAllChannelRecord();
    
    Integer deleteChannelRecord();
    
    List<T> selectAllChannelRecord(@Param("channel_id")String channel_id,@Param("board_id")Integer board_id,@Param("module_id")Integer module_id);
}
