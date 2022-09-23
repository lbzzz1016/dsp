package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Arrays;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.system.domain.SysProcess;
import com.ruoyi.system.domain.bo.SysProcessBo;
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
import com.ruoyi.system.domain.vo.SysProcessVo;
import com.ruoyi.system.service.ISysProcessService;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

/**
 * 请假流程Controller
 *
 * @author LBZ
 * @date 2022-09-15
 */
@Validated
@Api(value = "请假流程控制器", tags = {"请假流程管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/process")
public class SysProcessController extends BaseController {

    private final ISysProcessService iSysProcessService;

    /**
     * 查询请假流程列表
     */
    @ApiOperation("查询请假流程列表")
    @SaCheckPermission("system:process:list")
    @GetMapping("/list")
    public TableDataInfo<SysProcessVo> list(SysProcessBo bo, PageQuery pageQuery) {
        return iSysProcessService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出请假流程列表
     */
    @ApiOperation("导出请假流程列表")
    @SaCheckPermission("system:process:export")
    @Log(title = "请假流程", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysProcessBo bo, HttpServletResponse response) {
        List<SysProcessVo> list = iSysProcessService.queryList(bo);
        ExcelUtil.exportExcel(list, "请假流程", SysProcessVo.class, response);
    }

    /**
     * 获取请假流程详细信息
     */
    @ApiOperation("获取请假流程详细信息")
    @SaCheckPermission("system:process:query")
    @GetMapping("/{processId}")
    public R<SysProcessVo> getInfo(@ApiParam("主键")
                                     @NotNull(message = "主键不能为空")
                                     @PathVariable("processId") Long processId) {
        return R.ok(iSysProcessService.queryById(processId));
    }

    /**
     * 新增请假流程
     */
    @ApiOperation("新增请假流程")
    @SaCheckPermission("system:process:add")
    @Log(title = "请假流程", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysProcessBo bo) {
        SysProcess add = BeanUtil.toBean(bo, SysProcess.class);
        return toAjax(iSysProcessService.insert(add) ? 1 : 0);
    }

    /**
     * 修改请假流程
     */
    @ApiOperation("修改请假流程")
    @SaCheckPermission("system:process:edit")
    @Log(title = "请假流程", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysProcessBo bo) {
        SysProcess update = BeanUtil.toBean(bo, SysProcess.class);
        return toAjax(iSysProcessService.update(update) ? 1 : 0);
    }

    /**
     * 删除请假流程
     */
    @ApiOperation("删除请假流程")
    @SaCheckPermission("system:process:remove")
    @Log(title = "请假流程", businessType = BusinessType.DELETE)
    @DeleteMapping("/{processIds}")
    public R<Void> remove(@ApiParam("主键串")
                                       @NotEmpty(message = "主键不能为空")
                                       @PathVariable Long[] processIds) {
        return toAjax(iSysProcessService.deleteWithValidByIds(Arrays.asList(processIds), true) ? 1 : 0);
    }
}
