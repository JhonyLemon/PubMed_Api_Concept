package pl.fissst.pubmed.dto;


import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PubMedAuthors {
    private Integer perPage;
    private Integer pagesCount;
    private Integer requestedPage;
    private SortType sort;
    private OrderType order;
    private List<PubMedAuthor> authors;

    public void setAuthorAndIncrement(String author, Integer index){
        if(index!=null){
            Integer count=authors.get(index).getCount();
            authors.get(index).setCount(count+1);
        }else{
            authors.add(new PubMedAuthor(author,1));
        }
    }

}
