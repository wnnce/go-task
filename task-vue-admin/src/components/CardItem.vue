<script setup lang="ts">
interface CardProps {
    title: string,
    value: string | number,
    color?: string,
    textColor?: string,
}

const props = withDefaults(defineProps<CardProps>(), {
    color: 'white',
    textColor: '#333'
})

</script>

<template>
    <div class="item bg-white border radius-md p-4" :style="{backgroundColor: props.color, color: props.textColor}">
        <div class="text-xl font-bold text-left">
            {{props.title}}
        </div>
        <div class="text-2xl text-center py-4 font-bold">
            {{props.value}}
        </div>
    </div>
</template>

<style scoped>
.item:hover {
    box-shadow: 0 0 24px #E8EBEF;
}
.item {
    transition: 300ms;
}
</style>