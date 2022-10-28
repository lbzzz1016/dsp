package com.ruoyi.project.service;

import cn.hutool.json.JSON;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.JsonUtils;
import com.ruoyi.project.domain.ProjectNode;
import com.ruoyi.project.mapper.ProjectNodeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class ProjectNodeService extends ServiceImpl<ProjectNodeMapper, ProjectNode> {

    public List<Map> getProjectNodeByNodeLike(String node) {

        return baseMapper.selectProjectNodeByNodeLike(node);
    }

    public List<Map> getAllProjectNode() {
        return baseMapper.selectAllProjectNode();
    }

    public void get(String module) {
        if ("project".equals(module)) {

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveNode(String jsonList) {
        List<Map> maps = JsonUtils.parseArray(jsonList, Map.class);
        maps.forEach(o -> {
            ProjectNode.ProjectNodeBuilder node = ProjectNode.builder().node(o.get("node").toString());
            List<Map> name = (List<Map>) o.get("name");
            List<Map> value = (List<Map>) o.get("value");
            for (int i = 0; i < name.size(); i++) {
                String auth = name.get(i).get("name").toString();
                Boolean authVal = (Boolean) value.get(i).get("value");
                if (Objects.equals("is_auth", auth)) {
                    node.isAuth(authVal ? 1 : 0);
                } else {
                    node.isLogin(authVal ? 1 : 0);
                }
            }
            ProjectNode build = node.build();
            LambdaUpdateChainWrapper<ProjectNode> wrapper = lambdaUpdate().eq(ProjectNode::getNode, build.getNode());
            if (build.getIsAuth() != null){
                wrapper.set(ProjectNode::getIsAuth, build.getIsAuth());
            }
            if (build.getIsLogin() != null){
                wrapper.set(ProjectNode::getIsLogin, build.getIsLogin());
            }
            boolean update = wrapper.update();
            log.info("节点：{},保存：{}", build.getNode(), update);
        });
        return true;
    }
}
