package com.hgf.lib.utils;

import com.hgf.lib.utils.constant.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
/**
 *
 *  author   : HuangGuoFeng
 *  package  : com.hgf.lib.utils
 *  date     : 2019-12-17
 *  desc     : 压缩 解压缩
 *
 */
public final class ZipUtils {
    private static final int BUFFER_LEN = 8192;
    private static final String TAG= ZipUtils.class.getSimpleName();
    private ZipUtils() {
        throw new UnsupportedOperationException(Constants.INSTANCE_EXCEPTION);
    }

    /**
       *
       * @param srcFiles 要压缩的文件资源集
       * @param zipFilePath 压缩目标路径
       * Description: 压缩多个文件到一个路径
       *
    */
    public static boolean zipFiles(final Collection<String> srcFiles,
                                   final String zipFilePath)
            throws IOException {
        return zipFiles(srcFiles, zipFilePath, null);
    }

    public static boolean zipFiles(final Collection<String> srcFilePaths,
                                   final String zipFilePath,
                                   final String comment)
            throws IOException {
        if (srcFilePaths == null || zipFilePath == null) return false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFilePath));
            for (String srcFile : srcFilePaths) {
                if (!zipFile(getFileByPath(srcFile), "", zos, comment)) return false;
            }
            return true;
        } finally {
            if (zos != null) {
                zos.finish();
                zos.close();
            }
        }
    }

    /**
     *
     * @param srcFiles 要压缩的文件资源集
     * @param zipFile  目标压缩路径
     * Description: 压缩多个文件
     */
    public static boolean zipFiles(final Collection<File> srcFiles, final File zipFile)
            throws IOException {
        return zipFiles(srcFiles, zipFile, null);
    }

    public static boolean zipFiles(final Collection<File> srcFiles,
                                   final File zipFile,
                                   final String comment)
            throws IOException {
        if (srcFiles == null || zipFile == null) return false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            for (File srcFile : srcFiles) {
                if (!zipFile(srcFile, "", zos, comment)) return false;
            }
            return true;
        } finally {
            if (zos != null) {
                zos.finish();
                zos.close();
            }
        }
    }

    /**
     *
     * @param srcFilePath 压缩文件
     * @param zipFilePath 目标压缩路径
     * Description: 压缩单一文件
     */
    public static boolean zipFile(final String srcFilePath,
                                  final String zipFilePath)
            throws IOException {
        return zipFile(getFileByPath(srcFilePath), getFileByPath(zipFilePath), null);
    }

    public static boolean zipFile(final String srcFilePath,
                                  final String zipFilePath,
                                  final String comment)
            throws IOException {
        return zipFile(getFileByPath(srcFilePath), getFileByPath(zipFilePath), comment);
    }

    /**
     *
     * @param srcFile 压缩文件
     * @param zipFile 目标路径
     * Description: 压缩单一文件
     */
    public static boolean zipFile(final File srcFile,
                                  final File zipFile)
            throws IOException {
        return zipFile(srcFile, zipFile, null);
    }

    public static boolean zipFile(final File srcFile,
                                  final File zipFile,
                                  final String comment)
            throws IOException {
        if (srcFile == null || zipFile == null) return false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            return zipFile(srcFile, "", zos, comment);
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
    }

    /**
       *
       * @param srcFile 要压缩的文件
       * @param rootPath 根目录
       * @param zos 目标路径的压缩流
       * @param comment 批注
       * Description：压缩文件
       *
    */
    private static boolean zipFile(final File srcFile,
                                   String rootPath,
                                   final ZipOutputStream zos,
                                   final String comment)
            throws IOException {
        rootPath = rootPath + (isSpace(rootPath) ? "" : File.separator) + srcFile.getName();
        if (srcFile.isDirectory()) {
            File[] fileList = srcFile.listFiles();
            if (fileList == null || fileList.length <= 0) {
                ZipEntry entry = new ZipEntry(rootPath + '/');
                entry.setComment(comment);
                zos.putNextEntry(entry);
                zos.closeEntry();
            } else {
                for (File file : fileList) {
                    if (!zipFile(file, rootPath, zos, comment)) return false;
                }
            }
        } else {
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(srcFile));
                ZipEntry entry = new ZipEntry(rootPath);
                entry.setComment(comment);
                zos.putNextEntry(entry);
                byte buffer[] = new byte[BUFFER_LEN];
                int len;
                while ((len = is.read(buffer, 0, BUFFER_LEN)) != -1) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return true;
    }

    /**
     *
     * @param zipFilePath 要解压的文件
     * @param destDirPath 目标路径
     * Description：解压缩文件
     */
    public static List<File> unzipFile(final String zipFilePath,
                                       final String destDirPath)
            throws IOException {
        return unzipFileByKeyword(zipFilePath, destDirPath, null);
    }

    /**
     * Unzip the file.
     *
     * @param zipFile 要解压的文件
     * @param destDir 目标路径
     * Description：解压缩文件
     */
    public static List<File> unzipFile(final File zipFile,
                                       final File destDir)
            throws IOException {
        return unzipFileByKeyword(zipFile, destDir, null);
    }

    /**
     * Unzip the file by keyword.
     *
     * @param zipFilePath 要解压的文件
     * @param destDirPath 目标路径
     * @param keyword     关键字
     * Description：解压缩文件
     */
    public static List<File> unzipFileByKeyword(final String zipFilePath,
                                                final String destDirPath,
                                                final String keyword)
            throws IOException {
        return unzipFileByKeyword(getFileByPath(zipFilePath), getFileByPath(destDirPath), keyword);
    }

    /**
     * Unzip the file by keyword.
     *
     * @param zipFile 要解压的文件
     * @param destDir 目标路径
     * @param keyword 关键字
     * Description：解压缩文件
     */
    public static List<File> unzipFileByKeyword(final File zipFile,
                                                final File destDir,
                                                final String keyword)
            throws IOException {
        if (zipFile == null || destDir == null) return null;
        List<File> files = new ArrayList<>();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<?> entries = zip.entries();
        try {
            if (isSpace(keyword)) {
                while (entries.hasMoreElements()) {
                    ZipEntry entry = ((ZipEntry) entries.nextElement());
                    String entryName = entry.getName().replace("\\", "/");
                    if (entryName.contains("../")) {
                        L.e(TAG, "entryName: " + entryName + " is dangerous!");
                        continue;
                    }
                    if (!unzipChildFile(destDir, files, zip, entry, entryName)) return files;
                }
            } else {
                while (entries.hasMoreElements()) {
                    ZipEntry entry = ((ZipEntry) entries.nextElement());
                    String entryName = entry.getName().replace("\\", "/");
                    if (entryName.contains("../")) {
                        L.e(TAG, "entryName: " + entryName + " is dangerous!");
                        continue;
                    }
                    if (entryName.contains(keyword)) {
                        if (!unzipChildFile(destDir, files, zip, entry, entryName)) return files;
                    }
                }
            }
        } finally {
            zip.close();
        }
        return files;
    }

    private static boolean unzipChildFile(final File destDir,
                                          final List<File> files,
                                          final ZipFile zip,
                                          final ZipEntry entry,
                                          final String name) throws IOException {
        File file = new File(destDir, name);
        files.add(file);
        if (entry.isDirectory()) {
            return createOrExistsDir(file);
        } else {
            if (!createOrExistsFile(file)) return false;
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(zip.getInputStream(entry));
                out = new BufferedOutputStream(new FileOutputStream(file));
                byte buffer[] = new byte[BUFFER_LEN];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        }
        return true;
    }

    /**
     *
     * @param zipFilePath 目标文件
     * Description：获取文件下所有文件路径
     */
    public static List<String> getFilesPath(final String zipFilePath)
            throws IOException {
        return getFilesPath(getFileByPath(zipFilePath));
    }

    /**
     *
     * @param zipFile 目标文件
     * Description：获取文件下所有文件路径
     */
    public static List<String> getFilesPath(final File zipFile)
            throws IOException {
        if (zipFile == null) return null;
        List<String> paths = new ArrayList<>();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<?> entries = zip.entries();
        while (entries.hasMoreElements()) {
            String entryName = ((ZipEntry) entries.nextElement()).getName().replace("\\", "/");;
            if (entryName.contains("../")) {
                L.e(TAG, "entryName: " + entryName + " is dangerous!");
                paths.add(entryName);
            } else {
                paths.add(entryName);
            }
        }
        zip.close();
        return paths;
    }

    /**
     *
     * @param zipFilePath 目标文件
     * Description：获取所有批注信息
     */
    public static List<String> getComments(final String zipFilePath)
            throws IOException {
        return getComments(getFileByPath(zipFilePath));
    }

    /**
     *
     * @param zipFile 目标文件
     * Description：获取所有批注信息
     */
    public static List<String> getComments(final File zipFile)
            throws IOException {
        if (zipFile == null) return null;
        List<String> comments = new ArrayList<>();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<?> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            comments.add(entry.getComment());
        }
        zip.close();
        return comments;
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
       *
       * @param s 要检查的字符串
       * Description: 检查是否为空白字符
       *
    */
    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
