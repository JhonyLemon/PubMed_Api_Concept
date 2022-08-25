package pl.fissst.pubmed.dto.efetch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Date {
    @JacksonXmlProperty(localName = "Year")
    private String year;
    @JacksonXmlProperty(localName = "Month")
    private String month;
    @JacksonXmlProperty(localName = "Day")
    private String day;
}
