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
    public  PubMedAuthors getAuthorsPaginated(
            @RequestParam(name = "disease",required = true) String disease,
            @RequestParam(name = "size",required = false,defaultValue = "10000")Integer size,
            @RequestParam(name = "page",required = false,defaultValue = "0")Integer page,
            @RequestParam(name = "sort",required = false,defaultValue = "COUNT") SortType sort,
            @RequestParam(name = "order",required = false,defaultValue = "DESC") OrderType order
    ){
        return service.getAuthorsPaginated(disease,size,page,sort,order);
    }

    @GetMapping("/all")
    public List<PubMedAuthor> getAllAuthors(
            @RequestParam(name = "disease",required = true) String disease
    ){
        return service.getAllAuthors(disease);
    }

    @PostMapping("/info")
    public Map<String, ComputingInfo> getInfo(@RequestBody() List<String> diseases){
        return service.getInfo(diseases);
    }

}
