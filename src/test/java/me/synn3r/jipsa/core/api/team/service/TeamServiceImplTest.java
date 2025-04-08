package me.synn3r.jipsa.core.api.team.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import me.synn3r.jipsa.core.api.base.enumeration.DeleteType;
import me.synn3r.jipsa.core.api.team.TeamTestSupport;
import me.synn3r.jipsa.core.api.team.domain.TeamRequest;
import me.synn3r.jipsa.core.api.team.domain.TeamResponse;
import me.synn3r.jipsa.core.api.team.domain.TeamSearchCondition;
import me.synn3r.jipsa.core.api.team.entity.Team;
import me.synn3r.jipsa.core.api.team.entity.TeamMapper;
import me.synn3r.jipsa.core.api.team.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("부서 서비스 테스트")
class TeamServiceImplTest extends TeamTestSupport {

  @Mock
  private TeamRepository teamRepository;
  @Mock
  private TeamMapper teamMapper;

  @InjectMocks
  private TeamServiceImpl teamService;

  @Test
  @DisplayName("부서 리스트 조회 테스트")
  void getTeamListTest() {
    TeamSearchCondition condition = new TeamSearchCondition();
    TeamResponse response1 = getMockedTeamResponse(1L, "비서실");
    TeamResponse response2 = getMockedTeamResponse(2L, "인사팀");
    List<TeamResponse> mockedTeamResponseList = List.of(response1, response2);
    when(teamRepository.getTeamList(condition))
      .thenReturn(mockedTeamResponseList);

    List<TeamResponse> teams = teamService.findTeams(condition);

    for (int i = 0; i < teams.size(); i++) {
      TeamResponse actualTeamResponse = teams.get(i);
      TeamResponse expectedTeamResponse = mockedTeamResponseList.get(i);

      assertEquals(expectedTeamResponse.getId(), actualTeamResponse.getId());
      assertEquals(expectedTeamResponse.getName(), actualTeamResponse.getName());

    }

    verify(teamRepository, atMostOnce())
      .getTeamList(condition);
  }

  @Test
  @DisplayName("부서 상세 조회 테스트")
  void getTeamDetailTest() {
    final long mockTeamId = 1L;
    final String mockTeamName = "비서실";
    Team mockedTeam = getMockedTeam(mockTeamId, mockTeamName);
    TeamResponse mockedTeamResponse = getMockedTeamResponse(mockTeamId, mockTeamName);
    when(teamRepository.findTeamByDeleteTypeAndId(DeleteType.ACTIVE, mockTeamId))
      .thenReturn(Optional.of(mockedTeam));
    when(teamMapper.toResponse(mockedTeam))
      .thenReturn(mockedTeamResponse);

    TeamResponse team = teamService.findTeam(mockTeamId);

    assertEquals(mockTeamId, team.getId());
    assertEquals(mockTeamName, team.getName());

    verify(teamRepository, atMostOnce())
      .findTeamByDeleteTypeAndId(DeleteType.ACTIVE, mockTeamId);
  }

  @Test
  @DisplayName("없는 부서 조회 시 예외 발생해야함")
  void getTeamDetailNotFoundTest() {
    final long mockTeamId = 1L;
    when(teamRepository.findTeamByDeleteTypeAndId(DeleteType.ACTIVE, mockTeamId))
      .thenReturn(Optional.empty());

    NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class,
      () -> teamService.findTeam(mockTeamId));

    assertEquals("부서가 존재하지 않습니다. ", noSuchElementException.getMessage());
  }

  @Test
  @DisplayName("부서 추가 테스트")
  void saveTeamTest() {
    TeamRequest mockedTeamRequest = getMockedTeamRequest(null, "주임원사실");
    Team mockedTeam = getMockedTeam(null, "주임원사실");
    Team mockedSavedTeam = getMockedTeam(1L, "주임원사실");
    when(teamMapper.toEntity(mockedTeamRequest))
      .thenReturn(mockedTeam);
    when(teamRepository.save(mockedTeam))
      .thenReturn(mockedSavedTeam);

    long resultTeamId = teamService.saveTeam(mockedTeamRequest);

    assertEquals(mockedSavedTeam.getId(), resultTeamId);

    verify(teamMapper, atMostOnce()).toEntity(mockedTeamRequest);
    verify(teamRepository, atMostOnce()).save(mockedTeam);

  }

  @Test
  @DisplayName("부서 변경 테스트")
  void updateTeamTest() {
    TeamRequest mockedTeamRequest = getMockedTeamRequest(1L, "주임원사실123");
    Team mockedTeam = getMockedTeam(1L, "주임원사실");

    when(teamRepository.findTeamByDeleteTypeAndId(DeleteType.ACTIVE, mockedTeamRequest.getId()))
      .thenReturn(Optional.of(mockedTeam));

    teamService.updateTeam(mockedTeamRequest);

    verify(teamRepository, atMostOnce()).findTeamByDeleteTypeAndId(DeleteType.ACTIVE,
      mockedTeamRequest.getId());
  }

  @Test
  @DisplayName("없는 부서를 변경하면 예외 발생해야함")
  void updateTeamNotFoundTest() {
    TeamRequest mockedTeamRequest = getMockedTeamRequest(1L, "주임원사실123");

    when(teamRepository.findTeamByDeleteTypeAndId(DeleteType.ACTIVE, mockedTeamRequest.getId()))
      .thenReturn(Optional.empty());

    NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class,
      () -> teamService.updateTeam(mockedTeamRequest));

    assertEquals("부서가 존재하지 않습니다. ", noSuchElementException.getMessage());
  }

  @Test
  @DisplayName("부서 삭제 테스트")
  void deleteTeamTest() {
    long mockedTeamId = 1L;
    Team mockedTeam = getMockedTeam(1L, "비서실");
    when(teamRepository.findById(mockedTeamId))
      .thenReturn(Optional.of(mockedTeam));

    teamService.deleteTeam(mockedTeamId);

    verify(teamRepository, atMostOnce()).findById(mockedTeamId);
  }

  @Test
  @DisplayName("없느 부서 삭제 시 예외 발생해야함 ")
  void deleteTeamNotFoundTest() {
    long mockedTeamId = 1L;
    when(teamRepository.findById(mockedTeamId))
      .thenReturn(Optional.empty());

    NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class,
      () -> teamService.deleteTeam(mockedTeamId));

    assertEquals("부서가 존재하지 않습니다. ", noSuchElementException.getMessage());
  }
}
