<script setup lang="ts">
import CardItem from '@/components/CardItem.vue';
import type {TableColumnData} from '@arco-design/web-vue';

interface Example {
    name: string,
    address: string,
    cpuUsage: number,
    memoryUsage: number,
    diskUsage: number,
    pingTime: string
}

const columns: TableColumnData[] = [
    {
        title: '实例名称',
        dataIndex: 'name'
    },
    {
        title: '实例地址',
        dataIndex: 'address'
    },
    {
        title: 'CPU使用率',
        dataIndex: 'cpuUsage'
    },
    {
        title: '内存使用率',
        dataIndex: 'memoryUsage'
    },
    {
        title: '磁盘使用率',
        dataIndex: 'diskUsage'
    },
    {
        title: '心跳时间',
        dataIndex: 'pingTime'
    }
]

const tableData: Example[] = [
    {
        name: '单机',
        address: '127.0.0.1:121321',
        cpuUsage: 12.7,
        memoryUsage: 24.3,
        diskUsage: 12.3,
        pingTime: '2011-11-11 10:10:10'
    }
]
</script>

<template>
    <div>
        <div class="text-3xl font-bold text-gray-700 border-l-8 border-l-sky-400 p-4">
            运行状态实时监控面板
        </div>
        <div class="flex mt-8">
            <div class="show-item mx-2">
                <CardItem title="任务总数" value="2" color="#F7F7F7"/>
            </div>
            <div class="show-item mx-2">
                <CardItem title="当前运行实例数" value="2" color="#A2D8F4"/>
            </div>
            <div class="show-item mx-2">
                <CardItem title="近期失败任务数" value="2" color="#FF6347" text-color="white"/>
            </div>
            <div class="show-item mx-2">
                <CardItem title="实例在线数" value="2" color="#a7f2a7"/>
            </div>
        </div>
        <div class="mt-8">
            <a-table :columns="columns" :data="tableData" />
        </div>
    </div>
</template>

<style scoped>
.show-item {
    width: 25%;
}
</style>