<template>
  <div class="view-section">
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

    <div class="filter-bar">
      <select class="filter-select" :value="filterCategory" @change="$emit('update:filterCategory', $event.target.value)">
        <option value="">{{ $t('inventory.category') }}: {{ $t('common.viewAll') }}</option>
        <option value="Electronics">{{ $t('inventory.categories.electronics') }}</option>
        <option value="Utility">{{ $t('inventory.categories.utility') }}</option>
        <option value="Furniture">{{ $t('inventory.categories.furniture') }}</option>
      </select>

      <select class="filter-select" :value="filterStatus" @change="$emit('update:filterStatus', $event.target.value)">
        <option value="">{{ $t('inventory.status') }}: {{ $t('common.viewAll') }}</option>
        <option value="Available">{{ $t('inventory.statuses.available') }}</option>
        <option value="Checked-Out">{{ $t('inventory.statuses.checkedOut') }}</option>
        <option value="Under-Repair">{{ $t('inventory.statuses.underRepair') }}</option>
        <option value="Retired">{{ $t('inventory.statuses.retired') }}</option>
      </select>
    </div>

    <div class="panel">
      <div v-if="filteredItems.length === 0" class="empty-state">{{ $t('dashboard.noData') }}</div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>{{ $t('inventory.itemName') }}</th>
            <th>{{ $t('inventory.category') }}</th>
            <th>{{ $t('inventory.serialNumber') }}</th>
            <th>{{ $t('inventory.status') }}</th>
            <th>{{ $t('inventory.condition') }}</th>
            <th>{{ $t('inventory.location') }}</th>
            <th>{{ $t('common.active') }}</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="item in filteredItems" :key="item.id">
            <tr class="inv-row" :class="{ 'inv-row-expanded': expandedId === item.id }" @click="toggleExpand(item.id)">
              <td>
                <div class="item-name-cell">
                  <div class="item-img-wrapper">
                    <img v-if="item.photoUrl" :src="item.photoUrl" class="item-actual-img" alt="item" />
                    <span v-else class="item-emoji">{{ item.emoji }}</span>
                  </div>
                  <span class="item-text-name">{{ item.name }}</span>
                  <span class="expand-arrow">{{ expandedId === item.id ? '▲' : '▼' }}</span>
                </div>
              </td>
              <td>{{ translateCategory(item.category) }}</td>
              <td class="mono-cell">{{ item.serial || '—' }}</td>
              <td>
                <span class="status-badge" :class="statusClass(item.status)">
                  {{ translateStatus(item.status) }}
                </span>
              </td>
              <td>
                <span class="cond-badge" :class="'cond-' + item.condition">
                  {{ translateCondition(item.condition) }}
                </span>
              </td>
              <td class="location-cell">{{ item.location || '—' }}</td>
              <td>
                <div class="action-btns" @click.stop>
                  <button class="act-btn act-edit" @click="$emit('editItem', item)">{{ $t('common.save') }}</button>
                  <button class="act-btn act-delete" @click="$emit('deleteItem', item.id)">{{ $t('common.remove') }}</button>
                  <button class="act-btn act-usage" :disabled="exportingUsage === item.id + '_CSV'" @click="exportUsage(item, 'CSV')" title="Export usage report CSV">
                    {{ exportingUsage === item.id + '_CSV' ? '...' : '⬇ CSV' }}
                  </button>
                  <button class="act-btn act-usage" :disabled="exportingUsage === item.id + '_XLSX'" @click="exportUsage(item, 'XLSX')" title="Export usage report XLSX">
                    {{ exportingUsage === item.id + '_XLSX' ? '...' : '⬇ XLSX' }}
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="expandedId === item.id" class="inv-detail-row">
              <td colspan="7">
                <div class="inv-detail-panel">
                  <div class="inv-detail-info">
                    <div class="inv-detail-photo">
                      <img v-if="item.photoUrl" :src="item.photoUrl" class="inv-detail-img" alt="item" />
                      <span v-else class="inv-detail-emoji">{{ item.emoji }}</span>
                    </div>
                    <div class="inv-detail-fields">
                      <h3 class="inv-detail-name">{{ item.name }}</h3>
                      <div class="inv-detail-grid">
                        <div class="inv-detail-field">
                          <span class="inv-detail-label">{{ $t('inventory.category') }}</span>
                          <span class="inv-detail-value">{{ translateCategory(item.category) }}</span>
                        </div>
                        <div class="inv-detail-field">
                          <span class="inv-detail-label">{{ $t('inventory.serialNumber') }}</span>
                          <span class="inv-detail-value mono-cell">{{ item.serial || '—' }}</span>
                        </div>
                        <div class="inv-detail-field">
                          <span class="inv-detail-label">{{ $t('inventory.status') }}</span>
                          <span class="status-badge" :class="statusClass(item.status)">{{ translateStatus(item.status) }}</span>
                        </div>
                        <div class="inv-detail-field">
                          <span class="inv-detail-label">{{ $t('inventory.condition') }}</span>
                          <span class="cond-badge" :class="'cond-' + item.condition">{{ translateCondition(item.condition) }}</span>
                        </div>
                        <div class="inv-detail-field">
                          <span class="inv-detail-label">{{ $t('inventory.location') }}</span>
                          <span class="inv-detail-value">{{ item.location || '—' }}</span>
                        </div>
                        <div class="inv-detail-field">
                          <span class="inv-detail-label">ID</span>
                          <span class="inv-detail-value mono-cell">{{ item.id }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="inv-detail-qr">
                    <p class="inv-detail-qr-label">QR Code</p>
                    <img
                      :src="item.qrCodeUrl || `https://api.qrserver.com/v1/create-qr-code/?size=140x140&data=${encodeURIComponent(item.serial || item.name)}`"
                      class="inv-qr-img"
                      alt="QR Code"
                    />
                    <p class="inv-qr-sub">{{ item.serial || item.name }}</p>
                  </div>
                </div>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

defineProps({
  filteredItems: Array,
  filterCategory: String,
  filterStatus: String,
})

defineEmits(['openAddItem', 'editItem', 'deleteItem', 'update:filterCategory', 'update:filterStatus'])

const exporting = ref(null)
const exportingUsage = ref(null)
const expandedId = ref(null)

function toggleExpand(id) {
  expandedId.value = expandedId.value === id ? null : id
}

async function exportUsage(item, format) {
  const key = item.id + '_' + format
  exportingUsage.value = key
  try {
    const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
    const res = await fetch(`/reports/usage?format=${format}`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
      body: JSON.stringify([item.id]),
    })
    if (!res.ok) { exportingUsage.value = null; return }
    const blob = await res.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    const safeName = (item.name || 'item').replace(/\s+/g, '_')
    a.download = format === 'CSV' ? `${safeName}_usage.csv` : `${safeName}_usage.xlsx`
    a.click()
    URL.revokeObjectURL(url)
  } catch (_) {}
  exportingUsage.value = null
}

async function exportItems(format) {
  exporting.value = format
  try {
    const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
    const res = await fetch(`/reports/export-equipment?format=${format}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (!res.ok) { exporting.value = null; return }
    const blob = await res.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = format === 'CSV' ? 'equipment.csv' : 'equipment.xlsx'
    a.click()
    URL.revokeObjectURL(url)
  } catch (_) {}
  exporting.value = null
}

function translateCategory(category) {
  const catMap = {
    'Electronics': 'inventory.categories.electronics',
    'Furniture': 'inventory.categories.furniture',
    'Utility': 'inventory.categories.utility'
  }
  return t(catMap[category] || category)
}

function translateStatus(status) {
  const statMap = {
    'Available': 'inventory.statuses.available',
    'Checked-Out': 'inventory.statuses.checkedOut',
    'Under-Repair': 'inventory.statuses.underRepair',
    'Retired': 'inventory.statuses.retired'
  }
  return t(statMap[status] || status)
}

function translateCondition(condition) {
  if (!condition) return '—'
  return t(`inventory.conditions.${condition.toLowerCase()}`)
}

function statusClass(status) {
  return 'status-' + (status || '').toLowerCase().replace(/[\s-]+/g, '-')
}
</script>

<style scoped>
.act-usage {
  background: rgba(82, 130, 210, 0.12);
  color: #3a7bd5;
  border: none;
  border-radius: 6px;
  padding: 4px 8px;
  font-size: 12px;
  cursor: pointer;
  font-family: inherit;
}
.act-usage:hover:not(:disabled) {
  background: rgba(82, 130, 210, 0.22);
}
.act-usage:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.inv-row {
  cursor: pointer;
  transition: background 0.15s;
}
.inv-row:hover {
  background: rgba(82, 210, 101, 0.06);
}
.inv-row-expanded {
  background: rgba(82, 210, 101, 0.08);
}
.expand-arrow {
  margin-left: 6px;
  font-size: 10px;
  color: #8a9baa;
}
.inv-detail-row td {
  padding: 0;
}
.inv-detail-panel {
  display: flex;
  gap: 24px;
  padding: 20px 24px;
  background: #f7fafc;
  border-top: 1px solid #e2e8f0;
  border-bottom: 2px solid #52d265;
}
:global(.dark) .inv-detail-panel {
  background: rgba(255,255,255,0.04);
  border-top-color: rgba(255,255,255,0.08);
}
.inv-detail-info {
  display: flex;
  gap: 20px;
  flex: 1;
}
.inv-detail-photo {
  display: flex;
  align-items: flex-start;
}
.inv-detail-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}
.inv-detail-emoji {
  font-size: 52px;
  line-height: 1;
}
.inv-detail-fields {
  flex: 1;
}
.inv-detail-name {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #1a2d3e;
}
:global(.dark) .inv-detail-name {
  color: #e2e8f0;
}
.inv-detail-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px 20px;
}
.inv-detail-field {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.inv-detail-label {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #8a9baa;
}
.inv-detail-value {
  font-size: 13px;
  color: #1a2d3e;
}
:global(.dark) .inv-detail-value {
  color: #cbd5e0;
}
.inv-detail-qr {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}
.inv-detail-qr-label {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #8a9baa;
}
.inv-qr-img {
  width: 140px;
  height: 140px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #fff;
}
.inv-qr-sub {
  font-size: 11px;
  color: #8a9baa;
  max-width: 140px;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
