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

      <template v-if="activeTab === 'login'">
        <div v-if="errorMsg"   class="alert alert-error">{{ errorMsg }}</div>
        <div v-if="successMsg" class="alert alert-success">{{ successMsg }}</div>

        <div class="input-field">
          <label>{{ $t('auth.email') }}</label>
          <input type="email" v-model="email" placeholder="you@example.com" :disabled="loading" />
        </div>
        <div class="input-field">
          <label>{{ $t('auth.password') }}</label>
          <input type="password" v-model="password" placeholder="••••••••" :disabled="loading" />
        </div>
        <div class="options">
          <label><input type="checkbox" v-model="rememberMe"> {{ $t('auth.rememberMe') }}</label>
          <a href="#" @click.prevent="handleForgot">{{ $t('auth.forgotPassword') }}</a>
        </div>

        <button class="login-button" @click="handleLogin" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>{{ $t('auth.login') }}</span>
        </button>

        <p class="bottom-link">
          {{ $t('auth.noAccount') }} <a href="#" @click.prevent="switchTab('register')">{{ $t('auth.registerNow') }} →</a>
        </p>
      </template>

      <template v-if="activeTab === 'register'">
        <div v-if="regError"   class="alert alert-error">{{ regError }}</div>
        <div v-if="regSuccess" class="alert alert-success">{{ regSuccess }}</div>

        <div class="name-row">
          <div class="input-field">
            <label>{{ $t('auth.firstName') }}</label>
            <input type="text" v-model="regFirstName" :placeholder="$t('auth.firstName')" :disabled="regLoading" />
          </div>
          <div class="input-field">
            <label>{{ $t('auth.lastName') }}</label>
            <input type="text" v-model="regFamilyName" :placeholder="$t('auth.lastName')" :disabled="regLoading" />
          </div>
        </div>
        <div class="input-field">
          <label>{{ $t('auth.email') }}</label>
          <input type="email" v-model="regEmail" placeholder="you@example.com" :disabled="regLoading" />
        </div>
        <div class="input-field">
          <label>{{ $t('auth.password') }}</label>
          <input type="password" v-model="regPassword" placeholder="••••••••" :disabled="regLoading" />
        </div>
        <div class="input-field">
          <label>{{ $t('auth.confirmPassword') }}</label>
          <input type="password" v-model="regConfirmPassword" placeholder="••••••••" :disabled="regLoading" />
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

    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-box">
        <div class="success-icon">✓</div>
        <h3 class="modal-title">{{ $t('auth.modal.title') }}</h3>
        <p style="color:#7a8a9a;font-size:14px;line-height:1.5">
          {{ $t('auth.modal.sub') }}<br>
          <strong style="color:#1a2d3e">{{ email }}</strong>
        </p>
        <button @click="showModal = false" class="close-modal-button">{{ $t('auth.modal.btn') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const API_BASE = ''
const router = useRouter()

// Табове и грешки
const activeTab = ref('login')
const switchTab = (tab) => {
  activeTab.value = tab
  errorMsg.value = successMsg.value = regError.value = regSuccess.value = ''
}

// Променливи за вход
const email      = ref('')
const password   = ref('')
const rememberMe = ref(false)
const loading    = ref(false)
const errorMsg   = ref('')
const successMsg = ref('')
const showModal  = ref(false)

const handleLogin = async () => {
  errorMsg.value = ''
  successMsg.value = ''
  if (!email.value || !password.value) { errorMsg.value = t('auth.errors.fillAll'); return }
  loading.value = true
  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value, password: password.value })
    })
    if (!res.ok) {
      errorMsg.value = t('auth.errors.invalid'); return
    }
    const data = await res.json()
    const token = data.token
    if (rememberMe.value) localStorage.setItem('invtr_token', token)
    else sessionStorage.setItem('invtr_token', token)
    
    successMsg.value = t('auth.success.login')
    setTimeout(() => router.push('/dashboard'), 800)
  } catch (_) {
    errorMsg.value = t('auth.errors.server')
  } finally {
    loading.value = false
  }
}

const handleForgot = () => {
  if (!email.value) { errorMsg.value = t('auth.errors.enterEmail'); return }
  showModal.value = true
}

// Регистрация
const regFirstName       = ref('')
const regFamilyName      = ref('')
const regEmail           = ref('')
const regPassword        = ref('')
const regConfirmPassword = ref('')
const regLoading         = ref(false)
const regError           = ref('')
const regSuccess         = ref('')

const handleFinalRegister = async () => {
  regError.value = ''
  regSuccess.value = ''
  if (!regFirstName.value || !regFamilyName.value || !regEmail.value || !regPassword.value || !regConfirmPassword.value) {
    regError.value = t('auth.errors.fillAll'); return
  }
  if (regPassword.value !== regConfirmPassword.value) {
    regError.value = t('auth.errors.noMatch'); return
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
      regError.value = t('auth.errors.regFailed'); return
    }
    regSuccess.value = t('auth.success.register')
    setTimeout(() => { email.value = regEmail.value; switchTab('login') }, 2000)
  } catch (_) {
    regError.value = t('auth.errors.server')
  } finally {
    regLoading.value = false
  }
}
</script>

<style>
@import '@/assets/login.css';
</style> 