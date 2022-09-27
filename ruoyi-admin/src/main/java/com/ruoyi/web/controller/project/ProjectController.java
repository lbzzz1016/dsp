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
import com.ruoyi.project.domain.vo.ProjectVo;
import com.ruoyi.project.domain.bo.ProjectBo;
import com.ruoyi.project.service.ProjectService;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

/**
 * 项目Controller
 *
 * @author lbzzz
 * @date 2022-09-26
 */
@Validated
@Api(value = "项目控制器", tags = {"项目管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/project")
public class ProjectController extends BaseController {

    private final ProjectService projectService;

    /**
     * 查询项目列表
     */
    @ApiOperation("查询项目列表")
    @SaCheckPermission("system:project:list")
    @GetMapping("/list")
    public TableDataInfo<ProjectVo> list(ProjectBo bo, PageQuery pageQuery) {
        return projectService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出项目列表
     */
    @ApiOperation("导出项目列表")
    @SaCheckPermission("system:project:export")
    @Log(title = "项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProjectBo bo, HttpServletResponse response) {
        List<ProjectVo> list = projectService.queryList(bo);
        ExcelUtil.exportExcel(list, "项目", ProjectVo.class, response);
    }

    /**
     * 获取项目详细信息
     */
    @ApiOperation("获取项目详细信息")
    @SaCheckPermission("system:project:query")
    @GetMapping("/{id}")
    public R<ProjectVo> getInfo(@ApiParam("主键")
                                     @NotNull(message = "主键不能为空")
                                     @PathVariable("id") Integer id) {
        return R.ok(projectService.queryById(id));
    }

    /**
     * 新增项目
     */
    @ApiOperation("新增项目")
    @SaCheckPermission("system:project:add")
    @Log(title = "项目", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProjectBo bo) {
        return toAjax(projectService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改项目
     */
    @ApiOperation("修改项目")
    @SaCheckPermission("system:project:edit")
    @Log(title = "项目", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProjectBo bo) {
        return toAjax(projectService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除项目
     */
    @ApiOperation("删除项目")
    @SaCheckPermission("system:project:remove")
    @Log(title = "项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@ApiParam("主键串")
                                       @NotEmpty(message = "主键不能为空")
                                       @PathVariable Integer[] ids) {
        return toAjax(projectService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
