<template>
  <aside class="sidebar" :class="{ collapsed: modelValue }">
    <div class="sidebar-header">
      <img src="@/assets/logo2.png" alt="INVTR" class="sidebar-logo" />
      <button class="collapse-btn" @click="$emit('update:modelValue', !modelValue)">
        <span>{{ modelValue ? '→' : '←' }}</span>
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
        <span class="nav-label">{{ $t('navigation.' + nav.view) }}</span>
      </button>
    </nav>
    <div class="sidebar-footer">
      <div class="user-chip">
        <div class="user-avatar">{{ initials }}</div>
        <div class="user-info">
          <span class="user-name">{{ adminName || 'Admin' }}</span>
          <span class="user-role">{{ $t('auth.adminRole')  }}</span>
        </div>
      </div>
<<<<<<< HEAD
      <button v-show="!modelValue" class="theme-toggle-btn" @click="$emit('toggleTheme')" :title="isDark ? $t('common.lightMode') : $t('common.darkMode')">
        {{ isDark ? '☀️' : '🌙' }}
      </button>
      <button v-show="!modelValue" class="logout-btn" @click="$emit('logout')" :title="$t('auth.logout')">⏻</button>
    </div>
=======
      <button class="theme-toggle-btn" @click="$emit('toggleTheme')" :title="isDark ? $t('common.lightMode') : $t('common.darkMode')">
        {{ isDark ? '☀️' : '🌙' }}
      </button>
      <button class="logout-btn" @click="$emit('logout')" :title="$t('auth.logout')">⏻</button>
    </div>
    <button class="lang-toggle-btn" @click="toggleLanguage">{{ $i18n.locale === 'bg' ? '🇬🇧 EN' : '🇧🇬 BG' }}</button>
>>>>>>> origin/main
  </aside>
</template>

<script setup>
import { computed } from 'vue'
<<<<<<< HEAD
=======
import { useI18n} from 'vue-i18n'
const { locale } = useI18n()
>>>>>>> origin/main

const props = defineProps({
  modelValue: Boolean,
  currentView: String,
  navItems: Array,
  adminName: String,
  isDark: Boolean,
})

defineEmits(['update:modelValue', 'navigate', 'logout', 'toggleTheme'])

const initials = computed(() => {
  if (!props.adminName) return 'AD'
  return props.adminName.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2)
})
<<<<<<< HEAD
=======
const toggleLanguage = () => {
  const newLocale = locale.value === 'bg' ? 'en' : 'bg';
  locale.value = newLocale;
  localStorage.setItem('user-locale', newLocale); 
}
>>>>>>> origin/main
</script>
