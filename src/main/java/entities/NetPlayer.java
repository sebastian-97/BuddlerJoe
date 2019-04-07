package entities;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.light.Light;
import entities.light.LightMaster;
import gui.text.Nameplate;
import org.joml.Vector3f;

/**
 * This will be reworked very shortly so I will not fully document it. A LOT will change and it is
 * not really used now. Ignore the class please.
 */
public class NetPlayer extends Entity {

  private static final float joeModelSize = .2f;
  private static final Vector3f[] lampColors = {
    new Vector3f(1, 1, 1).normalize(),
    new Vector3f(3f, 1, 1).normalize(),
    new Vector3f(1, 3f, 1).normalize(),
    new Vector3f(1, 1, 3f).normalize(),
    new Vector3f(3f, 1, 3f).normalize()
  };
  private static TexturedModel joeModel;
  private static int counter;
  private int clientId;
  private String username;
  private Light headLight;
  private Light headLightGlow;
  // private DirectionalUsername directionalUsername;
  private Nameplate nameplate;

  /**
   * Create a net player.
   *
   * <p>REWORK IN PROGRESS
   *
   * @param clientId client id is given out by server
   * @param position 3D world coords
   * @param rotX rotation x axis
   * @param rotY rotation y axis
   * @param rotZ rotation z axis
   * @param username username of net player
   */
  public NetPlayer(
      int clientId, String username, Vector3f position, float rotX, float rotY, float rotZ) {
    super(getJoeModel(), position, rotX, rotY, rotZ, getJoeModelSize());

    this.clientId = clientId;
    this.username = username;
    headLight =
        LightMaster.generateLight(
            LightMaster.LightTypes.SPOT, getHeadlightPosition(), lampColors[counter++]);
    headLight.setCutoff(25f);
    headLightGlow =
        LightMaster.generateLight(
            LightMaster.LightTypes.TORCH, getHeadlightPosition(), new Vector3f(1f, 1, 1));
    // headLightGlow.setDirection(new Vector3f(0,1,0));
    // headLightGlow.setCutoff(110);

    nameplate = new Nameplate(this);
  }

  /**
   * Load the player model before creating player models.
   *
   * @param loader main loader
   */
  public static void init(Loader loader) {
    RawModel rawPlayer = loader.loadToVao(ObjFileLoader.loadObj("joe"));
    joeModel = new TexturedModel(rawPlayer, new ModelTexture(loader.loadTexture("uvjoe")));

    joeModel.getTexture().setUseFakeLighting(true);
    joeModel.getTexture().setShineDamper(.3f);
  }

  public static TexturedModel getJoeModel() {
    return joeModel;
  }

  public static float getJoeModelSize() {
    return joeModelSize;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public void updateNameplate() {
    nameplate.updateString();
  }

  public void turnHeadlightOff() {
    headLight.setBrightness(0);
    headLightGlow.setBrightness(0);
  }

  public void turnHeadlightOn() {
    headLight.setBrightness(8);
    headLightGlow.setBrightness(1.5f);
  }

  private Vector3f getHeadlightPosition() {
    float rotCompX = ((getRotY()) / 90) * getBbox().getDimX() / 2 * .8f;
    return new Vector3f(getPosition()).add(rotCompX, 4.2f, 0);
  }

  private void updateHeadlightPosition() {
    headLight.setPosition(getHeadlightPosition());
    headLightGlow.setPosition(getHeadlightPosition().add(0, 0.5f, 0));
  }

  private void updateHeadlightDirection() {
    Vector3f direction = new Vector3f(0, 0, 1).rotateY((float) Math.toRadians(getRotY()));
    headLight.setDirection(direction);
  }

  @Override
  public void setRotY(float rotY) {
    super.setRotY(rotY);
    updateHeadlightDirection();
  }

  @Override
  public void increaseRotation(float dx, float dy, float dz) {
    super.increaseRotation(dx, dy, dz);
    updateHeadlightDirection();
  }

  @Override
  public void increaseRotation(Vector3f spin) {
    increaseRotation(spin.x, spin.y, spin.z);
  }

  @Override
  public void setPosition(Vector3f position) {
    super.setPosition(position);
    updateHeadlightPosition();
  }

  @Override
  public void increasePosition(Vector3f velocity) {
    super.increasePosition(velocity);
    updateHeadlightPosition();
  }
}
