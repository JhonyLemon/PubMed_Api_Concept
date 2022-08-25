package pl.fissst.pubmed.dto.epost;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EPost {
    @JacksonXmlProperty(localName = "QueryKey")
    private String queryKey;
    @JacksonXmlProperty(localName = "WebEnv")
    private String webEnv;
}
