package com.easyelectroshop.productcolormanagementservice.Controller;

import com.easyelectroshop.productcolormanagementservice.Model.Color;
import com.easyelectroshop.productcolormanagementservice.Service.ColorManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/color-management")
public class ColorManagementController {

    @Autowired
    ColorManagementService colorManagementService;

    @PostMapping("/add-color")
    public ResponseEntity saveColor(@RequestBody Color color){
        return ResponseEntity.status(colorManagementService.saveColor(color)).build();
    }


    @GetMapping("/get-all/paginated")
    public ResponseEntity getAllPaginated(@RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
                                 @RequestParam(value="pageSize",defaultValue = "5",required = false) int pageSize){
        List<Color> colors = colorManagementService.getAllColorsPaginated(pageNumber,pageSize);
        return (colors!= null) ? ResponseEntity.ok(colors) : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity getAll(){
        List<Color> colors = colorManagementService.getAllColors();
        return (colors!= null) ? ResponseEntity.ok(colors) : ResponseEntity.unprocessableEntity().build();
    }


    @GetMapping("/get-color/{colorId}")
    public ResponseEntity getColor(@PathVariable long colorId){
        Optional<Color> color = colorManagementService.getColorById(colorId);
        return (color.isPresent()) ? ResponseEntity.ok(color) : ResponseEntity.notFound().build();
    }

    @PutMapping("/update-color")
    public ResponseEntity updateColor(@RequestBody Color color){
        return ResponseEntity.status(colorManagementService.updateColor(color)).build();
    }

    @DeleteMapping("/delete-color/{colorId}")
    public ResponseEntity deleteColor(@PathVariable long colorId){
        return ResponseEntity.status(colorManagementService.deleteColor(colorId)).build();
    }

}