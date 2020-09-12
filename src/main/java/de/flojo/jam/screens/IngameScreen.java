package de.flojo.jam.screens;

import java.awt.Graphics2D;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.creature.CreatureCollection;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.player.PlayerId;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.screens.Screen;

public class IngameScreen extends Screen {

    private Board board;
    private CreatureCollection creatures;
    private CreatureFactory summoner;

    public static final String NAME = "INGAME";

    public IngameScreen() {
        super(NAME);
        Game.log().info("Building Ingame Screen");
    }

    @Override
    public void prepare() {
        super.prepare();

        board = new Board(Main.BOARD_WIDTH, Main.BOARD_HEIGHT, Main.FIELD_BACKGROUND, "configs/default.terrain");
        creatures = new CreatureCollection();
        summoner = new CreatureFactory();
        creatures.add(summoner.summonPeasant(board.getTile(7, 9), PlayerId.ONE));
        creatures.add(summoner.summonPeasant(board.getTile(8, 9), PlayerId.TWO));
    }


    @Override
    public void render(final Graphics2D g) {
        board.render(g);
        creatures.render(g);
        super.render(g);
    }

}
