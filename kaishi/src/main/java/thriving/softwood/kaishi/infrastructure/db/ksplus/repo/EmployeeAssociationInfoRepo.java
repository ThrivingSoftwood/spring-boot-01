package thriving.softwood.kaishi.infrastructure.db.ksplus.repo;

import thriving.softwood.kaishi.infrastructure.db.ksplus.entity.base.EmployeeAssociationInfo;
import thriving.softwood.kaishi.infrastructure.db.ksplus.mapper.base.EmployeeAssociationInfoMapper;
import thriving.softwood.common.database.ancestor.AncestorServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.dynamic.datasource.annotation.DS;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author meta-thriving
* @since 2026-04-16
*/
@DS("ksplus")
@Service
public class EmployeeAssociationInfoRepo extends AncestorServiceImpl<EmployeeAssociationInfoMapper, EmployeeAssociationInfo> {

}
