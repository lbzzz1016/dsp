<template>
  <div class="home-index">
    <div class="page-header">
      <!-- 位于首页的menu下的信息栏 -->
      <div class="header-content">
        <div class="left-content">
          <div class="user-info">
            <div class="title">{{ helloTime }}{{ userInfo.name }}，祝你开心每一天！</div>
          </div>
        </div>
        <div class="right-content">
          <div class="content-item">
            <img src="@/assets/logo/cetc.png"/>
          </div>
        </div>
      </div>
    </div>
    <!-- 信息栏下的项目和任务的简略展示 -->
    <!--<wrapper-content :showHeader="false">-->
    <div class="page-wrapper">
      <a-row :gutter="24">
        <!-- 项目/动态等在左侧显示的内容 -->
        <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">
          <!-- 公告组件 -->
          <a-card
            class="project-list"
            :loading="loading"
            style="margin-bottom: 24px"
            :bordered="false"
            title="公告"
            :body-style="{ padding: '0 0 0 24px' }"
          >
            <router-link style="color:rgba(11, 118, 241, 0.911)" slot="extra" to="/information/public_notice">全部公告</router-link>
            <div>
              <a-list>
                <a-list-item v-for="(item, index) in activities" :key="index">
                  <a-list-item-meta >
                    <div slot="title">
                      <a @click="handleDetail(item)"> {{ item.noticeTitle }} </a>
                    </div>
                    <div slot="description">
                      <span>作者：{{ item.createBy }} </span> <br>
                    </div>
                  </a-list-item-meta>
                </a-list-item>
              </a-list>
              <p v-if="activities.length == 0" class="muted text-center m-t-md m-b-md">暂无公告</p>
            </div>
          </a-card>
          <!-- 文件 组件 -->
          <a-card class="activities-list" :loading="loading" title="文件" :bordered="false">
            <router-link style="color:rgba(11, 118, 241, 0.911)" slot="extra" to="/information/sharedfile">全部文件</router-link>
            <div class="list-content">
            <a-tabs style="justify-content:space-between" default-active-key="rule" :animated="false" @change="fileTabChange">
              <a-tab-pane key="rule">
                <span slot="tab"><a-icon type="bars" />规章制度</span>
              </a-tab-pane>
              <a-tab-pane key="work">
                <span slot="tab"><a-icon type="bars" />办公文件</span>
              </a-tab-pane>
              <a-tab-pane key="learning">
                <span slot="tab"><a-icon type="bars" />学习文件</span>
              </a-tab-pane>
              <a-tab-pane key="others">
                <span slot="tab"><a-icon type="bars" />其他</span>
              </a-tab-pane>
            </a-tabs>
            <a-list
              :loading="loading"
            >

              
              <a-list-item class="list-item-title">
                <a-list-item-meta>
                  <div slot="title" class="muted">
                    名称
                  </div>
                </a-list-item-meta>
                <div class="other-info muted">
                  <!-- <div class="info-item">
                    <span>大小</span>
                  </div> -->
                  <div class="info-item">
                    <span>创建日期</span>
                  </div>
                  <div class="info-item">
                    <span>创建人</span>
                  </div>
                </div>
                <span v-for="item in 3" slot="actions" :key="item">
                  <span>  </span>
                </span>
              </a-list-item>
              <a-list-item v-for="(item, index) in files" :key="index" class="list-item">
                <a-list-item-meta>
                  <a-avatar slot="avatar" shape="square" icon="link" />
                  <div slot="title">
                    <a-tooltip :mouse-enter-delay="0.3">
                      <template slot="title">
                        <span>{{ item.fullName }}</span>
                      </template>
                      <div
                        v-show="!item.editing"
                        class="text-default"
                        
                      >{{ item.fullName }}</div>
                    </a-tooltip>
                  </div>
                </a-list-item-meta>
                <div class="other-info muted">
                  <!-- <div class="info-item">
                    <span>{{ (formatSize(item.fsize)) }}</span>
                  </div> -->
                  <div class="info-item">
                    <a-tooltip :title="item.createTime">
                      <span>{{ formatTime(item.createTime) }}</span>
                    </a-tooltip>
                  </div>
                  <div class="info-item">
                    <span>{{ item.creatorName }}</span>
                  </div>
                </div>
                <span slot="actions">
                  <a-tooltip title="下载">
                    <a class="muted" target="_blank" @click="handleDownload(item.fileUrl)"><a-icon type="download" /></a>
                  </a-tooltip>
                </span>
              </a-list-item>
            </a-list>
            <p v-if="activities.length == 0" class="muted text-center m-t-md m-b-md">暂无文件</p>
          </div>
          </a-card>
        </a-col>
        <!-- 我的任务 等组件在右侧显示的内容 -->
        <a-col
          style="padding: 0 12px"
          :xl="12"
          :lg="24"
          :md="24"
          :sm="24"
          :xs="24"
        >
          <a-card
            class="tasks-list"
            style="margin-bottom: 24px"
            :bordered="false"
          >
            <div slot="title">
              <div class="flex ant-row-flex-space-between ant-row-flex-middle">
                <span>我的项目 · {{ task.total }}</span>
                <a-select v-model="task.done" :default-active-first-option="false" @select="taskSelectChange">
                  <a-select-option :key="0">未完成</a-select-option>
                  <a-select-option :key="1">已完成</a-select-option>
                </a-select>
              </div>
            </div>
            <a-tabs default-active-key="1" :animated="false" @change="taskTabChange">
              <a-tab-pane key="1">
                <span slot="tab"><a-icon type="bars" />我执行的</span>
              </a-tab-pane>
              <a-tab-pane key="2">
                <span slot="tab"><a-icon type="team" />我参与的</span>
              </a-tab-pane>
              <a-tab-pane key="3">
                <span slot="tab"><a-icon type="rocket" />我创建的</span>
              </a-tab-pane>
            </a-tabs>
            <a-list :loading="task.loading">
              <a-list-item v-for="(item, index) in task.list" :key="index">
                <a-list-item-meta>
                  <div slot="title">
                    <div style="display: flex;justify-content: space-between ">
                      <router-link
                        class="task-title-wrap"
                        :to="`/project/space/task/${item.projectInfo.code}/detail/${item.code}`"
                      >
                        <a-tooltip title="优先级">
                          <a-tag :color="priColor(item.pri)">{{ item.priText }}</a-tag>
                        </a-tooltip>
                        <a-tooltip :title="item.name">
                          {{ item.name }}
                        </a-tooltip>
                        <!-- <a-tooltip v-if="item.end_time" title="任务开始 - 截止时间">
                          <span class="label m-r-sm" :class="showTimeLabel(item.end_time)">{{ showTaskTime(item.begin_time, item.end_time) }}</span>
                        </a-tooltip> -->
                        <a-tooltip v-if="item.end_time" title="任务截止时间">
                          <span class="label m-r-sm" :class="showTimeLabel(item.end_time)">{{ showTaskTime(null, item.end_time) }}</span>
                        </a-tooltip>
                      </router-link>
                      <div>
                        <a-tooltip v-if="item.pcode" title="子任务">
                          <a-icon type="cluster" class="m-r-sm muted" />
                        </a-tooltip>
                        <router-link class="muted" :to="'/project/space/task/' + item.projectInfo.code">
                          <a-tooltip title="所属项目">{{ item.projectInfo.name }}</a-tooltip>
                        </router-link>
                      </div>
                    </div>
                  </div>
                </a-list-item-meta>
                <p v-if="activities.length == 0" class="muted text-center m-t-md m-b-md">暂无项目</p>

              </a-list-item>
            </a-list>
            <a-pagination v-model="task.page" class="pull-right m-b" size="small" :default-page-size="task.pageSize" :total="task.total" @change="onLoadMoreTask" />
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!--</wrapper-content>-->
    <!-- 公告详情弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="50%" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="公告标题" prop="noticeTitle">
              <el-input :readonly = "true" v-model="form.noticeTitle" placeholder="请输入公告标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告类型" prop="noticeType">
              <el-select :disabled = "true" v-model="form.noticeType" placeholder="请选择公告类型">
                <el-option
                  v-for="dict in dict.type.sys_notice_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="内容">
              <editor :readOnly = "true" v-model="form.noticeContent" :min-height="350"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>
  </div>

</template>
<script>
import {edit, list, recycle, download } from "@/api/system/file";
import pagination from '@/mixins/pagination'
import { mapState } from 'vuex'
import moment from 'moment'
import { 
  formatTaskTime, 
  relativelyTime, 
  showHelloTime, 
  relativelyTaskTime 
} from '@/assets/js/dateTime'
import {
  getStore
} from '@/assets/js/storage'
/**
 * 后端接口
 */
import {
  selfList
} from '@/api/task'
import { 
  getNotice, 
  listNotice 
} from "@/api/system/notice";

export default {
  name: "Notice",
  dicts: ['sys_notice_status', 'sys_notice_type'],
  components: {},
  mixins: [pagination],
  data() {
    return {
      loading: false,
      activities: [],
      task: {
        list: [],
        taskType: '1',
        done: 0,
        total: 0,
        page: 1,
        pageSize: 10,
        loading: false
      },
      // 拉取文件
      files:[],
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 公告表格数据
      noticeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        noticeTitle: undefined,
        createBy: undefined,
        status: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        noticeTitle: [
          { required: true, message: "公告标题不能为空", trigger: "blur" }
        ],
        noticeType: [
          { required: true, message: "公告类型不能为空", trigger: "change" }
        ]
      },
      userInfo: []
    }
  },
  computed: {
    ...mapState({
      // userInfo: state => state.userInfo,
      socketAction: state => state.socketAction
    }),
    helloTime() {
      return showHelloTime()
    }
  },
  watch: {
    $route: function(to, from) {
      this.init()
    },
    socketAction(val) {
      if (val.action === 'organization:task') {
        this.init(false, false)
      }
    }
  },
  created() {
    this.init()
    this.userInfo = getStore('userInfo', true)
  },
  methods: {
    init(reset = true, loading = true) {
      if (reset) {
        this.projectList = []
        this.pagination.page = 1
        this.pagination.pageSize = 10
      }
      this.getTasks()
      this.getTaskLog()
      this.getFiles()
    },
    // 表单重置
    reset() {
      this.form = {
        noticeId: undefined,
        noticeTitle: undefined,
        noticeType: undefined,
        noticeContent: undefined,
        status: "0"
      };
      // this.resetForm("form");
    },
    /** 查看按钮操作 */
    handleDetail(row) {
      this.reset();
      const noticeId = row.noticeId // || this.ids
      getNotice(noticeId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "公告详情";
      });
    },
    getTaskLog() {
      listNotice(this.queryParams).then(res => {
        this.activities = res.rows
      })
    },
    getTasks() {
      this.task.loading = true
      selfList({ page: this.task.page, pageSize: this.task.pageSize, taskType: this.task.taskType, type: this.task.done }).then(res => {
        this.task.loading = false
        this.task.list = res.data.list
        // this.task.list =  this.task.list.concat(res.data.list);;
        this.task.total = res.data.total
      })
    },
    taskTabChange(key) {
      this.task.taskType = key
      this.task.loadingMore = true
      this.task.page = 1
      this.getTasks()
    },
    taskSelectChange(value) {
      this.task.done = value
      this.task.loadingMore = true
      this.task.page = 1
      this.getTasks()
    },
    onLoadMoreTask(page, PageSize) {
      this.task.loadingMore = true
      this.task.page = page
      this.getTasks()
    },
    onLoadMoreAccounts(page, PageSize) {
      this.accounts.loadingMore = true
      this.accounts.page = page
      this.getAccountList()
    },
    priColor(pri) {
      switch (pri) {
        case 1:
          return '#ff9900'
        case 2:
          return '#ed3f14'
        default:
          return 'green'
      }
    },
    formatData(data) {
      return relativelyTaskTime(data)
    },
    formatTime(time) {
      return relativelyTime(time)
    },
    showTaskTime(time, timeEnd) {
      return formatTaskTime(time, timeEnd)
    },
    showTimeLabel(time) {
      let str = 'label-primary'
      if (time == null) {
        return str
      }
      const cha = moment(moment(time).format('YYYY-MM-DD')).diff(moment().format('YYYY-MM-DD'), 'days')
      if (cha < 0) {
        str = 'label-danger'
      } else if (cha == 0) {
        str = 'label-warning'
      } else if (cha > 7) {
        str = 'label-normal'
      }
      return str
    },
    handleDownload(url) {
      var string = url.split("?")[1].split("&")
      var params = []
      params.filePathName = string[0].split("=")[1]
      params.realFileName = string[1].split("=")[1]
      download(params)
    },
    formatTime(time) {
      return relativelyTime(time)
    },
    formatSize(size) {
      let type = 'KB'
      size = size / 1024
      if (size >= 1024) {
        size /= 1024
        type = 'MB'
      }
      return `${size.toFixed(2)} ${type}`
    },
    getFiles(fileType="rule", reset = true) {
      const app = this
      if (reset) {
        this.pagination.page = 1
        this.pagination.pageSize = 5
      }
      app.requestData.projectCode = this.code
      app.requestData.deleted = 0
      app.requestData.fileType = fileType
      list(app.requestData).then(res => {
        if (reset) {
          this.files = []
        }
        res.data.list.forEach((v) => {
          v.editing = false
        })
        app.files = app.files.concat(res.data.list)
        app.pagination.total = res.data.total
        app.loading = false
        app.loadingMore = false
      })
    },
    fileTabChange(key) {
      this.getFiles(key)
    }
  }
}
</script>
<style lang="less">
    .ant-card-head-title {
      font-size: 18px;
      font-weight: bold;
    }

    .home-index {
        .page-header {
            .header-content {
                margin-bottom: 16px;
                display: flex;
                justify-content: space-between;
                .left-content {
                    display: flex;
                    align-items: center;
                    .user-info {
                        margin-left: 12px;
                        line-height: 33px;

                        .title {
                            font-size: 20px;
                            margin: 0;
                        }
                    }
                }
                .right-content {
                    display: flex;
                    align-items: center;
                    .content-item {
                        padding: 0 32px;
                        position: relative;
                        .item-text {
                            color: rgba(11, 118, 241, 0.911);
                            font-size: 20px;
                            .small {
                                font-size: 20px;
                            }
                        }
                        &:after {
                            background-color: #e8e8e8;
                            position: absolute;
                            top: 8px;
                            right: 0;
                            width: 1px;
                            height: 40px;
                            content: "";
                        }

                        &:last-child {
                            &:after {
                                width: 0;
                            }
                        }
                    }
                }
            }
        }

        .page-wrapper {
            margin: 24px;

            .project-list {

                .card-title {
                    font-size: 0;

                    a {
                        color: rgba(0, 0, 0, 0.85);
                        margin-left: 12px;
                        line-height: 24px;
                        height: 24px;
                        display: inline-block;
                        vertical-align: top;
                        font-size: 14px;

                        &:hover {
                            color: #1890ff;
                        }
                    }
                }

                .card-description {
                    color: rgba(0, 0, 0, 0.45);
                    height: 44px;
                    line-height: 22px;
                    overflow: hidden;
                    .description-text{
                        height: 22px;
                    }
                }

                .project-item {
                    display: flex;
                    margin-top: 8px;
                    overflow: hidden;
                    font-size: 12px;
                    height: 20px;
                    line-height: 20px;

                    a {
                        color: rgba(0, 0, 0, 0.45);
                        display: inline-block;
                        flex: 1 1 0;

                        &:hover {
                            color: #1890ff;
                        }
                    }

                    .datetime {
                        color: rgba(0, 0, 0, 0.25);
                        flex: 0 0 auto;
                        float: right;
                    }
                }

                .ant-card-meta-description {
                    color: rgba(0, 0, 0, 0.45);
                    height: 44px;
                    line-height: 22px;
                    overflow: hidden;
                }
            }

            .activities-list {
                .ant-card-body {
                  padding: 0px;
                }

                .ant-list-item-meta-title {
                    position: relative;
                }

                .comment-text {
                    margin-bottom: 0;
                }

                .right-item {
                    float: right;
                    position: absolute;
                    right: 0;
                    top: 0;
                }


                .list-content {
                        .list-item-title {
                            padding: 10px 20px;

                            .ant-list-item-action {
                                li {
                                    color: #fff;
                                }

                                em {
                                    width: 0;
                                }
                            }
                        }
                        .list-item {
                            border-bottom: none;
                            margin-bottom: 2px;
                            /*border-bottom: 1px solid #f5f5f5;*/
                            padding: 10px 20px;
                            transition: background-color 218ms;
                            &:hover {
                                background-color: #f5f5f5;
                            }
                            .ant-list-item-meta-title {
                                overflow: hidden;
                                text-overflow: ellipsis;
                                white-space: nowrap;
                                position: relative;
                                margin-bottom: 0;
                                line-height: 32px;
                            }
                            .ant-list-item-action {
                                em {
                                    width: 0;
                                }
                            }
                        }
                        .other-info {
                            display: flex;

                            .info-item {
                                display: flex;
                                flex-direction: column;
                                padding-left: 0;
                                width: 120px;
                                text-align: right;
                            }
                            .schedule {
                                width: 250px;
                            }
                        }
                    }


            }

            .tasks-list {
                .ant-card-body {
                    padding: 6px 24px;

                    .ant-list-item-meta, .ant-list-item-meta-content{
                        width: 100%;
                    }

                    .task-title-wrap{
                        /*max-width: 310px;*/
                        flex: 2;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        padding-right: 10px;
                    }
                }
            }

            .item-group {
                padding: 20px 0 8px 24px;
                font-size: 0;

                a {
                    color: rgba(0, 0, 0, 0.65);
                    display: inline-block;
                    font-size: 14px;
                    margin-bottom: 13px;
                    width: 25%;
                }
            }

            .members {
                a {
                    display: block;
                    margin: 12px 0;
                    line-height: 24px;
                    height: 24px;

                    .member {
                        font-size: 14px;
                        color: rgba(0, 0, 0, .65);
                        line-height: 24px;
                        max-width: 100px;
                        vertical-align: top;
                        margin-left: 6px;
                        transition: all 0.3s;
                        display: inline-block;
                    }

                    &:hover {
                        span {
                            color: #1890ff;
                        }
                    }
                }
            }
        }
    }
</style>
