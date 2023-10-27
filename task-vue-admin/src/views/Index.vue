<script setup lang="ts">
import {IconComputer, IconHome, IconMenu, IconUserGroup} from '@arco-design/web-vue/es/icon';
import Header from '@/components/Header.vue';
import {computed} from 'vue';
import {useRoute} from 'vue-router';
import router from '@/router';

interface Menu {
    name: string,
    path: string,
    icon: string
}

const route = useRoute();
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