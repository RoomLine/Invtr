<template>
  <Teleport to="body">
    <div v-if="modelValue" class="modal-overlay" @click.self="$emit('update:modelValue', false)">
      <div class="modal-box modal-box-add-item">

        <!-- ── LEFT: FORM ── -->
        <div class="modal-form-col">
          <h3 class="modal-title">
            {{ editingItem ? $t('inventory.editItem') : $t('inventory.addNew') }}
          </h3>

          <div class="input-field">
            <label>{{ $t('inventory.itemName') }}</label>
            <input type="text" v-model="form.name" :placeholder="$t('inventory.placeholders.name')" :class="{ 'input-error': errors.name }" />
            <span class="field-error" v-if="errors.name">{{ $t('inventory.errors.nameRequired') }}</span>
          </div>

          <div class="input-field">
            <label>{{ $t('inventory.category') }}</label>
            <select v-model="form.category" class="filter-select" style="width:100%">
              <option value="Electronics">{{ $t('inventory.categories.electronics') }}</option>
              <option value="Furniture">{{ $t('inventory.categories.furniture') }}</option>
              <option value="Utility">{{ $t('inventory.categories.utility') }}</option>
            </select>
          </div>

          <div class="input-field">
            <label>{{ $t('inventory.serialNumber') }}</label>
            <input type="text" v-model="form.serial" :placeholder="$t('inventory.placeholders.serial')" :class="{ 'input-error': errors.serial }" />
            <span class="field-error" v-if="errors.serial">{{ $t('inventory.errors.serialRequired') }}</span>
          </div>

          <div class="input-field">
            <label>{{ $t('inventory.status') }}</label>
            <select v-model="form.status" class="filter-select" style="width:100%">
              <option value="Available">{{ $t('inventory.statuses.available') }}</option>
              <option value="Checked-Out">{{ $t('inventory.statuses.checkedOut') }}</option>
              <option value="Under-Repair">{{ $t('inventory.statuses.underRepair') }}</option>
              <option value="Retired">{{ $t('inventory.statuses.retired') }}</option>
            </select>
          </div>

          <div class="input-field">
            <label>{{ $t('inventory.condition') }}</label>
            <select v-model="form.condition" class="filter-select" style="width:100%">
              <option value="excellent">{{ $t('inventory.conditions.excellent') }}</option>
              <option value="good">{{ $t('inventory.conditions.good') }}</option>
              <option value="damaged">{{ $t('inventory.conditions.damaged') }}</option>
              <option value="broken">{{ $t('inventory.conditions.broken') }}</option>
            </select>
          </div>

          <div class="input-field">
            <label>{{ $t('inventory.photoUrl') }}</label>
            <input type="text" v-model="form.photoUrl" placeholder="https://..." />
            <p class="field-help">{{ $t('inventory.placeholders.photoHelp') }}</p>
          </div>

          <div class="input-field">
            <label>{{ $t('inventory.location') }}</label>
            <input type="text" v-model="form.location" :placeholder="$t('inventory.placeholders.location')" />
          </div>

          <div class="modal-actions" style="flex-direction:row; gap:10px; margin-top:18px;">
            <button class="primary-btn" @click="save" style="flex:1">
              {{ editingItem ? $t('common.save') : $t('inventory.addItemBtn') }}
            </button>
            <button class="cancel-btn" @click="$emit('update:modelValue', false)" style="flex:1">
              {{ $t('common.cancel') }}
            </button>
          </div>
        </div>

        <!-- ── RIGHT: LIVE PREVIEW ── -->
        <div class="modal-preview-col">
          <p class="preview-label">{{ $t('inventory.preview') }}</p>

          <!-- Image / Emoji -->
          <div class="preview-image-box">
            <img
              v-if="form.photoUrl && imageOk"
              :src="form.photoUrl"
              @error="imageOk = false"
              @load="imageOk = true"
              class="preview-img"
              alt="preview"
            />
            <span v-else class="preview-emoji">{{ categoryEmoji }}</span>
          </div>

          <!-- Name -->
          <div class="preview-item-name">
            {{ form.name || $t('inventory.placeholders.name') }}
          </div>

          <!-- Badges row -->
          <div class="preview-badges">
            <span class="status-badge" :class="statusClass(form.status)">
              {{ translateStatus(form.status) }}
            </span>
            <span class="cond-badge" :class="'cond-' + form.condition">
              {{ translateCondition(form.condition) }}
            </span>
          </div>

          <!-- Details -->
          <div class="preview-details">
            <div class="preview-detail-row">
              <span class="preview-detail-label">{{ $t('inventory.category') }}</span>
              <span class="preview-detail-value">{{ form.category }}</span>
            </div>
            <div class="preview-detail-row" v-if="form.serial">
              <span class="preview-detail-label">{{ $t('inventory.serialNumber') }}</span>
              <span class="preview-detail-value mono-cell">{{ form.serial }}</span>
            </div>
            <div class="preview-detail-row" v-if="form.location">
              <span class="preview-detail-label">{{ $t('inventory.location') }}</span>
              <span class="preview-detail-value">{{ form.location }}</span>
            </div>
          </div>
        </div>

      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { reactive, watch, ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps({
  modelValue: Boolean,
  editingItem: Object,
})

const emit = defineEmits(['update:modelValue', 'save'])

const form = reactive({
  name: '', category: 'Electronics', serial: '', status: 'Available',
  condition: 'excellent', location: '', photoUrl: ''
})

const errors  = reactive({ name: false, serial: false })
const imageOk = ref(false)

// Watch photoUrl changes to reset imageOk so the img element re-evaluates
watch(() => form.photoUrl, () => { imageOk.value = !!form.photoUrl })

watch(() => props.editingItem, (item) => {
  if (item) {
    Object.assign(form, item)
    imageOk.value = !!item.photoUrl
  } else {
    Object.assign(form, { name: '', category: 'Electronics', serial: '', status: 'Available', condition: 'excellent', location: '', photoUrl: '' })
    imageOk.value = false
  }
})

// ── Preview helpers ──
const EMOJI_MAP = { Electronics: '💻', Furniture: '🪑', Utility: '🔧' }

const categoryEmoji = computed(() => EMOJI_MAP[form.category] || '📦')

function translateStatus(status) {
  const map = {
    'Available':   'inventory.statuses.available',
    'Checked-Out': 'inventory.statuses.checkedOut',
    'Under-Repair':'inventory.statuses.underRepair',
    'Retired':     'inventory.statuses.retired',
  }
  return t(map[status] || status)
}

function translateCondition(condition) {
  if (!condition) return '—'
  return t(`inventory.conditions.${condition.toLowerCase()}`)
}

function statusClass(status) {
  return 'status-' + (status || '').toLowerCase().replace(/[\s-]+/g, '-')
}

// ── Save ──
function save() {
  errors.name   = !form.name.trim()
  errors.serial = !form.serial.trim()
  if (errors.name || errors.serial) return
  emit('save', { ...form })
  emit('update:modelValue', false)
}
</script>
