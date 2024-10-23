// UserResponseDTO.java
package helloworld.studytogether.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class UserResponseDTO {
  private Long userId;
  private String username;
  private String email;
  private String nickname;
  private String role;

}