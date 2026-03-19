<template>
  <div class="login-fon">
    <div class="login-center-card">

      <!-- Logo -->
      <div class="card-header">
        <img src="@/assets/logo-png.jpg" alt="INVTR Logo" class="logo-image">
        <p class="subtitle"><span>School Inventory System</span></p>
      </div>

      <!-- Tab switcher -->
      <div class="tab-switcher">
        <button class="tab-btn" :class="{ active: activeTab === 'login' }" @click="switchTab('login')">Log In</button>
        <button class="tab-btn" :class="{ active: activeTab === 'register' }" @click="switchTab('register')">Register</button>
      </div>

      <!-- ── LOGIN FORM ── -->
      <template v-if="activeTab === 'login'">
        <div v-if="errorMsg"   class="alert alert-error">{{ errorMsg }}</div>
        <div v-if="successMsg" class="alert alert-success">{{ successMsg }}</div>

        <div class="input-field">
          <label>Email Address</label>
          <input type="email" v-model="email" placeholder="you@example.com" :disabled="loading" />
        </div>
        <div class="input-field">
          <label>Password</label>
          <input type="password" v-model="password" placeholder="••••••••" :disabled="loading" />
        </div>
        <div class="options">
          <label><input type="checkbox" v-model="rememberMe"> Remember Me</label>
          <a href="#" @click.prevent="handleForgot">Forgotten Password</a>
        </div>

        <button class="login-button" @click="handleLogin" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>Log In</span>
        </button>

        <p class="bottom-link">
          Don't have an account? <a href="#" @click.prevent="switchTab('register')">Register →</a>
        </p>
      </template>

      <!-- ── REGISTER FORM ── -->
      <template v-if="activeTab === 'register'">
        <div v-if="regError"   class="alert alert-error">{{ regError }}</div>
        <div v-if="regSuccess" class="alert alert-success">{{ regSuccess }}</div>

        <div class="name-row">
          <div class="input-field">
            <label>First Name</label>
            <input type="text" v-model="regFirstName" placeholder="First Name" :disabled="regLoading" />
          </div>
          <div class="input-field">
            <label>Last Name</label>
            <input type="text" v-model="regFamilyName" placeholder="Last Name" :disabled="regLoading" />
          </div>
        </div>
        <div class="input-field">
          <label>Email Address</label>
          <input type="email" v-model="regEmail" placeholder="you@example.com" :disabled="regLoading" />
        </div>
        <div class="input-field">
          <label>Password</label>
          <input type="password" v-model="regPassword" placeholder="••••••••" :disabled="regLoading" />
        </div>
        <div class="input-field">
          <label>Confirm Password</label>
          <input type="password" v-model="regConfirmPassword" placeholder="••••••••" :disabled="regLoading" />
        </div>

        <button class="login-button" @click="handleFinalRegister" :disabled="regLoading">
          <span v-if="regLoading" class="spinner"></span>
          <span v-else>Create Account</span>
        </button>

        <p class="bottom-link">
          Already have an account? <a href="#" @click.prevent="switchTab('login')">Log in →</a>
        </p>
      </template>
    </div>

    <!-- Forgot Password Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-box">
        <div class="success-icon">✓</div>
        <h3 class="modal-title">Email Sent!</h3>
        <p style="color:#7a8a9a;font-size:14px;line-height:1.5">
          Password reset instructions have been sent to<br>
          <strong style="color:#1a2d3e">{{ email }}</strong>
        </p>
        <button @click="showModal = false" class="close-modal-button">OK, Got it</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const API_BASE = ''
const router = useRouter()

// Tab
const activeTab = ref('login')
const switchTab = (tab) => {
  activeTab.value = tab
  errorMsg.value = successMsg.value = regError.value = regSuccess.value = ''
}

// Login
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
  if (!email.value || !password.value) { errorMsg.value = 'Please fill in all fields.'; return }
  loading.value = true
  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value, password: password.value })
    })
    if (!res.ok) {
      let msg = 'Invalid email or password.'
      try { const d = await res.json(); msg = d.message || d.error || msg } catch (_) {}
      errorMsg.value = msg; return
    }
    const data = await res.json()
    const token = data.token
    if (rememberMe.value) localStorage.setItem('invtr_token', token)
    else sessionStorage.setItem('invtr_token', token)
    successMsg.value = 'Login successful! Redirecting...'
    setTimeout(() => router.push('/dashboard'), 800)
  } catch (_) {
    errorMsg.value = 'Could not reach the server. Is the backend running?'
  } finally {
    loading.value = false
  }
}

const handleForgot = () => {
  if (!email.value) { errorMsg.value = 'Enter your email first, then click Forgotten Password.'; return }
  showModal.value = true
}

// Register
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
    regError.value = 'Please fill in all fields.'; return
  }
  if (regPassword.value !== regConfirmPassword.value) {
    regError.value = 'Passwords do not match.'; return
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
      let msg = 'Registration failed. Please try again.'
      try { const d = await res.json(); msg = d.message || d.error || msg } catch (_) {}
      regError.value = msg; return
    }
    regSuccess.value = 'Account created! You can now log in.'
    setTimeout(() => { email.value = regEmail.value; switchTab('login') }, 2000)
  } catch (_) {
    regError.value = 'Could not reach the server. Is the backend running?'
  } finally {
    regLoading.value = false
  }
}
</script>

<style>
@import '@/assets/login.css';
</style>