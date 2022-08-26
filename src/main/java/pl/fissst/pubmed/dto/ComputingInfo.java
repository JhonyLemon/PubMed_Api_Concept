package pl.fissst.pubmed.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComputingInfo {

    private Long articleCount;
    private Long authorsCount;
    private Long time;

}
