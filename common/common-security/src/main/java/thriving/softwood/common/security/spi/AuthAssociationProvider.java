package thriving.softwood.common.security.spi;

import thriving.softwood.common.security.pojo.vo.SysDeptVO;

public interface AuthAssociationProvider {

    void associateDelete(Long deptId);

    SysDeptVO loadAssocInfo(SysDeptVO vo);

    void associateDelete(String loginAccount);
}
