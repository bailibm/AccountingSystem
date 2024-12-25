<template>
  <div id="app">
    <main-layout>
      <router-view></router-view>
    </main-layout>
  </div>
</template>

<script setup>
import MainLayout from '@/layouts/MainLayout.vue'
</script>

<style>
#app {
  height: 100vh;
}
</style>
