<script setup lang="ts">
import {IconDelete, IconInfo, IconSearch, IconClockCircle, IconLoop} from '@arco-design/web-vue/es/icon';
import type {TableColumnData} from '@arco-design/web-vue';
import type {Description} from '@/views/Tasks.vue';
import {onMounted, reactive, ref} from 'vue';
import type {Page, Record, TaskLog} from '@/server/api';
import {RecordApi, TaskLogApi} from '@/server/api';
import {formatDateTime} from '@/assets/script/utils';
import {Msg, Not} from '@/assets/script/common';
import TableOption from '@/components/TableOption.vue';
export interface RecordQueryData {
    page: number,
    size: number,
    taskId?: number,
    status?: number
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
        slotName: 'createTime',
        width: 180,
        align: 'center'
    },
    {
        title: '结束时间',
        slotName: 'stopTime',
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
const queryData = reactive<RecordQueryData>({
    page: 1,
    size: 10,
    taskId: undefined,
    status: undefined
})
const recordPage = ref<Page<Record>>();
const tableLoading = ref<boolean>(false);
const searchLoading = ref<boolean>(false);
const infoModelVisible = ref<boolean>(false);
const recordInfoData = ref();
const logModelVisible = ref<boolean>(false);
const recordLogsLoading = ref<boolean>(false);
const recordLogList = ref<TaskLog[]>()
const logInfoVisible = ref<boolean>(false);
const recordLogInfo = ref<TaskLog>()
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
            value: formatDateTime(record.createTime)
        },
        {
            label: '运行时间',
            value: formatDateTime(record.runnerTime)
        },
        {
            label: '结束时间',
            value: formatDateTime(record.stopTime)
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

const getRecordPage = async () => {
    tableLoading.value = true;
    const result = await RecordApi.getRecordList(queryData)
    tableLoading.value = false;
    if (result.code === 200) {
        recordPage.value = result.data
    }
}
const handlerRecordInfo = async (recordId: number) => {
    infoModelVisible.value = true;
    const result = await RecordApi.queryRecordInfo(recordId);
    if (result.code === 200) {
        recordInfoData.value = recordToDescriptions(result.data);
    }
}

const handlerDelete = async (recordId: number) => {
    const message = Msg.loading("记录删除中")
    const result = await RecordApi.deleteRecord(recordId);
    if (result.code === 200) {
        message.close();
        getRecordPage();
        Not.success("删除成功")
    }
}

const handlerSearch = async () => {
    searchLoading.value = true;
    await getRecordPage();
    searchLoading.value = false;
}

const resetQueryData = () => {
    queryData.status = undefined;
    queryData.taskId = undefined;
}

const handlerRecordLogs = async (recordId: number) => {
    logModelVisible.value = true;
    recordLogsLoading.value = true;
    const result = await TaskLogApi.getLogListByRecordId(recordId);
    recordLogsLoading.value = false;
    if (result.code === 200) {
        recordLogList.value = result.data;
    }
}

const handlerLogInfo = async (logId: number) => {
    logInfoVisible.value = true;
    const result = await TaskLogApi.getLogInfo(logId);
    if (result.code === 200) {
        recordLogInfo.value = result.data;
    }
}

const handlerPageChange = (page: number) => {
    queryData.page = page;
    getRecordPage();
}

const handlerSizeChange = (size: number) => {
    queryData.size = size;
    queryData.page = 1;
    getRecordPage();
}

onMounted(() => {
    getRecordPage();
})
</script>

<template>
    <a-modal v-model:visible="infoModelVisible" title="运行记录详情" title-align="start">
        <a-descriptions v-if="recordInfoData" :data="recordInfoData" :column="1" />
        <div v-else class="text-center py-4">
            <a-spin tip="加载中" />
        </div>
    </a-modal>
    <a-modal v-model:visible="logInfoVisible" title="日志详情" title-align="start" width="800px" :footer="false"
             @close="recordLogInfo = undefined"
    >
        <div v-if="recordLogInfo">
            <div class="flex justify-between border-l-sky-400 border-l-4 py-1 pl-2">
                <div>
                    任务ID：{{recordLogInfo.taskId}}
                </div>
                <div class="text-gray-600">
                    运行时间：{{recordLogInfo.createTime}}
                </div>
            </div>
            <div class="radius-md bg-gray-100 p-4 text-gray-800 mt-2">
                {{recordLogInfo.content}}
            </div>
        </div>
        <div v-else class="py-4">
            <a-spin tip="日志加载中" />
        </div>
    </a-modal>
    <a-modal v-model:visible="logModelVisible" title="运行日志" title-align="start" :footer="false">
        <div v-if="recordLogsLoading" class="py-4 text-center">
            <a-spin tip="日志加载中" />
        </div>
        <div v-else>
            <div v-if="recordLogList && recordLogList.length > 0" v-for="(item, index) in recordLogList"
                 class="border-b hover:cursor-pointer hover:border-b-sky-400 transition"
                 @click="handlerLogInfo(item.id)"
            >
                {{`${index}、${item.createTime}`}}}
            </div>
            <div v-else class="py-2">
                <a-empty description="还没有日志" />
            </div>
        </div>
    </a-modal>
    <div>
        <div class="mb-8">
            <a-space>
                <a-input-number v-model="queryData.taskId" placeholder="任务ID" size="large"/>
                <a-select v-model="queryData.status" placeholder="任务状态" size="large" style="width: 120px">
                    <a-option :value="0">全部</a-option>
                    <a-option :value="2">等待中</a-option>
                    <a-option :value="3">运行中</a-option>
                    <a-option :value="1">成功</a-option>
                    <a-option :value="4">失败</a-option>
                </a-select>
                <a-button type="primary" size="large" @click="handlerSearch" :loading="searchLoading">
                    <template #default>
                        搜索
                    </template>
                    <template #icon>
                        <icon-search />
                    </template>
                </a-button>
                <a-button size="large" @click="resetQueryData">
                    <template #default>
                        清空
                    </template>
                    <template #icon>
                        <icon-delete />
                    </template>
                </a-button>
            </a-space>
        </div>
        <TableOption :show-add-button="false" @refresh="getRecordPage"/>
        <div v-if="recordPage">
            <a-table :columns="recordColumns" :data="recordPage.list" :loading="tableLoading"
                     :pagination="{total: recordPage.total, pageSize: recordPage.size, current: recordPage.page, pageSizeOptions: [10, 20, 40], defaultPageSize: 10, showPageSize: true}"
                     @page-change="handlerPageChange"
                     @page-size-change="handlerSizeChange"
            >
                <template #createTime="{ record }">
                    {{formatDateTime(record.createTime)}}
                </template>
                <template #stopTime="{ record }">
                    {{formatDateTime(record.stopTime)}}
                </template>
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
                        <a-button type="outline" shape="circle" @click="handlerRecordLogs(record.id)">
                            <template #icon>
                                <icon-clock-circle />
                            </template>
                        </a-button>
                        <a-button type="outline" status="warning" shape="circle">
                            <template #icon>
                                <icon-loop/>
                            </template>
                        </a-button>
                        <a-button type="outline" status="danger" shape="circle">
                            <template #icon>
                                <a-popconfirm content="确认删除这条运行记录吗" position="tr" :ok-button-props="{status: 'danger'}"
                                              ok-text="确认删除" type="error" @ok="handlerDelete(record.id)">
                                    <icon-delete/>
                                </a-popconfirm>
                            </template>
                        </a-button>
                    </a-space>
                </template>
            </a-table>
        </div>
        <div v-else>
            <a-empty description="还没有运行记录" />
        </div>
    </div>
</template>

<style scoped>

</style>