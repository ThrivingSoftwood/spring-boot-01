package thriving.softwood.kaishi.infrastructure.db.kaishi2026.repo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import thriving.softwood.kaishi.biz.pojo.dto.DlyndxDTO;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.Dlyndx;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.mapper.base.DlyndxMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-06
 */
@DS("kaishi-2026")
@Service
public class DlyndxRepo extends AncestorServiceImpl<DlyndxMapper, Dlyndx> {

    /**
     * 缓存时使用
     *
     * @return
     */
    public List<Dlyndx> listAllByVchcode() {
        LambdaQueryWrapper<Dlyndx> wrapper = Wrappers.lambdaQuery();
        wrapper.select(Dlyndx::getVchcode, Dlyndx::getNumber, Dlyndx::getSummary);
        return list(wrapper);
    }

    /**
     * 缓存时使用
     *
     * @return
     */
    public DlyndxDTO getDTOByVchcode(Long vchcode) {
        LambdaQueryWrapper<Dlyndx> wrapper = Wrappers.lambdaQuery();
        wrapper.select(Dlyndx::getVchcode, Dlyndx::getNumber, Dlyndx::getSummary);
        wrapper.eq(Dlyndx::getVchcode, vchcode);
        return new DlyndxDTO(getOne(wrapper));
    }

}
