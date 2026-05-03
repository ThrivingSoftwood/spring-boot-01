package thriving.softwood.common.file.component.strategy;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.io.file.FileNameUtil;
import cn.hutool.v7.core.io.file.FileUtil;
import thriving.softwood.common.file.api.FileStorageApi;

@Service
public class LocalFileProvider implements FileStorageApi {

    @Value("${app.upload.dir:${user.dir}/uploads}")
    private String uploadDir;

    @Override
    public String upload(MultipartFile file) {
        String extName = FileNameUtil.extName(file.getOriginalFilename());
        String newFileName = IdUtil.fastSimpleUUID() + "." + extName;
        File targetFile = FileUtil.file(uploadDir, newFileName);
        FileUtil.mkParentDirs(targetFile);
        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            throw new RuntimeException("本地存储失败", e);
        }
        return newFileName;
    }
}