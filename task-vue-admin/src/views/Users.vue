<script setup lang="ts">
import { IconSearch } from '@arco-design/web-vue/es/icon';
import type {TableColumnData} from '@arco-design/web-vue';
import TableOption from '@/components/TableOption.vue';

interface User {
    name: string,
    createTime: string,
    lastLoginTime: string,
    lastLoginIp: string,
    remark: string
}

const userColumns: TableColumnData[] = [
    {
        title: '名称',
        dataIndex: 'name',
        width: 150,
    },
    {
        title: '创建时间',
        dataIndex: 'createTime',
        width: 300,
        align: 'center'
    },
    {
        title: '最后登录时间',
        dataIndex: 'lastLoginTime',
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

const userData: User[] = [
    {
        name: '小明',
        createTime: '2011-11-11 10:10:10',
        lastLoginTime: '2021-10-10 10:10:10',
        lastLoginIp: '127.0.0.1',
        remark: '这是一条备注'
    }
]
</script>

<template>
    <div>
        <div class="mb-8">
            <a-space>
                <a-input size="large" placeholder="输入用户名搜索" />
                <a-button type="primary" size="large">
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
          <TableOption button-text="新增用户"/>
        </div>
        <div>
            <a-table :columns="userColumns" :data="userData">
                <template #optional>
                    <a-popconfirm content="确认删除该用户吗" position="lt" :ok-button-props="{status: 'danger'}"
                                  ok-text="确认删除" type="error">
                        <a-button type="text" status="danger">删除</a-button>
                    </a-popconfirm>
                </template>
            </a-table>
        </div>
    </div>
</template>

<style scoped>

</style>