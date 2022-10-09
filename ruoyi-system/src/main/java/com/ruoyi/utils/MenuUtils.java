package com.ruoyi.utils;

import com.ruoyi.project.domain.ProjectMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuUtils {

    private List<ProjectMenu> menuList = new ArrayList<ProjectMenu>();
    public MenuUtils(List<ProjectMenu> menuList) {
        this.menuList=menuList;
    }

    /**
     * 递归获取菜单编号
     * treeRoot:( ). <br/>
     * @author lishang
     * @param sourceList
     * @param rootMenu
     * @return
     */
    public static  List<Integer> getMenuIds(List<ProjectMenu> sourceList,ProjectMenu rootMenu)
    {
        List<Integer> result = new ArrayList<>();
        for (ProjectMenu menu : sourceList) {
            if(rootMenu.getId()==menu.getPid()){
                result.addAll(getMenuIds(sourceList, menu));
            }
        }
        return result;
    }

    //建立树形结构
    public List<ProjectMenu> builTree(){
        List<ProjectMenu> treeMenus =new  ArrayList<ProjectMenu>();
        for(ProjectMenu menuNode : getRootNode()) {
            menuNode=buildChilTree(menuNode);
            treeMenus.add(menuNode);
        }
        return treeMenus;
    }

    //递归，建立子树形结构
    private ProjectMenu buildChilTree(ProjectMenu pNode){
        List<ProjectMenu> chilMenus =new  ArrayList<ProjectMenu>();
        for(ProjectMenu menuNode : menuList) {
            if(menuNode.getPid().equals(pNode.getId())) {
                chilMenus.add(buildChilTree(menuNode));
            }
        }
        pNode.setChildren(chilMenus);
        return pNode;
    }

    //获取根节点
    private List<ProjectMenu> getRootNode() {
        List<ProjectMenu> rootMenuLists =new  ArrayList<ProjectMenu>();
        for(ProjectMenu menuNode : menuList) {
            if(menuNode.getPid()==0) {
                rootMenuLists.add(menuNode);
            }
        }
        return rootMenuLists;
    }

}
