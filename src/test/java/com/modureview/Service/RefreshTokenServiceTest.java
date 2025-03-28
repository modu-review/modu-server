package com.modureview.Service;

import static org.junit.jupiter.api.Assertions.*;

import com.modureview.Entity.RefreshToken;
import com.modureview.Repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RefreshTokenServiceTest {

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks
  private RefreshTokenService refreshTokenService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetRefreshTokenByUserId_found() {
    // given
    RefreshToken token = new RefreshToken(); // RefreshToken 객체 생성 (필요한 경우 프로퍼티 설정)
    Long userId = 1L;
    when(refreshTokenRepository.findById(userId)).thenReturn(Optional.of(token));

    // when
    Optional<RefreshToken> result = refreshTokenService.getRefreshTokenByUserId(userId);

    // then
    assertTrue(result.isPresent(), "RefreshToken should be present");
    assertEquals(token, result.get(), "Returned token should match the expected token");
    verify(refreshTokenRepository, times(1)).findById(userId);
  }

  @Test
  public void testGetRefreshTokenByUserId_notFound() {
    // given
    Long userId = 2L;
    when(refreshTokenRepository.findById(userId)).thenReturn(Optional.empty());

    // when
    Optional<RefreshToken> result = refreshTokenService.getRefreshTokenByUserId(userId);

    // then
    assertFalse(result.isPresent(), "RefreshToken should not be present");
    verify(refreshTokenRepository, times(1)).findById(userId);
  }

  @Test
  public void testSaveRefreshToken() {
    // given
    RefreshToken token = new RefreshToken();

    // when
    refreshTokenService.saveRefreshToken(token);

    // then
    verify(refreshTokenRepository, times(1)).save(token);
  }

  @Test
  public void testRemoveRefreshTokenByUserId_tokenExists() {
    // given
    Long userId = 3L;
    RefreshToken token = new RefreshToken();
    when(refreshTokenRepository.findById(userId)).thenReturn(Optional.of(token));

    // when
    refreshTokenService.removeRefreshTokenByUserId(userId);

    // then
    verify(refreshTokenRepository, times(1)).delete(token);
  }

  @Test
  public void testRemoveRefreshTokenByUserId_tokenNotExists() {
    // given
    Long userId = 4L;
    when(refreshTokenRepository.findById(userId)).thenReturn(Optional.empty());

    // when
    refreshTokenService.removeRefreshTokenByUserId(userId);

    // then
    verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
  }
}