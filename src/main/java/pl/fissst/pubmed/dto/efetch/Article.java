package pl.fissst.pubmed.dto.efetch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {
    @JacksonXmlProperty(localName = "ArticleTitle")
    private String articleTitle;
    @JacksonXmlProperty(localName = "AuthorList")
    private AuthorList authorList;
    @JacksonXmlProperty(localName = "Language")
    private String language;
    @JacksonXmlProperty(localName = "ArticleDate")
    private Date articleDate;
}
