package com.ruoyi.project.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.project.domain.ProjectReport;
import com.ruoyi.project.mapper.ProjectReportMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProjectReportService extends ServiceImpl<ProjectReportMapper, ProjectReport> {

    /**
     *
     * 计算最近n天的数据
     */
	 public Map getReportByDay(String projectCode, Integer day) {
         Map<String, Object> result = new HashMap<>();
         LocalDate now = LocalDate.now();
         List<String> date = new ArrayList<>();
         List<Integer> task = new ArrayList<>();
         List<Integer> undoneTask = new ArrayList<>();
         List<Integer> baseLineList = new ArrayList<>();
         List<LocalDate> dateList = Stream.iterate(now, o -> o.plusDays(-1)).limit(day).collect(Collectors.toList());
         List<ProjectReport> projectReports = lambdaQuery().in(ProjectReport::getDate, dateList).eq(ProjectReport::getProject_code, projectCode)
                 .orderByAsc(ProjectReport::getDate).list();
         if (projectReports != null) {
             projectReports.forEach(o -> {
                 date.add(o.getDate().substring(5));
                 Map<String, Object> map = JSONUtil.parseObj(o.getContent());
                 task.add((int) map.get("task"));
                 undoneTask.add((int) map.get("undoneTask"));
                 baseLineList.add((int) map.get("baseLineList"));
             });
         }
         result.put("date", date);
         result.put("task", task);
         result.put("undoneTask", undoneTask);
         result.put("baseLineList", baseLineList);
         return result;
    }


}
