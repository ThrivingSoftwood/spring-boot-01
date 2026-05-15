package thriving.softwood.customer.first.infrastructure.db.kaishi2026.entity.base;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("Employee")
public class Employee implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("typeId")
    private String typeId;

    @TableField("Parid")
    private String parid;

    @TableField("leveal")
    private Short leveal;

    @TableField("soncount")
    private Integer soncount;

    @TableField("sonnum")
    private Integer sonnum;

    @TableField("FullName")
    private String fullName;

    @TableField("Name")
    private String name;

    @TableField("UserCode")
    private String userCode;

    @TableField("Department")
    private String department;

    @TableField("Tel")
    private String tel;

    @TableField("Address")
    private String address;

    @TableField("Comment")
    private String comment;

    @TableField("deleted")
    private Boolean deleted;

    @TableField("Namepy")
    private String namepy;

    @TableField("Rec")
    private Integer rec;

    @TableField("ParRec")
    private Integer parRec;

    @TableField("Pay")
    private BigDecimal pay;

    @TableField("Incumbency")
    private Integer incumbency;

    @TableField("birthday")
    private String birthday;

    @TableField("OrderNo")
    private Integer orderNo;

    @TableField("creditupperlimit")
    private BigDecimal creditupperlimit;

    @TableField("yshouupperlimit")
    private BigDecimal yshouupperlimit;

    @TableField("ProduceType")
    private String produceType;

    @TableField("Account")
    private String account;

    @TableField("clerk")
    private Short clerk;

    @TableField("OnDutyDate")
    private String onDutyDate;

    @TableField("Sex")
    private String sex;

    @TableField("UpdateTag")
    private Integer updateTag;

    @TableField("Education")
    private String education;

    @TableField("ID")
    private String id;

    @TableField("Birthplace")
    private String birthplace;

    @TableField("CreateDate")
    private LocalDateTime createDate;
}
