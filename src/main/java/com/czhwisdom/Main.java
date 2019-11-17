package com.czhwisdom;

import com.czhwisdom.oss.OssService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.yaml.snakeyaml.Yaml;

import java.io.File;

@Slf4j
public class Main {
    public static void main(String[] args) throws Exception {
        File file = new File("oss_sync.yml");
        Yaml yaml = new Yaml();
        Config config = yaml.loadAs(FileUtils.readFileToString(file, "UTF-8"), Config.class);
        log.info("start to watch path :{}", config.getWatchPath());
        // 监控目录
        String rootDir = config.getWatchPath();
        // 轮询间隔 500 毫秒
        long interval = 500;

        // 创建过滤器
        IOFileFilter directories = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.prefixFileFilter("."));
        IOFileFilter files       = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),FileFilterUtils.prefixFileFilter("."));
        IOFileFilter filter = FileFilterUtils.or(directories, files);
        filter = FileFilterUtils.notFileFilter(filter);
//        IOFileFilter filter       = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),FileFilterUtils.prefixFileFilter(".DS_Store"));

        // 使用过滤器
        FileAlterationObserver observer = new FileAlterationObserver(new File(rootDir), filter);
        //不使用过滤器
//        FileAlterationObserver observer = new FileAlterationObserver(new File(rootDir));

        observer.addListener(new FileListener(config));
        //创建文件变化监听器
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
        // 开始监控
        monitor.start();

    }
}
