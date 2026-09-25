package com.pcpl.carbon.kitkat_service.Events.Controller;

import com.pcpl.carbon.kitkat_service.Config.PCPLConfig;
import com.pcpl.carbon.kitkat_service.Events.Repository.EventsRepository;
import com.pcpl.carbon.kitkat_service.Events.Response.EventsResponse;
import com.pcpl.carbon.kitkat_service.Events.Service.EventsService;
import com.pcpl.carbon.kitkat_service.Events.Service.EventsServiceImpl;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Events.DTO.EventsDTO;
import com.pcpl.carbon.pcplsdk.Events.Model.Events;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/carbon-events")
public class EventsController extends AbstractCRUDController<Events, EventsDTO, EventsRepository, EventsServiceImpl> {
    private static final Logger logger = LoggerFactory.getLogger(EventsController.class);

    @Autowired
    EventsService eventsService;

    @Autowired
    PCPLConfig pcplConfig;

    @PostConstruct
    public void setUpServices() {

    }

    // Event logo upload. The original app pushed logos to S3; here the file is stored under
    // FILE_UPLOAD_PATH and a servable URL (/api/carbon-events/files/{name}) is returned, which
    // the frontend saves as the event's logoPath and uses as the <img> src. Returns a List so the
    // response shape matches the frontend (reads response[0].success / response[0].excelUploadPath).
    @RequestMapping(value = "/upload-logo", method = RequestMethod.POST)
    public List<ApplicationResponse> uploadLogo(@RequestParam("file") MultipartFile[] files) {
        List<ApplicationResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String original = file.getOriginalFilename();
                String ext = (original != null && original.contains("."))
                        ? original.substring(original.lastIndexOf('.')) : "";
                String fileName = System.nanoTime() + "_logo" + ext;
                File dir = new File(pcplConfig.getExcelUploadPath());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                file.transferTo(new File(dir, fileName));
                responses.add(ApplicationResponse.builder()
                        .success(true)
                        .excelUploadPath("/api/carbon-events/files/" + fileName)
                        .build());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                responses.add(ApplicationResponse.builder().success(false).error(ex.getMessage()).build());
            }
        }
        return responses;
    }

    // Serve an uploaded logo / generated excel from FILE_UPLOAD_PATH. Public (see SecurityConfig)
    // so <img> tags and download navigations (no Authorization header) can load it. Only the bare
    // file name is honoured to prevent path traversal.
    @RequestMapping(value = "/files/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        try {
            String safeName = new File(fileName).getName();
            Path filePath = Paths.get(pcplConfig.getExcelUploadPath()).resolve(safeName).normalize();
            File file = filePath.toFile();
            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + safeName + "\"")
                    .body(new FileSystemResource(file));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<ApplicationResponse> save(@RequestBody EventsDTO dto) {
        if (eventsService.checkDuplicate(dto)) {
            return ResponseEntity.status(HttpStatus.OK).body(ApplicationResponse.builder().success(false).error("Duplicate unit type").build());
        } else {
            return super.save(dto);
        }
    }


    @RequestMapping(value = "/get-deleted", method = RequestMethod.POST)
    public EventsResponse getDeleted() {
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            eventsResponse = eventsService.getDeleted();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    @RequestMapping(value = "/get-all-events", method = RequestMethod.POST)
    public EventsResponse getAllEvents() {
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            eventsResponse = eventsService.getAllEvents();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    // Management list: ALL non-deleted events (any isLive / date). Distinct from
    // /get-all-events which is the live+current leaderboard feed.
    @RequestMapping(value = "/get-events", method = RequestMethod.POST)
    public EventsResponse getEvents() {
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            eventsResponse = eventsService.getAllEventsList();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    @RequestMapping(value = "/get-leader-board-events", method = RequestMethod.POST)
    public EventsResponse getLeaderBoardEvents(@RequestBody Map<String, String> formData) {
        logger.trace("Entering");
        EventsResponse eventsResponse =  new EventsResponse();
        try {
            eventsResponse = eventsService.getLeaderboardEvents(formData);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    @RequestMapping(value = "/trash", method = RequestMethod.POST)
    public EventsResponse moveToTrash(@RequestBody Map<String, String> formData) {
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            eventsResponse = eventsService.moveToTrash(formData);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    @RequestMapping(value = "/keep-session", method = RequestMethod.POST)
    public boolean keepSession() {
        return true;
    }
}
