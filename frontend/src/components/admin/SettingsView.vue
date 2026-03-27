<template>
  <div class="view-section">
    <div class="section-header">
      <div>
        <h2 class="section-title">{{ $t('navigation.settings') }}</h2>
        <p class="section-sub">{{ $t('settings.subTitle') }}</p>
      </div>
    </div>

    <div class="settings-grid">
      <div class="settings-card">
        <div class="settings-card-header">
          <span class="settings-card-icon">🔔</span>
          <h3>{{ $t('settings.notifications.title') }}</h3>
        </div>
        <div class="setting-row" v-for="s in settings.notifications" :key="s.key">
          <div class="setting-text">
            <div class="setting-label">{{ $t(`settings.notifications.${s.key}`) }}</div>
            <div class="setting-sub">{{ $t(`settings.notifications.${s.key}Sub`) }}</div>
          </div>
          <button class="toggle" :class="{ on: s.value }" @click="s.value = !s.value">
            <span class="toggle-knob"></span>
          </button>
        </div>
      </div>

      <div class="settings-card">
        <div class="settings-card-header">
          <span class="settings-card-icon">⚙️</span>
          <h3>{{ $t('settings.system.title') }}</h3>
        </div>
        <div class="setting-row" v-for="s in settings.system" :key="s.key">
          <div class="setting-text">
            <div class="setting-label">{{ $t(`settings.system.${s.key}`) }}</div>
            <div class="setting-sub">{{ $t(`settings.system.${s.key}Sub`) }}</div>
          </div>
          <button class="toggle" :class="{ on: s.value }" @click="s.value = !s.value">
            <span class="toggle-knob"></span>
          </button>
        </div>
      </div>

      <div class="settings-card">
        <div class="settings-card-header">
          <span class="settings-card-icon">👤</span>
          <h3>{{ $t('settings.profile.title') }}</h3>
        </div>
        <div class="input-field">
          <label>{{ $t('settings.profile.displayName') }}</label>
          <input type="text" v-model="settings.profile.name" />
        </div>
        <div class="input-field">
          <label>{{ $t('auth.email') }}</label>
          <input type="email" v-model="settings.profile.email" />
        </div>
        <div class="input-field">
          <label>{{ $t('settings.profile.newPassword') }}</label>
          <input type="password" v-model="settings.profile.password" placeholder="••••••••" />
        </div>
        <button class="primary-btn">{{ $t('common.save') }}</button>
      </div>

      <div class="settings-card">
        <div class="settings-card-header">
          <span class="settings-card-icon">📋</span>
          <h3>{{ $t('settings.policy.title') }}</h3>
        </div>
        <div class="input-field">
          <label>{{ $t('settings.policy.maxDays') }}</label>
          <input type="number" v-model="settings.policy.maxDays" />
        </div>
        <div class="input-field">
          <label>{{ $t('settings.policy.maxItems') }}</label>
          <input type="number" v-model="settings.policy.maxItems" />
        </div>
        <div class="input-field">
          <label>{{ $t('settings.policy.lateFee') }}</label>
          <input type="text" v-model="settings.policy.lateFee" />
        </div>
        <button class="primary-btn">{{ $t('settings.policy.updateBtn') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  settings: Object,
})
</script>