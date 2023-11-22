
package climatecontrol;

import net.minecraft.world.biome.WorldChunkManager;

import climatecontrol.customGenLayer.GenLayerRiverMixWrapper;

/**
 *
 * @author Zeno410
 */
public class WorldChunkManagerWrapper extends WorldChunkManager {

    public WorldChunkManagerWrapper(GenLayerRiverMixWrapper riverMix) {
        super();
        GenLayerUpdater.accessGenLayer.setField(this, riverMix);
        GenLayerUpdater.accessBiomeIndex.setField(this, riverMix.voronoi());
    }

}
