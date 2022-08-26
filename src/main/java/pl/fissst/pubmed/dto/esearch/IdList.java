package pl.fissst.pubmed.dto.esearch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdList {
    @JacksonXmlProperty(localName = "Id")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<String> ids;
}
