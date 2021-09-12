package org.upgrad.upstac.shared;

import org.springframework.web.multipart.MultipartFile;
import org.upgrad.upstac.exception.AppException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

public class FileReader {

    public static String readFromClassPath(String fileName) {

        try {
            return read(fileName);
        } catch (IOException e) {
            throw new AppException(e);
        }
    }

    public static String read(String fileName) throws IOException {
        ClassLoader classloader = currentThread().getContextClassLoader();
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(classloader.getResourceAsStream(fileName), UTF_8))) {
            return buffer.lines().collect(joining("\n"));

        }
    }

    public static InputStream readAsStream(String fileName) throws IOException {

        ClassLoader classloader = currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(fileName);
    }

    public static MultipartFile getMultipartFileFrom(String fileName) {

        try {
            return new MyMultiPartFile(fileName, readAsStream(fileName));
        } catch (IOException e) {
            throw new AppException(e);
        }

    }
}