package com.pcpl.carbon.authserver.AppFeature.Service;

import com.pcpl.carbon.authserver.AppFeature.Model.AppFeature;
import com.pcpl.carbon.authserver.AppFeature.Repository.AppFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppFeatureServiceImpl implements AppFeatureService {

    @Autowired
    AppFeatureRepository appFeatureRepository;

    @Override
    public List<AppFeature> getAppFeaturesForUser(Long userId) throws Exception {
        return appFeatureRepository.getAppFeaturesForUser(userId);
    }
}
