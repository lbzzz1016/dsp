package com.ruoyi.web.controller.project;

import java.util.List;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.project.domain.vo.ProjectLogVo;
import com.ruoyi.project.domain.bo.ProjectLogBo;
import com.ruoyi.project.service.ProjectLogService;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

/**
 * 项目日志Controller
 *
 * @author lbzzz
 * @date 2022-09-26
 */
@Validated
@Api(value = "项目日志控制器", tags = {"项目日志管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/project/projectLog")
public class ProjectLogController extends BaseController {

    private final ProjectLogService projectLogService;

    /**
     * 查询项目日志列表
     */
    @ApiOperation("查询项目日志列表")
    @SaCheckPermission("project:projectLog:list")
    @GetMapping("/list")
    public TableDataInfo<ProjectLogVo> list(ProjectLogBo bo, PageQuery pageQuery) {
        return projectLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出项目日志列表
     */
    @ApiOperation("导出项目日志列表")
    @SaCheckPermission("project:projectLog:export")
    @Log(title = "项目日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProjectLogBo bo, HttpServletResponse response) {
        List<ProjectLogVo> list = projectLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "项目日志", ProjectLogVo.class, response);
    }

    /**
     * 获取项目日志详细信息
     */
    @ApiOperation("获取项目日志详细信息")
    @SaCheckPermission("project:projectLog:query")
    @GetMapping("/{id}")
    public R<ProjectLogVo> getInfo(@ApiParam("主键")
                                     @NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(projectLogService.queryById(id));
    }

    /**
     * 新增项目日志
     */
    @ApiOperation("新增项目日志")
    @SaCheckPermission("project:projectLog:add")
    @Log(title = "项目日志", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProjectLogBo bo) {
        return toAjax(projectLogService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改项目日志
     */
    @ApiOperation("修改项目日志")
    @SaCheckPermission("project:projectLog:edit")
    @Log(title = "项目日志", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProjectLogBo bo) {
        return toAjax(projectLogService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除项目日志
     */
    @ApiOperation("删除项目日志")
    @SaCheckPermission("project:projectLog:remove")
    @Log(title = "项目日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@ApiParam("主键串")
                                       @NotEmpty(message = "主键不能为空")
                                       @PathVariable Long[] ids) {
        return toAjax(projectLogService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
