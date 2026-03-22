<template>
  <aside class="sidebar" :class="{ collapsed: modelValue }">
    <div class="sidebar-header">
      <span class="sidebar-logo-text">Equip<span>Pro</span></span>
      <button class="collapse-btn" @click="$emit('update:modelValue', !modelValue)">
        {{ modelValue ? '▶' : '◀' }}
      </button>
    </div>
    <nav class="sidebar-nav">
      <button
        v-for="nav in navItems"
        :key="nav.view"
        class="nav-item"
        :class="{ active: currentView === nav.view }"
        @click="$emit('navigate', nav.view)"
      >
        <span class="nav-icon">{{ nav.icon }}</span>
        <span class="nav-label">{{ nav.label }}</span>
      </button>
    </nav>
    <div class="sidebar-footer">
      <div class="user-chip">
        <div class="user-avatar">AD</div>
        <div class="user-info">
          <span class="user-name">Admin User</span>
          <span class="user-role">Administrator</span>
        </div>
      </div>
      <button class="logout-btn" @click="$emit('logout')" title="Logout">↩</button>
    </div>
  </aside>
</template>

<script setup>
defineProps({
  modelValue: Boolean,
  currentView: String,
  navItems: Array,
})

defineEmits(['update:modelValue', 'navigate', 'logout'])
</script>