<script setup lang="ts">
import { IconSearch, IconDelete, IconPlayArrow, IconEdit, IconInfo, IconCheck } from '@arco-design/web-vue/es/icon';
import TableOption from '@/components/TableOption.vue';
import type {TableColumnData} from '@arco-design/web-vue';
import {onMounted, reactive, ref} from 'vue';
import type {Page, Task} from '@/server/api';
import {formatDateTime} from '@/assets/script/utils';
import {TaskApi} from '@/server/api';
import {Msg, Not} from '@/assets/script/common';
import {useTaskNodeStore} from '@/stores/task_node';

export interface Description {
    label: string,
    value: any
}

export interface TaskExecuteReactive {
    taskId: number,
    taskType: number,
    mode: number,
    nodeId: string
}

export interface TaskQueryData {
    page: number,
    size: number,
    name: string,
    handlerType?: number,
    taskType?:number,
    rangeTime: string[]
}

export interface OptionalTask {
    id?: number,
    name: string,
    remark: string,
    taskType?: number,
    handlerType?: number,
    handlerName: string,
    params: string,
}
const taskNodeStore = useTaskNodeStore();

const taskColumns: TableColumnData[] = [
    {
        title: '任务ID',
        dataIndex: 'id',
        width: 100
    },
    {
        title: '任务名称',
        dataIndex: 'name',
        width: 300
    },
    {
        title: '任务类型',
        slotName: 'task-type',
        width: 140
    },
    {
        title: '处理器类型',
        slotName: 'handler-type',
        width: 140
    },
    {
        title: '创建时间',
        slotName: 'createTime',
        width: 200,
        align: 'center'
    },
    {
        title: '状态',
        slotName: 'status',
        width: 40,
        align: 'center'
    },
    {
        title: '操作',
        slotName: 'optional',
        width: 200,
        align: 'center'
    }
]
const taskRef = ref();
const taskPage = ref<Page<Task>>()
const editTask = reactive<OptionalTask>({
    id: undefined,
    name: '',
    remark: '',
    taskType: undefined,
    handlerType: undefined,
    handlerName: '',
    params: '',
})
const submitLoading = ref<boolean>(false)
const taskModelVisible = ref<boolean>(false);
const infoModelVisible = ref<boolean>(false);
const tableLoading = ref<boolean>(false);
const searchButtonLoading = ref<boolean>(false);
const taskInfoData = ref<Description[]>();

const taskExecuteVisible = ref(false);
const taskExecuteButtonLoading = ref(false);
const taskExecuteForm = reactive<TaskExecuteReactive>({
    taskId: 0,
    taskType: 0,
    mode: 0,
    nodeId: ''
})

const queryData = reactive<TaskQueryData>({
    page: 1,
    size: 10,
    name: '',
    handlerType: undefined,
    taskType: undefined,
    rangeTime: []
})

const getTaskPage = async () => {
    tableLoading.value = true;
    const result = await TaskApi.getTaskList(queryData);
    tableLoading.value = false;
    if (result.code === 200) {
        taskPage.value = result.data;
    }
}

const taskToDescriptions = (task: Task): Description[] => {
    return [
        {
            label: '任务ID',
            value: task.id
        },
        {
            label: '任务名称',
            value: task.name
        },
        {
            label: '任务类型',
            value: task.taskType === 0 ? '单机执行' : '广播执行'
        },
        {
            label: '处理器类型',
            value: task.handlerType === 0 ? 'SpringBean' : 'Task注解'
        },
        {
            label: '处理器名称',
            value: task.handlerName
        },
        {
            label: '任务参数',
            value: task.params
        },
        {
            label: '创建时间',
            value: formatDateTime(task.createTime)
        },
        {
            label: '最后更新时间',
            value: formatDateTime(task.updateTime)
        },
        {
            label: '状态',
            value: task.status === 0 ? '正常' : '禁用'
        }
    ]
}

const handlerSubmit = async () => {
    submitLoading.value = true;
    let result;
    if (editTask.id) {
        result = await TaskApi.updateTask(editTask);
    }else {
        result = await TaskApi.saveTask(editTask);
    }
    submitLoading.value = false;
    if (result.code === 200) {
        const message = editTask.id ? "修改成功" : "添加成功";
        Not.success(message)
        taskModelVisible.value = false;
    }
}

const handlerViewInfo = async (taskId: number) => {
    infoModelVisible.value = true;
    const result = await TaskApi.queryTaskInfo(taskId);
    if (result.code === 200) {
        taskInfoData.value = taskToDescriptions(result.data)
    }
}

const handlerAddTask = () => {
    editTask.id = undefined;
    taskModelVisible.value = true;
}
const handlerEditTask = async (taskId: number) => {
    const result = await TaskApi.queryTaskInfo(taskId);
    if (result.code !== 200) {
        return;
    }
    taskModelVisible.value = true;
    Object.assign(editTask, result.data)
}

const handlerDeleteTask = async (taskId: number) => {
    const message = Msg.loading("任务删除中");
    const result = await TaskApi.deleteTask(taskId);
    if (result.code === 200) {
        message.close();
        Not.success("删除成功")
        await getTaskPage()
    }
}

const handlerExecute = async (taskId: number, taskType: number) => {
    if (taskNodeStore.taskNodeOnlineCount <= 0) {
        Msg.waring("当前没有在线的任务节点，无法运行任务！")
        return;
    }
    taskExecuteVisible.value = true;
    taskExecuteForm.taskId = taskId;
    taskExecuteForm.taskType = taskType;
}

const submitTask = async () => {
    if (!taskExecuteForm.taskId) {
        Msg.error("任务未选择，无法提交");
        return;
    }
    taskExecuteButtonLoading.value = true;
    const result = await TaskApi.submitTaskExecute(taskExecuteForm)
    taskExecuteButtonLoading.value = false;
    if (result.code === 200) {
        Not.success("提交成功")
        // 清除输入框信息
        taskExecuteVisible.value = false;
        clearTaskForm();
        taskExecuteButtonLoading.value = false;
    }
}

const clearTaskForm = () => {
    taskExecuteForm.taskId = 0;
    taskExecuteForm.mode = 0;
    taskExecuteForm.nodeId = '';
}

const handlerPageChange = (page: number) => {
    queryData.page = page;
    getTaskPage();
}

const handlerSizeChange = (size: number) => {
    queryData.size = size;
    queryData.page = 1;
    getTaskPage();
}
const handlerSearch = () => {
    searchButtonLoading.value = true;
    getTaskPage();
    searchButtonLoading.value = false;
}

const resetQueryData = () => {
    queryData.name = '';
    queryData.taskType = undefined;
    queryData.handlerType = undefined;
    queryData.rangeTime = [];
}

onMounted(() => {
    getTaskPage();
})
</script>

<template>
    <a-modal width="400px" title="执行任务" v-model:visible="taskExecuteVisible" :ok-loading="taskExecuteButtonLoading"
             @ok="submitTask"
             @close="clearTaskForm"
    >
        <div class="py-4">
            <p class="text-gray-700">当前任务节点在线数量：{{taskNodeStore.taskNodeOnlineCount}}</p>
        </div>
        <div v-if="taskExecuteForm.taskType === 0">
            <a-select placeholder="任务调度方式" v-model="taskExecuteForm.mode">
                <a-option :value="0">默认调度</a-option>
                <a-option :value="1">指定任务节点</a-option>
            </a-select>
            <div class="py-4" v-if="taskExecuteForm.mode === 1">
                <p class="text-gray-800 py-1">执行任务节点:</p>
                <a-select placeholder="请选择任务节点" v-model="taskExecuteForm.nodeId">
                    <a-option v-for="node in taskNodeStore.taskNodeOnlineList" :value="node.id">{{`${node.address}（${node.name}）`}}</a-option>
                </a-select>
            </div>
        </div>
        <div v-else>
            <p class="font-bold">
                确定执行广播任务吗？会调用所有任务节点执行，请确保任务节点中存在该任务的对应处理器！
            </p>

        </div>
    </a-modal>
    <a-modal :title="editTask.id ? '修改任务' : '添加任务'" title-align="center" v-model:visible="taskModelVisible"
             :footer="false"
             @close="taskRef.resetFields()"
    >
        <a-form ref="taskRef" :model="editTask" layout="vertical" @submit-success="handlerSubmit">
            <a-form-item field="name" label="任务名称" :rules="[{required: true, message: '任务名称不能为空'}]">
                <a-input v-model="editTask.name" placeholder="请输入任务名称"/>
            </a-form-item>
            <a-form-item field="remark" label="任务备注">
                <a-textarea v-model="editTask.remark" placeholder="任务备注"/>
            </a-form-item>
            <div class="flex">
                <a-form-item field="taskType" label="任务类型" :rules="[{required: true, message: '任务类型不能为空'}]">
                    <a-select v-model="editTask.taskType" placeholder="任务类型" size="large" style="width: 235px">
                        <a-option :value="0">单机执行</a-option>
                        <a-option :value="1">广播执行</a-option>
                    </a-select>
                </a-form-item>
                <a-form-item field="handlerType" label="执行器类型" :rules="[{required: true, message: '任务执行器类型不能为空'}]">
                    <a-select v-model="editTask.handlerType" placeholder="执行器类型" size="large" style="width: 235px">
                        <a-option :value="0">SpringBean</a-option>
                        <a-option :value="1">Task注解</a-option>
                        <a-option :value="2">自定义名称</a-option>
                    </a-select>
                </a-form-item>
            </div>
            <a-form-item field="handlerName" label="执行器名称" :rules="[{required: true, message: '任务执行器名称不能为空'}]">
                <a-input v-model="editTask.handlerName" placeholder="请输入执行器名称" />
            </a-form-item>
            <a-form-item field="params" label="任务参数">
                <a-textarea v-model="editTask.params" placeholder="任务参数" />
            </a-form-item>
            <div class="flex justify-between">
                <a-button class="w-2/5" type="primary" html-type="submit" size="large" :loading="submitLoading">
                    <template #default>
                        确定
                    </template>
                    <template #icon>
                        <icon-check />
                    </template>
                </a-button>
                <a-button class="w-2/5" type="outline" @click="taskRef.resetFields()" size="large">
                    <template #default>
                        清空
                    </template>
                    <template #icon>
                        <icon-refresh />
                    </template>
                </a-button>
            </div>
        </a-form>
    </a-modal>
    <a-modal v-model:visible="infoModelVisible" :footer="false" title="任务详情" title-align="start" width="600px">
        <a-descriptions v-if="taskInfoData" :data="taskInfoData" size="large" :column="1"/>
        <div v-else class="w-full text-center">
            <a-spin tip="加载中"/>
        </div>
    </a-modal>
    <div>
        <div class="mb-8">
            <a-space>
                <a-input v-model="queryData.name" placeholder="输入任务名称搜索" size="large"/>
                <a-select v-model="queryData.handlerType" placeholder="执行器类型" size="large" style="width: 120px">
                    <a-option :value="0">全部</a-option>
                    <a-option :value="1">Bean</a-option>
                    <a-option :value="2">注解</a-option>
                    <a-option :value="3">方法</a-option>
                </a-select>
                <a-select v-model="queryData.taskType" placeholder="任务类型" size="large" style="width: 120px">
                    <a-option :value="0">全部</a-option>
                    <a-option :value="1">单机执行</a-option>
                    <a-option :value="2">广播执行</a-option>
                </a-select>
                <a-range-picker v-model="queryData.rangeTime" style="width: 254px;" size="large"/>
                <a-button type="primary" size="large" @click="handlerSearch" :loading="searchButtonLoading">
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
        <div>
            <table-option button-text="添加任务" @add="handlerAddTask" @refresh="getTaskPage"/>
        </div>
        <div v-if="taskPage">
            <a-table :columns="taskColumns" :data="taskPage.list" :loading="tableLoading"
                     :pagination="{total: taskPage.total, pageSize: taskPage.size, current: taskPage.page, pageSizeOptions: [10, 20, 40], defaultPageSize: 10, showPageSize: true}"
                     @page-change="handlerPageChange"
                     @page-size-change="handlerSizeChange"
            >
                <template #task-type="{ record }">
                    {{record.taskType === 0 ? '单机执行' : '广播执行'}}
                </template>
                <template #handler-type="{ record }">
                    <span v-if="record.handlerType === 0">
                        SpringBean
                    </span>
                    <span v-if="record.handlerType === 1">
                        注解
                    </span>
                    <span v-if="record.handlerType === 2">
                        自定义名称
                    </span>
                </template>
                <template #createTime="{ record }">
                    {{formatDateTime(record.createTime)}}
                </template>
                <template #status="{ record }">
                    <a-switch v-model="record.status" :checked-value="0" :unchecked-value="1" checked-color="#2F80ED"
                              unchecked-color="#D9D9D9"
                              :before-change="async (value) => {
                                  if (typeof value === 'string') {
                                      value = parseInt(value);
                                  }
                                  if (typeof value === 'boolean') {
                                      value = value ? 0 : 1;
                                  }
                                  const result = await TaskApi.updateTaskStatus(record.id, value);
                                  return result.code === 200;
                              }"
                    />
                </template>
                <template #optional="{ record }">
                    <a-space>
                        <a-button status="success" type="primary" shape="circle" @click="handlerExecute(record.id, record.taskType)">
                            <template #icon>
                                <icon-play-arrow/>
                            </template>
                        </a-button>
                        <a-button type="outline" shape="circle" @click="handlerViewInfo(record.id)">
                            <template #icon>
                                <icon-info/>
                            </template>
                        </a-button>
                        <a-button type="outline" status="warning" shape="circle" @click="handlerEditTask(record.id)">
                            <template #icon>
                                <icon-edit/>
                            </template>
                        </a-button>
                        <a-button type="outline" status="danger" shape="circle">
                            <template #icon>
                                <a-popconfirm content="确认删除该任务吗" position="tr" :ok-button-props="{status: 'danger'}"
                                              ok-text="确认删除" type="error" @ok="handlerDeleteTask(record.id)">
                                    <icon-delete/>
                                </a-popconfirm>
                            </template>
                        </a-button>
                    </a-space>
                </template>
            </a-table>
        </div>
        <div v-else>
            <a-empty description="还没有任务" />
        </div>
    </div>
</template>

<style scoped>
.info-item {
    font-size: 16px;
    display: flex;
    padding: 12px 0;
    color: #333;
}
.info-item .item-label {
    width: 100px;
    font-weight: 600;
    color: #666;
}
</style>