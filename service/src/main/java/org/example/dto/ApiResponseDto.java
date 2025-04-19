package org.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponseDto {
    private ResultDto result;
    @Data
    public static class ResultDto {
        private List<HeroDto> heroes;
    }
}
