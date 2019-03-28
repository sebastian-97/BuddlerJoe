package entities.blocks;

import engine.render.Loader;
import game.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector3f;

/** Create and manage blocks. Only ever create blocks using this class */
public class BlockMaster {
  // Organize Blocks in lists that can be accessed by their type
  private static final Map<BlockTypes, List<Block>> blockLists = new HashMap<>();
  // Keep a list with just blocks
  private static final List<Block> blocks = new ArrayList<>();
  // List of debris (small blocks)

  /**
   * Init is called once while loading the game. Pre-loads the block texture atlas
   *
   * @param loader main loader
   */
  public static void init(Loader loader) {
    Block.loadBlockModels(loader);
  }

  /**
   * ONLY USE THIS METHOD TO GENERATE BLOCKS.
   *
   * <p>Generates a block of the chosen type and adds it to all relevant lists. Keeps track of the
   * block and cleans it up when destroyed.
   *
   * @param type type of the block as described in {@link BlockTypes}
   * @param position 3D coordinate to place the block
   */
  public static Block generateBlock(BlockTypes type, Vector3f position) {
    Block block;
    switch (type) {
      case GRASS:
        block = new GrassBlock(position);
        break;
      case DIRT:
        block = new DirtBlock(position);
        break;
      case GOLD:
        block = new GoldBlock(position);
        break;
      case STONE:
        block = new StoneBlock(position);
        break;
      default:
        block = null;
        break;
    }
    addBlockToList(block);
    return block;
  }

  /**
   * Called every frame to update if a block has been destroyed. If so, remove that block from all
   * relevant lists (and clean out empty lists).
   */
  public static void update() {
    // Remove destroyed blocks from the list and update entities
    Iterator<Map.Entry<BlockTypes, List<Block>>> mapIterator = blockLists.entrySet().iterator();
    while (mapIterator.hasNext()) {
      List<Block> list = mapIterator.next().getValue();
      Iterator<Block> iterator = list.iterator();
      while (iterator.hasNext()) {
        Block block = iterator.next();
        if (block.isDestroyed()) {
          // Remove block from list and entities
          Game.removeEntity(block);
          iterator.remove();
          blocks.remove(block);
          // Clean up list if empty
          if (list.isEmpty()) {
            mapIterator.remove();
          }
        } else {
          if (block.getPosition() != block.getMoveTo()) {
            // Slowly move the block
            block.accelerate((float) Game.window.getFrameTimeSeconds());
            if (block.getPosition().distance(block.getMoveStartPos()) > block.getMoveDistance()) {
              block.setPosition(block.getMoveTo());
            } else {
              Vector3f dir = new Vector3f(block.getMoveTo()).sub(block.getMoveStartPos());
              block.increasePosition(dir.normalize().mul(block.getSpeed()));
            }
          }
        }
      }
    }
  }

  /**
   * Don't call this method directly. It is used by the Block Master to add new blocks to the game.
   *
   * <p>Adds them to the Blockmasters personal lists and to the Entity render's list.
   *
   * @param block freshly generated block
   */
  private static void addBlockToList(Block block) {
    // Get the list with the type of the block, if the list is absent, create it
    List<Block> list = blockLists.computeIfAbsent(block.getType(), k -> new ArrayList<>());

    // If the block is not destroyed, add it to the Game to be rendered
    if (!block.isDestroyed()) {
      // Add block to its type-specific list
      list.add(block);
      // Add to type-unspecific list
      blocks.add(block);
      // Add to render list
      Game.addEntity(block);
    }
  }

  public static List<Block> getBlocks() {
    return blocks;
  }

  // public static Map<BlockTypes, List<Block>> getBlockLists() {
  //  return blockLists;
  // }

  /** Easy access to block types by their name. */
  public enum BlockTypes {
    GRASS(4, "\u001B[34m█\u001B[0m"),
    DIRT(31, "\u001B[31;1m█\u001B[0m"),
    GOLD(30, "\u001B[33m█\u001B[0m"),
    STONE(11, "\u001B[37m█\u001B[0m"),
    AIR(0, "\u001B[35;1m█\u001B[0m");

    private final int textureId;
    private final String repr;

    BlockTypes(int textureId, String repr) {
      this.textureId = textureId;
      this.repr = repr;
    }

    public int getTextureId() {
      return textureId;
    }

    @Override
    public String toString() {
      return repr;
    }
  }
}
