<template>
  <div class="view-section">
    <div class="section-header">
      <h2>📋 Borrow Requests</h2>
    </div>
    <div class="req-tabs">
      <button
        v-for="tab in ['all','pending','approved','rejected']"
        :key="tab"
        class="tab-btn"
        :class="{ active: reqFilter === tab }"
        @click="$emit('update:reqFilter', tab)"
      >{{ tab.charAt(0).toUpperCase() + tab.slice(1) }}</button>
    </div>
    <div class="panel">
      <table class="data-table">
        <thead>
          <tr>
            <th>User</th>
            <th>Item</th>
            <th>Requested</th>
            <th>Return By</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="req in filteredRequests" :key="req.id">
            <td style="font-weight:600">{{ req.user }}</td>
            <td>{{ req.item }}</td>
            <td class="date-cell">{{ req.requested }}</td>
            <td class="date-cell">{{ req.returnBy }}</td>
            <td><span class="status-badge" :class="statusClass(req.status)">{{ req.status }}</span></td>
            <td>
              <div class="action-btns" v-if="req.status === 'pending'">
                <button class="act-btn act-approve" @click="$emit('approveReq', req.id)" title="Approve">✅</button>
                <button class="act-btn act-delete" @click="$emit('rejectReq', req.id)" title="Reject">❌</button>
              </div>
              <span v-else class="empty-state" style="padding:0">—</span>
            </td>
          </tr>
          <tr v-if="filteredRequests.length === 0">
            <td colspan="6" class="empty-state">No requests found</td>
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

function statusClass(status) {
  return 'status-' + (status || '').replace(/\s+/g, '-')
}
</script>