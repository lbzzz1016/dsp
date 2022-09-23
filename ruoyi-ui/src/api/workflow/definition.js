import request from '@/utils/request'

// 查询流程定义列表
export function listDefinition(query) {
  return request({
    url: '/workflow/definition/list',
    method: 'get',
    params: query
  })
}

// 查询指定流程发布的版本列表
export function publishList(query) {
  return request({
    url: '/workflow/definition/publishList',
    method: 'get',
    params: query
  })
}


// 部署流程实例
export function definitionStart(procDefId,data) {
  return request({
    url: '/workflow/definition/start/' + procDefId,
    method: 'post',
    data: data
  })
}

// 获取流程变量
export function getProcessVariables(taskId) {
  return request({
    url: '/workflow/task/processVariables/' + taskId,
    method: 'get'
  })
}

// 激活/挂起流程
export function updateState(params) {
  return request({
    url: '/workflow/definition/updateState',
    method: 'put',
    params: params
  })
}

// 读取xml文件
export function readXml(definitionId) {
  return request({
    url: '/workflow/definition/readXml/' + definitionId,
    method: 'get'
  })
}
// 读取image文件
export function readImage(deployId) {
  return request({
    url: '/workflow/definition/readImage/' + deployId,
    method: 'get'
  })
}

// 读取image文件
export function getFlowViewer(procInsId) {
  return request({
    url: '/workflow/task/flowViewer/' + procInsId,
    method: 'get'
  })
}

// 读取xml文件
export function saveXml(data) {
  return request({
    url: '/workflow/definition/save',
    method: 'post',
    data: data
  })
}

// 删除流程定义
export function delDeployment(query) {
  return request({
    url: '/workflow/definition/delete/',
    method: 'delete',
    params: query
  })
}

// 导出流程定义
export function exportDeployment(query) {
  return request({
    url: '/system/deployment/export',
    method: 'get',
    params: query
  })
}
