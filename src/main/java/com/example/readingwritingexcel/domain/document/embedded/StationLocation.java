package com.example.readingwritingexcel.domain.document.embedded;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class StationLocation {
    private String type;

    private double[] coordinates;
}