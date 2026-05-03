package thriving.softwood.common.file.api;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageApi {
    /** 上传并返回相对访问路径 */
    String upload(MultipartFile file);
}