package util;

import domain.FileCacheDomain;
import model.CacheModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Write file like DiskLruCache
 */
public class CacheFileWriter {

    private File cacheDir;
    private String path;
    private static final String TEMP_FILE_PREFIX = "temp_";

    public CacheFileWriter(String tempPath) {
        path = tempPath + "/";
        cacheDir = new File(tempPath);
        if (cacheDirCheck() && !cacheDir.mkdirs()) {
            //missing cache dir.
        }
    }

    public File writer2File(String name, byte[] bytes) {
        File tempFile = new File(path + TEMP_FILE_PREFIX + name);
        File cache = null;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
            out.write(bytes);
            cache = new File(path + name);
            tempFile.renameTo(cache);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cache;
    }

    public File writer2File(String name, InputStream stream) {
        File tempFile = new File(path + TEMP_FILE_PREFIX + name);
        File cache = null;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
            byte[] tmpBuf = new byte[4096];
            int len;
            while ((len = stream.read(tmpBuf)) > 0) {
                out.write(tmpBuf, 0, len);
            }
            cache = new File(path + name);
            tempFile.renameTo(cache);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cache;
    }

    public void syncDomain(FileCacheDomain domain) {
        if (cacheDirCheck()) {
            return;
        }
        File[] files = cacheDir.listFiles();
        if (files == null) {
            return;
        }
        for (File cache : files) {
            String fileName = cache.getName();
            if (fileName.startsWith(TEMP_FILE_PREFIX)) {
                cache.delete();
                continue;
            }
            String[] split = fileName.split("_");
            if (split.length < 2) {
                cache.delete();
                continue;
            }

            long lastAccess = cache.lastModified();
            CacheModel model = domain.addL2Cache(split[0], cache);
            model.lastAccessTime = lastAccess;
            model.accessCount = Integer.parseInt(Util.strIsEmpty(split[1]) ? "0" : split[1]);
        }
    }

    private boolean cacheDirCheck() {
        return !cacheDir.exists() || !cacheDir.isDirectory();
    }

    public void deleteFileStartWith(String name) {
        if (cacheDirCheck()) {
            return;
        }
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File cache : files) {
                String fileName = cache.getName();
                if (fileName.startsWith(name)) {
                    cache.delete();
                }
            }
        }
    }

    public void deleteAllUnderDir() {
        Util.deleteDir(cacheDir);
        cacheDir.mkdirs();
    }

    public long countFileSize() {
        if (cacheDirCheck()) {
            return 0L;
        }
        long size = 0L;
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File cache : files) {
                size += cache.length();
            }
        }
        return size;
    }
}
