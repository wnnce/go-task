import axios from 'axios';
import {useRouter} from 'vue-router';
import {Constants, Msg} from '@/assets/script/common';
export declare type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'
export interface HttpParams {
    url: string,
    method: HttpMethod,
    data?: object,
    params?: object
}

export interface Result<T> {
    code: number,
    message: string,
    timestamp: number,
    data: T
}

const baseUrl: string = import.meta.env.VITE_BASE_URL;
const router = useRouter();

axios.defaults.timeout = 10000;
axios.defaults.withCredentials = true;
axios.interceptors.request.use(req => {
    const token = localStorage.getItem(Constants.TOKEN_KEY)
    if (token) {
        req.headers["Authorization"] = "Bearer " + token
    }
    return req
})

axios.interceptors.response.use(res => {
    return res
}, error => {
    const message: string = error.message;
    if (message.includes("401")) {
        localStorage.removeItem(Constants.TOKEN_KEY)
        Msg.error('登录后才允许访问')
        setTimeout(() => {
            router.push({
                path: '/login'
            })
        }, 400)
    }else if (message.includes("timeout")) {
        Msg.error("网络连接超时")
    } else {
        Msg.error(message)
    }
})

async function axiosRequest<T>(params: HttpParams): Promise<Result<T>> {
    params.url = baseUrl + params.url;
    return new Promise<Result<T>>((resolve, reject) => {
        axios(params).then(res => {
            resolve(res.data)
        }).catch(err => {
            console.log(err);
        })
    })
}

export function sendGet<T>(url: string, data?: object) {
    const params: HttpParams = {
        url: url,
        method: 'GET',
        params: data
    }
    return axiosRequest<T>(params)
}

export function sendPost<T>(url: string, param?: object, data?: object) {
    const params: HttpParams = {
        url: url,
        method: 'POST',
        params: param,
        data: data
    }
    return axiosRequest<T>(params)
}

export function sendPut<T>(url: string, param: object, data?: Object){
    const params: HttpParams = {
        url: url,
        method: 'PUT',
        params: param,
        data: data
    }
    return axiosRequest<T>(params)
}

export function sendDelete<T>(url: string, param?: object) {
    const params: HttpParams = {
        url: url,
        method: 'DELETE',
        params: param
    }
    return axiosRequest<T>(params);
}