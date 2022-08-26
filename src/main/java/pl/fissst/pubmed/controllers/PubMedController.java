package pl.fissst.pubmed.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.fissst.pubmed.dto.Disease;
import pl.fissst.pubmed.services.PubMedService;

import java.util.*;

@RestController
@AllArgsConstructor
public class PubMedController {

    private final PubMedService service;

    @GetMapping("/doctors")
    public  Map<String, Integer> getDoctorsByDisease(@RequestParam(name = "disease",required = true) String disease){
        return service.getDoctors(disease);
    }

    @PostMapping("/info")
    public Map<String,Disease> getInfo(@RequestBody() List<String> diseases){
        Map<String,Disease> disease = new HashMap<>();
        diseases.forEach(x->{
            disease.put(x,service.getInfo(x));
        });
        return disease;
    }

}
