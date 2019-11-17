package com.czhwisdom;

import com.czhwisdom.oss.OssService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 文件变化监听器
 * <p>
 * 在Apache的Commons-IO中有关于文件的监控功能的代码. 文件监控的原理如下：
 * 由文件监控类FileAlterationMonitor中的线程不停的扫描文件观察器FileAlterationObserver，
 * 如果有文件的变化，则根据相关的文件比较器，判断文件时新增，还是删除，还是更改。（默认为1000毫秒执行一次扫描）
 */
@Slf4j
public class FileListener extends FileAlterationListenerAdaptor {

    private OssService ossService;
    private Config config;
    private String watchAbsolutePath;
    private String ossRootPath;

    public FileListener(Config config) {
        this.config = config;
        File watchPath = new File(config.getWatchPath());
        watchAbsolutePath = watchPath.getAbsolutePath();
        ossRootPath = StringUtils.endsWith(config.getAliyunPath(), "/") ? config.getAliyunPath().substring(0, config.getAliyunPath().length() - 1) : config.getAliyunPath();
        ossRootPath = StringUtils.startsWith(ossRootPath, "/") ? StringUtils.replace(ossRootPath, "/", "", 1) : ossRootPath;
        ossService = new OssService(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
    }

    /**
     * 文件创建执行
     */
    public void onFileCreate(File file) {
        log.info("[新建]:" + formartPath(file));
        uploadFile(file);
    }

    /**
     * 文件创建修改
     */
    public void onFileChange(File file) {
        log.info("[修改]:" + formartPath(file));
        uploadFile(file);
    }

    /**
     * 文件删除
     */
    public void onFileDelete(File file) {
        log.info("[删除]:" + formartPath(file));
        delateFile(file);
    }

    /**
     * 目录创建
     */
    public void onDirectoryCreate(File directory) {
        log.info("[新建目录]:" + formartPath(directory));
    }

    /**
     * 目录修改
     */
    public void onDirectoryChange(File directory) {
        log.info("[目录修改]:" + formartPath(directory));
    }

    /**
     * 目录删除
     */
    public void onDirectoryDelete(File directory) {
        log.info("[目录删除]:" + formartPath(directory));
    }

    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
    }

    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
    }

    private String formartPath(File file) {
        return StringUtils.replace(file.getAbsolutePath(), watchAbsolutePath, "", 1);
    }

    private void uploadFile(File file) {
        String path = formartPath(file);
        String ossPath = ossRootPath + path;
        try {
            ossService.putObject(config.getBucket(), ossPath, file);
            log.info("uploadFile to oss success...");
            StringBuilder urlSb = new StringBuilder("https://");
            urlSb.append(config.getBucket()).append(".").append(config.getEndpoint()).append("/")
                    .append(ossPath);

            log.info("markDown img url: ![]({})", urlSb);
        } catch (Exception e) {
            log.error("uploadFile ({}) to oss ({}) error", path, ossPath, e);
        }
    }

    private void delateFile(File file) {
        String path = formartPath(file);
        String ossPath = ossRootPath + path;
        try {
            ossService.delete(config.getBucket(), ossPath);
        } catch (Exception e) {
            log.error("delete oss file ({}) error ...", ossPath, e);
        }
    }

}