/**
 * 用户相关 API 接口封装
 */
import request from '@/utils/request'

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}


export function getVenueList(params) {
  return request({
    url: '/venue/list',
    method: 'get',
    params
  })
}


export function getVenueSchedule(venueId, date) {
  return request({
    url: '/schedule/list',
    method: 'get',
    params: { venueId, date }
  })
}


export function createReservation(data) {
  return request({
    url: '/reservation/add',
    method: 'post',
    params: { scheduleId: data.scheduleId } 
  })
}

export function getMyReservations(params) {
  return request({
    url: '/reservation/my',
    method: 'get',
    params
  })
}

export function cancelReservation(id) {
  return request({
    url: `/reservation/cancel/${id}`,
    method: 'post' 
  })
}

export function updateReservation(data) {
  return request({
    url: '/reservation/update',
    method: 'post',
    params: { id: data.id, newScheduleId: data.newScheduleId }
  })
}


export function getSportRecords(params) {
  return request({
    url: '/sport/my',
    method: 'get',
    params
  })
}

export function addSportRecord(data) {
  return request({
    url: '/sport/add',
    method: 'post',
    data
  })
}

export function deleteSportRecord(id) {
  return request({
    url: `/sport/delete/${id}`,
    method: 'post' 
  })
}


export function getHealthRecords(params) {
  return request({
    url: '/health/my',
    method: 'get',
    params
  })
}

export function addHealthRecord(data) {
  return request({
    url: '/health/add',
    method: 'post',
    data
  })
}

export function deleteHealthRecord(id) {
  return request({
    url: `/health/delete/${id}`,
    method: 'post'
  })
}
