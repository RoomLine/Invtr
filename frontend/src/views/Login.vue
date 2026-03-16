<template>
<div class="login-fon">
<div class="login-center-card">
<div class="card-header">
<h1><img src="@/assets/logo-png.jpg" alt="INVTR Logo" class="logo-image"></h1>
<p class="subtitle"><span>School Management</span></p>
</div>
<div class="input-field"><input type ="email" v-model="email" placeholder="Email Address" required></div>
<div class="input-field"><input type ="password" v-model="password" placeholder="Password" required></div>
<div class="options"><label><input type="checkbox" v-model="rememberMe">Remember Me</label>
<div><a href="#" @click.prevent="handleForgot">Forgotten Password</a></div>
</div>
<button class="login-button" @click="handleLogin">Log In</button>
<button class="register-button" @click="showRegisterModal = true">Register</button>
</div>
<div v-if="showModal" class="modal-overlay">
<div class="modal-box">
<div class="success-icon">✓</div>
<h3>Sent!</h3>
<p>Instruction for new password have been sent to:<br><strong>{{ email }}</strong></p>
<button @click="showModal = false" class="close-modal-button">OK</button>
</div>
</div>
<div  v-if="showRegisterModal" class="modal-overlay">
<div class="modal-box register-box">
<div class="input-field"><input type="text" v-model="regFirstName" placeholder="First Name" required></div>
<div class="input-field"><input type="text" v-model="regLastName" placeholder="Last Name" required></div>
<div class="input-field"><input type="email" v-model="regEmail" placeholder="Email Address" required></div>
<div class="input-field"><input type="password" v-model="regPassword" placeholder="Password" required></div>
<button class="login-button" @click="handleFinalRegister">Create Account</button>
<button class="close-modal-btn" @click="showRegisterModal = false" style="background-color: #95a5a6; margin-top: 10px;">Cancel</button>
</div>
</div>
</div>
</template>


<script setup>
import { ref } from 'vue'
const email = ref('')
const password = ref('')
const rememberMe = ref(false)
const showModal = ref(false) 
const showRegisterModal = ref(false)
const handleLogin = () => {
if (email.value === 'admin@school.com') {
alert('Log In for Admin! Remember me: ' +rememberMe.value)
} else {
alert('Log In for User: ' + email.value)
}
}
const handleForgot = () => {
  showModal.value = true
}
const regFirstName = ref('')
const regLastName = ref('')
const regEmail = ref('')
const regPassword = ref('')
const handleFinalRegister = () => {
if (regEmail.value && regPassword.value) {
alert('Account created successfully for ' + regFirstName.value);
showRegisterModal.value = false;
} else {
alert('Please fill in all fields');
}
}
</script>


<style scoped>
.login-fon {
display: flex;
justify-content: center;
align-items: center;
min-height: 100vh;
background: linear-gradient(135deg, #5896dc 0%, #043357 100%);
margin: 0;
padding: 0%; 
font-family: 'Segoe UI', sans-serif;
}
.login-center-card {
background: white;
width: 90%;
max-width: 400px; 
padding: 40px;
border-radius: 20px;
box-shadow: 0 15px 35px rgba(0,0,0,0.2);
text-align: center;
}
.card-header p { color: #636e72; font-size: 25px; margin-bottom: 30px; }
.card-body h2 { color: #2d3436; margin-bottom: 14px; }
.input-field { margin-bottom: 15px; }
.input-field input {
width: 100%;
padding: 14px;
border: 1px solid #dfe6e9;
border-radius: 10px;
font-size: 16px;
box-sizing: border-box;
}
.options {
display: flex;
justify-content: space-between;
font-size: 13px;
margin: 15px 0 25px 0;
color: #636e72;
}
.login-button {
  margin-bottom: 15px;
  width: 100%;
  padding: 15px;
  background: #2ecc71;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
}
.login-button:hover { background: #27ae60; }
@media (max-width: 480px) {
.login-center-card { padding: 25px; } }
.register-button {
  width: 100%;
  padding: 15px;
  background: #f3f4f5;
  color: #2ecc71;
  border: none;
  border-radius: 10px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
}
.register-button:hover { background: rgb(201, 201, 201);}
.register-box {
  max-width: 350px;
  padding: 25px;
}
.close-link {
  background: none;
  border: none;
  color: #5896dc;
  margin-top: 10px;
  font-size: 14px;
}
.subtitle {
  display: flex;
  align-items: center;
  text-align: center;
  color: #4f5050;
  font-size: 14px;
  margin-bottom: 30px;
}
.subtitle::before, .subtitle::after {
  content: "";
  flex: 1;
  height: 1px;
  background: #27ae60;
}
.subtitle span {
  padding: 0 15px;
}
.logo-image {
  height: 110px;
  margin: 0;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}
.modal-box {
  background: white;
  padding: 30px;
  border-radius: 20px;
  text-align: center;
  width: 90%;
  max-width: 320px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
}
.success-icon {
  width: 60px;
  height: 60px;
  background: #2ecc71;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  margin: 0 auto 15px;
}
.close-btn {
  background: #2ecc71;
  color: white;
  border: none;
  padding: 10px 0;
  width: 100%;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  margin-top: 20px;
}
.close-btn:hover { background: #27ae60; }
.close-modal-btn {
background-color: #1e4985;
color: white;
border: none;
padding: 10px 40px;
border-radius: 8px;
font-size: 16px;
font-weight: bold;
cursor: pointer;
margin-top: 25px;
width: 100%;
}
.close-modal-btn:hover {
background-color: #e5e6f2;
}
</style>

