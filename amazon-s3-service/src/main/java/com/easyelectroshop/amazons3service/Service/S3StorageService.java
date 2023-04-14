package com.easyelectroshop.amazons3service.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.easyelectroshop.amazons3service.DTO.Model3D;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;

@Service
@Slf4j
public class S3StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    AmazonS3 amazonS3;

    public Model3D uploadModel(MultipartFile file){
        try{
            File convertedFile = convertMultiPartFileToFile(file);
            long instant = Instant.now().toEpochMilli();
            String fileName = instant+"_"+file.getOriginalFilename();
            amazonS3.putObject(new PutObjectRequest(bucketName,fileName,convertedFile).withCannedAcl(CannedAccessControlList.PublicRead));
            convertedFile.delete();
            log.info("Successfully Uploaded Model "+file.getOriginalFilename()+" to Cloud.");
            URL url = amazonS3.getUrl(bucketName,fileName);
            return(new Model3D(fileName,url));
        } catch (Exception e){
            log.error("Error uploading Model "+file.getOriginalFilename(),e);
            return null;
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
            log.error("Error deleting Model "+fileName,e);
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
