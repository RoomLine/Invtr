<template>
  <Teleport to="body">
    <div v-if="modelValue" class="modal-overlay" @click.self="$emit('update:modelValue', false)">
      <div class="modal-box">
        <h3 class="modal-title">
          {{ editingUser ? $t('users.editUser') : $t('users.addNew') }}
        </h3>

        <div class="input-field">
          <label>{{ $t('auth.firstName') }}</label>
          <input type="text" v-model="form.firstName" :placeholder="$t('auth.firstName')" />
        </div>

        <div class="input-field">
          <label>{{ $t('auth.lastName') }}</label>
          <input type="text" v-model="form.lastName" :placeholder="$t('auth.lastName')" />
        </div>

        <div class="input-field">
          <label>{{ $t('auth.email') }}</label>
          <input type="email" v-model="form.email" placeholder="email@example.com" />
        </div>

        <div class="input-field">
          <label>{{ $t('inventory.status') }}</label>
          <select v-model="form.status">
            <option value="Active">{{ $t('common.active') }}</option>
            <option value="Inactive">{{ $t('common.inactive') }}</option>
          </select>
        </div>

        <div class="modal-actions">
          <button class="primary-btn" @click="save">
            {{ editingUser ? $t('common.save') : $t('users.addUserBtn') }}
          </button>
          <button class="cancel-btn" @click="$emit('update:modelValue', false)">
            {{ $t('common.cancel') }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  modelValue: Boolean,
  editingUser: Object,
})

const emit = defineEmits(['update:modelValue', 'save'])

const form = reactive({
  firstName: '',
  lastName: '',
  email: '',
  status: 'Active'
})

watch(() => props.editingUser, (user) => {
  if (user) {
    Object.assign(form, user)
  } else {
    Object.assign(form, { firstName: '', lastName: '', email: '', status: 'Active' })
  }
}, { immediate: true })

function save() {
  if (!form.firstName.trim() || !form.email.trim()) return
  emit('save', { ...form })
  emit('update:modelValue', false)
}
</script>