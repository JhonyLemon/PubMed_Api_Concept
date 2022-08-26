package pl.fissst.pubmed.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.fissst.pubmed.dto.*;
import pl.fissst.pubmed.services.PubMedService;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/author")
public class PubMedController {

    private final PubMedService service;

    @GetMapping("/paginated")
    public PubMedAuthors getAuthorsPaginated(
            @RequestParam(name = "disease",required = true) String disease,
            @RequestParam(name = "size",required = false,defaultValue = "10000")Integer size,
            @RequestParam(name = "page",required = false,defaultValue = "0")Integer page
    ){
        return service.getAuthorsPaginated(disease,size,page);
    }

    @GetMapping("/all")
    public Map<String,Integer> getAllAuthors(
            @RequestParam(name = "disease",required = true) String disease
    ){
        return service.getAllAuthors(disease);
    }

    @PostMapping("/info")
    public Map<String, ComputingInfo> getInfo(@RequestBody() List<String> diseases){
        return service.getInfo(diseases);
    }

}
