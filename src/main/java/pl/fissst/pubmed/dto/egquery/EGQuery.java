package pl.fissst.pubmed.dto.egquery;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EGQuery {
    @JacksonXmlProperty(localName = "Term")
    private String term;
    @JacksonXmlProperty(localName = "eGQueryResult")
    private EGQueryResult egQueryResult;
}

