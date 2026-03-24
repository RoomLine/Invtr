<template>
  <Teleport to="body">
<div v-if="modelValue" class="modal-overlay" @click.self="$emit('update:modelValue', false)">
      <div class="modal-box">
        <h3 class="modal-title">{{ editingItem ? 'Edit Item' : 'Add New Item' }}</h3>
        <div class="input-field">
          <label>Item Name</label>
          <input type="text" v-model="form.name" placeholder="e.g. Dell XPS Laptop" :class="{ 'input-error': errors.name }" />
          <span class="field-error" v-if="errors.name">{{ errors.name }}</span>
        </div>
        <div class="input-field">
          <label>Category</label>
          <select v-model="form.category">
            <option>Electronics</option>
            <option>Furniture</option>
            <option>Utility</option>
          </select>
        </div>
        <div class="input-field">
          <label>Serial Number</label>
          <input type="text" v-model="form.serial" placeholder="e.g. SN-XPS-001" :class="{ 'input-error': errors.serial }" />
          <span class="field-error" v-if="errors.serial">{{ errors.serial }}</span>
        </div>
        <div class="input-field">
          <label>Status</label>
          <select v-model="form.status">
            <option value="Available">Available</option>
            <option value="Checked-Out">Checked Out</option>
            <option value="Under-Repair">Under Repair</option>
            <option value="Retired">Retired</option>
          </select>
        </div>
        <div class="input-field">
          <label>Condition</label>
          <select v-model="form.condition">
            <option value="excellent">Excellent</option>
            <option value="good">Good</option>
            <option value="damaged">Damaged</option>
            <option value="broken">Broken</option>
          </select>
        </div>
        <div class="input-field">
          <label>Location</label>
          <input type="text" v-model="form.location" placeholder="e.g. Room 101" />
        </div>
        <div class="modal-actions">
          <button class="primary-btn" @click="save">
            {{ editingItem ? 'Save Changes' : 'Add Item' }}
          </button>
          <button class="cancel-btn" @click="$emit('update:modelValue', false)">Cancel</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  modelValue: Boolean,
  editingItem: Object,
})

const emit = defineEmits(['update:modelValue', 'save'])

const form = reactive({
  name: '', category: 'Electronics', serial: '', status: 'Available', condition: 'excellent', location: ''
})

const errors = reactive({ name: '', serial: '' })

watch(() => props.editingItem, (item) => {
  if (item) Object.assign(form, item)
  else Object.assign(form, { name: '', category: 'Electronics', serial: '', status: 'Available', condition: 'excellent', location: '' })
})

function save() {
  errors.name   = ''
  errors.serial = ''
  if (!form.name.trim())   { errors.name   = 'Item name is required.'; return }
  if (!form.serial.trim()) { errors.serial = 'Serial number is required.'; return }
  emit('save', { ...form })
  emit('update:modelValue', false)
}
</script>