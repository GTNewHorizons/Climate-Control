/*
 * Available under the Lesser GPL License 3.0
 */

package climatecontrol.api;

import net.minecraft.world.biome.BiomeGenBase;

import climatecontrol.utils.Numbered;

/**
 *
 * @author Zeno410
 */
public interface IncidenceModifier {

    public int modifiedIncidence(Numbered<BiomeGenBase> biomeIncidence);
}
