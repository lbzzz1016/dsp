package com.ruoyi.task.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.domain.ProjectReport;
import com.ruoyi.project.service.ProjectReportService;
import com.ruoyi.project.service.ProjectService;
import com.ruoyi.task.domain.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @program: teamwork
 * @package:
 * @description: 定时任务
 * @author: lzd
 * @create: 2020-08-04 15:19
 **/
@Slf4j
@Configuration
@EnableScheduling
public class ScheduleTask {

    @Autowired
    TaskProjectService taskProjectService;
    @Autowired
    ProjectService projectService;
    @Autowired
    ProjectReportService projectReportService;

    /**
     * 每天执行计算任务完成情况
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void taskSettlement() {
        taskCountInfo(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void taskCountInfo(List<String> proCodeList) {
        if (proCodeList == null) {
            List<Project> projects = projectService.lambdaQuery().select(Project::getCode).list();
            proCodeList = projects == null ? null : projects.parallelStream().map(Project::getCode).distinct().collect(Collectors.toList());
        }
        if (proCodeList != null) {
            proCodeList.forEach(pro -> {
                for (int i = -9; i <= -1; i++) {
                    LocalDate now = LocalDate.now().plusDays(i);
                    LocalDate date = now.plusDays(-1);
                    List<Task> list = taskProjectService.lambdaQuery().eq(Task::getDeleted, 0).eq(Task::getProjectCode, pro).lt(Task::getCreateTime, now).list();
                    Map<String, Object> map = new HashMap<>(8);
                    int task = 0;
                    int undoneTask = 0;
                    int baseLineList = 0;
                    if (list != null) {
                        task = list.size();
                        undoneTask = (int) list.stream().filter(o -> o.getDone() == 0).count();
                        baseLineList = (int) list.stream().filter(o -> o.getDone() == 0).filter(o -> {
                            if (StrUtil.isEmpty(o.getEndTime())) {
                                if (StrUtil.isNotEmpty(o.getCreateTime())) {
                                    LocalDate create = LocalDate.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS));
                                    return create.plusDays(5).isAfter(now);
                                }
                                return true;
                            } else {
                                LocalDate end = LocalDate.parse(o.getEndTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS));
                                return end.plusDays(-1).isBefore(now);
                            }
                        }).count();
                    }
                    map.put("task", task);
                    map.put("undoneTask", undoneTask);
                    map.put("baseLineList", baseLineList);
                    String content = JSONUtil.toJsonStr(map);
                    String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS));
                    ProjectReport.ProjectReportBuilder projectReportBuilder = ProjectReport.builder().content(content).update_time(dateStr);
                    ProjectReport one = projectReportService.lambdaQuery().eq(ProjectReport::getProject_code, pro).eq(ProjectReport::getDate, date).one();
                    if (one != null) {
                        ProjectReport build = projectReportBuilder.id(one.getId()).build();
                        boolean update = projectReportService.updateById(build);
                        log.info("更新项目完成数量：{}", update);
                    } else {
                        ProjectReport build = projectReportBuilder.create_time(dateStr).project_code(pro)
                                .date(date.format(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS))).build();
                        boolean save = projectReportService.save(build);
                        log.info("新增项目完成数量：{}", save);
                    }
                }

            });

        }
    }
}
