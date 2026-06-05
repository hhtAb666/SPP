/**
 * 管理员相关 API 接口封装
 */
import request from '@/utils/request'


export function getUserList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}
export function addUser(data) {
  return request({
    url: '/user/add',
    method: 'post',
    data
  })
}
+
export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/user/delete/${id}`,
    method: 'post'
  })
}


export function getVenueList(params) {
  return request({
    url: '/venue/list',
    method: 'get',
    params
  })
}

export function addVenue(data) {
  return request({
    url: '/venue/add',
    method: 'post',
    data
  })
}

export function updateVenue(data) {
  return request({
    url: '/venue/update',
    method: 'post',
    data
  })
}

export function deleteVenue(id) {
  return request({
    url: `/venue/delete/${id}`,
    method: 'post'
  })
}


export function getScheduleList(params) {
  return request({
    url: '/schedule/list',
    method: 'get',
    params
  })
}

export function addSchedule(data) {
  return request({
    url: '/schedule/save',
    method: 'post',
    data
  })
}


export function getAllReservations(params) {
  return request({
    url: '/reservation/list',
    method: 'get',
    params
  })
}

export function auditReservation(id, status) {
  return request({
    url: `/reservation/audit/${id}`,
    method: 'post',
    params: { status }
  })
}

export function adminUpdateReservation(id, newScheduleId) {
  return request({
    url: '/reservation/admin/update',
    method: 'post',
    params: { id, newScheduleId }
  })
}
