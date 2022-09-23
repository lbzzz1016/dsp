package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
import com.ruoyi.system.domain.vo.SysFileVo;
import com.ruoyi.system.domain.bo.SysFileBo;
import com.ruoyi.system.service.ISysFileService;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

/**
 * 文件Controller
 *
 * @author lbzzz
 * @date 2022-09-23
 */
@Validated
@Api(value = "文件控制器", tags = {"文件管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/file")
public class SysFileController extends BaseController {

    private final ISysFileService iSysFileService;

    /**
     * 查询文件列表
     */
    @ApiOperation("查询文件列表")
    @SaCheckPermission("system:file:list")
    @GetMapping("/list")
    public TableDataInfo<SysFileVo> list(SysFileBo bo, PageQuery pageQuery) {
        return iSysFileService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出文件列表
     */
    @ApiOperation("导出文件列表")
    @SaCheckPermission("system:file:export")
    @Log(title = "文件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysFileBo bo, HttpServletResponse response) {
        List<SysFileVo> list = iSysFileService.queryList(bo);
        ExcelUtil.exportExcel(list, "文件", SysFileVo.class, response);
    }

    /**
     * 获取文件详细信息
     */
    @ApiOperation("获取文件详细信息")
    @SaCheckPermission("system:file:query")
    @GetMapping("/{id}")
    public R<SysFileVo> getInfo(@ApiParam("主键")
                                     @NotNull(message = "主键不能为空")
                                     @PathVariable("id") Integer id) {
        return R.ok(iSysFileService.queryById(id));
    }

    /**
     * 新增文件
     */
    @ApiOperation("新增文件")
    @SaCheckPermission("system:file:add")
    @Log(title = "文件", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysFileBo bo) {
        return toAjax(iSysFileService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改文件
     */
    @ApiOperation("修改文件")
    @SaCheckPermission("system:file:edit")
    @Log(title = "文件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysFileBo bo) {
        return toAjax(iSysFileService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除文件
     */
    @ApiOperation("删除文件")
    @SaCheckPermission("system:file:remove")
    @Log(title = "文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@ApiParam("主键串")
                                       @NotEmpty(message = "主键不能为空")
                                       @PathVariable Integer[] ids) {
        return toAjax(iSysFileService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
