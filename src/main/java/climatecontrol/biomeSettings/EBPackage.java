
package climatecontrol.biomeSettings;

import climatecontrol.api.BiomePackage;
import climatecontrol.api.BiomeSettings;
import enhancedbiomes.EnhancedBiomesMod;

/**
 *
 * @author Zeno410
 */
public class EBPackage extends BiomePackage {

    public EBPackage() {
        super("EnhancedBiomesInCC.cfg");
        // confirm EB is there.
        Class EBModClass = EnhancedBiomesMod.class;
    }

    @Override
    public BiomeSettings freshBiomeSetting() {
        return new EBBiomeSettings();
    }

}
