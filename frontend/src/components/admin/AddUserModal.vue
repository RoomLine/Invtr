<template>
  <Teleport to="body">
<div v-if="modelValue" class="modal-overlay" @click.self="$emit('update:modelValue', false)">      <div class="modal-box">
        <div class="modal-title">👤 Add New User</div>
        <div class="input-field">
          <label>Full Name</label>
          <input type="text" v-model="form.name" placeholder="e.g. Jane Doe" :class="{ 'input-error': errors.name }" />
          <span class="field-error" v-if="errors.name">{{ errors.name }}</span>
        </div>
        <div class="input-field">
          <label>Email</label>
          <input type="email" v-model="form.email" placeholder="jane@example.com" :class="{ 'input-error': errors.email }" />
          <span class="field-error" v-if="errors.email">{{ errors.email }}</span>
        </div>
        <div class="input-field">
          <label>Role</label>
          <select v-model="form.role">
            <option>Staff</option>
            <option>Student</option>
            <option>Teacher</option>
            <option>Admin</option>
          </select>
        </div>
        <div class="modal-actions">
          <button class="primary-btn" @click="save">Add User</button>
          <button class="cancel-btn" @click="$emit('update:modelValue', false)">Cancel</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { reactive } from 'vue'

const props = defineProps({
  modelValue: Boolean,
})

const emit = defineEmits(['update:modelValue', 'save'])

const form = reactive({ name: '', email: '', role: 'Staff' })
const errors = reactive({ name: '', email: '' })

function save() {
  errors.name  = ''
  errors.email = ''
  if (!form.name.trim())  { errors.name  = 'Name is required.'; return }
  if (!form.email.trim()) { errors.email = 'Email is required.'; return }
  emit('save', { ...form })
  Object.assign(form, { name: '', email: '', role: 'Staff' })
  emit('update:modelValue', false)
}
</script>