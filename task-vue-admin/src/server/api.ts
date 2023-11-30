import {sendDelete, sendGet, sendPost, sendPut} from '@/server/request';
import type {OptionalTask, TaskExecuteReactive, TaskQueryData} from '@/views/Tasks.vue';
import type {RecordQueryData} from '@/views/Records.vue';

export interface Page<T> {
    page: number,
    size: number,
    total: number,
    totalPage: number,
    list: T[]
}

export interface Count {
    taskCount: number,
    failTaskCount: number,
    runnerNodeCount: number
}

export interface User {
    id: number,
    name: string,
    createTime: string,
    lastLoginTime: string,
    lastLoginIp: string,
    remark: string
}

export interface Task {
    id: number,
    name: string,
    remark: string,
    taskType: number,
    handlerType: number,
    handlerName: string,
    params: string,
    createTime: string,
    updateTime: string,
    status: number
}

export interface Record {
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

export interface TaskLog {
    id: number,
    taskId: number,
    recordId: number,
    content: string,
    createTime: string
}

export class UserApi {
    static async saveUser(name: string, password: string, remark?: string) {
        return sendPost<void>("/user", undefined, {name: name, password: password, remark: remark})
    }
    static async getUserList(page: number, size: number, name?: string) {
        return sendGet<Page<User>>("/user/list", {page: page, size: size, name: name});
    }
    static async login(name: string, password: string) {
        return sendPost<string>("/user/login", undefined, {name: name, password: password});
    }
    static async deleteUser(userId: number) {
        return sendDelete<void>(`/user/${userId}`);
    }
}

export class TaskApi {
    static async saveTask(taskInfo: OptionalTask){
        return sendPost("/task", undefined, taskInfo)
    }
    static async queryTaskInfo(taskId: number) {
        return sendGet<Task>(`/task/${taskId}`);
    }
    static async queryCountInfo() {
        return sendGet<Count>('/task/count/info')
    }
    static async updateTask(taskInfo: OptionalTask) {
        return sendPut("/task", undefined, taskInfo)
    }
    static async deleteTask(taskId: number) {
        return  sendDelete<void>(`/task/${taskId}`);
    }
    static async updateTaskStatus(taskId: number, status: number) {
        return sendPut<void>("/task/status", {id: taskId, status: status});
    }
    static async submitTaskExecute(executeParams: TaskExecuteReactive) {
        return sendPost<void>("/task/execute", undefined, executeParams)
    }

    static async getTaskList(query: TaskQueryData) {
        const condition = {
            name: query.name,
            taskType: query.taskType,
            handlerType: query.handlerType,
            startTime: '',
            endTime: ''
        }
        if (query.rangeTime.length > 1) {
            condition.startTime = query.rangeTime[0];
            condition.endTime = query.rangeTime[1];
        }
        return sendPost<Page<Task>>("/task/list", {page: query.page, size: query.size}, condition);
    }
}

export class RecordApi {
    static async getRecordList(query: RecordQueryData) {
        return sendPost<Page<Record>>("/record/list", {page: query.page, size: query.size}, {taskId: query.taskId, status: query.status});
    }
    static async queryRecordInfo(recordId: number) {
        return sendGet<Record>(`/record/${recordId}`);
    }
    static async deleteRecord(recordId: number) {
        return sendDelete<void>(`/record/${recordId}`);
    }
}

export class TaskLogApi {
    static async getLogListByRecordId(recordId: number) {
        return sendGet<TaskLog[]>(`/record/logs/list/${recordId}`)
    }
    static async getLogInfo(logId: number) {
        return sendGet<TaskLog>(`/record/logs/${logId}`)
    }
}