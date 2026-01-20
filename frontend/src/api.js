import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export const api = {
  getStatus: () => axios.get(`${API_BASE_URL}/status`),
  getVideos: () => axios.get(`${API_BASE_URL}/videos`),
  getVideo: (id) => axios.get(`${API_BASE_URL}/videos/${id}`),
  deleteVideo: (id) => axios.delete(`${API_BASE_URL}/videos/${id}`),
  generateVideo: (channel) => axios.post(`${API_BASE_URL}/generate/${channel}`),
  generateAll: () => axios.post(`${API_BASE_URL}/generate/all`),
  getQuota: () => axios.get(`${API_BASE_URL}/quota`),
};
