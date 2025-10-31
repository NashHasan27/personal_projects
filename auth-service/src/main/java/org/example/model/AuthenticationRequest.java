package org.example.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AuthenticationRequest {

    @NotBlank(message = "Username shouldn't be NULL OR EMPTY")
    private String username;

    @NotBlank(message = "Password shouldn't be NULL OR EMPTY")
    private String password;

    @NotBlank(message = "Title shouldn't be NULL OR EMPTY")
    private String authMessageTitle;

    @NotBlank(message = "Content shouldn't be NULL OR EMPTY")
    private String authMessageContent;

}
