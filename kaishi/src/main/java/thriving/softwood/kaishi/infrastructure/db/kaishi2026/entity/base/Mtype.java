package thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-06
 */
@Data
@NoArgsConstructor
@TableName("Mtype")
public class Mtype implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("Typeid")
    private String typeid;

    @TableField("parid")
    private String parid;

    @TableField("leveal")
    private Short leveal;

    @TableField("sonnum")
    private Integer sonnum;

    @TableField("soncount")
    private Integer soncount;

    @TableField("Rec")
    private Integer rec;

    @TableField("ParRec")
    private Integer parRec;

    @TableField("FullName")
    private String fullName;

    @TableField("UserCode")
    private String userCode;

    @TableField("deleted")
    private Boolean deleted;

    @TableField("NAMEPY")
    private String namepy;

    @TableField("OrderNo")
    private Integer orderNo;

    @TableField("remark")
    private String remark;

    @TableField("ZbLeiXing")
    private String zbLeiXing;

    @TableField("UpdateTag")
    private Integer updateTag;

    @TableField("PushRec")
    private Integer pushRec;

    @TableField("BeginDate")
    private String beginDate;

    @TableField("EndDate")
    private String endDate;

    @TableField("GrossInCome")
    private String grossInCome;

    @TableField("GrossOutCome")
    private String grossOutCome;

    @TableField("BtypeId")
    private String btypeId;

    @TableField("CustomerPerson")
    private String customerPerson;

    @TableField("CustomerTel")
    private String customerTel;

    @TableField("ContractNumber")
    private String contractNumber;

    @TableField("ContractTotal")
    private String contractTotal;

    @TableField("EtypeId")
    private String etypeId;

    @TableField("ProjectId")
    private String projectId;

    @TableField("TeamMembers")
    private String teamMembers;

    @TableField("PlanCreateTime")
    private LocalDateTime planCreateTime;

    @TableField("PlanUserOver")
    private Integer planUserOver;

    @TableField("PlanYearClose")
    private Integer planYearClose;

    @TableField("InputNo")
    private String inputNo;

    @TableField("CreateTime")
    private LocalDateTime createTime;

    @TableField("ModifyPerson")
    private String modifyPerson;

    @TableField("ModifyDate")
    private LocalDateTime modifyDate;
}
