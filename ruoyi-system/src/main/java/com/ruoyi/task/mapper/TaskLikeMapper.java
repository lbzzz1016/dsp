package com.ruoyi.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.task.domain.TaskLike;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface TaskLikeMapper extends BaseMapper<TaskLike> {

}
