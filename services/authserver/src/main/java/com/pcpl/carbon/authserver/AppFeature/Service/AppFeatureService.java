package com.pcpl.carbon.authserver.AppFeature.Service;



import com.pcpl.carbon.authserver.AppFeature.Model.AppFeature;

import java.util.List;

public interface AppFeatureService {

    List<AppFeature> getAppFeaturesForUser(Long userId) throws Exception;
}
