package thriving.softwood.kaishi.biz.pojo.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysPermission;

/**
 * 带有子节点集合的树形 VO 对象
 */
@Data
public class SysPermissionVO {
    private Long id;
    private Long parentId;
    private String permissionName;
    private String permissionCode;
    private Byte permissionType;
    private String path;
    private String component;
    private String icon;
    private Integer sortOrder;

    // 🌟 核心：子节点集合，用于前端 Element Plus 的树形表格渲染
    private List<SysPermissionVO> children = new ArrayList<>();

    // 构造函数：Entity 转 VO
    public SysPermissionVO(SysPermission entity) {
        id = entity.getId();
        parentId = entity.getParentId();
        permissionName = entity.getPermissionName();
        permissionCode = entity.getPermissionCode();
        permissionType = entity.getPermissionType();
        path = entity.getPath();
        component = entity.getComponent();
        icon = entity.getIcon();
        sortOrder = entity.getSortOrder();
    }
}