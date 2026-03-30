package edu.newnop.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 50, message = "Name must be 1–50 characters")
    @Pattern(
            regexp = "^(?! )(?!.*  )[\\p{L} ]+(?<! )$",
            message = "Title can contain only letters and single spaces, without leading, trailing, or repeated spaces"
    )
    private String title;
    @Size(max = 200, message = "Description must be up to 200 characters")
    private String description;
    private String status;
    private String priority;
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "Due date must be in the format YYYY-MM-DD"
    )
    private Date dueDate;
}
