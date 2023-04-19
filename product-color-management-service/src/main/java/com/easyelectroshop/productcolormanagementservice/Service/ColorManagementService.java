package com.easyelectroshop.productcolormanagementservice.Service;

import com.easyelectroshop.productcolormanagementservice.Model.Color;
import com.easyelectroshop.productcolormanagementservice.Repository.ColorManagementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ColorManagementService {

    @Autowired
    ColorManagementRepository colorManagementRepository;

    public HttpStatusCode saveColor(Color color) {
        log.info("ADDING COLOR WITH NAME "+color.getColorName());
        try{
            colorManagementRepository.save(color);
            log.info("SUCCESSFULLY ADDED COLOR WITH NAME "+color.getColorName());
            return HttpStatusCode.valueOf(201);
        } catch (Exception ex){
            log.error("ERROR ADDING COLOR WITH NAME"+color.getColorName(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode updateColor(Color color){
        log.info("EDITING COLOR WITH NAME "+color.getColorName());
        try{
            Optional<Color> tempColor = colorManagementRepository.findById(color.getColorId());
            if(tempColor.isPresent()){
                colorManagementRepository.save(color);
                log.info("SUCCESSFULLY EDITED COLOR WITH NAME "+color.getColorName());
                return HttpStatusCode.valueOf(202);
            } else {
                log.info("COULD NOT FIND GIVEN COLOR, ADDING NEW COLOR");
                colorManagementRepository.save(color);
                log.info("SUCCESSFULLY ADDED NEW COLOR");
                return HttpStatusCode.valueOf(201);
            }
        } catch (Exception ex){
            log.error("ERROR EDITING COLOR WITH NAME "+color.getColorName(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

//    public List<Color> getAllColorsPaginated(int pageNumber, int pageSize){
//        log.info("GETTING ALL COLORS (PAGINATED)");
//        try{
//            Page<Color> colorPage = colorManagementRepository.findAll(PageRequest.of(pageNumber,pageSize, Sort.by("colorName")));
//            List<Color> colors = colorPage.toList();
//            log.info("SUCCESSFULLY RETRIEVED ALL COLORS (PAGINATED)");
//            return colors;
//        } catch (Exception ex){
//            log.error("COULD NOT RETRIEVE ALL COLORS (PAGINATED) ",ex);
//            return null;
//        }
//    }

    public List<Color> getAllColors(){
        log.info("GETTING ALL COLORS");
        try{
            List<Color> colors = colorManagementRepository.findAll();
            log.info("SUCCESSFULLY RETRIEVED ALL COLORS");
            return colors;
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE ALL COLORS",ex);
            return null;
        }
    }

    public Optional<Color> getColorById(long colorId){
        log.info("GETTING COLOR WITH COLOR_ID "+colorId);
        try{
            Optional<Color> color = colorManagementRepository.findById(colorId);
            if(color.isPresent()){
                log.info("SUCCESSFULLY RETRIEVED COLOR WITH COLOR_ID "+colorId);
                return color;
            } else {
                log.error("COULD NOT FIND COLOR WITH COLOR_ID "+colorId+", COLOR NOT FOUND!");
                return Optional.empty();
            }
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE COLOR WITH COLOR_ID "+colorId,ex);
            return Optional.empty();
        }
    }

    public HttpStatusCode deleteColor(long colorId){
        log.info("DELETING COLOR WITH COLOR_ID "+colorId);
        try{
            Optional<Color> color = colorManagementRepository.findById(colorId);
            if(color.isPresent()){
                colorManagementRepository.deleteById(colorId);
                log.info("SUCCESSFULLY DELETED COLOR WITH COLOR_ID "+colorId);
                return HttpStatusCode.valueOf(200);
            } else {
                log.error("COULD NOT DELETE COLOR WITH COLOR_ID "+colorId+", COLOR NOT FOUND!");
                return HttpStatusCode.valueOf(404);
            }
        } catch (Exception ex){
            log.error("COULD NOT DELETE COLOR WITH COLOR_ID "+colorId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }
}