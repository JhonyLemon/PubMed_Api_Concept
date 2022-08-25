package pl.fissst.pubmed.dto.efetch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MedlineCitation {
    @JacksonXmlProperty(localName = "PMID")
    private String pmid;
    @JacksonXmlProperty(localName = "DateCompleted")
    private Date dateCompleted;
    @JacksonXmlProperty(localName = "DateRevised")
    private Date dateRevised;
    @JacksonXmlProperty(localName = "Article")
    private Article article;
}