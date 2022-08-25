package pl.fissst.pubmed.dto.egquery;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ResultItem {
    @JacksonXmlProperty(localName = "DbName")
    private String dbName;
    @JacksonXmlProperty(localName = "MenuName")
    private String menuName;
    @JacksonXmlProperty(localName = "Count")
    private String count;
    @JacksonXmlProperty(localName = "Status")
    private String status;

}
