<template>
  <div class="view-section">
    <div class="section-header">
      <div>
        <h2 class="section-title">{{ $t('navigation.requests') }}</h2>
        <p class="section-sub">{{ $t('dashboard.recentRequests') }}</p>
      </div>
    </div>

    <div class="req-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-btn"
        :class="{ active: reqFilter === tab.key }"
        @click="$emit('update:reqFilter', tab.key)"
      >
        {{ translateTab(tab.label, tab.key) }}
      </button>
    </div>

    <div class="panel">
      <div v-if="filteredRequests.length === 0" class="empty-state">{{ $t('dashboard.noRequests') }}</div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>{{ $t('auth.firstName') }}</th>
            <th>{{ $t('inventory.itemName') }}</th>
            <th>{{ $t('dashboard.recentRequests') }}</th>
            <th>{{ $t('inventory.status') }}</th>
            <th>{{ $t('inventory.condition') }}</th>
            <th>{{ $t('common.active') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="req in filteredRequests" :key="req.id">
            <td>
              <div class="item-name-cell">
                <div class="req-user-avatar">{{ req.user.charAt(0).toUpperCase() }}</div>
                <span class="req-user-name">{{ req.user }}</span>
              </div>
            </td>
            <td class="req-items-cell">{{ req.item }}</td>
            <td class="mono-cell">{{ req.requested }}</td>
            <td class="mono-cell">{{ req.returnBy || '—' }}</td>
            <td>
              <span class="status-badge" :class="statusClass(req.status)">
                {{ translateStatus(req.status) }}
              </span>
            </td>
            <td>
              <div class="action-btns" v-if="req.status === 'pending'">
                <button class="act-btn act-approve" @click="$emit('approveReq', req.id)">
                  {{ $t('common.approve') }}
                </button>
                <button class="act-btn act-delete" @click="$emit('rejectReq', req.id)">
                  {{ $t('common.reject') }}
                </button>
              </div>
              <span v-else class="no-action">—</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

defineProps({
  filteredRequests: Array,
  reqFilter: String,
})

defineEmits(['update:reqFilter', 'approveReq', 'rejectReq'])

const tabs = [
  { key: 'all',      label: 'All' },
  { key: 'pending',  label: 'Pending' },
  { key: 'approved', label: 'Approved' },
  { key: 'rejected', label: 'Rejected' },
]

function translateTab(label, key) {
  const tabMap = {
    'all': 'common.viewAll',
    'pending': 'inventory.statuses.underRepair', // Използваме смислово сходни или нови ключове
    'approved': 'inventory.statuses.available',
    'rejected': 'inventory.statuses.retired'
  }
  // Ако искаш специфични преводи за табовете, добавих ги в JSON-а по-долу
  return t(`requests.tabs.${key}`)
}

function translateStatus(status) {
  const statMap = {
    'pending': 'requests.tabs.pending',
    'approved': 'requests.tabs.approved',
    'rejected': 'requests.tabs.rejected'
  }
  return t(statMap[status.toLowerCase()] || status)
}

function statusClass(status) {
  return 'status-' + (status || '').replace(/\s+/g, '-')
}
</script>