<template>
  <div v-if="!isLoggedIn" class="login-page">
    <el-card class="login-card" shadow="never">
      <div class="login-title">逾期上岸后台</div>
      <div class="login-subtitle">本地演示管理系统</div>
      <el-form label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="管理员账号">
          <el-input v-model="loginForm.username" placeholder="boss" size="large" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="loginForm.password"
            placeholder="boss123456"
            show-password
            size="large"
            type="password"
          />
        </el-form-item>
        <el-button class="login-button" :loading="loggingIn" size="large" type="danger" @click="handleLogin">
          登录后台
        </el-button>
      </el-form>
    </el-card>
  </div>

  <el-container v-else class="admin-shell">
    <el-aside width="232px" class="admin-aside">
      <div class="admin-logo">
        <div class="logo-mark">岸</div>
        <div>
          <strong>逾期上岸</strong>
          <span>管理后台</span>
        </div>
      </div>
      <el-menu :default-active="activeSection" class="admin-menu" @select="handleSectionSelect">
        <el-menu-item index="leads">线索管理</el-menu-item>
        <el-menu-item index="assets">素材管理</el-menu-item>
        <el-menu-item index="articles">资讯管理</el-menu-item>
        <el-menu-item index="cases">成功案例</el-menu-item>
        <el-menu-item index="admins" :disabled="!isBoss">管理员管理</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div>
          <h1>{{ sectionTitle }}</h1>
          <p>{{ sectionDescription }}</p>
        </div>
        <div class="header-user">
          <el-tag :type="isBoss ? 'danger' : 'info'" effect="light">{{ me?.role }}</el-tag>
          <span>{{ me?.displayName }}</span>
          <el-button link type="danger" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>

      <el-main class="admin-main">
        <section v-show="activeSection === 'leads'" class="panel">
          <div class="toolbar">
            <el-input v-model="leadFilters.keyword" clearable placeholder="手机号/称呼/地区" />
            <el-select v-model="leadFilters.leadType" clearable placeholder="线索类型">
              <el-option label="已提交线索" value="SUBMITTED" />
              <el-option label="仅登录客户" value="LOGIN_ONLY" />
            </el-select>
            <el-input v-model="leadFilters.debtType" clearable placeholder="债务类型" />
            <el-select v-model="leadFilters.viewedWechatQr" clearable placeholder="企微二维码">
              <el-option label="已查看" value="true" />
              <el-option label="未查看" value="false" />
            </el-select>
            <el-date-picker
              v-model="leadFilters.startTime"
              placeholder="开始时间"
              type="datetime"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
            <el-date-picker
              v-model="leadFilters.endTime"
              placeholder="结束时间"
              type="datetime"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
            <el-button type="danger" @click="searchLeads">筛选</el-button>
            <el-button @click="resetLeadFilters">重置</el-button>
          </div>

          <div class="export-bar">
            <div class="export-fields">
              <span>导出字段</span>
              <el-checkbox-group v-model="exportFields">
                <el-checkbox v-for="field in availableExportFields" :key="field.value" :label="field.value">
                  {{ field.label }}
                </el-checkbox>
              </el-checkbox-group>
            </div>
            <el-button :loading="exportingLeads" type="danger" @click="handleExportLeads">导出 Excel</el-button>
          </div>

          <el-table v-loading="leadLoading" :data="leadRows" stripe>
            <el-table-column label="类型" min-width="112">
              <template #default="{ row }">
                <el-tag :type="row.rowType === 'SUBMITTED' ? 'danger' : 'info'" effect="light">
                  {{ displayRowType(row.rowType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="手机号" min-width="136" prop="phone" />
            <el-table-column label="称呼/地区" min-width="160">
              <template #default="{ row }">
                <strong>{{ row.surname || '-' }}</strong>
                <span class="muted block">{{ row.region || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="债务" min-width="160">
              <template #default="{ row }">
                <strong>{{ formatAmount(row.debtAmount) }}</strong>
                <span class="muted block">{{ row.debtType || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="来源" min-width="128">
              <template #default="{ row }">{{ displaySource(row.source) }}</template>
            </el-table-column>
            <el-table-column label="企微" min-width="96">
              <template #default="{ row }">
                <el-tag :type="row.viewedWechatQr ? 'success' : 'info'" effect="plain">
                  {{ row.viewedWechatQr ? '已查看' : '未查看' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="最近行为" min-width="172">
              <template #default="{ row }">
                {{ row.latestEventType || '-' }}
                <span class="muted block">{{ formatDateTime(row.latestEventAt) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="提交时间" min-width="172">
              <template #default="{ row }">{{ formatDateTime(row.leadCreatedAt || row.firstLoginAt) }}</template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="118">
              <template #default="{ row }">
                <el-button link type="danger" @click="openLeadHistory(row)">历史</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="leadFilters.page"
            v-model:page-size="leadFilters.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="leadTotal"
            background
            class="pagination"
            layout="total, sizes, prev, pager, next"
            @current-change="loadLeads"
            @size-change="loadLeads"
          />
        </section>

        <section v-show="activeSection === 'assets'" class="panel">
          <el-alert
            class="section-alert"
            :closable="false"
            show-icon
            title="图片支持 jpg、jpeg、png、webp，单个不超过 20MB；视频第一版仅支持 mp4，单个不超过 500MB。"
            type="info"
          />
          <el-table v-loading="assetLoading" :data="assets" stripe>
            <el-table-column label="素材" min-width="210">
              <template #default="{ row }">
                <strong>{{ row.title }}</strong>
                <span class="muted block">{{ row.assetKey }}</span>
              </template>
            </el-table-column>
            <el-table-column label="文件" min-width="240">
              <template #default="{ row }">
                <a v-if="row.fileUrl" :href="resolveFileUrl(row.fileUrl)" target="_blank">
                  {{ row.originalFileName || row.fileUrl }}
                </a>
                <span v-else class="muted">未上传</span>
                <span class="muted block">{{ row.mimeType || '-' }} {{ formatFileSize(row.fileSize) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="更新时间" min-width="172">
              <template #default="{ row }">{{ formatDateTime(row.updatedAt) }}</template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="240">
              <template #default="{ row }">
                <el-upload
                  :http-request="handleAssetUpload(row)"
                  :show-file-list="false"
                  class="inline-upload"
                >
                  <el-button size="small" type="danger">上传</el-button>
                </el-upload>
                <el-button size="small" @click="openAssetEdit(row)">编辑标题</el-button>
              </template>
            </el-table-column>
          </el-table>
        </section>

        <section v-show="activeSection === 'articles'" class="panel">
          <div class="toolbar">
            <el-input v-model="articleQuery.keyword" clearable placeholder="标题关键词" />
            <el-select v-model="articleQuery.status" clearable placeholder="状态">
              <el-option label="已发布" value="PUBLISHED" />
              <el-option label="草稿" value="DRAFT" />
            </el-select>
            <el-button type="danger" @click="loadArticles">筛选</el-button>
            <el-button type="danger" plain @click="openArticleCreate">新增资讯</el-button>
          </div>
          <el-table v-loading="articleLoading" :data="articles" stripe>
            <el-table-column label="标题" min-width="260">
              <template #default="{ row }">
                <strong>{{ row.title }}</strong>
                <span class="muted block">{{ row.summary || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'" effect="light">
                  {{ displayStatus(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="排序" width="90" prop="sortOrder" />
            <el-table-column label="更新时间" width="180">
              <template #default="{ row }">{{ formatDateTime(row.updatedAt) }}</template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="236">
              <template #default="{ row }">
                <el-button link type="danger" @click="openArticleEdit(row)">编辑</el-button>
                <el-button v-if="row.status !== 'PUBLISHED'" link type="success" @click="publishArticle(row.id)">
                  发布
                </el-button>
                <el-button v-else link @click="offlineArticle(row.id)">下线</el-button>
                <el-button link type="danger" @click="deleteArticle(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="articleQuery.page"
            :total="articleTotal"
            background
            class="pagination"
            layout="total, prev, pager, next"
            @current-change="loadArticles"
          />
        </section>

        <section v-show="activeSection === 'cases'" class="panel">
          <div class="toolbar">
            <el-input v-model="caseQuery.keyword" clearable placeholder="姓名/平台关键词" />
            <el-select v-model="caseQuery.status" clearable placeholder="状态">
              <el-option label="已发布" value="PUBLISHED" />
              <el-option label="草稿" value="DRAFT" />
            </el-select>
            <el-button type="danger" @click="loadCases">筛选</el-button>
            <el-button type="danger" plain @click="openCaseCreate">新增案例</el-button>
          </div>
          <el-table v-loading="caseLoading" :data="cases" stripe>
            <el-table-column label="客户" min-width="160">
              <template #default="{ row }">
                <strong>{{ row.displayName }}</strong>
                <span class="muted block">{{ row.maskedPhone || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="逾期信息" min-width="240">
              <template #default="{ row }">
                {{ row.overduePlatforms || '-' }}
                <span class="muted block">{{ formatAmount(row.overdueAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="方案" min-width="220" prop="handlingPlan" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'" effect="light">
                  {{ displayStatus(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="236">
              <template #default="{ row }">
                <el-button link type="danger" @click="openCaseEdit(row)">编辑</el-button>
                <el-button v-if="row.status !== 'PUBLISHED'" link type="success" @click="publishCase(row.id)">
                  发布
                </el-button>
                <el-button v-else link @click="offlineCase(row.id)">下线</el-button>
                <el-button link type="danger" @click="deleteCase(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="caseQuery.page"
            :total="caseTotal"
            background
            class="pagination"
            layout="total, prev, pager, next"
            @current-change="loadCases"
          />
        </section>

        <section v-show="activeSection === 'admins'" class="panel">
          <el-alert
            v-if="!isBoss"
            :closable="false"
            show-icon
            title="只有 BOSS 角色可以管理管理员账号。"
            type="warning"
          />
          <template v-else>
            <div class="toolbar right">
              <el-button type="danger" @click="openAdminCreate">新增管理员</el-button>
            </div>
            <el-table v-loading="adminUserLoading" :data="adminUsers" stripe>
              <el-table-column label="账号" min-width="160" prop="username" />
              <el-table-column label="显示名" min-width="160" prop="displayName" />
              <el-table-column label="角色" width="110">
                <template #default="{ row }">
                  <el-tag :type="row.role === 'BOSS' ? 'danger' : 'info'" effect="light">{{ row.role }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="110">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" effect="light">
                    {{ row.status === 'ACTIVE' ? '启用' : '停用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="最近登录" width="180">
                <template #default="{ row }">{{ formatDateTime(row.lastLoginAt) }}</template>
              </el-table-column>
              <el-table-column fixed="right" label="操作" width="220">
                <template #default="{ row }">
                  <el-button link type="danger" @click="openAdminEdit(row)">编辑</el-button>
                  <el-button link @click="resetAdminPassword(row)">重置密码</el-button>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </section>
      </el-main>
    </el-container>
  </el-container>

  <el-drawer v-model="historyDrawerVisible" size="520px" title="客户历史">
    <div v-loading="historyLoading">
      <template v-if="leadHistory">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="客户ID">{{ leadHistory.customer.customerId }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ leadHistory.customer.phone }}</el-descriptions-item>
          <el-descriptions-item label="首次登录">{{ formatDateTime(leadHistory.customer.firstLoginAt) }}</el-descriptions-item>
          <el-descriptions-item label="最近登录">{{ formatDateTime(leadHistory.customer.lastLoginAt) }}</el-descriptions-item>
        </el-descriptions>

        <h3 class="drawer-title">提交记录</h3>
        <el-collapse>
          <el-collapse-item v-for="item in leadHistory.leads" :key="item.id" :title="leadTitle(item)">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="来源">{{ displaySource(item.source) }}</el-descriptions-item>
              <el-descriptions-item label="称呼">{{ item.surname || '-' }}</el-descriptions-item>
              <el-descriptions-item label="地区">{{ item.region || '-' }}</el-descriptions-item>
              <el-descriptions-item label="债务金额">{{ formatAmount(item.debtAmount) }}</el-descriptions-item>
              <el-descriptions-item label="债务类型">{{ item.debtType || '-' }}</el-descriptions-item>
              <el-descriptions-item label="备注">{{ item.debtDescription || '-' }}</el-descriptions-item>
            </el-descriptions>
          </el-collapse-item>
        </el-collapse>

        <h3 class="drawer-title">行为记录</h3>
        <el-timeline>
          <el-timeline-item
            v-for="item in leadHistory.events"
            :key="item.id"
            :timestamp="formatDateTime(item.createdAt)"
          >
            {{ item.eventType }} <span class="muted">{{ item.sourcePage || '' }}</span>
          </el-timeline-item>
        </el-timeline>
      </template>
    </div>
  </el-drawer>

  <el-dialog v-model="assetDialogVisible" title="编辑素材标题" width="420px">
    <el-form label-position="top">
      <el-form-item label="素材标题">
        <el-input v-model="assetForm.title" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="assetDialogVisible = false">取消</el-button>
      <el-button type="danger" @click="saveAssetTitle">保存</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="articleDialogVisible" :title="articleForm.id ? '编辑资讯' : '新增资讯'" width="680px">
    <el-form label-position="top">
      <el-form-item label="标题">
        <el-input v-model="articleForm.title" maxlength="200" show-word-limit />
      </el-form-item>
      <el-form-item label="封面地址">
        <el-input v-model="articleForm.coverUrl" placeholder="可填上传后的图片地址" />
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="articleForm.summary" maxlength="500" show-word-limit type="textarea" />
      </el-form-item>
      <el-form-item label="正文">
        <el-input v-model="articleForm.contentText" :rows="8" maxlength="10000" show-word-limit type="textarea" />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="articleForm.sortOrder" :min="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="articleDialogVisible = false">取消</el-button>
      <el-button :loading="articleSaving" type="danger" @click="saveArticle">保存</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="caseDialogVisible" :title="caseForm.id ? '编辑案例' : '新增案例'" width="680px">
    <el-form label-position="top">
      <el-form-item label="展示姓名">
        <el-input v-model="caseForm.displayName" maxlength="50" />
      </el-form-item>
      <el-form-item label="脱敏手机号">
        <el-input v-model="caseForm.maskedPhone" placeholder="例如 138****6357" />
      </el-form-item>
      <el-form-item label="逾期平台">
        <el-input v-model="caseForm.overduePlatforms" maxlength="500" />
      </el-form-item>
      <el-form-item label="逾期金额">
        <el-input-number v-model="caseForm.overdueAmount" :min="0" :precision="2" />
      </el-form-item>
      <el-form-item label="处理方案">
        <el-input v-model="caseForm.handlingPlan" maxlength="500" />
      </el-form-item>
      <el-form-item label="详情文本">
        <el-input v-model="caseForm.detailText" :rows="6" maxlength="10000" show-word-limit type="textarea" />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="caseForm.sortOrder" :min="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="caseDialogVisible = false">取消</el-button>
      <el-button :loading="caseSaving" type="danger" @click="saveCase">保存</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="adminDialogVisible" :title="adminForm.id ? '编辑管理员' : '新增管理员'" width="460px">
    <el-form label-position="top">
      <el-form-item label="账号" v-if="!adminForm.id">
        <el-input v-model="adminForm.username" placeholder="3-30 位字母、数字、下划线" />
      </el-form-item>
      <el-form-item label="初始密码" v-if="!adminForm.id">
        <el-input v-model="adminForm.password" show-password type="password" />
      </el-form-item>
      <el-form-item label="显示名">
        <el-input v-model="adminForm.displayName" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="adminForm.role">
          <el-option label="普通管理员" value="ADMIN" />
          <el-option label="BOSS" value="BOSS" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="adminForm.id" label="状态">
        <el-select v-model="adminForm.status">
          <el-option label="启用" value="ACTIVE" />
          <el-option label="停用" value="DISABLED" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="adminDialogVisible = false">取消</el-button>
      <el-button :loading="adminSaving" type="danger" @click="saveAdminUser">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ADMIN_TOKEN_KEY,
  adminApi,
  clearAdminToken,
  saveAdminToken,
  type AdminMe,
  type AdminUserItem,
  type ArticleItem,
  type AssetResource,
  type CustomerHistory,
  type LeadHistoryItem,
  type LeadListItem,
  type SuccessCaseItem
} from './services/api'

type SectionKey = 'leads' | 'assets' | 'articles' | 'cases' | 'admins'

const loginForm = reactive({
  username: 'boss',
  password: 'boss123456'
})
const loggingIn = ref(false)
const token = ref(localStorage.getItem(ADMIN_TOKEN_KEY) || '')
const me = ref<AdminMe | null>(null)
const activeSection = ref<SectionKey>('leads')

const isLoggedIn = computed(() => Boolean(token.value && me.value))
const isBoss = computed(() => me.value?.role === 'BOSS')
const sectionTitle = computed(() => {
  const titles: Record<SectionKey, string> = {
    leads: '线索管理',
    assets: '素材管理',
    articles: '资讯管理',
    cases: '成功案例',
    admins: '管理员管理'
  }
  return titles[activeSection.value]
})
const sectionDescription = computed(() => {
  const descriptions: Record<SectionKey, string> = {
    leads: '查看登录客户、提交记录、行为记录，并按字段导出 Excel。',
    assets: '维护首页视频、入口图片和企业微信二维码。',
    articles: '维护 H5 资讯列表和详情文本。',
    cases: '维护成功案例卡片和详情文本。',
    admins: '创建少量内部管理员账号，只有 BOSS 可操作。'
  }
  return descriptions[activeSection.value]
})

const leadLoading = ref(false)
const leadRows = ref<LeadListItem[]>([])
const leadTotal = ref(0)
const leadFilters = reactive({
  keyword: '',
  leadType: '',
  debtType: '',
  viewedWechatQr: '' as '' | 'true' | 'false',
  startTime: '',
  endTime: '',
  page: 1,
  pageSize: 10
})
const availableExportFields = [
  { value: 'rowType', label: '线索类型' },
  { value: 'customerId', label: '客户ID' },
  { value: 'leadId', label: '线索ID' },
  { value: 'phone', label: '手机号' },
  { value: 'surname', label: '称呼' },
  { value: 'region', label: '地区' },
  { value: 'debtAmount', label: '债务金额' },
  { value: 'debtType', label: '债务类型' },
  { value: 'source', label: '来源' },
  { value: 'firstLoginAt', label: '首次登录' },
  { value: 'leadCreatedAt', label: '提交时间' },
  { value: 'viewedWechatQr', label: '企微查看' },
  { value: 'latestEventType', label: '最近行为' },
  { value: 'historyCount', label: '历史条数' }
]
const exportFields = ref(availableExportFields.map((field) => field.value))
const exportingLeads = ref(false)
const historyDrawerVisible = ref(false)
const historyLoading = ref(false)
const leadHistory = ref<CustomerHistory | null>(null)

const assetLoading = ref(false)
const assets = ref<AssetResource[]>([])
const assetDialogVisible = ref(false)
const assetForm = reactive({
  assetKey: '',
  title: ''
})

const articleLoading = ref(false)
const articleSaving = ref(false)
const articles = ref<ArticleItem[]>([])
const articleTotal = ref(0)
const articleQuery = reactive({
  keyword: '',
  status: '',
  page: 1,
  pageSize: 10
})
const articleDialogVisible = ref(false)
const articleForm = reactive<Partial<ArticleItem>>({
  id: undefined,
  title: '',
  coverUrl: '',
  summary: '',
  contentText: '',
  sortOrder: 0
})

const caseLoading = ref(false)
const caseSaving = ref(false)
const cases = ref<SuccessCaseItem[]>([])
const caseTotal = ref(0)
const caseQuery = reactive({
  keyword: '',
  status: '',
  page: 1,
  pageSize: 10
})
const caseDialogVisible = ref(false)
const caseForm = reactive<Partial<SuccessCaseItem>>({
  id: undefined,
  displayName: '',
  maskedPhone: '',
  overduePlatforms: '',
  overdueAmount: 0,
  handlingPlan: '',
  detailText: '',
  sortOrder: 0
})

const adminUserLoading = ref(false)
const adminSaving = ref(false)
const adminUsers = ref<AdminUserItem[]>([])
const adminDialogVisible = ref(false)
const adminForm = reactive({
  id: undefined as number | undefined,
  username: '',
  password: '',
  displayName: '',
  role: 'ADMIN',
  status: 'ACTIVE'
})

onMounted(async () => {
  if (!token.value) {
    return
  }
  await bootstrap()
})

async function bootstrap() {
  try {
    me.value = await adminApi.me()
    await loadCurrentSection()
  } catch (error) {
    clearSession()
    ElMessage.error(getErrorMessage(error))
  }
}

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loggingIn.value = true
  try {
    const result = await adminApi.login(loginForm)
    saveAdminToken(result.token)
    token.value = result.token
    await bootstrap()
    ElMessage.success('登录成功')
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    loggingIn.value = false
  }
}

async function handleLogout() {
  try {
    await adminApi.logout()
  } catch {
    // Local logout still clears the demo session.
  }
  clearSession()
}

function clearSession() {
  clearAdminToken()
  token.value = ''
  me.value = null
}

async function handleSectionSelect(section: string) {
  if (section === 'admins' && !isBoss.value) {
    ElMessage.warning('只有 BOSS 角色可以管理管理员')
    return
  }
  activeSection.value = section as SectionKey
  await loadCurrentSection()
}

async function loadCurrentSection() {
  if (activeSection.value === 'leads') {
    await loadLeads()
  } else if (activeSection.value === 'assets') {
    await loadAssets()
  } else if (activeSection.value === 'articles') {
    await loadArticles()
  } else if (activeSection.value === 'cases') {
    await loadCases()
  } else if (activeSection.value === 'admins' && isBoss.value) {
    await loadAdminUsers()
  }
}

function buildLeadQuery(includePage = true) {
  return {
    keyword: leadFilters.keyword,
    leadType: leadFilters.leadType,
    debtType: leadFilters.debtType,
    viewedWechatQr: leadFilters.viewedWechatQr === '' ? undefined : leadFilters.viewedWechatQr === 'true',
    startTime: leadFilters.startTime,
    endTime: leadFilters.endTime,
    page: includePage ? leadFilters.page : undefined,
    pageSize: includePage ? leadFilters.pageSize : undefined
  }
}

async function loadLeads() {
  leadLoading.value = true
  try {
    const result = await adminApi.leads(buildLeadQuery())
    leadRows.value = result.list
    leadTotal.value = result.total
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    leadLoading.value = false
  }
}

function searchLeads() {
  leadFilters.page = 1
  loadLeads()
}

function resetLeadFilters() {
  leadFilters.keyword = ''
  leadFilters.leadType = ''
  leadFilters.debtType = ''
  leadFilters.viewedWechatQr = ''
  leadFilters.startTime = ''
  leadFilters.endTime = ''
  leadFilters.page = 1
  loadLeads()
}

async function handleExportLeads() {
  exportingLeads.value = true
  try {
    const result = await adminApi.exportLeads({
      filters: buildLeadQuery(false),
      fields: exportFields.value
    })
    downloadBlob(result.blob, result.filename)
    ElMessage.success('导出完成')
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    exportingLeads.value = false
  }
}

async function openLeadHistory(row: LeadListItem) {
  historyDrawerVisible.value = true
  historyLoading.value = true
  leadHistory.value = null
  try {
    leadHistory.value = await adminApi.leadHistory(row.customerId)
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    historyLoading.value = false
  }
}

async function loadAssets() {
  assetLoading.value = true
  try {
    assets.value = await adminApi.assets()
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    assetLoading.value = false
  }
}

async function uploadAsset(row: AssetResource, option: { file: File }) {
  try {
    const category = row.assetKey === 'HOME_VIDEO' ? 'VIDEO' : 'IMAGE'
    const uploadResult = await adminApi.uploadFile(option.file, category)
    await adminApi.updateAsset(row.assetKey, {
      title: row.title,
      fileUrl: uploadResult.url,
      originalFileName: uploadResult.originalFileName,
      mimeType: uploadResult.mimeType,
      fileSize: uploadResult.fileSize
    })
    await loadAssets()
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  }
}

function handleAssetUpload(row: AssetResource) {
  return (option: { file: File }) => uploadAsset(row, option)
}

function openAssetEdit(row: AssetResource) {
  assetForm.assetKey = row.assetKey
  assetForm.title = row.title
  assetDialogVisible.value = true
}

async function saveAssetTitle() {
  const row = assets.value.find((item) => item.assetKey === assetForm.assetKey)
  if (!row) {
    return
  }
  try {
    await adminApi.updateAsset(row.assetKey, {
      title: assetForm.title,
      fileUrl: row.fileUrl,
      originalFileName: row.originalFileName,
      mimeType: row.mimeType,
      fileSize: row.fileSize
    })
    assetDialogVisible.value = false
    await loadAssets()
    ElMessage.success('已保存')
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  }
}

async function loadArticles() {
  articleLoading.value = true
  try {
    const result = await adminApi.articles(articleQuery)
    articles.value = result.list
    articleTotal.value = result.total
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    articleLoading.value = false
  }
}

function openArticleCreate() {
  Object.assign(articleForm, {
    id: undefined,
    title: '',
    coverUrl: '',
    summary: '',
    contentText: '',
    sortOrder: 0
  })
  articleDialogVisible.value = true
}

function openArticleEdit(row: ArticleItem) {
  Object.assign(articleForm, row)
  articleDialogVisible.value = true
}

async function saveArticle() {
  if (!articleForm.title) {
    ElMessage.warning('请填写资讯标题')
    return
  }
  articleSaving.value = true
  try {
    const payload = {
      title: articleForm.title,
      coverUrl: articleForm.coverUrl,
      summary: articleForm.summary,
      contentText: articleForm.contentText,
      sortOrder: articleForm.sortOrder || 0
    }
    if (articleForm.id) {
      await adminApi.updateArticle(articleForm.id, payload)
    } else {
      await adminApi.createArticle(payload)
    }
    articleDialogVisible.value = false
    await loadArticles()
    ElMessage.success('已保存')
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    articleSaving.value = false
  }
}

async function publishArticle(id: number) {
  await runAndRefresh(() => adminApi.publishArticle(id), loadArticles)
}

async function offlineArticle(id: number) {
  await runAndRefresh(() => adminApi.offlineArticle(id), loadArticles)
}

async function deleteArticle(id: number) {
  await confirmAndRun('确定删除这条资讯吗？', () => adminApi.deleteArticle(id), loadArticles)
}

async function loadCases() {
  caseLoading.value = true
  try {
    const result = await adminApi.cases(caseQuery)
    cases.value = result.list
    caseTotal.value = result.total
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    caseLoading.value = false
  }
}

function openCaseCreate() {
  Object.assign(caseForm, {
    id: undefined,
    displayName: '',
    maskedPhone: '',
    overduePlatforms: '',
    overdueAmount: 0,
    handlingPlan: '',
    detailText: '',
    sortOrder: 0
  })
  caseDialogVisible.value = true
}

function openCaseEdit(row: SuccessCaseItem) {
  Object.assign(caseForm, row)
  caseDialogVisible.value = true
}

async function saveCase() {
  if (!caseForm.displayName) {
    ElMessage.warning('请填写展示姓名')
    return
  }
  caseSaving.value = true
  try {
    const payload = {
      displayName: caseForm.displayName,
      maskedPhone: caseForm.maskedPhone,
      overduePlatforms: caseForm.overduePlatforms,
      overdueAmount: caseForm.overdueAmount || 0,
      handlingPlan: caseForm.handlingPlan,
      detailText: caseForm.detailText,
      sortOrder: caseForm.sortOrder || 0
    }
    if (caseForm.id) {
      await adminApi.updateCase(caseForm.id, payload)
    } else {
      await adminApi.createCase(payload)
    }
    caseDialogVisible.value = false
    await loadCases()
    ElMessage.success('已保存')
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    caseSaving.value = false
  }
}

async function publishCase(id: number) {
  await runAndRefresh(() => adminApi.publishCase(id), loadCases)
}

async function offlineCase(id: number) {
  await runAndRefresh(() => adminApi.offlineCase(id), loadCases)
}

async function deleteCase(id: number) {
  await confirmAndRun('确定删除这个案例吗？', () => adminApi.deleteCase(id), loadCases)
}

async function loadAdminUsers() {
  if (!isBoss.value) {
    return
  }
  adminUserLoading.value = true
  try {
    adminUsers.value = await adminApi.adminUsers()
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    adminUserLoading.value = false
  }
}

function openAdminCreate() {
  Object.assign(adminForm, {
    id: undefined,
    username: '',
    password: '',
    displayName: '',
    role: 'ADMIN',
    status: 'ACTIVE'
  })
  adminDialogVisible.value = true
}

function openAdminEdit(row: AdminUserItem) {
  Object.assign(adminForm, {
    id: row.id,
    username: row.username,
    password: '',
    displayName: row.displayName,
    role: row.role,
    status: row.status
  })
  adminDialogVisible.value = true
}

async function saveAdminUser() {
  adminSaving.value = true
  try {
    if (adminForm.id) {
      await adminApi.updateAdminUser(adminForm.id, {
        displayName: adminForm.displayName,
        role: adminForm.role,
        status: adminForm.status
      })
    } else {
      await adminApi.createAdminUser({
        username: adminForm.username,
        password: adminForm.password,
        displayName: adminForm.displayName,
        role: adminForm.role
      })
    }
    adminDialogVisible.value = false
    await loadAdminUsers()
    ElMessage.success('已保存')
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    adminSaving.value = false
  }
}

async function resetAdminPassword(row: AdminUserItem) {
  try {
    const result = await ElMessageBox.prompt(`为 ${row.username} 设置新密码`, '重置密码', {
      confirmButtonText: '确认重置',
      cancelButtonText: '取消',
      inputType: 'password',
      inputPattern: /^.{6,50}$/,
      inputErrorMessage: '密码长度需为 6-50 位'
    })
    await adminApi.resetAdminPassword(row.id, result.value)
    ElMessage.success('密码已重置')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(getErrorMessage(error))
    }
  }
}

async function confirmAndRun(message: string, action: () => Promise<unknown>, refresh: () => Promise<void>) {
  try {
    await ElMessageBox.confirm(message, '请确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await runAndRefresh(action, refresh)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(getErrorMessage(error))
    }
  }
}

async function runAndRefresh(action: () => Promise<unknown>, refresh: () => Promise<void>) {
  try {
    await action()
    await refresh()
    ElMessage.success('操作成功')
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  }
}

function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

function resolveFileUrl(fileUrl?: string) {
  if (!fileUrl) {
    return ''
  }
  if (fileUrl.startsWith('http')) {
    return fileUrl
  }
  return `http://localhost:8080${fileUrl}`
}

function displayRowType(rowType?: string) {
  return rowType === 'SUBMITTED' ? '已提交' : '仅登录'
}

function displaySource(source?: string) {
  const map: Record<string, string> = {
    AI_CHAT: 'AI聊天',
    PLAN_ASSESSMENT: '规划表单',
    HOME_CTA: '首页按钮'
  }
  return source ? map[source] || source : '-'
}

function displayStatus(status?: string) {
  return status === 'PUBLISHED' ? '已发布' : '草稿'
}

function formatDateTime(value?: string) {
  if (!value) {
    return '-'
  }
  return value.replace('T', ' ').slice(0, 19)
}

function formatAmount(value?: number) {
  if (value === undefined || value === null) {
    return '-'
  }
  return `${Number(value).toLocaleString('zh-CN')} 元`
}

function formatFileSize(value?: number) {
  if (!value) {
    return ''
  }
  if (value < 1024 * 1024) {
    return `${Math.round(value / 1024)}KB`
  }
  return `${(value / 1024 / 1024).toFixed(1)}MB`
}

function leadTitle(item: LeadHistoryItem) {
  return `${formatDateTime(item.createdAt)} ${displaySource(item.source)}`
}

function getErrorMessage(error: unknown) {
  if (error instanceof Error) {
    return error.message
  }
  return '操作失败，请稍后重试'
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f6f8;
}

.login-card {
  width: 420px;
  border-radius: 8px;
}

.login-title {
  color: #17181c;
  font-size: 28px;
  font-weight: 800;
  text-align: center;
}

.login-subtitle {
  color: #8b9099;
  margin: 8px 0 28px;
  text-align: center;
}

.login-button {
  width: 100%;
}

.admin-shell {
  min-height: 100vh;
  background: #f5f6f8;
}

.admin-aside {
  background: #ffffff;
  border-right: 1px solid #e8e9ee;
}

.admin-logo {
  height: 72px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  border-bottom: 1px solid #f0f1f4;
}

.admin-logo strong,
.admin-logo span {
  display: block;
}

.admin-logo strong {
  color: #15171c;
  font-size: 18px;
}

.admin-logo span {
  color: #8b9099;
  font-size: 12px;
  margin-top: 4px;
}

.logo-mark {
  width: 38px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-weight: 800;
  background: #f4524a;
  border-radius: 8px;
}

.admin-menu {
  border-right: 0;
}

.admin-header {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  background: #ffffff;
  border-bottom: 1px solid #e8e9ee;
}

.admin-header h1 {
  margin: 0;
  color: #15171c;
  font-size: 22px;
}

.admin-header p {
  margin: 6px 0 0;
  color: #8b9099;
  font-size: 13px;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #50545f;
}

.admin-main {
  padding: 24px;
}

.panel {
  min-height: calc(100vh - 120px);
  padding: 20px;
  background: #ffffff;
  border: 1px solid #eceef2;
  border-radius: 8px;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.toolbar.right {
  justify-content: flex-end;
}

.toolbar .el-input,
.toolbar .el-select,
.toolbar .el-date-editor {
  width: 180px;
}

.export-bar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 16px;
  margin-bottom: 16px;
  background: #fafafa;
  border: 1px solid #f0f1f4;
  border-radius: 8px;
}

.export-fields {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.export-fields > span {
  color: #60646f;
  font-weight: 700;
  line-height: 32px;
  white-space: nowrap;
}

.section-alert {
  margin-bottom: 16px;
}

.inline-upload {
  display: inline-block;
  margin-right: 8px;
}

.muted {
  color: #90949f;
}

.block {
  display: block;
  margin-top: 4px;
}

.pagination {
  margin-top: 18px;
  justify-content: flex-end;
}

.drawer-title {
  margin: 22px 0 12px;
  color: #20232a;
  font-size: 16px;
}
</style>
