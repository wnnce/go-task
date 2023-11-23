import {defineStore} from 'pinia';
import {computed, reactive, ref} from 'vue';

export interface NodeInfo {
    usedCpu: number,
    totalMemory: number,
    usedMemory: number,
    totalDisk: number,
    usedDisk: number,
    ipAddress: string
}

export interface TaskNode {
    id: string,
    name: string,
    address: string,
    intervals: number,
    onlineTime: string,
    status: number,
    info: NodeInfo
}

interface TaskNodeReactive {
    taskNodeCache: Map<string, TaskNode>
}

export const useTaskNodeStore = defineStore('taskNode', () => {
    const taskNodeCache = ref<Map<string, TaskNode>>(new Map())

    const taskNodeList = computed((): TaskNode[] => {
        return  Array.from(taskNodeCache.value.values());
    })

    function addTaskNode(node: TaskNode) {
        taskNodeCache.value.set(node.id, node)
    }

    function updateTaskNodeInfo(nodeId: string, onlineTime: string, info: NodeInfo) {
        const taskNode = taskNodeCache.value.get(nodeId);
        if (!taskNode) {
            return;
        }
        taskNode.onlineTime = onlineTime;
        taskNode.info = info;
    }

    function updateTaskNodeStatus(nodeId: string, status: number) {
        const taskNode = taskNodeCache.value.get(nodeId);
        if (!taskNode) {
            return;
        }
        taskNode.status = status;
    }

    function deleteTaskNode(nodeId: string) {
        taskNodeCache.value.delete(nodeId);
    }

    return {taskNodeList, addTaskNode, updateTaskNodeInfo, updateTaskNodeStatus, deleteTaskNode}
})