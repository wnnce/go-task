<script setup lang="ts">
import { IconSearch, IconDelete, IconPlayArrow, IconEdit, IconInfo } from '@arco-design/web-vue/es/icon';
import TableOption from '@/components/TableOption.vue';
import type {TableColumnData} from '@arco-design/web-vue';
import {reactive, ref} from 'vue';

interface Task {
    id?: number,
    name: string,
    remark?: string
    taskType: 0 | 1 | undefined,
    handlerType: 0 | 1 | undefined,
    handlerName: string,
    params?: string,
    createTime?: string,
    updateTime?: string,
    status?: 0 | 1
}

export interface Description {
    label: string,
    value: any
}

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
        dataIndex: 'createTime',
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
const taskData: Task[] = [
    {
        id: 1,
        name: '测试任务',
        remark: '备注，备注，备注',
        taskType: 0,
        handlerType: 0,
        handlerName: 'demoExecute',
        params: '{"name": "hello"}',
        createTime: '2011-11-11 10:10:10',
        updateTime: '2011-11-11 11:11:11',
        status: 0
    }
]
const editTask = reactive<Task>({
    id: undefined,
    name: '',
    remark: '',
    taskType: undefined,
    handlerType: undefined,
    params: '',
    handlerName: ''
})
const taskRef = ref();
const submitLoading = ref<boolean>(false)
const taskModelVisible = ref<boolean>(false);
const infoModelVisible = ref<boolean>(false);
const taskInfoData = ref();

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
            value: task.createTime
        },
        {
            label: '最后更新时间',
            value: task.updateTime
        },
        {
            label: '状态',
            value: task.status === 0 ? '正常' : '禁用'
        }
    ]
}

const handlerSubmit = () => {
    submitLoading.value = true;
    setTimeout(() => {
        console.log(editTask);
        submitLoading.value = false;
        taskModelVisible.value = false;
        taskRef.value.resetFields();
    }, 500)
}

const handlerViewInfo = (taskId: number) => {
    console.log(taskId);
    taskInfoData.value = taskToDescriptions(taskData[0]);
    infoModelVisible.value = true;
}

</script>

<template>
    <a-modal title="添加任务" title-align="center" v-model:visible="taskModelVisible" :footer="false">
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
                        <a-option value="0">单机执行</a-option>
                        <a-option value="1">广播执行</a-option>
                    </a-select>
                </a-form-item>
                <a-form-item field="handlerType" label="执行器类型" :rules="[{required: true, message: '任务执行器类型不能为空'}]">
                    <a-select v-model="editTask.handlerType" placeholder="执行器类型" size="large" style="width: 235px">
                        <a-option value="0">SpringBean</a-option>
                        <a-option value="1">Task注解</a-option>
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
                <a-button class="w-2/5" type="primary" html-type="submit" size="large" :loading="submitLoading">确定</a-button>
                <a-button class="w-2/5" type="outline" @click="taskRef.resetFields()" size="large">清空</a-button>
            </div>
        </a-form>
    </a-modal>
    <a-modal v-model:visible="infoModelVisible" :footer="false" title="任务详情" title-align="start" width="600px">
        <a-descriptions :data="taskInfoData" size="large" :column="1"/>
    </a-modal>
    <div>
        <div class="mb-8">
            <a-space>
                <a-input placeholder="输入任务名称搜索" size="large"/>
                <a-select placeholder="执行器类型" size="large" style="width: 120px">
                    <a-option value="0">Bean</a-option>
                    <a-option value="1">注解</a-option>
                </a-select>
                <a-select placeholder="任务类型" size="large" style="width: 120px">
                    <a-option value="0">单机执行</a-option>
                    <a-option value="1">广播执行</a-option>
                </a-select>
                <a-range-picker style="width: 254px;" size="large"/>
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
            <table-option button-text="添加任务" @add="taskModelVisible = true"/>
        </div>
        <div>
            <a-table :columns="taskColumns" :data="taskData">
                <template #task-type="{ record }">
                    {{record.taskType === 0 ? '单机执行' : '广播执行'}}
                </template>
                <template #handler-type="{ record }">
                    {{record.handlerType === 0 ? 'SpringBean' : '注解'}}
                </template>
                <template #status="{ record }">
                    <a-switch :model-value="record.status === 0" />
                </template>
                <template #optional="{ record }">
                    <a-space>
                        <a-button status="success" type="primary" shape="circle">
                            <template #icon>
                                <icon-play-arrow/>
                            </template>
                        </a-button>
                        <a-button type="outline" shape="circle" @click="handlerViewInfo(record.id)">
                            <template #icon>
                                <icon-info/>
                            </template>
                        </a-button>
                        <a-button type="outline" status="warning" shape="circle">
                            <template #icon>
                                <icon-edit/>
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