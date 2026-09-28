package tn.esprit.medicareaitigerback.utilities;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Utils {
    @Value("${app.base-dir}")
    private String baseDir;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public String addDateToFileName(String fileName) {
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

        String extension = getFileExtension(fileName);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        return baseName + "_" + timestamp + "." + extension;
    }

    public String getCustomFilePath(String fileName) {
        return uploadDir + "/" + fileName;
    }


}
