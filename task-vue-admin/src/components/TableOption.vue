<script setup lang="ts">

import {IconPlus, IconRefresh} from '@arco-design/web-vue/es/icon';

interface TableOptionProps {
    showAddButton?: boolean
    buttonText?: string,
}
const emits = defineEmits(['add', 'refresh'])
const props = withDefaults(defineProps<TableOptionProps>(), {
    showAddButton: true,
    buttonText: '新增'
})

</script>

<template>
    <div class="flex justify-between my-4 items-center px-1">
        <div>
            <a-button v-if="showAddButton" type="primary" @click="emits('add')">
                <template #default>
                    {{props.buttonText}}
                </template>
                <template #icon>
                    <icon-plus />
                </template>
            </a-button>
        </div>
        <div>
            <a-button type="outline" shape="circle" status="success" @click="emits('refresh')">
                <icon-refresh />
            </a-button>
        </div>
    </div>
</template>

<style scoped>

</style>