package net.packets.gamestatus;

import game.Game;
import game.stages.GameOver;
import game.stages.Playing;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Packet that gets send from the Client to the Server, to inform him about the end of a Round.
 * Packet-Code: STOPG
 *
 * @author Sebastian Schlachter
 */
public class PacketGameEnd extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketGameEnd.class);

  private String winner;
  private long time;

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param winner username of the winner
   * @param time number of milliseconds the game took
   */
  public PacketGameEnd(String winner, long time) {
    // server builds
    super(PacketTypes.GAME_OVER);
    setData(winner + "║" + time);
  }

  /**
   * Constructor that is used by the Client to build the Packet, after receiving the Command STOPG.
   *
   * @param data winner and time
   */
  public PacketGameEnd(String data) {
    // client builds
    super(PacketTypes.GAME_OVER);
    setData(data);
    validate();
  }

  @Override
  public void validate() {
    String[] dataArray = getData().split("║");
    if (dataArray.length != 2) {
      addError("Invalid Game Over Packet received.");
    }
    try {
      winner = dataArray[0];
      time = Long.parseLong(dataArray[1]);
    } catch (NumberFormatException e) {
      addError("Invalid time format.");
    }
  }

  @Override
  public void processData() {
    if (!hasErrors()) {
      GameOver.setMsg(
          "Congratulations to the winner "
              + winner
              + ". The time was: "
              + util.Util.milisToString(time)
              + ".");
      Playing.done();
      Game.addActiveStage(Game.Stage.GAMEOVER);
      Game.removeActiveStage(Game.Stage.PLAYING);
    } else {
      logger.warn("Packet Game End not properly received: " + createErrorMessage());
    }
  }
}
