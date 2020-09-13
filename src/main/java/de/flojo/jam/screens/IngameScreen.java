package de.flojo.jam.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.creature.skills.SkillId;
import de.flojo.jam.game.player.PlayerId;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.input.Input;

public class IngameScreen extends Screen {

    private Board board;
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
        summoner = new CreatureFactory();
        summoner.summonPeasant("Wir", board.getTile(5, 25), PlayerId.ONE);
        summoner.summonPeasant("Gegner1", board.getTile(5, 24), PlayerId.TWO);
        summoner.summonPeasant("Gegner2", board.getTile(4, 24), PlayerId.TWO);
        summoner.summonPeasant("Gegner3", board.getTile(4, 26), PlayerId.TWO);
        summoner.summonPeasant("Gegner4", board.getTile(5, 26), PlayerId.TWO);
        summoner.summonPeasant("Ball3", board.getTile(6, 22), PlayerId.TWO);
        Input.keyboard().onKeyTyped(KeyEvent.VK_1,
                k -> summoner.get("Wir").useSkill(board, SkillId.SIMPLE_PUNCH, summoner.get("Gegner1")));
        Input.keyboard().onKeyTyped(KeyEvent.VK_2,
                k -> summoner.get("Wir").useSkill(board, SkillId.SIMPLE_PUNCH, summoner.get("Gegner2")));
        Input.keyboard().onKeyTyped(KeyEvent.VK_3,
                k -> summoner.get("Wir").useSkill(board, SkillId.SIMPLE_PUNCH, summoner.get("Gegner3")));
        Input.keyboard().onKeyTyped(KeyEvent.VK_4,
                k -> summoner.get("Wir").useSkill(board, SkillId.SIMPLE_PUNCH, summoner.get("Gegner4")));

        summoner.summonPeasant("Wir2", board.getTile(9, 10), PlayerId.ONE);
        summoner.summonPeasant("2Gegner1", board.getTile(9, 9), PlayerId.TWO);
        summoner.summonPeasant("2Gegner2", board.getTile(9, 11), PlayerId.TWO);
        summoner.summonPeasant("2Gegner3", board.getTile(10, 9), PlayerId.TWO);
        summoner.summonPeasant("2Gegner4", board.getTile(10, 11), PlayerId.TWO);

        Input.keyboard().onKeyTyped(KeyEvent.VK_5,
                k -> summoner.get("Wir2").useSkill(board, SkillId.SIMPLE_PUNCH, summoner.get("2Gegner1")));
        Input.keyboard().onKeyTyped(KeyEvent.VK_6,
                k -> summoner.get("Wir2").useSkill(board, SkillId.SIMPLE_PUNCH, summoner.get("2Gegner2")));
        Input.keyboard().onKeyTyped(KeyEvent.VK_7,
                k -> summoner.get("Wir2").useSkill(board, SkillId.SIMPLE_PUNCH, summoner.get("2Gegner3")));
        Input.keyboard().onKeyTyped(KeyEvent.VK_8,
                k -> summoner.get("Wir2").useSkill(board, SkillId.SIMPLE_PUNCH, summoner.get("2Gegner4")));

    }

    @Override
    public void render(final Graphics2D g) {
        board.render(g);
        summoner.render(g);
        g.setPaint(Color.MAGENTA);
        g.setFont(Main.GUI_FONT_SMALL);
        TextRenderer.renderWithLinebreaks(g, "Selection: " + summoner.getSelectedCreature(), Main.INNER_MARGIN, 90d, Game.window().getWidth() - 2*Main.INNER_MARGIN);
        super.render(g);
    }

}
