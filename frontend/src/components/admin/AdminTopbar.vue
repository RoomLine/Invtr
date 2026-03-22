<template>
  <div class="topbar">
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
    <div class="topbar-right">
      <span class="topbar-date">{{ todayDate }}</span>
      <div class="welcome-chip">
        <span class="welcome-text">Welcome, <strong>Admin</strong></span>
        <div class="admin-avatar">AD</div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  modelValue: String,
  todayDate: String,
})

defineEmits(['update:modelValue'])
</script>