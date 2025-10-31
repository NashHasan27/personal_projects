package org.example.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationMessage {

    @NotBlank(message = "Title shouldn't be NULL OR EMPTY")
    private String messageTitle;

    @NotBlank(message = "Description shouldn't be NULL OR EMPTY")
    private String messageDescription;
}
