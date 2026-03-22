<template>
    <div class="view-section">
        <div class="section-header" style="margin-bottom:24px">
          <h2>⚙️ Settings</h2>
        </div>
        <div class="settings-grid">
          <!-- Notifications -->
          <div class="settings-card">
            <h3>Notifications</h3>
            <div class="setting-row" v-for="s in settings.notifications" :key="s.key">
              <div>
                <div class="setting-label">{{ s.label }}</div>
                <div class="setting-sub">{{ s.sub }}</div>
              </div>
              <button class="toggle" :class="{ on: s.value }" @click="s.value = !s.value"></button>
            </div>
          </div>
          <!-- System -->
          <div class="settings-card">
            <h3>System</h3>
            <div class="setting-row" v-for="s in settings.system" :key="s.key">
              <div>
                <div class="setting-label">{{ s.label }}</div>
                <div class="setting-sub">{{ s.sub }}</div>
              </div>
              <button class="toggle" :class="{ on: s.value }" @click="s.value = !s.value"></button>
            </div>
          </div>
          <!-- Profile -->
          <div class="settings-card">
            <h3>Profile</h3>
            <div class="input-field">
              <label>Display Name</label>
              <input type="text" v-model="settings.profile.name" />
            </div>
            <div class="input-field">
              <label>Email</label>
              <input type="email" v-model="settings.profile.email" />
            </div>
            <div class="input-field">
              <label>New Password</label>
              <input type="password" v-model="settings.profile.password" placeholder="••••••••" />
            </div>
            <button class="primary-btn">Save Changes</button>
          </div>
          <!-- Borrow Policy -->
          <div class="settings-card">
            <h3>Borrow Policy</h3>
            <div class="input-field">
              <label>Max Borrow Days</label>
              <input type="number" v-model="settings.policy.maxDays" />
            </div>
            <div class="input-field">
              <label>Max Items Per User</label>
              <input type="number" v-model="settings.policy.maxItems" />
            </div>
            <div class="input-field">
              <label>Late Fee (per day)</label>
              <input type="text" v-model="settings.policy.lateFee" />
            </div>
            <button class="primary-btn">Update Policy</button>
          </div>
        </div>
      </div>
</template>

<script setup>
defineProps({
  settings: Object,
})
</script>