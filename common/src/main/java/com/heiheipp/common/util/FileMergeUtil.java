package com.heiheipp.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * @author zhangxi
 * @version 1.0
 * @className FileMergeUtil
 * @desc TODO
 * @date 2022/3/13 20:19
 */
public class FileMergeUtil {

    /**
     * nio方式拼接文件
     * @param fpaths
     * @param resultPath
     * @return
     */
    public static boolean mergeFiles(List<String> fpaths, String resultPath) {
        if (fpaths == null || fpaths.size() < 1 || StrUtil.isEmpty(resultPath)) {
            return false;
        }

        if (fpaths.size() == 1) {
            return new File(fpaths.get(0)).renameTo(new File(resultPath));
        }

        File[] files = new File[fpaths.size()];
        for (int i = 0; i < fpaths.size(); i++) {
            files[i] = new File(fpaths.get(i));
            if (StrUtil.isEmpty(fpaths.get(i)) || !files[i].exists() || !files[i].isFile()) {
                return false;
            }
        }

        File resultFile = new File(resultPath);

        try {
            FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
            for (int i = 0; i < files.length; i++) {
                FileChannel blk = new FileInputStream(files[i]).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                blk.close();
            }
            resultFileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        for (int i = 0; i < files.length; i++) {
            files[i].deleteOnExit();
        }

        return true;
    }
}
