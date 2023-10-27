<script setup lang="ts">

import {IconPlus, IconRefresh} from '@arco-design/web-vue/es/icon';

interface TableOptionProps {
    buttonText?: string
}
const emits = defineEmits(['add'])
const props = withDefaults(defineProps<TableOptionProps>(), {
    buttonText: '新增'
})

</script>

<template>
    <div class="flex justify-between my-4 items-center px-1">
        <div>
            <a-button type="primary" @click="emits('add')">
                <template #default>
                    {{props.buttonText}}
                </template>
                <template #icon>
                    <icon-plus />
                </template>
            </a-button>
        </div>
        <div>
            <a-button type="outline" shape="circle" status="success">
                <icon-refresh />
            </a-button>
        </div>
    </div>
</template>

<style scoped>

</style>