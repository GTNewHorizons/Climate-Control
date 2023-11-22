/*
 * Available under the Lesser GPL License 3.0
 */

package climateControl.api;

import net.minecraft.world.biome.BiomeGenBase;

import climateControl.utils.Numbered;

/**
 *
 * @author Zeno410
 */
public interface IncidenceModifier {

    public int modifiedIncidence(Numbered<BiomeGenBase> biomeIncidence);
}
