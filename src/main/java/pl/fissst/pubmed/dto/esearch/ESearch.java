package pl.fissst.pubmed.dto.esearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
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
public class ESearch {

    @JacksonXmlProperty(localName = "Count")
    private String count;
    @JacksonXmlProperty(localName = "RetMax")
    private String retMax;
    @JacksonXmlProperty(localName = "RetStart")
    private String retStart;
    @JacksonXmlProperty(localName = "IdList")
    private IdList idList;
    @JacksonXmlProperty(localName = "QueryTranslation")
    private String queryTranslation;
}





