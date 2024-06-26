package climateControl.genLayerPack;

import java.util.logging.Logger;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import climateControl.utils.Zeno410Logger;

public class GenLayerBiome extends GenLayerPack {

    private static Logger logger = new Zeno410Logger("GenLayerBiome").logger();
    private BiomeGenBase[] field_151623_c;
    private BiomeGenBase[] field_151621_d;
    private BiomeGenBase[] field_151622_e;
    private BiomeGenBase[] field_151620_f;

    public GenLayerBiome(long par1, GenLayer par3GenLayer, WorldType worldType)// , WorldType par4WorldType)
    {
        super(par1);
        this.field_151623_c = new BiomeGenBase[] { BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.desert,
            BiomeGenBase.savanna, BiomeGenBase.savanna, BiomeGenBase.plains };
        this.field_151621_d = new BiomeGenBase[] { BiomeGenBase.forest, BiomeGenBase.roofedForest,
            BiomeGenBase.extremeHills, BiomeGenBase.plains, BiomeGenBase.birchForest, BiomeGenBase.swampland };
        this.field_151622_e = new BiomeGenBase[] { BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.taiga,
            BiomeGenBase.plains };
        this.field_151620_f = new BiomeGenBase[] { BiomeGenBase.icePlains, BiomeGenBase.icePlains,
            BiomeGenBase.icePlains, BiomeGenBase.coldTaiga };
        this.parent = par3GenLayer;

        if (worldType == WorldType.DEFAULT_1_1) {
            this.field_151623_c = new BiomeGenBase[] { BiomeGenBase.desert, BiomeGenBase.forest,
                BiomeGenBase.extremeHills, BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga };
        }
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int par1, int par2, int par3, int par4) {
        int[] aint = this.parent.getInts(par1, par2, par3, par4);
        int[] aint1 = IntCache.getIntCache(par3 * par4);

        for (int i1 = 0; i1 < par4; ++i1) {
            for (int j1 = 0; j1 < par3; ++j1) {
                this.initChunkSeed((long) (j1 + par1), (long) (i1 + par2));
                int k1 = aint[j1 + i1 * par3];
                int l1 = (k1 & 3840) >> 8;
                k1 &= -3841;

                if (isOceanic(k1)) {
                    aint1[j1 + i1 * par3] = k1;
                } else if (k1 == BiomeGenBase.mushroomIsland.biomeID) {
                    aint1[j1 + i1 * par3] = k1;
                } else if (k1 == 1) {
                    if (l1 > 0) {
                        if (this.nextInt(3) == 0) {
                            aint1[j1 + i1 * par3] = BiomeGenBase.mesaPlateau.biomeID;
                        } else {
                            aint1[j1 + i1 * par3] = BiomeGenBase.mesaPlateau_F.biomeID;
                        }
                    } else {
                        aint1[j1 + i1 * par3] = this.field_151623_c[this.nextInt(this.field_151623_c.length)].biomeID;
                    }
                } else if (k1 == 2) {
                    if (l1 > 0) {
                        aint1[j1 + i1 * par3] = BiomeGenBase.jungle.biomeID;
                    } else {
                        aint1[j1 + i1 * par3] = this.field_151621_d[this.nextInt(this.field_151621_d.length)].biomeID;
                    }
                } else if (k1 == 3) {
                    if (l1 > 0) {
                        aint1[j1 + i1 * par3] = BiomeGenBase.megaTaiga.biomeID;
                    } else {
                        aint1[j1 + i1 * par3] = this.field_151622_e[this.nextInt(this.field_151622_e.length)].biomeID;
                    }
                } else if (k1 == 4) {
                    aint1[j1 + i1 * par3] = this.field_151620_f[this.nextInt(this.field_151620_f.length)].biomeID;
                } else {
                    aint1[j1 + i1 * par3] = BiomeGenBase.mushroomIsland.biomeID;
                }
                logger.info(
                    "(" + (i1 + par2)
                        + ","
                        + (j1 + par1)
                        + ") Climate "
                        + k1
                        + " "
                        + aint[j1 + i1 * par3]
                        + " Biome "
                        + aint1[j1 + i1 * par3]);
            }
        }

        return aint1;
    }
}
