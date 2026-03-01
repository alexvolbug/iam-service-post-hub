package com.post_hub.iam_service.controller;

import com.post_hub.iam_service.model.constants.ApiLogMessage;
import com.post_hub.iam_service.model.dto.user.UserDTO;
import com.post_hub.iam_service.model.request.user.NewUserRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.service.UserService;
import com.post_hub.iam_service.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("${end.point.users}")
public class UserController {
    private final UserService userService;

    @GetMapping("${end.point.id}")
    public ResponseEntity<IamResponse<UserDTO>> getUserById(
            @PathVariable(name = "id") Integer userId){
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<UserDTO> response = userService.getById(userId);
        return ResponseEntity.ok(response);

    }

    @PostMapping("${end.point.create}")
    public ResponseEntity<IamResponse<UserDTO>> createUser(
            @RequestBody @Valid NewUserRequest request){
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());
        IamResponse<UserDTO> response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

}
