package com.post_hub.iam_service;

import com.post_hub.iam_service.model.entity.RefreshToken;
import com.post_hub.iam_service.model.entity.User;

public interface RefreshTokenService {

    RefreshToken generateOrUpdateRefreshToken(User user);

    RefreshToken validateAndRefreshToken(String refreshToken);

}
