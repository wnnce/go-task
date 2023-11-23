<script setup lang="ts">
import {IconComputer, IconHome, IconMenu, IconUserGroup} from '@arco-design/web-vue/es/icon';
import Header from '@/components/Header.vue';
import {computed, onMounted, onUnmounted} from 'vue';
import {useRoute} from 'vue-router';
import router from '@/router';
import {Msg} from '@/assets/script/common';
import type {NodeInfo, TaskNode} from '@/stores/task_node';
import {useTaskNodeStore} from '@/stores/task_node';

interface Menu {
    name: string,
    path: string,
    icon: string
}

interface Message {
    /**
     * 消息代码 0：自定义消息 1：新增任务节点消息 2:更新任务节点消息 3：删除任务节点消息
     */
    code: number,
    data: any
}

interface TaskNodeMessage {
    id: string,
    status?: number,
    onlineTime?: string,
    info?: NodeInfo
}

const route = useRoute();
const taskNodeStore = useTaskNodeStore();
const menuList: Menu[] =  [
    {
        name: '系统首页',
        path: '/home',
        icon: '<icon-home />'
    },
    {
        name: '用户管理',
        path: '/users',
        icon: '<icon-user-group />'
    },
    {
        name: '任务管理',
        path: '/tasks',
        icon: '<icon-menu />'
    },
    {
        name: '执行记录',
        path: '/records',
        icon: '<icon-computer />'
    }
]
const currentPath = computed(() => {
    return route.path;
})

const handleMenuClick = (path: string) => {
    router.push(path);
}

const socket = new WebSocket("ws://localhost:5400/client/websocket");
socket.onopen = () => {
    console.log('连接成功')
}

socket.onmessage = event => {
    const message: Message = JSON.parse(event.data)
    if (message.code === 0) {
        Msg.info(message.data)
    } else if (message.code === 1) {
        const taskNodeList: TaskNode[] = message.data
        taskNodeList.forEach(item => taskNodeStore.addTaskNode(item))
    } else if (message.code === 2) {
        const nodeMessage: TaskNodeMessage = message.data;
        if (nodeMessage.status) {
            taskNodeStore.updateTaskNodeStatus(nodeMessage.id, nodeMessage.status);
        } else if (nodeMessage.info && nodeMessage.onlineTime) {
            taskNodeStore.updateTaskNodeInfo(nodeMessage.id, nodeMessage.onlineTime, nodeMessage.info)
        }
    } else if (message.code === 3) {
        const nodeId = message.data;
        taskNodeStore.deleteTaskNode(nodeId)
    } else {
        console.log('return');
    }
}

socket.onerror = event => {
    console.error(event);
    Msg.error("WebSocket连接失败")
}

onUnmounted(() => {
    socket.close();
})
</script>

<template>
    <div class="absolute left-0 right-0 top-0 bottom-0 bg-gray-50">
        <Header />
        <div class="flex min-w-full min-h-full pt-14">
            <div class="min-h-full bg-white pt-5 global-shadow sticky top-0">
                <a-menu
                    :style="{ width: '280px', borderRadius: '4px' }"
                    :selected-keys="[currentPath]"
                    @menu-item-click="handleMenuClick"
                >
                    <a-menu-item :key="menu.path" v-for="menu in menuList">
                        <template #default>
                            {{menu.name}}
                        </template>
                        <template #icon>
                            <icon-home v-if="menu.path === '/home'" />
                            <icon-user-group v-if="menu.path === '/users'" />
                            <icon-menu v-if="menu.path === '/tasks'" />
                            <icon-computer v-if="menu.path === '/records'" />
                        </template>
                    </a-menu-item>
                </a-menu>
            </div>
            <div class="p-6 w-full">
                <div class="p-4 bg-white w-full min-h-full">
                    <router-view />
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>