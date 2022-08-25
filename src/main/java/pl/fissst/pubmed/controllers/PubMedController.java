package pl.fissst.pubmed.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.fissst.pubmed.services.PubMedService;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class PubMedController {

    private final PubMedService service;

    @GetMapping("/doctors")
    public Map<String,Integer> getDoctorsByDisease(@RequestParam(name = "disease",required = true) String disease){
        return service.getDoctors(disease);
    }

}
