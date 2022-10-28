package com.ruoyi.project.domain;

import com.alibaba.excel.util.ListUtils;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import com.ruoyi.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@TableName("team_project_menu")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMenu  extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer pid;
    private String title;
    private String icon;
    private String url;
    private String filePath;
    private String params;
    private String node;
    private Integer sort;
    private Integer status;
    private Integer createBy;
    private String createAt;

    @TableField("is_inner")
    private Integer isInner;

    private String _values;
    @TableField(exist = false)
    private String values;

    @TableField("show_slider")
    private Integer showSlider;

    @TableField(exist = false)
    private String statusText;
    @TableField(exist = false)
    private String innerText;
    @TableField(exist = false)
    private String fullUrl;

    @TableField(exist = false)
    private List<ProjectMenu> children;

    public List<ProjectMenu> getChildren(){
        if(children == null || children.isEmpty()) {
            return null;
        }
        return  children;
    }

    public boolean getIs_Inner(){
        if(isInner == 0){
            return false;
        }else{
            return true;
        }
    }

    public String getStatusText(){
        if(1 == status)return "使用中";
        else if(0 == status) return "禁用";
        else return "";
    }
    public String getInnerText(){
        if(1 == isInner)return "内页";
        else if(0 == isInner) return "导航";
        else return "";
    }
    public String getFullUrl(){
        //if((null != params && null != values) || !"".equals(values)){
        if(StringUtils.isNotEmpty(params) && StringUtils.isNotEmpty(values)){
            return url+"/"+values;
        }
        return url;
    }
}
