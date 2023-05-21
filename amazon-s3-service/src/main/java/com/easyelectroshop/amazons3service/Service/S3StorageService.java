package com.easyelectroshop.amazons3service.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.easyelectroshop.amazons3service.DTO.Model3D;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
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
        log.info("CALLING AMAZON API TO UPLOAD MODEL FOR FILE WITH FILE_NAME "+file.getOriginalFilename());
        try{
            File convertedFile = convertMultiPartFileToFile(file);
            long instant = Instant.now().toEpochMilli();
            String fileName = instant+"_"+file.getOriginalFilename();
            amazonS3.putObject(new PutObjectRequest(bucketName,fileName,convertedFile).withCannedAcl(CannedAccessControlList.PublicRead));
            convertedFile.delete();
            log.info("SUCCESSFULLY UPLOADED FILE WITH FILE_NAME "+file.getOriginalFilename()+" TO AMAZON S3 BUCKET WITH BUCKET_NAME "+bucketName);
            URL url = amazonS3.getUrl(bucketName,fileName);
            return(new Model3D(fileName,url));
        } catch (Exception ex){
            log.error("ERROR WHILE UPLOADING FILE WITH FILE_NAME "+file.getOriginalFilename(),ex);
            return null;
        }
    }

    public byte[] downloadModel(String fileName){
        log.info("DOWNLOADING FILE WITH FILE_NAME "+fileName);
        S3Object s3Object = amazonS3.getObject(bucketName,fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try{
            byte[] content = IOUtils.toByteArray(inputStream);
            log.info("SUCCESSFUL DOWNLOAD REQUEST FOR FILE WITH FILE_NAME "+fileName);
            return content;
        } catch (IOException ex){
            log.error("ERROR DOWNLOADING FILE WITH FILE_NAME "+fileName,ex);
            return null;
        }
    }

    public HttpStatusCode deleteFile(String fileName){
        log.info("DELETING MODEL FILE WITH FILE_NAME "+fileName);
        try{
            try{
                S3Object s3Object = amazonS3.getObject(bucketName,fileName);
            } catch (AmazonS3Exception s3Exception){
                log.error("FILE WITH FILE_NAME "+fileName+ " NOT FOUND!");
                return HttpStatusCode.valueOf(404);
            }
            amazonS3.deleteObject(bucketName,fileName);
            log.info("SUCCESSFULLY DELETED MODEL FILE WITH FILE_NAME "+fileName);
            return HttpStatusCode.valueOf(200);
        } catch (Exception ex){
            log.error("ERROR DELETING MODEL FILE WITH FILE_NAME "+fileName,ex);
            return HttpStatusCode.valueOf(500);
        }
    }


    private File convertMultiPartFileToFile(MultipartFile file){
        log.info("CONVERTING MULTI-PART FILE TO SINGLE FILE WITH FILE_NAME "+file.getOriginalFilename());
        File convertedFile = new File(file.getOriginalFilename());
        try(FileOutputStream fos = new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
            log.info("SUCCESSFULLY CONVERTED MULTI-PART FILE TO SINGLE FILE WITH FILE_NAME "+file.getOriginalFilename());
            return convertedFile;
        } catch (IOException e){
            log.error("ERROR CONVERTING MULTI-PART FILE TO SINGLE FILE WITH FILE_NAME  "+file.getOriginalFilename(),e);
            return null;
        }
    }

}