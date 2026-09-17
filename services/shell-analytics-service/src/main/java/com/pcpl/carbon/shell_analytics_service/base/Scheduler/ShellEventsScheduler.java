package com.pcpl.carbon.shell_analytics_service.base.Scheduler;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.ShellEventsDataDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Service.ShellEventsService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ShellEventsScheduler {
    @Autowired
    ShellEventsService shellEventsService;

    @Autowired
    RedisTemplate<String, ShellEventsDataDTO> redisTemplate;

    // Enabled by default (production unchanged). Set SHELL_EVENTS_SCHEDULER_ENABLED=false
    // to turn this Redis-backed import off for local dev when no Redis is running.
    @Value("${SHELL_EVENTS_SCHEDULER_ENABLED:true}")
    private boolean schedulerEnabled;

    @Scheduled(fixedDelay = 1000)
    @SchedulerLock(name = "ShellEventsScheduler")
    public void importShellEventsFromRedis() {
        if (!schedulerEnabled) {
            return;
        }
        try {
            String key = "shell_events";
            ApplicationResponse applicationResponse = shellEventsService.getObject(key);
            List<ShellEventsDataDTO> shellEventsDataDTOS = (List<ShellEventsDataDTO>) applicationResponse.getData();
            if(shellEventsDataDTOS != null && !shellEventsDataDTOS.isEmpty()) {
                for(ShellEventsDataDTO shellEventsDataDTO : shellEventsDataDTOS) {
                    ApplicationResponse applicationResponse1 = shellEventsService.saveShellEvents(shellEventsDataDTO);
                    if(applicationResponse1.isSuccess()){
                        redisTemplate.opsForList().leftPop(key);
                    }
                }
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
