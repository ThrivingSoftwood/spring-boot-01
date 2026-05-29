package thriving.softwood.kaishi.biz.pojo.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import thriving.softwood.common.security.pojo.vo.SysDeptVO;

/**
 * <p>
 * 
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-15
 */
@Data
public class KaishiSysDeptVO extends SysDeptVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String oriDepartmentTypeid;

    // 🌟 核心：子节点集合，用于前端 Element Plus 的树形表格渲染
    private List<SysDeptVO> children = new ArrayList<>();

    public KaishiSysDeptVO(SysDeptVO vo, String oriDepartmentTypeid) {
        super(vo);
        this.oriDepartmentTypeid = oriDepartmentTypeid;
    }
}
