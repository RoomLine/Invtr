<template>
  <div class="login-fon">
    <div class="lang-wrapper">
      <button class="lang-switch-btn" @click="$i18n.locale = $i18n.locale === 'bg' ? 'en' : 'bg'">
        {{ $i18n.locale === 'bg' ? 'EN' : 'BG' }}
      </button>
    </div>

    <div class="login-center-card">
      <div class="card-header">
        <img src="@/assets/logo-full.jpg" alt="INVTR Logo" class="logo-image">
        <p class="subtitle"><span>{{ $t('auth.systemName') }}</span></p>
      </div>

      <div class="tab-switcher">
        <button class="tab-btn" :class="{ active: activeTab === 'login' }" @click="switchTab('login')">
          {{ $t('auth.login') }}
        </button>
        <button class="tab-btn" :class="{ active: activeTab === 'register' }" @click="switchTab('register')">
          {{ $t('auth.register') }}
        </button>
      </div>

      <!-- ── LOGIN TAB ── -->
      <template v-if="activeTab === 'login'">
        <div class="input-field">
          <label>{{ $t('auth.email') }}</label>
          <input
            type="email"
            v-model="email"
            placeholder="you@example.com"
            :disabled="loading"
            @keyup.enter="handleLogin"
          />
        </div>
        <div class="input-field">
          <label>{{ $t('auth.password') }}</label>
          <input
            type="password"
            v-model="password"
            placeholder="••••••••"
            :disabled="loading"
            @keyup.enter="handleLogin"
          />
        </div>
        <button class="login-button" @click="handleLogin" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>{{ $t('auth.login') }}</span>
        </button>

        <p class="bottom-link">
          {{ $t('auth.noAccount') }} <a href="#" @click.prevent="switchTab('register')">{{ $t('auth.registerNow') }} →</a>
        </p>
      </template>

      <!-- ── REGISTER TAB ── -->
      <template v-if="activeTab === 'register'">
        <div class="name-row">
          <div class="input-field">
            <label>{{ $t('auth.firstName') }}</label>
            <input
              type="text"
              v-model="regFirstName"
              :placeholder="$t('auth.firstName')"
              :disabled="regLoading"
              @keyup.enter="handleFinalRegister"
            />
          </div>
          <div class="input-field">
            <label>{{ $t('auth.lastName') }}</label>
            <input
              type="text"
              v-model="regFamilyName"
              :placeholder="$t('auth.lastName')"
              :disabled="regLoading"
              @keyup.enter="handleFinalRegister"
            />
          </div>
        </div>
        <div class="input-field">
          <label>{{ $t('auth.email') }}</label>
          <input
            type="email"
            v-model="regEmail"
            placeholder="you@example.com"
            :disabled="regLoading"
            @keyup.enter="handleFinalRegister"
          />
        </div>
        <div class="input-field">
          <label>{{ $t('auth.password') }}</label>
          <input
            type="password"
            v-model="regPassword"
            placeholder="••••••••"
            :disabled="regLoading"
            @keyup.enter="handleFinalRegister"
          />
        </div>
        <div class="input-field">
          <label>{{ $t('auth.confirmPassword') }}</label>
          <input
            type="password"
            v-model="regConfirmPassword"
            placeholder="••••••••"
            :disabled="regLoading"
            @keyup.enter="handleFinalRegister"
          />
        </div>

        <button class="login-button" @click="handleFinalRegister" :disabled="regLoading">
          <span v-if="regLoading" class="spinner"></span>
          <span v-else>{{ $t('auth.createAccount') }}</span>
        </button>

        <p class="bottom-link">
          {{ $t('auth.haveAccount') }} <a href="#" @click.prevent="switchTab('login')">{{ $t('auth.loginNow') }} →</a>
        </p>
      </template>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { toast } from '@/services/toast'

const { t } = useI18n()
const API_BASE = ''
const router = useRouter()

// Login page is always light-themed — remove dark class if previously set
onMounted(() => { document.documentElement.classList.remove('dark') })

// Tabs
const activeTab = ref('login')
const switchTab = (tab) => {
  activeTab.value = tab
}

// ── Page title (reactive to tab + locale) ──
const loginPageTitle = computed(() => `${t('auth.' + activeTab.value)} · INVTR`)
watch(loginPageTitle, title => { document.title = title }, { immediate: true })

// Login
const email    = ref('')
const password = ref('')
const loading  = ref(false)

const handleLogin = async () => {
  if (!email.value || !password.value) {
    toast.error(t('auth.errors.fillAll'))
    return
  }
  loading.value = true
  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value, password: password.value })
    })
    if (!res.ok) {
      toast.error(t('auth.errors.invalid'))
      return
    }
    const data = await res.json()
    const token = data.token
    localStorage.setItem('invtr_token', token)

    toast.success(t('auth.success.login'))
    setTimeout(() => router.push('/dashboard'), 900)
  } catch (_) {
    toast.error(t('auth.errors.server'))
  } finally {
    loading.value = false
  }
}

// Register
const regFirstName       = ref('')
const regFamilyName      = ref('')
const regEmail           = ref('')
const regPassword        = ref('')
const regConfirmPassword = ref('')
const regLoading         = ref(false)

const handleFinalRegister = async () => {
  if (!regFirstName.value || !regFamilyName.value || !regEmail.value || !regPassword.value || !regConfirmPassword.value) {
    toast.error(t('auth.errors.fillAll'))
    return
  }
  if (regPassword.value !== regConfirmPassword.value) {
    toast.error(t('auth.errors.noMatch'))
    return
  }
  regLoading.value = true
  try {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: regEmail.value, password: regPassword.value,
        firstName: regFirstName.value, familyName: regFamilyName.value
      })
    })
    if (!res.ok) {
      toast.error(t('auth.errors.regFailed'))
      return
    }
    toast.success(t('auth.success.register'))
    setTimeout(() => { email.value = regEmail.value; switchTab('login') }, 1800)
  } catch (_) {
    toast.error(t('auth.errors.server'))
  } finally {
    regLoading.value = false
  }
}
</script>

<style>
@import '@/assets/login.css';
</style>
