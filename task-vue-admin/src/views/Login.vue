<script setup lang="ts">
import { IconUser, IconUnlock } from '@arco-design/web-vue/es/icon';
import {reactive, ref} from 'vue';

interface LoginForm {
    username: string
    password: string
    keep: boolean
}
const loginButtonLoading = ref<boolean>(false);
const loginForm = reactive<LoginForm>({
    username: '',
    password: '',
    keep: false
})


const handleSubmit = () => {
    loginButtonLoading.value = true;
    console.log(loginForm);
    setTimeout(() => {
        loginButtonLoading.value = false;
    }, 500)

}
</script>

<template>
    <div class="absolute left-0 right-0 top-0 bottom-0 flex bg-gray-50">
        <div class="login-left h-full w-1/3 flex justify-center items-center flex-shrink-0">
            <img class="w-4/5 h-auto" src="/images/login-bg.png" alt="bg">
        </div>
        <div class="flex items-center justify-center w-full">
            <div class="bg-white rounded-2xl p-8 border global-shadow w-1/3">
                <p class="text-xl my-8 py-2">欢迎使用Go-Task任务调度平台</p>
                <a-form :model="loginForm" :label-col-props="{span: 0}" auto-label-width @submit-success="handleSubmit">
                    <a-form-item field="username" :rules="[{required: true, message: '用户名不能为空'}]">
                        <a-input style="border-radius: 6px" v-model="loginForm.username" placeholder="请输入用户名" size="large">
                            <template #prefix>
                                <icon-user />
                            </template>
                        </a-input>
                    </a-form-item>
                    <a-form-item field="password" :rules="[{required: true, message: '密码不能为空'}]">
                        <a-input-password style="border-radius: 6px" v-model="loginForm.password" size="large" placeholder="请输入密码">
                            <template #prefix>
                                <icon-unlock />
                            </template>
                        </a-input-password>
                    </a-form-item>
                    <a-form-item>
                        <a-button html-type="submit" style="border-radius: 6px" type="primary" long size="large" :loading="loginButtonLoading">
                            登录
                        </a-button>
                    </a-form-item>
                    <a-form-item>
                        <a-checkbox v-model="loginForm.keep">保持登录</a-checkbox>
                    </a-form-item>
                </a-form>
            </div>
        </div>
    </div>
</template>

<style scoped>
.login-left {
    background-color: #E2F3FA;
}
</style>