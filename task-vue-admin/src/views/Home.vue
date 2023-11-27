<script setup lang="ts">
import CardItem from '@/components/CardItem.vue';
import type {TableColumnData} from '@arco-design/web-vue';
import {useTaskNodeStore} from '@/stores/task_node';
import {formatDateTime, formatSize} from '@/assets/script/utils';

const taskNodeStore = useTaskNodeStore();

const columns: TableColumnData[] = [
    {
        title: '名称',
        dataIndex: 'name'
    },
    {
        title: '地址',
        dataIndex: 'address',
        align: 'center'
    },
    {
        title: 'CPU',
        slotName: 'cpuUsed',
        align: 'center'
    },
    {
        title: 'Memory',
        slotName: 'memoryUsed',
        align: 'center'
    },
    {
        title: 'Disk',
        slotName: 'diskUsed',
        align: 'center'
    },
    {
        title: '心跳时间',
        slotName: 'time',
        align: 'center'
    },
    {
        title: '状态',
        slotName: 'status'
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
                <CardItem title="实例在线数" :value="taskNodeStore.taskNodeOnlineCount.toString()" color="#a7f2a7"/>
            </div>
        </div>
        <div class="mt-8">
            <a-table :columns="columns" :data="taskNodeStore.taskNodeList" :pagination="false">
                <template #cpuUsed="{ record }">
                    <span v-if="record.info">
                        {{record.info.usedCpu.toFixed(2)}} %
                    </span>
                </template>
                <template #memoryUsed="{ record }">
                    <span v-if="record.info">
                        {{`${formatSize(record.info.usedMemory)} / ${formatSize(record.info.totalMemory)}`}} GB
                    </span>
                </template>
                <template #diskUsed="{ record }">
                    <span v-if="record.info">
                        {{`${formatSize(record.info.usedDisk)} / ${formatSize(record.info.totalDisk)}`}} GB
                    </span>
                </template>
                <template #time="{ record }">
                    {{formatDateTime(record.onlineTime)}}
                </template>
                <template #status="{ record }">
                    <span v-if="record.status === 0" class="text-green-500">
                        在线
                    </span>
                    <span v-else class="text-red-500">
                        离线
                    </span>
                </template>
            </a-table>
        </div>
    </div>
</template>

<style scoped>
.show-item {
    width: 25%;
}
</style>