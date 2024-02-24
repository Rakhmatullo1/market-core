package com.rahmatullo.comfortmarket.config;

import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@Slf4j
public class StorageProperties {
    @Value("${storage.photoLocation}")
    private String photoLocation;

    public String getPhotoLocation() {
        checkException(photoLocation);
        return photoLocation;
    }

    private void checkException(String location) {
        if(Objects.isNull(location) || StringUtils.isEmpty(location)) {
            log.warn("location cannot be null");
            throw new NotFoundException("Photo location is not found, errors with server");
        }
    }
}
