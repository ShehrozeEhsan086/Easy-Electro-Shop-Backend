package com.easyelectroshop.amazons3service.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class S3StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3;

    public String uploadModel(MultipartFile file){
        try{
            File convertedFile = convertMultiPartFileToFile(file);
            String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
            amazonS3.putObject(new PutObjectRequest(bucketName,fileName,convertedFile));
            convertedFile.delete();
            log.info("Successfully Uploaded Model "+file.getOriginalFilename()+" to Cloud.");
            return fileName;
        } catch (Exception e){
            log.error("Error uploading Model "+file.getOriginalFilename(),e);
            return "ERROR could not upload Model!";
        }
    }

    public byte[] downloadModel(String fileName){
        S3Object s3Object = amazonS3.getObject(bucketName,fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try{
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e){
            log.error("Error downloading file "+fileName,e);
            return null;
        }
    }

    public String deleteFile(String fileName){
        try{
            amazonS3.deleteObject(bucketName,fileName);
            return "Successfully Deleted Model "+fileName;
        } catch (Exception e){
            log.error("Error deleting Model "+fileName);
            return "ERROR could not delete Model!";
        }
    }



    private File convertMultiPartFileToFile(MultipartFile file){
        File convertedFile = new File(file.getOriginalFilename());
        try(FileOutputStream fos = new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
        } catch (IOException e){
            log.error("Error Converting File "+file.getOriginalFilename(),e);
        }
        return convertedFile;
    }

}
