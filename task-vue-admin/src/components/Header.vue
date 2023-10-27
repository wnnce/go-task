<script setup lang="ts">

import {ref} from 'vue';

const username = ref("admin");
</script>

<template>
    <div class="absolute w-full top-0 py-1 px-8 bg-white global-shadow z-40">
        <div class="header-logo flex items-center">
            <div>
                <img src="/images/logo.png" alt="logo" />
            </div>
            <div class="text-2xl ml-4">
                Go-Task
            </div>
        </div>
    </div>
</template>

<style scoped>
.header-logo {
    height: 50px;
}
.header-logo img {
    height: 50px;
    width: auto;
}
</style>