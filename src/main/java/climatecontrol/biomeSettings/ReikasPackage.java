
package climatecontrol.biomeSettings;

import Reika.ChromatiCraft.API.BiomeBlacklist;
import climatecontrol.api.BiomePackage;
import climatecontrol.api.BiomeSettings;

/**
 *
 * @author Zeno410
 */
public class ReikasPackage extends BiomePackage {

    public ReikasPackage() {
        super("reikasInCC.cfg");
        // confirm Highlands is there.
        Class biomesClass = BiomeBlacklist.class;
    }

    @Override
    public BiomeSettings freshBiomeSetting() {
        return new ReikasBiomeSettings();
    }

}
