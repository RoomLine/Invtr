<template>
  <div class="view-section">

    <!-- Header -->
    <div class="section-header">
      <div>
        <h2 class="section-title">{{ $t('navigation.reports') }}</h2>
        <p class="section-sub">{{ $t('reports.subTitle') }}</p>
      </div>
      <div class="header-actions">
        <div class="export-group">
          <button class="export-btn" @click="exportReport('CSV')" :disabled="exporting">
            {{ exporting === 'CSV' ? '...' : '⬇ CSV' }}
          </button>
          <button class="export-btn" @click="exportReport('XLSX')" :disabled="exporting">
            {{ exporting === 'XLSX' ? '...' : '⬇ XLSX' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Summary stats -->
    <div class="stats-grid" style="margin-bottom:24px">
      <div class="stat-card stat-blue">
        <div class="stat-icon">📋</div>
        <div class="stat-body">
          <span class="stat-value">{{ totalRequests }}</span>
          <span class="stat-label">{{ $t('reports.totalRequests') }}</span>
        </div>
      </div>
      <div class="stat-card stat-green">
        <div class="stat-icon">✅</div>
        <div class="stat-body">
          <span class="stat-value">{{ returnedCount }}</span>
          <span class="stat-label">{{ $t('reports.returned') }}</span>
        </div>
      </div>
      <div class="stat-card stat-orange">
        <div class="stat-icon">⏳</div>
        <div class="stat-body">
          <span class="stat-value">{{ pendingCount }}</span>
          <span class="stat-label">{{ $t('reports.pending') }}</span>
        </div>
      </div>
      <div class="stat-card stat-red">
        <div class="stat-icon">📊</div>
        <div class="stat-body">
          <span class="stat-value">{{ approvalRate }}%</span>
          <span class="stat-label">{{ $t('reports.approvalRate') }}</span>
        </div>
      </div>
    </div>

    <!-- Filter tabs -->
    <div class="req-tabs" style="margin-bottom:16px">
      <button
        v-for="tab in tabs"
        :key="tab"
        class="tab-btn"
        :class="{ active: activeFilter === tab }"
        @click="activeFilter = tab"
      >
        {{ $t(`reports.tabs.${tab}`) }}
        <span class="tab-count">{{ tabCount(tab) }}</span>
      </button>
    </div>

    <!-- Table -->
    <div class="panel">
      <div v-if="filteredHistory.length === 0" class="empty-state">
        {{ $t('reports.noData') }}
      </div>
      <div v-else class="reports-table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>#</th>
              <th>{{ $t('auth.firstName') }}</th>
              <th>{{ $t('reports.items') }}</th>
              <th>{{ $t('dashboard.dateRequested') }}</th>
              <th>{{ $t('dashboard.returnBy') }}</th>
              <th>{{ $t('inventory.status') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="req in paginatedHistory" :key="req.id">
              <td class="mono-cell" style="color:#8a9ab0">#{{ req.id }}</td>
              <td>
                <div class="item-name-cell">
                  <div class="req-user-avatar">{{ req.user.charAt(0).toUpperCase() }}</div>
                  <span class="req-user-name">{{ req.user }}</span>
                </div>
              </td>
              <td class="req-items-cell">{{ req.item || '—' }}</td>
              <td class="mono-cell">{{ req.requested || '—' }}</td>
              <td class="mono-cell">{{ req.returnBy || '—' }}</td>
              <td>
                <span class="status-badge" :class="statusClass(req.status)">
                  {{ translateStatus(req.status) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination-row">
        <button class="page-btn" :disabled="page === 1" @click="page--">‹</button>
        <span class="page-info">{{ page }} / {{ totalPages }}</span>
        <button class="page-btn" :disabled="page === totalPages" @click="page++">›</button>
      </div>
    </div>

    <!-- Activity chart (CSS bar chart) -->
    <div class="panel" style="margin-top:20px">
      <div class="panel-header">
        <h3>{{ $t('reports.statusBreakdown') }}</h3>
      </div>
      <div v-if="totalRequests === 0" class="empty-state">{{ $t('reports.noData') }}</div>
      <div v-else class="breakdown-list">
        <div v-for="(entry, idx) in breakdown" :key="idx" class="breakdown-row">
          <div class="breakdown-header">
            <span class="breakdown-label">
              <span class="status-badge" :class="statusClass(entry.status)">
                {{ translateStatus(entry.status) }}
              </span>
            </span>
            <span class="breakdown-count">{{ entry.count }}</span>
          </div>
          <div class="breakdown-bar-bg">
            <div
              class="breakdown-bar-fill"
              :class="barClass(entry.status)"
              :style="{ width: (entry.count / totalRequests * 100) + '%' }"
            ></div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps({
  requests: { type: Array, default: () => [] },
})

// ── Filters ──
const tabs = ['all', 'pending', 'approved', 'returned', 'rejected', 'taken', 'late']
const activeFilter = ref('all')

const filteredHistory = computed(() => {
  if (activeFilter.value === 'all') return props.requests
  return props.requests.filter(r => r.status === activeFilter.value)
})

// ── Stats ──
const totalRequests = computed(() => props.requests.length)
const returnedCount = computed(() => props.requests.filter(r => r.status === 'returned').length)
const pendingCount  = computed(() => props.requests.filter(r => r.status === 'pending').length)
const approvalRate  = computed(() => {
  if (!totalRequests.value) return 0
  const approved = props.requests.filter(r => ['approved', 'taken', 'returned'].includes(r.status)).length
  return Math.round((approved / totalRequests.value) * 100)
})

// ── Tab counts ──
function tabCount(tab) {
  if (tab === 'all') return props.requests.length
  return props.requests.filter(r => r.status === tab).length
}

// ── Breakdown chart ──
const breakdown = computed(() => {
  const counts = {}
  props.requests.forEach(r => { counts[r.status] = (counts[r.status] || 0) + 1 })
  return Object.entries(counts).map(([status, count]) => ({ status, count }))
    .sort((a, b) => b.count - a.count)
})

// ── Pagination ──
const PAGE_SIZE = 12
const page = ref(1)
const totalPages = computed(() => Math.max(1, Math.ceil(filteredHistory.value.length / PAGE_SIZE)))
const paginatedHistory = computed(() => {
  const start = (page.value - 1) * PAGE_SIZE
  return filteredHistory.value.slice(start, start + PAGE_SIZE)
})

// Reset page when filter changes
import { watch } from 'vue'
watch(activeFilter, () => { page.value = 1 })

// ── Translations ──
function translateStatus(status) {
  const map = {
    pending:  'requests.tabs.pending',
    approved: 'requests.tabs.approved',
    rejected: 'requests.tabs.rejected',
    returned: 'requests.tabs.returned',
    taken:    'reports.tabs.taken',
    late:     'reports.tabs.late',
  }
  return t(map[status?.toLowerCase()] || status)
}

function statusClass(status) {
  return 'status-' + (status || '').replace(/\s+/g, '-')
}

function barClass(status) {
  const m = { pending: 'bar-orange', approved: 'bar-green', returned: 'bar-green',
    rejected: 'bar-red', taken: 'bar-blue', late: 'bar-orange' }
  return m[status] || 'bar-blue'
}

// ── Export ──
const exporting = ref(null)
async function exportReport(format) {
  exporting.value = format
  try {
    const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
    const res = await fetch(`/reports/export-borrow-history?format=${format}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (!res.ok) { exporting.value = null; return }
    const blob = await res.blob()
    const url  = URL.createObjectURL(blob)
    const a    = document.createElement('a')
    a.href = url
    a.download = format === 'CSV' ? 'borrow-history.csv' : 'borrow-history.xlsx'
    a.click()
    URL.revokeObjectURL(url)
  } catch (_) {}
  exporting.value = null
}
</script>
