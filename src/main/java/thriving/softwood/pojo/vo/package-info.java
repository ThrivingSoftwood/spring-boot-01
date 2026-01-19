/**
 *@formatter:off
 * 视图对象
 * 什么时候必须把 VO 独立出来？(讲究模式) 🛡️
 * 当出现以下需求时，DTO 就扛不住了，必须引入 VO：
 *     场景 A：数据脱敏 (Masking)
 *         DTO (内部传输): 需要包含完整的手机号，因为 Service 层可能要发短信。
 *         VO (给前端看): 必须显示 138****8888。
 *         冲突： 如果你直接返 DTO，前端稍微抓个包就能看到完整手机号，这是安全事故。你必须造一个 UserVO，把手机号字段处理后放进去。
 *     场景 B：字段差异 (视图特供字段)
 *         需求： 这是一个博客列表页。
 *         DTO: 包含 content (文章全文，可能 10KB)。
 *         VO: 列表页只需要 summary (摘要，前 50 字) 和 readTime (阅读时长，比如 "5 min read")。
 *         冲突： 此时 DTO 数据太重，且没有计算属性。你需要一个轻量级的 ArticleListVO。
 *     场景 C：多端适配 (Mobile vs PC)
 *         DTO: 是统一的业务数据。
 *         MobileVO: 手机屏幕小，只展示 3 个核心字段。
 *         PcVO: 电脑屏幕大，展示 20 个详细字段。
 *         冲突： 一个 DTO 无法同时满足两个端的“口味”，需要拆分不同的 VO。
 */


package thriving.softwood.pojo.vo;