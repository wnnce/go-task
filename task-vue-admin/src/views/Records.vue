<script setup lang="ts">
import {IconDelete, IconInfo, IconSearch, IconClockCircle} from '@arco-design/web-vue/es/icon';
import type {TableColumnData} from '@arco-design/web-vue';
import type {Description} from '@/views/Tasks.vue';
import {ref} from 'vue';

interface Record {
    id: number,
    taskId: number,
    taskName: string,
    executeCount: number,
    executeName: string,
    executeAddress: string,
    executeParams: string,
    createTime: string,
    runnerTime: string,
    stopTime: string,
    status: number,
    resultContent: string
}

const recordColumns: TableColumnData[] = [
    {
        title: '任务ID',
        dataIndex: 'taskId',
        width: 100,
        align: 'center'
    },
    {
        title: '任务名称',
        dataIndex: 'taskName',
    },
    {
        title: '执行实例',
        dataIndex: 'executeName'
    },
    {
        title: '重试次数',
        dataIndex: 'executeCount',
        width: 100,
        align: 'center'
    },
    {
        title: '创建时间',
        dataIndex: 'createTime',
        width: 180,
        align: 'center'
    },
    {
        title: '结束时间',
        dataIndex: 'stopTime',
        width: 180,
        align: 'center'
    },
    {
        title: '状态',
        slotName: 'status',
        width: 100,
        align: 'center'
    },
    {
        title: '操作',
        slotName: 'optional',
        width: 240,
        align: 'center'
    }
]
const recordData: Record[] = [
    {
        id: 1,
        taskId: 1,
        taskName: '测试任务',
        executeCount: 0,
        executeName: '单机',
        executeAddress: '127.0.0.1:8454',
        executeParams: '{"name": "xin"}',
        createTime: '2011-11-11 10:10:10',
        runnerTime: '2011-11-11 10:10:10',
        stopTime: '2011-11-11 10:10:10',
        status: 0,
        resultContent: '成功'
    }
]
const infoModelVisible = ref<boolean>(false);
const recordInfoData = ref();
const recordToDescriptions = (record: Record): Description[] => {
    return [
        {
            label: '任务ID',
            value: record.taskId
        },
        {
            label: '任务名称',
            value: record.taskName
        },
        {
            label: '重试次数',
            value: record.executeCount
        },
        {
            label: '执行实例名称',
            value: record.executeName
        },
        {
            label: '执行实例地址',
            value: record.executeAddress
        },
        {
            label: '执行参数',
            value: record.executeParams
        },
        {
            label: '创建时间',
            value: record.createTime
        },
        {
            label: '运行时间',
            value: record.runnerTime
        },
        {
            label: '结束时间',
            value: record.stopTime
        },
        {
            label: '状态',
            value: () => {
                let text: string = '';
                switch (record.status){
                    case 0: text = '成功';break;
                    case 1: text = '等待中';break;
                    case 2: text = '运行中';break;
                    case 3: text = '失败';break;
                }
                return text;
            }
        },
        {
            label: '运行结果',
            value: record.resultContent
        }
    ]
}

const handlerRecordInfo = (recordId: number) => {
    console.log(recordId);
    recordInfoData.value = recordToDescriptions(recordData[0]);
    infoModelVisible.value = true;
}
</script>

<template>
    <a-modal v-model:visible="infoModelVisible" title="运行记录详情" title-align="start">
        <a-descriptions :data="recordInfoData" :column="1" />
    </a-modal>
    <div>
        <div class="mb-8">
            <a-space>
                <a-input placeholder="任务ID" size="large"/>
                <a-select placeholder="任务状态" size="large" style="width: 120px">
                    <a-option value="1">等待中</a-option>
                    <a-option value="2">运行中</a-option>
                    <a-option value="0">成功</a-option>
                    <a-option value="4">失败</a-option>
                </a-select>
                <a-button type="primary" size="large">
                    <template #default>
                        搜索
                    </template>
                    <template #icon>
                        <icon-search />
                    </template>
                </a-button>
                <a-button size="large">
                    <template #default>
                        清空
                    </template>
                    <template #icon>
                        <icon-delete />
                    </template>
                </a-button>
            </a-space>
        </div>
        <div>
            <a-table :columns="recordColumns" :data="recordData" >
                <template #status="{ record }">
                    <a-typography-text type="success" v-if="record.status === 0">成功</a-typography-text>
                    <a-typography-text type="danger" v-if="record.status === 3">失败</a-typography-text>
                    <a-typography-text v-if="record.status === 1">等待中</a-typography-text>
                    <a-typography-text v-if="record.status === 2">执行中</a-typography-text>
                </template>
                <template #optional="{ record }">
                    <a-space>
                        <a-button type="primary" shape="circle" @click="handlerRecordInfo(record.id)">
                            <template #icon>
                                <icon-info/>
                            </template>
                        </a-button>
                        <a-button type="outline" shape="circle">
                            <template #icon>
                                <icon-clock-circle />
                            </template>
                        </a-button>
                        <a-button type="outline" status="warning" shape="circle">
                            <template #icon>
                                <icon-refresh/>
                            </template>
                        </a-button>
                        <a-button type="outline" status="danger" shape="circle">
                            <template #icon>
                                <icon-delete/>
                            </template>
                        </a-button>
                    </a-space>
                </template>
            </a-table>
        </div>
    </div>
</template>

<style scoped>

</style>