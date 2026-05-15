package thriving.softwood.customer.first.biz.api.support;

import thriving.softwood.customer.first.biz.pojo.dto.DlyndxDTO;

public interface DictionaryApi {

    String getBtypeName(String typeId);

    String getEmployeeName(String typeId);

    String getStockName(String typeId);

    String getPtypeName(String typeId);

    String getVchName(Integer vchtype);

    String getDepartmentName(String typeId);

    String getMtypeName(String typeId);

    DlyndxDTO getDlyndxDTO(Long vchcode);

    String getEdongfangProductName(String sku);

    String getUsedtypeName(String code);

    String getPdetailName(Integer code);

    String getRedWordName(String code);

    String getRedOldName(String redOld, String redWord);
}
