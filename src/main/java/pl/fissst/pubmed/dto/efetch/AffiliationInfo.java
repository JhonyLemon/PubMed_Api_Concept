package pl.fissst.pubmed.dto.efetch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AffiliationInfo {
    @JacksonXmlProperty(localName = "Affiliation")
    private String affiliation;
}