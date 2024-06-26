
package climateControl;

import java.util.logging.Logger;

import net.minecraft.world.World;
import net.minecraft.world.gen.layer.GenLayer;

import climateControl.customGenLayer.GenLayerCache;
import climateControl.customGenLayer.GenLayerLock;
import climateControl.genLayerPack.GenLayerPack;
import climateControl.utils.Acceptor;
import climateControl.utils.Accessor;
import climateControl.utils.Filter;
import climateControl.utils.Maker;
import climateControl.utils.PlaneLocated;
import climateControl.utils.SavedNumberedItems;
import climateControl.utils.Streamer;
import climateControl.utils.Zeno410Logger;

/**
 *
 * @author Zeno410
 */
public class LockGenLayer extends SavedNumberedItems<PlaneLocated<Integer>> {

    public static Logger logger = new Zeno410Logger("LockedBiomes").logger();

    private static Accessor<GenLayerPack, GenLayerPack> genLayerPackParent = new Accessor<>("field_75909_a", "parent");

    private static Accessor<GenLayer, GenLayer> genLayerParent = new Accessor<>("field_75909_a", "parent");

    private final Filter<GenLayer> targetLayerDetector;
    private final String targetName;
    private final Acceptor<LockGenLayer> generator;

    public LockGenLayer(String targetName, Filter<GenLayer> targetLayerDetector, Acceptor<LockGenLayer> generator) {
        super("Locked" + targetName + "Dimension", PlaneLocated.streamer(Streamer.ofInt()));
        this.targetName = targetName;
        this.targetLayerDetector = targetLayerDetector;
        this.generator = generator;
    }

    @Override
    public Maker<PlaneLocated<Integer>> maker(int index) {
        return new Maker<PlaneLocated<Integer>>() {

            public PlaneLocated<Integer> item() {
                // inform the generator this needs to be updated
                generator.accept(LockGenLayer.this);
                return new PlaneLocated<Integer>();
            }
        };
    }

    @Override
    public boolean saveOnNew(int index) {
        return true;
    }

    public static void showGenLayers(GenLayer top) {
        GenLayer parent = top;
        GenLayer current = null;
        while (parent != null) {
            current = parent;
            logger.info(current.toString());
            parent = parent(current);
        }
    }

    public boolean lock(GenLayer top, int dimension, World world, int exclusion, boolean watch) {
        // goes through the parental chain until it finds a suitable biome layer and inserts
        // a locker there
        GenLayer parent = top;
        GenLayer current = null;
        while (parent != null) {
            current = parent;
            // logger.info(current.toString());
            parent = parent(current);
            if (this.targetLayerDetector.accepts(parent)) {
                logger.info("locking with exclusion " + exclusion);
                // have a true cache behind
                GenLayerPack cache = new GenLayerCache(parent);
                GenLayerLock lock = new GenLayerLock(cache, this.saved(dimension, world), exclusion);
                if (watch) lock.setWatch(watch);
                logger.info("setting up " + this.targetName + " watching " + watch);
                // set the previous biome layer child to go to the lock
                if (current instanceof GenLayerPack) {
                    ((GenLayerPack) current).setParent(lock);
                } else {
                    genLayerParent.setField(current, lock);
                }
                return true;
            }
        }
        logger.info("can't find " + targetName + " level");
        return false;
    }

    public static GenLayer parent(GenLayer child) {
        if (child instanceof GenLayerPack) {
            return ((GenLayerPack) child).getParent();
        }
        return genLayerParent.get(child);
    }

}
