package thriving.softwood.common.database.consts;

public class MybatisPlusGenConst {

    public static final String SAMPLE_BASE_PACKAGE_NAME = "thriving.softwood.sample.infrastructure.db";

    public static final String KAISHI_BASE_PACKAGE_NAME = "thriving.softwood.kaishi.infrastructure.db";
    /**
     * 数据表映射类所在上层包名
     */
    public static final String ENTITY_PKG_NAME = "entity.base";
    /**
     * mapper 类所在上层包名
     */
    public static final String MAPPER_PKG_NAME = "mapper.base";
    /**
     * interface 类所在上层包名
     */
    public static final String REPO_PKG_NAME = "repo";
    /**
     * controller 类所在上层包名
     */
    public static final String CONTROLLER_PKG_NAME = "controllers";
    /**
     * 提示词模板名
     */
    public static final String TEMPLATE_001 = "小说章节文学分析提示词模板 - 0001";
    /**
     * Gemini 请求超限信息
     */
    public static final String GEMINI_EXCEED = "请求超限";
    /**
     * Gemini 请求超限代码
     */
    public static final Integer GEMINI_EXCEED_CODE = 429;

    public static final String[] KAISHI_2026_TABLE_NAMES =
        {"Btype", "Department", "DlyBuy", "Dlyndx", "Dlystock", "Employee", "Mtype", "ptype", "Stock", "T_GBL_Vchtype"};

    public static final String[] KAISHI_PLUS_TABLE_NAMES = {"purchase_manual_finish"};

    public static final String[] THRIVING_SOFTWOOD_AUTH_TABLE_NAMES = {"sys_dept", "sys_user", "sys_role",
        "sys_permission", "sys_data_rule", "sys_user_role", "sys_role_permission", "sys_role_data_rule"};

    /**
     * splash-inkflow 表名集合
     */
    public static final String[] MASTER_TABLE_NAMES = {"db_info", "ai_model", "app_info"};

    /**
     * weaving-stars 表名集合
     */
    public static final String[] NOVEL_TABLE_NAMES = {"theme", "writing_method", "world_construction_method",
        "outline_design_method", "genre", "school", "role_prototype", "plot_structure", "plot_prototype",
        "conflict_prototype", "suspense_prototype", "main_event_prototype", "unexpected_event_prototype",
        "key_scene_prototype", "framework", "role", "role_lifeline", "chapter_catalog", "chapter_content",
        "chapter_mainline_difference", "outline_difference", "spark", "weaving_flow", "weaving_flow_task"};

    public static final String[] MATERIAL_TABLE_NAMES = {"novel", "novel_world_setting", "novel_chapter",
        "novel_chapter_analyst", "novel_character", "template", "embedded_library"};

    public static final String[] EMBEDDING_TABLE_NAMES = {"data_docstore", "data_embeddings", "data_indexstore"};

}
