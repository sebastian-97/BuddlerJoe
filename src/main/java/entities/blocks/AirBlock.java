package entities.blocks;

import engine.models.TexturedModel;
import org.joml.Vector3f;

public class AirBlock extends Block {

  private static TexturedModel blockModel;

  /**
   * Dummy Block for empty space.
   *
   * @param gridX X coordinate for the block (map grid)
   * @param gridY Y coordinate for the block (map grid)
   */
  public AirBlock(int gridX, int gridY) {
    // Must pass block type and hardness here as they are required
    super(blockModel, BlockMaster.BlockTypes.AIR, 0f, 1f, new Vector3f(), 0, 0, 0, 0, gridX, gridY);
  }

  static void init() {
    blockModel = DirtBlock.getStaticDebrisModel();
  }

  @Override
  protected void onDestroy() {}

  @Override
  public TexturedModel getDebrisModel() {
    return null;
  }
}
