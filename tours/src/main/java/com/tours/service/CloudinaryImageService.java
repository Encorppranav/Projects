package com.tours.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CloudinaryImageService
{
        private static final Logger logger = Logger.getLogger(CloudinaryImageService.class.getName());

        private final Cloudinary cloudinary;

        //Intializing Cloudinary Object through construtor injection by providing the credentials through application.properties
        public CloudinaryImageService(
                @Value("${cloudinary.cloud_name}") String cloudName,
                @Value("${cloudinary.api_key}") String apiKey,
                @Value("${cloudinary.api_secret}") String secretKey
        )
        {
                 this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                        "cloud_name", cloudName,
                        "api_key",apiKey,
                        "api_secret",secretKey
                ));

                 logger.info("Object is created through constructor injection");
        }

        //Upload an image to cloudinary and return its URL

        public String uploadImages(MultipartFile file)
        {
            logger.info("Starting image uploading process");
            try{

                if(file.isEmpty())
                {
                    logger.warning("Upload failed:File is empty");
                    throw new IllegalArgumentException("File is empty");
                }

                //Upload the image to cloudinary
                Map <?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(),ObjectUtils.emptyMap());
                 String imageUrl = (String)uploadResult.get("secure_url");
                 return imageUrl;

            }
            catch(IOException e)
            {
                    logger.log(Level.SEVERE,"Unable to upload the image " + e.getMessage());
                    throw new RuntimeException("Failed to upload the images: " + e.getMessage());
            }
        }


        //Update an existing Url
    public String updateImage(String oldImageUrl , MultipartFile file )
    {
        logger.info("Starting the updation process");
        try{
            if(file != null || file.isEmpty())
            {
                logger.warning("Upload failed:File is empty");
                throw new IllegalArgumentException("File is empty");
            }

            //get the publicId
           String publicId = extractPublicIdFromUrl(oldImageUrl);

            //Detroying the old-image-url
            try{
                if(publicId != null)
                {
                    cloudinary.uploader().destroy(publicId,ObjectUtils.emptyMap());
                    logger.info("Old image url deleted successfully");
                }}catch (Exception e) {
                logger.log(Level.WARNING, "Could not delete old image: " + e.getMessage(), e);
            }

            return uploadImages(file);

        }catch (RuntimeException e)
        {
            logger.log(Level.SEVERE ,"Unable to update the image:" + e.getMessage(),e);
            throw new RuntimeException("Unable to update the image:" + e.getMessage());
        }


    }

    // Delete an image from Cloudinary using its URL
    public void deleteImage(String imageUrl) {
        logger.info("Starting image deletion process.");
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            logger.warning("Deletion failed: Image URL is null or empty.");
            return;
        }

        try {
            // Extract the public ID from the provided URL and delete the image
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                logger.info("Image deleted successfully.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to delete image: " + e.getMessage(), e);
            throw new RuntimeException("Failed to delete image: " + e.getMessage());
        }
    }



    // Extract the public ID from the Cloudinary URL to perform operations like deletion
    private String extractPublicIdFromUrl(String url) {
        logger.info("Extracting public ID from URL: " + url);
        if (url == null) return null;

        String[] urlParts = url.split("/");
        for (int i = 0; i < urlParts.length; i++) {
            if (urlParts[i].equals("upload") && i + 2 < urlParts.length) {
                String publicId = urlParts[i + 2];
                int dotIndex = publicId.lastIndexOf('.');
                publicId = dotIndex > 0 ? publicId.substring(0, dotIndex) : publicId;
                logger.info("Public ID extracted: " + publicId);
                return publicId;
            }
        }
        logger.warning("Public ID could not be extracted from URL: " + url);
        return null;
    }



}
