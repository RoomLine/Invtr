<template>
  <div class="topbar">
    <div class="topbar-left">
      <div class="search-bar">
        <span class="search-icon">🔍</span>
        <input
          class="search-input"
          type="text"
          placeholder="Search equipment..."
          :value="modelValue"
          @input="$emit('update:modelValue', $event.target.value)"
        />
      </div>
    </div>
    <div class="topbar-right">
      <div class="topbar-date">{{ todayDate }}</div>
      <div class="admin-chip">
        <div class="admin-chip-avatar">{{ initials }}</div>
        <div class="admin-chip-info">
          <span class="admin-chip-name">{{ adminName || 'Admin' }}</span>
          <span class="admin-chip-role">Administrator</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: String,
  todayDate: String,
  adminName: String,
})

defineEmits(['update:modelValue'])

const initials = computed(() => {
  if (!props.adminName) return 'AD'
  return props.adminName.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2)
})
</script>
