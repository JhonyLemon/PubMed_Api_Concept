package pl.fissst.pubmed.dto.efetch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Author {
    @JacksonXmlProperty(localName = "LastName")
    private String lastName;
    @JacksonXmlProperty(localName = "ForeName")
    private String foreName;
    @JacksonXmlProperty(localName = "Initials")
    private String initials;
    @JacksonXmlProperty(localName = "AffiliationInfo")
    private AffiliationInfo affiliationInfo;
}
