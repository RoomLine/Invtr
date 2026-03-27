<template>
  <div class="view-section">
    <div class="section-header">
      <div>
        <h2 class="section-title">{{ $t('navigation.requests') }}</h2>
        <p class="section-sub">{{ $t('dashboard.recentRequests') }}</p>
      </div>
<<<<<<< HEAD
      <div class="search-bar">
        <span class="search-icon">🔍</span>
        <input class="search-input" type="text" v-model="reqSearch" placeholder="Search user or item..." />
      </div>
=======
>>>>>>> origin/main
    </div>

    <div class="req-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-btn"
        :class="{ active: reqFilter === tab.key }"
        @click="$emit('update:reqFilter', tab.key)"
      >
<<<<<<< HEAD
        {{ $t(`requests.tabs.${tab.key}`) }}
=======
        {{ translateTab(tab.label, tab.key) }}
>>>>>>> origin/main
      </button>
    </div>

    <div class="panel">
<<<<<<< HEAD
      <div v-if="displayedRequests.length === 0" class="empty-state">{{ $t('dashboard.noRequests') }}</div>
=======
      <div v-if="filteredRequests.length === 0" class="empty-state">{{ $t('dashboard.noRequests') }}</div>
>>>>>>> origin/main
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>{{ $t('auth.firstName') }}</th>
            <th>{{ $t('inventory.itemName') }}</th>
<<<<<<< HEAD
            <th>{{ $t('dashboard.dateRequested') }}</th>
            <th>{{ $t('dashboard.returnBy') }}</th>
            <th>{{ $t('inventory.status') }}</th>
=======
            <th>{{ $t('dashboard.recentRequests') }}</th>
            <th>{{ $t('inventory.status') }}</th>
            <th>{{ $t('inventory.condition') }}</th>
>>>>>>> origin/main
            <th>{{ $t('common.active') }}</th>
          </tr>
        </thead>
        <tbody>
<<<<<<< HEAD
          <tr v-for="req in displayedRequests" :key="req.id">
=======
          <tr v-for="req in filteredRequests" :key="req.id">
>>>>>>> origin/main
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
<<<<<<< HEAD
              <div class="action-btns" v-else-if="req.status === 'approved'">
                <button class="act-btn act-return" @click="openReturnModal(req)">
                  {{ $t('requests.tabs.return') }}
                </button>
              </div>
=======
>>>>>>> origin/main
              <span v-else class="no-action">—</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
<<<<<<< HEAD

    <!-- Return Modal -->
    <div v-if="showReturnModal" class="modal-overlay" @click.self="closeReturnModal">
      <div class="modal-box">
        <h3 class="modal-title">{{ $t('requests.returnTitle') }}</h3>
        <p class="modal-sub">{{ returningReq?.item }}</p>

        <div v-for="eqId in (returningReq?.equipmentIds || [])" :key="eqId" class="return-condition-row">
          <label class="return-cond-label">{{ returningReq?.itemMap?.[eqId] || `Item #${eqId}` }}</label>
          <select v-model="returnConditions[eqId]" class="filter-select">
            <option value="EXCELLENT">{{ $t('inventory.conditions.excellent') }}</option>
            <option value="GOOD">{{ $t('inventory.conditions.good') }}</option>
            <option value="DAMAGED">{{ $t('inventory.conditions.damaged') }}</option>
            <option value="BROKEN">{{ $t('inventory.conditions.broken') }}</option>
          </select>
        </div>

        <div v-if="returnError" class="alert alert-error">{{ returnError }}</div>

        <div class="modal-actions">
          <button class="act-btn act-approve" @click="submitReturn" :disabled="returnLoading">
            <span v-if="returnLoading">...</span>
            <span v-else>{{ $t('requests.confirmReturn') }}</span>
          </button>
          <button class="cancel-btn" @click="closeReturnModal">{{ $t('common.cancel') }}</button>
        </div>
      </div>
    </div>
=======
>>>>>>> origin/main
  </div>
</template>

<script setup>
<<<<<<< HEAD
import { ref, reactive, computed } from 'vue'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

const props = defineProps({
=======
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

defineProps({
>>>>>>> origin/main
  filteredRequests: Array,
  reqFilter: String,
})

<<<<<<< HEAD
const reqSearch = ref('')
const displayedRequests = computed(() => {
  const q = reqSearch.value.toLowerCase()
  if (!q) return props.filteredRequests
  return props.filteredRequests.filter(r =>
    r.user.toLowerCase().includes(q) || r.item.toLowerCase().includes(q)
  )
})

const emit = defineEmits(['update:reqFilter', 'approveReq', 'rejectReq', 'returnReq'])

const tabs = [
  { key: 'all' },
  { key: 'pending' },
  { key: 'approved' },
  { key: 'rejected' },
  { key: 'returned' },
]

function translateStatus(status) {
  const statMap = {
    'pending':  'requests.tabs.pending',
    'approved': 'requests.tabs.approved',
    'rejected': 'requests.tabs.rejected',
    'returned': 'requests.tabs.returned',
  }
  return t(statMap[status?.toLowerCase()] || status)
=======
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
>>>>>>> origin/main
}

function statusClass(status) {
  return 'status-' + (status || '').replace(/\s+/g, '-')
}
<<<<<<< HEAD

// ── Return Modal ──
const showReturnModal = ref(false)
const returningReq    = ref(null)
const returnConditions = reactive({})
const returnLoading   = ref(false)
const returnError     = ref('')

function openReturnModal(req) {
  returningReq.value = req
  returnError.value  = ''
  // Default all items to GOOD condition
  ;(req.equipmentIds || []).forEach(id => { returnConditions[id] = 'GOOD' })
  showReturnModal.value = true
}

function closeReturnModal() {
  showReturnModal.value = false
  returningReq.value = null
}

async function submitReturn() {
  if (!returningReq.value) return
  returnLoading.value = true
  returnError.value   = ''
  try {
    emit('returnReq', {
      id: returningReq.value.id,
      conditionPerEquipment: { ...returnConditions },
    })
    closeReturnModal()
  } catch (_) {
    returnError.value = 'Failed to process return.'
  } finally {
    returnLoading.value = false
  }
}
</script>
=======
</script>
>>>>>>> origin/main
