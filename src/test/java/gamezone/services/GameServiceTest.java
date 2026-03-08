package gamezone.services;

import gamezone.BaseTest;
import gamezone.domain.PageResponse;
import gamezone.domain.dto.games.GameListItemResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest extends BaseTest {

    @Test
    void getGames() throws  Exception{
        PageResponse<GameListItemResponse> games = new GameService().getGames(1, 1);
        System.out.println(CommonServiceImpl.getInstance().gson().toJson(games));
    }
}