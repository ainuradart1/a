package kittyassistant.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {
    private String username;
    private String bio;
    private String picture;
}