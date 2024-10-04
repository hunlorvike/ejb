package model;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Query {
    private int pageNumber;
    private int pageSize;
    private boolean getAllRecords;
    private List<SortField> sortFields;
    private List<FilterField> filterFields;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SortField {
        private String field;
        private boolean ascending;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FilterField {
        private String field;
        private String value;
        private Operator operator;
    }
}
