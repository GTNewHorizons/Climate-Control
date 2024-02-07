
package climateControl.customGenLayer;

/*
 * this class replaces GenLayerHills with a configurable subbiome replacer
 * it also calls the BoP subbiome replacer after determining the subbiome, if BoP is on
 */

import java.util.logging.Logger;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import climateControl.biomeSettings.BiomeReplacer;
import climateControl.biomeSettings.BoPSubBiomeReplacer;
import climateControl.genLayerPack.GenLayerPack;
import climateControl.generator.BiomeSwapper;
import climateControl.generator.SubBiomeChooser;
import climateControl.utils.IntRandomizer;
import climateControl.utils.Zeno410Logger;

public class GenLayerSubBiome extends GenLayerPack {

    public static Logger logger = new Zeno410Logger("GenLayerSubBiome").logger();
    private GenLayer rivers;
    private final SubBiomeChooser subBiomeChooser;
    private final BiomeSwapper mBiomes;
    private BiomeReplacer BoPSubBiomeReplacer;

    private IntRandomizer randomCallback = new IntRandomizer() {

        public int nextInt(int maximum) {
            return GenLayerSubBiome.this.nextInt(maximum);
        }
    };

    public GenLayerSubBiome(long p_i45479_1_, GenLayer biomes, GenLayer rivers, SubBiomeChooser subBiomeChooser,
        BiomeSwapper mBiomes, boolean doBoP) {
        super(p_i45479_1_);
        this.parent = biomes;
        this.rivers = rivers;
        this.subBiomeChooser = subBiomeChooser;
        this.mBiomes = mBiomes;
        this.initChunkSeed(0, 0);
        try {
            if (doBoP) {
                BoPSubBiomeReplacer = new BoPSubBiomeReplacer(randomCallback);
                logger.info("Bop set up");
            }
        } catch (java.lang.NoClassDefFoundError e) {
            BoPSubBiomeReplacer = null;
            logger.info("no bop ");
        }
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int par1, int par2, int par3, int par4) {
        int[] biomeVals = this.parent.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2);
        int[] riverVals = this.rivers.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2);
        int[] aint2 = IntCache.getIntCache(par3 * par4);
        poison(aint2, par3 * par4);
        for (int i = 0; i < (par3 + 2) * (par4 + 2); i++) {
            if (biomeVals[i] > 256) throw new RuntimeException("" + biomeVals[i]);
        }

        for (int i1 = 0; i1 < par4; ++i1) {
            for (int j1 = 0; j1 < par3; ++j1) {
                this.initChunkSeed((long) (j1 + par1), (long) (i1 + par2));
                int biomeVal = biomeVals[j1 + 1 + (i1 + 1) * (par3 + 2)];
                int riverVal = riverVals[j1 + 1 + (i1 + 1) * (par3 + 2)];
                boolean flag = (riverVal - 2) % 29 == 0;
                // logger.info("biome "+biomeVal);
                if (biomeVal != 0 && riverVal >= 2 && (riverVal - 2) % 29 == 1 && biomeVal < 128) {
                    aint2[j1 + i1 * par3] = mBiomes.replacement(biomeVal);
                    // logger.info("Mbiome "+biomeVal + " to "+aint2[j1 + i1 * par3]);
                } else if (this.nextInt(3) != 0 && !flag) {
                    aint2[j1 + i1 * par3] = biomeVal;
                } else {
                    int i2 = this.subBiomeChooser.subBiome(biomeVal, randomCallback, j1 + par1, i1 + par2);
                    int j2;
                    if (flag && i2 != biomeVal) {
                        int newI2 = this.mBiomes.replacement(i2);
                        if (newI2 != i2) {
                            i2 = newI2;
                        } else {
                            i2 = biomeVal;
                        }
                    }

                    if (i2 == biomeVal) {
                        aint2[j1 + i1 * par3] = biomeVal;
                    } else {
                        j2 = biomeVals[j1 + 1 + (i1 + 1 - 1) * (par3 + 2)];
                        int k2 = biomeVals[j1 + 1 + 1 + (i1 + 1) * (par3 + 2)];
                        int l2 = biomeVals[j1 + 1 - 1 + (i1 + 1) * (par3 + 2)];
                        int i3 = biomeVals[j1 + 1 + (i1 + 1 + 1) * (par3 + 2)];
                        int j3 = 0;

                        if (compareBiomesById(j2, biomeVal)) {
                            ++j3;
                        }

                        if (compareBiomesById(k2, biomeVal)) {
                            ++j3;
                        }

                        if (compareBiomesById(l2, biomeVal)) {
                            ++j3;
                        }

                        if (compareBiomesById(i3, biomeVal)) {
                            ++j3;
                        }

                        if (j3 >= 3) {
                            aint2[j1 + i1 * par3] = i2;
                        } else {
                            aint2[j1 + i1 * par3] = biomeVal;
                        }
                    }
                }
                // now the GenLayerHills stuff is done so run BoP subbiome replacements if it's on
                if (this.BoPSubBiomeReplacer != null) {
                    this.initChunkSeed((long) (j1 + par1), (long) (i1 + par2));
                    int old = aint2[j1 + i1 * par3];
                    aint2[j1 + i1 * par3] = BoPSubBiomeReplacer
                        .replacement(aint2[j1 + i1 * par3], randomCallback, j1 + par1, i1 + par2);
                    if (aint2[j1 + i1 * par3] != old) {
                        // logger.info("BoP subbiome :"+old + " to "+aint2[j1 + i1 * par3]);
                    }
                }
            }

        }
        taste(aint2, par3 * par4);
        return aint2;
    }
}
