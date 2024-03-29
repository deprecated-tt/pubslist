/**
 * Special axios instance with token passed
 * @author victor
 * @date 11.12.2019
 * @project: pubslist
 */

import axios from 'axios';
import store from './../store/index';

const authInstance = axios.create({});

authInstance.interceptors.request.use(config => {
	config.headers.common['Authorization'] =
    'Bearer ' + store.getters['getUserToken'];
	store.commit('setIsLoading',true);
	return config;
});

authInstance.interceptors.response.use(function (response) {
	store.commit('setIsLoading',false);
	return response;
}, function (error) {
	store.commit('setIsLoading',false);
	return Promise.reject(error);
});
export default authInstance;
