<template>
  <div class="view-section">

    <!-- ── Section header ── -->
    <div class="section-header">
      <div>
        <h2 class="section-title">{{ $t('navigation.inventory') }}</h2>
        <p class="section-sub">{{ $t('dashboard.overview') }}</p>
      </div>
      <div class="header-actions">
        <div class="export-group">
          <button class="export-btn" @click="exportItems('CSV')" :disabled="exporting">
            {{ exporting === 'CSV' ? '...' : '⬇ CSV' }}
          </button>
          <button class="export-btn" @click="exportItems('XLSX')" :disabled="exporting">
            {{ exporting === 'XLSX' ? '...' : '⬇ XLSX' }}
          </button>
        </div>
        <button class="add-btn" @click="$emit('openAddItem')">+ {{ $t('inventory.addNew') }}</button>
      </div>
    </div>

    <!-- ── Filters ── -->
    <div class="filter-bar">
      <select
        class="filter-select"
        :value="filterCategory"
        @change="$emit('update:filterCategory', $event.target.value)"
      >
        <option value="">{{ $t('inventory.category') }}: {{ $t('common.viewAll') }}</option>
        <option value="Electronics">{{ $t('inventory.categories.electronics') }}</option>
        <option value="Utility">{{ $t('inventory.categories.utility') }}</option>
        <option value="Furniture">{{ $t('inventory.categories.furniture') }}</option>
      </select>

      <select
        class="filter-select"
        :value="filterStatus"
        @change="$emit('update:filterStatus', $event.target.value)"
      >
        <option value="">{{ $t('inventory.status') }}: {{ $t('common.viewAll') }}</option>
        <option value="Available">{{ $t('inventory.statuses.available') }}</option>
        <option value="Checked-Out">{{ $t('inventory.statuses.checkedOut') }}</option>
        <option value="Under-Repair">{{ $t('inventory.statuses.underRepair') }}</option>
        <option value="Retired">{{ $t('inventory.statuses.retired') }}</option>
      </select>
    </div>

    <!-- ── Empty state ── -->
    <div v-if="filteredItems.length === 0" class="empty-state-full">
      {{ $t('dashboard.noData') }}
    </div>

    <!-- ── Card grid ── -->
    <div v-else class="equipment-grid">
      <div
        v-for="item in filteredItems"
        :key="item.id"
        class="equip-card admin-equip-card"
      >

        <!-- Photo or emoji fallback -->
        <div v-if="item.photoUrl" class="equip-photo-container">
          <img
            :src="item.photoUrl"
            :alt="item.name"
            class="equip-photo"
            @error="$event.target.closest('.equip-photo-container').style.display='none'"
          />
        </div>
        <div v-else class="equip-no-photo">{{ item.emoji }}</div>

        <!-- Name / category / location / status -->
        <div class="equip-card-top">
          <div>
            <h4 class="equip-name">{{ item.name }}</h4>
            <p class="equip-meta">{{ translateCategory(item.category) }} · {{ item.location || '—' }}</p>
          </div>
          <span class="status-badge" :class="statusClass(item.status)">
            {{ translateStatus(item.status) }}
          </span>
        </div>

        <!-- Condition -->
        <div class="equip-condition">
          <span class="cond-label">{{ $t('inventory.condition') }}</span>
          <span class="cond-badge" :class="'cond-' + item.condition">
            {{ translateCondition(item.condition) }}
          </span>
        </div>

        <!-- Serial number -->
        <div class="equip-serial-row">
          <span class="serial-label">{{ $t('inventory.serialNumber') }}:</span>
          <span class="mono-cell">{{ item.serial || '—' }}</span>
        </div>

        <!-- Edit / Delete -->
        <div class="admin-card-actions">
          <button class="admin-card-edit-btn" @click="$emit('editItem', item)">
            ✏️ {{ $t('common.save') }}
          </button>
          <button class="admin-card-delete-btn" @click="$emit('deleteItem', item.id)">
            🗑 {{ $t('common.remove') }}
          </button>
        </div>

        <!-- Usage export per item -->
        <div class="admin-card-export">
          <button
            class="usage-export-btn"
            :disabled="exportingUsage === item.id + '_CSV'"
            @click="exportUsage(item, 'CSV')"
            title="Export usage CSV"
          >
            {{ exportingUsage === item.id + '_CSV' ? '…' : '⬇ CSV' }}
          </button>
          <button
            class="usage-export-btn"
            :disabled="exportingUsage === item.id + '_XLSX'"
            @click="exportUsage(item, 'XLSX')"
            title="Export usage XLSX"
          >
            {{ exportingUsage === item.id + '_XLSX' ? '…' : '⬇ XLSX' }}
          </button>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { toast } from '@/services/toast'

const { t } = useI18n()

defineProps({
  filteredItems:  Array,
  filterCategory: String,
  filterStatus:   String,
})

defineEmits(['openAddItem', 'editItem', 'deleteItem', 'update:filterCategory', 'update:filterStatus'])

const exporting      = ref(null)
const exportingUsage = ref(null)

// Maps button label → backend enum value
const toApiFormat = (fmt) => fmt === 'XLSX' ? 'EXCEL' : 'CSV'

// ── Bulk export ──
async function exportItems(format) {
  exporting.value = format
  try {
    const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
    const res = await fetch(`/reports/export-equipment?format=${toApiFormat(format)}`, {
      headers: { 'Authorization': `Bearer ${token}` },
    })
    if (!res.ok) {
      toast.error(t('toast.actionFailed'))
      exporting.value = null
      return
    }
    const blob = await res.blob()
    const mimeType = format === 'CSV' ? 'text/csv' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    const typedBlob = new Blob([blob], { type: mimeType })
    const url  = URL.createObjectURL(typedBlob)
    const a    = document.createElement('a')
    a.href     = url
    a.download = format === 'CSV' ? 'equipment.csv' : 'equipment.xlsx'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  } catch (_) {
    toast.error(t('toast.networkError'))
  }
  exporting.value = null
}

// ── Per-item usage export ──
async function exportUsage(item, format) {
  const key = item.id + '_' + format
  exportingUsage.value = key
  try {
    const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
    const res = await fetch(`/reports/usage?format=${toApiFormat(format)}`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
      body: JSON.stringify([item.id]),
    })
    if (!res.ok) {
      toast.error(t('toast.actionFailed'))
      exportingUsage.value = null
      return
    }
    const blob = await res.blob()
    const mimeType = format === 'CSV' ? 'text/csv' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    const typedBlob = new Blob([blob], { type: mimeType })
    const url  = URL.createObjectURL(typedBlob)
    const a    = document.createElement('a')
    a.href     = url
    const safe = (item.name || 'item').replace(/\s+/g, '_')
    a.download = format === 'CSV' ? `${safe}_usage.csv` : `${safe}_usage.xlsx`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  } catch (_) {
    toast.error(t('toast.networkError'))
  }
  exportingUsage.value = null
}

// ── Translation helpers ──
function translateCategory(category) {
  const map = {
    Electronics: 'inventory.categories.electronics',
    Furniture:   'inventory.categories.furniture',
    Utility:     'inventory.categories.utility',
  }
  return t(map[category] || category)
}

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
  return 'status-' + (status || '').replace(/[\s]+/g, '-')
}
</script>

<style scoped>
/* ── Card-level admin additions ── */
.admin-equip-card {
  cursor: default;
}

/* Serial row */
.equip-serial-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  margin-top: 2px;
}
.serial-label {
  font-size: 12px;
  color: #8a9ab0;
  font-weight: 500;
  flex-shrink: 0;
}

/* Edit + Delete row */
.admin-card-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
  padding-top: 6px;
}
.admin-card-edit-btn,
.admin-card-delete-btn {
  flex: 1;
  padding: 9px 6px;
  border: none;
  border-radius: 9px;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  white-space: nowrap;
}
.admin-card-edit-btn {
  background: #eef4ff;
  color: #1d6fa6;
}
.admin-card-edit-btn:hover {
  background: #d6eaff;
}
.admin-card-delete-btn {
  background: #fff0f0;
  color: #c0392b;
}
.admin-card-delete-btn:hover {
  background: #ffe0e0;
}

/* Usage export row */
.admin-card-export {
  display: flex;
  gap: 8px;
  padding-top: 8px;
  border-top: 1px solid #f0f4f8;
}
.usage-export-btn {
  flex: 1;
  padding: 6px 8px;
  background: rgba(82, 130, 210, 0.10);
  color: #3a7bd5;
  border: none;
  border-radius: 7px;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.usage-export-btn:hover:not(:disabled) {
  background: rgba(82, 130, 210, 0.22);
}
.usage-export-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ── Dark mode ── */
:global(.dark) .admin-card-edit-btn {
  background: rgba(29, 111, 166, 0.15);
  color: #7ab8e0;
}
:global(.dark) .admin-card-edit-btn:hover {
  background: rgba(29, 111, 166, 0.28);
}
:global(.dark) .admin-card-delete-btn {
  background: rgba(192, 57, 43, 0.15);
  color: #f87171;
}
:global(.dark) .admin-card-delete-btn:hover {
  background: rgba(192, 57, 43, 0.28);
}
:global(.dark) .admin-card-export {
  border-top-color: #1e3347;
}
:global(.dark) .usage-export-btn {
  background: rgba(82, 130, 210, 0.12);
  color: #60a5fa;
}
:global(.dark) .usage-export-btn:hover:not(:disabled) {
  background: rgba(82, 130, 210, 0.24);
}
:global(.dark) .serial-label {
  color: #4a6070;
}
</style>
