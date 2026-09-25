package com.pcpl.carbon.kitkat_service.EventScores.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcpl.carbon.kitkat_service.Config.PCPLConfig;
import com.pcpl.carbon.kitkat_service.EventScores.Repository.EventScoresRepository;
import com.pcpl.carbon.kitkat_service.EventScores.Response.EventScoresResponse;
import com.pcpl.carbon.kitkat_service.Events.Repository.EventsRepository;
import com.pcpl.carbon.pcplsdk.EventScores.Model.EventScores;
import com.pcpl.carbon.pcplsdk.EventScores.DTO.EventScoresDTO;
import com.pcpl.carbon.pcplsdk.Events.Model.Events;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Slf4j
@Service
public class EventScoresServiceImpl extends AbstractLazyService<EventScores, EventScoresDTO, EventScoresRepository> implements EventScoresService{

    @Autowired
    EventScoresRepository eventScoresRepository;

    @Autowired
    EventsRepository eventsRepository;

    @Autowired
    PCPLConfig pcplConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(EventScoresServiceImpl.class);

    @Override
    public EventScores getEntityObject() {
        return new EventScores();
    }

    @Override
    public EventScoresDTO getDtoObject() {
        return new EventScoresDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }

    @Override
    public boolean checkDuplicate(EventScoresDTO dto) {
        return false;
    }


    @Override
    public EventScoresResponse getDeleted() throws Exception {
        logger.trace("Entering");
        EventScoresResponse eventScoresResponse = new EventScoresResponse();
        try {
            eventScoresResponse.setData(
                    getEventScoreDTOS(eventScoresRepository.findAllByIsDeleted(1)
                    ));
            eventScoresResponse.setSuccess(true);
            eventScoresResponse.setError("");
            logger.trace("Completed Successfully");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventScoresResponse.setSuccess(false);
            eventScoresResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventScoresResponse;
    }

    @Override
    public EventScoresResponse getAllByEventIdAndParticipantId(Map<String, String> formData) throws Exception {
        logger.trace("Entering");
        EventScoresResponse eventScoresResponse = new EventScoresResponse();
        try {
            logger.trace("Data:{}", objectMapper.writeValueAsString(formData));
            int pageNumber = formData.get("current_page") == null ? 0 : Integer.parseInt(formData.get("current_page"));
            int pageSize = formData.get("page_size") == null ? 10 : Integer.parseInt(formData.get("page_size"));
            String sortFiled = formData.get("sort_field") == null ? "creationTime" : formData.get("sort_field");
            String sortOrder = formData.get("sort_order") == null ? "asc" : formData.get("sort_order");
            Sort sort;
            if (sortOrder.equals("asc")) {
                sort = Sort.by(Sort.Direction.ASC, sortFiled);
            } else {
                sort = Sort.by(Sort.Direction.DESC, sortFiled);
            }
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            Page<EventScoresDTO> page = eventScoresRepository.findAllByIsDeletedParticipantIdAndEventId(Long.valueOf(formData.get("eventId")),Long.valueOf(formData.get("participantId")),pageable);
            eventScoresResponse.setRecordsTotal(page.getTotalElements());
            eventScoresResponse.setRecordsFiltered(page.getTotalElements());
            eventScoresResponse.setTotalPages(page.getTotalPages());
            eventScoresResponse.setData(
                    (page.getContent())
            );
            eventScoresResponse.setCurrentRecords(eventScoresResponse.getData().size());
            eventScoresResponse.setSuccess(true);
            logger.trace("Completed Successfully");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventScoresResponse.setSuccess(false);
            eventScoresResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventScoresResponse;
    }

    @Override
    public EventScoresResponse getAllByEventId(Map<String, String> formData) throws Exception {
        logger.trace("Entering");
        EventScoresResponse eventScoresResponse = new EventScoresResponse();
        try {
            logger.trace("Data:{}", objectMapper.writeValueAsString(formData));
            int pageNumber = formData.get("current_page") == null ? 0 : Integer.parseInt(formData.get("current_page"));
            int pageSize = formData.get("page_size") == null ? 10 : Integer.parseInt(formData.get("page_size"));
            String eventId = formData.get("eventId") == null ? "eventId" : formData.get("eventId");
            String sortFiled = formData.get("sort_field") == null ? "creationTime" : formData.get("sort_field");
            String sortOrder = formData.get("sort_order") == null ? "asc" : formData.get("sort_order");
            Sort sort;
            if (sortOrder.equals("asc")) {
                sort = Sort.by(Sort.Direction.ASC, sortFiled);
            } else {
                sort = Sort.by(Sort.Direction.DESC, sortFiled);
            }
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            Page<EventScoresDTO> page = eventScoresRepository.findAllByIsDeletedEventId(Long.valueOf(formData.get("eventId")),pageable);
            eventScoresResponse.setData((page.getContent())
            );
            eventScoresResponse.setCurrentRecords(eventScoresResponse.getData().size());
            eventScoresResponse.setSuccess(true);
            logger.trace("Completed Successfully");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventScoresResponse.setSuccess(false);
            eventScoresResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventScoresResponse;
    }

    @Override
    public EventScoresResponse exportScoreToExcel(Map<String, String> formData) throws Exception {
        EventScoresResponse eventScoresResponse = new EventScoresResponse();
        try {

            Optional<Events> optionalEvents = eventsRepository.findById(Long.valueOf(formData.get("eventId")));

            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Places");
            CellStyle headerStyle = workbook.createCellStyle();

            Row header1 = sheet.createRow(0);
            Cell headerCell1 = header1.createCell(0);
            headerCell1.setCellValue("Event name is: "+optionalEvents.get().getEventName());
            headerCell1.setCellStyle(headerStyle);


            Row header = sheet.createRow(1);

            XSSFFont font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 11);
            font.setBold(true);
            headerStyle.setFont(font);

            Cell headerCell = header.createCell(0);
            headerCell.setCellValue("Name");
            headerCell.setCellStyle(headerStyle);


            headerCell = header.createCell(1);
            if(optionalEvents.isPresent()){
                if(optionalEvents.get().getScoreType()==0){
                    headerCell.setCellValue("Score (in Time)");
                }else{
                    headerCell.setCellValue("Score");
                }
            }
            headerCell.setCellStyle(headerStyle);


            File currDir = new File(pcplConfig.getExcelUploadPath());
            // Create the upload directory if it doesn't exist yet, so a valid configured path
            // works even before the folder is manually created (FileOutputStream won't make it).
            if (!currDir.exists()) {
                currDir.mkdirs();
            }
            String path = currDir.getAbsolutePath();
            CellStyle style = workbook.createCellStyle();
            XSSFFont cellFont = workbook.createFont();
            cellFont.setFontName("Arial");
            style.setWrapText(true);
            style.setFont(cellFont);
            int i = 2;
            EventScoresResponse eventScoresResponse1 = this.getAllByEventId(formData);
            for (EventScoresDTO eventScoresDTO : eventScoresResponse1.getData()) {
                Row row = sheet.createRow(i++);
                Cell cell = row.createCell(0);
                cell.setCellValue(eventScoresDTO.getParticipantName());
                cell.setCellStyle(headerStyle);


                cell = row.createCell(1);
                // getScore() is a Double and may be null (time-type events store `time`, or a
                // participant may have no score yet). setCellValue(double) auto-unboxes and NPEs
                // on null, so guard it: write the numeric score when present, else the time string,
                // else blank.
                if (eventScoresDTO.getScore() != null) {
                    cell.setCellValue(eventScoresDTO.getScore());
                } else if (eventScoresDTO.getTime() != null) {
                    cell.setCellValue(eventScoresDTO.getTime());
                } else {
                    cell.setCellValue("");
                }
                cell.setCellStyle(style);
            }

            // Write the workbook into FILE_UPLOAD_PATH but hand the browser a servable URL
            // (through the /api proxy -> kitkat) instead of the server filesystem path, which the
            // browser cannot navigate to. The file is served by GET /carbon-events/files/{name}.
            String fileName = System.nanoTime() + "_score.xlsx";
            String fileLocation = path + "/" + fileName;
            eventScoresResponse.setExcelUploadPath("/api/carbon-events/files/" + fileName);
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();
            outputStream.close();
            eventScoresResponse.setSuccess(true);
            return eventScoresResponse;



        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventScoresResponse.setSuccess(false);
            eventScoresResponse.setError(ex.getMessage());
            return eventScoresResponse;
        }
    }


    private List<EventScoresDTO> getEventScoreDTOS(List<EventScores> eventScores) {
        List<EventScoresDTO> eventScoresDTOS = new ArrayList<>();
        for (EventScores eventScores1 : eventScores) {
            eventScoresDTOS.add(new EventScoresDTO(eventScores1));
        }
        return eventScoresDTOS;
    }

}
