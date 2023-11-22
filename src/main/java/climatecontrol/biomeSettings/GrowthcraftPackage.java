
package climatecontrol.biomeSettings;

import climatecontrol.api.BiomePackage;
import climatecontrol.api.BiomeSettings;
import growthcraft.bamboo.BiomeGenBamboo;

/**
 *
 * @author Zeno410
 */

public class GrowthcraftPackage extends BiomePackage {

    public GrowthcraftPackage() {
        super("GrowthcraftInCC.cfg");
        // confirm Growthcraft is there.
        Class sampleClass = BiomeGenBamboo.class;
    }

    @Override
    public BiomeSettings freshBiomeSetting() {
        return new GrowthcraftBiomeSettings();
    }

}
