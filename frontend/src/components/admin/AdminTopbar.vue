<template>
  <div class="topbar">
    <div class="topbar-left">
<<<<<<< HEAD
      <div class="search-bar" v-if="currentView === 'inventory'">
=======
      <div class="search-bar">
>>>>>>> origin/main
        <span class="search-icon">🔍</span>
        <input
          class="search-input"
          type="text"
          :placeholder="$t('inventory.search')"
          :value="modelValue"
          @input="$emit('update:modelValue', $event.target.value)"
        />
      </div>
    </div>
    <div class="topbar-right">
<<<<<<< HEAD
      <button class="lang-toggle-btn" @click="toggleLanguage">{{ $i18n.locale === 'bg' ? '🇬🇧 EN' : '🇧🇬 BG' }}</button>
=======
>>>>>>> origin/main
      <div class="topbar-date">{{ todayDate }}</div>
      
      <div class="admin-chip">
        <div class="admin-chip-avatar">{{ initials }}</div>
        <div class="admin-chip-info">
          <span class="admin-chip-name">{{ adminName || 'Admin' }}</span>
          <span class="admin-chip-role">{{ $t('auth.adminRole') }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
<<<<<<< HEAD
import { useI18n } from 'vue-i18n'
const { locale } = useI18n()
function toggleLanguage() {
  const newLocale = locale.value === 'bg' ? 'en' : 'bg'
  locale.value = newLocale
  localStorage.setItem('user-locale', newLocale)
}
=======
>>>>>>> origin/main

const props = defineProps({
  modelValue: String,
  todayDate: String,
  adminName: String,
<<<<<<< HEAD
  currentView: String,
=======
>>>>>>> origin/main
})

defineEmits(['update:modelValue'])

const initials = computed(() => {
  if (!props.adminName) return 'AD'
  return props.adminName.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2)
})
</script>