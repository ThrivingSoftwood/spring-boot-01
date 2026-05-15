package thriving.softwood.kaishi.infrastructure.db.kaishi2026.mapper.extend;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mapstruct.Mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import thriving.softwood.kaishi.biz.pojo.dto.DlyndxDTO;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-06
 */
@Mapper
@DS("kaishi-2026")
public interface DictionaryExtendMapper {

    @Select("select FullName from Employee where typeId = #{typeid}")
    String getEmployeeFullNameByTypeid(@Param("typeid") String typeid);

    @Select("select FullName from Btype where typeId = #{typeid}")
    String getBtypeFullNameByTypeid(@Param("typeid") String typeid);

    @Select("select FullName from Stock where typeId = #{typeid}")
    String getStockFullNameByTypeid(@Param("typeid") String typeid);

    @Select("select FullName from ptype where typeId = #{typeid}")
    String getPtypeFullNameByTypeid(@Param("typeid") String typeid);

    @Select("select fullname from T_GBL_Vchtype where vchtype = #{vchtype}")
    String getVchNameByVchtype(@Param("vchtype") Integer vchtype);

    @Select("select FullName from Department where typeId = #{typeid}")
    String getDepartmentFullNameByTypeid(@Param("typeid") String typeid);

    @Select("select FullName from Mtype where typeId = #{typeid}")
    String getMtypeFullNameByTypeid(@Param("typeid") String typeid);

    @Select("select Vchcode as vchcode, NUMBER as number, summary as summary from Dlyndx where vchcode = #{vchcode}")
    public DlyndxDTO getDlyndxDTOByVchcode(@Param("vchcode") Long vchcode);

}