package pl.fissst.pubmed.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Disease {

    private Long articleCount;
    private Long authorsCount;
    private Long time;

}
