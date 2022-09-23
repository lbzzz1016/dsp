import request from '@/utils/request'

// 查询请假流程列表
export function listProcess(query) {
  return request({
    url: '/system/process/list',
    method: 'get',
    params: query
  })
}

// 查询请假流程详细
export function getProcess(processId) {
  return request({
    url: '/system/process/' + processId,
    method: 'get'
  })
}

// 新增请假流程
export function addProcess(data) {
  return request({
    url: '/system/process',
    method: 'post',
    data: data
  })
}

// 修改请假流程
export function updateProcess(data) {
  return request({
    url: '/system/process',
    method: 'put',
    data: data
  })
}

// 删除请假流程
export function delProcess(processId) {
  return request({
    url: '/system/process/' + processId,
    method: 'delete'
  })
}
