<template>
  <div class="view-section">
    <div class="section-header">
      <div>
        <h2 class="section-title">Borrow Requests</h2>
        <p class="section-sub">Review and manage user borrow requests</p>
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
        {{ tab.label }}
      </button>
    </div>

    <div class="panel">
      <div v-if="filteredRequests.length === 0" class="empty-state">No requests found.</div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>User</th>
            <th>Item(s)</th>
            <th>Requested</th>
            <th>Return By</th>
            <th>Status</th>
            <th>Actions</th>
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
            <td><span class="status-badge" :class="statusClass(req.status)">{{ req.status }}</span></td>
            <td>
              <div class="action-btns" v-if="req.status === 'pending'">
                <button class="act-btn act-approve" @click="$emit('approveReq', req.id)">Approve</button>
                <button class="act-btn act-delete" @click="$emit('rejectReq', req.id)">Reject</button>
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

function statusClass(status) {
  return 'status-' + (status || '').replace(/\s+/g, '-')
}
</script>
