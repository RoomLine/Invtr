<template>
  <Teleport to="body">
<div v-if="modelValue" class="modal-overlay" @click.self="$emit('update:modelValue', false)">
      <div class="modal-box">
        <h3 class="modal-title">{{ editingItem ? $t('inventory.editItem')  : $t('inventory.addNew') }}</h3>
        <div class="input-field">
          <label>{{ $t('inventory.itemName') }}</label>
          <input type="text" v-model="form.name" :placeholder="$t('inventory.placeholders.name')" :class="{ 'input-error' : errors.name }" />
          <span class="field-error" v-if="errors.name">{{ $t('inventory.errors.nameRequired') }}</span>
        </div>
        <div class="input-field">
          <label>{{ $t('inventory.category') }}</label>
          <select v-model="form.category">
            <option>{{ $t('inventory.categories.electronics') }}</option>
            <option>{{ $t('inventory.categories.furniture') }}</option>
            <option>{{ $t('inventory.categories.utility') }}</option>
          </select>
        </div>
        <div class="input-field">
          <label>{{ $t('inventory.serialNumber') }}</label>
          <input type="text" v-model="form.serial" :placeholder="$t('inventory.placeholders.serial')" :class="{ 'input-error': errors.serial }" />
          <span class="field-error" v-if="errors.serial">{{$t('inventory.errors.serialRequired') }}</span>
        </div>
        <div class="input-field">
          <label>{{ $t('inventory.status') }}</label>
          <select v-model="form.status">
            <option value="Available">{{ $t('inventory.statuses.available') }}</option>
            <option value="Checked-Out">{{ $t('inventory.statuses.checkedOut') }} </option>
            <option value="Under-Repair">{{ $t('inventory.statuses.underRepair') }} </option>
            <option value="Retired">{{ $t('inventory.statuses.retired') }}</option>
          </select>
        </div>
        <div class="input-field">
          <label>{{ $t('inventory.condition') }}</label>
          <select v-model="form.condition">
            <option value="excellent">{{ $t('inventory.conditions.excellent') }}</option>
            <option value="good">{{ $t('inventory.conditions.good') }}</option>
            <option value="damaged">{{ $t('inventory.conditions.damaged') }}</option>
            <option value="broken">{{ $t('inventory.conditions.broken') }}</option>
          </select>
        </div>
        <div class="input-field">
<label>{{ $t('inventory.photoUrl') }}</label>
<input type="text" v-model="form.photoUrl" placeholder="https://:..." />
<p class="field-help" style="font-size: 12px; color: #64748b; margin-top: 4px;">{{ $t('inventory.placeholders.photoHelp') }}</p>
</div>
        <div class="input-field">
          <label>{{ $t('inventory.location') }}</label>
          <input type="text" v-model="form.location" :placeholder="$t('inventory.placeholders.location')" :class="{ 'input-error': errors.serial }" />
        </div>
        <div class="modal-actions">
          <button class="primary-btn" @click="save">
            {{ editingItem ? $t('common.save')  : $t('inventory.addItemBtn') }}
          </button>
          <button class="cancel-btn" @click="$emit('update:modelValue', false)">{{ $t('common.cancel') }}</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { reactive, watch } from 'vue'
import { useI18n } from 'vue-i18n'
const props = defineProps({
  modelValue: Boolean,
  editingItem: Object,
})

const emit = defineEmits(['update:modelValue', 'save'])

const form = reactive({
  name: '', category: 'Electronics', serial: '', status: 'Available', condition: 'excellent', location: '', photoUrl: ''
})

const errors = reactive({ name: false, serial: false }) // Променихме на Boolean за по-лесно

watch(() => props.editingItem, (item) => {
  if (item) Object.assign(form, item)
  else Object.assign(form, { name: '', category: 'Electronics', serial: '', status: 'Available', condition: 'excellent', location: '', photoUrl: '' })
})

function save() {
  errors.name = !form.name.trim()
  errors.serial = !form.serial.trim()
  
  if (errors.name || errors.serial) return
  
  emit('save', { ...form })
  emit('update:modelValue', false)
}
</script>