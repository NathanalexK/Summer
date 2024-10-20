package mg.itu.prom16.util;

import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Paths;

public class CustomFile {
    private String fileName;
    private byte[] bytes;

    public CustomFile() {

    }

    public CustomFile(Part part) throws IOException {
        fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        System.out.println("Filename: " + fileName);
        bytes = part.getInputStream().readAllBytes();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
