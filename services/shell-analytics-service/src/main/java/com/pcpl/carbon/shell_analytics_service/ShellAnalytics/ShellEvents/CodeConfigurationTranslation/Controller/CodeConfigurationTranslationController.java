package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CodeConfigurationTranslation.Controller;

import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CodeConfigurationTranslationDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.CodeConfigurationTranslation;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CodeConfigurationTranslation.Repository.CodeConfigurationTranslationRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CodeConfigurationTranslation.Service.CodeConfigurationTranslationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/code-configuration-translation")
@Slf4j
public class CodeConfigurationTranslationController extends AbstractCRUDController<CodeConfigurationTranslation, CodeConfigurationTranslationDTO, CodeConfigurationTranslationRepository, CodeConfigurationTranslationServiceImpl> {

}
