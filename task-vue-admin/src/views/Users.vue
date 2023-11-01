<script setup lang="ts">
import { IconSearch, IconCheck, IconRefresh } from '@arco-design/web-vue/es/icon';
import type {TableColumnData} from '@arco-design/web-vue';
import TableOption from '@/components/TableOption.vue';
import type {Page, User} from '@/server/api';
import {onMounted, reactive, ref} from 'vue';
import {UserApi} from '@/server/api';
import {formatDateTime} from '@/assets/script/utils';
import {Msg, Not} from '@/assets/script/common';
interface QueryData {
    page: number,
    size: number,
    name: string
}

const userColumns: TableColumnData[] = [
    {
        title: '名称',
        dataIndex: 'name',
        width: 150,
    },
    {
        title: '创建时间',
        slotName: 'createTime',
        width: 300,
        align: 'center'
    },
    {
        title: '最后登录时间',
        slotName: 'lastLoginTime',
        width: 300,
        align: 'center'
    },
    {
        title: '最后登录IP',
        dataIndex: 'lastLoginIp',
        width: 200,
        align: 'center'
    },
    {
        title: '备注',
        dataIndex: 'remark',
        align: 'center'
    },
    {
        title: '操作',
        slotName: 'optional',
        width: 100,
        align: 'center'
    }
]
const userModelVisible = ref<boolean>(false);
const userPage = ref<Page<User>>();
const tableLoading = ref<boolean>(false);
const queryData = reactive<QueryData>({
    page: 1,
    size: 10,
    name: ''
});
const userRef = ref()
const submitButtonLoading = ref<boolean>(false);
const searchButtonLoading = ref<boolean>(false);
const userForm = reactive({
    username: '',
    password: '',
    remark: ''
})

const getUserPage = async () => {
    tableLoading.value = true;
    const result = await UserApi.getUserList(queryData.page, queryData.size, queryData.name)
    tableLoading.value = false;
    if (result.code === 200) {
        userPage.value = result.data;
    }
}

const handlerSearch = async () => {
    searchButtonLoading.value = true;
    if (!queryData.name) {
        Msg.waring("请输入用户名称");
    } else {
        queryData.page = 1;
        await getUserPage();
    }
    searchButtonLoading.value = false;
}

const handlerSubmit = async () => {
    submitButtonLoading.value = true;
    const result = await UserApi.saveUser(userForm.username, userForm.password, userForm.remark)
    submitButtonLoading.value = false;
    if (result.code === 200) {
        Not.success("添加用户成功")
        userModelVisible.value = false;
        userRef.value.resetFields()
    }
}

const handlerDelete = async (userId: number) => {
    const message = Msg.loading("用户删除中");
    const result = await UserApi.deleteUser(userId);
    message.close();
    if (result.code === 200) {
        Not.success('用户删除成功')
        await getUserPage();
    }
}

const handlerPageChange = async (page: number) => {
    tableLoading.value = true;
    queryData.page = page;
    await getUserPage()
    tableLoading.value = false;
}

const handlerSizeChange = async (size: number) => {
    tableLoading.value = true;
    queryData.size = size;
    queryData.page = 1;
    await getUserPage();
    tableLoading.value = false;
}
onMounted(() => {
    getUserPage();
})
</script>

<template>
    <a-modal title="添加用户" :footer="false" v-model:visible="userModelVisible" width="400px">
        <a-form ref="userRef" :model="userForm" layout="vertical" @submit-success="handlerSubmit">
            <a-form-item field="username" label="用户名" :rules="[{required: true, message: '用户名不能为空'}]">
                <a-input v-model="userForm.username" placeholder="请输入用户名" size="large"/>
            </a-form-item>
            <a-form-item field="password" label="密码" :rules="[{required: true, message: '密码不能为空'}]">
                <a-input-password v-model="userForm.password" placeholder="请输入用户密码" size="large"/>
            </a-form-item>
            <a-form-item field="remark" label="备注">
                <a-textarea v-model="userForm.remark" placeholder="用户备注"/>
            </a-form-item>
            <a-form-item>
                <div class="flex justify-between w-full">
                    <div class="w-2/5">
                        <a-button html-type="submit" type="primary" long size="large" :loading="submitButtonLoading">
                            <template #default>
                                提交
                            </template>
                            <template #icon>
                                <icon-check />
                            </template>
                        </a-button>
                    </div>
                    <div class="w-2/5">
                        <a-button type="primary" long size="large" @click="userRef.resetFields()">
                            <template #default>
                                重置
                            </template>
                            <template #icon>
                                <icon-refresh />
                            </template>
                        </a-button>
                    </div>
                </div>
            </a-form-item>
        </a-form>
    </a-modal>
    <div>
        <div class="mb-8">
            <a-space>
                <a-input size="large" v-model="queryData.name" placeholder="输入用户名搜索" />
                <a-button type="primary" size="large" @click="handlerSearch" :loading="searchButtonLoading">
                    <template #default>
                        搜索
                    </template>
                    <template #icon>
                        <icon-search />
                    </template>
                </a-button>
            </a-space>
        </div>
        <div>
          <TableOption button-text="新增用户" @refresh="getUserPage" @add="userModelVisible = true"/>
        </div>
        <div v-if="userPage">
            <a-table :columns="userColumns" :data="userPage.list" :loading="tableLoading"
                     :pagination="{total: userPage.total, pageSize: userPage.size, current: userPage.page, pageSizeOptions: [10, 20, 40], defaultPageSize: 10, showPageSize: true}"
                     @page-change="handlerPageChange"
                     @page-size-change="handlerSizeChange"
            >
                <template #createTime="{ record }">
                    {{formatDateTime(record.createTime)}}
                </template>
                <template #lastLoginTime="{ record }">
                    {{formatDateTime(record.lastLoginTime)}}
                </template>
                <template #optional="{ record }">
                    <a-popconfirm content="确认删除该用户吗" position="lt" :ok-button-props="{status: 'danger'}"
                                  ok-text="确认删除" type="error" @ok="handlerDelete(record.id)">
                        <a-button type="text" status="danger">删除</a-button>
                    </a-popconfirm>
                </template>
            </a-table>
        </div>
        <div v-else>
            <a-empty description="还没有用户" />
        </div>
    </div>
</template>

<style scoped>

</style>