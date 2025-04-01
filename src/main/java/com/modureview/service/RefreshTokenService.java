package com.modureview.service;

import com.modureview.entity.RefreshToken;
import com.modureview.entity.User;
import com.modureview.repository.RefreshTokenRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;

  public Optional<RefreshToken> getRefreshTokenByUserId(Long Id){
    return refreshTokenRepository.findById(Id);
  }

  public void saveRefreshToken(RefreshToken refreshToken){
     refreshTokenRepository.save(refreshToken);
  }

  public void removeRefreshTokenByUserId(Long Id){
    getRefreshTokenByUserId(Id).ifPresent(refreshTokenRepository::delete);
  }

  public void removeRefreshTokenDB(User user) {
    refreshTokenRepository.findByUserId(user.getId())
        .ifPresent(refreshToken -> {
          refreshTokenRepository.delete(refreshToken);
          refreshTokenRepository.flush(); // 즉시 반영
        });

  }


}
