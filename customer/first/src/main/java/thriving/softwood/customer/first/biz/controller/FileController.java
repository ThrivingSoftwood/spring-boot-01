package thriving.softwood.customer.first.biz.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import thriving.softwood.common.core.result.Result;
import thriving.softwood.common.file.api.FileStorageApi;

/**
 * 文件上传与预览入口
 *
 * @author CodeOmni
 */
@RestController
@RequestMapping("/cust0001/file")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final FileStorageApi fileApi;

    public FileController(FileStorageApi fileApi) {
        this.fileApi = fileApi;
    }

    /**
     * ⬆️ 1. 上传文件
     *
     * @param file 前端表单的 name 属性必须是 "file"
     * @return 文件的虚拟访问路径
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        String fileName = fileApi.upload(file);

        // 组合成前端可直接访问的 URL 路径
        // 注意：前端拼接完整路径时会自动带上当前服务器地址或经过 Proxy
        String fileUrl = "/api/cust0001/file/preview/" + fileName;

        logger.info("文件上传成功: {}", fileUrl);
        return Result.success(fileUrl);
    }

    /**
     * 👁️ 2. 文件预览/下载 使用 spring 资源映射处理,安全可靠,代码不会出现漏洞
     */
}