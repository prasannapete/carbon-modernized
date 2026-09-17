package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CodeConfigurationTranslation.Service;

import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CodeConfigurationTranslationDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.CodeConfigurationTranslation;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CodeConfigurationTranslation.Repository.CodeConfigurationTranslationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CodeConfigurationTranslationServiceImpl extends AbstractLazyService<CodeConfigurationTranslation, CodeConfigurationTranslationDTO, CodeConfigurationTranslationRepository> implements CodeConfigurationTranslationService {

    @Override
    public CodeConfigurationTranslation getEntityObject() {
        return new CodeConfigurationTranslation();
    }

    @Override
    public CodeConfigurationTranslationDTO getDtoObject() {return new CodeConfigurationTranslationDTO();}

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }

}
