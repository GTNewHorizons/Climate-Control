
package climatecontrol.generator;

import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;

import climatecontrol.utils.ListWrapper;

/**
 *
 * @author Zeno410
 */
public class VillageBiomes extends ListWrapper<BiomeGenBase> {

    // public static Logger logger = new Zeno410Logger("VillageBiomes").logger();
    public VillageBiomes(List<BiomeGenBase> biomes) {
        super(biomes);
    }

    @Override
    public boolean contains(Object arg0) {
        if (!(arg0 instanceof BiomeGenBase)) return false;
        BiomeGenBase biome = (BiomeGenBase) arg0;
        boolean result = super.contains(arg0);
        // logger.info(""+ result +" " +biome.biomeID+ " " + biome.toString());
        return result;
    }

    public void reportMembers() {
        for (BiomeGenBase biome : this) {
            // logger.info(""+biome.biomeID+ " " + biome.toString());
        }
        // logger.info("done with membership");
    }
}
