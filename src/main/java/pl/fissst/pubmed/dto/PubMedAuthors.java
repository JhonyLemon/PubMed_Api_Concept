package pl.fissst.pubmed.dto;


import lombok.*;

import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PubMedAuthors {
    private Integer perPage;
    private Integer pagesCount;
    private Integer requestedPage;
    private Map<String,Integer> authors;

    public void setAuthorAndIncrement(String author){
        Integer count = authors.get(author);
        authors.put(author,(count==null?0:count)+1);
    }

}
