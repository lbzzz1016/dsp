package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.system.domain.SysClockIn;
import com.ruoyi.system.domain.SysProcess;
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
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.vo.SysClockInVo;
import com.ruoyi.system.domain.bo.SysClockInBo;
import com.ruoyi.system.service.ISysClockInService;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

/**
 * 补卡流程Controller
 *
 * @author lbzzz
 * @date 2023-04-12
 */
@Validated
@Api(value = "补卡流程控制器", tags = {"补卡流程管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/clockIn")
public class SysClockInController extends BaseController {

    private final ISysClockInService iSysClockInService;

    /**
     * 查询补卡流程列表
     */
    @ApiOperation("查询补卡流程列表")
    @SaCheckPermission("system:clockIn:list")
    @GetMapping("/list")
    public TableDataInfo<SysClockInVo> list(SysClockInBo bo, PageQuery pageQuery) {
        return iSysClockInService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出补卡流程列表
     */
    @ApiOperation("导出补卡流程列表")
    @SaCheckPermission("system:clockIn:export")
    @Log(title = "补卡流程", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysClockInBo bo, HttpServletResponse response) {
        List<SysClockInVo> list = iSysClockInService.queryList(bo);
        ExcelUtil.exportExcel(list, "补卡流程", SysClockInVo.class, response);
    }

    /**
     * 获取补卡流程详细信息
     */
    @ApiOperation("获取补卡流程详细信息")
    @SaCheckPermission("system:clockIn:query")
    @GetMapping("/{processId}")
    public R<SysClockInVo> getInfo(@ApiParam("主键")
                                     @NotNull(message = "主键不能为空")
                                     @PathVariable("processId") Long processId) {
        return R.ok(iSysClockInService.queryById(processId));
    }

    /**
     * 新增补卡流程
     */
    @ApiOperation("新增补卡流程")
    @SaCheckPermission("system:clockIn:add")
    @Log(title = "补卡流程", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysClockInBo bo) {
        SysClockIn add = BeanUtil.toBean(bo, SysClockIn.class);
        return toAjax(iSysClockInService.insert(add) ? 1 : 0);
    }

    /**
     * 修改补卡流程
     */
    @ApiOperation("修改补卡流程")
    @SaCheckPermission("system:clockIn:edit")
    @Log(title = "补卡流程", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysClockInBo bo) {
        SysClockIn update = BeanUtil.toBean(bo, SysClockIn.class);
        return toAjax(iSysClockInService.update(update) ? 1 : 0);
    }

    /**
     * 删除补卡流程
     */
    @ApiOperation("删除补卡流程")
    @SaCheckPermission("system:clockIn:remove")
    @Log(title = "补卡流程", businessType = BusinessType.DELETE)
    @DeleteMapping("/{processIds}")
    public R<Void> remove(@ApiParam("主键串")
                                       @NotEmpty(message = "主键不能为空")
                                       @PathVariable Long[] processIds) {
        return toAjax(iSysClockInService.deleteWithValidByIds(Arrays.asList(processIds), true) ? 1 : 0);
    }
}
